package com.bcgogo.remind;

import com.bcgogo.api.*;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.etl.ImpactVideoExpDTO;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.remind.dto.ShopPlanDTO;
import com.bcgogo.remind.dto.TitlePromptDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.pushMessage.ShopTalkMessageDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.dto.pushMessage.impact.ImpactInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.mileage.MileageInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.sos.SosInfoSearchConditionDTO;
import com.bcgogo.txn.model.RepairOrderService;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.txn.service.remind.ICustomerRemindService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.model.app.AppVehicle;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/remind.do")
public class RemindController {
  private static final Logger LOG = LoggerFactory.getLogger(RemindController.class);
  private static Long defaultSize = 10l;

  @Autowired
  private RemindEventStrategySelector remindEventStrategySelector;

  //闪动提醒判断的开始时间：昨天的23:59:59-999
  private Long getFlashTime() throws Exception {
    return DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) - 1;
  }


  private Calendar getDateFromLong(Long time) {
    Calendar c = Calendar.getInstance();
    Date d = new Date(time);
    c.setTime(d);
    return c;
  }

//  /**
//   * 本店计划 单击发送短信
//   *
//   * @param request
//   * @param response
//   * @author zhangjuntao
//   * @description 本店计划项目 放入定时钟中
//   */
//  @RequestMapping(params = "method=smsSendPlan")
//  public void smsSendPlan(HttpServletRequest request, HttpServletResponse response) {
//    try {
//      Long shopId = (Long) request.getSession().getAttribute("shopId");
//      String customerIds = request.getParameter("customerIds");
//      String content = request.getParameter("content");
//      String customerType = request.getParameter("customerType");
//      String id = request.getParameter("idStr");
//      if (shopId != null && !StringUtil.isEmpty(id)) {
//        IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
//        ShopPlanDTO shopPlanDTO = new ShopPlanDTO();
//        shopPlanDTO.setId(Long.valueOf(id));
//        shopPlanDTO.setShopId(shopId);
//        shopPlanDTO.setContent(content);
//        shopPlanDTO.setCustomerIds(customerIds);
//        shopPlanDTO.setCustomerType(customerType);
//        shopPlanDTO.setSmsChannel(SmsChannel.MARKETING);
//        shopPlanService.sendPlanSms(shopPlanDTO);
//      }
//    } catch (Exception e) {
//      LOG.debug("/remind.do");
//      LOG.debug("method=smsSendPlan");
//      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
//      LOG.error(e.getMessage(), e);
//    }
//  }

  @RequestMapping(params = "method=newtodo")
  public String newToDo(HttpServletRequest request, ModelMap modelMap) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    LOG.info("查看代办事项。shopId:{}",shopId);
    if (shopId == null) {
      return "/remind/newtodo";
    }
    String access = request.getParameter("access");
    if (StringUtil.isNotEmpty(access)) {
      request.setAttribute("access", access);
    }
    modelMap.put("wsUrl", ConfigUtils.getWSUrl());
    modelMap.put("shopId", shopId);
    return "/remind/newtodo";
  }

  @RequestMapping(params = "method=getNewTodoCounts")
  @ResponseBody
  public Result getNewTodoCounts(HttpServletRequest request) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    IRepairService repairService = ServiceManager.getService(IRepairService.class);
    if (shopId == null) {
      return new Result(false);
    }
    Result result = new Result(true);
    StopWatchUtil sw = new StopWatchUtil("getNewTodoCountsSw");
    sw.stopAndStart("countRemind");
    RemindEventStrategy repairRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.REPAIR);
    RemindEventStrategy debtRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.DEBT);
    RemindEventStrategy txnRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.TXN);
    RemindEventStrategy customerRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.CUSTOMER_SERVICE);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Map<String, Integer> countMap = new HashMap<String, Integer>();
    result.setData(countMap);
    try {
      //维修美容提醒数量
      int repairRemindSize = txnService.countRemindEvent(shopId, null);
      sw.stopAndStart("lack");
      int lack = txnService.countRemindEvent(shopId, RepairRemindEventTypes.LACK);
      sw.stopAndStart("incoming");
      int incoming = txnService.countRemindEvent(shopId, RepairRemindEventTypes.INCOMING);
      sw.stopAndStart("pending");
      int pending = txnService.countRemindEvent(shopId, RepairRemindEventTypes.PENDING);
      sw.stopAndStart("wait_out");
      int waitOutStorage = txnService.countRemindEvent(shopId, RepairRemindEventTypes.WAIT_OUT_STORAGE);
      sw.stopAndStart("todayService");
      int serviceTodayTimes = repairService.countRepairOrderByDate(shopId, DateUtil.getStartTimeOfToday(), DateUtil.getEndTimeOfToday());//今天服务次数
      sw.stopAndStart("yesterdayService");
      int serviceYesterdayTimes = repairService.countRepairOrderByDate(shopId, DateUtil.getStartTimeOfYesterday(), DateUtil.getEndTimeOfYesterday());//昨天服务次数
      sw.stopAndStart("todayNewUser");
      int todayNewUserNumber = userService.countRepairOrderHistoryByNewVehicle(shopId, null, null, null, DateUtil.getStartTimeOfToday(), DateUtil.getEndTimeOfToday()); //当天新增用户数
      countMap.put("repairRemindSize", repairRemindSize);
      countMap.put("lack", lack);
      countMap.put("incoming", incoming);
      countMap.put("pending", pending);
      countMap.put("waitOutStorage", waitOutStorage);
      countMap.put("serviceTodayTimes", serviceTodayTimes);
      countMap.put("serviceYesterdayTimes", serviceYesterdayTimes);
      countMap.put("todayNewUserNumber", todayNewUserNumber);
      sw.stopAndStart("countArrears");
      int countArrears = debtRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
      sw.stopAndStart("countOverdue");
      int countArrearsRemindIsOverdue = debtRemindEventStrategy.countRemindEvent(shopId, true, false, getFlashTime());
      sw.stopAndStart("countNotOverdue");
      int countArrearsRemindIsNotOverdue = debtRemindEventStrategy.countRemindEvent(shopId, false, false, getFlashTime());
      sw.stopAndStart("countHasRemind");
      int countArrearsRemindHasRemind = debtRemindEventStrategy.countRemindEvent(shopId, null, true, getFlashTime());
      countMap.put("countArrears", countArrears);
      countMap.put("countArrearsRemindIsOverdue", countArrearsRemindIsOverdue);
      countMap.put("countArrearsRemindIsNotOverdue", countArrearsRemindIsNotOverdue);
      countMap.put("countArrearsRemindHasRemind", countArrearsRemindHasRemind);

      sw.stopAndStart("txn");
      //进销存提醒
      int iNumber = txnRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
      sw.stopAndStart("customerServiceJob");
      countMap.put("inventoryRemindSize", iNumber);

      //客户服务提醒
      int customerServiceJobNumber = customerRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
      sw.stopAndStart("customerRemindOverdue");
      int countCustomerRemindIsOverdue = customerRemindEventStrategy.countRemindEvent(shopId, true, false, getFlashTime());
      sw.stopAndStart("customerRemindNotOverdue");
      int countCustomerRemindIsNotOverdue = customerRemindEventStrategy.countRemindEvent(shopId, false, false, getFlashTime());
      sw.stopAndStart("customerRemindReminded");
      int countCustomerRemindHasRemind = customerRemindEventStrategy.countRemindEvent(shopId, null, true, getFlashTime());
      sw.stopAndPrintLog();
      countMap.put("customerServiceJobNumber", customerServiceJobNumber);
      countMap.put("countCustomerRemindIsOverdue", countCustomerRemindIsOverdue);
      countMap.put("countCustomerRemindIsNotOverdue", countCustomerRemindIsNotOverdue);
      countMap.put("countCustomerRemindHasRemind", countCustomerRemindHasRemind);
    } catch (Exception e) {
      LOG.error("getNewTodoCounts error", e);
      return new Result(false);
    }
    return result;
  }

  @RequestMapping(params = "method=repairRemind")
  @ResponseBody
  public PagingListResult repairRemind(HttpServletRequest request, Integer startPageNo) {
    StopWatchUtil sw = new StopWatchUtil("repairRemindSw");
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int start = startPageNo == null ? 1 : startPageNo;
    int pageSize = 10;
    String remindType = request.getParameter("remindType");
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);

    PagingListResult<RepairRemindResponse> result = new PagingListResult<RepairRemindResponse>();
    List<RepairRemindResponse> repairRemindResponseList = new ArrayList<RepairRemindResponse>();

    try {
      //待办事项选择器
      RemindEventStrategy repairRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.REPAIR);
