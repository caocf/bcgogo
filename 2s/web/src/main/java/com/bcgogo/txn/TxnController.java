package com.bcgogo.txn;

import com.bcgogo.BooleanEnum;
import com.bcgogo.PageErrorMsg;
import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.ObdDTO;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.common.*;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.Constant;
import com.bcgogo.constant.WashCardConstants;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.enums.user.UserSwitchType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.ProductRelevanceHelper;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.model.Model;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.service.IBaseProductService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.ItemIndexService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.suggestion.ISearchSuggestionService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.ServiceVehicleCountDTO;
import com.bcgogo.stat.model.ServiceVehicleCount;
import com.bcgogo.txn.bcgogoListener.orderEvent.RepairOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.orderEvent.SaleOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.dto.secondary.RepairOrderSecondaryDTO;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.RepairOrderTemplateService;
import com.bcgogo.txn.service.app.IAppVehicleService;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.txn.service.solr.OrderSolrWriterService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.permission.UserGroup;
import com.bcgogo.user.model.permission.UserGroupUser;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.AppUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.permission.IUserGroupService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/txn.do")
public class TxnController extends AbstractTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(TxnController.class);
  public static final Long REPAIR_ORDER_STATUS_NEW = 1L;
  public static final Long REPAIR_ORDER_STATUS_EXIST = 2L;
  private static final String validateResult = "resu";
  public static final int PRINT_ITEM_COUNT = 4;//空白打印的item数量
  public static final String TOTAL_RECEIVABLE = "totalReceivable";
  public static final String TOTAL_CONSUME = "totalConsume";
  public static final String TOTAL_PAYABLE = "totalPayable";

  @Autowired
  private RemindEventStrategySelector remindEventStrategySelector;

  private void getVehicleCustomerInfo(Long shopId, String vehicleNumber, List<VehicleDTO> vehicles,
                                      ModelMap model, RepairOrderDTO repairOrderDTO) throws Exception {
    IVehicleService vechicleService = ServiceManager.getService(IVehicleService.class);
    repairOrderDTO.setLicenceNo(vehicleNumber);
    model.addAttribute("repairOrderDTO", repairOrderDTO);
    //得到车辆
    if (shopId != null) {
      if (null == vehicles) {
        vehicles = userService.getVehicleByLicenceNo(shopId, vehicleNumber);
      }
      //如果存在这个用户，就查询他的施工单历史记录
      if (null != vehicles && vehicles.size() > 0) {
        VehicleDTO vehicle = vehicles.get(0);
        repairOrderDTO.setVehicleDTO(vehicle);
        List<CustomerDTO> customerDTOs = userService.getCustomerByLicenceNo(shopId, vehicle.getLicenceNo());
        if (CollectionUtils.isNotEmpty(customerDTOs)) {
          for (CustomerDTO customerDTO : customerDTOs) {
            if (!CustomerStatus.DISABLED.equals(customerDTO.getStatus())) {
              CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerDTO.getId());
              if (null != customerRecordDTO) {
                repairOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
                model.addAttribute(TOTAL_CONSUME, NumberUtil.numberValue(customerRecordDTO.getTotalAmount(), 0D));
              }
              repairOrderDTO.setCustomerDTO(customerDTO);
              break;
            }
          }
        }
        CustomerVehicleDTO customerVehicleDTO = userService.getCustomerVehicleDTOByVehicleIdAndCustomerId(vehicle.getId(), repairOrderDTO.getCustomerId());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, -1);
        Date nowTime = calendar.getTime();

        Long maintainTime = null;
        Long insureTimeStr = null;
        Long examineTime = null;
        Long maintainMileage = null;
        if (null != customerVehicleDTO) {
          maintainTime = customerVehicleDTO.getMaintainTime();
          insureTimeStr = customerVehicleDTO.getInsureTime();
          examineTime = customerVehicleDTO.getExamineTime();
          maintainMileage = customerVehicleDTO.getMaintainMileage();
        }
        if (null != maintainTime && nowTime.getTime() < maintainTime)
          repairOrderDTO.setMaintainTimeStr(customerVehicleDTO.getMaintainTimeStr());
        if (null != insureTimeStr && nowTime.getTime() < insureTimeStr)
          repairOrderDTO.setInsureTimeStr(customerVehicleDTO.getInsureTimeStr());
        if (null != examineTime && nowTime.getTime() < examineTime)
          repairOrderDTO.setExamineTimeStr(customerVehicleDTO.getExamineTimeStr());

        repairOrderDTO.setMaintainMileage(maintainMileage);
        List<AppointServiceDTO> appointServiceDTOs = userService.getAppointServiceByCustomerVehicle(shopId, vehicle.getId(), repairOrderDTO.getCustomerId());
        if (CollectionUtils.isNotEmpty(appointServiceDTOs)) {
          repairOrderDTO.setAppointServiceDTOs(appointServiceDTOs.toArray(new AppointServiceDTO[appointServiceDTOs.size()]));
        }
      }
    }
  }


  /*
  * 进入还款时间页面
  */
  @RequestMapping(params = "method=makeTime")
  public String makeTime(HttpServletRequest request, ModelMap model, @RequestParam("orderId") String orderId) {
    // 单笔单子结算过后就不能设还款时间了，没必要去数据库取一次
    // 欠款结算的时候如果一笔单子再取就可以了。
    return "/txn/makeTime";
  }


  /**
   * 根据车牌号找施工单列表
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=getRepairOrderByVehicleNumber")
  public String getRepairOrderByVehicleNumber(ModelMap model, HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    String vehicleNumber = request.getParameter("vehicleNumber");
    Long orderId = null;
    if (request.getParameter("orderId") != null) {
      orderId = Long.parseLong(request.getParameter("orderId"));
    }
    if (vehicleNumber != null) vehicleNumber = vehicleNumber.toUpperCase();
    Long shopId = WebUtil.getShopId(request);

    String customerIdStr = request.getParameter("customerId");
    if (customerIdStr == null || customerIdStr.trim().equals("")) {
      customerIdStr = (String) request.getAttribute("customerId");
    }
    Long customerId = NumberUtil.longValue(customerIdStr, 0L);
    RepairOrderDTO repairOrderDTO = null;
    if (!StringUtils.isBlank(vehicleNumber)) {
      //得到未结算的该车辆单据
      repairOrderDTO = txnService.getUnbalancedAccountRepairOrderByVehicleNumber(shopId, vehicleNumber.toUpperCase(), orderId);
      if (null != repairOrderDTO && null != repairOrderDTO.getId()) {
        repairOrderDTO = getRepairOrderInfo(model, request, repairOrderDTO.getId().toString());
      } else {
        //得到车辆
        List<VehicleDTO> vehicles = userService.getVehicleByLicenceNo(shopId, vehicleNumber.toUpperCase());
        if (null != vehicles && vehicles.size() > 0) {
          if (repairOrderDTO == null) {
            repairOrderDTO = new RepairOrderDTO();
          }
          repairOrderDTO.setCustomerId(customerId);
          getVehicleCustomerInfo(shopId, vehicleNumber.toUpperCase(), vehicles, model, repairOrderDTO);
        }
      }
    }
    if (repairOrderDTO == null) {
      repairOrderDTO = new RepairOrderDTO();
      if (!StringUtils.isBlank(vehicleNumber)) {
        repairOrderDTO.setVechicle(vehicleNumber.toUpperCase());
      }      //如果从客户管理处传来新客户信息
      customerIdStr = request.getParameter("customerId");
      if (customerIdStr == null || "null".equals(customerIdStr)) {
        if (request.getParameter("customerName") != null && !"".equals(request.getParameter("customerName"))) {
          String customerName = request.getParameter("customerName");
          CustomerDTO customerDTO = new CustomerDTO();
          customerDTO.setName(customerName);
          customerDTO.setShopId(shopId);
          customerDTO = userService.createCustomer(customerDTO);
          customerIdStr = customerDTO.getId().toString();
        }
      }
      if (StringUtils.isNotEmpty(customerIdStr)) {
        customerId = Long.parseLong(customerIdStr.trim());
        CustomerDTO customerDTO = userService.getCustomerById(customerId);
        if (customerDTO != null) {
          CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerDTO.getId());
          if (null != customerRecordDTO) {
            repairOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
          }
          repairOrderDTO.setCustomerDTO(customerDTO);
        }
      }

    }

    //根据customerId获取会员信息
    if (repairOrderDTO.getCustomerId() != null && StringUtil.isEmpty(repairOrderDTO.getMemberNo())) {
      try {
        MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, repairOrderDTO.getCustomerId());
        if (memberDTO != null) {
          if (memberDTO.getMemberServiceDTOs() != null) {
            for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
              Service service = rfiTxnService.getServiceById(memberServiceDTO.getServiceId());
              if (service != null) {
                memberServiceDTO.setServiceName(service.getName());
              }
            }
          }
          repairOrderDTO.setMemberDTO(memberDTO);
          repairOrderDTO.setMemberStatus(membersService.getMemberStatusByMemberDTO(memberDTO).getStatus());
        }
      } catch (Exception e) {
        LOG.error("/txn.do");
        LOG.error("method=getRepairOrderByVehicleNumber");
        LOG.error("查询会员出现异常");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error("customerId:" + repairOrderDTO.getCustomerId());
        LOG.error(repairOrderDTO.toString());
        LOG.error(e.getMessage(), e);
      }
    }

    //如果是用ajax方式进行查询 --点击洗车菜单按钮和离开车辆信息车牌号输入框时
    String type = request.getParameter("type");
    if (StringUtils.isNotEmpty(type) && type.equals("ajax")) {
      //获取上次消费的记录
      //验证洗车
      CustomerCardDTO customerCardDTO = (CustomerCardDTO) model.get("customerCardDTO");
      if (null != customerCardDTO) {
        repairOrderDTO.setCustomerCard(true);
        repairOrderDTO.setRemainWashTimes(customerCardDTO.getWashRemain());
      } else {
        if (repairOrderDTO != null && repairOrderDTO.getCustomerId() != null) {
          List<CustomerCardDTO> customerCardDTOs = userService.getCustomerCardByCustomerIdAndCardType(shopId, repairOrderDTO.getCustomerId(), 0);
          if (null != customerCardDTOs && customerCardDTOs.size() > 0) {
            repairOrderDTO.setCustomerCard(true);
            repairOrderDTO.setRemainWashTimes(customerCardDTOs.get(0).getWashRemain());
          }
        }
      }

      WashOrderDTO[] washOrderDTOs = (WashOrderDTO[]) model.get("washOrderDTOs");
      if (null != washOrderDTOs && washOrderDTOs.length > 0) {
        repairOrderDTO.setLastWashTime(washOrderDTOs[washOrderDTOs.length - 1].getCreationDate());
      } else {
        if (repairOrderDTO != null && repairOrderDTO.getCustomerId() != null) {
          List<WashOrderDTO> washOrderDTOsList = txnService.getCustomerWashOrders(repairOrderDTO.getCustomerId());
          if (null != washOrderDTOsList && washOrderDTOsList.size() > 0) {
            repairOrderDTO.setLastWashTime(washOrderDTOsList.get(0).getCreationDate());
          }
        }
      }
      ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, repairOrderDTO.getCustomerId());
      repairOrderDTO.setDebt(NumberUtil.round(NumberUtil.doubleValue((String) model.get(TOTAL_RECEIVABLE), 0), NumberUtil.MONEY_PRECISION));
      repairOrderDTO.setTotalReturnDebt(NumberUtil.round(NumberUtil.doubleValue((String) model.get(TOTAL_PAYABLE), 0), NumberUtil.MONEY_PRECISION));

      Double totalConsume = 0d;
      if (customerId != null && !customerId.equals(0L)) {
        List<CustomerRecordDTO> custs = userService.getCustomerRecordByCustomerId(customerId);
        if (CollectionUtils.isNotEmpty(custs) && custs.size() == 1) {
          totalConsume = NumberUtil.round(custs.get(0).getTotalAmount(), NumberUtil.MONEY_PRECISION);
        }
      }
      model.addAttribute(TOTAL_CONSUME, totalConsume);
      repairOrderDTO.setTotal(NumberUtil.round(NumberUtil.doubleValue(model.get(TOTAL_RECEIVABLE), 0), NumberUtil.MONEY_PRECISION));
      InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
      //更新库存 根据仓库
      inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, repairOrderDTO.getStorehouseId(), repairOrderDTO);

      List repairOrderDTOs = new ArrayList();

      repairOrderDTOs.add(repairOrderDTO);
//      String jsonStr = ServiceUtil.getJsonWithList(repairOrderDTOs);
      String jsonStr = JsonUtil.listToJson(repairOrderDTOs);
      response.setCharacterEncoding("utf-8");
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    }

    if ("noId".equals(request.getParameter("cancle"))) {
      repairOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
    }

    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapByServiceIds(shopId, repairOrderDTO.getServiceIds());
      for (int i = 0; i < repairOrderDTO.getServiceDTOs().length; i++) {
        RepairOrderServiceDTO repairOrderServiceDTO = repairOrderDTO.getServiceDTOs()[i];
        if (null == repairOrderServiceDTO.getServiceId()) {
          continue;
        }
        CategoryDTO categoryDTO = categoryDTOMap.get(repairOrderServiceDTO.getServiceId());

        if (null == categoryDTO) {
          repairOrderServiceDTO.setBusinessCategoryName(null);
          repairOrderServiceDTO.setBusinessCategoryId(null);
          continue;
        }
        repairOrderServiceDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
        repairOrderServiceDTO.setBusinessCategoryId(categoryDTO.getId());

      }
    }

    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      Set<Long> productIds = repairOrderDTO.getProductIdSet();
      Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(shopId, productIds.toArray(new Long[productIds.size()]));
      Set<Long> categoryIds = new HashSet<Long>();
      if (!MapUtils.isEmpty(productLocalInfoDTOMap)) {
        for (ProductLocalInfoDTO productLocalInfoDTO : productLocalInfoDTOMap.values()) {
          if (productLocalInfoDTO.getBusinessCategoryId() != null) {
            categoryIds.add(productLocalInfoDTO.getBusinessCategoryId());
          }
        }
      }
      Map<Long, CategoryDTO> categoryDTOMap = rfiTxnService.getCategoryDTOMapById(shopId, categoryIds);
      for (int i = 0; i < repairOrderDTO.getItemDTOs().length; i++) {
        RepairOrderItemDTO repairOrderItemDTO = repairOrderDTO.getItemDTOs()[0];
        if (null == repairOrderItemDTO.getProductId()) {
          continue;
        }
        ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(repairOrderItemDTO.getProductId());
        if (null == productLocalInfoDTO || null == productLocalInfoDTO.getBusinessCategoryId()) {
          continue;
        }
        CategoryDTO categoryDTO = categoryDTOMap.get(productLocalInfoDTO.getBusinessCategoryId());
        if (null == categoryDTO || CategoryStatus.DISABLED.equals(categoryDTO.getStatus())) {
          repairOrderItemDTO.setBusinessCategoryName(null);
          repairOrderItemDTO.setBusinessCategoryId(null);
          continue;
        }
        repairOrderItemDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
        repairOrderItemDTO.setBusinessCategoryId(categoryDTO.getId());
      }
    }
    return invoicing(request, model, repairOrderDTO);
  }


  @RequestMapping(params = "method=getRepairOrderByDraftOrder")
  public String getRepairOrderByDraftOrder(ModelMap model, HttpServletRequest request) throws Exception {
    Long shopId = WebUtil.getShopId(request);
    Long shopVersionId = WebUtil.getShopVersionId(request);
    String draftOrderId = request.getParameter("draftOrderId");
    DraftOrderService draftOrderService = ServiceManager.getService(DraftOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();

    if (StringUtils.isNotBlank(draftOrderId)) {
      DraftOrderDTO draftOrderDTO = draftOrderService.getOrderByDraftOrderId(shopId, shopVersionId, Long.valueOf(draftOrderId));
      repairOrderDTO = draftOrderDTO.toRepairOrderDTO();
      repairOrderDTO.setSettledAmount(0d);
      repairOrderDTO.setSettledAmountHid(0d);
      repairOrderDTO.setDebt(0d);
      repairOrderDTO.setDebtHid(0d);
      try {
        InsuranceOrderDTO insuranceOrderDTO = insuranceService.getInsuranceOrderByRepairDraftOrderId(shopId, Long.valueOf(draftOrderId));
        if (insuranceOrderDTO != null) {
          repairOrderDTO.setInsuranceOrderDTO(insuranceOrderDTO);
          repairOrderDTO.setInsuranceOrderId(insuranceOrderDTO.getId());
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
    }
    ShopDTO shopDTO = configService.getShopById(new Long(shopId));
    repairOrderDTO.setShopId(new Long(shopId));
    repairOrderDTO.setShopName(shopDTO.getName());
    repairOrderDTO.setShopAddress(shopDTO.getAddress());
    repairOrderDTO.setShopLandLine(shopDTO.getLandline());

    repairOrderDTO.setShopVersionId(shopVersionId);
    repairService.getProductInfo(repairOrderDTO);

    //如果从客户管理处传来新客户信息
    String customerIdStr = request.getParameter("customerId");
    if (customerIdStr == null || "null".equals(customerIdStr)) {
      if (request.getParameter("customerName") != null && !"".equals(request.getParameter("customerName"))) {
        String customerName = request.getParameter("customerName");
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName(customerName);
        customerDTO.setShopId(shopId);
        customerDTO = userService.createCustomer(customerDTO);
        customerIdStr = customerDTO.getId().toString();
      }
    }
    if (StringUtils.isNotEmpty(customerIdStr)) {
      Long customerId = Long.parseLong(customerIdStr.trim());
      CustomerDTO customerDTO = userService.getCustomerById(customerId);
      if (customerDTO != null) {
        repairOrderDTO.setContact(customerDTO.getContact());
        repairOrderDTO.setCustomerName(customerDTO.getName());
        repairOrderDTO.setCustomerId(customerDTO.getId());
        repairOrderDTO.setLandLine(customerDTO.getLandLine());
        repairOrderDTO.setMobile(customerDTO.getMobile());
      }
    }

    //根据customerId获取会员信息
    if (repairOrderDTO.getCustomerId() != null && StringUtil.isEmpty(repairOrderDTO.getMemberNo())) {
      try {
        IMembersService membersService = ServiceManager.getService(IMembersService.class);
        MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, repairOrderDTO.getCustomerId());
        if (memberDTO != null) {
          if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
            for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
              Service service = rfiTxnService.getServiceById(memberServiceDTO.getServiceId());
              if (service != null) {
                memberServiceDTO.setServiceName(service.getName());
              }
            }
          }
          repairOrderDTO.setMemberDTO(memberDTO);
          repairOrderDTO.setMemberStatus(membersService.getMemberStatusByMemberDTO(memberDTO).getStatus());
        }
      } catch (Exception e) {
        LOG.error("/txn.do");
        LOG.error("method=getRepairOrderByDraftOrder");
        LOG.error("查询会员出现异常");
        LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
        LOG.error("customerId:" + repairOrderDTO.getCustomerId());
        LOG.error(repairOrderDTO.toString());
        LOG.error(e.getMessage(), e);
      }
    }

    if (StringUtils.isBlank(repairOrderDTO.getReceiptNo())) {
      repairOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
    }

    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (int i = 0; i < repairOrderDTO.getServiceDTOs().length; i++) {
        RepairOrderServiceDTO repairOrderServiceDTO = repairOrderDTO.getServiceDTOs()[i];
        if (null == repairOrderServiceDTO.getServiceId()) {
          if (null != repairOrderServiceDTO.getBusinessCategoryId() && StringUtils.isNotBlank(repairOrderServiceDTO.getService())) {
            Category category = rfiTxnService.getEnabledCategoryById(shopId, repairOrderServiceDTO.getBusinessCategoryId());
            if (null == category) {
              repairOrderServiceDTO.setBusinessCategoryName(null);
              repairOrderServiceDTO.setBusinessCategoryId(null);
            }
          }

          continue;
        }

//        ServiceDTO serviceDTO = txnService.getServiceById(repairOrderServiceDTO.getServiceId());
//        repairOrderServiceDTO.setStandardHours(serviceDTO.getStandardHours());
//        repairOrderServiceDTO.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
//        repairOrderServiceDTO.setActualHours(serviceDTO.getStandardHours());
//        repairOrderServiceDTO.setTotal(serviceDTO.getPrice());

        CategoryDTO categoryDTO = rfiTxnService.getCateGoryByServiceId(shopId, repairOrderServiceDTO.getServiceId());

        if (null == categoryDTO) {
          repairOrderServiceDTO.setBusinessCategoryName(null);
          repairOrderServiceDTO.setBusinessCategoryId(null);
          continue;
        }
        repairOrderServiceDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
        repairOrderServiceDTO.setBusinessCategoryId(categoryDTO.getId());

      }
    }

    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (int i = 0; i < repairOrderDTO.getItemDTOs().length; i++) {
        RepairOrderItemDTO repairOrderItemDTO = repairOrderDTO.getItemDTOs()[0];
        if (null == repairOrderItemDTO.getProductId()) {
          if (null != repairOrderItemDTO.getBusinessCategoryId() && StringUtils.isNotBlank(repairOrderItemDTO.getProductName())) {
            Category category = rfiTxnService.getEnabledCategoryById(shopId, repairOrderItemDTO.getBusinessCategoryId());
            if (null == category) {
              repairOrderItemDTO.setBusinessCategoryName(null);
              repairOrderItemDTO.setBusinessCategoryId(null);
            }
          }

          continue;
        }

        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(repairOrderItemDTO.getProductId(), shopId);

        if (null == productLocalInfoDTO || null == productLocalInfoDTO.getBusinessCategoryId()) {
          continue;
        }

        Category category = rfiTxnService.getCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());

        if (null == category) {
          continue;
        }

        repairOrderItemDTO.setBusinessCategoryName(category.getCategoryName());
        repairOrderItemDTO.setBusinessCategoryId(category.getId());
      }
    }
    List<AppointServiceDTO> appointServiceDTOs = userService.getAppointServiceByCustomerVehicle(repairOrderDTO.getShopId(),
      repairOrderDTO.getVechicleId(), repairOrderDTO.getCustomerId());
    if (CollectionUtils.isNotEmpty(appointServiceDTOs)) {
      repairOrderDTO.setAppointServiceDTOs(appointServiceDTOs.toArray(new AppointServiceDTO[appointServiceDTOs.size()]));
    }

    //加入代金券消费记录
    IConsumingService consumingService=ServiceManager.getService(ConsumingService.class);
    Long consumingRecordId=repairOrderDTO.getConsumingRecordId();
    if(consumingRecordId!=null){
      CouponConsumeRecordDTO couponConsumeRecordDTO=consumingService.getCouponConsumeRecordById(consumingRecordId);
      if(couponConsumeRecordDTO==null||couponConsumeRecordDTO.getOrderId()!=null){
        couponConsumeRecordDTO=new CouponConsumeRecordDTO();
      }
      repairOrderDTO.setCouponConsumeRecordDTO(couponConsumeRecordDTO);
    }
    return invoicing(request, model, repairOrderDTO);
  }

  private String invoicing(HttpServletRequest request, ModelMap model, RepairOrderDTO repairOrderDTO) throws Exception {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<CustomerCardDTO> customerCardDTOs = null;
    if (null != repairOrderDTO && null != shopId) {
      if (repairOrderDTO.getCustomerId() != null) {              //
        ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, repairOrderDTO.getCustomerId());
        repairOrderDTO.setTotalReturnDebt(NumberUtil.round(Double.parseDouble(model.get(TOTAL_PAYABLE).toString()), NumberUtil.MONEY_PRECISION));
        customerCardDTOs = userService.getCustomerCardByCustomerIdAndCardType(shopId, repairOrderDTO.getCustomerId(),
          WashCardConstants.CARD_TYPE_DEFAULT);
        if (CollectionUtils.isNotEmpty(customerCardDTOs)) {
          model.addAttribute("customerCardDTO", customerCardDTOs.get(0));
          //获取当天洗车次数
          model.addAttribute("todayWashTimes", txnService.getTodayWashTimes(repairOrderDTO.getCustomerId()));
        }
        List<WashOrderDTO> washOrderDTOs = txnService.getCustomerWashOrders(repairOrderDTO.getCustomerId());
        if (CollectionUtils.isNotEmpty(washOrderDTOs)) {
          model.addAttribute("washOrderDTOs", washOrderDTOs);
        }
      }
      if (null == repairOrderDTO.getId()) {
        long curTime = System.currentTimeMillis();
        String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
        if (repairOrderDTO.getStartDate() == null) {
          repairOrderDTO.setStartDateStr(time);
          repairOrderDTO.setStartDate(curTime);
        }

        if (repairOrderDTO.getServiceType() == null) {
          repairOrderDTO.setServiceType(OrderTypes.REPAIR);//主要内容默认保养/维修/美容
        }
      }
    }
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
      IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
      List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
      model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      if (CollectionUtils.isNotEmpty(storeHouseDTOList) && repairOrderDTO.getStorehouseId() == null) {
        if (storeHouseDTOList.size() == 1) {
          repairOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
        }
      }
      InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
      //更新库存 根据仓库
      inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, repairOrderDTO.getStorehouseId(), repairOrderDTO);
    }
//    activeRecommendSupplierHtmlBuilder.buildActiveRecommendSuppliers(model, repairOrderDTO, WebUtil.getShopVersionId(request));

    if (repairOrderDTO != null) {
      repairOrderDTO.calculateTotal();
    }

    model.addAttribute("repairOrderDTO", repairOrderDTO);
    model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    //主要内容
    Map serviceTypeList = OrderTypes.getServicesLocaleMap(request.getLocale());
    model.addAttribute("serviceTypeList", serviceTypeList);
    //剩余油量
    Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
    model.addAttribute("fuelNumberList", fuelNumberList);

    return "/txn/invoicing";
  }

  /**
   * 保存施工单
   * 已经迁移到repair controller
   *
   * @param model
   * @param repairOrderDTO
   * @return
   */
  @RequestMapping(params = "method=saveRepairOrder")
  @Deprecated
  public String saveRepairOrder(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request,
                                @RequestParam("btnType") String btnType) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    LOG.info("保存施工单开始!,shopId:{}", shopId);
    long begin = System.currentTimeMillis();
    long current = begin;
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    Long userId = WebUtil.getUserId(request);

    Long shopVersionId = WebUtil.getShopVersionId(request);
    String username = WebUtil.getUserName(request);
    String submitBtnType = "";
    try {
      LOG.debug("开始进入维修单操作：操作类型：{},repairOrderDTO :{}", btnType, repairOrderDTO);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
      }
      if (shopId == null || !repairOrderDTO.isValidateSuccess() || StringUtils.isBlank(btnType)) {
        LOG.warn("saveRepairOrder error, repairOrderDTO attributes are empty!");
        return "/txn/invoicing";
      }
      repairOrderDTO.setShopVersionId(shopVersionId);
      repairOrderDTO.setUserId(userId);
      repairOrderDTO.setUserName(username);

      // 施工单模板使用计次
      Long repairOrderTemplateId = repairOrderDTO.getRepairOrderTemplateId();
      if (repairOrderTemplateId != null) {
        IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
        repairOrderTemplateService.updateRepairOrderTemplateUsageCounter(repairOrderTemplateId);
      }


      //派单之后 结算校验
      if (repairOrderDTO.getId() != null && btnType.trim().equals("account")) {
        RepairOrderDTO dbRepairOrderDTO = txnService.getRepairOrder(repairOrderDTO.getId());
        if (OrderStatus.REPAIR_SETTLED.equals(dbRepairOrderDTO.getStatus())) {
          LOG.warn("Repair Order [{}] 已经被结算过", repairOrderDTO.getId());
          submitBtnType = RepairOrderSubmitType.SETTLED.getName();
          return "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
            "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success&btnType=" + submitBtnType;
        }
      }
      String startDateStr = repairOrderDTO.getStartDateStr();
      Long startDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, startDateStr);
      repairOrderDTO.setStartDate(startDate);
      LOG.info("保存施工单--阶段1。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      //得到店面信息
      if (shopId != null) {
        rfiTxnService.populateRepairOrderDTO(repairOrderDTO);//维修单 所填车辆若为新车型，则新增，并将ID保存到此维修单
        //获得 shop信息
        ShopDTO shopDTO = configService.getShopById(Long.valueOf(shopId));
        //车牌号
        String vehicleNumber = repairOrderDTO.getLicenceNo();

        repairOrderDTO.setVechicle(repairOrderDTO.getLicenceNo());
        repairOrderDTO.setShopId(shopId);

        repairOrderDTO.setInventoryLimitDTO(new InventoryLimitDTO());
        repairOrderDTO.getInventoryLimitDTO().setShopId(shopId);
        //更新 customer CustomerRecordDTO
        if (null == repairOrderDTO.getAfterMemberDiscountTotal()) {
          repairOrderDTO.setAfterMemberDiscountTotal(repairOrderDTO.getTotal());
        }
        customerService.handleCustomerForRepairOrder(repairOrderDTO, shopId, userId);
        txnService.saveRepairOrderRemindEvent(repairOrderDTO);
        customerService.updateCustomerRecordForRepairOrder(repairOrderDTO);
        request.getSession().setAttribute("vehicleNumber", repairOrderDTO.getLicenceNo());
        //更新维修单中原先库存存在且不带单位的商品的单位
        txnService.updateProductUnit(repairOrderDTO);

        //更新维修单中使用过的单位顺序
        configService.updateOrderUnitSort(shopId, repairOrderDTO);
        //处理微型店 补充库存时用于判断店铺类型
        repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
        if (repairOrderDTO.getServiceType() != null && !repairOrderDTO.getServiceType().equals(OrderTypes.SALE)) {
          repairOrderDTO.setStartDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getStartDateStr()));       //进厂时间
          repairOrderDTO.setEndDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getEndDateStr()));       //预计出厂时间
          if (StringUtils.isBlank(repairOrderDTO.getReceiptNo())) {
            repairOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.REPAIR, null));
          }
        } else {
          repairOrderDTO.setStatus(OrderStatus.REPAIR_SETTLED);
        }
        //保存单据归属时间
        repairOrderDTO.setSettleDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getSettleDateStr()));
        //归属时间设置  （通过入场时间来设置）
        Long settleDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getStartDateStr());
        if (settleDate != null && (System.currentTimeMillis() - settleDate > 1000 * 60)) {
          settleDate = repairOrderDTO.getStartDate();
        } else {
          LOG.warn("txn repair order vest can't be null");
          //出厂日期 > 当前时间 使用当前时间
          settleDate = System.currentTimeMillis();
        }
        repairOrderDTO.setSettleDate(settleDate);
        LOG.info("保存施工单--阶段2。执行时间: {} ms", System.currentTimeMillis() - current);
        current = System.currentTimeMillis();

        RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
        RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
        if (!ArrayUtils.isEmpty(repairOrderItemDTOs)) {
          for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOs) {
            if (null == repairOrderItemDTO || StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
              continue;
            }

            if (StringUtils.isNotBlank(repairOrderItemDTO.getBusinessCategoryName())) {
              repairOrderItemDTO.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName().trim());
              repairOrderItemDTO.setBusinessCategoryId(rfiTxnService.saveCategory(shopId, repairOrderItemDTO.getBusinessCategoryName()).getId());
            }
          }
        }
        if (!ArrayUtils.isEmpty(repairOrderServiceDTOs)) {
          for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
            if (null == repairOrderServiceDTO || StringUtils.isBlank(repairOrderServiceDTO.getService())) {
              continue;
            }

            if (StringUtils.isNotBlank(repairOrderServiceDTO.getBusinessCategoryName())) {
              repairOrderServiceDTO.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName().trim());
              repairOrderServiceDTO.setBusinessCategoryId(rfiTxnService.saveCategory(shopId, repairOrderServiceDTO.getBusinessCategoryName()).getId());
            }
          }
        }

        List<RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOList = null;

        if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
          for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
            if (StringUtils.isBlank(itemDTO.getName())) {
              continue;
            }
            txnService.saveOrUpdateOtherIncomeKind(shopId, itemDTO.getName());
            if (null == otherIncomeItemDTOList) {
              otherIncomeItemDTOList = new ArrayList<RepairOrderOtherIncomeItemDTO>();
            }
            otherIncomeItemDTOList.add(itemDTO);
          }

        }

        repairOrderDTO.setOtherIncomeItemDTOList(otherIncomeItemDTOList);
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          if (repairOrderDTO.getStorehouseId() != null) {
            StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, repairOrderDTO.getStorehouseId());
            repairOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
          }
        }
        LOG.info("保存施工单--阶段3。执行时间: {} ms", System.currentTimeMillis() - current);
        current = System.currentTimeMillis();

        if (btnType != null && btnType.trim().equals("save") && null != vehicleNumber) {
          //如果是派单
          repairOrderDTO.setStatus(OrderStatus.REPAIR_DISPATCH);
          repairOrderDTO.setPrint("false");
          if (null == repairOrderDTO.getId()) {
            submitBtnType = RepairOrderSubmitType.DISPATCH.getName();
            if (userService.isRepairPickingSwitchOn(shopId)) {
              repairService.saveRepairOrderWithPicking(repairOrderDTO);
            } else {
              repairOrderDTO = repairService.createRepairOrder(repairOrderDTO);
            }
            //ad by WLF 保存施工单的派单日志
            ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
              new OperationLogDTO(shopId, repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.CREATE));
            repairOrderDTO.setOrderStatus(REPAIR_ORDER_STATUS_NEW);            //创建或更新当日服务次数
            //查询有没有服务记录
            //没有就创建
            //有就更新
            List<ServiceVehicleCount> serviceTimes = serviceVehicleCountService.getServiceVehicleCountByTime(shopId, Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date())));//汽修单状态
            if (serviceTimes == null) {
              ServiceVehicleCountDTO svcDTO = new ServiceVehicleCountDTO(shopId, Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date())), 1l);
              serviceVehicleCountService.saveServiceVehicleCount(svcDTO);
            } else {
              for (ServiceVehicleCount s : serviceTimes) {
                s.setCount(s.getCount() + 1);
                serviceVehicleCountService.updateServiceVehicleCountByTime(s);
              }
            }
          } else {
            submitBtnType = RepairOrderSubmitType.CHANGE.getName();
            if (userService.isRepairPickingSwitchOn(shopId)) {
              repairService.updateRepairOrderWithPicking(repairOrderDTO);
            } else {
              repairOrderDTO = repairService.updateRepairOrder(repairOrderDTO);
            }
            repairOrderDTO.setOrderStatus(REPAIR_ORDER_STATUS_EXIST);
            //ad by WLF 保存施工单的改单日志
            ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
              new OperationLogDTO(shopId, repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.UPDATE));
          }
          LOG.info("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
          current = System.currentTimeMillis();
          ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId, repairOrderDTO);
          RepairOrderSavedEvent repairOrderSavedEvent = new RepairOrderSavedEvent(repairOrderDTO);
          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          bcgogoEventPublisher.publisherRepairOrderSaved(repairOrderSavedEvent);
          request.setAttribute("UNIT_TEST", repairOrderSavedEvent); //单元测试
          repairOrderSavedEvent.setMainFlag(true);
          LOG.info("保存施工单--阶段5。执行时间: {} ms", System.currentTimeMillis() - current);
          current = System.currentTimeMillis();
          //修改保险理赔
          ServiceManager.getService(IInsuranceService.class).RFupdateInsuranceOrderById(repairOrderDTO.getId(), null, repairOrderDTO.getInsuranceOrderId(), repairOrderDTO.getReceiptNo());
          LOG.info("保存施工单--阶段6。执行时间: {} ms", System.currentTimeMillis() - current);
        } else if (btnType != null && btnType.trim().equals("finish")) {
          submitBtnType = RepairOrderSubmitType.DONE.getName();
          //如果是完工  (改汽修单状态，记录收款单)
          repairOrderDTO.setStatus(OrderStatus.REPAIR_DONE);
          repairOrderDTO.setPrint("false");
          if (null == repairOrderDTO.getId()) {
            repairOrderDTO = repairService.createRepairOrder(repairOrderDTO);
          } else {
            if (userService.isRepairPickingSwitchOn(shopId)) {
              repairService.updateRepairOrderWithPicking(repairOrderDTO);
            } else {
              repairOrderDTO = repairService.updateRepairOrder(repairOrderDTO);
            }
          }
          //ad by WLF 保存施工单的完工日志
          ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
            new OperationLogDTO(shopId, repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.FINISH));
          repairOrderDTO.setCurrentUsedProductDTOList();
          repairOrderDTO.setCurrentUsedVehicleDTOList();      //todo 施工汽车品牌 与  销售的材料 的特定汽车品牌 冲突问题
          LOG.info("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
          current = System.currentTimeMillis();
          ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId, repairOrderDTO);
          RepairOrderSavedEvent repairOrderSavedEvent = new RepairOrderSavedEvent(repairOrderDTO);
          BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
          bcgogoEventPublisher.publisherRepairOrderSaved(repairOrderSavedEvent);
          request.setAttribute("UNIT_TEST", repairOrderSavedEvent); //单元测试
          repairOrderSavedEvent.setMainFlag(true);
          //完工短信：给顾客发送完工短信
          smsService.sendFinishMsgToCustomer(repairOrderDTO, shopId, shopDTO);
          LOG.info("保存施工单--阶段5。执行时间: {} ms", System.currentTimeMillis() - current);
          current = System.currentTimeMillis();
          //修改保险理赔
          ServiceManager.getService(IInsuranceService.class).RFupdateInsuranceOrderById(repairOrderDTO.getId(), null, repairOrderDTO.getInsuranceOrderId(), repairOrderDTO.getReceiptNo());
          LOG.info("保存施工单--阶段6。执行时间: {} ms", System.currentTimeMillis() - current);
        } else if (btnType != null && btnType.trim().equals("account")) {           //结算开始
          submitBtnType = RepairOrderSubmitType.SETTLED.getName();
          //如果是维修美容保养
          if (repairOrderDTO.getServiceType() != null && repairOrderDTO.getServiceType().equals(OrderTypes.REPAIR)) {
            //如果是结算 (改汽修单状态)
            repairOrderDTO.setStatus(OrderStatus.REPAIR_SETTLED);
            repairOrderDTO.setEditDate(System.currentTimeMillis());
            //归属时间设置  （通过出厂时间来设置）
            Long vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, repairOrderDTO.getEndDateStr());
            if (System.currentTimeMillis() - NumberUtil.longValue(vestDate) < 1000 * 60 || vestDate == null) {
              //出厂日期 > 当前时间 使用当前时间
              vestDate = System.currentTimeMillis();
            }
            repairOrderDTO.setVestDate(vestDate);
            repairOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, vestDate));

            if (null == repairOrderDTO.getId()) {
              List<ServiceVehicleCount> serviceTimes = serviceVehicleCountService.getServiceVehicleCountByTime(shopId, Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date())));//汽修单状态

              if (serviceTimes == null) {
                ServiceVehicleCountDTO svcDTO = new ServiceVehicleCountDTO(shopId, Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date())), 1l);
                serviceVehicleCountService.saveServiceVehicleCount(svcDTO);
              } else {
                for (ServiceVehicleCount s : serviceTimes) {
                  s.setCount(s.getCount() + 1);
                  serviceVehicleCountService.updateServiceVehicleCountByTime(s);
                }
              }
              //直接结算
              repairOrderDTO = repairService.createRepairOrder(repairOrderDTO);
            } else {
              //派单之后 结算
              if (userService.isRepairPickingSwitchOn(shopId)) {
                repairService.updateRepairOrderWithPicking(repairOrderDTO);
              } else {
                repairOrderDTO = repairService.updateRepairOrder(repairOrderDTO);
              }
            }
            LOG.info("保存施工单--阶段4。执行时间: {} ms", System.currentTimeMillis() - current);
            current = System.currentTimeMillis();
            //ad by WLF 保存施工单的结算日志
            ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
              new OperationLogDTO(shopId, repairOrderDTO.getUserId(), repairOrderDTO.getId(), ObjectTypes.REPAIR_ORDER, OperationTypes.SETTLE));

            //更新会员信息, 并发送提醒短信给持卡人（如果勾上）
            VelocityContext context = txnService.updateMemberInfo(repairOrderDTO);
            CustomerDTO cardOwner = userService.getCustomerWithMemberByMemberNoShopId(repairOrderDTO.getAccountMemberNo(), shopId);
            if (cardOwner != null && StringUtils.isNotEmpty(cardOwner.getMobile()) && repairOrderDTO.isSendMemberSms()) {
              smsService.sendMemberMsgToCardOwner(cardOwner, shopDTO, context);
            }
            repairOrderDTO.setCurrentUsedProductDTOList();
            repairOrderDTO.setCurrentUsedVehicleDTOList();      //todo 施工汽车品牌 与  销售的材料 的特定汽车品牌 冲突问题
            ServiceManager.getService(IProductHistoryService.class).saveProductHistoryForOrder(shopId, repairOrderDTO);
            LOG.info("保存施工单--阶段5。执行时间: {} ms", System.currentTimeMillis() - current);
            current = System.currentTimeMillis();
            BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
            RepairOrderSavedEvent repairOrderSavedEvent = new RepairOrderSavedEvent(repairOrderDTO);
            String isRunThread = request.getParameter("isRunThread");
            if (!"noRun".equals(isRunThread)) {
              bcgogoEventPublisher.publisherRepairOrderSaved(repairOrderSavedEvent);
            }
            request.setAttribute("UNIT_TEST", repairOrderSavedEvent); //单元测试
            repairOrderSavedEvent.setMainFlag(true);
            long curTime = System.currentTimeMillis();
            String time = DateUtil.convertDateLongToDateString(RfTxnConstant.FORMAT_CHINESE_YEAR_MONTH_DATE, curTime);
            //维修美容折扣短信：如果总计大于实收和欠款和的话，就代表打折了，要发送折扣短信给店老板.
            if (repairOrderDTO.getTotal() > repairOrderDTO.getSettledAmount() + repairOrderDTO.getDebt()) {
              smsService.sendCheapMsgToBoss(repairOrderDTO, shopId, shopDTO, time);
            }
            //如果有欠款就要发送欠款备忘给店老板
            if (repairOrderDTO.getDebt() > 0) {
              smsService.sendDebtMsgToBoss(repairOrderDTO, shopId, shopDTO, time);
            }
            //修改保险理赔
            ServiceManager.getService(IInsuranceService.class).RFupdateInsuranceOrderById(repairOrderDTO.getId(), null, repairOrderDTO.getInsuranceOrderId(), repairOrderDTO.getReceiptNo());
          } else if (repairOrderDTO.getServiceType() != null && repairOrderDTO.getServiceType().equals(OrderTypes.SALE)) {    //旧的销售逻辑，应已废弃
            SalesOrderDTO salesOrderDTO = new SalesOrderDTO(repairOrderDTO);
            salesOrderDTO.setEditor((String) request.getSession().getAttribute("userName"));
            salesOrderDTO.setEditorId((Long) request.getSession().getAttribute("userId"));
            salesOrderDTO.setDebt(repairOrderDTO.getDebt());
            salesOrderDTO.setSettledAmount(repairOrderDTO.getSettledAmount());
            salesOrderDTO.setGoodsSaler(repairOrderDTO.getProductSaler());
            salesOrderDTO.setEditDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, repairOrderDTO.getStartDateStr()));
            txnService.createOrUpdateSalesOrder(salesOrderDTO, repairOrderDTO.getHuankuanTime());
            Long payTime = null;
            if (salesOrderDTO.getDebt() > 0.001) {
              if (StringUtils.isNotEmpty(repairOrderDTO.getHuankuanTime()))
                payTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, repairOrderDTO.getHuankuanTime());
            }
            long curTime = System.currentTimeMillis();
            String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_CN, curTime);
            //销售折扣短信：如果总计大于实收和欠款和的话，就代表打折了，要发送折扣短信给店老板; modify:zhangjuntnao
            if (salesOrderDTO.getTotal() > salesOrderDTO.getSettledAmount() + salesOrderDTO.getDebt()) {
              smsService.sendSalesOrderCustomerCheapMsgToBoss(salesOrderDTO, shopId, shopDTO, time);
            }
            //销售欠款短信：如果有欠款就要发送欠款备忘给店老板; modify:zhangjuntnao
            if (salesOrderDTO.getDebt() > 0) {
              smsService.sendSalesOrderCustomerDebtMsgToBoss(salesOrderDTO, shopId, shopDTO, time, payTime);
            }
            salesOrderDTO.setMobile(repairOrderDTO.getMobile() == null ? repairOrderDTO.getLandLine() : repairOrderDTO.getMobile());
            salesOrderDTO.setPaymentTime(payTime);
            SaleOrderSavedEvent saleOrderSavedEvent = new SaleOrderSavedEvent(salesOrderDTO);
            BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
            bcgogoEventPublisher.publisherSaleOrderSaved(saleOrderSavedEvent);
            return "/txn/invoicing";
          }
        }           //结算结束
        //先做保险单，再生成施工单，更新保险单和施工单的关系
        insuranceService.updateInsuranceByRepairOrderDTO(repairOrderDTO);
        //更新memcacheLimitInfo
        iInventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, repairOrderDTO.getInventoryLimitDTO());
        //更新memcache  product 更新的标识和时间
        ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(repairOrderDTO);
        //每新增一张单据，就要将同一个客户里面的欠款提醒的状态改为未提醒
        ServiceManager.getService(ITxnService.class).updateRemindEventStatus(repairOrderDTO.getShopId(), repairOrderDTO.getCustomerId(), "customer");
        LOG.info("保存施工单--阶段6。执行时间: {} ms", System.currentTimeMillis() - current);
        current = System.currentTimeMillis();
        return "redirect:/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR&repairOrderId=" + String.valueOf(repairOrderDTO.getId()) +
          "&print=" + repairOrderDTO.getPrint() + "&resultMsg=success&btnType=" + submitBtnType;
      }
    } catch (Exception e) {
      LOG.error("/txn.do?method=saveRepairOrder,shopId={},userId ={}", request.getSession().getAttribute("shopId"),
        request.getSession().getAttribute("userId"));
      LOG.error("repairOrderDTO ={}", repairOrderDTO);
      LOG.error(e.getMessage(), e);
      model.addAttribute("resultMsg", "failure");
      model.addAttribute("btnType", submitBtnType);
      return "txn/invoicing";
    }
    return "/login";
  }

  /**
   * 根据施工单Id去准备做打印
   *
   * @param model
   * @return
   */
  @RequestMapping(params = "method=getRepairOrderToPrint")
  public void getRepairOrderToPrint(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam("repairOrderId") String repairOrderId, String templateId) throws Exception {
    RepairOrderDTO repairOrderDTO = null;
    if (StringUtils.isNotEmpty(repairOrderId) && !"null".equals(repairOrderId)) {
      repairOrderDTO = getRepairOrderInfo(model, request, repairOrderId);
      if (repairOrderDTO != null) {
//        repairOrderDTO.setSettledAmount(NumberUtils.toDouble(request.getParameter("settledAmount")));
//        repairOrderDTO.setDebt(NumberUtils.toDouble(request.getParameter("debt")));
      }
    }
    Map fuelNumberList = (Map) model.get("fuelNumberList");
    CustomerRecordDTO customerRecordDTO = (CustomerRecordDTO) model.get("customerRecordDTO");
    String totalReceivable = (String) model.get(TOTAL_RECEIVABLE);    //应收
    String totalPayable = (String) model.get(TOTAL_PAYABLE);    //应付
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    try {
      ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
      PrintTemplateDTO printTemplateDTO = null;
      if (StringUtils.isBlank(templateId) || !NumberUtil.isNumber(templateId)) {
        printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.REPAIR);
      } else {
        printTemplateDTO = printService.getPrintTemplateDTOFullById(Long.parseLong(templateId));
      }
      if (StringUtils.isEmpty(repairOrderId)) {
        repairOrderDTO = new RepairOrderDTO();
        RepairOrderItemDTO[] repairOrderItemDTOs = new RepairOrderItemDTO[PRINT_ITEM_COUNT];
        RepairOrderServiceDTO[] repairOrderServiceDTOs = new RepairOrderServiceDTO[PRINT_ITEM_COUNT];
        RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();
        RepairOrderServiceDTO repairOrderServiceDTO = new RepairOrderServiceDTO();
        repairOrderItemDTO.setShopId(shopDTO.getId());
        repairOrderServiceDTO.setShopId(shopDTO.getId());
        for (int i = 0; i < PRINT_ITEM_COUNT; i++) {
          repairOrderItemDTOs[i] = repairOrderItemDTO;
          repairOrderServiceDTOs[i] = repairOrderServiceDTO;
        }
        repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
        repairOrderDTO.setServiceDTOs(repairOrderServiceDTOs);
      }
      repairOrderDTO.setShopLandLine(shopDTO.getLandline());
      repairOrderDTO.setShopMobile(shopDTO.getMobile());
      repairOrderDTO.setShopAddress(shopDTO.getAddress());
      repairOrderDTO.setShopName(shopDTO.getName());
      repairOrderDTO.setUserName(userService.getNameByUserId(WebUtil.getUserId(request)));
      repairOrderDTO.setDiscount(com.bcgogo.utils.NumberUtil.round(repairOrderDTO.getTotal() - repairOrderDTO.getSettledAmount() - repairOrderDTO.getDebt(), NumberUtil.MONEY_PRECISION));
      repairOrderDTO.setTotal(com.bcgogo.utils.NumberUtil.round(repairOrderDTO.getTotal(), NumberUtil.MONEY_PRECISION));
      repairOrderDTO.setSettledAmount(com.bcgogo.utils.NumberUtil.round(repairOrderDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION));
      repairOrderDTO.setDebt(com.bcgogo.utils.NumberUtil.round(repairOrderDTO.getDebt(), NumberUtil.MONEY_PRECISION));
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopDTO.getId(), repairOrderDTO.getCustomerId());
      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");
        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库
        StringResourceRepository repo = StringResourceLoader.getRepository();
        String myTemplateName = "invoicingPrint" + String.valueOf(WebUtil.getShopId(request));
        String myTemplate = str;
        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);
        //从资源库中加载模板
        Template template = ve.getTemplate(myTemplateName);
        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        //把数据填入上下文
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DATE);

        VehicleDTO vehicleDTO = null;
        if (null != repairOrderDTO.getLicenceNo()) {
          IUserService userService = ServiceManager.getService(IUserService.class);
          List<VehicleDTO> vehicleDTOList = userService.getVehicleByLicenceNo(shopDTO.getId(), repairOrderDTO.getLicenceNo());
          if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
            vehicleDTO = vehicleDTOList.get(0);
          }
        }
        if (vehicleDTO != null && customerRecordDTO != null) {
          customerRecordDTO.setVin(vehicleDTO.getChassisNumber());
        }
        CustomerDTO customerDTO = (CustomerDTO) model.get("customerDTO");
        String settleType = null;
        if (null != customerDTO && null != customerDTO.getSettlementType()) {
          if (Long.valueOf("1").equals(customerDTO.getSettlementType())) {
            settleType = "现金";
          } else if (Long.valueOf("2").equals(customerDTO.getSettlementType())) {
            settleType = "月结";
          } else if (Long.valueOf("3").equals(customerDTO.getSettlementType())) {
            settleType = "货到付款";
          } else if (Long.valueOf("4").equals(customerDTO.getSettlementType())) {
            settleType = "季付";
          }
        }

        if (customerDTO != null) {
          repairOrderDTO.setCustomerDTO(customerDTO);
        }

        repairOrderDTO.setSettledAmountStr(MoneyUtil.toBigType(com.bcgogo.utils.StringUtil.valueOf(repairOrderDTO.getTotal())));
        repairOrderDTO.setTotalStr(MoneyUtil.toBigType(com.bcgogo.utils.StringUtil.valueOf(repairOrderDTO.getTotal())));
        repairOrderDTO.setServiceTotalStr(MoneyUtil.toBigType(com.bcgogo.utils.StringUtil.valueOf(repairOrderDTO.getServiceTotal())));
        Double productTotal = 0d;
        if (!ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs())) {
          for (RepairOrderItemDTO itemDTO : repairOrderDTO.getItemDTOs()) {
            productTotal = NumberUtil.addition(productTotal, itemDTO.getTotal());
          }
        }
        repairOrderDTO.setProductTotal(productTotal);  //材料费
        repairOrderDTO.setProductTotalStr(MoneyUtil.toBigType(StringUtil.valueOf(productTotal)));
        context.put("settleType", settleType);
        context.put("repairOrderDTO", repairOrderDTO);
        context.put("brandModelEmpty", StringUtils.isBlank(repairOrderDTO.getBrand()) && StringUtils.isBlank(repairOrderDTO.getModel()));
        context.put("storeManagerMobile", shopDTO.getStoreManagerMobile());
        context.put("shop", shopDTO);
        context.put("fuelNumberList", fuelNumberList);
        context.put("customerRecordDTO", customerRecordDTO);
        context.put("userName", (String) request.getSession().getAttribute("userName"));
        context.put("memberNo", null == memberDTO ? "" : memberDTO.getMemberNo());
        context.put("memberBalance", null == memberDTO ? "" : memberDTO.getBalance());
        context.put("vehicleDTO", vehicleDTO);
        context.put("year", year);
        context.put("month", month);
        context.put("date", date);
        String DateStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, System.currentTimeMillis());
        context.put("dataStr", DateStr);
        context.put("isDebug", System.getProperty("is.developer.debug"));
        context.put(TOTAL_RECEIVABLE, NumberUtil.doubleValue(totalReceivable, 0d));
        context.put(TOTAL_PAYABLE, NumberUtil.doubleValue(totalPayable, 0d));
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body><h1>没有可用的模板</h1></body><html>");
      }
    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("id:" + repairOrderId);
      WebUtil.reThrow(LOG, e);
    } finally {
      out.close();
    }
  }

  /**
   * 根据施工单Id找施工单
   *
   * @param model
   * @return
   */

  @RequestMapping(params = "method=getRepairOrder")
  public String getRepairOrder(ModelMap model, HttpServletRequest request,
                               @RequestParam("repairOrderId") String repairOrderId) {
    LOG.info("查看施工单,shopId:{},repairOrderId:{}", WebUtil.getShopId(request), repairOrderId);
    long begin = System.currentTimeMillis();
    long current = begin;
    RepairOrderDTO repairOrderDTO = null;


    if (StringUtils.isNotEmpty(repairOrderId) && !"null".equals(repairOrderId)) {
      repairOrderDTO = getRepairOrderInfo(model, request, repairOrderId);
      LOG.debug("查看施工单--阶段1。执行时间: {} ms", System.currentTimeMillis() - current);
      current = System.currentTimeMillis();
      List<ReceptionRecordDTO> receptionRecordDTOs = ServiceManager.getService(ITxnService.class).getSettledRecord(WebUtil.getShopId(request), OrderTypes.REPAIR, Long.valueOf(repairOrderId));
      model.addAttribute("receptionRecordDTOs", receptionRecordDTOs);
      model.addAttribute("receiveNo", ServiceManager.getService(ITxnService.class).getStatementAccountOrderNo(WebUtil.getShopId(request), repairOrderDTO.getStatementAccountOrderId()));
      LOG.debug("repair order {} ", repairOrderId);
    }
    try {
      Long shopId = WebUtil.getShopId(request);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && repairOrderDTO.getStorehouseId() == null) {
          if (storeHouseDTOList.size() == 1) {
            repairOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, repairOrderDTO.getStorehouseId(), repairOrderDTO);
      }
      //结算提醒控制开关
      UserSwitchDTO userSwitchDTO = ServiceManager.getService(IUserService.class).getUserSwitchByShopIdAndScene(shopId, UserSwitchType.SETTLED_REMINDER.toString());
      if (userSwitchDTO != null && "ON".equals(userSwitchDTO.getStatus())) {
        model.addAttribute("smsSwitch", true);
      } else {
        model.addAttribute("smsSwitch", false);
      }
    } catch (Exception e) {
      LOG.debug("/txn.do?method=getRepairOrder&menu-uid=VEHICLE_CONSTRUCTION_REPAIR");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId"));
      LOG.debug(repairOrderDTO.toString());
      LOG.error(e.getMessage(), e);
    }
    LOG.debug("查看施工单--阶段2。执行时间: {} ms", System.currentTimeMillis() - current);
    current = System.currentTimeMillis();

    if (repairOrderDTO != null && (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus()) || OrderStatus.REPAIR_REPEAL.equals(repairOrderDTO.getStatus()))) {
      IRepairOrderSecondaryService repairOrderSecondaryService = ServiceManager.getService(IRepairOrderSecondaryService.class);
      RepairOrderSecondaryDTO repairOrderSecondaryDTO = repairOrderSecondaryService.findRepairOrderSecondaryByRepairOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
      if (repairOrderSecondaryDTO != null) {
        request.setAttribute("repairOrderSecondaryDTO", repairOrderSecondaryDTO);
      }
      return "/txn/invoicingFinish";
    } else {
      return "/txn/invoicing";
    }
  }

  public RepairOrderDTO getRepairOrderInfo(ModelMap model, HttpServletRequest request, String repairOrderId) {
    StopWatchUtil sw = new StopWatchUtil("TxnController getRepairOrderInfo", "getOrder_insuranceOrder");
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    DraftOrderService draftOrderService = ServiceManager.getService(DraftOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    RepairOrderDTO repairOrderDTO = null;
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (StringUtils.isNotBlank(repairOrderId)) {
        repairOrderDTO = rfiTxnService.getRepairOrderDTODetailById(new Long(repairOrderId), shopId);
        if (repairOrderDTO != null) {
          try {
            InsuranceOrderDTO insuranceOrderDTO = insuranceService.getInsuranceOrderByRepairOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
            repairOrderDTO.setInsuranceOrderDTO(insuranceOrderDTO);
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }
        sw.stopAndStart("getDraftOrder");
        String receiptNo = null == repairOrderDTO ? "" : repairOrderDTO.getReceiptNo();
        DraftOrderDTO draftOrderDTO = draftOrderService.getDraftOrderByTxnOrderId(shopId, WebUtil.getShopVersionId(request), new Long(repairOrderId));
        if (draftOrderDTO != null) {
          LOG.debug("ID为" + repairOrderId + "的订单有对应的草稿存在！草稿ID：" + draftOrderDTO.getId());
          repairOrderDTO = draftOrderDTO.toRepairOrderDTO();
          repairOrderDTO.setSettledAmount(0d);
          repairOrderDTO.setSettledAmountHid(0d);
          repairOrderDTO.setDebt(0d);
          repairOrderDTO.setDebtHid(0d);
          repairOrderDTO.setReceiptNo(receiptNo);


          if (!ArrayUtil.isEmpty(repairOrderDTO.getServiceDTOs())) {
            for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
              if (repairOrderServiceDTO.getServiceId() == null) {
                continue;
              }
              ServiceDTO serviceDTO = txnService.getServiceById(repairOrderServiceDTO.getServiceId());
              repairOrderServiceDTO.setStandardHours(serviceDTO.getStandardHours());
              repairOrderServiceDTO.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());
            }
          }
        }
        sw.stopAndStart("getQualifiedCredentials");
        QualifiedCredentialsDTO qualifiedCredentialsDTO = txnService.getQualifiedCredentialsDTO(shopId, Long.valueOf(repairOrderId));
        if (null != qualifiedCredentialsDTO) {
          repairOrderDTO.setQualifiedNo(qualifiedCredentialsDTO.getNo());
          repairOrderDTO.setRepairContractNo(qualifiedCredentialsDTO.getRepairContractNo());
        }
      }
      if (repairOrderDTO != null) {
        repairOrderDTO.setPrint(request.getParameter("print"));
        model.addAttribute("repairOrderDTO", repairOrderDTO);
      } else {
        repairOrderDTO = new RepairOrderDTO();
        model.addAttribute("repairOrderDTO", repairOrderDTO);
        return repairOrderDTO;
      }
      ServiceManager.getService(RFITxnService.class).populateVehicleAppointment(repairOrderDTO);
      if (repairOrderDTO == null) {
        repairOrderDTO = new RepairOrderDTO();
        LOG.error("ID为" + repairOrderId + "的订单不存在！");
      }

//      String vehicleNumber = repairOrderDTO.getVechicle();
//      if (StringUtils.isNotEmpty(vehicleNumber))
//        request.getSession().setAttribute("vehicleNumber", vehicleNumber);
      if (null != repairOrderDTO.getStartDate())
        repairOrderDTO.setStartDateStr(
          DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,
            repairOrderDTO.getStartDate()));          // 进厂时间
      if (null != repairOrderDTO.getEndDate())
        repairOrderDTO.setEndDateStr(
          DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,
            repairOrderDTO.getEndDate()));             //预约出厂时间

      ShopDTO shopDTO = configService.getShopById(new Long(shopId));
      repairOrderDTO.setShopId(new Long(shopId));
      repairOrderDTO.setShopName(shopDTO.getName());
      repairOrderDTO.setShopAddress(shopDTO.getAddress());
      repairOrderDTO.setShopLandLine(shopDTO.getLandline());
      // 判断是否要渲染: 导出相应省份施工单结算清单Excel
      repairOrderDTO.setFinishOrderDownType(transformProvinceToType(shopDTO));
      //
      sw.stopAndStart("getMemberInfo");
      if (repairOrderDTO.getCustomerId() != null) {
        try {
          MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, repairOrderDTO.getCustomerId());
          if (memberDTO != null) {
            if (CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
              for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
                Service service = rfiTxnService.getServiceById(memberServiceDTO.getServiceId());
                if (service != null) {
                  memberServiceDTO.setServiceName(service.getName());
                }
              }
            }
            repairOrderDTO.setMemberDTO(memberDTO);
            repairOrderDTO.setMemberStatus(membersService.getMemberStatusByMemberDTO(memberDTO).getStatus());
          }
        } catch (Exception e) {
          LOG.error("/txn.do");
          LOG.error("method=getRepairOderInfo");
          LOG.error("查询会员出现异常");
          LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error("customerId:" + repairOrderDTO.getCustomerId());
          LOG.error(repairOrderDTO.toString());
          e.printStackTrace();
          LOG.error(e.getMessage(), e);
        }
      }

      repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      sw.stopAndStart("getProductInfo");
      repairService.getProductInfo(repairOrderDTO);
      sw.stopAndStart("getVehicle");
      //得到车辆
      VehicleDTO vehicleDTO = userService.getVehicleById(repairOrderDTO.getVechicleId());
      model.addAttribute("vehicleDTO", vehicleDTO);
      if (vehicleDTO == null) {
        return repairOrderDTO;
      }
      List<CustomerVehicleDTO> customerVehicleDTOs = userService.getCustomerVehicleByVehicleId(vehicleDTO.getId());
      CustomerVehicleDTO customerVehicleDTO = null;
      if (CollectionUtils.isNotEmpty(customerVehicleDTOs)) {
        for (CustomerVehicleDTO vehicleIndex : customerVehicleDTOs) {
          if (vehicleIndex == null) {
            continue;
          }
          if (!VehicleStatus.DISABLED.equals(vehicleIndex.getStatus())) {
            customerVehicleDTO = vehicleIndex;
            break;
          }
        }
      }
      CustomerRecordDTO customerRecordDTO = null;
      if (customerVehicleDTO != null) {
        CustomerDTO customerDTO = userService.getCustomerById(customerVehicleDTO.getCustomerId());
        if (OrderUtil.repairOrderInProgress.contains(repairOrderDTO.getStatus())) {   //中间状态的施工单更新客户及车辆信息
          if (null != customerDTO) {
            repairOrderDTO.setCustomerDTO(customerDTO);
            repairOrderDTO.setCustomerQqEmailDTO(customerDTO);
          }
          repairOrderDTO.setVehicleDTO(vehicleDTO);
        }
        model.addAttribute("customerDTO", customerDTO);
        List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(customerDTO.getId());
        if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
          customerRecordDTO = customerRecordDTOs.get(0);
          model.addAttribute(TOTAL_CONSUME, NumberUtil.numberValue(customerRecordDTO.getTotalAmount(), 0d));
          model.addAttribute("customerRecordDTO", customerRecordDTO);
        }
      }

      //sw.stopAndStart("consumingRecord");
      //加入代金券消费记录
      IConsumingService consumingService=ServiceManager.getService(ConsumingService.class);
      Long consumingRecordId=repairOrderDTO.getConsumingRecordId();
      if(consumingRecordId!=null){
        CouponConsumeRecordDTO couponConsumeRecordDTO=consumingService.getCouponConsumeRecordById(consumingRecordId);
        if(couponConsumeRecordDTO==null||!repairOrderDTO.getStatus().equals(couponConsumeRecordDTO.getOrderStatus())){
          couponConsumeRecordDTO=new CouponConsumeRecordDTO();
        }
        repairOrderDTO.setCouponConsumeRecordDTO(couponConsumeRecordDTO);
        repairOrderDTO.setCouponAmount(couponConsumeRecordDTO.getCoupon());
      }

      sw.stopAndStart("getReceivable");
      //得到收款单信息
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(repairOrderDTO.getShopId(), OrderTypes.REPAIR,
        repairOrderDTO.getId());
      if (null != receivableDTO) {
        repairOrderDTO.setReceivableId(receivableDTO.getId());
        repairOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
        repairOrderDTO.setSettledAmountHid(receivableDTO.getSettledAmount());
        repairOrderDTO.setDebt(receivableDTO.getDebt());
        repairOrderDTO.setCashAmount(receivableDTO.getCash());
        repairOrderDTO.setBankAmount(receivableDTO.getBankCard());
        repairOrderDTO.setMemberAmount(receivableDTO.getMemberBalancePay());
        repairOrderDTO.setBankCheckAmount(receivableDTO.getCheque());
        repairOrderDTO.setOrderDiscount(receivableDTO.getDiscount());
        repairOrderDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
        repairOrderDTO.setStrikeAmount(receivableDTO.getStrike());
        repairOrderDTO.setStatementAmount(NumberUtil.doubleVal(receivableDTO.getStatementAmount()));
        repairOrderDTO.setHuankuanTime(receivableDTO.getRemindTime() == null ? null : DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, receivableDTO.getRemindTime()));
        customerRecordDTO.setRepayDate(receivableDTO.getRemindTime());
        if (null != receivableDTO.getMemberDiscountRatio()) {
          repairOrderDTO.setMemberDiscountRatio(NumberUtil.round(receivableDTO.getMemberDiscountRatio() * 10, 1));
        }
        repairOrderDTO.setPayee(receivableDTO.getLastPayee());
        ReceptionRecordDTO[] receptionRecordDTOs = receivableDTO.getRecordDTOs();
        if (receptionRecordDTOs != null && receptionRecordDTOs.length > 0) {
          ReceptionRecordDTO receptionRecordDTO = receptionRecordDTOs[0];
          repairOrderDTO.setBankCheckNo(receptionRecordDTO.getChequeNo());
        }
        if (receivableDTO.getMemberId() != null) {
          Member member = membersService.getMemberById(receivableDTO.getMemberId());
          if (member != null) {
            repairOrderDTO.setAccountMemberNo(member.getMemberNo());
          }
        }
        //添加代金券金额
        repairOrderDTO.setCouponAmount(receivableDTO.getCoupon());
      }
      //判断是否缺料
      if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
        for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
          if (NumberUtil.doubleVal(repairOrderItemDTO.getInventoryAmount()) + NumberUtil.doubleVal(repairOrderItemDTO.getReserved())
            < NumberUtil.doubleVal(repairOrderItemDTO.getAmount()) + 0.0001) {
            repairOrderItemDTO.setLack(true);
          }
        }
      }

      Double otherIncomeTotal = 0D;

      sw.stopAndStart("other");
      if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
        for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
          otherIncomeTotal += (null == itemDTO || null == itemDTO.getPrice()) ? 0D : itemDTO.getPrice();
        }
      }

      repairOrderDTO.setOtherIncomeTotal(NumberUtil.round(otherIncomeTotal, NumberUtil.MONEY_PRECISION));
      //主要内容
      Map serviceTypeList = OrderTypes.getServicesLocaleMap(request.getLocale());
      model.addAttribute("serviceTypeList", serviceTypeList);
      //剩余油量
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      model.addAttribute("fuelNumberList", fuelNumberList);

      ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, repairOrderDTO.getCustomerId());
      repairOrderDTO.setTotalReturnDebt(NumberUtil.round(Double.parseDouble(model.get(TOTAL_PAYABLE).toString()), NumberUtil.MONEY_PRECISION));
      //判断是否有有材料内容
      if (repairOrderDTO != null && !ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs())) {
        repairOrderDTO.setContainMaterial(true);
      }
      RepairPickingDTO repairPickingDTO = pickingService.getRepairPickingDTOSimpleByRepairId(shopId, repairOrderDTO.getId());
      if (repairPickingDTO != null) {
        repairOrderDTO.setRepairPickingId(repairPickingDTO.getId());
        repairOrderDTO.setRepairPickingReceiptNo(repairPickingDTO.getReceiptNo());
      }
      model.addAttribute("afterMemberDeduction", NumberUtil.round(NumberUtil.numberValue(repairOrderDTO.getAfterMemberDiscountTotal(), 0D)
        - NumberUtil.numberValue(repairOrderDTO.getSettledAmount(), 0D) - NumberUtil.numberValue(repairOrderDTO.getDebt(), 0D), 2));
    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("repairOrderId:" + repairOrderId);
      LOG.error(e.getMessage(), e);
    }
    sw.stopAndStart("total");
    //设置服务费用总和销售费用总和
    setServiceAndSaleTotal(repairOrderDTO);
    List<AppointServiceDTO> appointServiceDTOs = userService.getAppointServiceByCustomerVehicle(repairOrderDTO.getShopId(), repairOrderDTO.getVechicleId(), repairOrderDTO.getCustomerId());
    if (CollectionUtils.isNotEmpty(appointServiceDTOs)) {
      repairOrderDTO.setAppointServiceDTOs(appointServiceDTOs.toArray(new AppointServiceDTO[appointServiceDTOs.size()]));
    }

    sw.stopAndPrintLog();
