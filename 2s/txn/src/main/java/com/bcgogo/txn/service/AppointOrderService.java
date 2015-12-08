package com.bcgogo.txn.service;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.app.AppointWay;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.user.SalesManStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.pushMessage.appoint.AppAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.ShopAppointParameter;
import com.bcgogo.txn.dto.pushMessage.appoint.SysAppointParameter;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.model.app.AppointOrderMaterial;
import com.bcgogo.txn.model.app.AppointOrderServiceDetail;
import com.bcgogo.txn.model.app.AppointOrderServiceItem;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.permission.IStaffService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.user.service.wx.IWXMsgSender;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.UnitUtil;
import com.bcgogo.wx.user.WXUserDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-9-5
 * Time: 上午11:31
 */
@Component
public class AppointOrderService implements IAppointOrderService {
  private static final Logger LOG = LoggerFactory.getLogger(TxnService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public AppointOrderDTO getAppointOrderById(Long shopId, Long appointOrderId) {
    IStaffService staffService = ServiceManager.getService(IStaffService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    if (appointOrderId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    AppointOrder appointOrder = writer.getById(AppointOrder.class, appointOrderId);
    if (appointOrder == null) {
      return null;
    }
    AppointOrderDTO appointOrderDTO = appointOrder.toDTO();
    List<AppointOrderServiceDetailDTO> appointOrderServiceDetailDTOs = getAppointOrderServiceDetailDTOs(shopId, appointOrderId);
    appointOrderDTO.setServiceDTOs(appointOrderServiceDetailDTOs.toArray(new AppointOrderServiceDetailDTO[appointOrderServiceDetailDTOs.size()]));

    List<AppointOrderMaterialDTO> appointOrderMaterialDTOs = getAppointOrderMaterialDTOs(shopId, appointOrderId);
    appointOrderDTO.setItemDTOs(appointOrderMaterialDTOs.toArray(new AppointOrderMaterialDTO[appointOrderMaterialDTOs.size()]));

    //接待人
    if (appointOrderDTO.getAssistantId() != null) {
      SalesManDTO salesManDTO = staffService.getSalesManById(appointOrderDTO.getAssistantId());
      if (salesManDTO != null) {
        appointOrderDTO.setAssistantMan(salesManDTO.getName());
      }
    }
    //会员相关
    if (appointOrderDTO.getCustomerId() != null) {
      try {
        MemberDTO memberDTO = membersService.getMemberByCustomerId(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerId());
        if (memberDTO != null) {
          MemberStatus memberStatus = membersService.getMemberStatusByMemberDTO(memberDTO);
          appointOrderDTO.setMemberDTO(memberDTO);
          appointOrderDTO.setMemberStatus(memberStatus.getStatus());
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }

    }
    //服务项目
    List<AppointOrderServiceItemDTO> appointOrderServiceItemDTOList = getAppointOrderServiceItemDTOs(shopId, appointOrderId);
    if (CollectionUtil.isNotEmpty(appointOrderServiceItemDTOList)) {
      AppointOrderServiceItemDTO[] appointOrderServiceItemDTOs = new AppointOrderServiceItemDTO[appointOrderServiceItemDTOList.size()];
      appointOrderServiceItemDTOList.toArray(appointOrderServiceItemDTOs);
      appointOrderDTO.setServiceItemDTOs(appointOrderServiceItemDTOs);
      String appointServiceType = "";
      for (AppointOrderServiceItemDTO appointOrderServiceItemDTO : appointOrderServiceItemDTOList) {
        appointServiceType += appointOrderServiceItemDTO.getServiceName() + ",";
      }
      if (StringUtils.isNotEmpty(appointServiceType)) {
        appointOrderDTO.setAppointServiceType(appointServiceType.substring(0, appointServiceType.length() - 1));
      }
    }
    //取消，拒绝理由
    List<OperationLogDTO> operationLogDTOs = ServiceManager.getService(IOperationLogService.class).getOprationLogByObjectId(ObjectTypes.APPOINT_ORDER, appointOrderId);
    appointOrderDTO.setRefuseOrCancelMsg(operationLogDTOs);
    return appointOrderDTO;
  }

  @Override
  public List<AppointOrderMaterialDTO> getAppointOrderMaterialDTOs(Long shopId, Long appointOrderId) {
    List<AppointOrderMaterialDTO> appointOrderMaterialDTOs = new ArrayList<AppointOrderMaterialDTO>();
    if (shopId == null || appointOrderId == null) {
      return appointOrderMaterialDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrderMaterial> appointOrderMaterials = writer.getAppointOrderMaterials(shopId, appointOrderId);
    if (CollectionUtils.isNotEmpty(appointOrderMaterials)) {
      for (AppointOrderMaterial appointOrderMaterial : appointOrderMaterials) {
        if (appointOrderMaterial != null) {
          appointOrderMaterialDTOs.add(appointOrderMaterial.toDTO());
        }
      }
    }
    return appointOrderMaterialDTOs;
  }

  @Override
  public List<AppointOrderServiceDetailDTO> getAppointOrderServiceDetailDTOs(Long shopId, Long appointOrderId) {
    List<AppointOrderServiceDetailDTO> appointOrderServiceDetailDTOs = new ArrayList<AppointOrderServiceDetailDTO>();
    if (shopId == null || appointOrderId == null) {
      return appointOrderServiceDetailDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrderServiceDetail> appointOrderServiceDetails = writer.getAppointOrderServiceDetails(shopId, appointOrderId);
    if (CollectionUtils.isNotEmpty(appointOrderServiceDetails)) {
      for (AppointOrderServiceDetail appointOrderServiceDetail : appointOrderServiceDetails) {
        if (appointOrderServiceDetail != null) {
          appointOrderServiceDetailDTOs.add(appointOrderServiceDetail.toDTO());
        }
      }
    }
    return appointOrderServiceDetailDTOs;
  }

  @Override
  public AppointOrderDTO getSimpleAppointOrderById(Long shopId, Long appointOrderId) {
    if (appointOrderId == null || shopId == null) {
      return null;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    AppointOrder appointOrder = writer.getAppointOrderById(appointOrderId, shopId);
    if (appointOrder == null) {
      return null;
    }
    return appointOrder.toDTO();
  }

  @Override
  public List<AppointOrderServiceItemDTO> getAppointOrderServiceItemDTOs(Long shopId, Long appointOrderId) {
    List<AppointOrderServiceItemDTO> appointOrderServiceItemDTOList = new ArrayList<AppointOrderServiceItemDTO>();
    if (shopId == null || appointOrderId == null) {
      return appointOrderServiceItemDTOList;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrderServiceItem> appointOrderServiceItems = writer.getAppointOrderServiceItems(shopId, appointOrderId);
    if (CollectionUtil.isNotEmpty(appointOrderServiceItems)) {
      for (AppointOrderServiceItem appointOrderServiceItem : appointOrderServiceItems) {
        appointOrderServiceItemDTOList.add(appointOrderServiceItem.toDTO());
      }
    }
    return appointOrderServiceItemDTOList;
  }

  @Override
  public List<AppointOrderDTO> getRemindedAppointOrder(Long upTime, Long downTime, int start, int limit) {
    List<AppointOrderDTO> appointOrderDTOs = new ArrayList<AppointOrderDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrder> appointOrders = writer.getRemindedAppointOrder(upTime, downTime, start, limit);
    if (CollectionUtils.isNotEmpty(appointOrders)) {
      Set<Long> ids = new HashSet<Long>();
      for (AppointOrder appointOrder : appointOrders) {
        ids.add(appointOrder.getId());
      }
      Map<Long, List<AppointOrderServiceItemDTO>> itemDTOMap = getAppointOrderServiceItemDTOMap(ids);
      for (AppointOrder appointOrder : appointOrders) {
        AppointOrderDTO appointOrderDTO = appointOrder.toDTO();
        List<AppointOrderServiceItemDTO> appointOrderServiceItems = itemDTOMap.get(appointOrder.getId());
        if (CollectionUtil.isEmpty(appointOrderServiceItems)) continue;
        appointOrderDTO.setServiceItemDTOs(appointOrderServiceItems.toArray(new AppointOrderServiceItemDTO[appointOrderServiceItems.size()]));
        appointOrderDTOs.add(appointOrderDTO);
      }
    }
    return appointOrderDTOs;
  }

  @Override
  public void handleSaveAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    if (appointOrderDTO.getAppointWay() == null) {
      appointOrderDTO.setAppointWay(AppointWay.SHOP);
    }
    //1,单据号
    if (StringUtils.isBlank(appointOrderDTO.getReceiptNo())) {
      appointOrderDTO.setReceiptNo(txnService.getReceiptNo(appointOrderDTO.getShopId(), OrderTypes.APPOINT_ORDER, null));
    }
    //2，客户信息
    customerService.handleCustomerForAppointOrder(appointOrderDTO);
    //3，车辆信息
    rfiTxnService.populateVehicleAppointOrderDTO(appointOrderDTO);
    //4，接待人
    saveAssistantMan(appointOrderDTO);
    //5，保存单据 ,保存item
    saveAppointOrder(appointOrderDTO);
    //6，保存提醒事件 (不需要了，以后可能要的)
    saveAppointOrderRemindEvent(appointOrderDTO);
    // 7，保存操作记录
    OperationLogDTO operationLogDTO = new OperationLogDTO();
    operationLogDTO.setAppointOrderOperation(appointOrderDTO);
    operationLogDTO.setOperationType(OperationTypes.CREATE);
    ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);
    //8，客户索引，车辆品牌索引，车牌号索引
    if (appointOrderDTO.getCustomerId() != null) {
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(appointOrderDTO.getCustomerId());
    }
    //新增车辆信息添加到solr
    if (appointOrderDTO.isAddVehicleToSolr()) {
      VehicleDTO vehicleDTOForSolr = new VehicleDTO(appointOrderDTO);
      if (vehicleDTOForSolr.getId() != null) {
        List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
        vehicleDTOs.add(vehicleDTOForSolr);
        ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
      }
    }
    //新增车牌信息到solr
    if (appointOrderDTO.isAddVehicleLicenceNoToSolr()) {
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(appointOrderDTO.getShopId(), appointOrderDTO.getVehicleId());
    }
    //更新故障提醒关联
    updateFaultInfoToShop(appointOrderDTO);

    appointOrderDTO.setServiceItemDTOs(appointOrderDTO.getServiceItemDTOs());
    createShopAcceptAppointMessage(appointOrderDTO);
  }

  private void updateFaultInfoToShop(AppointOrderDTO appointOrderDTO) {
    if (appointOrderDTO != null && !ArrayUtils.isEmpty(appointOrderDTO.getFaultInfoToShopDTOs())) {
      Set<Long> faultInfoToShopIds = new HashSet<Long>();
      for (FaultInfoToShopDTO faultInfoToShopDTO : appointOrderDTO.getFaultInfoToShopDTOs()) {
        if (faultInfoToShopDTO != null && faultInfoToShopDTO.getId() != null) {
          faultInfoToShopIds.add(faultInfoToShopDTO.getId());
        }
      }
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        List<FaultInfoToShop> faultInfoToShops = writer.getFaultInfoToShopByIds(appointOrderDTO.getShopId(), faultInfoToShopIds.toArray(new Long[faultInfoToShopIds.size()]));
        if (CollectionUtils.isNotEmpty(faultInfoToShops)) {
          for (FaultInfoToShop faultInfoToShop : faultInfoToShops) {
            faultInfoToShop.setAppointOrderId(appointOrderDTO.getId());
            faultInfoToShop.setIsCreateAppointOrder(YesNo.YES);
            writer.update(faultInfoToShop);
          }
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  private void saveAppointOrderRemindEvent(AppointOrderDTO appointOrderDTO) {
  }

  private void updateAppointOrderRemindEvent(AppointOrderDTO appointOrderDTO) {
  }

  private void saveAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //保存维修单
      appointOrderDTO.setStatus(AppointOrderStatus.ACCEPTED);
      AppointOrder appointOrder = new AppointOrder();
      appointOrder.fromDTO(appointOrderDTO);
      writer.save(appointOrder);
      appointOrderDTO.setId(appointOrder.getId());
      // 保存预约服务内容
      if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceItemDTOs())) {
        for (AppointOrderServiceItemDTO appointOrderServiceItemDTO : appointOrderDTO.getServiceItemDTOs()) {
          if (appointOrderServiceItemDTO.getServiceId() == null) {
            continue;
          }
          ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(appointOrderServiceItemDTO.getServiceId());
          if (serviceCategoryDTO == null) {
            continue;
          }
          appointOrderServiceItemDTO.setServiceName(serviceCategoryDTO.getName());
          appointOrderServiceItemDTO.setShopId(appointOrderDTO.getShopId());
          appointOrderServiceItemDTO.setAppointOrderId(appointOrderDTO.getId());
          appointOrderServiceItemDTO.setStatus(ObjectStatus.ENABLED);
          AppointOrderServiceItem appointOrderServiceItem = new AppointOrderServiceItem();
          appointOrderServiceItem.fromDTO(appointOrderServiceItemDTO);
          appointOrderServiceItem.setAppointOrderId(appointOrderDTO.getId());
          writer.save(appointOrderServiceItem);
          appointOrderServiceItemDTO.setId(appointOrderServiceItem.getId());
        }
      }

      //保存服务项目
      if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())) {
        for (AppointOrderServiceDetailDTO serviceDTO : appointOrderDTO.getServiceDTOs()) {
          if (StringUtils.isEmpty(serviceDTO.getService())) {
            continue;
          }
          serviceDTO.setStatus(ObjectStatus.ENABLED);
          AppointOrderServiceDetail appointOrderService = new AppointOrderServiceDetail();
          appointOrderService.fromDTO(serviceDTO);
          appointOrderService.setAppointOrderId(appointOrderDTO.getId());
          appointOrderService.setShopId(appointOrderDTO.getShopId());
          writer.save(appointOrderService);
          serviceDTO.setId(appointOrderService.getId());
        }
      }
      //保存材料项目
      if (!ArrayUtils.isEmpty(appointOrderDTO.getItemDTOs())) {
        for (AppointOrderMaterialDTO itemDTO : appointOrderDTO.getItemDTOs()) {
          if (StringUtils.isEmpty(itemDTO.getProductName())) {
            continue;
          }
          itemDTO.setStatus(ObjectStatus.ENABLED);
          AppointOrderMaterial appointOrderMaterial = new AppointOrderMaterial();
          appointOrderMaterial.fromDTO(itemDTO);
          appointOrderMaterial.setAppointOrderId(appointOrderDTO.getId());
          appointOrderMaterial.setShopId(appointOrderDTO.getShopId());
          writer.save(appointOrderMaterial);
          itemDTO.setId(appointOrderMaterial.getId());
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private void saveAssistantMan(AppointOrderDTO appointOrderDTO) throws Exception {
    if (appointOrderDTO == null || StringUtils.isBlank(appointOrderDTO.getAssistantMan())) {
      return;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    //接待人
    Set<String> allAssistantMan = new LinkedHashSet<String>();  //所有施工人
    allAssistantMan.add(appointOrderDTO.getAssistantMan());
    Map<String, SalesManDTO> salesManDTOMap = userService.getSalesManDTOMap(appointOrderDTO.getShopId(), allAssistantMan);
    SalesManDTO salesManDTO = salesManDTOMap.get(appointOrderDTO.getAssistantMan());
    if (salesManDTO == null) {
      salesManDTO = new SalesManDTO();
      salesManDTO.setName(appointOrderDTO.getAssistantMan());
      salesManDTO.setShopId(appointOrderDTO.getShopId());
      salesManDTO.setStatus(SalesManStatus.ONTRIAL);
      salesManDTO.setDepartmentName(SalesManDTO.defaultEmptyDepartment);
      userService.saveOrUpdateSalesMan(salesManDTO);
      salesManDTOMap.put(appointOrderDTO.getAssistantMan(), salesManDTO);
    }
    appointOrderDTO.setAssistantId(salesManDTO.getId());
  }

  @Override
  public void handleUpdateAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    String lastAppointServices = dbAppointOrderDTO.getAppointServiceType();
    Long lastAppointTime = dbAppointOrderDTO.getAppointTime();
    dbAppointOrderDTO.setUpdateInfo(appointOrderDTO);
    //手机端过来的appointOrder不能更新客户，车辆信息
    if (!AppointWay.APP.equals(dbAppointOrderDTO.getAppointWay())) {
      //2，客户信息
      customerService.handleCustomerForAppointOrder(dbAppointOrderDTO);
      //3，车辆信息
      rfiTxnService.populateVehicleAppointOrderDTO(dbAppointOrderDTO);
    }
    //4，接待人
    saveAssistantMan(dbAppointOrderDTO);
    //5，保存单据 ,保存item
    updateAppointOrder(dbAppointOrderDTO);
    //6，保存提醒事件   (不需要了，以后可能要的)
    updateAppointOrderRemindEvent(dbAppointOrderDTO);
    // 7，保存操作记录
    OperationLogDTO operationLogDTO = new OperationLogDTO();
    operationLogDTO.setAppointOrderOperation(dbAppointOrderDTO);
    operationLogDTO.setOperationType(OperationTypes.UPDATE);
    ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);
    //8，客户索引，车辆品牌索引，车牌号索引
    if (dbAppointOrderDTO.getCustomerId() != null) {
      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(dbAppointOrderDTO.getCustomerId());
    }

    //新增车辆信息添加到solr
    if (dbAppointOrderDTO.isAddVehicleToSolr()) {
      VehicleDTO vehicleDTOForSolr = new VehicleDTO(appointOrderDTO);
      if (vehicleDTOForSolr.getId() != null) {
        List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
        vehicleDTOs.add(vehicleDTOForSolr);
        ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
      }
    }
    //新增车牌信息到solr
    if (dbAppointOrderDTO.isAddVehicleLicenceNoToSolr()) {
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(appointOrderDTO.getShopId(), appointOrderDTO.getVehicleId());
    }

    //发送修改消息给app
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    List<ShopAppointParameter> shopAppointParameters = createUpdateShopAppointParameters(dbAppointOrderDTO, lastAppointServices, lastAppointTime);
    if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
      for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
        appointPushMessageService.createShopChangeAppointMessage(shopAppointParameter);
      }
    }

  }

  @Override
  public Result handleAcceptAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    Set<AppointOrderStatus> preStatus = new HashSet<AppointOrderStatus>();
    preStatus.add(AppointOrderStatus.PENDING);
    Result result = updateAppointOrderStatus(appointOrderDTO, preStatus, AppointOrderStatus.ACCEPTED);
    if (result == null && !result.isSuccess()) return result;
    if (AppointWay.WECHAT.equals(dbAppointOrderDTO.getAppointWay())) {
      String openId = dbAppointOrderDTO.getOpenId();
      WXUserDTO userDTO = ServiceManager.getService(IWXUserService.class).getWXUserDTOByOpenId(openId);
      if (userDTO != null) {
        String content = "尊敬的" + dbAppointOrderDTO.getVehicleNo() + "车主，您的预约已被接受";
        ServiceManager.getService(IWXMsgSender.class).sendCustomTextMsg(userDTO.getPublicNo(), openId, content);
      }
    }
    // 7，保存操作记录
    OperationLogDTO operationLogDTO = new OperationLogDTO();
    operationLogDTO.setAppointOrderOperation(appointOrderDTO);
    operationLogDTO.setOperationType(OperationTypes.ACCEPT);
    ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);
    createShopAcceptAppointMessage(dbAppointOrderDTO);
    return result;
  }

  private void createShopAcceptAppointMessage(AppointOrderDTO dbAppointOrderDTO) throws Exception {
    //发送接受消息给app
    IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
    List<ShopAppointParameter> shopAppointParameters = createShopAppointParameters(dbAppointOrderDTO);
    if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
      for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
        appointPushMessageService.createShopAcceptAppointMessage(shopAppointParameter);
      }
    }
  }