//      int totalCount = repairRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
//      List<RemindEventDTO> repairRemindEventDTOList = repairRemindEventStrategy.queryRemindEvent(shopId, null, null, getFlashTime(), start-1, pageSize);
      int totalCount = 0;
      List<RemindEventDTO> repairRemindEventDTOList = new ArrayList<RemindEventDTO>();
      sw.stopAndStart("count");
      if (StringUtils.isNotEmpty(remindType) && StringUtils.isNotBlank(remindType)) {
        if ("lack".equals(remindType)) {
          totalCount = txnService.countRemindEvent(shopId, RepairRemindEventTypes.LACK);
          repairRemindEventDTOList = txnService.queryRepairRemindEvent(shopId, getFlashTime(), RepairRemindEventTypes.LACK, start - 1, pageSize);
        } else if ("incoming".equals(remindType)) {
          totalCount = txnService.countRemindEvent(shopId, RepairRemindEventTypes.INCOMING);
          repairRemindEventDTOList = txnService.queryRepairRemindEvent(shopId, getFlashTime(), RepairRemindEventTypes.INCOMING, start - 1, pageSize);
        } else if ("pending".equals(remindType)) {
          totalCount = txnService.countRemindEvent(shopId, RepairRemindEventTypes.PENDING);
          repairRemindEventDTOList = txnService.queryRepairRemindEvent(shopId, getFlashTime(), RepairRemindEventTypes.PENDING, start - 1, pageSize);
        } else if ("waitOutStorage".equals(remindType)) {
          totalCount = txnService.countRemindEvent(shopId, RepairRemindEventTypes.WAIT_OUT_STORAGE);
          repairRemindEventDTOList = txnService.queryRepairRemindEvent(shopId, getFlashTime(), RepairRemindEventTypes.WAIT_OUT_STORAGE, start - 1, pageSize);
        }

      } else {
        totalCount = txnService.countRemindEvent(shopId, null);
        sw.stopAndStart("list");
        repairRemindEventDTOList = txnService.queryRepairRemindEvent(shopId, getFlashTime(), null, start - 1, pageSize);
        sw.stopAndStart("getMaps");
      }
      if (CollectionUtils.isNotEmpty(repairRemindEventDTOList)) {
        Set<String> licenceNos = new HashSet<String>();
        Set<Long> orderIds = new HashSet<Long>();
        Set<Long> productDetailOrderIds = new HashSet<Long>();

        for (RemindEventDTO remindEventDTO : repairRemindEventDTOList) {
          licenceNos.add(remindEventDTO.getLicenceNo());
          orderIds.add(remindEventDTO.getOrderId());
          if (remindEventDTO.getObjectId() == null && RepairRemindEventTypes.PENDING.equals(RepairRemindEventTypes.valueOf(remindEventDTO.getEventStatus()))) {
            productDetailOrderIds.add(remindEventDTO.getOrderId());
          }
        }

        Map<String, VehicleDTO> vehicleDTOMap = userService.getVehicleMapByLicenceNos(shopId, licenceNos);
        Map<Long, RepairOrderDTO> repairOrderDTOMap = txnService.getRepairOrderMapByShopIdAndOrderIds(shopId, orderIds.toArray(new Long[orderIds.size()]));
        List<RepairOrderItemDTO> repairOrderItemDTOs = new ArrayList<RepairOrderItemDTO>();
        if (CollectionUtils.isNotEmpty(productDetailOrderIds)) {
          repairOrderItemDTOs = txnService.getRepairOrderItemDTOsByShopIdAndArrayOrderId(shopId, productDetailOrderIds.toArray(new Long[productDetailOrderIds.size()]));
        }

        Map<Long, List<RepairOrderItemDTO>> repairOrderItemMap = new HashMap<Long, List<RepairOrderItemDTO>>();
        Map<Long, ProductHistoryDTO> productHistoryMap = new HashMap<Long, ProductHistoryDTO>();
        if (CollectionUtils.isNotEmpty(repairOrderItemDTOs)) {
          Set<Long> productIds = new HashSet<Long>();
          for (RepairOrderItemDTO itemDTO : repairOrderItemDTOs) {
            productIds.add(itemDTO.getProductHistoryId());
          }
          productHistoryMap = ServiceManager.getService(IProductHistoryService.class).getProductHistoryDTOMapByProductHistoryIds(productIds);
          for (RepairOrderItemDTO itemDTO : repairOrderItemDTOs) {
            if (repairOrderItemMap.get(itemDTO.getRepairOrderId()) == null) {
              repairOrderItemMap.put(itemDTO.getRepairOrderId(), new ArrayList<RepairOrderItemDTO>());
            }
            repairOrderItemMap.get(itemDTO.getRepairOrderId()).add(itemDTO);
          }
        }

        sw.stopAndStart("process");
        for (RemindEventDTO repairRemindEventDTO : repairRemindEventDTOList) {
          RepairRemindResponse repairRemindResponse = new RepairRemindResponse();
          repairRemindResponse.setRemindType(RepairRemindEventTypes.valueOf(repairRemindEventDTO.getEventStatus()).getName());
          Long remindTime = repairRemindEventDTO.getRemindTime();
          if (remindTime != null) {
            repairRemindResponse.setEstimateTime(remindTime);
          }
          //组装页面要展示的列
          repairRemindResponse.setRepairOrderId(repairRemindEventDTO.getOrderId());
          repairRemindResponse.setLicenceNo(repairRemindEventDTO.getLicenceNo());
          VehicleDTO vehicleDTO = vehicleDTOMap.get(repairRemindEventDTO.getLicenceNo());
          repairRemindResponse.setModel(vehicleDTO == null ? "" : vehicleDTO.getModel());
          RepairOrderDTO repairOrderDTO = repairOrderDTOMap.get(repairRemindEventDTO.getOrderId());
          //维修单号
          if (repairOrderDTO != null) {
            repairRemindResponse.setReceiptNo(repairOrderDTO.getReceiptNo());
          }
          //商品名称
          if (repairRemindEventDTO.getObjectId() != null) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(repairRemindEventDTO.getObjectId(), shopId);
            if (productDTO != null) {
              repairRemindResponse.setProductIds1(productDTO.getId().toString());
              repairRemindResponse.setProductName(productDTO.getName());
            }
          } else {
            //待交付状态，需要显示全部产品名称，其他状态显示单个产品名称
            if (RepairRemindEventTypes.PENDING.equals(RepairRemindEventTypes.valueOf(repairRemindEventDTO.getEventStatus()))) {
              StringBuffer productNames = new StringBuffer();
              List<RepairOrderItemDTO> repairOrderItemList = repairOrderItemMap.get(repairOrderDTO.getId());
              if (CollectionUtil.isNotEmpty(repairOrderItemList)) {
                for (RepairOrderItemDTO repairOrderItem : repairOrderItemList) {
                  ProductHistoryDTO productHistoryDTO = productHistoryMap.get(repairOrderItem.getProductHistoryId());
                  if (productHistoryDTO != null) {
                    productNames.append(productHistoryDTO.getName()).append("; ");
                  }
                }
              }
              //去尾部分号
              if (productNames.length() > 0) {
                productNames.setLength(productNames.length() - 2);
              }
              repairRemindResponse.setProductName(productNames.toString());
            }
          }
          //客户信息
          repairRemindResponse.setName(repairRemindEventDTO.getCustomerName());         //姓名
          repairRemindResponse.setMobile(repairRemindEventDTO.getMobile());             //联系方式
          //服务内容
//          List<RepairOrderService> repairOrderServiceDTOList = ServiceManager.getService(ITxnService.class).getRepairOrderServicesByRepairOrderId(repairRemindEventDTO.getOrderId());
//          String services = "";
//          if(CollectionUtil.isNotEmpty(repairOrderServiceDTOList)){
//            for(RepairOrderService repairOrderService : repairOrderServiceDTOList){
//              ServiceDTO serviceDTO = ServiceManager.getService(ITxnService.class).getServiceById(repairOrderService.getServiceId());
//              services = services + serviceDTO.getName() + "; ";
//            }
//          }
//          //去尾部分号
//          if(StringUtil.isNotEmpty(services)){
//            services = services.substring(0,services.length()-2);
//          }
//          repairRemindResponse.setContent(services);
          repairRemindResponse.setContent(repairRemindEventDTO.getService());
          repairRemindResponseList.add(repairRemindResponse);
        }
      }
      sw.stopAndPrintLog();
      Pager pager = new Pager(totalCount, start, pageSize);
      result.setResults(repairRemindResponseList);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询维修美容提醒出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=arrearsRemind")
  @ResponseBody
  public PagingListResult arrearsRemind(HttpServletRequest request, Integer startPageNo) {
    StopWatchUtil sw = new StopWatchUtil("arrearsRemindSw");
    IContactService contactService = ServiceManager.getService(IContactService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ISupplierRecordService supplierRecordService = ServiceManager.getService(ISupplierRecordService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    //是否过期
    String isOverdueStr = request.getParameter("isOverdue");
    //是否已提醒
    String hasRemindStr = request.getParameter("hasRemind");
    Boolean isOverdue = null;
    Boolean hasRemind = null;
    if (StringUtil.isNotEmpty(isOverdueStr)) {
      isOverdue = Boolean.parseBoolean(isOverdueStr);
    }
    if (StringUtil.isNotEmpty(hasRemindStr)) {
      hasRemind = Boolean.parseBoolean(hasRemindStr);
    }

    int start = startPageNo == null ? 1 : startPageNo;
    int pageSize = 10;
    PagingListResult<ArrearsRemindResponse> result = new PagingListResult<ArrearsRemindResponse>();

    try {
      List<ArrearsRemindResponse> arrearsRemindResponseList = new ArrayList<ArrearsRemindResponse>();
      RemindEventStrategy debtRemindEventStrategy = remindEventStrategySelector.selectStrategy(RemindEventType.DEBT);
      sw.stopAndStart("count");
      int totalRows = debtRemindEventStrategy.countRemindEvent(shopId, isOverdue, hasRemind, getFlashTime());
      sw.stopAndStart("list");
      List<RemindEventDTO> debtRemindEventList = debtRemindEventStrategy.queryRemindEvent(shopId, isOverdue, hasRemind, getFlashTime(), start - 1, pageSize);
      sw.stopAndStart("process");
      if (CollectionUtils.isNotEmpty(debtRemindEventList)) {
        Set<Long> customerIds = new HashSet<Long>();
        Set<Long> supplierIds = new HashSet<Long>();
        for (RemindEventDTO remindEventDTO : debtRemindEventList) {
          if (remindEventDTO.getCustomerId() != null) {
            customerIds.add(remindEventDTO.getCustomerId());
          }
          if (remindEventDTO.getSupplierId() != null) {
            supplierIds.add(remindEventDTO.getSupplierId());
          }
        }

        Map<Long, SupplierDTO> supplierDTOMap = supplierService.getSupplierByIdSet(shopId, supplierIds);
        Map<Long, SupplierRecordDTO> supplierRecordDTOMap = supplierRecordService.getSupplierRecordDTOMapBySupplierId(shopId, new ArrayList<Long>(supplierIds));
        Map<Long, CustomerDTO> customerMap = customerService.getCustomerByIdSet(shopId, customerIds);
        Map<Long, CustomerRecordDTO> customerRecordMap = customerService.getCustomerRecordMap(shopId, customerIds.toArray(new Long[customerIds.size()]));
        Map<Long, List<ContactDTO>> supplierContactDTOsMap = contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(supplierIds), "supplier");
        Map<Long, List<ContactDTO>> customerContactDTOsMap = contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerIds), "customer");

        for (RemindEventDTO remindEventDTO : debtRemindEventList) {
          Long customerId = remindEventDTO.getCustomerId();
          Long supplierId = remindEventDTO.getSupplierId();
          String remindStatus = remindEventDTO.getRemindStatus();
          String remindStatusStr = "";
          if (UserConstant.Status.ACTIVITY.equals(remindStatus)) {
            remindStatusStr = "未提醒";
          }
          if (UserConstant.Status.REMINDED.equals(remindStatus)) {
            remindStatusStr = "已提醒";
          }
          String remindTimeStr = "";
          if (remindEventDTO.getRemindTime() != null) {
            remindTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, remindEventDTO.getRemindTime());
          }
          Double totalArrears = 0.0;
          CustomerDTO customerDTO = null;
          SupplierDTO supplierDTO = null;
          if (customerId != null) {
            //欠款金额从CustomerRecord中重新获取
            CustomerRecordDTO customerRecordDTO = customerRecordMap.get(customerId);
            customerDTO = customerMap.get(customerId);
            double supplierReceivable = 0d;
            if (customerDTO != null && customerDTO.getSupplierId() != null) {
              SupplierRecordDTO supplierRecordDTO = ServiceManager.getService(ISupplierRecordService.class).getSupplierRecordDTOBySupplierId(shopId, customerDTO.getSupplierId());
              if (supplierRecordDTO != null) {
                supplierReceivable = supplierRecordDTO.getDebt();
              }
            }
            double customerReceivable = customerRecordDTO == null ? 0 : customerRecordDTO.getTotalReceivable();
            totalArrears = customerReceivable + supplierReceivable;
          }
          if (supplierId != null) {
            SupplierRecordDTO supplierRecordDTO = supplierRecordDTOMap.get(supplierId);
            supplierDTO = supplierDTOMap.get(supplierId);
            totalArrears = supplierRecordDTO == null ? 0 : supplierRecordDTO.getDebt();

          }
          if (totalArrears == 0) {
            continue;
          }
          ArrearsRemindResponse arrearsRemindResponse = new ArrearsRemindResponse();
          arrearsRemindResponse.setCustomerId(customerId);
          arrearsRemindResponse.setSupplierId(supplierId);
          arrearsRemindResponse.setTotalArrears(NumberUtil.doubleVal(totalArrears));
          arrearsRemindResponse.setRemindStatus(remindStatusStr);
          if (remindEventDTO.getRemindTime() != null) {
            arrearsRemindResponse.setRepayDate(remindEventDTO.getRemindTime());
          }
          arrearsRemindResponse.setRepayDateStr(remindTimeStr);
          arrearsRemindResponse.setDebtId(remindEventDTO.getId());
          if (customerDTO != null) {
            arrearsRemindResponse.setClientName(customerDTO.getName());
            ContactDTO contactDTO = SmsHelper.getFirstHasMobileContactDTO(customerContactDTOsMap.get(customerDTO.getId()));
            if (contactDTO != null) {
              arrearsRemindResponse.setMobile(contactDTO.getMobile());
              arrearsRemindResponse.setContact(contactDTO.getName());
              arrearsRemindResponse.setContactId(contactDTO.getId());
            }
          }
          if (supplierDTO != null) {
            arrearsRemindResponse.setClientName(supplierDTO.getName());
            ContactDTO contactDTO = SmsHelper.getFirstHasMobileContactDTO(supplierContactDTOsMap.get(supplierDTO.getId()));
            if (contactDTO != null) {
              arrearsRemindResponse.setMobile(contactDTO.getMobile());
              arrearsRemindResponse.setContact(contactDTO.getName());
              arrearsRemindResponse.setContactId(contactDTO.getId());
            }
          }
          arrearsRemindResponseList.add(arrearsRemindResponse);
        }
      }
      Pager pager = new Pager(totalRows, start, pageSize);
      result.setResults(arrearsRemindResponseList);
      result.setPager(pager);
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.error("查询客户欠款提醒出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  //ajax 进销存类分页
  @RequestMapping(params = "method=invoicing")
  @ResponseBody
  public PagingListResult invoicing(HttpServletRequest request, Integer startPageNo) throws Exception {
    StopWatchUtil sw = new StopWatchUtil("remnd_invoicing");
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int start = startPageNo == null ? 1 : startPageNo;
    int pageSize = 10;

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);

    PagingListResult<InventoryRemindResponse> result = new PagingListResult<InventoryRemindResponse>();
    List<InventoryRemindResponse> inventoryRemindResponseList = new ArrayList<InventoryRemindResponse>();

    try {
      //待办事项选择器
      RemindEventStrategy txnRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.TXN);
      sw.stopAndStart("count");
      int totalCount = txnRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
      sw.stopAndStart("list");
      List<RemindEventDTO> txnRemindEventDTOList = txnRemindEventStrategy.queryRemindEvent(shopId, null, null, getFlashTime(), start - 1, pageSize);
      sw.stopAndStart("process");
      if (CollectionUtils.isNotEmpty(txnRemindEventDTOList)) {
        Set<Long> orderIds = new HashSet<Long>();
        for (RemindEventDTO remindEventDTO : txnRemindEventDTOList) {
          orderIds.add(remindEventDTO.getOrderId());
        }
        List<PurchaseOrderDTO> purchaseOrderDTOs = txnService.getPurchaseOrdersWithItemAndProductByOrderIds(shopId, orderIds.toArray(new Long[orderIds.size()]));
        Map<Long, PurchaseOrderDTO> purchaseOrderDTOMap = new HashMap<Long, PurchaseOrderDTO>();
        for (PurchaseOrderDTO orderDTO : purchaseOrderDTOs) {
          purchaseOrderDTOMap.put(orderDTO.getId(), orderDTO);
        }
        for (RemindEventDTO remindEventDTO : txnRemindEventDTOList) {
          InventoryRemindResponse inventoryRemindResponse = new InventoryRemindResponse();
          inventoryRemindResponse.setRemindType("待入库");
          Long deliveryDate = remindEventDTO.getRemindTime();
          if (deliveryDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Date date = new Date(deliveryDate);
            String deliveryDateStr = sdf.format(date);
            inventoryRemindResponse.setEstimateTimeStr(deliveryDateStr);      //预计时间
          }
          //获取采购单明细
          PurchaseOrderDTO purchaseOrderDTO = purchaseOrderDTOMap.get(remindEventDTO.getOrderId());
          inventoryRemindResponse.setPurchaseOrderId(remindEventDTO.getOrderId());
          inventoryRemindResponse.setReceiptNo(purchaseOrderDTO.getReceiptNo());
          inventoryRemindResponse.setTotalPrice(purchaseOrderDTO.getTotal());
          inventoryRemindResponse.setSupplierId(remindEventDTO.getSupplierId());
          inventoryRemindResponse.setSupplier(remindEventDTO.getSupplierName());
          inventoryRemindResponse.setProductName(purchaseOrderDTO.getAllProductNames(", "));
          inventoryRemindResponse.setNumber(purchaseOrderDTO.getItemDTOs() == null ? 0 : purchaseOrderDTO.getItemDTOs().length);
          inventoryRemindResponseList.add(inventoryRemindResponse);
        }
      }
      Pager pager = new Pager(totalCount, start, pageSize);
      result.setResults(inventoryRemindResponseList);
      result.setPager(pager);
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.error("查询进销存提醒出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }


  @RequestMapping(params = "method=customerRemind")
  @ResponseBody
  public PagingListResult customerRemind(HttpServletRequest request, Integer startPageNo) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    //是否过期
    String isOverdueStr = request.getParameter("isOverdue");
    //是否已提醒
    String hasRemindStr = request.getParameter("hasRemind");
    Boolean isOverdue = null;
    Boolean hasRemind = null;
    if (StringUtil.isNotEmpty(isOverdueStr)) {
      isOverdue = Boolean.parseBoolean(isOverdueStr);
    }
    if (StringUtil.isNotEmpty(hasRemindStr)) {
      hasRemind = Boolean.parseBoolean(hasRemindStr);
    }

    int start = startPageNo == null ? 1 : startPageNo;
    int pageSize = 10;

    PagingListResult<CustomerServiceJobDTO> result = new PagingListResult<CustomerServiceJobDTO>();
    List<CustomerServiceJobDTO> customerServiceJobDTOList = new ArrayList<CustomerServiceJobDTO>();

    try {
//      customerServiceJobDTOList = userService.getCustomerServiceRemindByCondition(shopId, isOverdue, hasRemind, getFlashTime(), start - 1, max);
      //待办事项选择器
      RemindEventStrategy customerRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.CUSTOMER_SERVICE);
      int totalCount = customerRemindEventStrategy.countRemindEvent(shopId, isOverdue, hasRemind, getFlashTime());
      List<RemindEventDTO> txnRemindEventDTOList = customerRemindEventStrategy.queryRemindEvent(shopId, isOverdue, hasRemind, getFlashTime(), start - 1, pageSize);
      //会员卡服务需要重新赋值
      if (CollectionUtils.isNotEmpty(txnRemindEventDTOList)) {
        ICustomerRemindService customerRemindService = ServiceManager.getService(ICustomerRemindService.class);
        customerServiceJobDTOList = customerRemindService.generateCustomerServiceJob(shopId, txnRemindEventDTOList);
      }
      Pager pager = new Pager(totalCount, start, pageSize);
      result.setResults(customerServiceJobDTOList);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询客户服务提醒出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=createPlan")
  public void createPlan(HttpServletRequest request, String plans, HttpServletResponse response) throws Exception {
    PrintWriter writer = response.getWriter();
    LOG.info(plans);
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ShopPlanDTO shopPlanDTO = new Gson().fromJson(plans, new TypeToken<ShopPlanDTO>() {
    }.getType());

    shopPlanDTO.setShopId(shopId);
    shopPlanDTO.setStatus(PlansRemindStatus.activity);
    shopPlanDTO.setRemindTime(shopPlanDTO.getRemindTimeStr());
    String jsonStr = "";
    try {

      shopPlanService.savePlan(shopPlanDTO);

    } catch (Exception e) {
      LOG.debug("/remind.do");
      LOG.debug("method=createPlan");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    } finally {
      if (null != shopPlanDTO.getId()) {
        jsonStr = "success";
      } else {
        jsonStr = "error";
      }

      Map<String, String> map = new HashMap<String, String>();
      map.put("resu", jsonStr);
      map.put("id", shopPlanDTO.getId().toString());
      writer.write(JsonUtil.mapToJson(map));
      writer.close();
    }
  }

  @RequestMapping(params = "method=getPlans")
  @ResponseBody
  public List getPlans(ModelMap model, HttpServletRequest request, HttpServletResponse response, Integer startPageNo, String tableStatus) throws Exception {
    IShopPlanService iShopPlanService = ServiceManager.getService(IShopPlanService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int maxRows = 10;
    String jsonStr = "";
    if (StringUtils.isBlank(tableStatus)) {
      tableStatus = "totalRows";
    }
    Long now = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, DateUtil.convertDateLongToDateString(
      DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));

    List shopPlanDTOList = iShopPlanService.getPlans(shopId, startPageNo - 1, maxRows, now, tableStatus);

    int totalRows = iShopPlanService.countPlans(shopId, PlansRemindStatus.getActivityAndReminded(), DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()), tableStatus);

    Pager pager = new Pager(totalRows, NumberUtil.intValue(String.valueOf(startPageNo), 1));

    shopPlanDTOList.add(pager);
    return shopPlanDTOList;
  }

  @RequestMapping(params = "method=addCustomer")
  public String addCustomer(HttpServletRequest request) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int customerNumber = (int) userService.countShopCustomerRecord(shopId);
    request.setAttribute("customerNumber", customerNumber);
    return "remind/addCustomer";
  }

  @RequestMapping(params = "method=getCustomers")
  public void getCustomers(HttpServletRequest request, HttpServletResponse response, Integer startPageNo) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    String jsonStr = "";
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int customerNumber = (int) userService.countShopCustomerRecord(shopId);
    int pageNo = startPageNo == null ? 1 : startPageNo;
    int maxPageSize = 10;
    int pageCount = customerNumber % maxPageSize == 0 ? customerNumber / maxPageSize : customerNumber / maxPageSize + 1;
    List<CustomerRecordDTO> customerRecordDTOList = userService.getSmsCustomerInfoList(shopId, pageNo, maxPageSize);
    jsonStr = JsonUtil.listToJson(customerRecordDTOList);
    Pager pager = new Pager(customerNumber, pageNo, maxPageSize);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();
  }

  @RequestMapping(params = "method=dropPlan")
  public void dropPlan(HttpServletRequest request, HttpServletResponse response, String idStr) throws Exception {
    IShopPlanService iShopPlanService = ServiceManager.getService(IShopPlanService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (!"".equals(idStr)) {
      Long id = Long.parseLong(idStr);
      iShopPlanService.dropPlan(shopId, id);
    }
    PrintWriter writer = response.getWriter();
    writer.write("succ");
    writer.close();
  }

  @RequestMapping(params = "method=dropCustomerRemind")
  public void dropCustomerRemind(HttpServletRequest request, HttpServletResponse response, String idStr) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    if (!"".equals(idStr)) {
      Long id = Long.parseLong(idStr);
      userService.dropCustomerRemind(shopId, id);
    }
    PrintWriter writer = response.getWriter();
    writer.write("succ");
    writer.close();
  }


  //  public List<ShopPlanDTO> initPlans(Long shopId, String plans) throws Exception {