//    activeRecommendSupplierHtmlBuilder.buildActiveRecommendSuppliers(model,repairOrderDTO, WebUtil.getShopVersionId(request));
    return repairOrderDTO;
  }


  /*
  * 进入客户信息页面
  */
  @RequestMapping(params = "method=clientInfo")
  public String clientInfo(ModelMap model, HttpServletRequest request, String customer, String mobile, String hiddenMobile, String customerId, String contact, String landLine) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<CarDTO> vehicles = new ArrayList<CarDTO>();
    CustomerRecordDTO customerRecordDTO = null;
    if (StringUtil.isNotEmpty(customerId) || (shopId != null && StringUtil.isNotEmpty(customer))) {

      if (!StringUtil.isEmpty(customerId)) {
        List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(Long.parseLong(customerId));
        if (customerRecordDTOs != null && !customerRecordDTOs.isEmpty()) {
          customerRecordDTO = customerRecordDTOs.get(0);
        }
      }
      if (customerRecordDTO == null) {
        List<CustomerRecordDTO> customerRecordDTOs = userService.getShopCustomerRecordByMobile(shopId, customer, mobile);
        if (customerRecordDTOs != null && !customerRecordDTOs.isEmpty()) {
          customerRecordDTO = customerRecordDTOs.get(0);
        }
      }
      if (customerRecordDTO != null) {
        CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(customerRecordDTO.getCustomerId(), shopId);
        model.addAttribute("customerDTO", customerDTO);
        if (customerDTO != null) {
          customerRecordDTO.fromCustomerDTO(customerDTO);
          customerRecordDTO.setContact(contact);
          customerRecordDTO.setMobile(mobile);
          if (StringUtils.isNotEmpty(landLine)) {
            customerRecordDTO.setPhone(landLine);
          }
          if (customerRecordDTO.getCustomerShopId() != null) {
            customerRecordDTO.setOnlineShop(true);
          }
          customerRecordDTO.setShopId(shopId);
          customerRecordDTO.setName(customer);
          customerRecordDTO.setMobile(mobile);
          if (null != customerDTO.getBirthday())
            customerRecordDTO.setBirthdayString(
              DateUtil.convertDateLongToDateString(DateUtil.MONTH_DATE, customerDTO.getBirthday()));
          vehicles = userService.getVehiclesByCustomerId(shopId, customerDTO.getId());
          if (CollectionUtil.isNotEmpty(vehicles)) {
            for (int i = 0; i < vehicles.size(); i++) {
              if (0 != vehicles.get(i).getCarDate())
                vehicles.get(i).setDateString(
                  DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, vehicles.get(i).getCarDate()));
            }
            customerRecordDTO.setVehicles(vehicles.toArray(new CarDTO[vehicles.size()]));
          }
          //既是客户又是供应商，只要对方为店铺，也不能修改资料
          if (customerDTO.getSupplierId() != null) {
            SupplierDTO supplierDTO = ServiceManager.getService(IUserService.class).getSupplierById(customerDTO.getSupplierId());
            if (supplierDTO != null) {
              if (supplierDTO.getSupplierShopId() != null) {
                customerRecordDTO.setOnlineShop(true);
              }
            }
          }
        }
        try {
          MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, customerRecordDTO.getCustomerId());
          if (memberDTO != null) {
            if (memberDTO.getMemberServiceDTOs() != null) {
              for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
                Service service = rfiTxnService.getServiceById(memberServiceDTO.getServiceId());
                if (service != null) {
                  memberServiceDTO.setServiceName(service.getName());
                }
              }
            }
            customerRecordDTO.setMemberDTO(memberDTO);
            MemberCardOrderItem memberCardOrderItem = rfiTxnService.getLastMemberCardOrderItemByCustomerId(shopId, customerRecordDTO.getCustomerId());
            if (memberCardOrderItem != null) {
              customerRecordDTO.setLastChargeAmount(memberCardOrderItem.getAmount());
              customerRecordDTO.setLastChargeDateStr(DateUtil.dateLongToStr(memberCardOrderItem.getCreationDate(), DateUtil.DATE_STRING_FORMAT_DAY2));
            }
          }
        } catch (Exception e) {
          LOG.error("method=clientInfo" + e.getMessage(), e);
        }
      } else {
        // 如果该信息
        customerRecordDTO = new CustomerRecordDTO();
        customerRecordDTO.setShopId(shopId);
        customerRecordDTO.setName(customer);
        customerRecordDTO.setContact(contact);
        customerRecordDTO.setMobile(mobile);
        customerRecordDTO.setPhone(landLine);
        // 设置到联系人列表里面 add by zhuj
        ContactDTO[] contactDTOs = new ContactDTO[3];
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName(contact);
        contactDTO.setMobile(mobile);
        contactDTO.setLevel(0);
        contactDTO.setIsMainContact(1);
        contactDTO.setShopId(shopId);
        contactDTOs[0] = contactDTO;
        customerRecordDTO.setContacts(contactDTOs);
      }


      model.addAttribute("customerRecordDTO", customerRecordDTO);

    } else {
      customerRecordDTO = new CustomerRecordDTO();
      customerRecordDTO.setName(customer);
      customerRecordDTO.setContact(contact);
      customerRecordDTO.setMobile(mobile);
      customerRecordDTO.setVehicles(new CarDTO[1]);
      // 设置到联系人列表里面 add by zhuj
      ContactDTO[] contactDTOs = new ContactDTO[3];
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setName(contact);
      contactDTO.setMobile(mobile);
      contactDTO.setLevel(0);
      contactDTO.setIsMainContact(1);
      contactDTO.setShopId(shopId);
      contactDTOs[0] = contactDTO;
      customerRecordDTO.setContacts(contactDTOs);
      model.addAttribute("customerRecordDTO", customerRecordDTO);
    }
    //获取页面下拉框信息
    Map<String, String> invoiceCatagoryMap = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(request.getLocale());
    Map<String, String> settlementTyoeMap = TxnConstant.getSettlementTypeMap(request.getLocale());
    model.addAttribute("invoiceCatagoryMap", invoiceCatagoryMap);
    model.addAttribute("customerTypeMap", customerTypeMap);
    model.addAttribute("settlementTypeMap", settlementTyoeMap);
    model.addAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    model.addAttribute("obd_imei", request.getParameter("obd_imei"));
    model.addAttribute("sim_no", request.getParameter("sim_no"));
    if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
      return "/txn/clientInfo_parts";
    } else {
      return "/txn/clientInfo";
    }
  }

  /**
   * 做单的时候进入供应商详细页面
   *
   * @param model
   * @param request
   * @param supplier
   * @param mobile
   * @param hiddenMobile
   * @param supplierId
   * @param contact
   * @param landLine
   * @return
   */
  @RequestMapping(params = "method=orderSupplierInfo")
  public String orderSupplierInfo(ModelMap model, HttpServletRequest request, String supplier,
                                  String mobile, String hiddenMobile,
                                  String supplierId, String contact, String landLine) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Long supplierIdLong = 0L;
    if (StringUtils.isNotBlank(supplierId)) {
      supplierIdLong = Long.parseLong(supplierId);
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    SupplierDTO supplierDTO = userService.getSupplierById(supplierIdLong);
    if (supplierDTO == null) {
      // 通过mobile 来查询 或者supplier 来查询supplierDTO
      if (StringUtils.isNotBlank(supplier)) {
        List<SupplierDTO> supplierDTOs = userService.getSupplierByName(shopId, supplier);
        if (!CollectionUtils.isEmpty(supplierDTOs)) {
          supplierDTO = supplierDTOs.get(0);
        }
      }
    }

    if (supplierDTO == null) {
      if (StringUtils.isNotBlank(mobile)) {
        List<SupplierDTO> supplierDTOs = userService.getSupplierByMobile(shopId, mobile);
        if (!CollectionUtils.isEmpty(supplierDTOs)) {
          supplierDTO = supplierDTOs.get(0);
        }
      }
    }

    if (supplierDTO == null) {
      supplierDTO = new SupplierDTO();
      supplierDTO.setShopId(shopId);
      supplierDTO.setContact(contact);
      supplierDTO.setMobile(mobile);
      supplierDTO.setName(supplier);
      ContactDTO[] contactDTOs = new ContactDTO[3];
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setName(contact);
      contactDTO.setMobile(mobile);
      contactDTO.setLevel(0);
      contactDTO.setIsMainContact(1);
      contactDTO.setShopId(shopId);
      contactDTOs[0] = contactDTO;
      supplierDTO.setContacts(contactDTOs);
    }
    if (supplierDTO.getSupplierShopId() != null) {
      supplierDTO.setOnlineShop(true);
    }
    //即使客户又是供应商，当另一身份为店铺时，也不能更新资料
    if (supplierDTO.getCustomerId() != null) {
      CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(supplierDTO.getCustomerId());
      if (customerDTO != null) {
        if (customerDTO.getCustomerShopId() != null) {
          supplierDTO.setOnlineShop(true);
        }
      }
    }
    if (!supplierDTO.hasValidContact()) {
      ContactDTO[] contactDTOs = new ContactDTO[3];
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setName(contact);
      contactDTO.setMobile(mobile);
      contactDTO.setLevel(0);
      contactDTO.setIsMainContact(1);
      contactDTO.setShopId(shopId);
      contactDTOs[0] = contactDTO;
      supplierDTO.setContacts(contactDTOs);
    }
    if (StringUtils.isBlank(supplierDTO.getLandLine())) {
      if (StringUtils.isBlank(supplierDTO.getLandLineSecond())) {
        if (StringUtils.isBlank(supplierDTO.getLandLineThird())) {
          supplierDTO.setLandLine(landLine);
        } else {
          supplierDTO.setLandLineThird(landLine);
        }
      } else {
        supplierDTO.setLandLineSecond(landLine);
      }
    } else {
      supplierDTO.setLandLine(landLine);
    }
    supplierDTO.compositeLandline();
    /*supplierDTO.setLandLine(landLine);*/
    request.setAttribute("supplierDTO", supplierDTO);
    request.setAttribute("hasValidContact", supplierDTO.hasValidContact()); // add by zhuj
    //客户类型
    Map categoryList = TxnConstant.getCustomerTypeMap(request.getLocale());
    //结算方式
    Map settlementTypeList = TxnConstant.getSettlementTypeMap(request.getLocale());
    //发票类型
    Map invoiceCategoryList = TxnConstant.getInvoiceCatagoryMap(request.getLocale());
    request.setAttribute("categoryList", categoryList);
    request.setAttribute("settlementTypeList", settlementTypeList);
    request.setAttribute("invoiceCategoryList", invoiceCategoryList);

    if (request.getAttribute("supplierId") == null)
      request.setAttribute("supplierId", supplierIdLong);

    request.setAttribute("wholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
    return "/txn/orderSupplierInfo";
  }

  /**
   * 更新供应商
   *
   * @param model
   * @param supplierDTO
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=updateSupplier")
  @ResponseBody
  public Object updateSupplier(ModelMap model, SupplierDTO supplierDTO, HttpServletRequest request, HttpServletResponse response) {
    SupplierDTO dbSupplierDTO = null;
    try {
      Long supplierId = null;
      String id = request.getParameter("supplierId");
      String name = request.getParameter("supplier"); // key和DTO对象不符合...
      Long shopId = WebUtil.getShopId(request);
      supplierDTO.setName(name);
      if (StringUtils.isNotBlank(id) && !StringUtils.equals(id, "0")) {
        supplierId = Long.parseLong(id);
      }
      if (supplierId != null) {
        dbSupplierDTO = userService.getSupplierById(supplierId);
      }
      if (supplierId != null && dbSupplierDTO != null) {
        IUserService userService = ServiceManager.getService(IUserService.class);
        setUpdatedSupplierField(supplierDTO, dbSupplierDTO); // 设置需要更新的供应商字段
        // customerId、identity 以DB中为准
        // 存在客户信息更新客户信息 关联的话 直接更新联系人信息 updateSupplier的时候不做操作
        if (dbSupplierDTO.getCustomerId() != null && StringUtils.equals(dbSupplierDTO.getIdentity(), "isCustomer")) {
          CustomerDTO customerDTO = userService.getCustomerById(dbSupplierDTO.getCustomerId());
          customerDTO.fromSupplierDTO(supplierDTO);
          ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
          ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(dbSupplierDTO.getCustomerId(), dbSupplierDTO.getId(), shopId, dbSupplierDTO.getContacts());
          ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(dbSupplierDTO.getCustomerId(), dbSupplierDTO.getId(), shopId, supplierDTO.getContacts());
          CustomerRecordDTO customerRecordDTO = null;
          List<CustomerRecordDTO> customerRecordDTOList = ServiceManager.getService(IUserService.class).getCustomerRecordByCustomerId(dbSupplierDTO.getCustomerId());
          if (!CollectionUtils.isEmpty(customerRecordDTOList)) {
            customerRecordDTO = customerRecordDTOList.get(0);
          }
          if (customerRecordDTO != null) {
            customerRecordDTO.fromCustomerDTO(customerDTO);
            userService.updateCustomerRecord(customerRecordDTO);
          }
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        }
        /* if (StringUtils.isBlank(supplierDTO.getIdentity()) && supplierDTO.getCustomerId() != null) {
          CustomerDTO customerDTO = userService.getCustomerById(supplierDTO.getCustomerId());
          customerDTO.setSupplierId(null);
          customerDTO.setIdentity(null);
          userService.updateCustomer(customerDTO);
          supplierDTO.setCustomerId(null);
          ServiceManager.getService(ISupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
        }*/
        dbSupplierDTO.setFromManagePage(true);
        userService.updateSupplier(dbSupplierDTO);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(dbSupplierDTO.getId());
        dbSupplierDTO = userService.getSupplierById(supplierId); // 用于填充主联系人
      } else {
        supplierDTO.setShopId(shopId);
        supplierDTO.setInvoiceCategoryId(NumberUtil.isLongNumber(supplierDTO.getInvoiceCategory()) ? Long.valueOf(supplierDTO.getInvoiceCategory()) : null);
        supplierDTO.setSettlementTypeId(NumberUtil.isLongNumber(supplierDTO.getSettlementType()) ? Long.valueOf(supplierDTO.getInvoiceCategory()) : null);
        dbSupplierDTO = userService.createSupplier(supplierDTO);
        if (!ArrayUtils.isEmpty(dbSupplierDTO.getContacts())) {
          for (ContactDTO contact : dbSupplierDTO.getContacts()) {
            if (contact != null && contact.getIsMainContact() != null && contact.getIsMainContact() == 1) {
              dbSupplierDTO.setContact(contact.getName());
              dbSupplierDTO.setContactId(contact.getId());
              dbSupplierDTO.setMobile(contact.getMobile());
              dbSupplierDTO.setQq(contact.getQq());
              dbSupplierDTO.setEmail(contact.getEmail());
            }
          }
        }
        ServiceManager.getService(ISupplierRecordService.class).createSupplierRecordUsingSupplierDTO(supplierDTO);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(supplierDTO.getId());
      }
    } catch (Exception e) {
      LOG.warn("/txn.do?method=updateSupplier");
      LOG.error(e.getMessage(), e);
    }

    return dbSupplierDTO;
  }

  private void setUpdatedSupplierField(SupplierDTO supplierDTO, SupplierDTO dbSupplierDTO) {
    dbSupplierDTO.setName(supplierDTO.getName());
    dbSupplierDTO.setAbbr(supplierDTO.getAbbr());
    dbSupplierDTO.setAccount(supplierDTO.getAccount());
    dbSupplierDTO.setAccountName(supplierDTO.getAccountName());
    dbSupplierDTO.setAddress(supplierDTO.getAddress());
    dbSupplierDTO.setBank(supplierDTO.getBank());
    dbSupplierDTO.setLandLine(supplierDTO.getLandLine());
    dbSupplierDTO.setLandLineSecond(supplierDTO.getLandLineSecond());
    dbSupplierDTO.setLandLineThird(supplierDTO.getLandLineThird());
    dbSupplierDTO.compositeLandline();
    dbSupplierDTO.setFax(supplierDTO.getFax());
    // 这个地方需要设置supplierId、customerId、disabled
    if (!ArrayUtils.isEmpty(supplierDTO.getContacts())) {
      for (ContactDTO contactDTO : supplierDTO.getContacts()) {
        if (contactDTO != null && contactDTO.isValidContact()) {
          contactDTO.setSupplierId(dbSupplierDTO.getId());
          contactDTO.setCustomerId(dbSupplierDTO.getCustomerId());
          contactDTO.setDisabled(1);
        }
      }
      dbSupplierDTO.setContacts(supplierDTO.getContacts());
    }
    dbSupplierDTO.setProvince(supplierDTO.getProvince());
    dbSupplierDTO.setCity(supplierDTO.getCity());
    dbSupplierDTO.setRegion(supplierDTO.getRegion());
    dbSupplierDTO.setInvoiceCategory(supplierDTO.getInvoiceCategory());
    dbSupplierDTO.setSettlementType(supplierDTO.getSettlementType());
    dbSupplierDTO.setInvoiceCategoryId(NumberUtil.isLongNumber(supplierDTO.getInvoiceCategory()) ? Long.valueOf(supplierDTO.getInvoiceCategory()) : null);
    dbSupplierDTO.setSettlementTypeId(NumberUtil.isLongNumber(supplierDTO.getSettlementType()) ? Long.valueOf(supplierDTO.getInvoiceCategory()) : null);
    dbSupplierDTO.setMemo(supplierDTO.getMemo());
  }


  /*
  * 更新客户信息页面
  */
  @RequestMapping(params = "method=updateCustomer")
  @ResponseBody
  public Object updateCustomer(ModelMap model, CustomerRecordDTO customerRecordDTO, HttpServletRequest request, HttpServletResponse response) throws BcgogoException, IOException {
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    Long customerId = null;
    List<VehicleDTO> vehicleDTOs = null;
    Result result = new Result("", true);
    try {
      customerRecordDTO.setShopId(shopId);
      CustomerDTO customerDTO = null;
      if (customerRecordDTO.getCustomerId() != null) {
        customerDTO = userService.getCustomerDTOByCustomerId(customerRecordDTO.getCustomerId(), shopId);
      } else if (CustomerConstant.DEFAULT_CUSTOMER_NAME.equals(customerRecordDTO.getName())) {
        customerDTO = customerService.isCustomerExist(shopId, CustomerConstant.DEFAULT_CUSTOMER_NAME, null, null);
        if (customerDTO != null && customerDTO.getId() != null) {
          customerRecordDTO.setCustomerId(customerDTO.getId());
        }
      }

      customerService.validateAddCustomer(customerRecordDTO, result);
      if (!result.isSuccess()) {
        return result;
      }
      if (null == customerDTO) { //如果用户id不存在，创建用户
        customerDTO = new CustomerDTO();
        customerDTO.fromCustomerRecordDTO(customerRecordDTO, false, false);
        customerDTO.setShopId(shopId);
        userService.createCustomer(customerDTO);
        customerId = customerDTO.getId();
        CarDTO[] carDTOs = customerRecordDTO.getVehicles();
        if (!ArrayUtil.isEmpty(carDTOs)) {
          for (CarDTO carDTO : carDTOs) {
            if (carDTO == null) continue;
            if (NumberUtil.isNumber(carDTO.getLicenceNo())) {
              ShopDTO shopDTO = configService.getShopById(WebUtil.getShopId(request));
              if (shopDTO != null && !StringUtil.isEmpty(shopDTO.getLicencePlate())) {
                carDTO.setLicenceNo(shopDTO.getLicencePlate() + carDTO.getLicenceNo());
              }
            }
          }
        }
        vehicleDTOs = productService.saveOrUpdateVehicleInfo(shopId, userId, customerDTO.getId(), customerRecordDTO.getVehicles());
        //如果填写obd信息，将自动绑定到车辆
        IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);
        appVehicleService.syncAppVehicle(vehicleDTOs);
        //绑定OBD
        IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
        if (!ArrayUtil.isEmpty(carDTOs)) {
          for (CarDTO carDTO : carDTOs) {
            if (StringUtil.isEmpty(carDTO.getGsmObdImei()) || StringUtil.isEmpty(carDTO.getGsmObdImeiMoblie())) {
              continue;
            }
            obdManagerService.gsmOBDBind(CollectionUtil.getFirst(vehicleDTOs), carDTO.getGsmObdImei(), carDTO.getGsmObdImeiMoblie(),
              WebUtil.getUserId(request), WebUtil.getShopName(request), WebUtil.getUserName(request));
            IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
            IObdManagerService iObdManagerService = ServiceManager.getService(IObdManagerService.class);
            ObdDTO obdDTO = iObdManagerService.getObdByImeiAndMobile(carDTO.getGsmObdImei(), carDTO.getGsmObdImeiMoblie());
            AppUserDTO appUserDTO = appUserService.getAppUserDTOByMobileUserType(carDTO.getGsmObdImeiMoblie(), AppUserType.MIRROR);
            if (appUserDTO == null && obdDTO != null && StringUtil.isNotEmpty(obdDTO.getImei())) {
              //后视镜自动分配帐号
              if (ObdType.MIRROR.equals(obdDTO.getObdType())) {
                GSMRegisterDTO gsmRegisterDTO = new GSMRegisterDTO();
                gsmRegisterDTO.setImei(obdDTO.getImei());
                ServiceManager.getService(IAppUserService.class).gsmAllocateAppUser(gsmRegisterDTO);
              }
            }
          }
        }
        //update customerRecordDTO
        customerRecordDTO.setCustomerId(customerDTO.getId());
        customerRecordDTO.setShopId(shopId);
        userService.createCustomerRecord(customerRecordDTO);
        //reindex customer in solr
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
      } else {    //已有客户, 更新
        customerId = customerDTO.getId();
        CustomerRecordDTO customerRecordDTO1 = null;
        customerDTO.fromCustomerRecordDTO(customerRecordDTO, false, false);
        customerDTO.setFromManagePage(true);
        userService.updateCustomer(customerDTO);
//        userService.deleteVehiclesByCarDTOs(shopId, customerId, customerRecordDTO.getVehicles()); //改成前台删除
        vehicleDTOs = productService.saveOrUpdateVehicleInfo(shopId, userId, customerDTO.getId(), customerRecordDTO.getVehicles());
        CarDTO[] vehicles = customerRecordDTO.getVehicles();
        if (ArrayUtil.isNotEmpty(vehicles)) {
          List<Long> vehicleIds = new ArrayList<Long>();
          Map<Long, CarDTO> carDTOMap = new HashMap<Long, CarDTO>();
          for (CarDTO carDTO : vehicles) {
            vehicleIds.add(NumberUtil.longValue(carDTO.getId()));
            carDTOMap.put(NumberUtil.longValue(carDTO.getId()), carDTO);
          }
          List<CustomerVehicleDTO> customerVehicleDTOs = userService.getCustomerVehicleDTO(ArrayUtil.toLongArr(vehicleIds));
          if (CollectionUtil.isNotEmpty(customerVehicleDTOs)) {
            for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOs) {
              CarDTO carDTO = carDTOMap.get(customerVehicleDTO.getVehicleId());
              if (carDTO != null) {
                customerVehicleDTO.setMaintainMileagePeriod(carDTO.getMaintainMileagePeriod());
              }
            }
            userService.saveOrUpdateCustomerVehicle(customerVehicleDTOs);
          }
        }

        if (ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))) {
          IAppVehicleService appVehicleService = ServiceManager.getService(IAppVehicleService.class);
          appVehicleService.syncAppVehicle(vehicleDTOs);
          //绑定OBD
//          IObdManagerService obdManagerService=ServiceManager.getService(IObdManagerService.class);
//          if(!ArrayUtil.isEmpty(vehicles)){
//            for(CarDTO carDTO:vehicles){
//              obdManagerService.gsmOBDBind(CollectionUtil.getFirst(vehicleDTOs),carDTO.getGsmObdImei(),carDTO.getGsmObdImeiMoblie());
//            }
//          }
        }
        CustomerRecord customerRecord = userService.getShopCustomerRecordByCustomerId(shopId, customerDTO.getId());
        if (null != customerRecord) {
          customerRecordDTO1 = customerRecord.toDTO();
        }
        if (customerDTO.getSupplierId() != null) {
          SupplierDTO supplierDTO = CollectionUtil.getFirst(ServiceManager.getService(IUserService.class).getSupplierById(shopId, customerDTO.getSupplierId()));
          supplierDTO.fromCustomerRecordDTO(customerRecordDTO);
          ServiceManager.getService(IUserService.class).updateSupplier(supplierDTO);
          // add by zhuj
          ServiceManager.getService(IContactService.class).updateContactsBelongCustomerAndSupplier(customerRecordDTO.getCustomerId(), customerRecordDTO.getSupplierId(), shopId, customerDTO.getContacts());
          ServiceManager.getService(IContactService.class).addContactsBelongCustomerAndSupplier(customerDTO.getId(), customerDTO.getSupplierId(), shopId, customerDTO.getContacts());
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(customerDTO.getSupplierId());
        }

        if (null != customerRecordDTO1) {
          customerRecordDTO1.setName(customerRecordDTO.getName());
          customerRecordDTO1.setShortName(customerRecordDTO.getShortName());
          customerRecordDTO1.setAddress(customerRecordDTO.getAddress());
          customerRecordDTO1.setContact(customerRecordDTO.getContact());
          customerRecordDTO1.setMobile(customerRecordDTO.getMobile());
          customerRecordDTO1.setPhone(customerRecordDTO.getPhone());
          customerRecordDTO1.setFax(customerRecordDTO.getFax());
          customerRecordDTO1.setMemberNumber(customerRecordDTO.getMemberNumber());
          customerRecordDTO1.setMemo(customerRecordDTO.getMemo());
          customerRecordDTO1.setArea(customerRecordDTO.getArea());
          customerRecordDTO1.setBirthdayString(customerRecordDTO.getBirthdayString());
          customerRecordDTO1.setQq(customerRecordDTO.getQq());
          customerRecordDTO1.setEmail(customerRecordDTO.getEmail());
          customerRecordDTO1.setBank(customerRecordDTO.getBank());
          customerRecordDTO1.setBankAccountName(customerRecordDTO.getBankAccountName());
          customerRecordDTO1.setAccount(customerRecordDTO.getAccount());
          customerRecordDTO1.setInvoiceCategory(customerRecordDTO.getInvoiceCategory());
          customerRecordDTO1.setSettlementType(customerRecordDTO.getSettlementType());
          customerRecordDTO1.setCustomerKind(customerRecordDTO.getCustomerKind());
          userService.updateCustomerRecord(customerRecordDTO1);

        }

        //reindex customer in solr
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(customerDTO.getId());
      }

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("customerDTO", customerDTO);
      map.put("id", StringUtil.valueOf(customerId));
      if (CollectionUtil.isNotEmpty(vehicleDTOs)) {
        VehicleDTO vehicleDTO = CollectionUtil.getFirst(vehicleDTOs);
        map.put("licenceNo", vehicleDTO.getLicenceNo());
        map.put("vehicleId", vehicleDTO.getIdStr());
        map.put("vehicleDTOs", vehicleDTOs);
      }

      result.setData(map);

      return result;
    } catch (Exception e) {
      LOG.debug(customerRecordDTO.toString());
      LOG.error("/txn.do?method=updateCustomer" + "shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request) + e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("网络异常，请联系管理员！");
      return result;
    }

  }

  /*
  * 进入预约服务页面
  */
  @RequestMapping(params = "method=service")
  public String service(ModelMap model, HttpServletRequest request, @RequestParam("customerId") String customerId,
                        @RequestParam("vehicleId") String vehicleId) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      List<ScheduleServiceEventDTO> scheduleServiceEventDTOs =
        txnService.getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(shopId, new Long(customerId), new Long(vehicleId));
      ScheduleServiceEventDTO scheduleServiceEventDTO = null;
      if (null != scheduleServiceEventDTOs && scheduleServiceEventDTOs.size() > 0) {
        scheduleServiceEventDTO = scheduleServiceEventDTOs.get(0);
        scheduleServiceEventDTO.setServiceDateStr(
          DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, scheduleServiceEventDTO.getServiceDate()));
        if (null != scheduleServiceEventDTO.getServiceType())
          scheduleServiceEventDTO.setServiceTypeArr(scheduleServiceEventDTO.getServiceType().split(","));
      } else {
        scheduleServiceEventDTO = new ScheduleServiceEventDTO();
        scheduleServiceEventDTO.setShopId(shopId);
        scheduleServiceEventDTO.setCustomerId(new Long(customerId));
        scheduleServiceEventDTO.setVechicleId(new Long(vehicleId));
      }
      model.addAttribute("scheduleServiceEventDTO", scheduleServiceEventDTO);
    } catch (BcgogoException e) {
      LOG.debug("/txn.do");
      LOG.debug("method=service");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("customerId:" + customerId + ",vehicleId:" + vehicleId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/service";
  }

  /*
  * 更新预约服务
  */
  @RequestMapping(params = "method=updateService")
  public void updateService(ModelMap model, ScheduleServiceEventDTO scheduleServiceEventDTO) {
    try {
      if (StringUtils.isNotEmpty(scheduleServiceEventDTO.getServiceDateStr()))
        scheduleServiceEventDTO.setServiceDate(
          DateUtil.convertDateStringToDateLong(
            DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, scheduleServiceEventDTO.getServiceDateStr()));
      scheduleServiceEventDTO.setServiceType(StringUtils.join(scheduleServiceEventDTO.getServiceTypeArr(), ","));
      if (null == scheduleServiceEventDTO.getId()) {
        txnService.createScheduleServiceEvent(scheduleServiceEventDTO);
      } else {
        txnService.updateScheduleServiceEvent(scheduleServiceEventDTO);
      }
    } catch (ParseException e) {
      LOG.debug("/txn.do");
      LOG.debug("method=updateService");
      LOG.debug(scheduleServiceEventDTO.toString());
      LOG.error(e.getMessage(), e);
    } catch (BcgogoException e) {
      LOG.debug("/txn.do");
      LOG.debug("method=updateService");
      LOG.debug(scheduleServiceEventDTO.toString());
      LOG.error(e.getMessage(), e);
    }
  }


  /**
   * debtPrint
   */
  @RequestMapping(params = "method=printDebtArrears")
  public void printDebtArrears(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    ShopDTO shopDTO = configService.getShopById(shopId);
    Long customerId = NumberUtil.longValue(request.getParameter("customerId"), 0L);
    String totalAmount = request.getParameter("totalAmount");
    String payedAmount = request.getParameter("payedAmount");
    String[] orderIds = request.getParameter("orderId").split(",");
    List<DebtDTO> debtDTOList = new ArrayList<DebtDTO>();
    CustomerDTO customerDTO = customerService.getCustomerById(customerId);
    if (customerDTO == null) {
      customerDTO = new CustomerDTO();
    }
    for (int i = 1; i < orderIds.length; i++) {
      DebtDTO debtDTO = txnService.getDebtByShopIdAndCustomerIdAndOrderId(shopId, customerId, Long.parseLong(orderIds[i]));
      debtDTOList.add(debtDTO);
    }
    Date now = new Date();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String szDatetime1 = sdf.format(now);
    IPrintService printService = ServiceManager.getService(IPrintService.class);

    try {
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.DEBT);
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "balanceCount" + String.valueOf(WebUtil.getShopId(request));

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");

        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        context.put("dataStr", szDatetime1);
        context.put("customerDTO", customerDTO);
        context.put("debtDTOList", debtDTOList);
        context.put("payedAmount", payedAmount);
        context.put("totalAmount", totalAmount);
        context.put("totalAmountStr", MoneyUtil.toBigType(totalAmount));
        context.put("payedAmountStr", MoneyUtil.toBigType(payedAmount));
        context.put("shopDTO", shopDTO);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("id:" + customerId);
      WebUtil.reThrow(LOG, e);
    }
  }

  /*
  * 进入批量还款时间页面
  */
  @RequestMapping(params = "method=makeAllTime")
  public String makeAllTime(HttpServletRequest request, ModelMap model, @RequestParam("orderId") String orderId) {
    try {
      String[] orderIds = orderId.split(",");
      if (orderIds.length == 1) {
        //如果只有单条记录，获取一下还款时间
        Long shopId = (Long) request.getSession().getAttribute("shopId");
        model.addAttribute("debtRemindEventDTO", new DebtDTO());
      } else {
        model.addAttribute("debtRemindEventDTO", new DebtDTO());
      }
    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("method=makeAllTime");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("orderId:" + orderId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/makeAllTime";
  }

  /**
   * @param request
   * @param name
   * @return
   */
  @RequestMapping(params = "method=searchService")
  @ResponseBody
  public Object searchService(HttpServletRequest request, @RequestParam("name") String name, String uuid) {
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      List<SearchSuggestionDTO> searchSuggestionDTOList = ServiceManager.getService(ISearchSuggestionService.class).getRepairServiceSuggestion(shopId, name);
      if (StringUtils.isNotBlank(uuid)) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map> dropDownList = new ArrayList<Map>();
        if (CollectionUtils.isNotEmpty(searchSuggestionDTOList)) {
          for (SearchSuggestionDTO searchSuggestionDTO : searchSuggestionDTOList)
            dropDownList.add(searchSuggestionDTO.toStandardDropDownItemMap());
        }
        result.put("uuid", uuid);
        result.put("data", dropDownList);
        return result;
      } else {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (SearchSuggestionDTO searchSuggestionDTO : searchSuggestionDTOList) {
          result.add(searchSuggestionDTO.toRepairServiceDropDownItemMap());
        }
        return result;
      }
    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("method=searchService");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=getServiceByServiceName")
  @ResponseBody
  public List<ServiceDTO> getServiceByServiceName(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String serviceName = (String) request.getParameter("serviceName");
    try {
      List<ServiceDTO> serviceDTOs = txnService.getServiceByServiceNameAndShopId(shopId, serviceName);
      return serviceDTOs;
    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("method=getServiceByServiceName");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getServiceById")
  @ResponseBody
  public Object getServiceById(HttpServletRequest request, Long serviceId) {
    try {
      return txnService.getServiceById(serviceId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 批量结算
   */
  @ResponseBody
  @RequestMapping(params = "method=payAll")
  public Object payAll(ModelMap model, HttpServletRequest request, @RequestParam("customerId") String customerId,
                       @RequestParam("totalAmount") double totalAmount,
                       @RequestParam("payedAmount") double payedAmount,
                       @RequestParam("owedAmount") double owedAmount,
                       @RequestParam("orderIdsString") String orderIdsString,
                       @RequestParam("orderTypesString") String orderTypesString,
                       @RequestParam("receivableOrderIdsString") String receivableOrderIdsString,
                       @RequestParam("licenseNosString") String licenseNosString,
                       @RequestParam("orderTotalsString") String orderTotalsString,
                       @RequestParam("orderOwedsString") String orderOwedsString,
                       @RequestParam("orderPayedsString") String orderPayedsString,
                       @RequestParam("name") String name,
                       @RequestParam("phone") String phone,
                       @RequestParam("debtIdsString") String debtIdsString,
                       @RequestParam("huankuanTime") String huankuanTime) {
    Map<String, String> payResult = new HashMap<String, String>();

    StringBuffer debtArrearsInfo = new StringBuffer();//欠款结算信息

    try {
      IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      //得到店面信息
      ShopDTO shopDTO = configService.getShopById(new Long(shopId));

      //拼装欠款结算信息
      debtArrearsInfo.append("欠款结算信息:" + "shopId" + shopId + ",userId:" + request.getSession().getAttribute("userId") + ",customerId:" + customerId);
      debtArrearsInfo.append("totalAmount:" + totalAmount + "payedAmount:" + payedAmount + "owedAmount:" + owedAmount + "discount:" + NumberUtil.doubleValue(request.getParameter("discount"), 0));
      debtArrearsInfo.append("cash:" + NumberUtil.doubleValue(request.getParameter("cashAmount"), 0) + "bankAmount:" + NumberUtil.doubleValue(request.getParameter("bankAmount"), 0));
      debtArrearsInfo.append("bankCheckAmount:" + NumberUtil.doubleValue(request.getParameter("bankCheckAmount"), 0) + "memberAmount:" + NumberUtil.doubleValue(request.getParameter("memberAmount"), 0));
      debtArrearsInfo.append("bankCheckNo:" + request.getParameter("bankCheckNo") + "accountMemberNo:" + request.getParameter("accountMemberNo") + "accountMemberPassword:" + request.getParameter("accountMemberPassword"));
      debtArrearsInfo.append("depositAmount:" + NumberUtil.doubleValue(request.getParameter("depositAmount"), 0));
      debtArrearsInfo.append("orderIdsString:" + orderIdsString);
      debtArrearsInfo.append("orderTypesString:" + orderTypesString);
      debtArrearsInfo.append("receivableOrderIdsString:" + receivableOrderIdsString);
      debtArrearsInfo.append("licenseNosString:" + licenseNosString);
      debtArrearsInfo.append("orderTotalsString:" + orderTotalsString);
      debtArrearsInfo.append("orderOwedsString:" + orderOwedsString);
      debtArrearsInfo.append("orderPayedsString:" + orderPayedsString);
      debtArrearsInfo.append("name:" + name);
      debtArrearsInfo.append("phone:" + phone);
      debtArrearsInfo.append("debtIdsString:" + debtIdsString);
      debtArrearsInfo.append("huankuanTime:" + huankuanTime);

      request.setAttribute("debtArrearsInfo", debtArrearsInfo);

      if (StringUtils.isNotBlank(customerId)) {
        Customer customer = userService.getCustomerByCustomerId(Long.valueOf(customerId), shopId);
        if (null != customer && CustomerStatus.DISABLED.equals(customer.getStatus())) {
          payResult.put("result", "fail");
          payResult.put("failMsg", CustomerConstant.CUSTOMER_DISABLED_NO_SETTLE);
          return payResult;
        }
      }

      //校验会员余额是否正确
      String jsonStr = "";
      try {
        jsonStr = memberCheckerService.checkMemberBalance(shopId, request.getParameter("accountMemberNo"), request.getParameter("accountMemberPassword"),
          request.getParameter("memberAmount"));
      } catch (Exception e) {
        LOG.error("/txn.do method=payAll checkMemberBalance " + debtArrearsInfo);
        LOG.error(e.getMessage(), e);
        jsonStr = MemberConstant.AJAX_SUBMIT_FAILURE;
      }

      //校验按返回结果
      if (!MemberConstant.MEMBER_VALIDATE_SUCCESS.equals(jsonStr)) {
        LOG.error("/txn.do method=payAll checkMemberBalance 欠款结算失败,前台校验会员密码和余额失败 " + debtArrearsInfo);
        return "/";
      }

      //校验前台总和 实收 欠款  现金 银联 支票 会员储值  是否正确
      if (!memberCheckerService.checkDetailsArrearsInfo(totalAmount, payedAmount, owedAmount, request)) {
        LOG.error("/txn.do method=payAll checkDetailsArrearsInfo 欠款结算失败,实收和现金、银行卡、支票、会员储值、预收款不符 " + debtArrearsInfo);
        return "/";
      }

      String[] licenseNosArray = licenseNosString.split(",");
      double payedAmountOld = payedAmount;//保存付款金额，发短信时使用。
      OwedCheapIdenty owedCheapIdentity = OwedCheapIdenty.none;
      if (owedAmount > 0.001) { //有欠款
        if (Math.abs(totalAmount - payedAmount - owedAmount) < 0.000001) {
          owedCheapIdentity = OwedCheapIdenty.owedUncheap;//无优惠
        } else if (totalAmount > (payedAmount + owedAmount + 0.000001)) {
          owedCheapIdentity = OwedCheapIdenty.owedCheap;//有优惠
        }
      } else {  //无欠款
        if (Math.abs(totalAmount - payedAmount - owedAmount) < 0.000001) {
          owedCheapIdentity = OwedCheapIdenty.unowedUncheap; //无优惠
        } else if (totalAmount > (payedAmount + owedAmount + 0.000001)) {
          owedCheapIdentity = OwedCheapIdenty.unowedCheap;//有优惠
        }
      }

      boolean isPaySuccess = txnService.payDebt(totalAmount, payedAmount, owedAmount, receivableOrderIdsString, orderTotalsString,
        orderOwedsString, orderPayedsString, debtIdsString, huankuanTime, request, shopId);
      if (!isPaySuccess) {
        payResult.put("result", "fail");
        if (StringUtils.isNotBlank(customerId)) {
          List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(Long.valueOf(customerId));
          if (CollectionUtils.isNotEmpty(customerRecordDTOList)) {
            payResult.put("totalDebt", String.valueOf(NumberUtil.round(customerRecordDTOList.get(0).getTotalReceivable(), NumberUtil.MONEY_PRECISION)));
          }
        }
        return payResult;
      }
      payResult.put("result", "success");

      CustomerRecordDTO customerRecordDTO = userService.getShopCustomerRecordByCustomerId(shopId, new Long(customerId)).toDTO();
      customerRecordDTO.setTotalReceivable(customerRecordDTO.getTotalReceivable() - (totalAmount - owedAmount));
      customerRecordDTO.setTotalAmount(customerRecordDTO.getTotalAmount() - (totalAmount - owedAmount - payedAmount));
      customerRecordDTO.setRepayDate(StringUtils.isBlank(huankuanTime) ? null : DateUtil.convertDateStringToDateLong("yyyy-MM-dd", huankuanTime));   // BCSHOP-2562

      if (NumberUtil.doubleVal(request.getParameter("memberAmount")) > 0) {
        customerRecordDTO.setMemberConsumeTotal(NumberUtil.toReserve(NumberUtil.doubleVal(customerRecordDTO.getMemberConsumeTotal()) + NumberUtil.doubleVal(request.getParameter("memberAmount")), NumberUtil.MONEY_PRECISION));
        customerRecordDTO.setMemberConsumeTimes(NumberUtil.longValue(NumberUtil.longValue(customerRecordDTO.getMemberConsumeTimes())) + 1);
      }
      userService.updateCustomerRecord(customerRecordDTO);
      CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(customerRecordDTO.getCustomerId());
      if (customerDTO.getSupplierId() != null) {
        PayableDTO payableDTO = new PayableDTO();
        payableDTO.setAmount(totalAmount);
        payableDTO.setShopId(customerRecordDTO.getShopId());
        payableDTO.setCreditAmount(owedAmount);
        payableDTO.setPaidAmount(payedAmount);
        ServiceManager.getService(ISupplierPayableService.class).savePayable(payableDTO);
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierBySupplierId(customerDTO.getSupplierId());
      }
      if (owedCheapIdentity == OwedCheapIdenty.owedCheap || owedCheapIdentity == OwedCheapIdenty.unowedCheap) {
        //发短信给老板提醒有优惠
        try {
          smsService.sendCheapInfoMessageToBoss(totalAmount, payedAmount, owedAmount, name, phone, shopId, shopDTO,
            licenseNosArray, payedAmountOld, RfTxnConstant.FORMAT_CHINESE_YEAR_MONTH_DATE);
        } catch (Exception e) {
          LOG.error("/txn.do method=payAll sendCheapInfoMessageToBoss " + debtArrearsInfo);
          LOG.error(e.getMessage(), e);
        }
      }
      if (owedCheapIdentity == OwedCheapIdenty.owedCheap || owedCheapIdentity == OwedCheapIdenty.owedUncheap) {
        //发短信给老板提示有欠款
        try {
          smsService.sendOwedInfoMessageToBoss(totalAmount, owedAmount, name, phone, shopId, shopDTO,
            licenseNosArray, payedAmountOld, huankuanTime, RfTxnConstant.FORMAT_CHINESE_YEAR_MONTH_DATE);
        } catch (Exception e) {
          LOG.error("/txn.do method=payAll sendOwedInfoMessageToBoss " + debtArrearsInfo);
          LOG.error(e.getMessage(), e);
        }
      }
      //solr reindex
      if (StringUtils.isNotBlank(orderIdsString) && StringUtils.isNotBlank(orderTypesString)) {
        try {
          String[] orderIds = orderIdsString.split(",");
          String[] orderTypes = orderTypesString.split(",");
          for (int i = 0; i < orderIds.length; i++) {
            ServiceManager.getService(OrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), OrderTypes.valueOf(orderTypes[i]), Long.valueOf(orderIds[i]));
            //ad by WLF 保存欠款结算的日志
            ObjectTypes objectType = null;
            if (orderTypes[i].equals(OrderTypes.REPAIR.toString())) {
              objectType = ObjectTypes.REPAIR_ORDER;
            } else if (orderTypes[i].equals(OrderTypes.SALE.toString())) {
              objectType = ObjectTypes.SALE_ORDER;
            } else if (orderTypes[i].equals(OrderTypes.WASH.toString()) || orderTypes[i].equals(OrderTypes.WASH_BEAUTY.toString()) || orderTypes[i].equals(OrderTypes.WASH_MEMBER.toString())) {
              objectType = ObjectTypes.WASH_ORDER;
            } else if (orderTypes[i].equals(OrderTypes.MEMBER_BUY_CARD.toString())) {
              objectType = ObjectTypes.MEMBER_CARD_BUY_ORDER;
            }
            ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
              new OperationLogDTO(shopId, (Long) request.getSession().getAttribute("userId"), Long.valueOf(orderIds[i]), objectType, OperationTypes.SETTLE));
          }
        } catch (Exception e) {
          LOG.error("/txn.do method=payAll reCreateOrderSolrIndex " + debtArrearsInfo);
          LOG.error(e.getMessage(), e);
        }
      }

      ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(Long.valueOf(customerId));
      model.addAttribute("customerId", customerId);
    } catch (Exception ex) {
      LOG.error("/txn.do method=payAll " + debtArrearsInfo);
      LOG.error(ex.getMessage(), ex);
    }
    if (StringUtils.isNotBlank(customerId)) {
      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(Long.valueOf(customerId));
      if (CollectionUtils.isNotEmpty(customerRecordDTOList)) {
        payResult.put("totalDebt", String.valueOf(NumberUtil.round(customerRecordDTOList.get(0).getTotalReceivable(), NumberUtil.MONEY_PRECISION)));
      }
    }
    return payResult;
  }

  @RequestMapping(params = "method=getProducts")
  public String getProducts(ModelMap model, HttpServletRequest request) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      String productIds2 = request.getParameter("productIds");
      String[] productIds = productIds2.split(",");

      if (productIds != null && productIds.length > 0) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int arrayLength = 0; arrayLength < productIds.length; arrayLength++) {
          if (com.bcgogo.utils.NumberUtil.isNumber(productIds[arrayLength])) {
            stringBuilder.append(productIds[arrayLength]).append(",");
          }
        }
        productIds = stringBuilder.toString().split(",");
      }

      Long[] productIds1 = new Long[productIds.length];
      double repairOrderTotal = 0d;
      for (int i = 0; i < productIds1.length; i++) {
        if (!com.bcgogo.utils.NumberUtil.isNumber(productIds[i])) {
          continue;
        }
        productIds1[i] = Long.valueOf(productIds[i]);
      }
      RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
      repairOrderDTO.setServiceType(OrderTypes.REPAIR);
      if (productIds != null && productIds.length > 0) {
        List<InventorySearchIndex> inventorySearchIndexes = searchService.searchInventorySearchIndexByProductIds(shopId, productIds1);
        List<RepairOrderItemDTO> repairOrderItemDTOList = new ArrayList<RepairOrderItemDTO>();
        for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
          RepairOrderItemDTO itemDTO = new RepairOrderItemDTO();
          ProductDTO productDTO = productService.getProductByProductLocalInfoId(inventorySearchIndex.getProductId(), shopId);

          ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(inventorySearchIndex.getProductId(), shopId);

          if (null != productLocalInfoDTO && null != productLocalInfoDTO.getBusinessCategoryId()) {
            Category category = rfiTxnService.getEnabledCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());
            if (null != category) {
              itemDTO.setBusinessCategoryId(category.getId());
              itemDTO.setBusinessCategoryName(category.getCategoryName());
            }
          }

          itemDTO.setProductType(inventorySearchIndex.getProductVehicleStatus());
          itemDTO.setProductId(inventorySearchIndex.getProductId());
          itemDTO.setProductName(inventorySearchIndex.getProductName());
          itemDTO.setBrand(inventorySearchIndex.getProductBrand());
          itemDTO.setSpec(inventorySearchIndex.getProductSpec());
          itemDTO.setModel(inventorySearchIndex.getProductModel());
          itemDTO.setCommodityCode(inventorySearchIndex.getCommodityCode());
          itemDTO.setAmount(RfTxnConstant.ORDER_DEFAULT_AMOUNT);
          itemDTO.setInventoryAmount(inventorySearchIndex.getAmount());
          itemDTO.setPrice(inventorySearchIndex.getRecommendedPrice() == null ? 0d : inventorySearchIndex.getRecommendedPrice());
          itemDTO.setUnit(inventorySearchIndex.getUnit());
          itemDTO.setTotal(itemDTO.getAmount() == null ? 0d : itemDTO.getAmount() * itemDTO.getPrice());
          itemDTO.setReserved(0d);

          itemDTO.setStorageUnit(productDTO.getStorageUnit());
          itemDTO.setSellUnit(productDTO.getSellUnit());
          itemDTO.setRate(productDTO.getRate());
          itemDTO.setTradePrice(productDTO.getTradePrice());
          itemDTO.setStorageBin(productDTO.getStorageBin());
          double pp = inventorySearchIndex.getPurchasePrice() == null ? 0d : inventorySearchIndex.getPurchasePrice();
          itemDTO.setPurchasePrice(pp);
          repairOrderTotal += itemDTO.getTotal();
          repairOrderItemDTOList.add(itemDTO);
        }
        RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderItemDTOList.toArray(new RepairOrderItemDTO[repairOrderItemDTOList.size()]);
        repairOrderDTO.setItemDTOs(repairOrderItemDTOs);
        repairOrderTotal = com.bcgogo.utils.NumberUtil.round(repairOrderTotal, NumberUtil.MONEY_PRECISION);
        repairOrderDTO.setTotal(repairOrderTotal);
      }
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      long curTime = System.currentTimeMillis();
      String time = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
      repairOrderDTO.setStartDateStr(time);
      repairOrderDTO.setEndDateStr(time);
      repairOrderDTO.setStartDate(curTime);
      model.addAttribute("fuelNumberList", fuelNumberList);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
        List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
        model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
        if (CollectionUtils.isNotEmpty(storeHouseDTOList) && repairOrderDTO.getStorehouseId() == null) {
          if (storeHouseDTOList.size() == 1) {
            repairOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
          }
        }
        InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
        //更新库存 根据仓库
        inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, repairOrderDTO.getStorehouseId(), repairOrderDTO);
      }
      repairOrderDTO.initDefaultItemUnit(ProductUnitCache.getProductUnitMap());
      model.addAttribute("repairOrderDTO", repairOrderDTO);
    } catch (Exception e) {
      LOG.debug("/txn.do&method=getProducts\n" + "shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/txn/invoicing";
  }

  @RequestMapping(params = "method=chooseProductType")
  public String chooseProductType(ModelMap model, HttpServletRequest request, @RequestParam("id") String id,
                                  @RequestParam("isSubmit") String isSubmit, @RequestParam("flag") String flag) {
    model.addAttribute("id", id);
    model.addAttribute("isSubmit", isSubmit);
    model.addAttribute("flag", flag);
    return "/txn/chooseProductType";
  }

  // 客户搜索
  @RequestMapping(params = "method=getCustomerName")
  @ResponseBody
  public List getCustomer(ModelMap model, HttpServletRequest request, String name) {
    Long shopId = null;
    if (request.getSession().getAttribute("shopId") != null) {
      shopId = (Long) request.getSession().getAttribute("shopId");
    }
//        获得搜索关键字首字字符
    char keyword = ' ';
    try {
      if (StringUtils.isNotBlank(name)) {
        keyword = name.charAt(0);
//         如果首字符是汉字按照汉字查询
        if (keyword >= 0x0391 && keyword <= 0xFFE5)
          return userService.getCustomer(name, shopId);
        else
//        如果首字符是字母按照字母查询
          // 如果存在客户，则组成JSON
          return userService.getCustomerByZiMu(name, shopId);
      }
    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("method=getCustomerName");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("name:" + name);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=setSale")
  public String setSale() {
    return "txn/setSale";
  }

  @RequestMapping(params = "method=gotoSetUnitPage")
  public String gotoSetUnitPage(ModelMap model, HttpServletRequest request) {
    model.put("unitId", request.getParameter("unitId"));
    return "txn/setUnit";
  }

  @RequestMapping(params = "method=setSellUnitAndRate")
  public void setSellUnitAndRate(HttpServletRequest request, HttpServletResponse response, Long productId,
                                 String storageUnit, String sellUnit, Long rate) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        LOG.debug("shop not found");
      }
      Long userId = WebUtil.getUserId(request);
      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(productId, shopId);
      ProductModifyLogDTO beforeModifyLogDTO = new ProductModifyLogDTO();
      beforeModifyLogDTO.setStorageUnit(productLocalInfoDTO.getStorageUnit());
      beforeModifyLogDTO.setSellUnit(productLocalInfoDTO.getSellUnit());
      if (!productLocalInfoDTO.getSellUnit().equals(productLocalInfoDTO.getStorageUnit())) {
        String jsonStr = "[{\"result\":\"fail\"}]";
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
        LOG.debug("sellUnit storageUnit is exit");
        return;
      }
      ProductLocalInfoDTO newProductLocalInfoDTO = productLocalInfoDTO.clone();
      newProductLocalInfoDTO.setStorageUnit(storageUnit);
      newProductLocalInfoDTO.setSellUnit(sellUnit);
      newProductLocalInfoDTO.setRate(rate);
      ProductModifyLogDTO afterModifyLogDTO = new ProductModifyLogDTO();
      afterModifyLogDTO.setStorageUnit(storageUnit);
      afterModifyLogDTO.setSellUnit(sellUnit);
      List<ProductModifyLogDTO> logs = ProductModifyLogDTO.compare(beforeModifyLogDTO, afterModifyLogDTO);
      for (ProductModifyLogDTO logDTO : logs) {
        logDTO.setProductId(productId);
        logDTO.setShopId(shopId);
        logDTO.setUserId(userId);
        logDTO.setStatProcessStatus(StatProcessStatus.NEW);
        logDTO.setOperationType(ProductModifyOperations.TXN_SET_SECOND_UNIT);
      }

      //标准化商品关联属性修改记录
      Product product = productService.getProductById(productLocalInfoDTO.getProductId());
      if (ProductRelevanceHelper.existRelevancePropertyModify(product.getRelevanceStatus(), logs.toArray(new ProductModifyLogDTO[logs.size()]))) {
        for (ProductModifyLogDTO modifyLogDTO : logs) {
          if (!ProductRelevanceHelper.existRelevanceProperty(modifyLogDTO)) {
            continue;
          }
          modifyLogDTO.setRelevanceStatus(ProductRelevanceStatus.UN_CHECKED);
        }
      }
      txnService.batchCreateProductModifyLog(logs);

      if (ProductRelevanceHelper.existRelevancePropertyModify(product.getRelevanceStatus(), logs.toArray(new ProductModifyLogDTO[logs.size()]))) {
        List<ProductModifyFields> productModifyFieldsList = txnService.getRelevanceStatusUnCheckedProductModifiedFieldsMap(productLocalInfoDTO.getId()).get(productLocalInfoDTO.getId());
        if (CollectionUtils.isNotEmpty(productModifyFieldsList)) {
          productService.updateProductRelevanceStatus(product.getId(), ProductRelevanceStatus.UN_CHECKED);
        } else {
          productService.updateProductRelevanceStatus(product.getId(), ProductRelevanceStatus.YES);
          txnService.updateProductModifyLogDTORelevanceStatus(productLocalInfoDTO.getId(), ProductRelevanceStatus.YES);
        }
      }

      InventoryDTO inventoryDTO = txnService.getInventoryAmount(shopId, productId);
      Long[] productIds = new Long[1];
      productIds[0] = productId;
      List<InventorySearchIndex> inventorySearchIndexs = searchService.searchInventorySearchIndexByProductIds(shopId, productIds);
      InventorySearchIndex inventorySearchIndex = new InventorySearchIndex();
      if (inventorySearchIndexs != null && inventorySearchIndexs.size() > 0) {
        inventorySearchIndex = inventorySearchIndexs.get(0);
      } else {
        LOG.debug("inventroySearchIndex Not found");
      }
      List<InventorySearchIndexDTO> inventorySearchIndexDTOJsonList = new ArrayList<InventorySearchIndexDTO>();
      //大单位化成小单位    update productLocalInfo inventroy  inventroySearchIndex productSolr
      if (productLocalInfoDTO.getStorageUnit().equals(storageUnit) && !productLocalInfoDTO.getSellUnit().equals(sellUnit)) {
        newProductLocalInfoDTO.setPrice(newProductLocalInfoDTO.getPrice() == null ? 0 : newProductLocalInfoDTO.getPrice() / rate);
        newProductLocalInfoDTO.setPurchasePrice(newProductLocalInfoDTO.getPurchasePrice() == null ?
          0 : newProductLocalInfoDTO.getPurchasePrice() / rate);
        newProductLocalInfoDTO.setTradePrice(newProductLocalInfoDTO.getTradePrice() == null ?
          0 : newProductLocalInfoDTO.getTradePrice() / rate);
        newProductLocalInfoDTO.setInSalesPrice(newProductLocalInfoDTO.getInSalesPrice() == null ? 0 : newProductLocalInfoDTO.getInSalesPrice() / rate);
        newProductLocalInfoDTO.setInSalesAmount(newProductLocalInfoDTO.getInSalesAmount() == null ? 0 : newProductLocalInfoDTO.getInSalesAmount() * rate);
        productService.updateProductLocalInfo(newProductLocalInfoDTO);
        inventoryDTO.setUnit(sellUnit);
        inventoryDTO.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice() == null ? null : inventoryDTO.getInventoryAveragePrice() / rate);
        inventoryDTO.setLatestInventoryPrice(inventoryDTO.getLatestInventoryPrice() == null ? null : inventoryDTO.getLatestInventoryPrice() / rate);
        inventoryDTO.setSalesPrice(inventoryDTO.getSalesPrice() == null ? null : inventoryDTO.getSalesPrice() / rate);

        inventoryDTO.setAmount(inventoryDTO.getAmount() * rate);
        inventoryDTO.setLowerLimit(inventoryDTO.getLowerLimit() == null ? null : inventoryDTO.getLowerLimit() * rate);
        inventoryDTO.setUpperLimit(inventoryDTO.getUpperLimit() == null ? null : inventoryDTO.getUpperLimit() * rate);


        txnService.createOrUpdateInventory(inventoryDTO);
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          List<StoreHouseInventoryDTO> storeHouseInventoryDTOList = storeHouseService.getStoreHouseInventoryDTOByProductIds(shopId, productId);
          if (CollectionUtils.isNotEmpty(storeHouseInventoryDTOList)) {
            for (StoreHouseInventoryDTO storeHouseInventoryDTO : storeHouseInventoryDTOList) {
              storeHouseInventoryDTO.setAmount(storeHouseInventoryDTO.getAmount() * rate);
              storeHouseService.saveOrUpdateStoreHouseInventoryDTO(storeHouseInventoryDTO);
            }
          }
        }
        List<SupplierInventoryDTO> supplierInventoryDTOs = productThroughService.getSupplierInventoryDTOsByProductId(shopId, productId);
        if (CollectionUtils.isNotEmpty(supplierInventoryDTOs)) {
          for (SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs) {
            supplierInventoryDTO.setUnit(sellUnit);
            if (NumberUtil.doubleVal(supplierInventoryDTO.getTotalInStorageAmount()) > 0.0001) {
              supplierInventoryDTO.setTotalInStorageAmount(NumberUtil.round(NumberUtil.doubleVal(supplierInventoryDTO.getTotalInStorageAmount()) * rate, 2));
            }
            if (NumberUtil.doubleVal(supplierInventoryDTO.getRemainAmount()) > 0.0001) {
              supplierInventoryDTO.setRemainAmount(NumberUtil.round(NumberUtil.doubleVal(supplierInventoryDTO.getRemainAmount()) * rate, 2));
            }
            if (NumberUtil.doubleVal(supplierInventoryDTO.getLastStorageAmount()) > 0.0001) {
              supplierInventoryDTO.setLastStorageAmount(NumberUtil.round(NumberUtil.doubleVal(supplierInventoryDTO.getLastStorageAmount()) * rate, 2));
            }

            if (NumberUtil.doubleVal(supplierInventoryDTO.getMaxStoragePrice()) > 0.0001) {
              supplierInventoryDTO.setMaxStoragePrice(NumberUtil.round(supplierInventoryDTO.getMaxStoragePrice() / rate, 2));
            }
            if (NumberUtil.doubleVal(supplierInventoryDTO.getMinStoragePrice()) > 0.0001) {
              supplierInventoryDTO.setMinStoragePrice(NumberUtil.round(supplierInventoryDTO.getMinStoragePrice() / rate, 2));
            }
            if (NumberUtil.doubleVal(supplierInventoryDTO.getLastStoragePrice()) > 0.0001) {
              supplierInventoryDTO.setLastStoragePrice(NumberUtil.round(supplierInventoryDTO.getLastStoragePrice() / rate, 2));
            }
            if (NumberUtil.doubleVal(supplierInventoryDTO.getAverageStoragePrice()) > 0.0001) {
              supplierInventoryDTO.setAverageStoragePrice(NumberUtil.round(NumberUtil.doubleVal(supplierInventoryDTO.getAverageStoragePrice()) / rate, 2));
            }
          }
          productThroughService.saveOrUpdateSupplierInventoryByModify(supplierInventoryDTOs);
        }

        inventorySearchIndex.setUnit(sellUnit);
        inventorySearchIndex.setAmount(inventorySearchIndex.getAmount() == null ? 0D : inventorySearchIndex.getAmount() * rate);
        inventorySearchIndex.setPrice(inventorySearchIndex.getPrice() == null ? 0D : inventorySearchIndex.getPrice() / rate);
        inventorySearchIndex.setPurchasePrice(inventorySearchIndex.getPurchasePrice() == null ?
          0D : inventorySearchIndex.getPurchasePrice() / rate);
        inventorySearchIndex.setRecommendedPrice(inventorySearchIndex.getRecommendedPrice() == null ?
          0D : inventorySearchIndex.getRecommendedPrice() / rate);
        inventorySearchIndex.setLowerLimit(inventorySearchIndex.getLowerLimit() == null ? null : inventorySearchIndex.getLowerLimit() * rate);
        inventorySearchIndex.setUpperLimit(inventorySearchIndex.getUpperLimit() == null ? null : inventorySearchIndex.getUpperLimit() * rate);
        inventorySearchIndex.setInventoryAveragePrice(inventorySearchIndex.getInventoryAveragePrice() == null ? null : inventorySearchIndex.getInventoryAveragePrice() / rate);
        List<InventorySearchIndex> newInventorySearchIndexs = new ArrayList<InventorySearchIndex>();
        newInventorySearchIndexs.add(inventorySearchIndex);
        searchService.updateInventorySearchIndexAmountWithList(newInventorySearchIndexs);

        InventorySearchIndexDTO inventorySearchIndexDTO = inventorySearchIndex.toDTO();
        inventorySearchIndexDTO.setSellUnit(sellUnit);
        inventorySearchIndexDTO.setStorageUnit(storageUnit);
        inventorySearchIndexDTO.setRate(rate);
        inventorySearchIndexDTO.setLowerLimit(inventoryDTO.getLowerLimit());
        inventorySearchIndexDTO.setUpperLimit(inventoryDTO.getUpperLimit());

        //
        productSolrWriterService.createProductSolrIndex(shopId, productId);
        inventorySearchIndexDTOJsonList.add(inventorySearchIndexDTO);
      }
      //小单位化成大单位  库存仍用小单位  update productLocalInfo, productSolr
      else if (!productLocalInfoDTO.getStorageUnit().equals(storageUnit) && productLocalInfoDTO.getSellUnit().equals(sellUnit)) {
        productService.updateProductLocalInfo(newProductLocalInfoDTO);

        InventorySearchIndexDTO inventorySearchIndexDTO = inventorySearchIndex.toDTO();
        inventorySearchIndexDTO.setSellUnit(sellUnit);
        inventorySearchIndexDTO.setStorageUnit(storageUnit);
        inventorySearchIndexDTO.setRate(rate);
        productSolrWriterService.createProductSolrIndex(shopId, productId);
        inventorySearchIndexDTO.setLowerLimit(inventoryDTO.getLowerLimit());
        inventorySearchIndexDTO.setUpperLimit(inventoryDTO.getUpperLimit());
        inventorySearchIndexDTOJsonList.add(inventorySearchIndexDTO);
      } else {
        String jsonStr = "[{\"result\":\"fail\"}]";
        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.close();
        LOG.debug("update unit fail");
        return;
      }
      String jsonStr;
      jsonStr = JsonUtil.listToJson(inventorySearchIndexDTOJsonList);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1) + ",{\"result\":\"success\"}]";
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();

    } catch (Exception e) {
      LOG.debug("/txn.do , method=setSellUnitAndRate ,shopId:" + request.getSession().getAttribute("shopId") +
        "productId: " + productId + "storageUnit :" + storageUnit + "sellUnit :" + sellUnit + "rate :" + rate);
      LOG.error(e.getMessage(), e);
    }

  }

  //AJAX查询洗车单信息
  @RequestMapping(params = "method=queryWashOrder")
  public void queryWashOrder(HttpServletRequest request, HttpServletResponse response, ModelMap model, Long customerId) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    List<CustomerCardDTO> customerCardDTOs = null;
    try {
      if (customerId != null) {
        customerCardDTOs = userService
          .getCustomerCardByCustomerIdAndCardType(shopId, customerId, WashCardConstants.CARD_TYPE_DEFAULT);
      }
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.listToJson(customerCardDTOs));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/txn.do ,method=queryWashOrder,shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * @param request
   * @param response
   * @param id       productLocalInfo id
   * @param price
   * @throws Exception
   */
  @RequestMapping(params = "method=ajaxUpdateRecommendedPrice")
  public void ajaxUpdateRecommendedPrice(HttpServletRequest request, HttpServletResponse response, String id, String price) throws Exception {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (price == null || "".equals(price)) {
        price = "0";
      }
      InventoryDTO inventoryDTO = new InventoryDTO();
      inventoryDTO.setId(Long.parseLong(id));
      inventoryDTO.setSalesPrice(Double.parseDouble(price));
      ServiceManager.getService(IInventoryService.class).updateInventoryInfo(shopId, inventoryDTO, null);
      ServiceManager.getService(ITxnService.class).checkAndInsertInventorySearchIndex(Long.parseLong(id), shopId);
      ServiceManager.getService(IInventoryService.class).updateInventorySearchIndexByProductId(shopId, Long.parseLong(id), Double.parseDouble(price));
      PrintWriter printWriter = response.getWriter();
      printWriter.write("{}");
      printWriter.close();
    } catch (Exception e) {
      LOG.error("txn.do?method=ajaxUpdateRecommendedPrice\n" +
        "id:" + id + "\n" +
        "price:" + price + "\n" +
        e.getMessage(), e);
    }
  }

  //显示批量设定上下限界面
  @RequestMapping(params = "method=showSetLimtPage")
  public String showSetLimtPage(ModelMap model, String task) {
    model.put("task", task);
    return "txn/setLimit";
  }

  @RequestMapping(params = "method=validateCopyRepairOrder")
  @ResponseBody
  public Result validateCopy(ModelMap model, HttpServletRequest request, String repairOrderId) {
    IRepairService repairService = ServiceManager.getService(IRepairService.class);

    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || repairOrderId == null) {
        LOG.error("txn.do?method=validateCopy, shopId:{}, repairOrderId:{}", shopId, repairOrderId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      return repairService.validateCopy(Long.parseLong(repairOrderId), shopId);
    } catch (Exception e) {
      LOG.error("txn.do?method=validateCopy. shopId:{}, userId:{}, repairOrderId:{}", new Object[]{shopId, userId, repairOrderId});
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  @RequestMapping(params = "method=getCopyRepairOrder")
  public String copy(ModelMap model, HttpServletRequest request, HttpServletResponse response, String repairOrderId) {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IProductHistoryService productHistoryService = ServiceManager.getService(IProductHistoryService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    getRepairOrderInfo(model, request, repairOrderId);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    RepairOrderDTO repairOrderDTO = (RepairOrderDTO) model.get("repairOrderDTO");
    try {
      repairOrderDTO = repairOrderDTO.clone();
      if (repairOrderDTO != null && repairOrderDTO.getId() != null && (OrderStatus.REPAIR_REPEAL == repairOrderDTO.getStatus() || OrderStatus.REPAIR_SETTLED == repairOrderDTO.getStatus())) {
        LOG.debug("copy from repair order {} ", repairOrderId);
        long curTime = System.currentTimeMillis();
        String curTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, curTime);
        repairOrderDTO.setId(null);
        repairOrderDTO.setIdStr("");
        repairOrderDTO.setStatus(null);
        repairOrderDTO.setOrderStatus(null);
        repairOrderDTO.setEditDate(null);
        repairOrderDTO.setDraftOrderIdStr(null);
        repairOrderDTO.setOrderDiscount(0d);
        repairOrderDTO.setCashAmount(null);
        repairOrderDTO.setBankAmount(null);
        repairOrderDTO.setBankCheckAmount(null);
        repairOrderDTO.setBankCheckNo(null);
        repairOrderDTO.setAccountMemberNo(null);
        repairOrderDTO.setAccountMemberPassword(null);
        repairOrderDTO.setMemberAmount(null);
        repairOrderDTO.setHuankuanTime(null);
        repairOrderDTO.setReceivableId(null);
        repairOrderDTO.setSettledAmount(0d);
        repairOrderDTO.setSettledAmountHid(0d);
        repairOrderDTO.setDebt(0d);
        repairOrderDTO.setDebtHid(0d);
        repairOrderDTO.setVestDate(null);
        repairOrderDTO.setVestDateStr(null);
        repairOrderDTO.setSettleDate(null);
        repairOrderDTO.setSettleDateStr(null);
        repairOrderDTO.setEndDate(null);
        repairOrderDTO.setEndDateStr(null);
        repairOrderDTO.setStartDate(curTime);
        repairOrderDTO.setStartDateStr(curTimeStr);

        //如果客户/车辆信息被修改，不复制相关内容
        if (!customerService.compareCustomerSameWithHistory(repairOrderDTO.generateCustomerDTO(), shopId)
          || !vehicleService.compareVehicleSameWithHistory(repairOrderDTO.generateVehicleDTO(), shopId)) {
          repairOrderDTO.clearCustomerInfo();
          model.remove("customerDTO");
          model.remove("customerRecordDTO");
          repairOrderDTO.clearVehicleInfo();
          model.remove("vehicleDTO");
          repairOrderDTO.setMemberNo(null);
          repairOrderDTO.setMemberStatus(null);
          repairOrderDTO.setMemberType(null);
          repairOrderDTO.setMemberRemainAmount(null);
          model.remove(TOTAL_RECEIVABLE);
          model.remove(TOTAL_CONSUME);
        } else {
          VehicleDTO vehicleDTO = userService.getVehicleById(repairOrderDTO.getVechicleId());
          repairOrderDTO.setVehicleDTO(vehicleDTO);
          CustomerDTO customerDTO = userService.getCustomerById(repairOrderDTO.getCustomerId());
          repairOrderDTO.setCustomerDTO(customerDTO);
        }

        RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
        if (itemDTOs != null) {
          RepairOrderItemDTO[] newItemDTOs = new RepairOrderItemDTO[0];
          RepairOrderItemDTO newItemDTO = null;
          for (int i = 0; i < itemDTOs.length; i++) {
            newItemDTO = itemDTOs[i];
            if (!productHistoryService.compareProductSameWithHistory(newItemDTO.getProductId(), newItemDTO.getProductHistoryId(), shopId)) {
              continue;
            }
            newItemDTO.setId(null);
            newItemDTO.setIdStr("");
            newItemDTO.setRepairOrderId(null);
            newItemDTO.setReserved(0d);

            if (null != newItemDTO.getProductId()) {

              ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(newItemDTO.getProductId(), shopId);
              if (productLocalInfoDTO != null) {
                newItemDTO.setSellUnit(productLocalInfoDTO.getSellUnit());
                newItemDTO.setStorageUnit(productLocalInfoDTO.getStorageUnit());
                newItemDTO.setRate(productLocalInfoDTO.getRate());
              }
              Category category = rfiTxnService.getEnabledCategoryById(shopId, productLocalInfoDTO.getBusinessCategoryId());

              if (null == category) {
                newItemDTO.setBusinessCategoryId(null);
                newItemDTO.setBusinessCategoryName(null);
              } else {
                newItemDTO.setBusinessCategoryName(category.getCategoryName());
                newItemDTO.setBusinessCategoryId(category.getId());
              }

            }
            newItemDTOs = (RepairOrderItemDTO[]) ArrayUtils.add(newItemDTOs, newItemDTO);
          }
          repairOrderDTO.setItemDTOs(newItemDTOs);
        }
        RepairOrderServiceDTO[] serviceDTOs = repairOrderDTO.getServiceDTOs();
        if (serviceDTOs != null) {
          RepairOrderServiceDTO[] newServiceDTOs = new RepairOrderServiceDTO[0];
          RepairOrderServiceDTO newServiceDTO = null;
          for (int i = 0; i < serviceDTOs.length; i++) {
            newServiceDTO = serviceDTOs[i];
            if (!serviceHistoryService.compareServiceSameWithHistory(newServiceDTO.getServiceId(), newServiceDTO.getServiceHistoryId(), shopId)) {
              continue;
            }
            newServiceDTO.setId(null);
            newServiceDTO.setIdStr("");
            newServiceDTO.setRepairOrderId(null);

            if (null != newServiceDTO.getServiceId()) {
              CategoryDTO categoryDTO = rfiTxnService.getCateGoryByServiceId(shopId, newServiceDTO.getServiceId());
              if (null == categoryDTO) {
                newServiceDTO.setBusinessCategoryId(null);
                newServiceDTO.setBusinessCategoryName(null);
              } else {
                newServiceDTO.setBusinessCategoryName(categoryDTO.getCategoryName());
                newServiceDTO.setBusinessCategoryId(categoryDTO.getId());
              }
              ServiceDTO serviceDTO = txnService.getServiceById(newServiceDTO.getServiceId());
              newServiceDTO.setStandardHours(serviceDTO.getStandardHours());
              newServiceDTO.setStandardUnitPrice(serviceDTO.getStandardUnitPrice());

              if (newServiceDTO.getActualHours() != null && newServiceDTO.getStandardUnitPrice() != null) {
                newServiceDTO.setTotal(NumberUtil.toReserve(NumberUtil.doubleVal(newServiceDTO.getActualHours()) * newServiceDTO.getStandardUnitPrice(), NumberUtil.PRECISION));
              } else {
                newServiceDTO.setTotal(serviceDTO.getPrice());
              }
            }

            newServiceDTOs = (RepairOrderServiceDTO[]) ArrayUtils.add(newServiceDTOs, newServiceDTO);
          }
          repairOrderDTO.setServiceDTOs(newServiceDTOs);
        }
        repairOrderDTO.setReceiptNo(null);

        if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
          for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
            itemDTO.setOrderId(null);
            itemDTO.setId(null);
          }
        }

        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          List<StoreHouseDTO> storeHouseDTOList = storeHouseService.getAllStoreHousesByShopId(shopId);
          model.addAttribute("storeHouseDTOList", storeHouseDTOList);//select 选项
          if (repairOrderDTO.getStorehouseId() != null) {
            StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, repairOrderDTO.getStorehouseId());
            if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
              repairOrderDTO.setStorehouseId(null);
              repairOrderDTO.setStorehouseName(null);
            }
          }
          if (CollectionUtils.isNotEmpty(storeHouseDTOList) && repairOrderDTO.getStorehouseId() == null) {
            if (storeHouseDTOList.size() == 1) {
              repairOrderDTO.setStorehouseId(storeHouseDTOList.get(0).getId());
            }
          }
          InventoryService inventoryService = ServiceManager.getService(InventoryService.class);
          //更新库存 根据仓库
          inventoryService.updateItemDTOInventoryAmountByStorehouse(shopId, repairOrderDTO.getStorehouseId(), repairOrderDTO);
        }
        repairOrderDTO.calculateTotal();
        model.addAttribute("repairOrderDTO", repairOrderDTO);
      }
    } catch (Exception e) {
      LOG.error("施工单复制出错, shopID:{}, repairOrderID:{}", shopId, repairOrderId);
      LOG.error(e.getMessage(), e);
    }
    return "/txn/invoicing";
  }

  @RequestMapping(params = "method=repairOrderRepeal")
  public String repairOrderRepeal(ModelMap model, HttpServletRequest request, HttpServletResponse response, String repairOrderId, Long toStorehouseId) {
    StopWatchUtil sw = new StopWatchUtil("repairOrderRepeal", "getOrder");
    IItemIndexService itemIndexService = ServiceManager.getService(ItemIndexService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    RepairOrderDTO repairOrderDTO = new RepairOrderDTO();
    model.addAttribute("btnType", "repeal");
    Long shopId = WebUtil.getShopId(request);
    String lockKey = ConcurrentScene.WEB_REPAIR_ORDER_REPEAL.getName() + String.valueOf(shopId) + "_" + repairOrderId;
    try {
      if (!BcgogoConcurrentController.lock(ConcurrentScene.WEB_REPAIR_ORDER_REPEAL, lockKey)) {
        LOG.warn("repairOrderRepeal has been handling,customerId is {}", repairOrderId);
        model.put("result", new PageErrorMsg("删除操作正在执行", "请稍后再试"));
        return Constant.PAGE_ERROR;
      }
      if (StringUtils.isBlank(repairOrderId)) {
        model = new ModelMap();
        request.setAttribute("task", "maintain");
        return getRepairOrderByVehicleNumber(model, request, response);
      } else {
        repairOrderDTO = getRepairOrderInfo(model, request, repairOrderId);
        if (repairOrderDTO == null || repairOrderDTO.getId() == null) {
          model = new ModelMap();
          request.setAttribute("task", "maintain");
          return getRepairOrderByVehicleNumber(model, request, response);
        }
      }
      sw.stopAndStart("validateStrike");
      if (repairOrderDTO != null && repairOrderDTO.getStatus() != null && OrderStatus.REPAIR_REPEAL != repairOrderDTO.getStatus() && repairOrderDTO.getStatementAccountOrderId() == null) {
        if (shopId != null && shopId >= 0) {
          repairOrderDTO.setShopId(shopId);
        }
        repairOrderDTO.setEditDate(System.currentTimeMillis());
        repairOrderDTO.setEditor(WebUtil.getUserName(request));
        repairOrderDTO.setEditorId(WebUtil.getUserId(request));
        repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
        ReceivableDTO strikeReceivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.REPAIR, repairOrderDTO.getId());
        if (strikeReceivableDTO != null && strikeReceivableDTO.getStrike() != null && strikeReceivableDTO.getStrike() > 0) {
          WebUtil.addSimpleJsMsg(model, new Result("作废失败", "该单据已被冲帐结算，无法作废。", false));
          return getRepairOrder(model, request, repairOrderDTO.getId().toString());
        }
        sw.stopAndStart("updateProduct");
        //更新已经被删除的商品，状态的值置空
        rfiTxnService.updateDeleteProductsByOrderDTO(repairOrderDTO);
        repairOrderDTO.setInventoryLimitDTO(new InventoryLimitDTO());
        repairOrderDTO.getInventoryLimitDTO().setShopId(shopId);
        sw.stopAndStart("updateCustomerRecord");
        rfiTxnService.updateCustomerRecordByShopIdAndOrderId(shopId, repairOrderDTO.getId(), repairOrderDTO.getCustomerId(), OrderTypes.REPAIR);

        sw.stopAndStart("repealMain");
        getRepairService().repealRepairOrder(repairOrderDTO, toStorehouseId);

        sw.stopAndStart("vehicleServeStat");

        //车辆服务统计
        if (repairOrderDTO.getServiceDTOs() != null && repairOrderDTO.getServiceDTOs().length > 0) {
          List<ServiceVehicleCount> serviceTimes = serviceVehicleCountService.getServiceVehicleCountByTime(shopId,
            Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(repairOrderDTO.getStartDate())));
          if (serviceTimes != null) {
            for (ServiceVehicleCount s : serviceTimes) {
              s.setCount(s.getCount() - 1);
              serviceVehicleCountService.updateServiceVehicleCountByTime(s);
            }
          }
        }

        sw.stopAndStart("thread and other");
        //代金券消费记录作废
        if (null!=repairOrderDTO.getConsumingRecordId()) {
          ServiceManager.getService(ConsumingService.class).consumingRecordRepeal(shopId, repairOrderDTO.getConsumingRecordId());
        }
        RepairOrderSavedEvent repairOrderSavedEvent = new RepairOrderSavedEvent(repairOrderDTO);
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        bcgogoEventPublisher.publisherRepairOrderSaved(repairOrderSavedEvent);
        request.setAttribute("UNIT_TEST", repairOrderSavedEvent); //单元测试
        repairOrderSavedEvent.setMainFlag(true);

        model.put("repairOrderDTO", repairOrderDTO);
        //更新memcacheLimitInfo
        inventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, repairOrderDTO.getInventoryLimitDTO());
        //更新memcache  product 更新的标识和时间
        ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(repairOrderDTO);
        //更新累积欠款信息
        ServiceManager.getService(ITxnService.class).getPayableAndReceivableToModel(model, shopId, repairOrderDTO.getCustomerId());
        repairOrderDTO.setTotalReturnDebt(NumberUtil.round(Double.parseDouble(model.get(TOTAL_PAYABLE).toString()), NumberUtil.MONEY_PRECISION));

        List<ReceptionRecordDTO> receptionRecordDTOs = ServiceManager.getService(ITxnService.class).getSettledRecord(WebUtil.getShopId(request), OrderTypes.REPAIR, Long.valueOf(repairOrderId));
        model.addAttribute("receptionRecordDTOs", receptionRecordDTOs);
      } else {
        return getRepairOrder(model, request, repairOrderDTO.getId().toString());
      }
      model.addAttribute("resultMsg", "success");
      sw.stopAndPrintLog();
    } catch (Exception e) {
      LOG.error("/txn.do  method=repairOrderRepeal " + "施工单作废 repairOrder 为空 或者状态不对" + repairOrderDTO);
      LOG.error(e.getMessage(), e);
      model.addAttribute("resultMsg", "failure");
    } finally {
      BcgogoConcurrentController.release(ConcurrentScene.WEB_REPAIR_ORDER_REPEAL, lockKey);
    }
    return "/txn/invoicingFinish";
  }


  @RequestMapping(params = "method=ajaxUpdateMultipleRecommendedPrice")
  @ResponseBody
  public Object ajaxUpdateMultipleRecommendedPrice(HttpServletRequest request, HttpServletResponse response,
                                                   InventoryLimitDTO inventoryLimitDTO) throws Exception {
    Long shopId;
    try {
      shopId = WebUtil.getShopId(request);
      if (inventoryLimitDTO.getProductDTOs() == null || inventoryLimitDTO.getProductDTOs().length == 0) {
        LOG.info("shopId:{}开始更新多个商品销售价失败,productIds信息为空:{}", shopId, inventoryLimitDTO.getProductDTOs());
        return "success";
      }
      Long[] productIds = new Long[inventoryLimitDTO.getProductDTOs().length];
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      for (int i = 0, len = inventoryLimitDTO.getProductDTOs().length; i < len; i++) {
        if (inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId() != null) {
          productIds[i] = inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId();
          recentChangedProductMap.put(productIds[i], new Pair(System.currentTimeMillis(), false));
        }
      }

      ServiceManager.getService(ITxnService.class).checkAndInsertInventorySearchIndexes(shopId, productIds);

      rfiTxnService.updateMultipleInventoryRecommendedPrice(inventoryLimitDTO.getProductDTOs(), shopId);

      ServiceManager.getService(ISearchService.class).updateMultipleInventorySearchIndexRecommendedPrice(inventoryLimitDTO.getProductDTOs(), shopId);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productIds);
      //保存修改的productId到solr
      ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);


      return "success";
    } catch (Exception e) {
      LOG.error("txn.do?method=ajaxUpdateMultipleRecommendedPrice\n" + e.getMessage(), e);
      return "error";
    }
  }

  //商品首页，库存查询首页更新库存上下限
  @RequestMapping(params = "method=updateLimit")
  public void updateLimit(ModelMap model, HttpServletRequest request, HttpServletResponse response, InventoryLimitDTO inventoryLimitDTO) {
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    ISearchService iSearchService = ServiceManager.getService(ISearchService.class);
    List<MemcacheLimitDTO> memcacheLimitDTOs = new ArrayList<MemcacheLimitDTO>();
    try {
      Long shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return;
      }
      inventoryLimitDTO.setShopId(shopId);
      //更新inventory limit 获得告警数量
      iInventoryService.updateInventoryLimit(inventoryLimitDTO);
      iSearchService.updateInventorySearchIndexLimit(inventoryLimitDTO);

      if (inventoryLimitDTO == null || inventoryLimitDTO.getProductDTOs() == null || inventoryLimitDTO.getShopId() == null) {
        return;
      }
      Set<Long> productLocalInfoIdSet = new HashSet<Long>();
      for (ProductDTO productDTO : inventoryLimitDTO.getProductDTOs()) {
        if (productDTO == null || productDTO.getProductLocalInfoId() == null) {
          continue;
        }
        productLocalInfoIdSet.add(productDTO.getProductLocalInfoId());
      }
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productLocalInfoIdSet.toArray(new Long[productLocalInfoIdSet.size()]));
      MemcacheLimitDTO memcacheLimitDTO = iInventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);

      memcacheLimitDTOs.add(memcacheLimitDTO);
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.listToJson(memcacheLimitDTOs));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/txn.do ,method=updateLimit,shopId:" + inventoryLimitDTO.getShopId() + "批量更新库存上下限异常");
      LOG.error(e.getMessage(), e);
    }
  }

  //商品首页，库存查询首页更新单个商品库存上下限
  @RequestMapping(params = "method=updateSingleLimit")
  public void updateSingleLimit(HttpServletRequest request, HttpServletResponse response,
                                Long productId, String lowerLimitVal, String upperLimitVal) {

    Long shopId = null;
    Double lowerVal = null;
    Double upperVal = null;
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    List<MemcacheLimitDTO> memcacheLimitDTOs = new ArrayList<MemcacheLimitDTO>();
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    ISearchService iSearchService = ServiceManager.getService(ISearchService.class);
    try {
      shopId = WebUtil.getShopId(request);
      ServiceManager.getService(ITxnService.class).checkAndInsertInventorySearchIndex(productId, shopId);
      lowerVal = Double.parseDouble((NumberUtils.isNumber(lowerLimitVal) && lowerLimitVal != null) ? lowerLimitVal : "0");
      upperVal = Double.parseDouble((NumberUtils.isNumber(upperLimitVal) && upperLimitVal != null) ? upperLimitVal : "0");
      LOG.info("shopId:{}开始更新单个商品库存上下限", shopId);
      LOG.info("productId为：{},上下限分别为{}", productId, upperLimitVal + "|" + lowerLimitVal);
      //更新库存inventory
      iInventoryService.updateSingelInventoryLimit(productId, lowerVal, upperVal, shopId, inventoryLimitDTO);
      //更新Inventory_search_index
      iSearchService.updateSingelInventoryInventorySearchIndexLimit(productId, lowerVal, upperVal, shopId);
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productId);
      //更新memocache  返回json数据
      MemcacheLimitDTO memcacheLimitDTO = iInventoryService.updateMemocacheLimitByInventoryLimitDTO(shopId, inventoryLimitDTO);
      memcacheLimitDTOs.add(memcacheLimitDTO);
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.listToJson(memcacheLimitDTOs));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/txn.do ,method=updateSingleLimit,shopId:{},productId{}更新单个库存上下限异常", shopId, productId);
      LOG.error(e.getMessage(), e);
    }
  }

  //ajax 查询库存告警数量
  @RequestMapping(params = "method=getLimitCount")
  public void getLimitCount(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = null;
    IInventoryService iInventoryService = ServiceManager.getService(IInventoryService.class);
    List<MemcacheLimitDTO> memcacheLimitDTOs = new ArrayList<MemcacheLimitDTO>();
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        LOG.info("shopId is null ,can't get memcacheInfo");
      }
      MemcacheLimitDTO memcacheLimitDTO = iInventoryService.getMemcacheLimitDTO(shopId);
      memcacheLimitDTOs.add(memcacheLimitDTO);
      PrintWriter writer = response.getWriter();
      writer.write(JsonUtil.listToJson(memcacheLimitDTOs));
      writer.close();
    } catch (Exception e) {
      LOG.debug("/txn.do ,method=updateSingleLimit,shopId:" + shopId + "ajax 查询库存告警数量异常");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=accountDetail")
  public String accountDetail(HttpServletRequest request) throws Exception {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    String customerIdStr = request.getParameter("customerId");
    Long shopId = WebUtil.getShopId(request);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    if (StringUtils.isNotBlank(customerIdStr)) {
      MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, Long.valueOf(customerIdStr));
      if (memberDTO != null && memberDTO.getStatus().equals(MemberStatus.ENABLED)) {
        if (memberDTO.getMemberDiscount() != null) {
          memberDTO.setMemberDiscount(NumberUtil.round(memberDTO.getMemberDiscount() * 10, 1));
        }
        request.setAttribute("memberBalance", memberDTO.getBalance());
        request.setAttribute("memberDiscount", memberDTO.getMemberDiscount());
        request.setAttribute("memberNo", memberDTO.getMemberNo());
      }
    }
    //短信控制开关
    MessageSwitchDTO messageSwitchDTO = notificationService.getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MEMBER_CONSUME_SMS_SWITCH);
    if (messageSwitchDTO == null || (messageSwitchDTO != null && MessageSwitchStatus.ON.equals(messageSwitchDTO.getStatus()))) {
      request.setAttribute("smsSwitch", true);
    } else {
      request.setAttribute("smsSwitch", false);
    }
    return "/txn/accountDetail1";
  }


  @RequestMapping(params = "method=searchWorks")
  @ResponseBody
  public Object searchWorks(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, String keyWord) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      return membersService.searchSaleManByShopIdAndKeyword(shopId, keyWord);
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=searchWorks");
      LOG.error("系统获取员工列表出错");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 计算施工单的 施工总费用 和材料总费用
   *
   * @param repairOrderDTO
   */
  public void setServiceAndSaleTotal(RepairOrderDTO repairOrderDTO) {
    if (repairOrderDTO == null) {
      return;
    }
    double salesTotal = 0.0;
    double serviceTotal = 0.0;
    double actualHoursTotal = 0.0;

    if (repairOrderDTO.getServiceDTOs() != null && repairOrderDTO.getServiceDTOs().length > 0) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO == null || StringUtil.isEmpty(repairOrderServiceDTO.getService())) {
          continue;
        }
        if (ConsumeType.TIMES == repairOrderServiceDTO.getConsumeType()) {
          continue;
        }
        serviceTotal += repairOrderServiceDTO.getTotal();
        actualHoursTotal += NumberUtil.doubleVal(repairOrderServiceDTO.getActualHours());
      }
    }

    //保留一位小数
    repairOrderDTO.setServiceTotal(NumberUtil.round(serviceTotal, NumberUtil.MONEY_PRECISION));
    //保留一位小数
    repairOrderDTO.setActualHoursTotal(NumberUtil.round(actualHoursTotal, NumberUtil.MONEY_PRECISION));
    if (repairOrderDTO.getItemDTOs() != null && repairOrderDTO.getItemDTOs().length > 0) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (repairOrderItemDTO == null || StringUtil.isEmpty(repairOrderItemDTO.getProductName())) {
          continue;
        }
        salesTotal += repairOrderItemDTO.getTotal();
      }
    }

    //保留一位小数
    repairOrderDTO.setSalesTotal(NumberUtil.round(salesTotal, NumberUtil.MONEY_PRECISION));
  }

  /**
   * 前台在选择服务或者更改消费类型时，后台判断该客户有无此项计次划卡项目
   *
   * @param model
   * @param request
   * @param response
   */

  @Deprecated
  @RequestMapping(params = "method=judgeService")
  public void judgeService(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);

      Long shopId = (Long) request.getSession().getAttribute("shopId");
      String jsonStr = "";
      String serviceName = request.getParameter("serviceName");
      String vehicleNumber = request.getParameter("vehicleNumber");
      String customerIdStr = request.getParameter("customerId");

      if (StringUtils.isBlank(serviceName) || StringUtils.isBlank(customerIdStr) || StringUtils.isBlank(vehicleNumber)) {
        jsonStr = MemberConstant.MEMBER_VALIDATE_SUCCESS;
      } else {
        List<ServiceDTO> serviceList = txnService.getServiceByServiceNameAndShopId(shopId, serviceName);
        if (CollectionUtils.isEmpty(serviceList)) {
          jsonStr = MemberConstant.SHOP_NO_CONTAIN_SERVICE;
        } else {
          if (serviceList.size() > 1) {
            LOG.error("Txn.do");
            LOG.error("method = judgeServiceByCustomerIdAndLicenceNo");
            LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
            LOG.error("服务项目" + serviceName + "在该店铺下有多个，请确认");
          }

          Long serviceId = serviceList.get(0).getId();
          Long customerId = Long.valueOf(customerIdStr);
          MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, customerId);
          if (memberDTO == null) {
            jsonStr = MemberConstant.CUSTOMER_CONTAIN_SERVICE;
          } else {
            jsonStr = memberCheckerService.memberContainService(memberDTO, serviceId, vehicleNumber);
          }
        }
      }
      jsonStr = JsonUtil.objectToJson(jsonStr);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.error("Txn.do");
      LOG.error("method = judgeServiceByCustomerIdAndLicenceNo");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=setTradePrice")
  public String setTradePrice() {
    return "txn/setTradePrice";
  }

  //商品首页，库存查询首页更新单个商品库存批发价
  @RequestMapping(params = "method=updateSingleTradePrice")
  @ResponseBody
  public Object updateSingleTradePrice(HttpServletRequest request, HttpServletResponse response,
                                       Long productId, Double tradePrice) {

    Long shopId = null;
    Map<String, String> resultMap = new HashMap<String, String>();
    try {
      shopId = WebUtil.getShopId(request);
      if (productId == null) {
        LOG.info("shopId:{}开始更新失败,productId为空:{},tradePrice:{}", new Object[]{shopId, productId, tradePrice});
        resultMap.put("result", "error");
        return resultMap;
      }
      ProductDTO productDTO = new ProductDTO();
      productDTO.setProductLocalInfoId(productId);
      productDTO.setTradePrice(tradePrice);
      ProductDTO[] productDTOs = new ProductDTO[1];
      productDTOs[0] = productDTO;
      productService.updateTradePrice(productDTOs, shopId);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productId);
      //保存修改的productId到solr
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      recentChangedProductMap.put(productDTO.getProductLocalInfoId(), new Pair(System.currentTimeMillis(), false));
      ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);
      resultMap.put("result", "success");
      return resultMap;
    } catch (Exception e) {
      LOG.error("shopId:{}更新单个商品库存批发价出错,productId:{},tradePrice:{}" + e.getMessage(), new Object[]{shopId, productId, tradePrice, e});
      resultMap.put("result", "error");
      return resultMap;
    }
  }

  //商品首页，库存查询首页更新单个商品仓位
  @RequestMapping(params = "method=updateSingleStorageBin")
  @ResponseBody
  public Object updateSingleStorageBin(HttpServletRequest request, HttpServletResponse response,
                                       Long productId, String storageBin) {

    Long shopId = null;
    Map<String, String> resultMap = new HashMap<String, String>();
    try {
      shopId = WebUtil.getShopId(request);
      if (productId == null) {
        LOG.info("shopId:{}开始更新失败,productId为空:{},storageBin:{}", new Object[]{shopId, productId, storageBin});
        resultMap.put("result", "error");
        return resultMap;
      }
      ProductDTO productDTO = new ProductDTO();
      productDTO.setProductLocalInfoId(productId);
      productDTO.setStorageBin(storageBin);
      ProductDTO[] productDTOs = new ProductDTO[1];
      productDTOs[0] = productDTO;
      productService.updateStorageBin(productDTOs, shopId);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productId);
      resultMap.put("result", "success");
      return resultMap;
    } catch (Exception e) {
      LOG.error("shopId:{}库存查询首页更新单个商品仓位,productId:{},storageBin:{}" + e.getMessage(), new Object[]{shopId, productId, storageBin, e});
      resultMap.put("result", "error");
      return resultMap;
    }
  }

  //商品首页，库存查询首页更新单个商品库存批发价
  @RequestMapping(params = "method=updateMultipleTradePrice")
  @ResponseBody
  public Object updateMultipleTradePrice(HttpServletRequest request, InventoryLimitDTO inventoryLimitDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (inventoryLimitDTO.getProductDTOs() == null || inventoryLimitDTO.getProductDTOs().length == 0) {
        LOG.info("shopId:{}开始更新多个商品库存批发价失败,productIds信息为空:{}", shopId, inventoryLimitDTO.getProductDTOs());
        return new Result().LogErrorMsg("请选择要设置批发价的商品。");
      }
      Long[] productIds = new Long[inventoryLimitDTO.getProductDTOs().length];
      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      for (int i = 0, len = inventoryLimitDTO.getProductDTOs().length; i < len; i++) {
        if (inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId() != null) {
          productIds[i] = inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId();
          recentChangedProductMap.put(productIds[i], new Pair(System.currentTimeMillis(), false));
        }
      }
      productService.updateTradePrice(inventoryLimitDTO.getProductDTOs(), shopId);
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, productIds);
      //保存修改的productId到solr
      ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);
    } catch (Exception e) {
      LOG.error("shopId:{}更新单个商品库存批发价出错,productId:{}" + e.getMessage(), new Object[]{shopId, inventoryLimitDTO.getProductDTOs(), e});
      return new Result(false);
    }
    return new Result();
  }


  //商品首页，库存查询首页批量更新商品分类
  @RequestMapping(params = "method=updateMultipleProductKind")
  @ResponseBody
  public Object updateMultipleProductKind(HttpServletRequest request, InventoryLimitDTO inventoryLimitDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null) {
        return "error";
      }
      //InventorySearchIndex的idList
      String idListStr = request.getParameter("idList");
      String newKindName = request.getParameter("newKindName");
      if (idListStr == null || "".equals(idListStr)) {
        return "error";
      }
      String[] tempIdList = idListStr.split(",");
      Long[] idList = new Long[tempIdList.length];
      //Product表的idList
      Long[] productIdList = new Long[tempIdList.length];
      for (int i = 0; i < tempIdList.length; i++) {
        idList[i] = Long.parseLong(tempIdList[i]);
        productIdList[i] = searchService.getInventorySearchIndexById(shopId, idList[i]).getParentProductId();
      }
      Long newKindId = productService.getProductKindId(shopId, newKindName);
      productService.updateMultipleProductKind(shopId, productIdList, newKindId);
      searchService.updateMultipleInventoryKind(shopId, idList, newKindName);

      Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
      for (int i = 0, len = inventoryLimitDTO.getProductDTOs().length; i < len; i++) {
        if (inventoryLimitDTO.getProductDTOs()[i].getProductLocalInfoId() != null) {
          recentChangedProductMap.put(idList[i], new Pair(System.currentTimeMillis(), false));
        }
      }
      //重做solr索引
      ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(shopId, idList);
      //保存修改的productId到solr
      ServiceManager.getService(IProductCurrentUsedService.class).saveRecentChangedProductInMemory(shopId, recentChangedProductMap);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return "error";
    }
    return "success";
  }


  /**
   * 根据前台ajax提交的dto进行验证 返回校验结果
   *
   * @param request
   * @param repairOrderDTO 施工单
   */
  @RequestMapping(params = "method=validateRepairOrder")
  @ResponseBody
  public Result validateRepairOrder(HttpServletRequest request, RepairOrderDTO repairOrderDTO) {
    try {
      String validateType = request.getParameter("validateType");
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      repairOrderDTO.setShopId(shopId);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      IUserService userService = ServiceManager.getService(IUserService.class);
      if (null != repairOrderDTO.getCustomerId()) {
        Customer customer = userService.getCustomerByCustomerId(repairOrderDTO.getCustomerId(), shopId);

        if (null != customer && CustomerStatus.DISABLED == customer.getStatus()) {
          Result result1 = new Result(CustomerConstant.CUSTOMER_DISABLED_NO_SETTLE, false);
          return result1;
        }
      }
      Result result = null;
//      一辆车可以对应多个施工单
//     Result result = validateRepairOrderExist(shopId, repairOrderDTO);
//      if (result != null) return result;
      result = validateRepairStatus(validateType, shopId, repairOrderDTO);
      if (result != null && !result.isSuccess()) return result;
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
        if (repairOrderDTO.getStorehouseId() != null) {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, repairOrderDTO.getStorehouseId());
          if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
            return new Result(ValidatorConstant.STOREHOUSE_DELETED_MSG, false);
          }
        }
      }
      if ("finish".equals(validateType) || "account".equals(validateType) || "accountDetail".equals(validateType)) {
        if (userService.isRepairPickingSwitchOn(shopId)) {
          result = repairService.validateRepairPicking(repairOrderDTO, validateType);
          if (result != null) return result;
        } else if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId)) {
          result = validateProductInventory(shopId, shopVersionId, repairOrderDTO);
          if (result != null) return result;
        }

      }
      if ("accountDetail".equals(validateType)) {
        //校验会员信息
        result = validateMember(shopId, repairOrderDTO);
        if (result != null) return result;
      }
      repairOrderDTO.setShopId(shopId);
      result = rfiTxnService.getDeletedProductValidatorResult(repairOrderDTO);
      if (result != null) {
        return result;
      }
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=validateRepairOrder");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return new Result(MemberConstant.SUBMIT_EXCEPTION, false);
    }
    return new Result();
  }

  /**
   * 校验在派单的时候  车牌号是否已经有未完成的单据
   *
   * @param shopId
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  private Result validateRepairOrderExist(Long shopId, RepairOrderDTO repairOrderDTO) throws Exception {
    if (StringUtils.isNotBlank(repairOrderDTO.getLicenceNo())) {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      RepairOrderDTO dbRepairOrderDTO = txnService.getUnbalancedAccountRepairOrderByVehicleNumber(shopId, repairOrderDTO.getLicenceNo().toUpperCase(), null);
      if (dbRepairOrderDTO != null) {
        if (repairOrderDTO.getId() == null || (repairOrderDTO.getId() != null && repairOrderDTO.getId().longValue() != dbRepairOrderDTO.getId().longValue())) {
          Map map = new HashMap();
          map.put("receiptNo", dbRepairOrderDTO.getReceiptNo());
          map.put("orderId", dbRepairOrderDTO.getId().toString());
          return new Result(MemberConstant.REPAIR_ORDER_EXIST, false, Result.Operation.PROMPT_EXIST_REPAIR_ORDER.getValue(), map);
        }
      }
    }
    return null;
  }

  private Result validateRepairStatus(String validateType, Long shopId, RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO.getId() != null) {
      RFITxnService rfTxnService = ServiceManager.getService(RFITxnService.class);
      RepairOrderDTO dbRepairOrderDTO = rfTxnService.getRepairOrderDTODetailById(repairOrderDTO.getId(), shopId);
      if (dbRepairOrderDTO != null) {
        if (OrderStatus.REPAIR_REPEAL.equals(dbRepairOrderDTO.getStatus())) {
          return new Result(MemberConstant.REPAIR_ORDER_REPEAL, false, Result.Operation.REFRESH_REPAIR_ORDER.getValue(), dbRepairOrderDTO.getIdStr());
        }
        //一张单据不能结算多次
        if (("account".equals(validateType) || "accountDetail".equals(validateType)) && OrderStatus.REPAIR_SETTLED.equals(dbRepairOrderDTO.getStatus())) {
          return new Result(MemberConstant.REPAIR_ORDER_SETTLED, false, Result.Operation.REFRESH_REPAIR_ORDER.getValue(), dbRepairOrderDTO.getIdStr());
        } else if ("save".equals(validateType) && OrderStatus.REPAIR_SETTLED.equals(dbRepairOrderDTO.getStatus())) {
          return new Result(MemberConstant.REPAIR_ORDER_SETTLED_SAVE, false, Result.Operation.REFRESH_REPAIR_ORDER.getValue(), dbRepairOrderDTO.getIdStr());
        } else if ("finish".equals(validateType) && OrderStatus.REPAIR_SETTLED.equals(dbRepairOrderDTO.getStatus())) {
          return new Result(MemberConstant.REPAIR_ORDER_SETTLED_FINISH, false, Result.Operation.REFRESH_REPAIR_ORDER.getValue(), dbRepairOrderDTO.getIdStr());
        }
      }
    }
    return null;
  }

  private Result validateMember(Long shopId, RepairOrderDTO repairOrderDTO) throws Exception {
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    String msg = memberCheckerService.checkRepairOrderDTO(shopId, repairOrderDTO);
    if (!MemberConstant.MEMBER_VALIDATE_SUCCESS.equals(msg)) {
      return new Result(msg, false);
    }
    return null;
  }

  private Result validateProductInventory(Long shopId, Long shopVersionId, RepairOrderDTO repairOrderDTO) throws Exception {
    //去掉空行
    List<Long> productIdList = removeNullRow(repairOrderDTO);
    if (ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs()) && ArrayUtil.isEmpty(repairOrderDTO.getServiceDTOs())) {
      return new Result(ValidatorConstant.ORDER_NULL_MSG, false);
    } else if (!ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs()) && repairOrderDTO.getItemDTOs().length > productIdList.size()) {
      return new Result("不能使用新商品进行完工或者结算操作,请修改后再提交！", false);
    } else {
      if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(shopVersionId) && CollectionUtils.isNotEmpty(productIdList)) {
        IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopVersionId)) {
          //通过仓库校验库存
          if (repairOrderDTO.getStorehouseId() != null) {
            Map<String, String> data = new HashMap<String, String>();
            //获得对应的数据库里的单据
            RepairOrderDTO repairOrderDTOInDB = null;
            Boolean result = null;
            if (repairOrderDTO.getId() != null) {
              repairOrderDTOInDB = ServiceManager.getService(ITxnService.class).getRepairOrder(repairOrderDTO.getShopId(), repairOrderDTO.getId());
            }
            if (repairOrderDTOInDB != null) {
              result = inventoryService.RFCheckBatchProductInventoryByStoreHouse(shopId, repairOrderDTO.getStorehouseId(), repairOrderDTOInDB.getStorehouseId(), repairOrderDTO.getItemDTOs(), data, productIdList);
            } else {
              result = inventoryService.RFCheckBatchProductInventoryByStoreHouse(shopId, repairOrderDTO.getStorehouseId(), null, repairOrderDTO.getItemDTOs(), data, productIdList);
            }
            if (!result) {
              //校验产品是否可以调拨
              if (inventoryService.checkBatchProductInventoryInOtherStorehouse(shopId, repairOrderDTO, productIdList)) {
                return new Result(ValidatorConstant.PRODUCT_STOREHOUSE_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue() + "+" + Result.Operation.CONFIRM_ALLOCATE_RECORD.getValue(), data);
              } else {
                return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
              }
            }
          } else {
            return new Result(ValidatorConstant.STOREHOUSE_NULL_MSG, false);
          }
        } else {
          //校验产品库存
          Map<String, String> data = new HashMap<String, String>();
          if (!inventoryService.checkBatchProductInventory(shopId, repairOrderDTO, data, productIdList)) {
            return new Result(ValidatorConstant.PRODUCT_INVENTORY_LACK, false, Result.Operation.UPDATE_PRODUCT_INVENTORY.getValue(), data);
          }
        }
      }
    }
    return null;
  }

  private List<Long> removeNullRow(RepairOrderDTO repairOrderDTO) throws Exception {
    if (repairOrderDTO.getServiceDTOs() != null) {
      RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
      List<RepairOrderServiceDTO> repairOrderServiceDTOList = new ArrayList<RepairOrderServiceDTO>();
      for (int i = 0; i < repairOrderServiceDTOs.length; i++) {
        if (StringUtils.isNotBlank(repairOrderServiceDTOs[i].getService())) {
          repairOrderServiceDTOList.add(repairOrderServiceDTOs[i]);
        }
      }
      if (CollectionUtils.isNotEmpty(repairOrderServiceDTOList)) {
        repairOrderDTO.setServiceDTOs(repairOrderServiceDTOList.toArray(new RepairOrderServiceDTO[repairOrderServiceDTOList.size()]));
      } else {
        repairOrderDTO.setServiceDTOs(null);
      }
    }
    List<Long> productIdList = new ArrayList<Long>();
    if (repairOrderDTO.getItemDTOs() != null) {
      RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
      List<RepairOrderItemDTO> repairOrderItemDTOList = new ArrayList<RepairOrderItemDTO>();
      for (int i = 0; i < repairOrderItemDTOs.length; i++) {
        if (StringUtils.isNotBlank(repairOrderItemDTOs[i].getProductName())) {
          if (repairOrderItemDTOs[i].getProductId() == null) {
            ProductDTO searchCondition = new ProductDTO(repairOrderDTO.getShopId(), repairOrderItemDTOs[i]);
            ProductDTO dbProduct = CollectionUtil.uniqueResult(productService.getProductDTOsBy7P(repairOrderDTO.getShopId(), searchCondition));
            if (dbProduct != null) {
              ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoByProductId(dbProduct.getId(), repairOrderDTO.getShopId());
              if (productLocalInfoDTO != null) {
                repairOrderItemDTOs[i].setProductId(productLocalInfoDTO.getId());
              }
            }
          }
          repairOrderItemDTOList.add(repairOrderItemDTOs[i]);
          if (repairOrderItemDTOs[i].getProductId() != null) {
            productIdList.add(repairOrderItemDTOs[i].getProductId());
          }
        }
      }
      if (CollectionUtils.isNotEmpty(repairOrderItemDTOList)) {
        repairOrderDTO.setItemDTOs(repairOrderItemDTOList.toArray(new RepairOrderItemDTO[repairOrderItemDTOList.size()]));
      } else {
        repairOrderDTO.setItemDTOs(null);
      }
    }
    return productIdList;
  }

  /**
   * 保存施工单模板
   *
   * @param modelMap
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(params = "method=saveRepairOrderTemplate")
  @ResponseBody
  public Object saveRepairOrderTemplate(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, RepairOrderDTO repairOrderDTO) {

    Long shopId = null;

    try {

      shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      if (shopId == null || repairOrderDTO == null || shopVersionId == null) {
        return null;
      }
      repairOrderDTO.setShopVersionId(shopVersionId);
      if (!BcgogoConcurrentController.lock(ConcurrentScene.REPAIR_ORDER_TEMPLATE, shopId)) {
        return null;
      }

      RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
      RepairOrderServiceDTO[] serviceDTOs = repairOrderDTO.getServiceDTOs();
      if ((itemDTOs == null || itemDTOs.length <= 0) && (serviceDTOs == null || serviceDTOs.length <= 0)) {
        return null;
      } else {
        IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);

        RepairOrderTemplateDTO repairOrderTemplateDTO = new RepairOrderTemplateDTO();
        repairOrderTemplateDTO.setTemplateName(repairOrderDTO.getRepairOrderTemplateName());
        repairOrderTemplateDTO.setShopId(shopId);
        repairOrderTemplateDTO.setStatus(RepairOrderTemplateStatus.ENABLED);

        repairOrderDTO.setShopId(shopId);
        repairOrderTemplateDTO.setRepairOrderDTO(repairOrderDTO);

        ArrayList<RepairOrderTemplateItemDTO> templateItemDTOArrayList = new ArrayList<RepairOrderTemplateItemDTO>();
        if (itemDTOs != null && itemDTOs.length > 0) {

          for (RepairOrderItemDTO repairOrderItemDTO : itemDTOs) {

            if (repairOrderItemDTO.getProductName() == null || "".equals(repairOrderItemDTO.getProductName().trim()))
              continue;
            RepairOrderTemplateItemDTO templateItemDTO = new RepairOrderTemplateItemDTO();
            templateItemDTO.setRepairOrderItemDTO(repairOrderItemDTO);
            templateItemDTOArrayList.add(templateItemDTO);
          }

        }
        repairOrderTemplateDTO.setRepairOrderTemplateItemDTOs(templateItemDTOArrayList);

        ArrayList<RepairOrderTemplateServiceDTO> templateServiceDTOArrayList = new ArrayList<RepairOrderTemplateServiceDTO>();
        if (serviceDTOs != null && serviceDTOs.length > 0) {

          for (RepairOrderServiceDTO repairOrderServiceDTO : serviceDTOs) {
            if (repairOrderServiceDTO.getService() == null || "".equals(repairOrderServiceDTO.getService().trim()))
              continue;
            RepairOrderTemplateServiceDTO templateServiceDTO = new RepairOrderTemplateServiceDTO();
            templateServiceDTO.setRepairOrderServiceDTO(repairOrderServiceDTO);
            templateServiceDTOArrayList.add(templateServiceDTO);
          }

        }
        repairOrderTemplateDTO.setRepairOrderTemplateServiceDTOs(templateServiceDTOArrayList);

        List<RepairOrderTemplateOtherIncomeItemDTO> templateOtherIncomeItemDTOList = null;

        if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
          for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
            if (StringUtils.isBlank(itemDTO.getName())) {
              continue;
            }

            if (null == templateOtherIncomeItemDTOList) {
              templateOtherIncomeItemDTOList = new ArrayList<RepairOrderTemplateOtherIncomeItemDTO>();
            }

            RepairOrderTemplateOtherIncomeItemDTO otherIncomeItemDTO = new RepairOrderTemplateOtherIncomeItemDTO();

            otherIncomeItemDTO.setMemo(itemDTO.getMemo());
            otherIncomeItemDTO.setName(itemDTO.getName());
            otherIncomeItemDTO.setPrice(itemDTO.getPrice());
            otherIncomeItemDTO.setShopId(itemDTO.getShopId());
            otherIncomeItemDTO.setOtherIncomeCalculateWay(itemDTO.getOtherIncomeCalculateWay());
            otherIncomeItemDTO.setOtherIncomeRate(NumberUtil.doubleVal(itemDTO.getOtherIncomeRate()));

            if (itemDTO.getOtherIncomeCostPrice() == null) {
              otherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.FALSE);
            } else {
              otherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.TRUE);
            }
            otherIncomeItemDTO.setOtherIncomeCostPrice(NumberUtil.doubleVal(itemDTO.getOtherIncomeCostPrice()));
            templateOtherIncomeItemDTOList.add(otherIncomeItemDTO);
          }
        }
        repairOrderTemplateDTO.setRepairOrderTemplateOtherIncomeItemDTOList(templateOtherIncomeItemDTOList);
        repairOrderTemplateDTO = repairOrderTemplateService.saveOrUpdateRepairOrderTemplate(repairOrderTemplateDTO);
        return repairOrderTemplateDTO;
      }
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=saveRepairOrderTemplate");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    } finally {
      if (shopId != null) {
        BcgogoConcurrentController.release(ConcurrentScene.REPAIR_ORDER_TEMPLATE, shopId);
      }
    }
  }


  /**
   * 根据模板名称获取施工单模板信息
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getRepairOrderTemplateByTemplateName")
  @ResponseBody
  public Object getRepairOrderTemplateByTemplateName(HttpServletRequest request, String repairOrderTemplateName, Long storehouseId, Boolean isSimple) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      Long shopVersionId = WebUtil.getShopVersionId(request);
      if (shopId == null || StringUtils.isBlank(repairOrderTemplateName) || shopVersionId == null) {
        return null;
      }
      IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
      RepairOrderTemplateDTO repairOrderTemplateDTO = null;
      if (isSimple) {
        repairOrderTemplateDTO = repairOrderTemplateService.getSimpleRepairOrderTemplateByTemplateName(shopId, shopVersionId, storehouseId, repairOrderTemplateName);
      } else {
        repairOrderTemplateDTO = repairOrderTemplateService.getRepairOrderTemplateByTemplateName(shopId, shopVersionId, storehouseId, repairOrderTemplateName);
      }
      return new Result(repairOrderTemplateDTO);
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=getRepairOrderTemplateByTemplateName");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 查询得到最常用的5个施工单模板
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getTop5RepairOrderTemplateOrderByUsageCounter")
  @ResponseBody
  public Object getTop5RepairOrderTemplateOrderByUsageCounter(HttpServletRequest request) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);

      if (shopId == null) {
        return null;
      }
      IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
      List<RepairOrderTemplateDTO> repairOrderTemplateDTOList = repairOrderTemplateService.getTop5RepairOrderTemplateOrderByUsageCounter(shopId);
      return repairOrderTemplateDTOList;
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=getTop5RepairOrderTemplateOrderByUsageCounter");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  /**
   * 查询得到所有施工单模板
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getAllRepairOrderTemplateOrder")
  @ResponseBody
  public Object getAllRepairOrderTemplateOrder(HttpServletRequest request) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);

      if (shopId == null) {
        return null;
      }
      IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
      List<RepairOrderTemplateDTO> repairOrderTemplateDTOList = repairOrderTemplateService.getAllRepairOrderTemplate(shopId);
      return repairOrderTemplateDTOList;
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=getAllRepairOrderTemplateOrder");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=deleteRepairOrderTemplate")
  @ResponseBody
  public Object deleteRepairOrderTemplate(HttpServletRequest request) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      String repairOrderTemplateId = request.getParameter("repairOrderTemplateId");

      if (shopId == null || repairOrderTemplateId == null) {
        return null;
      }
      IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
      RepairOrderTemplateDTO repairOrderTemplateDTO = repairOrderTemplateService.deleteRepairOrderTemplateById(Long.valueOf(repairOrderTemplateId));
      return repairOrderTemplateDTO;
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=deleteRepairOrderTemplate");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  @RequestMapping(params = "method=renameRepairOrderTemplate")
  @ResponseBody
  public Object renameRepairOrderTemplate(HttpServletRequest request) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      String repairOrderTemplateId = request.getParameter("repairOrderTemplateId");
      String newRepairOrderTemplateName = request.getParameter("newRepairOrderTemplateName");

      if (shopId == null || repairOrderTemplateId == null || newRepairOrderTemplateName == null) {
        return null;
      }
      IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);
      RepairOrderTemplateDTO repairOrderTemplateDTO = repairOrderTemplateService.renameRepairOrderTemplateById(shopId, Long.valueOf(repairOrderTemplateId), newRepairOrderTemplateName);
      return repairOrderTemplateDTO;
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=deleteRepairOrderTemplate");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }


  @RequestMapping(params = "method=saveRepairOrderTemplateWithExistingSameNameRepairOrderTemplate")
  @ResponseBody
  public Object saveRepairOrderTemplateWithExistingSameNameRepairOrderTemplate(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, RepairOrderDTO repairOrderDTO) {

    Long shopId = null;

    try {
      shopId = WebUtil.getShopId(request);
      if (shopId == null || repairOrderDTO == null) {
        return null;

      }
      repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
      RepairOrderItemDTO[] itemDTOs = repairOrderDTO.getItemDTOs();
      RepairOrderServiceDTO[] serviceDTOs = repairOrderDTO.getServiceDTOs();
      if ((itemDTOs == null || itemDTOs.length <= 0) && (serviceDTOs == null || serviceDTOs.length <= 0)) {
        return null;
      } else {
        IRepairOrderTemplateService repairOrderTemplateService = ServiceManager.getService(RepairOrderTemplateService.class);

        RepairOrderTemplateDTO repairOrderTemplateDTO = new RepairOrderTemplateDTO();
        repairOrderTemplateDTO.setTemplateName(repairOrderDTO.getRepairOrderTemplateName());
        repairOrderTemplateDTO.setShopId(shopId);
        repairOrderTemplateDTO.setStatus(RepairOrderTemplateStatus.ENABLED);

        repairOrderDTO.setShopId(shopId);
        repairOrderTemplateDTO.setRepairOrderDTO(repairOrderDTO);

        ArrayList<RepairOrderTemplateItemDTO> templateItemDTOArrayList = new ArrayList<RepairOrderTemplateItemDTO>();
        if (itemDTOs != null && itemDTOs.length > 0) {

          for (RepairOrderItemDTO repairOrderItemDTO : itemDTOs) {

            if (repairOrderItemDTO.getProductName() == null || "".equals(repairOrderItemDTO.getProductName().trim()))
              continue;
            RepairOrderTemplateItemDTO templateItemDTO = new RepairOrderTemplateItemDTO();
            templateItemDTO.setRepairOrderItemDTO(repairOrderItemDTO);
            templateItemDTOArrayList.add(templateItemDTO);
          }

        }
        repairOrderTemplateDTO.setRepairOrderTemplateItemDTOs(templateItemDTOArrayList);

        ArrayList<RepairOrderTemplateServiceDTO> templateServiceDTOArrayList = new ArrayList<RepairOrderTemplateServiceDTO>();
        if (serviceDTOs != null && serviceDTOs.length > 0) {

          for (RepairOrderServiceDTO repairOrderServiceDTO : serviceDTOs) {
            if (repairOrderServiceDTO.getService() == null || "".equals(repairOrderServiceDTO.getService().trim()))
              continue;
            RepairOrderTemplateServiceDTO templateServiceDTO = new RepairOrderTemplateServiceDTO();
            templateServiceDTO.setRepairOrderServiceDTO(repairOrderServiceDTO);
            templateServiceDTOArrayList.add(templateServiceDTO);
          }

        }
        repairOrderTemplateDTO.setRepairOrderTemplateServiceDTOs(templateServiceDTOArrayList);

        List<RepairOrderTemplateOtherIncomeItemDTO> templateOtherIncomeItemDTOList = null;

        if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
          for (RepairOrderOtherIncomeItemDTO itemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
            if (StringUtils.isBlank(itemDTO.getName())) {
              continue;
            }

            if (null == templateOtherIncomeItemDTOList) {
              templateOtherIncomeItemDTOList = new ArrayList<RepairOrderTemplateOtherIncomeItemDTO>();
            }

            RepairOrderTemplateOtherIncomeItemDTO templateOtherIncomeItemDTO = new RepairOrderTemplateOtherIncomeItemDTO();

            templateOtherIncomeItemDTO.setId(itemDTO.getTemplateId());
            templateOtherIncomeItemDTO.setMemo(itemDTO.getMemo());
            templateOtherIncomeItemDTO.setPrice(itemDTO.getPrice());
            templateOtherIncomeItemDTO.setName(itemDTO.getName());

            templateOtherIncomeItemDTO.setOtherIncomeCalculateWay(itemDTO.getOtherIncomeCalculateWay());
            templateOtherIncomeItemDTO.setOtherIncomeRate(NumberUtil.doubleVal(itemDTO.getOtherIncomeRate()));

            if (itemDTO.getOtherIncomeCostPrice() == null) {
              templateOtherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.FALSE);
            } else {
              templateOtherIncomeItemDTO.setCalculateCostPrice(BooleanEnum.TRUE);
            }
            templateOtherIncomeItemDTO.setOtherIncomeCostPrice(NumberUtil.doubleVal(itemDTO.getOtherIncomeCostPrice()));

            templateOtherIncomeItemDTOList.add(templateOtherIncomeItemDTO);
          }
        }
        repairOrderTemplateDTO.setRepairOrderTemplateOtherIncomeItemDTOList(templateOtherIncomeItemDTOList);

        RepairOrderTemplateDTO deleteRepairOrderTemplateDTO = repairOrderTemplateService.getRepairOrderTemplateByTemplateName(shopId, null, null, repairOrderTemplateDTO.getTemplateName());
        repairOrderTemplateService.deleteRepairOrderTemplateById(deleteRepairOrderTemplateDTO.getId());
        repairOrderTemplateDTO = repairOrderTemplateService.saveOrUpdateRepairOrderTemplate(repairOrderTemplateDTO);
        return repairOrderTemplateDTO;
      }
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=saveRepairOrderTemplateWithExistingSameNameRepairOrderTemplate");
      LOG.error("shopId:" + shopId + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  /**
   * 施工单结算之后补录里程数
   *
   * @param request
   * @param response
   * @param repairOrderIdStr
   * @param startMileageStr
   */
  @RequestMapping(params = "method=updateStartMileage")
  public void updateStartMileage(HttpServletRequest request, HttpServletResponse response, String repairOrderIdStr, String startMileageStr) {
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      if (shopId == null) {
        return;
      }
      if (StringUtil.isEmpty(repairOrderIdStr) || StringUtil.isEmpty(startMileageStr)) {
        return;
      }

      Long orderId = Long.valueOf(repairOrderIdStr);
      if (orderId == null) {
        return;
      }
      double startMileage = NumberUtil.doubleVal(Double.valueOf(startMileageStr));
      RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(orderId);
      Long vehicleId = repairOrderDTO.getVechicleId();
      VehicleDTO vehicleDTO = vehicleService.findVehicleById(vehicleId);
      vehicleDTO.setStartMileage(startMileage);
      if (startMileage > 0 && repairOrderDTO.getStartDate() != null && repairOrderDTO.getStartDate() >= NumberUtil.longValue(vehicleDTO.getMileageLastUpdateTime())) {
        vehicleDTO.setObdMileage(startMileage);
      }
      vehicleService.updateVehicle(vehicleDTO);
      SolrHelper.doVehicleReindex(shopId, vehicleDTO.getId());
      boolean updateResult = txnService.updateStartMileage(orderId, startMileage);
      if (updateResult) {
        PrintWriter writer = response.getWriter();
        writer.write("更新完成");
        writer.close();
      }
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=updateStartMileage");
      LOG.error("更新进厂里程数失败");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

  }

  @RequestMapping(params = "method=checkRepairOrderStatus")
  public void checkRepairOrderStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    String customerIdStr = request.getParameter("customerId");
    Long customerId = null;
    if (StringUtils.isNotBlank(customerIdStr)) {
      customerId = Long.valueOf(customerIdStr);
    }
    Long shopId = WebUtil.getShopId(request);
    try {
      if (null == customerId) {
        return;
      }

      int num = txnService.countRepairOrderOfNotSettled(shopId, customerId);

      if (num > 0) {
        jsonStr = "error";
      } else {
        jsonStr = "success";
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      Map<String, String> map = new HashMap<String, String>();

      map.put("resu", jsonStr);

      out.write(JsonUtil.mapToJson(map));

      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method=getRepairOrderReceiptNoOfNotSettled")
  @ResponseBody
  public Object getRepairOrderReceiptNoOfNotSettled(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
//    PrintWriter out = response.getWriter();
    String jsonStr = "";
    Map map = new HashMap();
    String customerIdStr = request.getParameter("customerId");
    Long customerId = null;
    if (StringUtils.isNotBlank(customerIdStr)) {
      customerId = Long.valueOf(customerIdStr);
    }
    Long shopId = WebUtil.getShopId(request);

    try {
      if (null == customerId) {
        return null;
      }

      List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderReceiptNoOfNotSettled(shopId, customerId);

      if (CollectionUtils.isEmpty(repairOrderDTOList)) {
        return null;
      } else {
        jsonStr = "success";
        map.put("repair", repairOrderDTOList);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {

      map.put("resu", jsonStr);
      return map;
    }
  }


  @RequestMapping(params = "method=checkUndoneOrder")
  @ResponseBody
  public Object checkUndoneOrder(HttpServletRequest request) throws Exception {
    ITxnService iTxnService = ServiceManager.getService(ITxnService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String vehicleIdStr = request.getParameter("vehicleId");
    String licenceNo = request.getParameter("licenceNo");
    String customerId = request.getParameter("customerId");
    try {
      if (StringUtils.isNotBlank(vehicleIdStr) && StringUtils.isNotBlank(licenceNo) && StringUtils.isNotBlank(customerId)) {
        Long vehicleId = Long.parseLong(vehicleIdStr);
        int count = iTxnService.countUndoneRepairOrderByVehicleId(shopId, vehicleId);
        if (count > 0) {
          return new Result("此车辆有未结算单据，不可进行此操作！", false);
        }

        OrderSearchConditionDTO orderSearchConditionDTO = new OrderSearchConditionDTO();
        orderSearchConditionDTO.setShopId(shopId);
        orderSearchConditionDTO.setCustomerId(customerId == null ? "" : customerId.toString());
        orderSearchConditionDTO.setCustomerOrSupplierId(customerId == null ? "" : customerId.toString());
        orderSearchConditionDTO.setCustomerOrSupplierIds(new String[]{orderSearchConditionDTO.getCustomerId()});
        orderSearchConditionDTO.setOrderType(new String[]{"WASH_BEAUTY", "REPAIR"});
        orderSearchConditionDTO.setOrderStatus(new String[]{"WASH_SETTLED", "SALE_DONE", "SALE_DEBT_DONE", "REPAIR_SETTLED"});
        orderSearchConditionDTO.setVehicle(licenceNo);
        ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
        orderSearchConditionDTO.setNotPaid(true);
        OrderSearchResultListDTO orderSearchResultListDTO = searchOrderService.queryOrders(orderSearchConditionDTO);

        if (orderSearchResultListDTO.getNumFound() > 0) {
          return new Result("此车辆有欠款，不可进行此操作！", false);
        }
      }
    } catch (Exception e) {
      LOG.debug("/customer.do");
      LOG.debug("method=checkUndoneOrder");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("vehicleId:" + vehicleIdStr);
      LOG.error(e.getMessage(), e);
      return new Result("数据异常", false);
    }
    return new Result();
  }

  @RequestMapping(params = "method=validatorDeletedProductOrderRepeal")
  @ResponseBody
  public Object validatorDeletedProductOrderRepeal(HttpServletRequest request, Long orderId, String orderType) {
    try {
      Long shopId = WebUtil.getShopId(request);
      BcgogoOrderDto bcgogoOrderDto = rfiTxnService.getOrderDTOByOrderIdAndType(orderId, shopId, orderType);
      if (bcgogoOrderDto == null) {
        return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
      }

      if (bcgogoOrderDto.getStatementAccountOrderId() != null) {
        return new Result(ValidatorConstant.ORDER_STATEMENT_ACCOUNTED, false);
      }

      List<ProductDTO> deletedProductDTOs = rfiTxnService.getDeletedProductsByOrderDTOs(bcgogoOrderDto);
      if (CollectionUtils.isNotEmpty(deletedProductDTOs)) {
        String resultMsg = rfiTxnService.getDeletedProductMsg(deletedProductDTOs);
        resultMsg += ValidatorConstant.DELETED_PRODUCT_REPEAL_MSG;
        return new Result(resultMsg, false, Result.Operation.CONFIRM_DELETED_PRODUCT.getValue(), null);
      } else {
        return new Result(ValidatorConstant.NO_DELETED_PRODUCT_MSG, true);
      }
    } catch (Exception e) {
      LOG.error("validatorDeletedProductOrderRepeal 验证出错OrderId:{}" + e.getMessage(), orderId, e);
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
  }

  @RequestMapping(params = "method=setProductKind")
  public String setProductKind() {
    return "txn/setProductKind";
  }

  /**
   * 如果销售、施工、，则此单据无法被作废。
   *
   * @param orderType 只限销售，施工，洗车单
   * @param orderId
   * @return Result, 如果可以作废，则返回true, 不能则false.
   */
  @RequestMapping(params = "method=validateRepealStrikeSettled")
  @ResponseBody
  public Result validateRepealStrikeSettled(Model model, HttpServletRequest request, OrderTypes orderType, Long orderId) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null || orderType == null || orderId == null) {
      return new Result("无法作废", "参数为空，无法验证。", false);
    }

    try {
      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId, orderType, orderId);

      if (receivableDTO != null && receivableDTO.getStatementAccountOrderId() != null) {
        return new Result("无法作废", "该单据已被对账，无法作废。", false);
      }
      if (receivableDTO == null || receivableDTO.getStrike() == null || receivableDTO.getStrike() == 0) {
        return new Result("无法作废", "无实收信息，通过验证。", true);
      } else {
        return new Result("无法作废", "该单据已被冲帐结算，无法作废。", false);
      }
    } catch (Exception e) {
      LOG.error("验证单据是否可结算时出错. TxnController.validateRepealable");
      LOG.error(e.getMessage(), e);
      return new Result("无法作废", "验证出错。", false);
    }
  }

  @RequestMapping(params = "method=getOtherIncomeKind")
  @ResponseBody
  public Map getOtherIncomeKind(HttpServletRequest request, HttpServletResponse response) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    Map map = new HashMap();
    Long shopId = WebUtil.getShopId(request);

    List<OtherIncomeKindDTO> otherIncomeKindDTOList = txnService.vagueGetOtherIncomeKind(shopId, (String) request.getParameter("keyWord"));

    map.put("uuid", (String) request.getParameter("uuid"));
    map.put("data", otherIncomeKindDTOList);

    return map;
  }

  @RequestMapping(params = "method=deleteOtherIncomeKind")
  @ResponseBody
  private Map deleteOtherIncomeKind(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long shopId = WebUtil.getShopId(request);

    String otherIncomeKindIdStr = request.getParameter("otherIncomeKindId");

    Map map = new HashMap();

    if (StringUtils.isBlank(otherIncomeKindIdStr)) {
      return map;
    }

    Long otherIncomeKindId = Long.valueOf(otherIncomeKindIdStr);

    try {

      txnService.changeOtherIncomeKindStatus(shopId, otherIncomeKindId, KindStatus.DISABLED);

      map.put("resu", "success");
    } catch (Exception e) {
      map.put("resu", "error");
      LOG.error(e.getMessage(), e);
      LOG.error("method=deleteOtherIncomeKind  otherIncomeId=" + otherIncomeKindIdStr);
    }

    return map;
  }


  @RequestMapping(params = "method=updateOtherIncomeKind")
  @ResponseBody
  public Map updateCategoryName(HttpServletRequest request, HttpServletResponse response) {
    String otherIncomeKindName = request.getParameter("otherIncomeKind");

    String otherIncomeKindIdStr = request.getParameter("otherIncomeKindId");

    Long otherIncomeKindId = null;

    Map map = new HashMap();

    if (StringUtils.isNotBlank(otherIncomeKindIdStr)) {
      otherIncomeKindId = Long.valueOf(otherIncomeKindIdStr);
    }

    if (StringUtils.isBlank(otherIncomeKindName) || null == otherIncomeKindId) {
      map.put("resu", "error");
      map.put("msg", "no otherIncomeKindName or no otherIncomeKindId");
      return map;
    }

    Long shopId = WebUtil.getShopId(request);

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<OtherIncomeKind> otherIncomeKindList = txnService.getOtherIncomeKindByName(shopId, otherIncomeKindName);

    if (CollectionUtils.isEmpty(otherIncomeKindList)) {
      txnService.updateOtherIncomeKind(shopId, otherIncomeKindId, otherIncomeKindName);
      map.put("resu", "success");
    } else if (otherIncomeKindList.size() == 1) {
      OtherIncomeKind otherIncomeKind = otherIncomeKindList.get(0);
      if (KindStatus.DISABLED.equals(otherIncomeKind.getStatus())) {
        if (otherIncomeKind.getId().equals(otherIncomeKindId)) {
          txnService.changeOtherIncomeKindStatus(shopId, otherIncomeKindId, KindStatus.ENABLE);
          map.put("resu", "success");
          map.put("msg", "it is self");
        } else {
          txnService.changeOtherIncomeKindStatus(shopId, otherIncomeKind.getId(), KindStatus.ENABLE);
          txnService.changeOtherIncomeKindStatus(shopId, otherIncomeKindId, KindStatus.DISABLED);
          map.put("resu", "success");
          map.put("msg", "change status for two otherIncomeKindName");
        }
      } else {
        if (otherIncomeKind.getId().equals(otherIncomeKindId)) {

          map.put("resu", "success");
          map.put("msg", "it is self");
        } else {
          map.put("resu", "error");
          map.put("msg", "already has otherIncomeKindName like this");
        }
      }
    } else if (otherIncomeKindList.size() > 1) {
      map.put("resu", "error");
      map.put("msg", "already has otherIncomeKindName like this");
    }

    return map;

  }

  @RequestMapping(params = "method=validateDeleteSupplier")
  @ResponseBody
  private Result validateDeleteSupplier(HttpServletRequest request, SupplierDTO supplierDTO) throws BcgogoException {
    Result result = new Result();
    result.setSuccess(true);
    if (StringUtil.isEmpty(supplierDTO.getIdStr())) {
      result.setSuccess(false);
      result.setMsg("供应商信息异常！");
      return result;
    }
    Long shopId = WebUtil.getShopId(request);
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    SupplierDTO supplierFromDB = supplierService.getSupplierById(NumberUtil.longValue(supplierDTO.getIdStr()), shopId);
    if (supplierFromDB == null || CustomerStatus.DISABLED.equals(supplierFromDB.getStatus())) {
      result.setSuccess(false);
      result.setMsg("供应商不存在或已被删除！");
      return result;
    }
    if (supplierFromDB.getSupplierShopId() != null) {
      result = ServiceManager.getService(RFITxnService.class).validateDeleteOnlineSupplier(shopId, supplierFromDB);
      if (result != null && !result.isSuccess()) {
        return result;
      }
    }
    supplierDTO.setShopId(WebUtil.getShopId(request));
    supplierDTO.setId(NumberUtil.longValue(supplierDTO.getIdStr()));
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    try {
      //validate arrear
      RecOrPayIndexDTO recOrPayIndex = new RecOrPayIndexDTO();
      recOrPayIndex.setShopId(supplierDTO.getShopId());
      recOrPayIndex.setCustomerOrSupplierId(supplierDTO.getId());

      List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(), supplierDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      if (NumberUtil.doubleVal(doubleList.get(0)) > 0) {
        result.setSuccess(false);
        result.setMsg("供应商存在应收或应付款，不能删除！");
        return result;
      }
      doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(), supplierDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
      if (Math.abs(NumberUtil.doubleVal(doubleList.get(0))) > 0) {
        result.setSuccess(false);
        result.setMsg("供应商存在应收款不能删除！");
        return result;
      }

      //validate deposit
      Double totalDeposit = supplierPayableService.getSumDepositBySupplierId(supplierDTO.getId(), WebUtil.getShopId(request));
      if (totalDeposit != null && totalDeposit > 0.001) {
        result.setSuccess(false);
        result.setMsg("供应商存在预付款不能删除！");
        return result;
      }
      //validate unSettledOrder
      Map<String, String> receiptNoMap = new HashMap<String, String>();
      for (PurchaseOrder purchaseOrder : ServiceManager.getService(ITxnService.class).getPurchaseOrderByShopIdAndSupplierId(supplierDTO.getShopId(), supplierDTO.getId())) {
        if (purchaseOrder == null) {
          continue;
        }
        if (OrderUtil.purchaseOrderInProgress.contains(purchaseOrder.getStatusEnum())) {
          receiptNoMap.put(String.valueOf(purchaseOrder.getId()), purchaseOrder.getReceiptNo());
        }
      }
      if (CollectionUtils.isNotEmpty(receiptNoMap.keySet())) {
        result.setMsg("供应商存在未入库的采购单！");
        result.setData(receiptNoMap);
        result.setSuccess(false);
        return result;
      }
      //validate supplierInventory
      if (BcgogoShopLogicResourceUtils.isThroughSelectSupplier(WebUtil.getShopVersion(request).getId())) {
        SupplierInventoryDTO condition = new SupplierInventoryDTO();
        condition.setShopId(WebUtil.getShopId(request));
        condition.setSupplierId(supplierDTO.getId());
        List<SupplierInventory> supplierInventories = ServiceManager.getService(IProductThroughService.class).getSupplierInventory(condition);
        if (CollectionUtil.isNotEmpty(supplierInventories)) {
          for (SupplierInventory inventory : supplierInventories) {
            if (inventory == null) continue;
            if (NumberUtil.doubleVal(inventory.getRemainAmount()) > 0) {
              result.setSuccess(false);
              result.setMsg("存在该供应商的库存，无法删除！");
              return result;
            }
          }
        }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("验证供应商删除出现异常！");
      return result;
    }
  }

  @RequestMapping(params = "method=validatorStoreHouseOrderRepeal")
  @ResponseBody
  public Object validatorStoreHouseOrderRepeal(HttpServletRequest request, Long toStorehouseId, Long orderId, String orderType) {
    try {
      Long shopId = WebUtil.getShopId(request);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        if (toStorehouseId == null) {
          BcgogoOrderDto bcgogoOrderDto = rfiTxnService.getOrderDTOByOrderIdAndType(orderId, shopId, orderType);
          if (bcgogoOrderDto == null) {
            return new Result(ValidatorConstant.ORDER_IS_NULL_MSG, false);
          }
          if (orderType.equals(OrderTypes.REPAIR.toString()) && ArrayUtils.isEmpty(bcgogoOrderDto.getItemDTOs())) {
            return new Result();
          }
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, bcgogoOrderDto.getStorehouseId());
          if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
            return new Result(false);
          }
        } else {
          IStoreHouseService storeHouseService = ServiceManager.getService(IStoreHouseService.class);
          StoreHouseDTO storeHouseDTO = storeHouseService.getStoreHouseDTOById(shopId, toStorehouseId);
          if (storeHouseDTO == null || DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
            return new Result(false);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("validatorStoreHouseOrderRepeal 验证出错OrderId:{}" + e.getMessage(), orderId, e);
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
    return new Result();
  }

  //点击缺料操作之后判断调拨还是入库 检验结果为，true 不缺料，false confirm 是可以选择调拨或者入库，其余为入库操作
  //施工单 缺料
  @RequestMapping(params = "method=validatorLackProductTodo")
  @ResponseBody
  public Object validatorLackProductTodo(HttpServletRequest request, Long orderId, String orderType) {
    try {
      Result result = new Result();
      Long shopId = WebUtil.getShopId(request);
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(WebUtil.getShopVersionId(request))) {
        if (orderType != null && orderType.equals(OrderTypes.REPAIR_PICKING.toString())) {
          result = pickingService.validatorLackProductTodo(shopId, orderId);
        } else if (orderType != null && orderType.equals(OrderTypes.REPAIR.toString())) {
          RepairOrderDTO repairOrderDTO = ServiceManager.getService(ITxnService.class).getRepairOrder(shopId, orderId);
          Set<Long> lackProductIds = new HashSet<Long>();
          if (repairOrderDTO != null && !ArrayUtil.isEmpty(repairOrderDTO.getItemDTOs())) {
            repairOrderDTO.setShopVersionId(WebUtil.getShopVersionId(request));
            repairService.getProductInfo(repairOrderDTO);

            for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
              if (repairOrderItemDTO.getProductId() != null && repairOrderItemDTO.isLack()) {
                lackProductIds.add(repairOrderItemDTO.getProductId());
              }
            }
          }

          if (CollectionUtils.isNotEmpty(lackProductIds) && repairOrderDTO.getStorehouseId() != null) {
            if (ServiceManager.getService(IInventoryService.class).checkBatchProductInventoryInOtherStorehouse(shopId, repairOrderDTO, new ArrayList<Long>(lackProductIds))) {
              return new Result(ValidatorConstant.ALLOCATE_OR_PURCHASE, false, Result.Operation.ALLOCATE_OR_PURCHASE.getValue(), null);
            }
          }
        }
      }
      return result == null ? new Result() : result;
    } catch (Exception e) {
      LOG.error("validatorLackProductTodo 验证出错OrderId:{}" + e.getMessage(), orderId, e);
      return new Result(ValidatorConstant.REQUEST_ERROR_MSG, false);
    }
  }

  @RequestMapping(params = "method=createQualified")
  public String createQualified(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String idStr = request.getParameter("repairOrderId");
    QualifiedCredentialsDTO qualifiedCredentialsDTO = null;

    Long shopId = WebUtil.getShopId(request);
    ShopDTO shopDTO = configService.getShopById(shopId);

    if (StringUtils.isBlank(idStr)) {
      return "/WEB-INF/views/main";
    }

    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    Map<String, String> map = new HashMap<String, String>();

    map.put("二级维护", "二级维护");
    map.put("年检", "年检");
    map.put("小修", "小修");
    map.put("发动机大修", "发动机大修");
    map.put("事故", "事故");
    map.put("二维年检", "二维年检");

    request.setAttribute("repairOrderType", map);
    qualifiedCredentialsDTO = txnService.getQualifiedCredentialsDTO(shopId, Long.valueOf(idStr));

    //如果此单据的合格证已经在数据库中有了，就不用再去order中去数据了
    if (null != qualifiedCredentialsDTO) {
      request.setAttribute("qualifiedCredentialsDTO", qualifiedCredentialsDTO);
      return "/txn/qualifiedCredentials";
    }
    //如果在合格证里没有对应的数据，就去order中取相关信息
    RepairOrderDTO repairOrderDTO = txnService.getRepairOrder(shopId, Long.valueOf(idStr));
    qualifiedCredentialsDTO = new QualifiedCredentialsDTO();
    qualifiedCredentialsDTO.setShopId(shopId);
    qualifiedCredentialsDTO.setShopName(shopDTO.getName());
    if (null == repairOrderDTO) {
      request.setAttribute("qualifiedCredentialsDTO", qualifiedCredentialsDTO);
      return "/txn/qualifiedCredentials";
    }

    VehicleDTO vehicleDTO = userService.getVehicleById(repairOrderDTO.getVechicleId());
    CustomerDTO customerDTO = userService.getCustomerDTOByCustomerId(repairOrderDTO.getCustomerId(), shopId);
    qualifiedCredentialsDTO.setOrderId(repairOrderDTO.getId());
    qualifiedCredentialsDTO.setCustomerId(repairOrderDTO.getCustomerId());
    qualifiedCredentialsDTO.setBrand(repairOrderDTO.getBrand());
    qualifiedCredentialsDTO.setModel(repairOrderDTO.getModel());
    qualifiedCredentialsDTO.setLicenseNo(vehicleDTO.getLicenceNo());
    qualifiedCredentialsDTO.setCustomer(customerDTO.getName());
    qualifiedCredentialsDTO.setEngineNo(vehicleDTO.getEngineNo());
    qualifiedCredentialsDTO.setChassisNumber(vehicleDTO.getChassisNumber());
    qualifiedCredentialsDTO.setStartDate(repairOrderDTO.getStartDate());
    qualifiedCredentialsDTO.setEndDate(repairOrderDTO.getEndDate());
    qualifiedCredentialsDTO.setProducedMileage(repairOrderDTO.getStartMileage() == null ? "" : repairOrderDTO.getStartMileage().toString());

    request.setAttribute("qualifiedCredentialsDTO", qualifiedCredentialsDTO);
    return "/txn/qualifiedCredentials";
  }

  @RequestMapping(params = "method=saveOrUpdateQualifiedCredentials")
  @ResponseBody
  public Object saveOrUpdateQualifiedCredentials(HttpServletRequest request, QualifiedCredentialsDTO qualifiedCredentialsDTO) throws Exception {
    Long shopId = WebUtil.getShopId(request);

    qualifiedCredentialsDTO.setShopId(shopId);

    Map map = new HashMap();
    if (null == qualifiedCredentialsDTO) {
      map.put("saveResult", "error");
      map.put("saveErrorMsg", "传入后台的参数都为空");
      return map;
    }

    if (null == qualifiedCredentialsDTO) {
      map.put("saveResult", "error");
      map.put("saveErrorMsg", "没有施工单ID");
    }


    if (StringUtils.isNotBlank(qualifiedCredentialsDTO.getStartDateStr())) {
      qualifiedCredentialsDTO.setStartDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, qualifiedCredentialsDTO.getStartDateStr()));
    }

    if (StringUtils.isNotBlank(qualifiedCredentialsDTO.getEndDateStr())) {
      qualifiedCredentialsDTO.setEndDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, qualifiedCredentialsDTO.getEndDateStr()));
    }

    try {
      txnService.saveOrUpdateQualifiedCredentials(qualifiedCredentialsDTO);

      map.put("saveResult", "success");

      List<VehicleDTO> vehicleDTOList = userService.getVehicleByLicenceNo(shopId, qualifiedCredentialsDTO.getLicenseNo());

      Long[] vehicleIds = ServiceManager.getService(IBaseProductService.class).
        saveVehicle(null, null, null,
          null, qualifiedCredentialsDTO.getBrand(), qualifiedCredentialsDTO.getModel(), null,
          null);

      if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
        VehicleDTO vehicleDTO = vehicleDTOList.get(0);

        vehicleDTO.setEngineNo(qualifiedCredentialsDTO.getEngineNo());
        vehicleDTO.setChassisNumber(qualifiedCredentialsDTO.getChassisNumber());
        vehicleDTO.setModel(qualifiedCredentialsDTO.getModel());
        vehicleDTO.setBrand(qualifiedCredentialsDTO.getBrand());
        vehicleDTO.setBrandId(vehicleIds[0]);
        vehicleDTO.setModelId(vehicleIds[1]);
        userService.updateVehicle(vehicleDTO);
      }
      //保存操作记录
      if (qualifiedCredentialsDTO.getOrderId() != null) {
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
          new OperationLogDTO(qualifiedCredentialsDTO.getShopId(), WebUtil.getUserId(request), qualifiedCredentialsDTO.getOrderId(), ObjectTypes.REPAIR_ORDER, OperationTypes.QUALIFIED_CREDENTIALS));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      map.put("saveResult", "error");
      map.put("saveErrorMsg", "保存失败");
    }

    return map;
  }

  @RequestMapping(params = "method=printQualifiedCredentials")
  public void printQualifiedCredentials(HttpServletRequest request, HttpServletResponse response, Long orderId) {
    if (null == orderId) {
      return;
    }

    QualifiedCredentialsDTO qualifiedCredentialsDTO = txnService.getQualifiedCredentialsDTO(WebUtil.getShopId(request), orderId);

    if (null == qualifiedCredentialsDTO) {
      return;
    }

    try {
      IPrintService printService = ServiceManager.getService(IPrintService.class);
      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.QUALIFIED_CREDENTIAL);

      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");

      if (null != printTemplateDTO) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String str = new String(bytes, "UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();

        String myTemplateName = "QUALIFIED_CREDENTIAL" + String.valueOf(WebUtil.getShopId(request));

        String myTemplate = str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        //把数据填入上下文

        context.put("qualifiedCredentialsDTO", qualifiedCredentialsDTO);
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body><h1>没有可用的模板</h1></body><html>");
      }

      out.close();

    } catch (Exception e) {
      LOG.debug("/txn.do");
      LOG.debug("orderId:" + orderId);
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=getLatestCustomerPrice")
  @ResponseBody
  public Result getLatestCustomerPrice(HttpServletRequest request, Long productId, Long customerId, Long serviceId) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null || customerId == null) {
      return new Result(false);
    }
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    try {
      if (productId != null) {
        OrderSearchConditionDTO condition = new OrderSearchConditionDTO();
        condition.setProductIds(new String[]{productId.toString()});
        condition.setCustomerOrSupplierIds(new String[]{customerId.toString()});
        condition.setShopId(shopId);
        condition.setOrderType(new String[]{OrderTypes.SALE.toString(), OrderTypes.REPAIR.toString()});
        condition.setOrderStatus(new String[]{OrderStatus.SALE_DONE.toString(), OrderStatus.REPAIR_SETTLED.toString()});
        condition.setSort("order_created_time desc");
        condition.setRowStart(0);
        condition.setPageRows(1);
        OrderSearchResultListDTO resultDTO = searchOrderService.queryOrderItems(condition);
        if (resultDTO == null || CollectionUtils.isEmpty(resultDTO.getOrderItems()) || CollectionUtil.getFirst(resultDTO.getOrderItems()).getItemPrice() == null) {
          return new Result(true, null);
        }
        return new Result(true, CollectionUtil.getFirst(resultDTO.getOrderItems()));
      } else if (serviceId != null) {
        OrderSearchConditionDTO condition = new OrderSearchConditionDTO();
        condition.setServiceIds(new String[]{serviceId.toString()});
        condition.setCustomerOrSupplierIds(new String[]{customerId.toString()});
        condition.setShopId(shopId);
        condition.setOrderType(new String[]{OrderTypes.WASH_BEAUTY.toString(), OrderTypes.REPAIR.toString()});
        condition.setOrderStatus(new String[]{OrderStatus.WASH_SETTLED.toString(), OrderStatus.REPAIR_SETTLED.toString()});
        condition.setSort("order_created_time desc");
        condition.setRowStart(0);
        condition.setPageRows(1);
        OrderSearchResultListDTO resultDTO = searchOrderService.queryOrderItems(condition);
        if (resultDTO == null || CollectionUtils.isEmpty(resultDTO.getOrderItems()) || CollectionUtil.getFirst(resultDTO.getOrderItems()).getItemPrice() == null) {
          return new Result(true, null);
        }
        return new Result(true, CollectionUtil.getFirst(resultDTO.getOrderItems()));
      } else {
        return new Result(false);
      }
    } catch (Exception e) {
      LOG.error("txn.do?method=getLatestCustomerPrice 出错, shopId:{}, productId:{}, customerId:{}", new Object[]{shopId, productId, customerId});
      LOG.error(e.getMessage(), e);
      return new Result(false);
    }
  }


  @RequestMapping(params = "method=getQualifiedCredentials")
  @ResponseBody
  public Object getQualifiedCredentials(HttpServletRequest request, Long orderId) {
    if (null == orderId) {
      return null;
    }

    QualifiedCredentialsDTO qualifiedCredentialsDTO = txnService.getQualifiedCredentialsDTO(WebUtil.getShopId(request), orderId);

    return qualifiedCredentialsDTO;
  }

  //逻辑删除客户服务提醒
  @RequestMapping(params = "method=cancelRemindEventById")
  @ResponseBody
  public String cancelRemindEventById(HttpServletRequest request) {
    try {
      Long remindEventId = null;
      String idStr = request.getParameter("idStr");
      if (!StringUtil.isEmpty(idStr)) {
        remindEventId = NumberUtil.longValue(idStr);
      }
      if (remindEventId != null) {
        ServiceManager.getService(ITxnService.class).cancelCustomerRemindEventById(remindEventId);
        //更新缓存
        RemindEventDTO remindEventDTO = txnService.getRemindEventById(remindEventId);
        RemindEventType type = RemindEventType.valueOf(remindEventDTO.getEventType());
        txnService.updateRemindCountInMemcacheByTypeAndShopId(type, remindEventDTO.getShopId());
      }
      RemindEventStrategy customerRemindEventStrategy = this.remindEventStrategySelector.selectStrategy(RemindEventType.CUSTOMER_SERVICE);
      Long shopId = WebUtil.getShopId(request);
      Long flashTime = DateUtil.getToday(DateUtil.YEAR_MONTH_DATE, new Date()) - 1;
      int customerServiceJobNumber = customerRemindEventStrategy.countRemindEvent(shopId, null, null, flashTime);
      int countCustomerRemindIsOverdue = customerRemindEventStrategy.countRemindEvent(shopId, true, false, flashTime);
      int countCustomerRemindIsNotOverdue = customerRemindEventStrategy.countRemindEvent(shopId, false, false, flashTime);
      int countCustomerRemindHasRemind = customerRemindEventStrategy.countRemindEvent(shopId, null, true, flashTime);
      return customerServiceJobNumber + "," + countCustomerRemindIsOverdue + "," + countCustomerRemindIsNotOverdue + "," + countCustomerRemindHasRemind;
    } catch (Exception e) {
      LOG.error("逻辑删除客户服务提醒出错！");
      LOG.error(e.getMessage(), e);
      return "error";
    }
  }


  @RequestMapping(params = "method=showOperationLog")
  @ResponseBody
  public List<OperationLogDTO> showOperationLog(HttpServletRequest request) {
    String orderTypeStr = request.getParameter("orderType");
    String orderIdStr = request.getParameter("orderId");
    String fromPage = request.getParameter("fromPage");
    ObjectTypes type = null;
    Long orderId = null;

    if ("purchase".equals(orderTypeStr)) {
      type = ObjectTypes.PURCHASE_ORDER;
    } else if ("purchase_return".equals(orderTypeStr)) {
      type = ObjectTypes.PURCHASE_RETURN_ORDER;
    } else if ("sale".equals(orderTypeStr)) {
      type = ObjectTypes.SALE_ORDER;
    } else if ("sale_return".equals(orderTypeStr)) {
      type = ObjectTypes.SALE_RETURN_ORDER;
    }
    if (!StringUtil.isEmpty(orderIdStr)) {
      orderId = Long.parseLong(orderIdStr);
    }

    List<OperationLogDTO> operationLogDTOList = null;
    if (type == null || orderId == null) {
      return operationLogDTOList;
    }
    operationLogDTOList = ServiceManager.getService(IOperationLogService.class).getOprationLogByObjectId(type, orderId);
    List<OperationLogDTO> result = new ArrayList<OperationLogDTO>();
    //如果是待办销售单或者是销售退货单，userId为买方，需要在页面标明
    if (CollectionUtil.isNotEmpty(operationLogDTOList)) {
      for (OperationLogDTO operationLogDTO : operationLogDTOList) {
        UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(operationLogDTO.getUserId());
        //本店自己操作的用店员名字，卖家操作的，用批发商的店铺名称
        if (userDTO.getShopId().equals(operationLogDTO.getShopId())) {
          if ("SALE_NEW_ORDER".equals(fromPage)) {
            operationLogDTO.setUserName(userDTO.getName() + "(客户)");
          } else {
            operationLogDTO.setUserName(userDTO.getName());
          }
        } else {
          ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(userDTO.getShopId());
          if (ObjectTypes.SALE_ORDER.equals(type) || ObjectTypes.SALE_RETURN_ORDER.equals(type)) {
            operationLogDTO.setUserName(shopDTO.getName() + "(客户)");
          } else {
            if ("SALE_NEW_ORDER".equals(fromPage)) {
              operationLogDTO.setUserName(userDTO.getName());
            } else {
              operationLogDTO.setUserName(shopDTO.getName() + "(供应商)");
            }

          }
        }
        //采购单
        if (ObjectTypes.PURCHASE_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建采购订单");
          if (OperationTypes.UPDATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("修改采购订单");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废采购订单");
          if (OperationTypes.STORAGE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("生成入库单");
          if (OperationTypes.ACCEPT.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("接受采购订单");
          if (OperationTypes.REFUSE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("拒绝采购订单");
          if (OperationTypes.DISPATCH.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("发货");
          if (OperationTypes.SELL_STOP.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("终止销售");
        }
        //入库退货单
        if (ObjectTypes.PURCHASE_RETURN_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建入库退货单");
          if (OperationTypes.UPDATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("修改入库退货单");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废入库退货单");
          if (OperationTypes.ACCEPT.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("接受退货");
          if (OperationTypes.REFUSE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("拒绝退货");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("结算");
        }
        //销售单
        if (ObjectTypes.SALE_ORDER.equals(operationLogDTO.getObjectType())) {
          if (userDTO.getShopId().equals(operationLogDTO.getShopId())) {
            if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建销售单");
            if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废销售单");
          } else {
            if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建采购订单");
            if (OperationTypes.UPDATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("修改采购订单");
            if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("买家终止交易");
          }
          if (OperationTypes.ACCEPT.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("接受订单");
          if (OperationTypes.REFUSE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("拒绝订单");
          if (OperationTypes.DISPATCH.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("发货");
          if (OperationTypes.SELL_STOP.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("终止销售");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("结算");
        }
        //销售退货单
        if (ObjectTypes.SALE_RETURN_ORDER.equals(operationLogDTO.getObjectType())) {
          if (userDTO.getShopId().equals(operationLogDTO.getShopId())) {
            if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建销售退货单");
            if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType()))
              operationLogDTO.setContent("作废销售退货单");
          } else {
            if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建入库退货单");
            if (OperationTypes.UPDATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("修改入库退货单");
            if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType()))
              operationLogDTO.setContent("作废入库退货单");
          }
          if (OperationTypes.ACCEPT.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("接受退货");
          if (OperationTypes.REFUSE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("拒绝退货");
          if (OperationTypes.DISPATCH.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("退货入库");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("结算");
        }
        result.add(operationLogDTO);
      }
    }
    return result;
  }

  @RequestMapping(params = "method=getSupplierInventory")
  @ResponseBody
  public Object getSupplierInventory(HttpServletRequest request, SupplierInventoryDTO condition, Long orderId, Long outStorageItemId) {
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    Long shopId = WebUtil.getShopId(request);
    List<SupplierInventoryDTO> supplierInventoryDTOList = new ArrayList<SupplierInventoryDTO>();
    condition.setShopId(shopId);
    Double supplierInventoryTotal = 0d;
    try {
      if (condition != null && OrderUtil.inStorageOrders.contains(condition.getOrderType())) {
        if (!ArrayUtil.isEmpty(condition.getProductIds())) {
          supplierInventoryDTOList = productThroughService.getSupplierInventoryDTOsWithOtherStorehouse(condition.getShopId(), condition.getProductIds()[0], condition.getStorehouseId());
        }
      } else {
        List<SupplierInventory> supplierInventoryList = productThroughService.getSupplierInventory(condition);
        if (CollectionUtil.isEmpty(supplierInventoryList)) {
          return supplierInventoryDTOList;
        }
        for (SupplierInventory inventory : supplierInventoryList) {
          if (inventory == null) continue;
          supplierInventoryDTOList.add(inventory.toDTO());
          supplierInventoryTotal += NumberUtil.round(inventory.getRemainAmount());
        }
        if (OrderStatus.STOCKING.equals(condition.getOrderStatus()) && orderId != null) {
          List<OutStorageRelationDTO> relationDTOList = productThroughService.getOutStorageRelation(shopId, orderId, OrderTypes.SALE, outStorageItemId, condition.getProductId());
          Map<String, OutStorageRelationDTO> relationDTOMap = new HashMap<String, OutStorageRelationDTO>();
          if (CollectionUtil.isNotEmpty(relationDTOList)) {
            for (OutStorageRelationDTO relationDTO : relationDTOList) {
              relationDTOMap.put(ObjectUtil.generateKey(relationDTO.getProductId(), relationDTO.getRelatedSupplierId(), relationDTO.getSupplierType()), relationDTO);
            }
            for (SupplierInventoryDTO inventoryDTO : supplierInventoryDTOList) {
              OutStorageRelationDTO relationDTO = relationDTOMap.get(ObjectUtil.generateKey(inventoryDTO.getProductId(), inventoryDTO.getSupplierId(), inventoryDTO.getSupplierType()));
              if (relationDTO != null) {
                inventoryDTO.setTempAmount(relationDTO.getSupplierRelatedAmount());
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    List result = new ArrayList();
    result.add(supplierInventoryDTOList);
    result.add(supplierInventoryTotal);
    return result;
  }

  @RequestMapping(params = "method=getSupplierInventoryByStorehouse")
  @ResponseBody
  public Object getSupplierInventoryByStorehouse(HttpServletRequest request, SupplierInventoryDTO condition) {
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    Map<Long, List<SupplierInventoryDTO>> supplierInventoryDTOMap = new HashMap<Long, List<SupplierInventoryDTO>>();
    condition.setShopId(WebUtil.getShopId(request));
    try {
      if (condition != null && OrderUtil.inStorageOrders.contains(condition.getOrderType())) {
        Set<Long> productIds = new HashSet<Long>();
        for (Long productId : condition.getProductIds()) {
          productIds.add(productId);
        }
        supplierInventoryDTOMap = productThroughService.getSupplierInventoryDTOsWithOtherStorehouseMap(condition.getShopId(), productIds, condition.getStorehouseId());
      } else {
        supplierInventoryDTOMap = ServiceManager.getService(IProductThroughService.class).getSupplierInventoryDTOByStorehouse(condition);
      }
      if (supplierInventoryDTOMap.isEmpty()) {
        return supplierInventoryDTOMap;
      }
      Map<String, List<SupplierInventoryDTO>> resultMap = new HashMap<String, List<SupplierInventoryDTO>>();
      for (Map.Entry<Long, List<SupplierInventoryDTO>> entry : supplierInventoryDTOMap.entrySet()) {
        resultMap.put(entry.getKey().toString(), entry.getValue());
      }
      return resultMap;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=getServiceTime")
  @ResponseBody
  public Object getServiceTime(HttpServletRequest request) {
    Map timeMap = new HashMap();
    timeMap.put("currentTime", DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.STANDARD));
    return timeMap;
  }

  @RequestMapping(params = "method=getRepairAndDraftOrders")
  @ResponseBody
  public Object getRepairAndDraftOrders(HttpServletRequest request, DraftOrderSearchDTO draftOrderSearchDTO) {
    try {
      IDraftOrderService draftOrderService = ServiceManager.getService(IDraftOrderService.class);
      if (draftOrderSearchDTO == null || com.bcgogo.common.StringUtil.isEmpty(draftOrderSearchDTO.getStartPageNo()) || NumberUtil.intValue(draftOrderSearchDTO.getStartPageNo()) < 0) {
        return null;
      }
      draftOrderSearchDTO.convertOrderType(draftOrderSearchDTO.getOrderTypes());
      draftOrderSearchDTO.setShopId(WebUtil.getShopId(request));
      draftOrderSearchDTO.setUserId(WebUtil.getUserId(request));
      int countDraftOrders = draftOrderService.countDraftOrders(draftOrderSearchDTO);
      int countRepairOrders = txnService.countRepairOrders(draftOrderSearchDTO.getShopId(), draftOrderSearchDTO.getVehicleId());
      int totalNum = countDraftOrders + countRepairOrders;
      Pager pager = new Pager(totalNum, NumberUtil.intValue(String.valueOf(draftOrderSearchDTO.getStartPageNo()), 1));
      List<Object> result = new ArrayList<Object>();
      Map<String, Object> data = new HashMap<String, Object>();
      draftOrderSearchDTO.setPager(pager);
      data.put("draftOrderData", txnService.getRepairAndDraftOrders(draftOrderSearchDTO));
      data.put("countOrderTypeList", draftOrderService.countDraftOrderByOrderType(draftOrderSearchDTO));
      data.put("countDraftOrders", countDraftOrders);
      data.put("countRepairOrders", countRepairOrders);
      result.add(data);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.error("/txn.do?method=getRepairAndDraftOrders,shopId={},userId ={}", request.getSession().getAttribute("shopId"), request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=showOrderOperationLog")
  @ResponseBody
  public List<OperationLogDTO> showOrderOperationLog(HttpServletRequest request) {
    String orderTypeStr = request.getParameter("orderType");
    String orderIdStr = request.getParameter("orderId");
    if (StringUtils.isEmpty(orderTypeStr) || StringUtils.isEmpty(orderIdStr)) {
      LOG.error("orderType is null or orderId is null!");
      return null;
    }
    ObjectTypes type = null;
    Long orderId = Long.parseLong(orderIdStr);

    if ("purchase".equals(orderTypeStr)) {
      type = ObjectTypes.PURCHASE_ORDER;
    } else if ("purchase_return".equals(orderTypeStr)) {
      type = ObjectTypes.PURCHASE_RETURN_ORDER;
    } else if ("sale".equals(orderTypeStr)) {
      type = ObjectTypes.SALE_ORDER;
    } else if ("sale_return".equals(orderTypeStr)) {
      type = ObjectTypes.SALE_RETURN_ORDER;
    } else if ("inventory".equals(orderTypeStr)) {
      type = ObjectTypes.INVENTORY_ORDER;
    } else if ("wash".equals(orderTypeStr)) {
      type = ObjectTypes.WASH_ORDER;
    } else if ("repair".equals(orderTypeStr)) {
      type = ObjectTypes.REPAIR_ORDER;
    }

    List<OperationLogDTO> operationLogDTOList = null;
    if (type == null || orderId == null) {
      return operationLogDTOList;
    }
    operationLogDTOList = ServiceManager.getService(IOperationLogService.class).getOprationLogByObjectId(type, orderId);
    List<OperationLogDTO> result = new ArrayList<OperationLogDTO>();

    if (CollectionUtil.isNotEmpty(operationLogDTOList)) {
      for (OperationLogDTO operationLogDTO : operationLogDTOList) {

        //采购单
        if (ObjectTypes.PURCHASE_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建单据");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.STORAGE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("生成入库单");
        }

        //入库单
        if (ObjectTypes.INVENTORY_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建单据&单据结算");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("欠款结算");
          if (OperationTypes.STATEMENT_ACCOUNT.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("单据对账");
        }

        //入库退货单
        if (ObjectTypes.PURCHASE_RETURN_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建单据&单据结算");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.STATEMENT_ACCOUNT.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("单据对账");
        }
        //销售单
        if (ObjectTypes.SALE_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建单据&单据结算");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("欠款结算");
          if (OperationTypes.STATEMENT_ACCOUNT.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("单据对账");
        }
        //销售退货单
        if (ObjectTypes.SALE_RETURN_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建单据&单据结算");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.STATEMENT_ACCOUNT.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("单据对账");
        }
        //洗车单
        if (ObjectTypes.WASH_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("创建单据&单据结算");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("欠款结算");
          if (OperationTypes.STATEMENT_ACCOUNT.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("单据对账");
        }
        //施工单
        if (ObjectTypes.REPAIR_ORDER.equals(operationLogDTO.getObjectType())) {
          if (OperationTypes.CREATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("派单");
          if (OperationTypes.UPDATE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("改单");
          if (OperationTypes.FINISH.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("完工");
          if (OperationTypes.INVALID.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("作废单据");
          if (OperationTypes.DEBT_SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("单据结算");
          if (OperationTypes.SETTLE.equals(operationLogDTO.getOperationType())) operationLogDTO.setContent("欠款结算");
          if (OperationTypes.STATEMENT_ACCOUNT.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("单据对账");
          if (OperationTypes.INSURANCE_ORDER.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("生成保险理赔");
          if (OperationTypes.QUALIFIED_CREDENTIALS.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("生成合格证");
          if (OperationTypes.REPAIR_ORDER_SECONDARY.equals(operationLogDTO.getOperationType()))
            operationLogDTO.setContent("生成结算附表");
        }
        result.add(operationLogDTO);
      }
    }
    return result;
  }

  @RequestMapping(params = "method=sendMessageRemind")
  @ResponseBody
  public Object sendMessageRemind(HttpServletRequest request, MessageScene sceneApp, MessageSwitchStatus statusApp, MessageScene sceneSms, MessageSwitchStatus statusSms, CustomerRemindSms customerRemindSms) throws Exception {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    String noRemind = request.getParameter("noRemind");
    String id = request.getParameter("id");
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    if (shopId == null) {
      LOG.error("txn.sendMessageRemind,shopId is error:" + shopId);
      return null;
    }
    if (StringUtils.isBlank(customerRemindSms.getLicenceNo())) throw new Exception("licenceNo is null");


    if (StringUtils.isNotEmpty(noRemind)) {
      if (noRemind.equals("noRemindChecked") || noRemind.equals("noRemindUnchecked")) {
        //保存用户勾选项（App和Sms）
        if (sceneApp != null && statusApp != null) {
          notificationService.SaveOrUpdateMessageSwitch(shopId, sceneApp, statusApp);
          if (statusApp.toString().equals("ON")) {
            customerRemindSms.setAppFlag(true);
          } else {
            customerRemindSms.setAppFlag(false);
          }
        }
        if (sceneSms != null && statusSms != null) {
          notificationService.SaveOrUpdateMessageSwitch(shopId, sceneSms, statusSms);
          if (statusSms.toString().equals("ON")) {
            customerRemindSms.setSmsFlag(true);
          } else {
            customerRemindSms.setSmsFlag(false);
          }
        }
        if (noRemind.equals("noRemindChecked")) {
          userService.saveOrUpdateUserSwitch(shopId, "SETTLED_REMINDER", "OFF");
        }
      } else if (noRemind.equals("afterNoRemindChecked")) {
        MessageSwitchDTO appMessageSwitchDTO = ServiceManager.getService(INotificationService.class).getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MOBILE_APP);
        MessageSwitchDTO smsMessageSwitchDTO = ServiceManager.getService(INotificationService.class).getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MOBILE_SMS);
        if (appMessageSwitchDTO != null) {
          if (appMessageSwitchDTO.getStatus().toString().equals("ON")) {
            customerRemindSms.setAppFlag(true);
          } else {
            customerRemindSms.setAppFlag(false);
          }
        } else {
          customerRemindSms.setAppFlag(true);
        }
        if (smsMessageSwitchDTO != null) {
          if (smsMessageSwitchDTO.getStatus().toString().equals("ON")) {
            customerRemindSms.setSmsFlag(true);
          } else {
            customerRemindSms.setSmsFlag(false);
          }
        } else {
          customerRemindSms.setSmsFlag(false);
        }

      }
    }
    if (customerRemindSms.isAppFlag() || customerRemindSms.isSmsFlag()) {
      //设置施工单发送状态值
      txnService.updateRepairOrderMessageFlag(shopId, "SEND", Long.parseLong(id));
      customerRemindSms.setShopId(shopId);
      customerRemindSms = ServiceManager.getService(ISmsService.class).sendCustomerServiceRemindMessage(customerRemindSms);
      if (customerRemindSms != null) {
        ContactDTO contactDTO = null;
        if (StringUtils.isNotBlank(customerRemindSms.getMobile())) {
          VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).updateVehicleMobile(shopId, customerRemindSms.getLicenceNo(), customerRemindSms.getMobile());
          if (vehicleDTO != null) {
            contactDTO = new ContactDTO(vehicleDTO.getId());
          }
        } else {
          VehicleDTO vehicleDTO = ServiceManager.getService(IVehicleService.class).getVehicleDTOByLicenceNo(shopId, customerRemindSms.getLicenceNo());
          if (vehicleDTO != null) {
            if (StringUtils.isNotBlank(vehicleDTO.getMobile())) {
              contactDTO = new ContactDTO(vehicleDTO.getId());
            } else {
              List<CustomerVehicleDTO> customerVehicleDTOList = ServiceManager.getService(IUserService.class).getCustomerVehicleByVehicleId(vehicleDTO.getId());
              if (CollectionUtils.isNotEmpty(customerVehicleDTOList)) {
                Long customerId = CollectionUtil.getFirst(customerVehicleDTOList).getCustomerId();
                Map<Long, ContactDTO> mainContactDTOMap = ServiceManager.getService(IContactService.class).getMainContactDTOMapByCusIds(customerId);
                contactDTO = mainContactDTOMap.get(customerId);
              }
            }
          }
        }
        if (contactDTO != null) {
          return ServiceManager.getService(ISendSmsService.class).sendSms(shopId, userId, customerRemindSms.getContent(), customerRemindSms.isAppFlag(), customerRemindSms.isSmsFlag(), customerRemindSms.isTemplateFlag(), contactDTO);
        }
      }
    } else {
      return null;
    }
    return null;
  }

  @RequestMapping(params = "method=judgeCustomerType")
  @ResponseBody
  public Object judgeCustomerType(HttpServletRequest request) throws Exception {
    ISearchCustomerSupplierService searchService = ServiceManager.getService(ISearchCustomerSupplierService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserGroupService userGroupService = ServiceManager.getService(IUserGroupService.class);
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter userWriter = userDaoManager.getWriter();
    List result = new ArrayList();
    Long shopId = WebUtil.getShopId(request);
    Long userId = WebUtil.getUserId(request);
    if (shopId == null) {
      LOG.error("txn.judgeCustomerType,shopId is error:" + shopId);
      return null;
    }
    MessageSwitchDTO appMessageSwitchDTO = ServiceManager.getService(INotificationService.class).getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MOBILE_APP);
    MessageSwitchDTO smsMessageSwitchDTO = ServiceManager.getService(INotificationService.class).getMessageSwitchDTOByShopIdAndScene(shopId, MessageScene.MOBILE_SMS);
    if (appMessageSwitchDTO != null) {//判断是否打开app按钮（默认开）
      if (appMessageSwitchDTO.getStatus().toString().equals("ON")) {
        result.add(true);
      } else {
        result.add(false);
      }
    } else {
      result.add(true);
    }
    if (smsMessageSwitchDTO != null) {  //判断是否打开短信按钮（默认关）
      if (smsMessageSwitchDTO.getStatus().toString().equals("ON")) {
        result.add(true);
      } else {
        result.add(false);
      }
    } else {
      result.add(false);
    }
    //获取是否是老板权限
    List<UserGroupUser> userGroupUsers = userWriter.getUserGroupUser(userId);
    if (userGroupUsers != null) {
      if (CollectionUtils.isNotEmpty(userGroupUsers)) {
        List<UserGroup> userGroups = userGroupService.getUserGroupByIds(userGroupUsers.get(0).getUserGroupId());
        if (userGroups != null) {
          if (CollectionUtils.isNotEmpty(userGroups)) {
            result.add(userGroups.get(0).getName());
          }
        }
      }
    }

    return result;

  }

  @RequestMapping(params = "method=exportRepairOrder")
  public void exportRepairOrder(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("repairOrderId") String repairOrderId) {
    OutputStream os = null;// 取得输出流
    try {
      RepairOrderDTO repairOrderDTO = null;
      if (StringUtils.isNotEmpty(repairOrderId) && !"null".equals(repairOrderId)) {
        repairOrderDTO = getRepairOrderInfo(model, request, repairOrderId);
        if (repairOrderDTO == null) {
          repairOrderDTO = new RepairOrderDTO();
        }
        Long shopId = WebUtil.getShopId(request);
        ShopDTO shopDTO = configService.getShopById(shopId);
        if (shopDTO == null) {
          shopDTO = new ShopDTO();
        }
        os = response.getOutputStream();
        //组装导出Excel文件名称
        StringBuilder sb = new StringBuilder();
        if (null != repairOrderDTO.getLicenceNo()) {
          sb.append(repairOrderDTO.getLicenceNo()).append(".xls");
        } else {
          sb.append("江苏省结算清单").append(".xls");
        }
        //  sb.append("结算清单").append(StringUtil.valueOf(shopDTO.getName())).append(StringUtil.valueOf(repairOrderDTO.getReceiptNo())).append(".xls");
        //设置文件名称格式 ，字符集
        response.setHeader("Content-disposition", "attachment; filename="
          + new String(sb.toString().getBytes("GB2312"), "ISO8859-1"));
        response.setContentType("application/msexcel");// 定义输出类型
        QualifiedCredentialsDTO qualifiedCredentialsDTO = txnService.getQualifiedCredentialsDTO(shopId, Long.valueOf(repairOrderId));
        if (qualifiedCredentialsDTO == null) {
          qualifiedCredentialsDTO = new QualifiedCredentialsDTO();
        }
        ExportOrderListExcel.createExcel(os, repairOrderDTO, shopDTO, qualifiedCredentialsDTO);
      }
    } catch (Exception e) {
      LOG.error("/txn.do");
      LOG.error("method=exportRepairOrder");
      LOG.error("导出江苏省结算清单excel");
      LOG.error("shopId:" + WebUtil.getShopId(request) + ",repairOrderId:" + repairOrderId);
      LOG.error(e.getMessage(), e);
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException ioe) {
          LOG.error(ioe.getMessage(), ioe);
        }
      }
    }
  }

  //判断省份
  public String transformProvinceToType(ShopDTO shopDTO) {
    String provinceCode = ""; //默认江苏省
    if (shopDTO != null && shopDTO.getProvince() != null) {
      Long province = shopDTO.getProvince();
      if ("1010".equals(province.toString())) {
        provinceCode = "jiangsu";
      }
    }
    return provinceCode;
  }

  /**
   * 打开空白单据
   * @param model
   * @param request
   * @param response
   * @return 调用invoicing，打开施工销售页面
   * @throws Exception
   */
  @RequestMapping(params = "method=getBlankRepairOrder")
  public String getBlankRepairOrderByOrderId(ModelMap model, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
    IConsumingService consumingService=ServiceManager.getService(ConsumingService.class);
    IAppUserService appUserService=ServiceManager.getService(AppUserService.class);
    Long consumingRecordId=null;
    Long orderId=null;
    String appUserNo=null;
    Long shopId=WebUtil.getShopId(request);
    CouponConsumeRecordDTO couponConsumeRecordDTO=null;
    CustomerDTO customerDTO=new CustomerDTO();
    VehicleDTO vehicleDTO=new VehicleDTO();
    RepairOrderDTO repairOrderDTO=new RepairOrderDTO();
    if(shopId!=null) {
      if (!StringUtils.isBlank(request.getParameter("consumingRecordId"))) {
        consumingRecordId = Long.parseLong(request.getParameter("consumingRecordId"));
      }
      if (consumingRecordId != null) {
        couponConsumeRecordDTO = consumingService.getCouponConsumeRecordById(consumingRecordId);
        //判断是否已存在代金券消费记录和单据
        if (couponConsumeRecordDTO != null) {
          orderId = couponConsumeRecordDTO.getOrderId();
          appUserNo = couponConsumeRecordDTO.getAppUserNo();
          //如果已存在订单号，则直接通过订单号打开该订单
          if (orderId != null) {
            if("洗车美容".equals(couponConsumeRecordDTO.getProduct())) {
              return "redirect:/washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + orderId.toString();
            }
            else if("施工销售".equals(couponConsumeRecordDTO.getProduct())){
              return "redirect:/txn.do?method=getRepairOrder&repairOrderId=" + orderId.toString();
            }
            else{
              return "redirect:/txn.do?method=getRepairOrder&repairOrderId=";
            }
          }
          //代金券消费金额不为空，且没有单据ID（即还未被消费）则返回couponConsumeRecordDTO，和consumingRecordId（代金券消费记录id）
          if(null!=couponConsumeRecordDTO.getCoupon()&&null==orderId&&!OrderStatus.REPEAL.equals(couponConsumeRecordDTO.getOrderStatus())){
            repairOrderDTO.setCouponConsumeRecordDTO(couponConsumeRecordDTO);
            //给施工单添加代金券消费记录id
            repairOrderDTO.setConsumingRecordId(couponConsumeRecordDTO.getId());
            repairOrderDTO.setCouponAmount(couponConsumeRecordDTO.getCoupon());
          }
        } else {
          couponConsumeRecordDTO = new CouponConsumeRecordDTO();
          repairOrderDTO.setCouponConsumeRecordDTO(couponConsumeRecordDTO);
        }
        //准备查找用户和车辆信息
        if (!StringUtils.isBlank(appUserNo)) {
          //通过appUserNo获取AppUserCustomerDTO列表
          List<AppUserCustomerDTO> appUserCustomerDTOs = appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId);
          //通过AppUserCustomerDTO列表查找对应的customer和vehicle
          //先找到customerId的集合
          if(appUserCustomerDTOs != null &&appUserCustomerDTOs.size()>0){
            Set<Long> customerIdSet=new HashSet<Long>();
            for(AppUserCustomerDTO dto:appUserCustomerDTOs){
              Long customerId=dto.getCustomerId();
              if(customerId!=null){
                customerIdSet.add(customerId);
              }
            }
            //再通过customerId找到CustomerVehicleDTO
            if(customerIdSet.size()>0){
              List<CustomerVehicleDTO> customerVehicleDTOs=userService.getCustomerVehicleDTOByCustomerId(customerIdSet);
              //之后分别获取Customer和Vehicle，都有效的则保留
              if(customerVehicleDTOs!=null&&customerVehicleDTOs.size()>0){
                for(CustomerVehicleDTO dto:customerVehicleDTOs){
                  if(dto.getVehicleId()!=null&&dto.getCustomerId()!=null&&!VehicleStatus.DISABLED.equals(dto.getStatus())){
                    customerDTO=userService.getCustomerById(dto.getCustomerId());
                    vehicleDTO=userService.getVehicleById(dto.getVehicleId());
                    if(!CustomerStatus.DISABLED.equals(customerDTO.getStatus())&&!VehicleStatus.DISABLED.equals(vehicleDTO.getStatus())){
                      break;
                    }
                  }
                }
              }
            }
          }
        }
        //存放客户信息
        if (customerDTO != null){
          //
          repairOrderDTO.setCustomerId(customerDTO.getId());
          CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId, customerDTO.getId());
          if (null != customerRecordDTO) {
            repairOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
            model.addAttribute(TOTAL_CONSUME, NumberUtil.numberValue(customerRecordDTO.getTotalAmount(), 0D));
          }
          repairOrderDTO.setCustomerDTO(customerDTO);
          //customerDTO不为空，则通过CustomerVehicleDTO获取vehicleDTO

        }
        //存放车辆信息
        if (vehicleDTO !=null){
          repairOrderDTO.setVehicleDTO(vehicleDTO);
          repairOrderDTO.setLicenceNo(vehicleDTO.getLicenceNo());
          CustomerVehicleDTO customerVehicleDTO = userService.getCustomerVehicleDTOByVehicleIdAndCustomerId(vehicleDTO.getId(), repairOrderDTO.getCustomerId());
          Calendar calendar = new GregorianCalendar();
          calendar.setTime(new Date());
          calendar.add(calendar.DATE, -1);
          Date nowTime = calendar.getTime();

          Long  maintainTime = null;
          Long  insureTimeStr = null;
          Long  examineTime = null;
          Long  maintainMileage = null;
          if(null != customerVehicleDTO)
          {
            maintainTime =customerVehicleDTO.getMaintainTime();
            insureTimeStr =customerVehicleDTO.getInsureTime();
            examineTime =customerVehicleDTO.getExamineTime();
            maintainMileage =customerVehicleDTO.getMaintainMileage();
          }
          if (null != maintainTime && nowTime.getTime() < maintainTime)
            repairOrderDTO.setMaintainTimeStr(customerVehicleDTO.getMaintainTimeStr());
          if (null != insureTimeStr && nowTime.getTime() < insureTimeStr)
            repairOrderDTO.setInsureTimeStr(customerVehicleDTO.getInsureTimeStr());
          if (null != examineTime && nowTime.getTime() < examineTime)
            repairOrderDTO.setExamineTimeStr(customerVehicleDTO.getExamineTimeStr());

          repairOrderDTO.setMaintainMileage(maintainMileage);
          List<AppointServiceDTO> appointServiceDTOs=userService.getAppointServiceByCustomerVehicle(shopId,vehicleDTO.getId(),repairOrderDTO.getCustomerId());
          if(CollectionUtils.isNotEmpty(appointServiceDTOs)){
            repairOrderDTO.setAppointServiceDTOs(appointServiceDTOs.toArray(new AppointServiceDTO[appointServiceDTOs.size()]));
          }
        }
      }

      //根据customerId获取会员信息
      if (repairOrderDTO.getCustomerId() != null && StringUtil.isEmpty(repairOrderDTO.getMemberNo())) {
        try {
          MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, repairOrderDTO.getCustomerId());
          if (memberDTO != null) {
            if (memberDTO.getMemberServiceDTOs() != null) {
              for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
                Service service = rfiTxnService.getServiceById(memberServiceDTO.getServiceId());
                if (service != null) {
                  memberServiceDTO.setServiceName(service.getName());
                }
              }
            }
            repairOrderDTO.setMemberDTO(memberDTO);
            repairOrderDTO.setMemberStatus(membersService.getMemberStatusByMemberDTO(memberDTO).getStatus());
          }
        } catch (Exception e) {
          LOG.error("/txn.do");
          LOG.error("method=getRepairOrderByVehicleNumber");
          LOG.error("查询会员出现异常");
          LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
          LOG.error("customerId:" + repairOrderDTO.getCustomerId());
          LOG.error(repairOrderDTO.toString());
          LOG.error(e.getMessage(), e);
        }
      }
    }
    return invoicing(request,model,repairOrderDTO);
  }
}