  public List<AppAppointParameter> createAppAppointParameter(AppointOrderDTO dbAppointOrderDTO) {
    List<AppAppointParameter> appAppointParameters = new ArrayList<AppAppointParameter>();
    Set<String> sendAppUserNo = new HashSet<String>();
    if (dbAppointOrderDTO != null && AppointWay.APP.equals(dbAppointOrderDTO.getAppointWay()) && StringUtils.isNotBlank(dbAppointOrderDTO.getAppUserNo())) {
      appAppointParameters.add(dbAppointOrderDTO.toAppAppointParameter());
      sendAppUserNo.add(dbAppointOrderDTO.getAppUserNo());
    }
    if (dbAppointOrderDTO != null && dbAppointOrderDTO.getCustomerId() != null) {
      IAppUserCustomerMatchService appUserCustomerMatchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
      List<AppUserDTO> appUserDTOs = appUserCustomerMatchService.getCustomerRelatedAppUserDTOs(dbAppointOrderDTO.getShopId(), dbAppointOrderDTO.getCustomerId());
      if (CollectionUtils.isNotEmpty(appUserDTOs)) {
        for (AppUserDTO appUserDTO : appUserDTOs) {
          if (sendAppUserNo.contains(appUserDTO.getUserNo())) {
            continue;
          }
          AppAppointParameter appAppointParameter = dbAppointOrderDTO.toAppAppointParameter();
          appAppointParameter.setAppUserNo(appUserDTO.getUserNo());

          appAppointParameters.add(appAppointParameter);
        }
      }
    }
    return appAppointParameters;
  }


