package com.bcgogo.txn;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.cache.DataHolder;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.enums.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.RepairOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.user.dto.CouponConsumeRecordDTO;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.service.ConsumingService;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-12
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/repair.do")
public class RepairController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(RepairController.class);
  public static final Long REPAIR_ORDER_STATUS_NEW = 1L;
  public static final Long REPAIR_ORDER_STATUS_EXIST = 2L;

  /**
   * 施工单-派单
   * @param model
   * @param repairOrderDTO
   * @param request
   * @return
   */
  @RequestMapping(params = "method=dispatchRepairOrder")
  public String dispatchRepairOrder(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    LOG.info("dispatchRepairOrder施工单派单改单开始!:shopId:{}", shopId);
    Long userId = WebUtil.getUserId(request);
    OperationTypes operationType = null;
    String submitBtnType = "";
    try {
      long begin = System.currentTimeMillis();
      long current = System.currentTimeMillis();
      repairOrderDTO.setShopId(shopId);
      repairOrderDTO.setUserId(userId);
      repairOrderDTO.setPrint("false");
      repairOrderDTO.setStatus(OrderStatus.REPAIR_DISPATCH);
      DataHolder dataHolder = new DataHolder();
      //组装仓库的select
      prepareStorehouseForRepairOrderDTO(model, repairOrderDTO, request, dataHolder);
      Result result = validateSaveRepairOrder(repairOrderDTO);
      if (result != null && !result.isSuccess()) {
        return result.getMsg();
      }
      preProcessRepairOrder(model, repairOrderDTO, request, dataHolder);
      if (null == repairOrderDTO.getId()) {
        submitBtnType = RepairOrderSubmitType.DISPATCH.getName();
        operationType = OperationTypes.CREATE;
        if (userService.isRepairPickingSwitchOn(shopId)) {
          repairService.saveRepairOrderWithPicking(repairOrderDTO);
        } else {
          repairOrderDTO = repairService.RFCreateRepairOrder(repairOrderDTO);
        }
        repairOrderDTO.setOrderStatus(REPAIR_ORDER_STATUS_NEW);
        if (repairOrderDTO.getInsuranceOrderId() != null) {
          ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
            new OperationLogDTO(repairOrderDTO.getShopId(), repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.INSURANCE_ORDER));
        }
      } else {
        submitBtnType = RepairOrderSubmitType.CHANGE.getName();
        operationType = OperationTypes.UPDATE;
        if (userService.isRepairPickingSwitchOn(shopId)) {
          repairService.updateRepairOrderWithPicking(repairOrderDTO);
        } else {
          repairOrderDTO = repairService.RFUpdateRepairOrder(repairOrderDTO);
        }
        repairOrderDTO.setOrderStatus(REPAIR_ORDER_STATUS_EXIST);
      }
      LOG.debug("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      afterProcessRepairOrder(model, repairOrderDTO, request, operationType);
      LOG.debug("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
      LOG.debug("性能监控: 施工单派单改单进程——结束。执行时间: {} ms", System.currentTimeMillis() - begin);
      return "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
        "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success&btnType=" + submitBtnType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("/Repair.do?method=saveRepairOrder,shopId={},userId ={},repairOrderDTO ={}," + e.getMessage(),
        new String[]{String.valueOf(shopId), String.valueOf(userId), repairOrderDTO.toString()});
      model.addAttribute("resultMsg", "failure");
      model.addAttribute("btnType", "save");
      return "txn/invoicing";
    }
  }

  @RequestMapping(params = "method=finishRepairOrder")
  public String finishRepairOrder(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request) {
    LOG.info("性能监控:施工单完工开始!");
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    OperationTypes operationType = null;
    String submitBtnType = "";
    try {
      long begin = System.currentTimeMillis();
      long current = System.currentTimeMillis();
      repairOrderDTO.setShopId(shopId);
      repairOrderDTO.setUserId(userId);
      repairOrderDTO.setPrint("false");
      repairOrderDTO.setStatus(OrderStatus.REPAIR_DONE);
      submitBtnType = RepairOrderSubmitType.DONE.getName();
      operationType = OperationTypes.FINISH;
      DataHolder dataHolder = new DataHolder();
      //组装仓库的select
      prepareStorehouseForRepairOrderDTO(model, repairOrderDTO, request, dataHolder);
      Result result = validateSaveRepairOrder(repairOrderDTO);
      if (result != null && !result.isSuccess()) {
        return result.getMsg();
      }
      preProcessRepairOrder(model, repairOrderDTO, request, dataHolder);

      if (null == repairOrderDTO.getId()) {
        repairOrderDTO = repairService.RFCreateRepairOrder(repairOrderDTO);
        if (repairOrderDTO.getInsuranceOrderId() != null) {
          ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
            new OperationLogDTO(repairOrderDTO.getShopId(), repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.INSURANCE_ORDER));
        }
      } else {
        if (userService.isRepairPickingSwitchOn(shopId)) {
          repairService.updateRepairOrderWithPicking(repairOrderDTO);
        } else {
          repairOrderDTO = repairService.RFUpdateRepairOrder(repairOrderDTO);
        }
      }
      LOG.debug("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      afterProcessRepairOrder(model, repairOrderDTO, request, operationType);
      LOG.debug("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
      LOG.debug("性能监控:保存施工单完工——结束。执行时间: {} ms", System.currentTimeMillis() - begin);
      return "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
        "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success&btnType=" + submitBtnType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("/Repair.do?method=saveRepairOrder,shopId={},userId ={},repairOrderDTO ={}," + e.getMessage(),
        new String[]{String.valueOf(shopId), String.valueOf(userId), repairOrderDTO.toString()});
      model.addAttribute("resultMsg", "failure");
      model.addAttribute("btnType", "save");
      return "txn/invoicing";
    }
  }

  @RequestMapping(params = "method=accountRepairOrder")
  public String accountRepairOrder(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    LOG.info("accountRepairOrder施工单结算开始!,shopId:{}", shopId);
    OperationTypes operationType = null;
    String submitBtnType = "";
    try {
      long begin = System.currentTimeMillis();
      long current = System.currentTimeMillis();
      repairOrderDTO.setShopId(shopId);
      repairOrderDTO.setUserId(userId);
      if (StringUtils.isBlank(repairOrderDTO.getPrint())) {
        repairOrderDTO.setPrint("false");
      }
      submitBtnType = RepairOrderSubmitType.SETTLED.getName();
      operationType = OperationTypes.DEBT_SETTLE;
      repairOrderDTO.setStatus(OrderStatus.REPAIR_SETTLED);
      repairOrderDTO.setEditDate(System.currentTimeMillis());
      DataHolder dataHolder = new DataHolder();
      //组装仓库的select
      prepareStorehouseForRepairOrderDTO(model, repairOrderDTO, request, dataHolder);
      Result result = validateSaveRepairOrder(repairOrderDTO);
      if (result != null && !result.isSuccess()) {
        return result.getMsg();
      }
      preProcessRepairOrder(model, repairOrderDTO, request, dataHolder);
      //归属时间设置  （通过出厂时间来设置）出厂日期 > 当前时间 使用当前时间
      Long vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getEndDateStr());
      if (System.currentTimeMillis() - NumberUtil.longValue(vestDate) < 1000 * 60 || vestDate == null) {
        vestDate = System.currentTimeMillis();
      }
      repairOrderDTO.setVestDate(vestDate);
      //如果是维修美容保养
      if (repairOrderDTO.getServiceType() != null && repairOrderDTO.getServiceType().equals(OrderTypes.REPAIR)) {
        //如果是结算 (改汽修单状态)
        if (null == repairOrderDTO.getId()) {
          //直接结算
          repairOrderDTO = repairService.RFCreateRepairOrder(repairOrderDTO);
          if (repairOrderDTO.getInsuranceOrderId() != null) {
            ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
              new OperationLogDTO(repairOrderDTO.getShopId(), repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.INSURANCE_ORDER));
          }
        } else {
          //派单之后 结算
          if (userService.isRepairPickingSwitchOn(shopId)) {
            repairService.updateRepairOrderWithPicking(repairOrderDTO);
          } else {
            repairOrderDTO = repairService.RFUpdateRepairOrder(repairOrderDTO);
          }
        }
        //更新会员信息, 并发送提醒短信给持卡人（如果勾上）
        VelocityContext context = txnService.updateMemberInfo(repairOrderDTO);
        repairOrderDTO.setMemberSmsVelocityContext(context);   //线程里需要用于发送会员短信
      }
      LOG.debug("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      afterProcessRepairOrder(model, repairOrderDTO, request, operationType);
      LOG.debug("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
      LOG.debug("性能监控:保存施工单结算——结束。执行时间: {} ms", System.currentTimeMillis() - begin);
      LOG.info("施工单保存成功,repairOrderId:{}", repairOrderDTO.getId());
      return "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
        "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success&btnType=" + submitBtnType;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("/Repair.do?method=saveRepairOrder,shopId={},userId ={},repairOrderDTO ={}," + e.getMessage(),
        new String[]{String.valueOf(shopId), String.valueOf(userId), repairOrderDTO.toString()});
      model.addAttribute("resultMsg", "failure");
      model.addAttribute("btnType", "save");
      return "txn/invoicing";
    }
  }

  private Result validateSaveRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception {
    Result result = new Result();
    if (repairOrderDTO.getShopId() == null || !repairOrderDTO.isValidateSuccess()) {
      LOG.warn("saveRepairOrder error, repairOrderDTO attributes are empty!");
      result = new Result("/txn/invoicing", false);
      return result;
    }
    if (repairOrderDTO.getId() != null) {
      RepairOrderDTO dbRepairOrderDTO = repairService.getSimpleRepairOrderDTO(repairOrderDTO.getShopId(), repairOrderDTO.getId());
      if (OrderStatus.REPAIR_SETTLED.equals(dbRepairOrderDTO.getStatus())) {
        LOG.warn("Repair Order [{}] 已经被结算过", repairOrderDTO.getId());
        String msg = "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
          "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success&btnType=" + RepairOrderSubmitType.SETTLED.getName();
        result = new Result(msg, false);
      }
    }
    return result;
  }

  private void prepareRepairOrder(RepairOrderDTO repairOrderDTO, HttpServletRequest request) throws Exception {
    Long shopVersionId = WebUtil.getShopVersionId(request);
    String username = WebUtil.getUserName(request);
    repairOrderDTO.setShopVersionId(shopVersionId);
    repairOrderDTO.setUserName(username);
    repairOrderDTO.setVechicle(repairOrderDTO.getLicenceNo());
    repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
    repairOrderDTO.setInventoryLimitDTO(new InventoryLimitDTO());
    repairOrderDTO.getInventoryLimitDTO().setShopId(repairOrderDTO.getShopId());
    //折扣
    if (null == repairOrderDTO.getAfterMemberDiscountTotal()) {
      repairOrderDTO.setAfterMemberDiscountTotal(repairOrderDTO.getTotal());
    }
    repairOrderDTO.setStartDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getStartDateStr()));       //进厂时间
    repairOrderDTO.setEndDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getEndDateStr()));       //预计出厂时间
    //归属时间设置  （通过入场时间来设置）  入厂时间 > 当前时间 使用当前时间
    Long settleDate = repairOrderDTO.getEndDate();
    if (settleDate != null && (System.currentTimeMillis() - settleDate > 1000 * 60)) {
      settleDate = repairOrderDTO.getStartDate();
    } else {
      settleDate = System.currentTimeMillis();
    }
    repairOrderDTO.setSettleDate(settleDate);
    repairOrderDTO.setEditDate(System.currentTimeMillis());
  }

  //组装仓库的select
  private void prepareStorehouseForRepairOrderDTO(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request, DataHolder dataHolder) throws Exception {
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      Map<Long, StoreHouseDTO> storeHouseDTOMap = storeHouseService.getAllStoreHousesMapByShopId(WebUtil.getShopId(request));
      dataHolder.setStoreHouseDTOMap(storeHouseDTOMap);
      if (repairOrderDTO.getStorehouseId() != null) {
        StoreHouseDTO storeHouseDTO = storeHouseDTOMap.get(repairOrderDTO.getStorehouseId());
        repairOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
      }
      model.addAttribute("storeHouseDTOList", storeHouseDTOMap.values());//select 选项
    }
  }

  /**
   * 批量保存营业分类，商品分类，其他服务项目
   *
   * @param repairOrderDTO
   * @throws Exception
   */
  private void prepareRepairOrderItemInfo(RepairOrderDTO repairOrderDTO) throws Exception {
    RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    Long shopId = repairOrderDTO.getShopId();
    Set<String> categoryNames = new HashSet<String>();
    //组装营业分类
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (null == repairOrderItemDTO || StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          continue;
        }
        if (StringUtils.isNotBlank(repairOrderItemDTO.getBusinessCategoryName())) {
          categoryNames.add(repairOrderItemDTO.getBusinessCategoryName().trim());
        }
      }
    }
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (null == repairOrderServiceDTO || StringUtils.isBlank(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (StringUtils.isNotBlank(repairOrderServiceDTO.getBusinessCategoryName())) {
          categoryNames.add(repairOrderServiceDTO.getBusinessCategoryName().trim());
        }
      }
    }

    Map<String, CategoryDTO> categoryDTOMap = rfiTxnService.batchSaveAndGetCateGory(repairOrderDTO.getShopId(), categoryNames);

    //保存材料栏的营业分类
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (null == repairOrderItemDTO || StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          continue;
        }
        if (StringUtils.isNotBlank(repairOrderItemDTO.getBusinessCategoryName())) {
          repairOrderItemDTO.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName().trim());
          CategoryDTO categoryDTO = categoryDTOMap.get(repairOrderItemDTO.getBusinessCategoryName());
          if (categoryDTO != null) {
            repairOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
          }
        }
      }
    }

    //保存服务栏的营业分类
    if (!ArrayUtils.isEmpty(repairOrderServiceDTOs)) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
        if (null == repairOrderServiceDTO || StringUtils.isBlank(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (StringUtils.isNotBlank(repairOrderServiceDTO.getBusinessCategoryName())) {
          repairOrderServiceDTO.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName().trim());
          CategoryDTO categoryDTO = categoryDTOMap.get(repairOrderServiceDTO.getBusinessCategoryName());
          if (categoryDTO != null) {
            repairOrderServiceDTO.setBusinessCategoryId(categoryDTO.getId());
          }
        }
      }
    }

    Double otherIncomeTotal = 0D;
    //其他费用栏目
    Set<String> kindNames = new HashSet<String>();
    List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;
    if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        if (StringUtils.isBlank(itemDTO.getName())) {
          continue;
        }
        otherIncomeTotal += NumberUtil.doubleVal(itemDTO.getPrice());
        kindNames.add(itemDTO.getName().trim());
        if (null == otherIncomeItemDTOList) {
          otherIncomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
        }
        otherIncomeItemDTOList.add(itemDTO);
      }
    }
    repairOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);
    repairOrderDTO.setOtherIncomeTotal(NumberUtil.toReserve(otherIncomeTotal, NumberUtil.MONEY_PRECISION));
    txnService.batchSaveOrUpdateOtherIncomeKind(shopId, kindNames);
  }

  private void preProcessRepairOrder(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request,
                                     DataHolder dataHolder) throws Exception {
    if (dataHolder == null) {
      dataHolder = new DataHolder();
    }
    //为施工单组装一些静态数据
    prepareRepairOrder(repairOrderDTO, request);
    //单据号
    if (StringUtils.isBlank(repairOrderDTO.getReceiptNo())) {
      repairOrderDTO.setReceiptNo(txnService.getReceiptNo(repairOrderDTO.getShopId(), OrderTypes.REPAIR, null));
    }
    //车辆信息
    rfiTxnService.populateRepairOrderDTO(repairOrderDTO);//维修单 所填车辆若为新车型，则新增，并将ID保存到此维修单
    //更新 customer CustomerRecordDTO 更新车辆信息
    customerService.handleCustomerForRepairOrder(repairOrderDTO, WebUtil.getShopId(request), WebUtil.getUserId(request));
    txnService.saveRepairOrderRemindEvent(repairOrderDTO);
    customerService.updateCustomerRecordForRepairOrder(repairOrderDTO);

    prepareRepairOrderItemInfo(repairOrderDTO);
    repairService.saveOrUpdateProductForRepairOrder(repairOrderDTO);
    //保存施工人,销售人
    repairService.setServiceWorksAndProductSaler(repairOrderDTO);
    //查询有没有服务记录 没有就创建 有就更新
    if (repairOrderDTO.getId() == null) {
      serviceVehicleCountService.saveOrUpdateServiceVehicleCount(WebUtil.getShopId(request), System.currentTimeMillis());
    }
    //施工单有关联的预约单的话，把预约单的appUserNo 放到施工单里面
    if (repairOrderDTO.getAppointOrderId() != null) {
      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      AppointOrderDTO appointOrderDTO = appointOrderService.getSimpleAppointOrderById(repairOrderDTO.getShopId(), repairOrderDTO.getAppointOrderId());
      if (appointOrderDTO != null && StringUtils.isNotBlank(appointOrderDTO.getAppUserNo())) {
        repairOrderDTO.setAppUserNo(appointOrderDTO.getAppUserNo());
      }
    }
    //待添加代金券消费记录couponConsumeRecordDTO到repairOrderDTO
    if(repairOrderDTO.getConsumingRecordId()!=null){
      IConsumingService consumingService=ServiceManager.getService(ConsumingService.class);
      CouponConsumeRecordDTO couponConsumeRecordDTO=consumingService.getCouponConsumeRecordById(repairOrderDTO.getConsumingRecordId());
      if(couponConsumeRecordDTO==null&&(OrderStatus.REPAIR_SETTLED.equals(couponConsumeRecordDTO.getOrderStatus())||OrderStatus.REPEAL.equals(couponConsumeRecordDTO.getOrderStatus()))){
        couponConsumeRecordDTO=new CouponConsumeRecordDTO();
      }
      repairOrderDTO.setCouponConsumeRecordDTO(couponConsumeRecordDTO);
      repairOrderDTO.setCouponAmount(couponConsumeRecordDTO.getCoupon());
    }
  }

  private void afterProcessRepairOrder(ModelMap model, RepairOrderDTO repairOrderDTO,
                                       HttpServletRequest request, OperationTypes operationType) throws Exception {
    //更新代金券消费记录
    repairService.updateConsumingRecordFromRepairOrder(repairOrderDTO);
    //发送微信账单到车主
    if (OperationTypes.DEBT_SETTLE.equals(operationType) || OperationTypes.SETTLE.equals(operationType)) {
      ServiceManager.getService(WXTxnService.class).sendConsumeMsg(repairOrderDTO);
    }
    //保存施工单的操作日志
    ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
      new OperationLogDTO(repairOrderDTO.getShopId(), repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, operationType));
    //删除草稿单
    if (StringUtils.isNotBlank(repairOrderDTO.getDraftOrderIdStr())) {
      ServiceManager.getService(DraftOrderService.class).deleteDraftOrder(repairOrderDTO.getShopId(), Long.valueOf(repairOrderDTO.getDraftOrderIdStr()));
    }
    //修改保险理赔
    ServiceManager.getService(IInsuranceService.class).RFupdateInsuranceOrderById(repairOrderDTO.getId(), null, repairOrderDTO.getInsuranceOrderId(), repairOrderDTO.getReceiptNo());

    //更新预约单信息
    ServiceManager.getService(IAppointOrderService.class).handelAppointOrderAfterSaveRepairOrder(repairOrderDTO);

    RepairOrderSavedEvent repairOrderSavedEvent = new RepairOrderSavedEvent(repairOrderDTO);
    BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
    String isRunThread = request.getParameter("isRunThread");
    if (!"noRun".equals(isRunThread)) {
      bcgogoEventPublisher.publisherRepairOrderSaved(repairOrderSavedEvent);
    }
    request.setAttribute("UNIT_TEST", repairOrderSavedEvent); //单元测试
    repairOrderSavedEvent.setMainFlag(true);
  }

  @ResponseBody
  @RequestMapping(params = "method=getMemberData")
  public Object getMemberData(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    String customerId = request.getParameter("customerId") == null ? "" : request.getParameter("customerId");
    if (customerId.equals("")) {
      LOG.error("customerId 为空");
      return new MemberDTO();
    }
    try {
      MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(shopId, NumberUtil.longValue(customerId));
      if (memberDTO != null) {
        if (memberDTO.getMemberServiceDTOs() != null) {
          RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
          for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
            Service service = txnService.getServiceById(memberServiceDTO.getServiceId());
            if (service != null) {
              memberServiceDTO.setServiceName(service.getName());
            }
          }
        }
      }
      return memberDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new MemberDTO();
    }
  }

  /**
   * 代金券--快速结算
   * @param request
   * @param consumingRecordId
   * @return
   */
  @ResponseBody
  @RequestMapping(params = "method=autoAccountRepairOrder")
  public Result autoAccountRepairOrder(HttpServletRequest request, Long consumingRecordId) {
    try {
      if (consumingRecordId == null) {
        return new Result("代金券消费记录不存在", false);
      }
      RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
      repairOrderDTO.setUserId(WebUtil.getUserId(request));
      repairOrderDTO.setUserName(WebUtil.getUserName(request));
      //添加代金券消费记录id
      repairOrderDTO.setConsumingRecordId(consumingRecordId);
      return repairService.accountRepairOrderByCouponConsumingRecord(repairOrderDTO);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
