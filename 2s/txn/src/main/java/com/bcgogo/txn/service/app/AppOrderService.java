package com.bcgogo.txn.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiPageListResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.model.app.AppointOrderFaultInfoItem;
import com.bcgogo.txn.model.app.AppointOrderMaterial;
import com.bcgogo.txn.model.app.AppointOrderServiceItem;
import com.bcgogo.txn.model.supplierComment.CommentRecord;
import com.bcgogo.txn.model.supplierComment.CommentStat;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.supplierComment.IAppUserCommentService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.model.wx.WXUser;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.utils.*;
import com.bcgogo.wx.user.WXUserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-25
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
@Component
public class AppOrderService implements IAppOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(AppOrderService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  /**
   * 手机端用户取消服务
   *
   * @param userNo
   * @param orderId
   * @return
   */
  public Result appUserCancelOrder(String userNo, Long orderId) {
    Result result = this.validateBeforeCancelOrder(userNo, orderId);
    if (!result.isSuccess()) {
      return result;
    }
    if (!(BcgogoConcurrentController.lock(ConcurrentScene.APPOINT_ORDER, orderId.toString()))) {
      result.setMsg("单据正在被操作,请等待");
      result.setSuccess(false);
      return result;
    }
    TxnWriter txnWriter = txnDaoManager.getWriter();
    AppointOrder appointOrder = txnWriter.getById(AppointOrder.class, orderId);
    appointOrder.setStatus(AppointOrderStatus.CANCELED);

    Object status = txnWriter.begin();
    try {
      txnWriter.update(appointOrder);
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }

    BcgogoConcurrentController.release(ConcurrentScene.APPOINT_ORDER, orderId.toString());
    result.setData(appointOrder.getShopId());
    return result;
  }

  private Result validateBeforeCancelOrder(String userNo, Long orderId) {
    Result result = new Result();
    result.setSuccess(false);

    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);

    TxnWriter txnWriter = txnDaoManager.getWriter();

    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(userNo, null);
    if (appUserDTO == null) {
      result.setMsg("该用户不存在");
      return result;
    }

    AppointOrder appointOrder = txnWriter.getById(AppointOrder.class, orderId);
    if (appointOrder == null) {
      result.setMsg("预约单不存在");
      return result;
    }

    if (appointOrder.getStatus() == AppointOrderStatus.HANDLED) {
      result.setMsg("预约单已生成单据,不能取消");
      return result;
    }
    if (appointOrder.getStatus() == AppointOrderStatus.REFUSED) {
      result.setMsg("预约单已被店家拒绝,不能取消");
      return result;
    }

    if (appointOrder.getStatus() == AppointOrderStatus.CANCELED) {
      result.setMsg("预约单已取消");
      result.setSuccess(true);
      return result;
    }
    if (appointOrder.getStatus() != AppointOrderStatus.PENDING) {
      result.setMsg("预约单已处理,不能取消");
      result.setSuccess(true);
      return result;
    }

    if (userNo.equals(appointOrder.getAppUserNo())) {
      result.setSuccess(true);
      return result;
    }

    List<AppVehicleDTO> appVehicleDTOList = appUserVehicleObdService.getAppVehicleByVehicleNo(appointOrder.getVehicleNo());
    if (CollectionUtils.isEmpty(appVehicleDTOList)) {
      result.setMsg("预约单和用户账号不匹配");
      return result;
    }
    for (AppVehicleDTO appVehicleDTO : appVehicleDTOList) {
      if (userNo.equals(appVehicleDTO.getUserNo())) {
        result.setSuccess(true);
        return result;
      }
    }
    result.setMsg("预约单和用户账号不匹配");
    return result;
  }

  /**
   * 手机端预约服务
   *
   * @param appServiceDTO
   * @return
   */
  public Result appUserAppointOrder(AppServiceDTO appServiceDTO) {

    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnWriter txnWriter = txnDaoManager.getWriter();

    Result result = new Result();
    result.setSuccess(false);

    AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appServiceDTO.getUserNo(), null);
    if (appUserDTO == null) {
      result.setMsg("该手机用户不存在");
      return result;
    }
    appServiceDTO.setAppUserId(appUserDTO.getId());

    String receiptNo = txnService.getReceiptNo(appServiceDTO.getShopId(), OrderTypes.APPOINT_ORDER, null);
    if (StringUtil.isEmpty(receiptNo)) {
      result.setMsg("单据号生成错误");
      return result;
    }

    ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(appServiceDTO.getServiceCategoryId());
    if (serviceCategoryDTO == null) {
      result.setMsg("预约服务不存在");
      return result;
    }

    Set<AppointOrderStatus> statusSet = new HashSet<AppointOrderStatus>();
    statusSet.add(AppointOrderStatus.PENDING);
    statusSet.add(AppointOrderStatus.ACCEPTED);
    statusSet.add(AppointOrderStatus.TO_DO_REPAIR);
    statusSet.add(AppointOrderStatus.HANDLED);

    String dateTime = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, appServiceDTO.getAppointTime());
    Long appointTime = appServiceDTO.getAppointTime();
    try {
      appointTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, dateTime);
      appServiceDTO.setAppointTime(appointTime);
    } catch (ParseException e) {
      LOG.error(e.getMessage(), e);
    }

    List<AppointOrder> appointOrders = txnWriter.getAppointOrderByCondition(appServiceDTO.getAppointTime(), appServiceDTO.getShopId(),
      appServiceDTO.getServiceCategoryId(), appServiceDTO.getVehicleNo(), statusSet);
    if (CollectionUtils.isNotEmpty(appointOrders)) {
      for (AppointOrder appointOrder : appointOrders) {
        if (appointOrder.getStatus() == AppointOrderStatus.HANDLED) {
          Long orderId = appointOrder.getOrderId();
          String orderType = appointOrder.getOrderType();
          if (StringUtil.isNotEmpty(orderType) && orderId != null) {
            if (orderType.equals(OrderTypes.REPAIR.toString())) {
              RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, orderId);
              if (repairOrder.getStatusEnum() == OrderStatus.REPAIR_DISPATCH || repairOrder.getStatusEnum() == OrderStatus.REPAIR_DONE) {
                result.setMsg("该车辆已经在服务中");
                return result;
              }
            }
          }
        }
        result.setMsg("该车辆已经在服务中");
        return result;
      }
    }

    statusSet = new HashSet<AppointOrderStatus>();
    statusSet.add(AppointOrderStatus.HANDLED);
    appointOrders = txnWriter.getAppointOrderByVehicleNoStatus(appServiceDTO.getVehicleNo(), statusSet);
    if (CollectionUtils.isNotEmpty(appointOrders)) {
      for (AppointOrder appointOrder : appointOrders) {
        if (appointOrder.getStatus() == AppointOrderStatus.HANDLED) {
          Long orderId = appointOrder.getOrderId();
          String orderType = appointOrder.getOrderType();
          if (StringUtil.isNotEmpty(orderType) && orderId != null) {
            if (orderType.equals(OrderTypes.REPAIR.toString())) {
              RepairOrder repairOrder = txnWriter.getById(RepairOrder.class, orderId);
              if (repairOrder.getStatusEnum() == OrderStatus.REPAIR_DISPATCH || repairOrder.getStatusEnum() == OrderStatus.REPAIR_DONE) {
                result.setMsg("该车辆已经在服务中");
                return result;
              }
            }
          }
        }
      }
    }


    AppointOrder appointOrder = new AppointOrder(appServiceDTO);
    appointOrder.setAppointCustomer(appUserDTO.getName());
    if (!StringUtils.isEmpty(appServiceDTO.getVehicleNo())) {
      List<CustomerDTO> customerDTOList = userService.getCustomerByLicenceNo(appServiceDTO.getShopId(), appServiceDTO.getVehicleNo());
      if (CollectionUtils.isEmpty(customerDTOList)) {
        appointOrder.setCustomer(appUserDTO.getName());
        appointOrder.setCustomerMobile(appUserDTO.getMobile());
      } else {
        appointOrder.setCustomer(customerDTOList.get(0).getName());
        appointOrder.setCustomerMobile(customerDTOList.get(0).getMobile());
        appointOrder.setCustomerId(customerDTOList.get(0).getId());
      }
    }
    appointOrder.setReceiptNo(receiptNo);

    AppointOrderServiceItem appointOrderServiceItem = new AppointOrderServiceItem();
    appointOrderServiceItem.setShopId(appointOrder.getShopId());
    appointOrderServiceItem.setServiceId(appServiceDTO.getServiceCategoryId());
    appointOrderServiceItem.setServiceName(serviceCategoryDTO.getName());
    appointOrderServiceItem.setStatus(ObjectStatus.ENABLED);

    List<AppointOrderFaultInfoItem> appointOrderFaultInfoItems = generateAppointOrderFaultInfoItem(appServiceDTO);
    appointOrder.setRemark(appServiceDTO.getRemark());
    Object status = txnWriter.begin();
    try {
      txnWriter.save(appointOrder);
      appointOrderServiceItem.setAppointOrderId(appointOrder.getId());
      txnWriter.save(appointOrderServiceItem);
      if (CollectionUtils.isNotEmpty(appointOrderFaultInfoItems)) {
        for (AppointOrderFaultInfoItem appointOrderFaultInfoItem : appointOrderFaultInfoItems) {
          appointOrderFaultInfoItem.setAppointOrderId(appointOrder.getId());
          txnWriter.save(appointOrderFaultInfoItem);
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    result.setSuccess(true);
    result.setData(appointOrder);
    return result;

  }

  /**
   * 微信端预约服务
   *
   * @param appServiceDTO
   * @return
   */
  public Result saveWXAppointOrder(AppServiceDTO appServiceDTO) {
    TxnWriter txnWriter = txnDaoManager.getWriter();
    Result result = new Result();
    ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(appServiceDTO.getServiceCategoryId());
    if (serviceCategoryDTO == null) return result.LogErrorMsg("预约服务不存在");
    AppointOrder appointOrder = new AppointOrder(appServiceDTO);
    Object status = txnWriter.begin();
    try {
      txnWriter.save(appointOrder);
      AppointOrderServiceItem appointOrderServiceItem = new AppointOrderServiceItem();
      appointOrderServiceItem.setShopId(appServiceDTO.getShopId());
      appointOrderServiceItem.setServiceId(appServiceDTO.getServiceCategoryId());
      appointOrderServiceItem.setServiceName(serviceCategoryDTO.getName());
      appointOrderServiceItem.setStatus(ObjectStatus.ENABLED);
      appointOrderServiceItem.setAppointOrderId(appointOrder.getId());
      txnWriter.save(appointOrderServiceItem);
      List<AppointOrderFaultInfoItem> appointOrderFaultInfoItems = generateAppointOrderFaultInfoItem(appServiceDTO);
      if (CollectionUtils.isNotEmpty(appointOrderFaultInfoItems)) {
        for (AppointOrderFaultInfoItem appointOrderFaultInfoItem : appointOrderFaultInfoItems) {
          appointOrderFaultInfoItem.setAppointOrderId(appointOrder.getId());
          txnWriter.save(appointOrderFaultInfoItem);
        }
      }
      txnWriter.commit(status);
    } finally {
      txnWriter.rollback(status);
    }
    result.setSuccess(true);
    result.setData(appointOrder);
    return result;
  }

  private List<AppointOrderFaultInfoItem> generateAppointOrderFaultInfoItem(AppServiceDTO appServiceDTO) {
    if (appServiceDTO != null && !ArrayUtils.isEmpty(appServiceDTO.getFaultInfoItems())) {
      StringBuilder remark = new StringBuilder();
      List<AppointOrderFaultInfoItem> appointOrderFaultInfoItems = new ArrayList<AppointOrderFaultInfoItem>();
      for (AppointOrderFaultInfoItemDTO appointOrderFaultInfoItemDTO : appServiceDTO.getFaultInfoItems()) {
        if (StringUtils.isNotBlank(appServiceDTO.getRemark())) {
          remark.append(appServiceDTO.getRemark());
        }
        AppointOrderFaultInfoItem appointOrderFaultInfoItem = new AppointOrderFaultInfoItem();
        appointOrderFaultInfoItem.fromDTO(appointOrderFaultInfoItemDTO);
        appointOrderFaultInfoItems.add(appointOrderFaultInfoItem);
        remark.append("故障码：").append(appointOrderFaultInfoItemDTO.getFaultCode());
        if (StringUtils.isNotBlank(appointOrderFaultInfoItemDTO.getDescription())) {
          remark.append(",描述：").append(appointOrderFaultInfoItemDTO.getDescription()).append("。");
        }
      }
      appServiceDTO.setRemark(remark.toString());
      return appointOrderFaultInfoItems;
    } else {
      return null;
    }
  }

  /**
   * 根据orderId和类型获取单据详情
   *
   * @param orderId
   * @param type
   * @return
   */
  public AppOrderDTO getAppOrderByOrderId(Long orderId, String type) {

    AppOrderDTO appOrderDTO = null;

    try {
      Long customerId = null;
      Long shopId = null;
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      IAppUserCommentService appUserCommentService = ServiceManager.getService(IAppUserCommentService.class);

      TxnWriter writer = txnDaoManager.getWriter();

      AppointOrder appointOrder = writer.getById(AppointOrder.class, orderId);
      if (appointOrder == null) {

        RepairOrder repairOrder = writer.getById(RepairOrder.class, orderId);

        if (repairOrder == null) {
          WashBeautyOrder washBeautyOrder = writer.getById(WashBeautyOrder.class, orderId);
          if (washBeautyOrder == null) {
            return null;
          }
          shopId = washBeautyOrder.getShopId();

          WashBeautyOrderDTO washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId, orderId);

          if (ArrayUtil.isNotEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
            for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
              ServiceHistoryDTO serviceHistoryDTO = serviceHistoryService.getServiceHistoryById(washBeautyOrderItemDTO.getServiceHistoryId(), washBeautyOrderDTO.getShopId());
              if (serviceHistoryDTO != null) {
                washBeautyOrderItemDTO.setServiceName(serviceHistoryDTO.getName());
                continue;
              }
              ServiceDTO serviceDTO = txnService.getServiceById(washBeautyOrderItemDTO.getServiceId());
              washBeautyOrderItemDTO.setServiceName(serviceDTO.getName());
            }
          }

          appOrderDTO = new AppOrderDTO();
          appOrderDTO.fromWashBeauty(washBeautyOrderDTO);

          StringBuffer stringBuffer = new StringBuffer();
          if (StringUtil.isNotEmpty(washBeautyOrder.getVehicleBrand())) {
            stringBuffer.append(washBeautyOrder.getVehicleBrand());
          }
          if (StringUtil.isNotEmpty(washBeautyOrder.getVehicleModel())) {
            stringBuffer.append(washBeautyOrder.getVehicleModel());
          }
          appOrderDTO.setVehicleBrandModelStr(stringBuffer.toString());


          if (washBeautyOrderDTO.getAppointOrderId() != null) {
            AppointOrder repairAppointOrder = writer.getById(AppointOrder.class, washBeautyOrderDTO.getAppointOrderId());
            if (repairAppointOrder != null && repairAppointOrder.getAppointTime() != null) {
              appOrderDTO.setOrderTime(repairAppointOrder.getAppointTime());
              appOrderDTO.setRemark(repairAppointOrder.getRemark());
              appOrderDTO.setVehicleContact(repairAppointOrder.getVehicleContact());
              appOrderDTO.setVehicleMobile(repairAppointOrder.getVehicleMobile());
            }
          }

          if (NumberUtil.longValue(appOrderDTO.getOrderTime()) <= 0) {
            appOrderDTO.setOrderTime(washBeautyOrder.getCreationDate());
          }

        } else {
          shopId = repairOrder.getShopId();
          RepairOrderDTO repairOrderDTO = rfiTxnService.getRepairOrderDTODetailById(orderId, shopId);
          IRepairService repairService = ServiceManager.getService(IRepairService.class);
          repairService.getProductInfo(repairOrderDTO);
          appOrderDTO = repairOrderDTO.toAppOrderDTO();

          if (repairOrderDTO.getAppointOrderId() != null) {
            AppointOrder repairAppointOrder = writer.getById(AppointOrder.class, repairOrderDTO.getAppointOrderId());
            if (repairAppointOrder != null && repairAppointOrder.getAppointTime() != null) {
              appOrderDTO.setOrderTime(repairAppointOrder.getAppointTime());
              appOrderDTO.setRemark(repairAppointOrder.getRemark());
              appOrderDTO.setVehicleContact(repairAppointOrder.getVehicleContact());
              appOrderDTO.setVehicleMobile(repairAppointOrder.getVehicleMobile());
            }
          }

          if (NumberUtil.longValue(appOrderDTO.getOrderTime()) <= 0) {
            appOrderDTO.setOrderTime(repairOrderDTO.getCreationDate());
          }


          StringBuffer stringBuffer = new StringBuffer();
          if (StringUtil.isNotEmpty(repairOrder.getVehicleBrand())) {
            stringBuffer.append(repairOrder.getVehicleBrand());
          }
          if (StringUtil.isNotEmpty(repairOrder.getVehicleModel())) {
            stringBuffer.append(repairOrder.getVehicleModel());
          }
          appOrderDTO.setVehicleBrandModelStr(stringBuffer.toString());
        }

        List<CommentRecord> commentRecordList = writer.getCommentRecordByOrderId(null, appOrderDTO.getId());
        if (CollectionUtil.isEmpty(commentRecordList)) {
          appOrderDTO.setActionType(ActionType.COMMENT_SHOP.toString());
        }

      } else {
        shopId = appointOrder.getShopId();
        appOrderDTO = appointOrder.toAppOrderDTO();
        List<AppointOrderServiceItem> items = writer.getAppointOrderServiceItems(shopId, appointOrder.getId());
        IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
        List<AppointOrderMaterialDTO> appointOrderMaterialDTOs = appointOrderService.getAppointOrderMaterialDTOs(shopId, appointOrder.getId());
        List<AppointOrderServiceDetailDTO> appointOrderServiceDetailDTOs = appointOrderService.getAppointOrderServiceDetailDTOs(shopId, appointOrder.getId());
        List<AppOrderItemDTO> appOrderItemDTOs = new ArrayList<AppOrderItemDTO>();
        if (CollectionUtils.isNotEmpty(appointOrderMaterialDTOs)) {
          for (AppointOrderMaterialDTO appointOrderMaterialDTO : appointOrderMaterialDTOs) {
            appOrderItemDTOs.add(appointOrderMaterialDTO.toAppOrderItemDTO());
          }
        }
        if (CollectionUtils.isNotEmpty(appointOrderServiceDetailDTOs)) {
          for (AppointOrderServiceDetailDTO appointOrderServiceDetailDTO : appointOrderServiceDetailDTOs) {
            appOrderItemDTOs.add(appointOrderServiceDetailDTO.toAppOrderItemDTO());
          }
        }
        if (CollectionUtils.isNotEmpty(appOrderItemDTOs)) {
          appOrderDTO.setOrderItems(appOrderItemDTOs);
        }
        if (CollectionUtils.isNotEmpty(items)) {
          Long serviceId = CollectionUtil.getFirst(items).getServiceId();
          if (serviceId != null) {
            ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(serviceId);
            appOrderDTO.setServiceType(serviceCategoryDTO.getName());
          }
        }

        StringBuffer stringBuffer = new StringBuffer();
        if (StringUtil.isNotEmpty(appointOrder.getVehicleBrand())) {
          stringBuffer.append(appointOrder.getVehicleBrand());
        }
        if (StringUtil.isNotEmpty(appointOrder.getVehicleModel())) {
          stringBuffer.append(appointOrder.getVehicleModel());
        }
        appOrderDTO.setVehicleBrandModelStr(stringBuffer.toString());

        if (AppointOrderStatus.getCancelPreStatus().contains(appointOrder.getStatus())) {
          appOrderDTO.setActionType(ActionType.CANCEL_ORDER.toString());
        }

      }
      ShopDTO shopDTO = configService.getShopByIdWithoutContacts(shopId);

      if (shopDTO == null) {
        return null;
      }

      appOrderDTO.setShopId(shopDTO.getId());
      appOrderDTO.setShopName(shopDTO.getName());

      List<CommentStat> commentStatList = writer.getCommentStatByShopId(shopId);
      if (CollectionUtil.isNotEmpty(commentStatList)) {
        CommentStat commentStat = CollectionUtil.getFirst(commentStatList);
        if (NumberUtil.doubleVal(commentStat.getCommentTotalScore()) > 0) {
          appOrderDTO.setShopTotalScore(NumberUtil.toReserve(commentStat.getCommentTotalScore() / (commentStat.getCommentFiveAmount() + commentStat.getCommentFourAmount() + commentStat.getCommentThreeAmount()
            + commentStat.getCommentTwoAmount() + commentStat.getCommentOneAmount())));
        }
      }

      List<CommentRecord> commentRecordList = writer.getCommentRecordByOrderId(null, orderId);
      if (CollectionUtils.isNotEmpty(commentRecordList)) {
        appOrderDTO.setComment(commentRecordList.get(0).toAppUserCommentRecordDTO().toShopOrderCommentDTO());
      }

      Receivable receivable = writer.getReceivableByShopIdAndOrderId(shopId, orderId);
      if (receivable != null) {
        appOrderDTO.setSettleAccounts(new AppOrderAccountDTO(receivable.toDTO()));
      }
      return appOrderDTO;

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * 手机端服务查询
   */
  public ApiOrderHistoryResponse getAppOrderHistory(String userNo, String pageNo, String pageSize, String[] status, AppUserType appUserType) {
    ApiOrderHistoryResponse apiOrderResponse = new ApiOrderHistoryResponse(MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_SUCCESS));

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      TxnWriter writer = txnDaoManager.getWriter();

      StringBuffer statusStr = new StringBuffer();

      Set<Long> repairOrderIdSet = new HashSet<Long>();
      Set<Long> washOrderIdSet = new HashSet<Long>();
      Set<Long> appointOrderIdSet = new HashSet<Long>();

      Map<Long, List<AppointOrderServiceItem>> itemListMap = new HashMap<Long, List<AppointOrderServiceItem>>();

      if (ArrayUtil.isNotEmpty(status)) {
        for (String str : status) {
          if (StringUtil.isEmpty(str)) {
            continue;
          }
          statusStr.append(str);
        }
      }

      Pager pager = null;
      Set<Long> shopIdSet = new HashSet<Long>();

      if (statusStr.toString().contains("unfinished")) {
        int appointOrderCount = writer.countAppointOrderByAppUserNoStatus(userNo);
        apiOrderResponse.setUnFinishedServiceCount(appointOrderCount);
        if (appointOrderCount > 0) {
          pager = new Pager(appointOrderCount, Integer.parseInt(pageNo), Integer.parseInt(pageSize));
          List list = writer.getAppointRepairByAppUserNoStatus(userNo, pager);
          if (CollectionUtils.isNotEmpty(list)) {


            for (Object object : list) {
              Object[] array = (Object[]) object;
              AppOrderDTO appOrderDTO = new AppOrderDTO();
              appOrderDTO.setId(Long.valueOf(array[0].toString()));
              appOrderDTO.setOrderId(appOrderDTO.getId());
              appOrderDTO.setOrderTime(Long.valueOf(array[1].toString()));

              if (OrderTypes.APPOINT_ORDER.toString().equals(array[2].toString())) {

                appointOrderIdSet.add(appOrderDTO.getId());
                appOrderDTO.setOrderType(OrderTypes.APPOINT_ORDER.getName());

                if (AppointOrderStatus.PENDING.name().equals(array[4].toString()) || AppointOrderStatus.ACCEPTED.name().equals(array[4].toString())) {
                  if (AppointOrderStatus.PENDING.name().equals(array[4].toString())) {
                    appOrderDTO.setStatus(AppointOrderStatus.PENDING.getName());
                  } else if (AppointOrderStatus.ACCEPTED.name().equals(array[4].toString())) {
                    appOrderDTO.setStatus(AppointOrderStatus.ACCEPTED.getName());
                  }
                  appOrderDTO.setActionType(ActionType.CANCEL_ORDER.toString());
                } else if (AppointOrderStatus.ACCEPTED.name().equals(array[4].toString())) {
                  appOrderDTO.setStatus(AppointOrderStatus.ACCEPTED.getName());
                } else if (AppointOrderStatus.TO_DO_REPAIR.name().equals(array[4].toString())) {
                  appOrderDTO.setStatus(AppointOrderStatus.TO_DO_REPAIR.getName());
                }

              } else if (OrderTypes.REPAIR.toString().equals(array[2].toString())) {
                appOrderDTO.setOrderType(OrderTypes.REPAIR.getName());

                repairOrderIdSet.add(appOrderDTO.getId());

                if (OrderStatus.REPAIR_DISPATCH.name().equals(array[4].toString())) {
                  appOrderDTO.setStatus(OrderStatus.REPAIR_DISPATCH.getName());
                } else if (OrderStatus.REPAIR_DONE.name().equals(array[4].toString())) {
                  appOrderDTO.setStatus(OrderStatus.REPAIR_DONE.getName());
                }
              }
              appOrderDTO.setShopId(Long.valueOf(array[3].toString()));
              shopIdSet.add(appOrderDTO.getShopId());
              apiOrderResponse.getUnFinishedServiceList().add(appOrderDTO);
            }
          }

          if (CollectionUtils.isNotEmpty(appointOrderIdSet)) {
            List<AppointOrderServiceItem> appointOrderServiceItems = writer.getAppointOrderServiceItemsByOrderIds(appointOrderIdSet);

            if (CollectionUtils.isNotEmpty(appointOrderServiceItems)) {
              for (AppointOrderServiceItem serviceItem : appointOrderServiceItems) {
                List<AppointOrderServiceItem> items = itemListMap.get(serviceItem.getAppointOrderId());
                if (CollectionUtils.isEmpty(items)) {
                  items = new ArrayList<AppointOrderServiceItem>();
                }
                items.add(serviceItem);
                itemListMap.put(serviceItem.getAppointOrderId(), items);
              }
            }
          }
        }
      }

      if (statusStr.toString().replace("unfinished", "").contains("finished")) {
        List<String> stringList = writer.countWashRepairByAppUserNoStatus(userNo, OrderStatus.WASH_SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString());

        int count = Integer.valueOf(CollectionUtil.getFirst(stringList));

        apiOrderResponse.setFinishedServiceCount(count);
        apiOrderResponse.setFinishedServiceTotal(Double.valueOf(stringList.get(1)));
        if (count > 0) {
          pager = new Pager(count, Integer.parseInt(pageNo), Integer.parseInt(pageSize));
          List list = writer.getWashRepairByPagerAppUserNo(userNo, OrderStatus.WASH_SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString(), pager);
          if (CollectionUtils.isNotEmpty(list)) {
            for (Object object : list) {
              Object[] array = (Object[]) object;
              AppOrderDTO appOrderDTO = new AppOrderDTO();
              appOrderDTO.setId(Long.valueOf(array[0].toString()));
              appOrderDTO.setOrderId(appOrderDTO.getId());
              appOrderDTO.setOrderTime(Long.valueOf(array[1].toString()));
              appOrderDTO.setOrderTotal(Double.valueOf(array[4].toString()));

              if (OrderTypes.WASH_BEAUTY.toString().equals(array[2].toString())) {
                appOrderDTO.setOrderType(OrderTypes.WASH_BEAUTY.getName());
                washOrderIdSet.add(appOrderDTO.getId());
              } else if (OrderTypes.REPAIR.toString().equals(array[2].toString())) {
                appOrderDTO.setOrderType(OrderTypes.REPAIR.getName());
                repairOrderIdSet.add(appOrderDTO.getId());
              }
              appOrderDTO.setShopId(Long.valueOf(array[3].toString()));

              appOrderDTO.setStatus(OrderStatus.WASH_SETTLED.getName());
              shopIdSet.add(appOrderDTO.getShopId());
              apiOrderResponse.getFinishedServiceList().add(appOrderDTO);
            }
          }
        }
      }

      Map<Long, ShopDTO> shopDTOMap = new HashMap<Long, ShopDTO>();
      if (CollectionUtils.isNotEmpty(shopIdSet)) {
        List<ShopDTO> shopDTOList = configService.getShopByIds(shopIdSet.toArray(new Long[shopIdSet.size()]));
        if (CollectionUtils.isNotEmpty(shopDTOList)) {
          for (ShopDTO shopDTO : shopDTOList) {
            shopDTOMap.put(shopDTO.getId(), shopDTO);
          }
        }
      }

      Map<Long, List<RepairOrderService>> repairListMap = new HashMap<Long, List<RepairOrderService>>();
      Map<Long, ServiceHistoryDTO> serviceHistoryDTOHashMap = new HashMap<Long, ServiceHistoryDTO>();
      Set<Long> serviceHistoryIdSet = new HashSet<Long>();

      Map<Long, List<WashBeautyOrderItem>> washListMap = new HashMap<Long, List<WashBeautyOrderItem>>();

      if (CollectionUtil.isNotEmpty(repairOrderIdSet)) {
        List<RepairOrderService> repairOrderServices = writer.getRepairOrderServicesByShopIdAndArrayOrderId(null, repairOrderIdSet.toArray(new Long[repairOrderIdSet.size()]));

        if (CollectionUtil.isNotEmpty(repairOrderServices)) {
          for (RepairOrderService repairOrderService : repairOrderServices) {
            if (repairOrderService.getServiceHistoryId() == null) {
              continue;
            }

            List<RepairOrderService> items = repairListMap.get(repairOrderService.getRepairOrderId());
            if (CollectionUtils.isEmpty(items)) {
              items = new ArrayList<RepairOrderService>();
            }
            items.add(repairOrderService);
            repairListMap.put(repairOrderService.getRepairOrderId(), items);
            serviceHistoryIdSet.add(repairOrderService.getServiceHistoryId());
          }
        }
      }

      if (CollectionUtil.isNotEmpty(washOrderIdSet)) {
        List<WashBeautyOrderItem> washBeautyOrderItems = writer.getWashBeautyOrderItemByShopIdAndOrderIds(null, washOrderIdSet.toArray(new Long[washOrderIdSet.size()]));

        if (CollectionUtil.isNotEmpty(washBeautyOrderItems)) {
          for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItems) {
            if (washBeautyOrderItem.getServiceHistoryId() == null) {
              continue;
            }

            List<WashBeautyOrderItem> items = washListMap.get(washBeautyOrderItem.getWashBeautyOrderId());
            if (CollectionUtils.isEmpty(items)) {
              items = new ArrayList<WashBeautyOrderItem>();
            }
            items.add(washBeautyOrderItem);
            washListMap.put(washBeautyOrderItem.getWashBeautyOrderId(), items);
            serviceHistoryIdSet.add(washBeautyOrderItem.getServiceHistoryId());
          }
        }
      }


      if (CollectionUtil.isNotEmpty(serviceHistoryIdSet)) {
        List<ServiceHistory> serviceHistoryList = writer.getServiceHistoryByServiceHistoryIdSet(null, serviceHistoryIdSet);
        if (CollectionUtil.isNotEmpty(serviceHistoryList)) {
          for (ServiceHistory serviceHistory : serviceHistoryList) {
            serviceHistoryDTOHashMap.put(serviceHistory.getId(), serviceHistory.toDTO());
          }
        }
      }

      for (AppOrderDTO orderDTO : apiOrderResponse.getUnFinishedServiceList()) {

        if (OrderTypes.APPOINT_ORDER.getName().equals(orderDTO.getOrderType())) {
          List<AppointOrderServiceItem> itemList = itemListMap.get(orderDTO.getId());
          if (CollectionUtils.isNotEmpty(itemList)) {
            for (AppointOrderServiceItem serviceItem : itemList) {
              orderDTO.setContent((StringUtil.isEmpty(orderDTO.getContent()) ? "" : orderDTO.getContent() + ",") + serviceItem.getServiceName());
            }
          }
        } else if (OrderTypes.REPAIR.getName().equals(orderDTO.getOrderType())) {
          List<RepairOrderService> repairOrderServices = repairListMap.get(orderDTO.getId());
          if (CollectionUtil.isNotEmpty(repairOrderServices)) {
            for (RepairOrderService repairOrderService : repairOrderServices) {
              if (repairOrderService.getServiceHistoryId() == null) {
                continue;
              }
              ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOHashMap.get(repairOrderService.getServiceHistoryId());
              if (serviceHistoryDTO == null) {
                continue;
              }
              orderDTO.setContent((StringUtil.isEmpty(orderDTO.getContent()) ? "" : orderDTO.getContent() + ",") + serviceHistoryDTO.getName());
            }
          }
        }

        ShopDTO shopDTO = shopDTOMap.get(orderDTO.getShopId());
        if (shopDTO != null) {
          orderDTO.setShopName(shopDTO.getName());
          orderDTO.setShopImageUrl(null);
        }
      }


      for (AppOrderDTO appOrderDTO : apiOrderResponse.getFinishedServiceList()) {
        ShopDTO shopDTO = shopDTOMap.get(appOrderDTO.getShopId());
        if (shopDTO != null) {
          appOrderDTO.setShopName(shopDTO.getName());
          appOrderDTO.setShopImageUrl(null);
        }

        if (OrderTypes.REPAIR.getName().equals(appOrderDTO.getOrderType())) {
          List<RepairOrderService> repairOrderServices = repairListMap.get(appOrderDTO.getId());
          if (CollectionUtil.isNotEmpty(repairOrderServices)) {
            for (RepairOrderService repairOrderService : repairOrderServices) {
              if (repairOrderService.getServiceHistoryId() == null) {
                continue;
              }
              ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOHashMap.get(repairOrderService.getServiceHistoryId());
              if (serviceHistoryDTO == null) {
                continue;
              }
              appOrderDTO.setContent((StringUtil.isEmpty(appOrderDTO.getContent()) ? "" : appOrderDTO.getContent() + ",") + serviceHistoryDTO.getName());
            }
          }
        } else if (OrderTypes.WASH_BEAUTY.getName().equals(appOrderDTO.getOrderType())) {
          List<WashBeautyOrderItem> washBeautyOrderItems = washListMap.get(appOrderDTO.getId());
          if (CollectionUtil.isNotEmpty(washBeautyOrderItems)) {
            for (WashBeautyOrderItem washBeautyOrderItem : washBeautyOrderItems) {
              if (washBeautyOrderItem.getServiceHistoryId() == null) {
                continue;
              }
              ServiceHistoryDTO serviceHistoryDTO = serviceHistoryDTOHashMap.get(washBeautyOrderItem.getServiceHistoryId());
              if (serviceHistoryDTO == null) {
                continue;
              }
              appOrderDTO.setContent((StringUtil.isEmpty(appOrderDTO.getContent()) ? "" : appOrderDTO.getContent() + ",") + serviceHistoryDTO.getName());
            }
          }
        }

        List<CommentRecord> commentRecordList = writer.getCommentRecordByOrderId(null, appOrderDTO.getId());
        if (CollectionUtil.isEmpty(commentRecordList)) {
          appOrderDTO.setActionType(ActionType.COMMENT_SHOP.toString());
        }

      }

      AppUserLoginInfoDTO infoDTO = ServiceManager.getService(IAppUserService.class).getAppUserLoginInfoByUserNo(userNo, appUserType);

//      ServiceManager.getService(IImageService.class).addShopImageAppOrderDTO(ImageVersion.getSmallShopImageVersion(infoDTO.getImageVersion()), true, apiOrderResponse);


      return apiOrderResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      apiOrderResponse = new ApiOrderHistoryResponse(MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_EXCEPTION));
    }
    return apiOrderResponse;
  }

  @Override
  public ApiPageListResponse<AppOrderDTO> getAllAppOrderHistory(String userNo, int pageNo, int pageSize, AppUserType appUserType) throws Exception {
    ApiPageListResponse<AppOrderDTO> apiPageListResponse = new ApiPageListResponse<AppOrderDTO>(MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_SUCCESS));
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IRepairService repairService = ServiceManager.getService(IRepairService.class);
    IWashBeautyService washBeautyService = ServiceManager.getService(IWashBeautyService.class);
    IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    try {
      TxnWriter writer = txnDaoManager.getWriter();
      Set<Long> repairOrderIdSet = new HashSet<Long>();
      Set<Long> washOrderIdSet = new HashSet<Long>();
      Set<Long> appointOrderIdSet = new HashSet<Long>();
      Set<Long> shopIdSet = new HashSet<Long>();
      List<Pair<OrderTypes, Long>> orderTypeIdPairList = new ArrayList<Pair<OrderTypes, Long>>();
      int orderCount = writer.countAllAppOrderDTOs(userNo);
      Pager pager = new Pager(orderCount, pageNo, pageSize);
      apiPageListResponse.setPager(pager);
      if (orderCount > 0) {
        List<AppOrderDTO> appOrderDTOs = writer.getAllAppointOrderDTOs(userNo, pager);
        if (CollectionUtils.isNotEmpty(appOrderDTOs)) {
          for (AppOrderDTO appOrderDTO : appOrderDTOs) {
            if (appOrderDTO != null) {
              if (appOrderDTO.getShopId() != null) {
                shopIdSet.add(appOrderDTO.getShopId());
              }
              if (OrderTypes.REPAIR.getName().equals(appOrderDTO.getOrderType())) {
                repairOrderIdSet.add(appOrderDTO.getId());
                if (OrderStatus.REPAIR_SETTLED.name().equals(appOrderDTO.getStatus())) {
                  orderTypeIdPairList.add(new Pair<OrderTypes, Long>(OrderTypes.REPAIR, appOrderDTO.getId()));
                }
              } else if (OrderTypes.WASH_BEAUTY.getName().equals(appOrderDTO.getOrderType())) {
                washOrderIdSet.add(appOrderDTO.getId());
                if (OrderStatus.WASH_SETTLED.name().equals(appOrderDTO.getStatus())) {
                  orderTypeIdPairList.add(new Pair<OrderTypes, Long>(OrderTypes.WASH_BEAUTY, appOrderDTO.getId()));
                }
              } else if (OrderTypes.APPOINT_ORDER.getName().equals(appOrderDTO.getOrderType())) {
                appointOrderIdSet.add(appOrderDTO.getId());
              }
            }
          }
          Map<Long, List<AppointOrderServiceItemDTO>> appointServiceMap = appointOrderService.getAppointOrderServiceItemDTOMap(appointOrderIdSet);
          Map<Long, List<RepairOrderServiceDTO>> repairServiceMap = repairService.getRepairOrderServiceDTOMap(repairOrderIdSet);
          Map<Long, List<WashBeautyOrderItemDTO>> washBeautyItemMap = washBeautyService.getWashBeautyOrderItemDTOMap(washOrderIdSet);
          Map<Long, ShopDTO> shopDTOMap = configService.getShopByShopId(shopIdSet.toArray(new Long[shopIdSet.size()]));
          Map<OrderTypes, Map<Long, Integer>> orderCommentCountMap = writer.getOrderCommentCountMap(orderTypeIdPairList);
          for (AppOrderDTO appOrderDTO : appOrderDTOs) {
            if (appOrderDTO != null) {
              if (appOrderDTO.getShopId() != null) {
                ShopDTO shopDTO = shopDTOMap.get(appOrderDTO.getShopId());
                if (shopDTO != null) {
                  appOrderDTO.setShopName(shopDTO.getName());
                }
              }
              if (OrderTypes.REPAIR.getName().equals(appOrderDTO.getOrderType())) {
                appOrderDTO.setRepairContent(repairServiceMap.get(appOrderDTO.getId()));
                if (OrderStatus.REPAIR_SETTLED.getName().equals(appOrderDTO.getStatus())) {
                  Map<Long, Integer> repairCommentCount = orderCommentCountMap.get(OrderTypes.REPAIR);
                  if (repairCommentCount == null || NumberUtil.intValue(repairCommentCount.get(appOrderDTO.getId()), 0) == 0) {
                    appOrderDTO.setActionType(ActionType.COMMENT_SHOP.name());
                  }
                }
              } else if (OrderTypes.WASH_BEAUTY.getName().equals(appOrderDTO.getOrderType())) {
                appOrderDTO.setWashBeautyContent(washBeautyItemMap.get(appOrderDTO.getId()));
                if (OrderStatus.WASH_SETTLED.getName().equals(appOrderDTO.getStatus())) {
                  Map<Long, Integer> washBeautyCommentCount = orderCommentCountMap.get(OrderTypes.WASH_BEAUTY);
                  if (washBeautyCommentCount == null || NumberUtil.intValue(washBeautyCommentCount.get(appOrderDTO.getId()), 0) == 0) {
                    appOrderDTO.setActionType(ActionType.COMMENT_SHOP.name());
                  }
                }
              } else if (OrderTypes.APPOINT_ORDER.getName().equals(appOrderDTO.getOrderType())) {
                appOrderDTO.setAppointContent(appointServiceMap.get(appOrderDTO.getId()));
                if (AppointOrderStatus.PENDING.getName().equals(appOrderDTO.getStatus())
                  || AppointOrderStatus.ACCEPTED.getName().equals(appOrderDTO.getStatus())) {
                  appOrderDTO.setActionType(ActionType.CANCEL_ORDER.name());
                }
              }
            }
          }
          apiPageListResponse.setResults(appOrderDTOs);
          AppUserLoginInfoDTO infoDTO = appUserService.getAppUserLoginInfoByUserNo(userNo, appUserType);
          if (infoDTO != null) {
            imageService.addShopImageAppOrderDTO(ImageVersion.getSmallShopImageVersion(infoDTO.getImageVersion()), true, apiPageListResponse);
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      apiPageListResponse = new ApiPageListResponse<AppOrderDTO>(MessageCode.toApiResponse(MessageCode.ORDER_HISTORY_GET_EXCEPTION));
    }
    return apiPageListResponse;
  }
}