  public List<SysAppointParameter> createSysAppointParameter(AppointOrderDTO dbAppointOrderDTO) {
    List<SysAppointParameter> sysAppointParameters = new ArrayList<SysAppointParameter>();
    Set<String> sendAppUserNo = new HashSet<String>();
    if (dbAppointOrderDTO != null && AppointWay.APP.equals(dbAppointOrderDTO.getAppointWay()) && StringUtils.isNotBlank(dbAppointOrderDTO.getAppUserNo())) {
      sysAppointParameters.add(dbAppointOrderDTO.toSysAppointParameter());
      sendAppUserNo.add(dbAppointOrderDTO.getAppUserNo());
    }
    if (dbAppointOrderDTO != null && dbAppointOrderDTO.getCustomerId() != null) {
      IAppUserCustomerMatchService appUserCustomerMatchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
      List<AppUserDTO> appUserDTOs = appUserCustomerMatchService.getCustomerRelatedAppUserDTOs(dbAppointOrderDTO.getShopId(), dbAppointOrderDTO.getCustomerId());
      if (CollectionUtils.isNotEmpty(appUserDTOs)) {
        for (AppUserDTO appUserDTO : appUserDTOs) {
          if (sendAppUserNo.contains(appUserDTO.getUserNo())) {
            continue;
          }
          SysAppointParameter sysAppointParameter = dbAppointOrderDTO.toSysAppointParameter();
          sysAppointParameter.setAppUserNo(appUserDTO.getUserNo());

          sysAppointParameters.add(sysAppointParameter);
        }
      }
    }
    return sysAppointParameters;
  }

  private List<ShopAppointParameter> createShopAppointParameters(AppointOrderDTO appointOrderDTO) {
    List<ShopAppointParameter> shopAppointParameters = new ArrayList<ShopAppointParameter>();
    Set<String> sendAppUserNo = new HashSet<String>();
    if (appointOrderDTO != null
      && AppointWay.APP.equals(appointOrderDTO.getAppointWay())
      && StringUtils.isNotBlank(appointOrderDTO.getAppUserNo())) {
      shopAppointParameters.add(new ShopAppointParameter(appointOrderDTO));
      sendAppUserNo.add(appointOrderDTO.getAppUserNo());
    }
    if (appointOrderDTO != null && appointOrderDTO.getCustomerId() != null) {
      IAppUserCustomerMatchService appUserCustomerMatchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
      List<AppUserDTO> appUserDTOs = appUserCustomerMatchService.getCustomerRelatedAppUserDTOs(appointOrderDTO.getShopId(),
        appointOrderDTO.getCustomerId());
      if (CollectionUtils.isNotEmpty(appUserDTOs)) {
        for (AppUserDTO appUserDTO : appUserDTOs) {
          if (sendAppUserNo.contains(appUserDTO.getUserNo())) {
            continue;
          }
          shopAppointParameters.add(new ShopAppointParameter(appointOrderDTO, appUserDTO.getUserNo()));
        }
      }
    }
    return shopAppointParameters;
  }

  private List<ShopAppointParameter> createUpdateShopAppointParameters(AppointOrderDTO appointOrderDTO, String lastService, Long lastAppointTime) {
    List<ShopAppointParameter> shopAppointParameters = new ArrayList<ShopAppointParameter>();
    Set<String> sendAppUserNo = new HashSet<String>();
    if (appointOrderDTO != null && AppointWay.APP.equals(appointOrderDTO.getAppointWay()) && StringUtils.isNotBlank(appointOrderDTO.getAppUserNo())) {
      shopAppointParameters.add(new ShopAppointParameter(appointOrderDTO.getShopId(), appointOrderDTO.getAppUserNo(),
        lastService, appointOrderDTO.getAppointServiceType(), appointOrderDTO.getId(), lastAppointTime,
        appointOrderDTO.getAppointTime(), appointOrderDTO.getVehicleNo()));
      sendAppUserNo.add(appointOrderDTO.getAppUserNo());
    }
    if (appointOrderDTO != null && appointOrderDTO.getCustomerId() != null) {
      IAppUserCustomerMatchService appUserCustomerMatchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
      List<AppUserDTO> appUserDTOs = appUserCustomerMatchService.getCustomerRelatedAppUserDTOs(appointOrderDTO.getShopId(),
        appointOrderDTO.getCustomerId());

      if (CollectionUtils.isNotEmpty(appUserDTOs)) {
        for (AppUserDTO appUserDTO : appUserDTOs) {
          if (sendAppUserNo.contains(appUserDTO.getUserNo())) {
            continue;
          }
          sendAppUserNo.add(appUserDTO.getUserNo());
          shopAppointParameters.add(new ShopAppointParameter(appointOrderDTO.getShopId(), appUserDTO.getUserNo(),
            lastService, appointOrderDTO.getAppointServiceType(), appointOrderDTO.getId(), lastAppointTime,
            appointOrderDTO.getAppointTime(), appointOrderDTO.getVehicleNo()));
        }
      }
    }
    return shopAppointParameters;
  }

  @Override
  public Result handleRefuseAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    Set<AppointOrderStatus> preStatus = new HashSet<AppointOrderStatus>();
    preStatus.add(AppointOrderStatus.PENDING);
    Result result = updateAppointOrderStatus(appointOrderDTO, preStatus, AppointOrderStatus.REFUSED);
    if (result != null && result.isSuccess()) {
      // 保存操作记录
      OperationLogDTO operationLogDTO = new OperationLogDTO();
      operationLogDTO.setAppointOrderOperation(appointOrderDTO);
      operationLogDTO.setOperationType(OperationTypes.REFUSE);
      operationLogDTO.setContent(appointOrderDTO.getRefuseMsg());
      ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);