//    String[] plans1 = plans.split(";");
//    List<ShopPlanDTO> shopPlanDTOList = new ArrayList<ShopPlanDTO>();
//    for (int i = 0; i < plans1.length; i++) {
//      String[] childPlans = plans1[i].split("<");
//      ShopPlanDTO shopPlanDTO = new ShopPlanDTO();
//      shopPlanDTO.setShopId(shopId);
//      shopPlanDTO.setStatus(UserConstant.Status.ACTIVITY);
//      shopPlanDTO.setRemindType(childPlans[0]);
//      shopPlanDTO.setContent(childPlans[1]);
//      shopPlanDTO.setCustomerNames(childPlans[2]);
//      shopPlanDTO.setRemindTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, childPlans[3]));
//      shopPlanDTO.setCustomerIds(childPlans[4]);
//      shopPlanDTO.setCustomerType(childPlans[5]);
//      shopPlanDTOList.add(shopPlanDTO);
//    }
//    return shopPlanDTOList;
//  }
  @RequestMapping(params = "method=toPlansRemind")
  public String toPlansRemind(HttpServletRequest request, HttpServletResponse response) throws Exception {
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);

    Long shopId = (Long) request.getSession().getAttribute("shopId");

    Long now = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, DateUtil.convertDateLongToDateString(
      DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));

    int activity = shopPlanService.countPlansByStatus(shopId, PlansRemindStatus.activity);
    int reminded = shopPlanService.countPlansByStatus(shopId, PlansRemindStatus.reminded);
    int activityExpired = shopPlanService.countActivityPlansExpired(shopId, now);
    int totalRows = activity + reminded;
    int activityNoExpired = activity - activityExpired;
    request.setAttribute("totalRows", totalRows);
    request.setAttribute("activity", activity);
    request.setAttribute("reminded", reminded);
    request.setAttribute("activityExpired", activityExpired);
    request.setAttribute("activityNoExpired", activityNoExpired);

    return "remind/plansRemind";
  }


  @RequestMapping(params = "method=updatePlan")
  public void updatePlan(HttpServletRequest request, String plans, HttpServletResponse response) throws Exception {
    PrintWriter writer = response.getWriter();
    LOG.info(plans);
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ShopPlanDTO shopPlanDTO = new Gson().fromJson(plans, new TypeToken<ShopPlanDTO>() {
    }.getType());

    shopPlanDTO.setShopId(shopId);
    shopPlanDTO.setStatus(PlansRemindStatus.activity);
    shopPlanDTO.setRemindTime(shopPlanDTO.getRemindTimeStr());
    String jsonStr = "";
    try {

      shopPlanService.updatePlan(shopPlanDTO);

      jsonStr = "success";
    } catch (Exception e) {
      LOG.debug("/remind.do");
      LOG.debug("method=updatePlan");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      jsonStr = "error";
    } finally {

      Map<String, String> map = new HashMap<String, String>();
      map.put("resu", jsonStr);
      map.put("id", shopPlanDTO.getId().toString());
      writer.write(JsonUtil.mapToJson(map));
      writer.close();
    }
  }

  @RequestMapping(params = "method=getRowInfo")
  public void getRowInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);

    Long shopId = (Long) request.getSession().getAttribute("shopId");

    Long now = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, DateUtil.convertDateLongToDateString(
      DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis()));

    int activity = 0;
    int reminded = 0;
    int activityExpired = 0;
    int totalRows = 0;
    int activityNoExpired = 0;

    try {
      activity = shopPlanService.countPlansByStatus(shopId, PlansRemindStatus.activity);
      reminded = shopPlanService.countPlansByStatus(shopId, PlansRemindStatus.reminded);
      activityExpired = shopPlanService.countActivityPlansExpired(shopId, now);
      totalRows = activity + reminded;
      activityNoExpired = activity - activityExpired;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("method=getRowInfo");
      LOG.error("shopId{}", shopId);
    } finally {
      Map<String, String> map = new HashMap<String, String>();
      map.put("totalRows", String.valueOf(totalRows));
      map.put("reminded", String.valueOf(reminded));
      map.put("activityExpired", String.valueOf(activityExpired));
      map.put("activityNoExpired", String.valueOf(activityNoExpired));
      out.write(JsonUtil.mapToJson(map));
      out.close();
    }
  }

  @RequestMapping(params = "method=selectMan")
  public String selectMan(HttpServletRequest request, HttpServletResponse response, int personNum) throws Exception {
    ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);

    CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO = new CustomerSupplierSearchConditionDTO();

    customerSupplierSearchConditionDTO.setCustomerOrSupplier("customer");
    customerSupplierSearchConditionDTO.setShopId(WebUtil.getShopId(request));
    CustomerSupplierSearchResultListDTO customerSearchResultListDTO = searchCustomerSupplierService
      .queryCustomerWithUnknownField(customerSupplierSearchConditionDTO);

    customerSupplierSearchConditionDTO.setCustomerOrSupplier("supplier");


    CustomerSupplierSearchResultListDTO supplierSearchResultListDTO = searchCustomerSupplierService
      .querySupplierWithUnknownField(customerSupplierSearchConditionDTO);

    long memberNum = customerSearchResultListDTO.getMemberNumFound();

    long customerNum = customerSearchResultListDTO.getNumFound();

    long supplierNum = supplierSearchResultListDTO.getNumFound();

    long total = customerNum + supplierNum;

    long mobileNum = customerSearchResultListDTO.getHasMobileNumFound() + supplierSearchResultListDTO.getHasMobileNumFound();

    request.setAttribute("total", total);
    request.setAttribute("memberNum", memberNum);
    request.setAttribute("customerNum", customerNum);
    request.setAttribute("supplierNum", supplierNum);
    request.setAttribute("personNum", personNum);
    request.setAttribute("mobileNum", mobileNum);

    return "remind/selectMan";
  }

  /**
   * 获取 短信管理 选择联系人组 所需数据
   *
   * @param request
   * @param response
   * @throws Exception
   */
  @RequestMapping(params = "method=getCustomerAndSupplierList")
  public void getCustomerAndSupplierList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);

    CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO = new CustomerSupplierSearchConditionDTO();

    customerSupplierSearchConditionDTO.setCustomerOrSupplier("customer");
    customerSupplierSearchConditionDTO.setShopId(WebUtil.getShopId(request));
    CustomerSupplierSearchResultListDTO customerSearchResultListDTO = searchCustomerSupplierService
      .queryCustomerWithUnknownField(customerSupplierSearchConditionDTO);

    customerSupplierSearchConditionDTO.setCustomerOrSupplier("supplier");


    CustomerSupplierSearchResultListDTO supplierSearchResultListDTO = searchCustomerSupplierService
      .querySupplierWithUnknownField(customerSupplierSearchConditionDTO);

    long memberNum = customerSearchResultListDTO.getMemberNumFound();

    long customerNum = customerSearchResultListDTO.getNumFound();

    long supplierNum = supplierSearchResultListDTO.getNumFound();

    long total = customerNum + supplierNum;

    long mobileNum = customerSearchResultListDTO.getHasMobileNumFound() + supplierSearchResultListDTO.getHasMobileNumFound();

    Map<String, Long> map = new HashMap<String, Long>();
    map.put("total", total);
    map.put("customerNum", customerNum);
    map.put("supplierNum", supplierNum);
    map.put("mobileNum", mobileNum);
    map.put("memberNum", memberNum);

    out.write(JsonUtil.mapToJson(map));
    out.close();
  }

  @RequestMapping(params = "method=getCustomerAndSupplier")
  public void getCustomerAndSupplier(HttpServletRequest request, HttpServletResponse response, String keyWords, Integer startPageNo) throws Exception {
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    Long shopId = WebUtil.getShopId(request);
    ISearchCustomerSupplierService searchCustomerSupplierService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    int maxSize = 5;
    CustomerSupplierSearchConditionDTO customerSupplierSearchConditionDTO = new CustomerSupplierSearchConditionDTO();
    if (null == startPageNo || 0 == startPageNo) {
      startPageNo = 1;
    }
    customerSupplierSearchConditionDTO.setShopId(shopId);
//    customerSupplierSearchConditionDTO.setCustomerOrSupplier("customer");

    customerSupplierSearchConditionDTO.setSearchWord(keyWords);
    customerSupplierSearchConditionDTO.setStart((startPageNo - 1) * maxSize);
    customerSupplierSearchConditionDTO.setRows(maxSize);
    try {
      CustomerSupplierSearchResultListDTO customerSupplierSearchResultListDTO = searchCustomerSupplierService.queryCustomerSupplierWithUnknownField(customerSupplierSearchConditionDTO);

      jsonStr = JsonUtil.listToJson(customerSupplierSearchResultListDTO.getCustomerSuppliers());
      Pager pager = new Pager(Long.valueOf(customerSupplierSearchResultListDTO.getNumFound()).intValue(), startPageNo.intValue(), maxSize);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
      if (!"[".equals(jsonStr.trim())) {
        jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
      } else {
        jsonStr = pager.toJson();
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      out.write(jsonStr);
      out.close();
    }

  }

  @RequestMapping(params = "method=sendMsg")
  public String sendMsg(HttpServletRequest request, HttpServletResponse response, Long shopPlanId) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
    ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    //手机号去重
    Map<String, String> map = new HashMap<String, String>();
    try {
      CustomerSupplierSearchConditionDTO searchConditionDTO = new CustomerSupplierSearchConditionDTO();
      searchConditionDTO.setShopId(WebUtil.getShopId(request));
      CustomerSupplierSearchResultListDTO searchResultListDTO = null;
      ShopPlanDTO shopPlanDTO = shopPlanService.getPlanDTO(shopId, shopPlanId);
      String mobiles = shopPlanDTO.getContact();
      if (StringUtils.isNotBlank(mobiles) || (-1 != mobiles.indexOf(TxnConstant.ALL_PERSON) || -1 != mobiles.indexOf(TxnConstant.ALL_CUSTOMER) ||
        -1 != mobiles.indexOf(TxnConstant.ALL_SUPPLIER) || -1 != mobiles.indexOf(TxnConstant.ALL_MEMBER) || -1 != mobiles.indexOf(TxnConstant.ALL_PHONE_CONTACTS))) {
        String[] mobileArr = mobiles.split(",");

        for (int i = 0; i < mobileArr.length; i++) {
          String mobile = "";
          if (TxnConstant.ALL_PERSON.equals(mobileArr[i]) || TxnConstant.ALL_PHONE_CONTACTS.equals(mobileArr[i])) {
            //solr中获取有手机的全体用户遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);

            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else if (TxnConstant.ALL_CUSTOMER.equals(mobileArr[i])) {
            searchConditionDTO.setCustomerOrSupplier("customer");
            //solr中获取有手机的全体k客户遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);

            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else if (TxnConstant.ALL_SUPPLIER.equals(mobileArr[i])) {
            searchConditionDTO.setCustomerOrSupplier("supplier");
            //solr中获取有手机的全体供应商遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);

            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else if (TxnConstant.ALL_MEMBER.endsWith(mobileArr[i])) {
            List<String> memberCardTypes = ServiceManager.getService(IMembersService.class).getMemberCardTypeByShopId(searchConditionDTO.getShopId());
            searchConditionDTO.setMemberType(CollectionUtil.collectionToCommaString(memberCardTypes));
            //solr中获取有手机的全体会员遍历获取手机号，正确的放入map中
            searchResultListDTO = searchService.queryCustomerMobiles(searchConditionDTO);

            putMobileFromCustomerOrSupplierOrMember(searchResultListDTO, map);
          } else {
            String newMobile = mobileArr[i].trim();
            if (StringUtils.isNotBlank(newMobile) && newMobile.length() == 11 && newMobile.substring(0, 1).equals("1") && isNumeric(newMobile)) {
              map.put(newMobile, newMobile);
            }
          }
        }

        Set sets = map.keySet();
        if (CollectionUtils.isNotEmpty(sets)) {
          shopPlanDTO.setContact(sets.toString().substring(1, sets.toString().length() - 1));
        } else {
          shopPlanDTO.setContact("");
        }
      }
      String content = shopPlanDTO.getContent();
      content = content.trim();
      if (StringUtils.isBlank(content) || 160 == content.charAt(0)) {
        shopPlanDTO.setContent(shopPlanDTO.getRemindType());
      }
      request.setAttribute("shopPlanId", shopPlanDTO.getId().toString());
      request.setAttribute("sendMobile", shopPlanDTO.getContact());
      request.setAttribute("smsContent", shopPlanDTO.getContent());

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return "sms/smswrite2";
  }

  public void putMobileFromCustomerOrSupplierOrMember(CustomerSupplierSearchResultListDTO searchResultListDTO, Map<String, String> map) {
    if (null == searchResultListDTO || CollectionUtils.isEmpty(searchResultListDTO.getCustomerSuppliers())) {
      return;
    }

    for (CustomerSupplierSearchResultDTO searchResultDTO : searchResultListDTO.getCustomerSuppliers()) {
      if (CollectionUtils.isNotEmpty(searchResultDTO.getContactDTOList())) {
        for (ContactDTO contactDTO : searchResultDTO.getContactDTOList()) {
          String mobile = contactDTO.getMobile().trim();
          if (StringUtils.isNotBlank(mobile) && mobile.length() == 11 && mobile.substring(0, 1).equals("1") && isNumeric(mobile)) {
            map.put(mobile, mobile);
          }
        }
      }
    }
  }

  public boolean isNumeric(String str) {
    if (str.matches("\\d*")) {
      return true;
    } else {
      return false;
    }
  }

  public void setRemindEventStrategySelector(RemindEventStrategySelector remindEventStrategySelector) {
    this.remindEventStrategySelector = remindEventStrategySelector;
  }

  //施工单的缺料提醒
  @RequestMapping(params = "method=lackStorageRemind")
  @ResponseBody
  public PagingListResult lackStorageRemind(HttpServletRequest request, Integer startPageNo) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    int start = startPageNo == null ? 1 : startPageNo;
    int pageSize = 10;

    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    PagingListResult<RepairRemindResponse> result = new PagingListResult<RepairRemindResponse>();
    List<RepairRemindResponse> repairRemindResponseList = new ArrayList<RepairRemindResponse>();

    try {
      //待办事项选择器
      int totalCount = txnService.countLackStorageRemind(shopId);
      List<RemindEventDTO> repairRemindEventDTOList = txnService.getLackStorageRemind(shopId, start - 1, pageSize);

      if (CollectionUtils.isNotEmpty(repairRemindEventDTOList)) {
        for (RemindEventDTO repairRemindEventDTO : repairRemindEventDTOList) {
          RepairRemindResponse repairRemindResponse = new RepairRemindResponse();
          repairRemindResponse.setRemindType(RepairRemindEventTypes.valueOf(repairRemindEventDTO.getEventStatus()).getName());
          Long remindTime = repairRemindEventDTO.getRemindTime();
          if (remindTime != null) {
            repairRemindResponse.setEstimateTime(remindTime);
          }
          //组装页面要展示的列
          repairRemindResponse.setRepairOrderId(repairRemindEventDTO.getOrderId());
          repairRemindResponse.setLicenceNo(repairRemindEventDTO.getLicenceNo());
          RepairOrderDTO repairOrderDTO = rfiTxnService.getRepairOrderDTOById(repairRemindEventDTO.getOrderId(), shopId);
          //维修单号
          if (repairOrderDTO != null) {
            repairRemindResponse.setReceiptNo(repairOrderDTO.getReceiptNo());
          }
          //商品名称
          if (repairRemindEventDTO.getObjectId() != null) {
            ProductDTO productDTO = productService.getProductByProductLocalInfoId(repairRemindEventDTO.getObjectId(), shopId);
            if (productDTO != null) {
              repairRemindResponse.setProductIds1(productDTO.getId().toString());
              repairRemindResponse.setProductName(productDTO.getName());
            }
          }
          //客户信息
          repairRemindResponse.setName(repairRemindEventDTO.getCustomerName());         //姓名
          repairRemindResponse.setMobile(repairRemindEventDTO.getMobile());             //联系方式
          //服务内容
          List<RepairOrderService> repairOrderServiceDTOList = ServiceManager.getService(ITxnService.class).getRepairOrderServicesByRepairOrderId(repairRemindEventDTO.getOrderId());
          StringBuffer services = new StringBuffer();
          if (CollectionUtil.isNotEmpty(repairOrderServiceDTOList)) {
            for (RepairOrderService repairOrderService : repairOrderServiceDTOList) {
              services.append(repairOrderService.getBusinessCategoryName()).append("; ");
            }
          }
          repairRemindResponse.setContent(services.toString());
          repairRemindResponseList.add(repairRemindResponse);
        }
      }
      Pager pager = new Pager(totalCount, start, pageSize);
      result.setResults(repairRemindResponseList);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询维修美容提醒出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getTitlePromptNums")
  @ResponseBody
  public Result getTitlePromptNums(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    Long userGroupId = WebUtil.getUserGroupId(request);
    ShopVersionDTO shopVersionDTO = WebUtil.getShopVersion(request);
    Result result = new Result(true);
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    try {
      //待办单据提醒总数
      int todoOrderAmount = 0;
      //待办销售单
      int todoSalesOrdersAmount = 0;
      //待办销售退货单
      int todoSalesReturnOrdersAmount = 0;
      //待办采购单
      int todoPurchaseOrdersAmount = 0;
      //待办入库退货单
      int todoPurchaseReturnOrdersAmount = 0;

      //待办事项提醒总数
      int todoRemindAmount = 0;
      //维修美容
      int todoRepairRemindAmount = 0;
      //碰撞视频类
      int todoImpactRemindAmount = 0;
      //欠款提醒
      int todoArrearRemindAmount = 0;
      //进销存
      int todoTxnRemindAmount = 0;
      //客户服务
      int todoCustomerServiceRemindAmount = 0;

      //进销存导航提醒
      int todoTxnRemindAmountNavi = 0;
      //客户管理导航提醒
      int todoCustomerAmountNavi = 0;

      if (shopId == null && shopVersionDTO == null && userGroupId == null) {
        return new Result(false);
      }
      //四种待办单据的render权限
      boolean render_sale = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_ORDERS.SALE");
      boolean render_saleReturn = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_ORDERS.SALE_RETURN");
      boolean render_purchase = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE");
      boolean render_purchaseReturn = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_ORDERS.PURCHASE_RETURN");
      //四种待办事项的render权限
      boolean render_repair = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_TODO.VEHICLE_CONSTRUCTION_BEAUTY");
      boolean render_debt = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_TODO.ARREARS");
      boolean render_txn = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_TODO.TXN");
      boolean render_customerService = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_TODO.CUSTOMER_SERVICE");
//      boolean render_impact = PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "WEB.SCHEDULE.REMIND_TODO.VEHICLE_IMPACT");    //VEHICLE_IMPACT

      //今天0点时刻前一豪秒，用于判断提醒是否过期
      Long startTime = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) - 1;

      //查出本店全部关联客户的idList
      List<Long> relatedCustomerIdList = ServiceManager.getService(ICustomerService.class).getRelatedCustomerIdListByShopId(shopId);
      //查出本店全部关联供应商的idList
      List<Long> relatedSupplierIdList = ServiceManager.getService(IUserService.class).getRelatedSupplierIdListByShopId(shopId);

      //待办单据的冒泡数量
      if (render_sale) {
        todoSalesOrdersAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.TODO_SALE_ORDER, relatedCustomerIdList, null);
      }
      if (render_saleReturn) {
        todoSalesReturnOrdersAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.TODO_SALE_RETURN_ORDER, relatedCustomerIdList, null);
      }
      if (render_purchase) {
        todoPurchaseOrdersAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.TODO_PURCHASE_ORDER, relatedSupplierIdList, null);
      }
      if (render_purchaseReturn) {
        todoPurchaseReturnOrdersAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.TODO_PURCHASE_RETURN_ORDER, relatedSupplierIdList, null);
      }
      todoOrderAmount = todoPurchaseOrdersAmount + todoPurchaseReturnOrdersAmount + todoSalesOrdersAmount + todoSalesReturnOrdersAmount;

      //待办事项的冒泡数量
      if (render_repair) {
        todoRepairRemindAmount = ServiceManager.getService(ITxnService.class).countRemindEvent(shopId, null);
      }
//      if (render_impact) {
//        todoImpactRemindAmount = ServiceManager.getService(ITxnService.class).countRemindEvent(shopId, null);
//      }
      if (render_debt) {
        RemindEventStrategy debtRemindEventStrategy = remindEventStrategySelector.selectStrategy(RemindEventType.DEBT);
        todoArrearRemindAmount = debtRemindEventStrategy.countRemindEvent(shopId, null, null, getFlashTime());
      }
      if (render_txn) {
        todoTxnRemindAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.TXN, null, startTime);
      }
      if (render_customerService) {
        todoCustomerServiceRemindAmount = ServiceManager.getService(ITxnService.class).getRemindEventAmountByType(shopId, RemindEventType.CUSTOMER_SERVICE, null, startTime);
      }
      todoTxnRemindAmountNavi = todoTxnRemindAmount;
      todoCustomerAmountNavi = todoArrearRemindAmount + todoCustomerServiceRemindAmount;
      todoRemindAmount = todoRepairRemindAmount + todoArrearRemindAmount + todoTxnRemindAmount + todoCustomerServiceRemindAmount;

      TitlePromptDTO titlePromptDTO = new TitlePromptDTO(todoOrderAmount, todoSalesOrdersAmount, todoSalesReturnOrdersAmount, todoPurchaseOrdersAmount,
        todoPurchaseReturnOrdersAmount, todoRemindAmount, todoRepairRemindAmount, todoArrearRemindAmount, todoTxnRemindAmount, todoCustomerServiceRemindAmount,
        todoTxnRemindAmountNavi, todoCustomerAmountNavi, todoImpactRemindAmount);
      result.setData(titlePromptDTO);
      return result;
    } catch (Exception e) {
      LOG.error("remind.do?method=getTitlePromptNums erro", e);
      return new Result(false);
    }
  }

  @RequestMapping(params = "method=getRemindNaviTitlePromptNums")
  @ResponseBody
  public Result getRemindNaviTitlePromptNums(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result(true);
    IShopPlanService shopPlanService = ServiceManager.getService(IShopPlanService.class);
    IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
    try {
      if (shopId == null) {
        return new Result(false);
      }
      Map<String, Integer> resultMap = new HashMap<String, Integer>();
      //待办事项提醒总数 代办事项总数 在 “method=getTitlePromptNums”里已经获取了，页面渲染就好了，不重复获取了
      int todoRemindAmount = 0;
      //自定义提醒提醒总数 全部未提醒数量
      List<PlansRemindStatus> plansRemindStatuses = new ArrayList<PlansRemindStatus>();
      plansRemindStatuses.add(PlansRemindStatus.activity);
      int todoPlansRemindAmount = shopPlanService.countPlans(shopId, plansRemindStatuses, null, TxnConstant.SHOP_PLAN_TOTALROWS);
      resultMap.put("todoPlansRemindAmount", todoPlansRemindAmount);
      //预约服务提醒总数 全部未处理醒数量
      AppointOrderSearchCondition searchCondition = new AppointOrderSearchCondition();
      searchCondition.setShopId(shopId);
      AppointOrderStatus[] appointOrderStatuses = new AppointOrderStatus[1];
      appointOrderStatuses[0] = AppointOrderStatus.PENDING;
      searchCondition.setAppointOrderStatus(appointOrderStatuses);
      int todoAppointAmount = appointOrderService.countAppointOrderDTOs(searchCondition);
      resultMap.put("todoAppointAmount", todoAppointAmount);
      result.setData(resultMap);
      return result;
    } catch (Exception e) {
      LOG.error("remind.do?method=getRemindNaviTitlePromptNums erro", e);
      return new Result(false);
    }
  }

  @RequestMapping(params = "method=deleteDebtRemind")
  @ResponseBody
  public Result deleteDebtRemind(HttpServletRequest request, HttpServletResponse response) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result(true);
    try {
      if (shopId == null) throw new Exception("shopId is null");
      String customerOrSupplierIdStr = request.getParameter("customerOrSupplierId");
      String identity = request.getParameter("type");
      if (StringUtils.isEmpty(customerOrSupplierIdStr) || StringUtils.isEmpty(identity)) {
        return new Result(false);
      }
      txnService.updateDebtRemindDeletedType(shopId, NumberUtil.longValue(customerOrSupplierIdStr), identity, DeletedType.TRUE);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }
  }

  @RequestMapping(params = "method=deleteTxnRemind")
  @ResponseBody
  public Result deleteTxnRemind(HttpServletRequest request, HttpServletResponse response) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result(true);
    try {
      if (shopId == null) throw new Exception("shopId is null");
      String purchaseOrderIdStr = request.getParameter("orderId");
      if (StringUtils.isEmpty(purchaseOrderIdStr)) {
        return new Result(false);
      }
      txnService.deleteTxnRemind(shopId, NumberUtil.longValue(purchaseOrderIdStr));
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }
  }

  @RequestMapping(params = "method=impactVideo")
  @ResponseBody
  public PagingListResult impactVideo(HttpServletRequest request, ImpactInfoSearchConditionDTO impactInfoSearchConditionDTO) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    impactInfoSearchConditionDTO.setIsUntreated(YesNo.YES);
    int start = impactInfoSearchConditionDTO.getStartPageNo() == 0 ? 1 : impactInfoSearchConditionDTO.getStartPageNo();
    int pageSize = 10;
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    AddressComponent addressComponent = null;
    IImpactService iImpactService = ServiceManager.getService(IImpactService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IGeocodingService iGeocodingService = ServiceManager.getService(IGeocodingService.class);
    PagingListResult<ImpactVideoExpDTO> result = new PagingListResult<ImpactVideoExpDTO>();
    List<ImpactVideoExpDTO> impactVideoExpDTOs = new ArrayList<ImpactVideoExpDTO>();
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    CustomerDTO customerDTO = null;
    AppUserCustomerDTO appUserCustomerDTO =null;
    int size = 0;
    AppVehicleDTO appVehicleDTO = null;
    try {
      impactInfoSearchConditionDTO.setUploadStatus(UploadStatus.SUCCESS);
      size = iImpactService.countGetImpactVideoExpDTOs_page(shopId.toString(),impactInfoSearchConditionDTO);
      if(size>0){
        impactVideoExpDTOs= iImpactService.getImpactVideoExpDTOByAppUserNo_page(shopId.toString(), impactInfoSearchConditionDTO);
        if (CollectionUtil.isNotEmpty(impactVideoExpDTOs)) {
          for (ImpactVideoExpDTO impactVideoExpDTO : impactVideoExpDTOs) {
            impactVideoExpDTO.setImpactIdStr(impactVideoExpDTO.getImpactId().toString());
            impactVideoExpDTO.setImpactVideoIdStr(impactVideoExpDTO.getImpactVideoId().toString());
            appVehicleDTO = CollectionUtil.getFirst(appUserService.getAppVehicleDTOByAppUserNo(impactVideoExpDTO.getAppUserNo()));
            if(appVehicleDTO!=null){
              impactVideoExpDTO.setVehicleNo(appVehicleDTO.getVehicleNo());//碰撞车牌号
              impactVideoExpDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
              impactVideoExpDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
            }
            addressComponent = iGeocodingService.gpsToAddress(impactVideoExpDTO.getLatitude(), impactVideoExpDTO.getLongitude());
            if (addressComponent != null) {
              impactVideoExpDTO.setAddress(addressComponent.getDistrict() + addressComponent.getStreet()); //碰撞地址
            }
            impactVideoExpDTO.setUploadTimeDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, impactVideoExpDTO.getUploadTime()));
            impactVideoExpDTO.setUploadTimeStr(impactVideoExpDTO.getUploadTime().toString());
            impactVideoExpDTO.setUrl(impactService.getImpactVideoUrl(impactVideoExpDTO.getImpactVideoId()));
            appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(impactVideoExpDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              customerDTO = customerService.getCustomerById(appUserCustomerDTO.getCustomerId());
              if(customerDTO!=null){
                impactVideoExpDTO.setCustomerId(customerDTO.getId().toString());
                impactVideoExpDTO.setCustomerName(customerDTO.getName());
                impactVideoExpDTO.setCustomerMobile(customerDTO.getMobile());
              }
            }
          }
        }
      }
      Pager pager = new Pager(size, start, pageSize);
      result.setResults(impactVideoExpDTOs);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询碰撞视频类出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=deleteImpactVideo")
  @ResponseBody
  public Result deleteImpactVideo(HttpServletRequest request, HttpServletResponse response, String impactVideoId) {
    IImpactService impactService = ServiceManager.getService(IImpactService.class);
    try {
      impactService.deleImpactVideoExpDTOByAppUserNo(impactVideoId);
      return new Result(true);
    } catch (Exception e) {
      LOG.error("删除碰撞视频类出错！");
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }

  }

  @RequestMapping(params = "method=detailSos")
  @ResponseBody
  public Result detailSos(HttpServletRequest request, HttpServletResponse response, String id) {
    IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
    try {
      iRescueService.detailShopSosInfo(Long.valueOf(id));
      return new Result(true);
    } catch (Exception e) {
      LOG.error("处理救援信息出错！");
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }

  }

  @RequestMapping(params = "method=toVideo")
  public String toVideo(HttpServletRequest request, ModelMap modelMap, String url) {
    modelMap.put("url", url);
    return "/remind/impact_video";
  }


  @RequestMapping(params = "method=searchShopFaultInfoList")
  @ResponseBody
  public PagingListResult searchShopFaultInfoList(HttpServletRequest request, HttpServletResponse response,
                                                  FaultInfoSearchConditionDTO searchCondition, Integer startPageNo) {
    int start = startPageNo == null ? 1 : startPageNo;
    int pageSize = 10;
    PagingListResult<FaultInfoToShopDTO> result = new PagingListResult<FaultInfoToShopDTO>();
    Long shopId = WebUtil.getShopId(request);
    searchCondition.setShopId(shopId);
    searchCondition.setIsDeleted(YesNo.NO);
    IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
    try {
      int size= shopFaultInfoService.countShopFaultInfoList_(searchCondition);
      List<FaultInfoToShopDTO> faultInfoToShopDTOList = shopFaultInfoService.findShopFaultInfoList(searchCondition);
      Pager pager = new Pager(size, start, pageSize);
      result.setResults(faultInfoToShopDTOList);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询故障类出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=searchSosList")
  @ResponseBody
  public PagingListResult searchShopFaultInfoList(HttpServletRequest request, HttpServletResponse response, SosInfoSearchConditionDTO sosInfoSearchConditionDTO) {
    int start = sosInfoSearchConditionDTO.getStartPageNo() == 0 ? 1 : sosInfoSearchConditionDTO.getStartPageNo();
    int pageSize = 10;
    PagingListResult<RescueDTO> result = new PagingListResult<RescueDTO>();
    Long shopId = WebUtil.getShopId(request);
    IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    sosInfoSearchConditionDTO.setUntreated(YesNo.YES);
    List<RescueDTO> rescueDTOs = new ArrayList<RescueDTO>();
    try {
      int size = iRescueService.countGetRescueDTOs(shopId, sosInfoSearchConditionDTO);
      if(size>0){
        rescueDTOs = iRescueService.getRescueDTOsByShopId(shopId, sosInfoSearchConditionDTO);
        if (CollectionUtil.isNotEmpty(rescueDTOs)) {
          for (RescueDTO rescueDTO : rescueDTOs) {
            rescueDTO.setUploadTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, rescueDTO.getUploadTime()));
            rescueDTO.setIdStr(rescueDTO.getId().toString());
            if(StringUtil.isNotEmpty(rescueDTO.getAddr())&&rescueDTO.getAddr().indexOf("null")!=-1){
              rescueDTO.setAddr(rescueDTO.getAddr().replace("null",""));
            }
            AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(rescueDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              //客户信息
              CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(appUserCustomerDTO.getCustomerId(), shopId);
              if(customerDTO!=null){
                rescueDTO.setCustomerName(customerDTO.getName());
                rescueDTO.setCustomerMobile(customerDTO.getMobile());
                rescueDTO.setCustomerId(customerDTO.getId().toString());
              }
              //车辆信息
              AppVehicleDTO appVehicleDTO =  appUserVehicleObdService.getAppVehicleById(appUserCustomerDTO.getAppVehicleId());
              if (appVehicleDTO != null) {
                rescueDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
                rescueDTO.setVehicleBrand(appVehicleDTO.getVehicleBrand());
                rescueDTO.setVehicleModel(appVehicleDTO.getVehicleModel());
                if(appVehicleDTO.getCurrentMileage()!=null){
                  rescueDTO.setCurrentMileage(appVehicleDTO.getCurrentMileage().toString());
                }
                rescueDTO.setVehicleMobile(appVehicleDTO.getMobile());
                rescueDTO.setVehicleContact(appVehicleDTO.getContact());
              }
            }
          }
        }
      }
      Pager pager = new Pager(size, start, pageSize);
      result.setResults(rescueDTOs);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询SOS（rescue）类出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=searchMileageList")
  @ResponseBody
  public PagingListResult searchShopMileageList(HttpServletRequest request, HttpServletResponse response, MileageInfoSearchConditionDTO mileageInfoSearchConditionDTO) {
    int start = mileageInfoSearchConditionDTO.getStartPageNo() == 0 ? 1 : mileageInfoSearchConditionDTO.getStartPageNo();
    int pageSize = 10;
    PagingListResult<MileageDTO> result = new PagingListResult<MileageDTO>();
    Long shopId = WebUtil.getShopId(request);
    IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
    List<MileageDTO> mileageDTOs = new ArrayList<MileageDTO>();
    try {
      int size = iRescueService.countGetMileageDTOs(shopId, mileageInfoSearchConditionDTO);
      if(size>0){
        mileageDTOs = iRescueService.getMileageDTOsByShopId(shopId, mileageInfoSearchConditionDTO);
        if(CollectionUtil.isNotEmpty(mileageDTOs)){
          for(MileageDTO mileageDTO:mileageDTOs){
            GsmVehicleDataDTO gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(mileageDTO.getAppUserNo());
            if (gsmVehicleDataDTO != null) {
              if(StringUtil.isNotEmpty(gsmVehicleDataDTO.getCurMil())){
                mileageDTO.setCurrentMileage(gsmVehicleDataDTO.getCurMil().toString());
              }
            }
            AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(mileageDTO.getAppUserNo(), shopId));
            if(appUserCustomerDTO!=null){
              CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(appUserCustomerDTO.getCustomerId(), shopId);
                if(customerDTO!=null){
                  mileageDTO.setCustomerName(customerDTO.getName());
                  mileageDTO.setCustomerMobile(customerDTO.getMobile());
                  mileageDTO.setCustomerId(customerDTO.getId().toString());
                }
            }
          }
        }
      }
      Pager pager = new Pager(size, start, pageSize);
      result.setResults(mileageDTOs);
      result.setPager(pager);
    } catch (Exception e) {
      LOG.error("查询里程提醒列表（appVehicle）类出错！");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getShopTalkMessageList")
  @ResponseBody
  public PagingListResult getShopTalkMessageList(HttpServletRequest request, HttpServletResponse response, Integer startPageNo, Integer maxRows) throws PageException {
    PagingListResult<ShopTalkMessageDTO> result = new PagingListResult<ShopTalkMessageDTO>();
    IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IAppUserVehicleObdService appUserVehicleObdService = ServiceManager.getService(IAppUserVehicleObdService.class);
    Long shopId = WebUtil.getShopId(request);
    int count = pushMessageService.countShopTalkMessageList(null,null, shopId);
    Pager pager = new Pager(count, startPageNo, maxRows);
    List<ShopTalkMessageDTO> talkMessageDTOs = pushMessageService.getShopTalkMessageDTO(null,null, shopId, pager.getRowStart(), pager.getTotalRows());
    if(CollectionUtil.isNotEmpty(talkMessageDTOs)){
      for(ShopTalkMessageDTO shopTalkMessageDTO:talkMessageDTOs){
        AppUserCustomerDTO appUserCustomerDTO = CollectionUtil.getFirst(appUserService.getAppUserCustomerByAppUserNoAndShopId(shopTalkMessageDTO.getAppUserNo(), shopId));
        if(appUserCustomerDTO!=null){
          CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(appUserCustomerDTO.getCustomerId(), shopId);
          if(customerDTO!=null){
            shopTalkMessageDTO.setCustomerName(customerDTO.getName());
            shopTalkMessageDTO.setCustomerMobile(customerDTO.getMobile());
            shopTalkMessageDTO.setCustomerIdStr(customerDTO.getId().toString());
          }
          AppVehicleDTO appVehicleDTO =  appUserVehicleObdService.getAppVehicleById(appUserCustomerDTO.getAppVehicleId());
          if (appVehicleDTO != null) {
            shopTalkMessageDTO.setVehicleNo(appVehicleDTO.getVehicleNo());
            shopTalkMessageDTO.setVehicleMobile(appVehicleDTO.getMobile());
            shopTalkMessageDTO.setVehicleContact(appVehicleDTO.getContact());
          }
        }
      }
    }
    result.setResults(talkMessageDTOs);
    result.setPager(pager);
    return result;
  }

  @RequestMapping(params = "method=updateShopMileageInfo")
  @ResponseBody
  public Result updateShopMileageInfo(HttpServletRequest request, HttpServletResponse response, String appUserNo) {
    IRescueService iRescueService = ServiceManager.getService(IRescueService.class);
    Long shopId = WebUtil.getShopId(request);
    try {
      iRescueService.updateShopMileageInfo(appUserNo,shopId);
      return new Result(true);
    } catch (Exception e) {
      LOG.error("处理救援信息出错！");
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }

  }


}