      //发送拒绝消息给app
      IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      dbAppointOrderDTO.setStatus(AppointOrderStatus.REFUSED);
      dbAppointOrderDTO.setRefuseMsg(appointOrderDTO.getRefuseMsg());
      List<ShopAppointParameter> shopAppointParameters = createShopAppointParameters(dbAppointOrderDTO);
      if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
        for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
          appointPushMessageService.createShopRejectAppointMessage(shopAppointParameter);
        }
      }

    }
    return result;
  }

  @Override
  public Result handleCancelAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    Set<AppointOrderStatus> preStatus = new HashSet<AppointOrderStatus>(AppointOrderStatus.getCancelPreStatus());
    Result result = updateAppointOrderStatus(appointOrderDTO, preStatus, AppointOrderStatus.CANCELED);
    if (result != null && result.isSuccess()) {

      // 保存操作记录
      OperationLogDTO operationLogDTO = new OperationLogDTO();
      operationLogDTO.setAppointOrderOperation(appointOrderDTO);
      operationLogDTO.setOperationType(OperationTypes.CANCEL);
      operationLogDTO.setContent(appointOrderDTO.getCancelMsg());
      ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);

      //发送取消消息给app
      IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      dbAppointOrderDTO.setStatus(AppointOrderStatus.CANCELED);
      dbAppointOrderDTO.setCancelMsg(appointOrderDTO.getCancelMsg());
      List<ShopAppointParameter> shopAppointParameters = createShopAppointParameters(dbAppointOrderDTO);
      if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
        for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
          appointPushMessageService.createShopCancelAppointMessage(shopAppointParameter);
        }
      }
    }
    return result;
  }

  /**
   * 更新预约单状态
   *
   * @param appointOrderDTO
   * @param preStatus
   * @param newStatus
   */
  private Result updateAppointOrderStatus(AppointOrderDTO appointOrderDTO, Set<AppointOrderStatus> preStatus, AppointOrderStatus newStatus) {
    Result result = new Result();
    if (appointOrderDTO == null || appointOrderDTO.getId() == null || appointOrderDTO.getShopId() == null) {
      return new Result("要接受的预约单不存在！", false);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppointOrder appointOrder = writer.getAppointOrderById(appointOrderDTO.getId(), appointOrderDTO.getShopId());
      if (appointOrder != null && (CollectionUtils.isEmpty(preStatus) || preStatus.contains(appointOrder.getStatus()))) {
        appointOrder.setStatus(newStatus);
        writer.update(appointOrder);
        writer.commit(status);
      } else {
        result = new Result("预约单操作异常，请检查数据！", false);
      }
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  private void updateAppointOrder(AppointOrderDTO appointOrderDTO) {
    if (appointOrderDTO != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        //保存维修单
        AppointOrder appointOrder = writer.getAppointOrderById(appointOrderDTO.getId(), appointOrderDTO.getShopId());
        if (appointOrder != null) {
          appointOrder.fromDTO(appointOrderDTO);
          writer.update(appointOrder);
          //处理预约类型
          updateServiceItem(appointOrderDTO, writer);
          //处理施工项目
          updateAppointOrderServiceDetail(appointOrderDTO, writer);
          //处理材料项目
          updateAppointOrderMaterial(appointOrderDTO, writer);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
  }

  private void updateServiceItem(AppointOrderDTO appointOrderDTO, TxnWriter writer) {
    Set<Long> dbServiceCategoryIds = new HashSet<Long>();  //旧的预约服务Id
    Set<Long> pageServiceCategoryIds = new HashSet<Long>(); //新的预约服务Id
    List<AppointOrderServiceItem> appointOrderServiceItems = writer.getAppointOrderServiceItems(appointOrderDTO.getShopId(), appointOrderDTO.getId());

    if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceItemDTOs())) {
      for (AppointOrderServiceItemDTO appointOrderServiceItemDTO : appointOrderDTO.getServiceItemDTOs()) {
        if (appointOrderServiceItemDTO != null && appointOrderServiceItemDTO.getServiceId() != null
          && !ObjectStatus.DISABLED.equals(appointOrderServiceItemDTO.getStatus())) {
          ServiceCategoryDTO serviceCategoryDTO = ServiceCategoryCache.getServiceCategoryDTOById(appointOrderServiceItemDTO.getServiceId());
          if (serviceCategoryDTO != null) {
            appointOrderServiceItemDTO.setServiceName(serviceCategoryDTO.getName());
          }
          appointOrderServiceItemDTO.setStatus(ObjectStatus.ENABLED);
          pageServiceCategoryIds.add(appointOrderServiceItemDTO.getServiceId());
        }
      }
    }
    //1，需要disabled的
    if (CollectionUtils.isNotEmpty(appointOrderServiceItems)) {
      for (AppointOrderServiceItem appointOrderServiceItem : appointOrderServiceItems) {
        if (!pageServiceCategoryIds.contains(appointOrderServiceItem.getServiceId())) {
          appointOrderServiceItem.setStatus(ObjectStatus.DISABLED);
          writer.update(appointOrderServiceItem);
        }
        dbServiceCategoryIds.add(appointOrderServiceItem.getServiceId());
      }
    }
    //2，需要save的
    if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceItemDTOs())) {
      for (AppointOrderServiceItemDTO appointOrderServiceItemDTO : appointOrderDTO.getServiceItemDTOs()) {
        if (appointOrderServiceItemDTO != null && appointOrderServiceItemDTO.getServiceId() != null) {
          if (!dbServiceCategoryIds.contains(appointOrderServiceItemDTO.getServiceId())) {
            appointOrderServiceItemDTO.setShopId(appointOrderDTO.getShopId());
            appointOrderServiceItemDTO.setAppointOrderId(appointOrderDTO.getId());
            appointOrderServiceItemDTO.setStatus(ObjectStatus.ENABLED);
            AppointOrderServiceItem appointOrderServiceItem = new AppointOrderServiceItem();
            appointOrderServiceItem.fromDTO(appointOrderServiceItemDTO);
            writer.save(appointOrderServiceItem);
            appointOrderServiceItemDTO.setId(appointOrderServiceItem.getId());
          }
        }
      }
    }
    appointOrderDTO.setServiceItemDTOs(appointOrderDTO.getServiceItemDTOs());
  }


  private void updateAppointOrderServiceDetail(AppointOrderDTO appointOrderDTO, TxnWriter writer) {
    Set<Long> dbServiceDetailIds = new HashSet<Long>();  //旧的预约服务项目Id
    Set<Long> pageServiceDetailIds = new HashSet<Long>(); //新的预约服务项目Id
    List<AppointOrderServiceDetail> appointOrderServiceDetails = writer.getAppointOrderServiceDetails(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    Map<Long, AppointOrderServiceDetailDTO> pageAppointOrderServiceMap = new HashMap<Long, AppointOrderServiceDetailDTO>();
    Set<AppointOrderServiceDetailDTO> handledServiceDetailDTOs = new HashSet<AppointOrderServiceDetailDTO>();
    //组装页面传过来的数据
    if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())) {
      for (AppointOrderServiceDetailDTO serviceDetailDTO : appointOrderDTO.getServiceDTOs()) {
        if (serviceDetailDTO != null && StringUtils.isNotBlank(serviceDetailDTO.getService())) {
          serviceDetailDTO.setStatus(ObjectStatus.ENABLED);
          serviceDetailDTO.setShopId(appointOrderDTO.getShopId());
          serviceDetailDTO.setAppointOrderId(appointOrderDTO.getId());
          if (serviceDetailDTO.getId() != null) {
            pageServiceDetailIds.add(serviceDetailDTO.getId());
            pageAppointOrderServiceMap.put(serviceDetailDTO.getId(), serviceDetailDTO);
          }
        }
      }
    }
    //1，需要disabled或者更新的
    if (CollectionUtils.isNotEmpty(appointOrderServiceDetails)) {
      for (AppointOrderServiceDetail appointOrderServiceDetail : appointOrderServiceDetails) {
        if (!pageServiceDetailIds.contains(appointOrderServiceDetail.getId())) {
          appointOrderServiceDetail.setStatus(ObjectStatus.DISABLED);
          writer.update(appointOrderServiceDetail);
        } else {
          AppointOrderServiceDetailDTO serviceDetailDTO = pageAppointOrderServiceMap.get(appointOrderServiceDetail.getId());
          appointOrderServiceDetail.fromDTO(serviceDetailDTO);
          writer.update(appointOrderServiceDetail);
          handledServiceDetailDTOs.add(serviceDetailDTO);
        }
        dbServiceDetailIds.add(appointOrderServiceDetail.getId());
      }
    }
    //2，需要save的
    if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceItemDTOs())) {
      for (AppointOrderServiceDetailDTO appointOrderServiceDetailDTO : appointOrderDTO.getServiceDTOs()) {
        if (appointOrderServiceDetailDTO != null && StringUtils.isNotBlank(appointOrderServiceDetailDTO.getService())) {
          if (!dbServiceDetailIds.contains(appointOrderServiceDetailDTO.getId())) {
            appointOrderServiceDetailDTO.setId(null);
            AppointOrderServiceDetail appointOrderServiceDetail = new AppointOrderServiceDetail();
            appointOrderServiceDetail.fromDTO(appointOrderServiceDetailDTO);
            writer.save(appointOrderServiceDetail);
            appointOrderServiceDetailDTO.setId(appointOrderServiceDetail.getId());
            handledServiceDetailDTOs.add(appointOrderServiceDetailDTO);
          }
        }
      }
    }
    appointOrderDTO.setServiceDTOs(handledServiceDetailDTOs.toArray(new AppointOrderServiceDetailDTO[handledServiceDetailDTOs.size()]));
  }

  private void updateAppointOrderMaterial(AppointOrderDTO appointOrderDTO, TxnWriter writer) {
    Set<Long> dbMaterialIds = new HashSet<Long>();  //旧的材料项目Id
    Set<Long> pageMaterialIds = new HashSet<Long>(); //新的材料项目Id
    List<AppointOrderMaterial> appointOrderMaterials = writer.getAppointOrderMaterials(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    Map<Long, AppointOrderMaterialDTO> pageAppointOrderMaterialMap = new HashMap<Long, AppointOrderMaterialDTO>();
    Set<AppointOrderMaterialDTO> handledServiceMaterialDTOs = new HashSet<AppointOrderMaterialDTO>();
    //组装页面传过来的数据
    if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())) {
      for (AppointOrderMaterialDTO appointOrderMaterialDTO : appointOrderDTO.getItemDTOs()) {
        if (appointOrderMaterialDTO != null && StringUtils.isNotBlank(appointOrderMaterialDTO.getProductName())) {
          appointOrderMaterialDTO.setStatus(ObjectStatus.ENABLED);
          appointOrderMaterialDTO.setShopId(appointOrderDTO.getShopId());
          appointOrderMaterialDTO.setAppointOrderId(appointOrderDTO.getId());
          if (appointOrderMaterialDTO.getId() != null) {
            pageMaterialIds.add(appointOrderMaterialDTO.getId());
            pageAppointOrderMaterialMap.put(appointOrderMaterialDTO.getId(), appointOrderMaterialDTO);
          }
        }
      }
    }
    //1，需要disabled或者更新的
    if (CollectionUtils.isNotEmpty(appointOrderMaterials)) {
      for (AppointOrderMaterial appointOrderMaterial : appointOrderMaterials) {
        if (!pageMaterialIds.contains(appointOrderMaterial.getId())) {
          appointOrderMaterial.setStatus(ObjectStatus.DISABLED);
          writer.update(appointOrderMaterial);
        } else {
          AppointOrderMaterialDTO appointOrderMaterialDTO = pageAppointOrderMaterialMap.get(appointOrderMaterial.getId());
          appointOrderMaterial.fromDTO(appointOrderMaterialDTO);
          writer.update(appointOrderMaterial);
          handledServiceMaterialDTOs.add(appointOrderMaterialDTO);
        }
        dbMaterialIds.add(appointOrderMaterial.getId());
      }
    }
    //2，需要save的
    if (!ArrayUtils.isEmpty(appointOrderDTO.getItemDTOs())) {
      for (AppointOrderMaterialDTO appointOrderMaterialDTO : appointOrderDTO.getItemDTOs()) {
        if (appointOrderMaterialDTO != null && StringUtils.isNotBlank(appointOrderMaterialDTO.getProductName())) {
          if (!dbMaterialIds.contains(appointOrderMaterialDTO.getId())) {
            appointOrderMaterialDTO.setId(null);
            AppointOrderMaterial appointOrderMaterial = new AppointOrderMaterial();
            appointOrderMaterial.fromDTO(appointOrderMaterialDTO);
            writer.save(appointOrderMaterial);
            appointOrderMaterialDTO.setId(appointOrderMaterial.getId());
            handledServiceMaterialDTOs.add(appointOrderMaterialDTO);
          }
        }
      }
    }
    appointOrderDTO.setItemDTOs(handledServiceMaterialDTOs.toArray(new AppointOrderMaterialDTO[handledServiceMaterialDTOs.size()]));
  }

  @Override
  public Result validateUpdateAppointOrder(AppointOrderDTO appointOrderDTO) {
    Result result = new Result();
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    StringBuilder msg = new StringBuilder();
    boolean isSuccess = true;
    if (dbAppointOrderDTO == null) {
      isSuccess = false;
      msg.append("需要修改的单据不存在，请重新输入！<br>");
    } else {
      if (!AppointOrderStatus.getModifyPreStatus().contains(dbAppointOrderDTO.getStatus())) {
        isSuccess = false;
        msg.append("当前单据已经被处理，无法修改！<br>");
      }
    }
    result.setSuccess(isSuccess);
    result.setMsg(msg.toString());
    return result;
  }

  @Override
  public Result validateRefuseAppointOrder(AppointOrderDTO appointOrderDTO) {
    Result result = new Result();
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    StringBuilder msg = new StringBuilder();
    boolean isSuccess = true;
    if (dbAppointOrderDTO == null) {
      isSuccess = false;
      msg.append("需要拒绝的单据不存在，请检查！<br>");
    } else {
      if (!AppointOrderStatus.PENDING.equals(dbAppointOrderDTO.getStatus())) {
        isSuccess = false;
        msg.append("当前单据已经被处理，无法拒绝！<br>");
      }
    }
    result.setSuccess(isSuccess);
    result.setMsg(msg.toString());
    return result;
  }

  @Override
  public Result validateCancelAppointOrder(AppointOrderDTO appointOrderDTO) {
    Result result = new Result();
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    StringBuilder msg = new StringBuilder();
    boolean isSuccess = true;
    if (dbAppointOrderDTO == null) {
      isSuccess = false;
      msg.append("需要取消的单据不存在，请检查！<br>");
    } else {
      if (!AppointOrderStatus.getCancelPreStatus().contains(dbAppointOrderDTO.getStatus())) {
        isSuccess = false;
        msg.append("当前单据已经被处理，无法拒绝！<br>");
      }
    }
    result.setSuccess(isSuccess);
    result.setMsg(msg.toString());
    return result;
  }

  @Override
  public Result validateAcceptAppointOrder(AppointOrderDTO appointOrderDTO) {
    Result result = new Result();
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    StringBuilder msg = new StringBuilder();
    boolean isSuccess = true;
    if (dbAppointOrderDTO == null) {
      isSuccess = false;
      msg.append("需要接受的单据不存在，请检查！<br>");
    } else {
      if (!AppointOrderStatus.PENDING.equals(dbAppointOrderDTO.getStatus())) {
        isSuccess = false;
        msg.append("当前单据已经被处理，无法接受！<br>");
      }
    }
    result.setSuccess(isSuccess);
    result.setMsg(msg.toString());
    return result;
  }

  @Override
  public List<AppointOrderDTO> searchAppointOrderDTOs(AppointOrderSearchCondition searchCondition) {
    List<AppointOrderDTO> appointOrderDTOs = new ArrayList<AppointOrderDTO>();
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return appointOrderDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrder> appointOrders = writer.searchAppointOrders(searchCondition);

    if (CollectionUtils.isNotEmpty(appointOrders)) {
      Set<Long> ids = new HashSet<Long>();
      for (AppointOrder appointOrder : appointOrders) {
        ids.add(appointOrder.getId());
      }
      Map<Long, List<AppointOrderServiceItemDTO>> itemDTOMap = getAppointOrderServiceItemDTOMap(ids);
      for (AppointOrder appointOrder : appointOrders) {
        AppointOrderDTO appointOrderDTO = appointOrder.toDTO();
        List<AppointOrderServiceItemDTO> appointOrderServiceItems = itemDTOMap.get(appointOrder.getId());
        appointOrderDTO.setServiceItemDTOs(appointOrderServiceItems.toArray(new AppointOrderServiceItemDTO[appointOrderServiceItems.size()]));
        appointOrderDTOs.add(appointOrderDTO);
      }
    }
    return appointOrderDTOs;
  }

  @Override
  public int countAppointOrderDTOs(AppointOrderSearchCondition searchCondition) {
    int result = 0;
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return result;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    result = writer.countAppointOrders(searchCondition);
    return result;

  }

  @Override
  public Map<Long, List<AppointOrderServiceItemDTO>> getAppointOrderServiceItemDTOMap(Set<Long> ids) {
    Map<Long, List<AppointOrderServiceItemDTO>> itemDTOMap = new HashMap<Long, List<AppointOrderServiceItemDTO>>();
    if (CollectionUtils.isEmpty(ids)) {
      return itemDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrderServiceItem> appointOrderServiceItems = writer.getAppointOrderServiceItemsByAppointOrderIds(ids);
    if (CollectionUtils.isNotEmpty(appointOrderServiceItems)) {
      for (AppointOrderServiceItem appointOrderServiceItem : appointOrderServiceItems) {
        AppointOrderServiceItemDTO appointOrderServiceItemDTO = appointOrderServiceItem.toDTO();
        List<AppointOrderServiceItemDTO> appointOrderServiceItemDTOs = itemDTOMap.get(appointOrderServiceItem.getAppointOrderId());
        if (appointOrderServiceItemDTOs == null) {
          appointOrderServiceItemDTOs = new ArrayList<AppointOrderServiceItemDTO>();
        }
        appointOrderServiceItemDTOs.add(appointOrderServiceItemDTO);
        itemDTOMap.put(appointOrderServiceItem.getAppointOrderId(), appointOrderServiceItemDTOs);
      }
    }
    return itemDTOMap;
  }

  @Override
  public Result validateCreateAppointOrder(Long shopId) {
    IServiceCategoryService serviceCategoryService = ServiceManager.getService(IServiceCategoryService.class);
    Map<Long, String> serviceScopeMap = serviceCategoryService.getShopServiceCategoryIdNameMap(shopId);
    if (MapUtils.isEmpty(serviceScopeMap)) {
      return new Result("您还未设置本店服务范围无法做预约，请先去本店资料设置服务范围！",
        false, Result.Operation.CONFIRM.toString(), "shopData.do?method=toManageShopData");
    }
    return new Result();
  }

  @Override
  public Result validateCreateOtherOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    AppointOrderDTO dbAppointOrderDTO = null;
    IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    if (appointOrderDTO != null && appointOrderDTO.getShopId() != null && appointOrderDTO.getId() != null) {
      dbAppointOrderDTO = appointOrderService.getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    }
    if (dbAppointOrderDTO == null) {
      return new Result("当前预约单不存在无法生成其他单据！", false);
    }

    if (!AppointOrderStatus.ACCEPTED.equals(dbAppointOrderDTO.getStatus())) {
      return new Result("当前预约单已经被处理无法生成其他单据！", false);
    }

    //除了洗车单外，都生成施工单，生成施工单的时候要校验下，是否存在车牌号相同施工中的单据
    if (!isCreateWashBeauty(dbAppointOrderDTO)) {
      RepairOrderDTO repairOrderDTO = txnService.getUnbalancedAccountRepairOrderByVehicleNumber(dbAppointOrderDTO.getShopId(), dbAppointOrderDTO.getVehicleNo(), null);
      if (repairOrderDTO != null) {
        return new Result("当前车牌号已经有未结算的施工单，请先结算该施工单！", false, Result.Operation.CONFIRM.toString(), repairOrderDTO.getId().toString());
      }
    }
    return new Result();
  }

  //判断预约单能否生成洗车单，false的时候认为是生成施工单 ,只要预约内容中有一条是洗车就认为是true
  @Override
  public boolean isCreateWashBeauty(AppointOrderDTO appointOrderDTO) {
    if (appointOrderDTO != null && !ArrayUtils.isEmpty(appointOrderDTO.getServiceItemDTOs())) {
      //有商品材料项目的时候生成施工单
      if (!ArrayUtils.isEmpty(appointOrderDTO.getItemDTOs())) {
        return false;
      }
      List<ServiceCategoryDTO> serviceCategoryDTOs = ServiceCategoryCache.getServiceCategoryDTOByServiceScope(ServiceScope.WASH.name());
      Set<Long> secondServiceCategoryIds = new HashSet<Long>();
      if (CollectionUtils.isNotEmpty(serviceCategoryDTOs)) {
        for (ServiceCategoryDTO serviceCategoryDTO : serviceCategoryDTOs) {
          if (serviceCategoryDTO != null && serviceCategoryDTO.getId() != null) {
            secondServiceCategoryIds.add(serviceCategoryDTO.getId());
          }
        }
      }
      for (AppointOrderServiceItemDTO appointOrderServiceItemDTO : appointOrderDTO.getServiceItemDTOs()) {
        if (appointOrderServiceItemDTO != null && ObjectStatus.ENABLED.equals(appointOrderServiceItemDTO.getStatus())
          && secondServiceCategoryIds.contains(appointOrderServiceItemDTO.getServiceId())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void generateCustomerInfo(CustomerDTO customerDTO, VehicleDTO vehicleDTO, AppointOrderDTO appointOrderDTO) {
    if (appointOrderDTO != null && appointOrderDTO.getShopId() != null) {
      IUserService userService = ServiceManager.getService(IUserService.class);
      vehicleDTO = CollectionUtil.getFirst(userService.getVehicleByLicenceNo(appointOrderDTO.getShopId(), appointOrderDTO.getVehicleNo()));
      if (vehicleDTO != null && vehicleDTO.getId() != null) {
        CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(userService.getCustomerVehicleByVehicleId(vehicleDTO.getId()));
        if (customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null) {
          customerDTO = userService.getCustomerDTOByCustomerId(customerVehicleDTO.getCustomerId(), appointOrderDTO.getShopId());
        }
      } else {
        if (appointOrderDTO.getCustomerId() != null) {
          customerDTO = userService.getCustomerDTOByCustomerId(appointOrderDTO.getCustomerId(), appointOrderDTO.getShopId());
        }
        if (customerDTO == null && StringUtils.isNotBlank(appointOrderDTO.getCustomerMobile())) {
          customerDTO = CollectionUtil.getFirst(userService.getCustomerByMobile(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerMobile()));
        }
      }

      if (vehicleDTO == null) {
        vehicleDTO = new VehicleDTO();
      }
      if (vehicleDTO.getId() == null) {
        vehicleDTO = new VehicleDTO();
        vehicleDTO.fromAppointOrderDTO(appointOrderDTO);
      }
      if (customerDTO == null) {
        customerDTO = new CustomerDTO();
      }
      if (customerDTO.getId() == null) {
        customerDTO.fromAppointOrderDTO(appointOrderDTO);
      }
    }
  }

  @Override
  public void handelAppointOrderAfterSaveWashBeauty(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    if (washBeautyOrderDTO != null && washBeautyOrderDTO.getAppointOrderId() != null && washBeautyOrderDTO.getShopId() != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        AppointOrder appointOrder = writer.getAppointOrderById(washBeautyOrderDTO.getAppointOrderId(), washBeautyOrderDTO.getShopId());
        if (appointOrder != null) {
          appointOrder.setOrderId(washBeautyOrderDTO.getId());
          appointOrder.setOrderType(OrderTypes.WASH_BEAUTY.toString());
          appointOrder.setStatus(AppointOrderStatus.HANDLED);
          writer.update(appointOrder);
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }

      //发送取消消息给app
      AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getAppointOrderId());
      if (dbAppointOrderDTO != null) {
        // 保存操作记录
        OperationLogDTO operationLogDTO = new OperationLogDTO();
        operationLogDTO.setAppointOrderOperation(dbAppointOrderDTO);
        operationLogDTO.setOperationType(OperationTypes.HANDLED);
        ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);

        IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
        List<ShopAppointParameter> shopAppointParameters = createShopAppointParameters(dbAppointOrderDTO);
        if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
          for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
            appointPushMessageService.createShopFinishAppointMessage(shopAppointParameter);
          }
        }
      }
    }
  }

  @Override
  public void handelAppointOrderAfterSaveRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO != null && repairOrderDTO.getAppointOrderId() != null && repairOrderDTO.getShopId() != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        AppointOrder appointOrder = writer.getAppointOrderById(repairOrderDTO.getAppointOrderId(), repairOrderDTO.getShopId());
        if (appointOrder != null && !AppointOrderStatus.HANDLED.equals(appointOrder.getStatus())) {
          appointOrder.setOrderId(repairOrderDTO.getId());
          appointOrder.setOrderType(OrderTypes.REPAIR.toString());
          appointOrder.setStatus(AppointOrderStatus.HANDLED);
          writer.update(appointOrder);
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }

      //发送取消消息给app

      if (repairOrderDTO != null && OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
        AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(repairOrderDTO.getShopId(), repairOrderDTO.getAppointOrderId());
        if (dbAppointOrderDTO != null) {
          // 保存操作记录
          OperationLogDTO operationLogDTO = new OperationLogDTO();
          operationLogDTO.setAppointOrderOperation(dbAppointOrderDTO);
          operationLogDTO.setOperationType(OperationTypes.HANDLED);
          ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);

          IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
          List<ShopAppointParameter> shopAppointParameters = createShopAppointParameters(dbAppointOrderDTO);
          if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
            for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
              appointPushMessageService.createShopFinishAppointMessage(shopAppointParameter);
            }
          }
        }

      }
    }
  }

  @Override
  public void handelAppointOrderAfterSaveRepairDraft(DraftOrderDTO draftOrderDTO) throws Exception {
    if (draftOrderDTO != null && draftOrderDTO.getAppointOrderId() != null && draftOrderDTO.getShopId() != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      Object status = writer.begin();
      try {
        AppointOrder appointOrder = writer.getAppointOrderById(draftOrderDTO.getAppointOrderId(), draftOrderDTO.getShopId());
        if (appointOrder != null && !AppointOrderStatus.HANDLED.equals(appointOrder.getStatus())) {
          appointOrder.setOrderId(draftOrderDTO.getId());
          appointOrder.setOrderType(OrderTypes.REPAIR_DRAFT_ORDER.toString());
          appointOrder.setStatus(AppointOrderStatus.TO_DO_REPAIR);
          writer.update(appointOrder);
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
    }
  }


  @Override
  public Result handleAutoAcceptAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception {
    AppointOrderDTO dbAppointOrderDTO = getAppointOrderById(appointOrderDTO.getShopId(), appointOrderDTO.getId());
    Set<AppointOrderStatus> preStatus = new HashSet<AppointOrderStatus>();
    preStatus.add(AppointOrderStatus.PENDING);
    Result result = updateAppointOrderStatus(appointOrderDTO, preStatus, AppointOrderStatus.ACCEPTED);
    if (result != null && result.isSuccess()) {
      // 7，保存操作记录
      OperationLogDTO operationLogDTO = new OperationLogDTO();
      operationLogDTO.setAppointOrderOperation(appointOrderDTO);
      operationLogDTO.setOperationType(OperationTypes.AUTO_ACCEPT);
      ServiceManager.getService(IOperationLogService.class).saveOperationLog(operationLogDTO);

      //发送接受消息给app
      IAppointPushMessageService appointPushMessageService = ServiceManager.getService(IAppointPushMessageService.class);
      List<SysAppointParameter> sysAppointParameters = createSysAppointParameter(dbAppointOrderDTO);
      if (CollectionUtils.isNotEmpty(sysAppointParameters)) {
        for (SysAppointParameter sysAppointParameter : sysAppointParameters) {
          appointPushMessageService.createSysAcceptAppointMessage(sysAppointParameter);
        }
      }

      //发送接受消息给app
      List<ShopAppointParameter> shopAppointParameters = createShopAppointParameters(dbAppointOrderDTO);
      if (CollectionUtils.isNotEmpty(shopAppointParameters)) {
        for (ShopAppointParameter shopAppointParameter : shopAppointParameters) {
          appointPushMessageService.createShopAcceptAppointMessage(shopAppointParameter);
        }
      }
    }
    return result;
  }


  public List<AppointOrderDTO> getAppointOrderByStatus(AppointOrderStatus appointOrderStatus, int start, int size) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<AppointOrderDTO> appointOrderDTOList = new ArrayList<AppointOrderDTO>();
    List<AppointOrder> appointOrderList = writer.getAppointOrderByStatus(appointOrderStatus, start, size);
    if (CollectionUtil.isEmpty(appointOrderList)) {
      return appointOrderDTOList;
    }

    for (AppointOrder appointOrder : appointOrderList) {
      appointOrderDTOList.add(appointOrder.toDTO());
    }
    return appointOrderDTOList;
  }


  public void autoAcceptAppointOrderHalfHour() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String config = configService.getConfig("ShopAutoAcceptAppointOrder", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isEmpty(config) || !NumberUtil.isNumber(config)) {
      LOG.error("店铺自动接受预约单配置错误");
      return;
    }

    int pageSize = 1000; //每次获取的数量
    int start = 0;
    while (true) {
      List<AppointOrderDTO> appointOrderDTOList = this.getAppointOrderByStatus(AppointOrderStatus.PENDING, start, pageSize);
      if (CollectionUtils.isEmpty(appointOrderDTOList)) {
        break;
      }

      start += appointOrderDTOList.size();

      for (AppointOrderDTO appointOrderDTO : appointOrderDTOList) {
        try {

          Result result = this.validateAcceptAppointOrder(appointOrderDTO);
          if (!result.isSuccess()) {
            continue;
          }

          Long createTime = appointOrderDTO.getCreateTime();
          if (NumberUtil.longValue(createTime) <= 0) {
            continue;
          }
          if (createTime.longValue() >= System.currentTimeMillis()) {
            continue;
          }
          if (System.currentTimeMillis() - createTime.longValue() >= Long.valueOf(config).longValue()) {
            this.handleAutoAcceptAppointOrder(appointOrderDTO);
          }
        } catch (Exception e) {
          LOG.error("appointOrder自动接受失败,id:" + appointOrderDTO.getId());
        }
      }
    }
  }

  @Override
  public AppointOrderDTO generateAppointOrderByCustomerInfo(CustomerDTO customerInfo) throws Exception {
    AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
    if (customerInfo != null && customerInfo.getShopId() != null) {
      appointOrderDTO.setShopId(customerInfo.getShopId());
      IUserService userService = ServiceManager.getService(IUserService.class);
      if (customerInfo.getId() != null) {
        CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(customerInfo.getId(), customerInfo.getShopId());
        if (customerDTO != null) {
          appointOrderDTO.setCustomerDTO(customerDTO);
          IMembersService membersService = ServiceManager.getService(IMembersService.class);
          MemberDTO memberDTO = membersService.getMemberByCustomerId(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerId());
          if (memberDTO != null) {
            MemberStatus memberStatus = membersService.getMemberStatusByMemberDTO(memberDTO);
            appointOrderDTO.setMemberDTO(memberDTO);
            appointOrderDTO.setMemberStatus(memberStatus.getStatus());
          }
        }
      }
      if (customerInfo.getVehicleId() != null) {
        VehicleDTO vehicleDTO = userService.getVehicleById(customerInfo.getVehicleId());
        if (vehicleDTO != null && vehicleDTO.getShopId().equals(customerInfo.getShopId())) {
          appointOrderDTO.setVehicleDTO(vehicleDTO);
        }
      }
    }
    appointOrderDTO.setItemDTOs(new AppointOrderMaterialDTO[]{new AppointOrderMaterialDTO()});
    appointOrderDTO.setServiceDTOs(new AppointOrderServiceDetailDTO[]{new AppointOrderServiceDetailDTO()});
    return appointOrderDTO;
  }

  @Override
  public void generateCreateRepairOrderItem(RepairOrderDTO repairOrderDTO, AppointOrderDTO appointOrderDTO) throws Exception {
    if (appointOrderDTO != null && repairOrderDTO != null) {
      double itemTotal = 0d, serviceTotal = 0d;
      //商品
      if (!ArrayUtils.isEmpty(appointOrderDTO.getItemDTOs())) {
        List<RepairOrderItemDTO> repairOrderItemDTOs = new ArrayList<RepairOrderItemDTO>();
        Set<Long> productIds = new HashSet<Long>();
        for (AppointOrderMaterialDTO appointOrderMaterialDTO : appointOrderDTO.getItemDTOs()) {
          if (appointOrderMaterialDTO != null && StringUtils.isNotBlank(appointOrderMaterialDTO.getProductName())) {
            if (appointOrderMaterialDTO.getProductId() != null) {
              productIds.add(appointOrderMaterialDTO.getProductId());
            }
          }
        }
        IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
        IProductService productService = ServiceManager.getService(IProductService.class);
        Map<Long, InventoryDTO> inventoryDTOMap = inventoryService.getInventoryDTOMap(appointOrderDTO.getShopId(), productIds);
        Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(appointOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
        Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(appointOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
        Map<Long, StoreHouseInventoryDTO> storeHouseInventoryDTOMap = new HashMap<Long, StoreHouseInventoryDTO>();
        boolean isHaveStoreHouse = BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(repairOrderDTO.getShopVersionId());
        if (isHaveStoreHouse) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          storeHouseInventoryDTOMap = storeHouseService.getStoreHouseInventoryDTOMapByStorehouseAndProductIds(repairOrderDTO.getShopId(), repairOrderDTO.getStorehouseId(), productIds.toArray(new Long[productIds.size()]));
        }
        Set<Long> businessCategoryIds = new HashSet<Long>();
        if (MapUtils.isNotEmpty(productLocalInfoDTOMap)) {
          for (ProductLocalInfoDTO productLocalInfoDTO : productLocalInfoDTOMap.values()) {
            if (productLocalInfoDTO != null && productLocalInfoDTO.getBusinessCategoryId() != null) {
              businessCategoryIds.add(productLocalInfoDTO.getBusinessCategoryId());
            }
          }
        }
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapById(appointOrderDTO.getShopId(), businessCategoryIds);

        for (AppointOrderMaterialDTO appointOrderMaterialDTO : appointOrderDTO.getItemDTOs()) {
          if (appointOrderMaterialDTO != null && StringUtils.isNotBlank(appointOrderMaterialDTO.getProductName())) {
            RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
            Long productLocalInfoId = appointOrderMaterialDTO.getProductId();
            ProductDTO productDTO = null;
            InventoryDTO inventoryDTO = null;
            CategoryDTO categoryDTO = null;
            if (productLocalInfoId != null) {
              productDTO = productDTOMap.get(productLocalInfoId);
              inventoryDTO = inventoryDTOMap.get(productLocalInfoId);
            }
            if (productDTO != null) {

              double inventoryAmountWithUnit = 0d;
              double purchasePriceWhitUnit = 0d;

              repairOrderItemDTO.setProductDTOWithOutUnit(productDTO);
              if (productDTO.getBusinessCategoryId() != null) {
                categoryDTO = categoryDTOMap.get(productDTO.getBusinessCategoryId());
                if (categoryDTO != null) {
                  repairOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
                  repairOrderItemDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
                }
              }

              repairOrderItemDTO.setUnit(appointOrderMaterialDTO.getUnit());
              repairOrderItemDTO.setAmount(appointOrderMaterialDTO.getAmount());
              repairOrderItemDTO.setPrice(appointOrderMaterialDTO.getPrice());
              if (inventoryDTO != null) {
                inventoryAmountWithUnit = NumberUtil.doubleVal(inventoryDTO.getAmount());
                purchasePriceWhitUnit = NumberUtil.doubleVal(inventoryDTO.getInventoryAveragePrice());
              }
              if (isHaveStoreHouse) {
                StoreHouseInventoryDTO storeHouseInventoryDTO = storeHouseInventoryDTOMap.get(productLocalInfoId);
                if (storeHouseInventoryDTO != null) {
                  inventoryAmountWithUnit = NumberUtil.doubleVal(storeHouseInventoryDTO.getAmount());
                }
              }
              if (UnitUtil.isStorageUnit(repairOrderItemDTO.getUnit(), productDTO)) {
                inventoryAmountWithUnit = NumberUtil.round(inventoryAmountWithUnit / productDTO.getRate(), 2);
                purchasePriceWhitUnit = NumberUtil.round(purchasePriceWhitUnit * productDTO.getRate(), 2);
              }
              repairOrderItemDTO.setInventoryAmount(inventoryAmountWithUnit);
              repairOrderItemDTO.setPurchasePrice(purchasePriceWhitUnit);
              double total = NumberUtil.round(NumberUtil.doubleVal(repairOrderItemDTO.getAmount()) * NumberUtil.doubleVal(repairOrderItemDTO.getPrice()), 2);
              itemTotal += total;
              repairOrderItemDTO.setTotal(total);
            } else {
              repairOrderItemDTO.fromAppointOrderMaterialWithoutProductId(appointOrderMaterialDTO);
              itemTotal += repairOrderItemDTO.getTotal();
            }
            repairOrderItemDTO.setReserved(0d);
            repairOrderItemDTOs.add(repairOrderItemDTO);
          }
        }
        repairOrderDTO.setItemDTOs(repairOrderItemDTOs.toArray(new RepairOrderItemDTO[repairOrderItemDTOs.size()]));
        repairOrderDTO.setProductTotal(itemTotal);
      }
      //服务项目
      if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())) {
        List<RepairOrderServiceDTO> repairOrderServiceDTOs = new ArrayList<RepairOrderServiceDTO>();
        Set<Long> serviceIds = new HashSet<Long>();
        for (AppointOrderServiceDetailDTO appointOrderServiceDetailDTO : appointOrderDTO.getServiceDTOs()) {
          if (appointOrderServiceDetailDTO != null && StringUtils.isNotBlank(appointOrderServiceDetailDTO.getService())) {
            if (appointOrderServiceDetailDTO.getServiceId() != null) {
              serviceIds.add(appointOrderServiceDetailDTO.getServiceId());
            }
          }
        }
        RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
        Map<Long, ServiceDTO> serviceDTOMap = rfiTxnService.getServiceDTOMapByIds(repairOrderDTO.getShopId(), serviceIds);
        Set<Long> categoryIds = new HashSet<Long>();
        if (MapUtils.isNotEmpty(serviceDTOMap)) {
          for (ServiceDTO serviceDTO : serviceDTOMap.values()) {
            if (serviceDTO != null && serviceDTO.getCategoryId() != null) {
              categoryIds.add(serviceDTO.getCategoryId());
            }
          }
        }
        Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapById(repairOrderDTO.getShopId(), categoryIds);
        for (AppointOrderServiceDetailDTO appointOrderServiceDetailDTO : appointOrderDTO.getServiceDTOs()) {

          if (appointOrderServiceDetailDTO != null && StringUtils.isNotBlank(appointOrderServiceDetailDTO.getService())) {
            RepairOrderServiceDTO repairOrderServiceDTO = new RepairOrderServiceDTO();
            repairOrderServiceDTO.fromAppointOrderServiceDetail(appointOrderServiceDetailDTO);
            if (appointOrderServiceDetailDTO.getServiceId() != null) {
              ServiceDTO serviceDTO = serviceDTOMap.get(appointOrderServiceDetailDTO.getServiceId());
              if (serviceDTO != null && serviceDTO.getCategoryId() != null) {
                CategoryDTO categoryDTO = categoryDTOMap.get(serviceDTO.getCategoryId());
                if (categoryDTO != null) {
                  repairOrderServiceDTO.setBusinessCategoryId(categoryDTO.getId());
                  repairOrderServiceDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
                }
              }
            }
            repairOrderServiceDTOs.add(repairOrderServiceDTO);
            serviceTotal += repairOrderServiceDTO.getTotal();

          }
          repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs.toArray(new RepairOrderServiceDTO[repairOrderServiceDTOs.size()]));
        }
        repairOrderDTO.setSalesTotal(NumberUtil.round(serviceTotal, 2));
      }
      repairOrderDTO.setTotal(NumberUtil.round(serviceTotal + itemTotal, 2));
    }
  }

  @Override
  public void generateCreateWashBeautyOrderItem(WashBeautyOrderDTO washBeautyOrderDTO, AppointOrderDTO appointOrderDTO) throws Exception {
    //服务项目
    double serviceTotal = 0;
    if (!ArrayUtils.isEmpty(appointOrderDTO.getServiceDTOs())) {
      List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>();
      Set<Long> serviceIds = new HashSet<Long>();
      for (AppointOrderServiceDetailDTO appointOrderServiceDetailDTO : appointOrderDTO.getServiceDTOs()) {
        if (appointOrderServiceDetailDTO != null && StringUtils.isNotBlank(appointOrderServiceDetailDTO.getService())) {
          if (appointOrderServiceDetailDTO.getServiceId() != null) {
            serviceIds.add(appointOrderServiceDetailDTO.getServiceId());
          }
        }
      }
      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      Map<Long, ServiceDTO> serviceDTOMap = rfiTxnService.getServiceDTOMapByIds(washBeautyOrderDTO.getShopId(), serviceIds);
      Set<Long> categoryIds = new HashSet<Long>();
      if (MapUtils.isNotEmpty(serviceDTOMap)) {
        for (ServiceDTO serviceDTO : serviceDTOMap.values()) {
          if (serviceDTO != null && serviceDTO.getCategoryId() != null) {
            categoryIds.add(serviceDTO.getCategoryId());
          }
        }
      }
      Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapById(washBeautyOrderDTO.getShopId(), categoryIds);
      for (AppointOrderServiceDetailDTO appointOrderServiceDetailDTO : appointOrderDTO.getServiceDTOs()) {

        if (appointOrderServiceDetailDTO != null && StringUtils.isNotBlank(appointOrderServiceDetailDTO.getService())) {
          WashBeautyOrderItemDTO washBeautyOrderItemDTO = new WashBeautyOrderItemDTO();
          washBeautyOrderItemDTO.setServiceId(appointOrderServiceDetailDTO.getServiceId());
          washBeautyOrderItemDTO.setServiceName(appointOrderServiceDetailDTO.getService());
          washBeautyOrderItemDTO.setPrice(appointOrderServiceDetailDTO.getTotal());
          washBeautyOrderItemDTO.setConsumeTypeStr(ConsumeType.MONEY);
          if (appointOrderServiceDetailDTO.getServiceId() != null) {
            ServiceDTO serviceDTO = serviceDTOMap.get(appointOrderServiceDetailDTO.getServiceId());
            if (serviceDTO != null && serviceDTO.getCategoryId() != null) {
              CategoryDTO categoryDTO = categoryDTOMap.get(serviceDTO.getCategoryId());
              if (categoryDTO != null) {
                washBeautyOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
                washBeautyOrderItemDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
              }
            }
          }
          washBeautyOrderItemDTOs.add(washBeautyOrderItemDTO);
          serviceTotal += washBeautyOrderItemDTO.getPrice();

        }
      }
      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs.toArray(new WashBeautyOrderItemDTO[washBeautyOrderItemDTOs.size()]));
      washBeautyOrderDTO.setTotal(NumberUtil.round(serviceTotal, 2));
    }
  }

  @Override
  public AppointOrderDTO generateAppointOrderByShopFaultCodeIds(Long shopId, String shopFaultInfoIds) {
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
    Set<Long> shopFaultInfoIdSet = NumberUtil.parseLongValuesToSet(shopFaultInfoIds, ",");
    String vehicleNo = "";
    FaultInfoToShopDTO firstFaultInfoToShopDTO = null;
    if (CollectionUtils.isNotEmpty(shopFaultInfoIdSet)) {
      IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
      List<FaultInfoToShopDTO> faultInfoToShopDTOs = shopFaultInfoService.getFaultInfoToShopDTOsByIds(shopId, shopFaultInfoIdSet.toArray(new Long[shopFaultInfoIdSet.size()]));
      if (CollectionUtils.isNotEmpty(faultInfoToShopDTOs)) {
        StringBuilder sb = new StringBuilder();
        sb.append("故障信息：");
        for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
          if (faultInfoToShopDTO != null) {
            if (StringUtils.isEmpty(vehicleNo) && StringUtils.isNotEmpty(faultInfoToShopDTO.getVehicleNo())) {
              vehicleNo = faultInfoToShopDTO.getVehicleNo();
            }
            if (firstFaultInfoToShopDTO == null) {
              firstFaultInfoToShopDTO = faultInfoToShopDTO;
            }
            if (StringUtils.isNotEmpty(faultInfoToShopDTO.getFaultCode())) {
              sb.append(faultInfoToShopDTO.getFaultCode()).append(",");
            }
            if (StringUtils.isNotEmpty(faultInfoToShopDTO.getFaultCodeCategory())) {
              sb.append(faultInfoToShopDTO.getFaultCodeCategory()).append(",");
            }
//            if (StringUtils.isNotEmpty(faultInfoToShopDTO.getFaultCodeDescription())) {
//              sb.append(faultInfoToShopDTO.getFaultCodeDescription());
//            }
          }
        }
        appointOrderDTO.setRemark(sb.toString());
        appointOrderDTO.setFaultInfoToShopDTOs(faultInfoToShopDTOs.toArray(new FaultInfoToShopDTO[faultInfoToShopDTOs.size()]));
      }
    }
    VehicleDTO vehicleDTO = null;
    CustomerDTO customerDTO = null;
    if (StringUtils.isNotEmpty(vehicleNo)) {
      vehicleDTO = vehicleService.getVehicleDTOByLicenceNo(shopId, vehicleNo);
      if (vehicleDTO != null && vehicleDTO.getId() != null) {
        CustomerVehicleDTO customerVehicleDTO = CollectionUtil.getFirst(userService.getCustomerVehicleByVehicleId(vehicleDTO.getId()));
        if (customerVehicleDTO != null && customerVehicleDTO.getCustomerId() != null) {
          customerDTO = customerService.getCustomerById(customerVehicleDTO.getCustomerId(), shopId);
        }
      }
    }
    AppUserDTO appUserDTO = null;
    AppVehicleDTO appVehicleDTO = null;
    if (firstFaultInfoToShopDTO != null) {
      if (StringUtils.isNotEmpty(firstFaultInfoToShopDTO.getAppUserNo())) {
        appUserDTO = appUserService.getAppUserByUserNo(firstFaultInfoToShopDTO.getAppUserNo(), null);
      }
      if (firstFaultInfoToShopDTO.getAppVehicleId() != null) {
        appVehicleDTO = appUserService.getAppVehicleDTOById(firstFaultInfoToShopDTO.getAppVehicleId());
      }
    }

    if (vehicleDTO == null && appVehicleDTO != null) {
      vehicleDTO = new VehicleDTO();
      vehicleDTO.addFromAppVehicleDTO(appVehicleDTO);
    }
    if (customerDTO == null && appUserDTO != null) {
      customerDTO = new CustomerDTO();
      customerDTO.addFromAppUserDTO(appUserDTO);
    }
    appointOrderDTO.setVehicleDTO(vehicleDTO);
    appointOrderDTO.setCustomerDTO(customerDTO);


    appointOrderDTO.setItemDTOs(new AppointOrderMaterialDTO[]{new AppointOrderMaterialDTO()});
    appointOrderDTO.setServiceDTOs(new AppointOrderServiceDetailDTO[]{new AppointOrderServiceDetailDTO()});

    return appointOrderDTO;
  }


  /**
   * 更新预约单 预约时间
   *
   * @param appointOrderDTO
   */
  public Result updateAppointOrderTime(AppointOrderDTO appointOrderDTO, Long appointOrderTime) {
    Result result = new Result();
    if (appointOrderDTO == null || appointOrderDTO.getId() == null || appointOrderDTO.getShopId() == null) {
      return new Result("要接受的预约单不存在！", false);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppointOrder appointOrder = writer.getAppointOrderById(appointOrderDTO.getId(), appointOrderDTO.getShopId());
      if (appointOrder != null) {
        appointOrder.setAppointTime(appointOrderTime);
        writer.update(appointOrder);
        writer.commit(status);
      } else {
        result = new Result("预约单操作异常，请检查数据！", false);
      }
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  /**
   * 更新预约单
   *
   * @param appointOrderDTO
   */
  @Override
  public void updateAppointOrder_status(AppointOrderDTO appointOrderDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      AppointOrder appointOrder = writer.getAppointOrderById(appointOrderDTO.getId(), appointOrderDTO.getShopId());
      if (appointOrder != null) {
        appointOrder.setStatus(AppointOrderStatus.CANCELED);
        writer.update(appointOrder);
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
   public AppointOrderDTO generateAppointOrderByCustomerId(Long customerId,Long shopId,String appUserNo) throws Exception {
    AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
    appointOrderDTO.setShopId(shopId);
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
    if (customerDTO != null) {
      appointOrderDTO.setCustomerDTO(customerDTO);
      appointOrderDTO.setCustomerId(customerDTO.getId());
      appointOrderDTO.setCustomerMobile(customerDTO.getMobile());
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      MemberDTO memberDTO = membersService.getMemberByCustomerId(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerId());
      if (memberDTO != null) {
        MemberStatus memberStatus = membersService.getMemberStatusByMemberDTO(memberDTO);
        appointOrderDTO.setMemberDTO(memberDTO);
        appointOrderDTO.setMemberStatus(memberStatus.getStatus());
      }
    }
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId));
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    if(appUserCustomerDTO!=null){
      AppVehicleDTO appVehicleDTO =  appUserVehicleObdService.getAppVehicleById(appUserCustomerDTO.getAppVehicleId());
      if (appVehicleDTO != null) {
        appointOrderDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
        appointOrderDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
        appointOrderDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
        appointOrderDTO.setCurrentMileage(appVehicleDTO.getCurrentMileage());
        appointOrderDTO.setVehicleMobile(appVehicleDTO.getMobile());
      }
    }
    appointOrderDTO.setItemDTOs(new AppointOrderMaterialDTO[]{new AppointOrderMaterialDTO()});
    appointOrderDTO.setServiceDTOs(new AppointOrderServiceDetailDTO[]{new AppointOrderServiceDetailDTO()});
    return appointOrderDTO;
  }

  @Override
  public AppointOrderDTO generateAppointOrderByAppUserNo(Long shopId,String appUserNo) throws Exception {
    AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
    appointOrderDTO.setShopId(shopId);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    CustomerDTO customerDTO =null;
    AppVehicleDTO appVehicleDTO = null;
    AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId));
    if(appUserCustomerDTO!=null){
       customerDTO = customerService.getCustomerById(appUserCustomerDTO.getCustomerId());
       appVehicleDTO =  appUserVehicleObdService.getAppVehicleById(appUserCustomerDTO.getAppVehicleId());
    }
    if (customerDTO != null) {
      appointOrderDTO.setCustomerDTO(customerDTO);
      appointOrderDTO.setCustomerId(customerDTO.getId());
      appointOrderDTO.setCustomerMobile(customerDTO.getMobile());
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      MemberDTO memberDTO = membersService.getMemberByCustomerId(appointOrderDTO.getShopId(), appointOrderDTO.getCustomerId());
        if (memberDTO != null) {
          MemberStatus memberStatus = membersService.getMemberStatusByMemberDTO(memberDTO);
          appointOrderDTO.setMemberDTO(memberDTO);
          appointOrderDTO.setMemberStatus(memberStatus.getStatus());
        }
     }
      if (appVehicleDTO != null) {
        appointOrderDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
        appointOrderDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
        appointOrderDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
        appointOrderDTO.setCurrentMileage(appVehicleDTO.getCurrentMileage());
        appointOrderDTO.setVehicleMobile(appVehicleDTO.getMobile());
      }
    appointOrderDTO.setItemDTOs(new AppointOrderMaterialDTO[]{new AppointOrderMaterialDTO()});
    appointOrderDTO.setServiceDTOs(new AppointOrderServiceDetailDTO[]{new AppointOrderServiceDetailDTO()});
    return appointOrderDTO;
  }

}
