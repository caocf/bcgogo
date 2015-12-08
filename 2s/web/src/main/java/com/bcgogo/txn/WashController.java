package com.bcgogo.txn;

import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.ServiceVehicleCountDTO;
import com.bcgogo.stat.model.ServiceVehicleCount;
import com.bcgogo.stat.service.IServiceVehicleCountService;
import com.bcgogo.txn.bcgogoListener.orderEvent.WashOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.WashOrderDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerRecord;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/wash.do")
public class WashController {

  private static final Logger LOG = LoggerFactory.getLogger(WashController.class);

  /*
  创建洗车卡或者洗车卡充值
   */
  @RequestMapping(params = "method=saveOrUpdateWashCard")
  public String saveOrUpdateWashCard(ModelMap model, RepairOrderDTO repairOrderDTO, HttpServletRequest request,
                                     @RequestParam("chargeCash") Double cashNum, @RequestParam("chargeTimes") long washTimes) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    cashNum = cashNum == null ? 0 : cashNum;
    CustomerRecordDTO customerRecordDTO = null;
    try {
      //维修单 所填车辆若为新车型，则新增，并将ID保存到此维修单
      List<VehicleDTO> vehicleDTOsForSolr = rfiTxnService.populateRepairOrderDTO(repairOrderDTO);
      //添加solr 后续可以考虑做线程
      if (repairOrderDTO.isAddVehicleInfoToSolr()) {
        ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOsForSolr);
      }

      Long shopId = (Long) request.getSession().getAttribute("shopId");
      long customerId = 0;
      if (repairOrderDTO.getCustomerId() != null) customerId = repairOrderDTO.getCustomerId().longValue();
      List<CustomerDTO> customerDTOs = userService.getShopCustomerById(shopId, customerId);
      CustomerDTO customerDTO = (customerDTOs != null && customerDTOs.size() > 0 ? customerDTOs.get(0) : null);
      CustomerRecord customerRecord = userService.getShopCustomerRecordByCustomerId(shopId, customerId);
      String washMsg = null;
      //判断该车牌号是否是客户，如果不是新增客户
      if (null != customerDTOs && customerDTOs.size() > 0 && null != customerRecord) {  //老客户
        CustomerRecordDTO customerRecordDTO1 = customerRecord.toDTO();
        customerDTO = customerDTOs.get(0);
        if (null != repairOrderDTO.getCustomerName()) {
          customerDTO.setName(repairOrderDTO.getCustomerName());
          customerRecordDTO1.setName(repairOrderDTO.getCustomerName());
        }
        if (null != repairOrderDTO.getMobile()) {
          customerDTO.setMobile(repairOrderDTO.getMobile());
          customerRecordDTO1.setMobile(repairOrderDTO.getMobile());
        }
        if (null != repairOrderDTO.getContact()) {
          customerDTO.setContact(repairOrderDTO.getContact());
          customerRecordDTO1.setContact(repairOrderDTO.getContact());
        }
        if (null != repairOrderDTO.getLandLine()) {
          customerDTO.setLandLine(repairOrderDTO.getLandLine());

        }
        if (null != repairOrderDTO.getLicenceNo()) {
          customerRecordDTO1.setLicenceNo(repairOrderDTO.getLicenceNo());
        }
        customerRecordDTO1.setLastBill("洗车");
        customerRecordDTO1.setLastBillShort("洗车");
        userService.updateCustomer(customerDTO);
        userService.updateCustomerRecord(customerRecordDTO1);
      } else {     //新客户
        customerDTO = new CustomerDTO();
        customerDTO.setMobile(repairOrderDTO.getMobile());
        customerDTO.setLandLine(repairOrderDTO.getLandLine());
        customerDTO.setContact(repairOrderDTO.getContact());
        customerDTO.setName(repairOrderDTO.getCustomerName() == null ? repairOrderDTO.getLicenceNo() : repairOrderDTO.getCustomerName());
        customerDTO.setShopId(shopId);
        userService.createCustomer(customerDTO);
        customerRecordDTO = new CustomerRecordDTO();
        customerRecordDTO.setShopId(shopId);
        customerRecordDTO.setCustomerId(customerDTO.getId());
        customerRecordDTO.setName(repairOrderDTO.getCustomerName());
        customerRecordDTO.setMobile(repairOrderDTO.getMobile());
        customerRecordDTO.setLicenceNo(repairOrderDTO.getLicenceNo());
        customerRecordDTO.setBrand(repairOrderDTO.getBrand());
        customerRecordDTO.setModel(repairOrderDTO.getModel());
        customerRecordDTO.setContact(repairOrderDTO.getContact());
        customerRecordDTO.setYear(repairOrderDTO.getYear());
        customerRecordDTO.setEngine(repairOrderDTO.getEngine());
        customerRecordDTO.setLastBill("洗车");
        customerRecordDTO.setLastBillShort("洗车");
        customerRecordDTO.setTotalAmount(0); //repairOrderDTO.getTotal()
        userService.createCustomerRecord(customerRecordDTO);
      }
      List<VehicleDTO> vehicles = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
      Long vehicleId=null;
      if (vehicles == null || vehicles.size() <= 0) {  //新增车辆
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setShopId(shopId);
        vehicle.setLicenceNo(repairOrderDTO.getLicenceNo());
        vehicle.setBrandId(repairOrderDTO.getBrandId());
        vehicle.setModelId(repairOrderDTO.getModelId());
        vehicle.setYearId(repairOrderDTO.getYearId());
        vehicle.setEngineId(repairOrderDTO.getEngineId());
        vehicle.setBrand(repairOrderDTO.getBrand());
        vehicle.setModel(repairOrderDTO.getModel());
        vehicle.setYear(repairOrderDTO.getYear());
        vehicle.setEngine(repairOrderDTO.getEngine());
        userService.createVehicle(vehicle);
        userService.addVehicleToCustomer(vehicle.getId(), customerDTO.getId());

        vehicleId=vehicle.getId();
        //创建客户消费记录表
        customerRecordDTO = new CustomerRecordDTO();

        customerRecordDTO.setShopId(shopId);
        customerRecordDTO.setCustomerId(customerDTO.getId());
        customerRecordDTO.setName(repairOrderDTO.getCustomerName());
        customerRecordDTO.setMobile(repairOrderDTO.getMobile());
        customerRecordDTO.setLicenceNo(repairOrderDTO.getLicenceNo());
        customerRecordDTO.setBrand(repairOrderDTO.getBrand());
        customerRecordDTO.setModel(repairOrderDTO.getModel());
        customerRecordDTO.setYear(repairOrderDTO.getYear());
        customerRecordDTO.setEngine(repairOrderDTO.getEngine());
//        userService.createCustomerRecord(customerRecordDTO);
      } else {
        // 如果车子一开始是洗车的可能没有车辆信息，这儿也要添加后更新车辆信息
        VehicleDTO vehicleDTO = vehicles.get(0);
        vehicleDTO.setLicenceNoRevert(new StringBuffer(repairOrderDTO.getLicenceNo()).reverse().toString().toUpperCase());
        if (!StringUtil.isEmpty(repairOrderDTO.getBrand())) {
          vehicleDTO.setBrand(repairOrderDTO.getBrand());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getModel())) {
          vehicleDTO.setModel(repairOrderDTO.getModel());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getYear())) {
          vehicleDTO.setYear(repairOrderDTO.getYear());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getMemo())) {
          vehicleDTO.setMemo(repairOrderDTO.getMemo());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getEngine())) {
          vehicleDTO.setEngine(repairOrderDTO.getEngine());
        }
        if (repairOrderDTO.getBrandId() != null) {
          vehicleDTO.setBrandId(repairOrderDTO.getBrandId());
        }
        if (repairOrderDTO.getModelId() != null) {
          vehicleDTO.setBrandId(repairOrderDTO.getModelId());
        }
        if (repairOrderDTO.getYearId() != null) {
          vehicleDTO.setYearId(repairOrderDTO.getYearId());
        }
        if (repairOrderDTO.getEngineId() != null) {
          vehicleDTO.setEngineId(repairOrderDTO.getEngineId());
        }
        userService.updateVehicle(vehicleDTO);
        vehicleId=vehicleDTO.getId();
      }


//        //创建关联表
//        userService.addVehicleToCustomer(vehicle.getId(), customerDTO.getId());
//        //创建客户消费记录表
//        CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
//        customerRecordDTO.setShopId(shopId);
//        customerRecordDTO.setCustomerId(customerDTO.getId());
//        customerRecordDTO.setName(repairOrderDTO.getCustomer());
//        customerRecordDTO.setMobile(repairOrderDTO.getMobile());
//        customerRecordDTO.setLicenceNo(repairOrderDTO.getLicenceNo());
//        customerRecordDTO.setBrand(repairOrderDTO.getBrand());
//        customerRecordDTO.setModel(repairOrderDTO.getModel());
//        customerRecordDTO.setYear(repairOrderDTO.getYear());
//        customerRecordDTO.setEngine(repairOrderDTO.getEngine());
//        userService.createCustomerRecord(customerRecordDTO);
//      } else {
//        // 如果车子一开始是洗车的可能没有车辆信息，这儿也要添加后更新车辆信息
//        VehicleDTO vehicleDTO = vehicles.get(0);
//        vehicleDTO.setLicenceNoRevert(new StringBuffer(repairOrderDTO.getLicenceNo()).reverse().toString().toUpperCase());
//        if (!StringUtil.isEmpty(repairOrderDTO.getBrand())) {
//          vehicleDTO.setBrand(repairOrderDTO.getBrand());
//        }
//        if (!StringUtil.isEmpty(repairOrderDTO.getModel())) {
//          vehicleDTO.setModel(repairOrderDTO.getModel());
//        }
//        if (!StringUtil.isEmpty(repairOrderDTO.getYear())) {
//          vehicleDTO.setYear(repairOrderDTO.getYear());
//        }
//        if (!StringUtil.isEmpty(repairOrderDTO.getMemo())) {
//          vehicleDTO.setMemo(repairOrderDTO.getMemo());
//        }
//        if (!StringUtil.isEmpty(repairOrderDTO.getEngine())) {
//          vehicleDTO.setEngine(repairOrderDTO.getEngine());
//        }
//        if (repairOrderDTO.getBrandId() != null) {
//          vehicleDTO.setBrandId(repairOrderDTO.getBrandId());
//        }
//        if (repairOrderDTO.getModelId() != null) {
//          vehicleDTO.setBrandId(repairOrderDTO.getModelId());
//        }
//        if (repairOrderDTO.getYearId() != null) {
//          vehicleDTO.setYearId(repairOrderDTO.getYearId());
//        }
//        if (repairOrderDTO.getEngineId() != null) {
//          vehicleDTO.setEngineId(repairOrderDTO.getEngineId());
//        }
//        userService.updateVehicle(vehicleDTO);
//      }

      //判断该客户是否有洗车卡
      List<CustomerCardDTO> customerCardDTOs = null;
      if (null != customerDTO) {
        customerCardDTOs = userService.getCustomerCardByCustomerIdAndCardType(shopId, customerDTO.getId(), 0);
      }
      CustomerCardDTO customerCardDTO = null;
      if (null != customerCardDTOs && customerCardDTOs.size() > 0) {
        customerCardDTO = customerCardDTOs.get(0);
        customerCardDTO.setWashRemain(customerCardDTO.getWashRemain() + washTimes);
        userService.updateCustomerCard(customerCardDTO);
        washMsg = "洗车卡充值";
      } else {
        customerCardDTO = new CustomerCardDTO();
        customerCardDTO.setShopId(shopId);
        customerCardDTO.setCustomerId(customerDTO.getId());
        customerCardDTO.setCardType(0l);
        customerCardDTO.setWashRemain(washTimes);
        userService.createCustomerCard(customerCardDTO);
        washMsg = "办理洗车卡";
      }
      //更新客户消费记录表 累计欠款
      updateCustomerRecord(shopId, customerDTO.getId(), cashNum, customerRecordDTO);

      //创建充值单（洗车单）
      WashOrderDTO washOrderDTO = new WashOrderDTO();
      washOrderDTO.setOrderType(OrderTypes.RECHARGE);//0表示办卡活冲卡
      washOrderDTO.setShopId(shopId);
      washOrderDTO.setCardId(customerCardDTO.getId());
      washOrderDTO.setCustomerId(customerCardDTO.getCustomerId());
      washOrderDTO.setCashNum(cashNum);
      washOrderDTO.setWashTimes(washTimes);
      //ToDO：这里先以点按钮的时间为准，以后可能在页面让客户选择时间传过来（Long精确到秒，Str精确到天）
      Long vestDate = System.currentTimeMillis();
      String vestDateStr = DateUtil.dateLongToStr(vestDate, DateUtil.YEAR_MONTH_DATE);
      washOrderDTO.setVestDate(vestDate);
      washOrderDTO.setVestDateStr(vestDateStr);

      if (!StringUtil.isEmpty(request.getParameter("customerStr"))) {
        washOrderDTO.setCustomer(request.getParameter("customerStr"));
      } else {
        washOrderDTO.setCustomer(repairOrderDTO.getCustomerName() == null ? repairOrderDTO.getLicenceNo() : repairOrderDTO.getCustomerName());
      }
      if (!StringUtil.isEmpty(request.getParameter("washWorkerStr"))) {
        washOrderDTO.setWashWorker(request.getParameter("washWorkerStr"));
      }
      if (!StringUtil.isEmpty(request.getParameter("mobileStr"))) {
        washOrderDTO.setContactNum(request.getParameter("mobileStr"));
      }

      washOrderDTO = txnService.createWashOrder(washOrderDTO);


      washOrderDTO.setVehicle(repairOrderDTO.getVechicle() == null ? repairOrderDTO.getLicenceNo() : repairOrderDTO.getVechicle());

      //washOrderDTO.setContactNum(repairOrderDTO.getMobile());
      if (!StringUtil.isEmpty(request.getParameter("mobileStr"))) {
        washOrderDTO.setContactNum(request.getParameter("mobileStr"));
      }
      washOrderDTO.setCreationDate(repairOrderDTO.getStartDateStr());
      WashOrderSavedEvent washOrderSavedEvent = new WashOrderSavedEvent(washOrderDTO);
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      bcgogoEventPublisher.publisherWashOrderSaved(washOrderSavedEvent);

      ItemIndex itemIndex = new ItemIndex();
      itemIndex.setCustomerId(washOrderDTO.getCustomerId());
      itemIndex.setShopId(washOrderDTO.getShopId());
      itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
      itemIndex.setOrderId(washOrderDTO.getId());
      Long timeLong = new Date().getTime();
      itemIndex.setOrderTimeCreated(timeLong);
      itemIndex.setPaymentTime(timeLong);
      itemIndex.setOrderTypeEnum(OrderTypes.RECHARGE);
      itemIndex.setItemTypeEnum(ItemTypes.RECHARGE);
      itemIndex.setOrderStatusEnum(OrderStatus.WASH_SETTLED);
      itemIndex.setItemName("购卡与续卡充值");
      itemIndex.setCustomerOrSupplierName(washOrderDTO.getCustomer());
      itemIndex.setItemPrice(washOrderDTO.getCashNum());
      itemIndex.setOrderTotalAmount(washOrderDTO.getCashNum());
      //itemIndex itemCout改为double类型
      itemIndex.setItemCount(Double.valueOf(String.valueOf(washTimes)));
      searchService.addItemIndex(itemIndex);

      model.addAttribute("customerCardDTO", customerCardDTO);
      model.addAttribute("washMsg", washMsg + "成功！");
      model.addAttribute("serviceType", 3);
      getWashHistory(txnService, model, customerDTO.getId());

      //剩余油量
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      model.addAttribute("fuelNumberList", fuelNumberList);

      AppointServiceDTO appointServiceDTO=new AppointServiceDTO();
      appointServiceDTO.setShopId(shopId);
      appointServiceDTO.setCustomerId(String.valueOf(customerDTO.getId()));
      appointServiceDTO.setVehicleId(String.valueOf(customerDTO.getId()));
      appointServiceDTO.setMaintainTimeStr(com.bcgogo.utils.StringUtil.replaceBlankStr(repairOrderDTO.getMaintainTimeStr()));
      appointServiceDTO.setInsureTimeStr(com.bcgogo.utils.StringUtil.replaceBlankStr(repairOrderDTO.getInsureTimeStr()));
      appointServiceDTO.setExamineTimeStr(com.bcgogo.utils.StringUtil.replaceBlankStr(repairOrderDTO.getExamineTimeStr()));
      ServiceManager.getService(IUserService.class).addYuyueToCustomerVehicle(appointServiceDTO);

    } catch (BcgogoException e) {
      LOG.debug("/wash.do");
      LOG.debug("method=saveOrUpdateWashCard");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("cashNum:" + cashNum + ",washTimes:" + washTimes);
      LOG.debug(repairOrderDTO.toString());
      LOG.error(e.getMessage(), e);
    } catch (Exception e) {
      LOG.debug("/wash.do");
      LOG.debug("method=saveOrUpdateWashCard");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("cashNum:" + cashNum + ",washTimes:" + washTimes);
      LOG.debug(repairOrderDTO.toString());
      LOG.error(e.getMessage(), e);
    }

    return "/txn/invoicing";
  }

  /*
  洗车
   */
  @RequestMapping(params = "method=washCar")
  public String saveOrUpdateWashCard(ModelMap model, HttpServletRequest request,
                                     @RequestParam("washType") String washType,
                                     RepairOrderDTO repairOrderDTO) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IServiceVehicleCountService iscService = ServiceManager.getService(IServiceVehicleCountService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);

    if (!StringUtil.isEmpty(request.getParameter("normalCash"))) {
      repairOrderDTO.setTotal(NumberUtil.doubleValue(request.getParameter("normalCash"), 0));
    }
    try {
      //清空 repairOrderDTO 数组中的空数据
      repairOrderDTO=txnService.removeBlankArrayOfRepairOrder(repairOrderDTO);
      //ToDO：这里先以点按钮的时间为准，以后可能在页面让客户选择时间传过来（Long精确到秒，Str精确到天）
      Long vestDate = System.currentTimeMillis();
      String vestDateStr = DateUtil.dateLongToStr(vestDate, DateUtil.YEAR_MONTH_DATE);
      List<VehicleDTO> vehicleDTOsForSolr =  rfiTxnService.populateRepairOrderDTO(repairOrderDTO);//维修单 所填车辆若为新车型，则新增，并将ID保存到此维修单
      if(repairOrderDTO.isAddVehicleInfoToSolr()) {
        ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOsForSolr); //添加solr 后续可以考虑做线程
      }

      Long shopId = (Long) request.getSession().getAttribute("shopId");
      long customerId = 0;
      if (repairOrderDTO.getCustomerId() != null) customerId = repairOrderDTO.getCustomerId().longValue();
      List<CustomerDTO> customerDTOs = userService.getShopCustomerById(shopId, customerId);
      CustomerRecord customerRecord = userService.getShopCustomerRecordByCustomerId(shopId, customerId);
      CustomerDTO customerDTO = null;
      String washMsg = null;
      //判断该车牌号是否是客户，如果不是新增客户
      if (null != customerDTOs && customerDTOs.size() > 0 && null != customerRecord) {  //老客户
        CustomerRecordDTO customerRecordDTO1 = customerRecord.toDTO();
        customerDTO = customerDTOs.get(0);
        if (null != repairOrderDTO.getCustomerName()) {
          customerDTO.setName(repairOrderDTO.getCustomerName());
          customerRecordDTO1.setName(repairOrderDTO.getCustomerName());
        }
        if (null != repairOrderDTO.getMobile()) {
          customerDTO.setMobile(repairOrderDTO.getMobile());
          customerRecordDTO1.setMobile(repairOrderDTO.getMobile());
        }
        if (null != repairOrderDTO.getContact()) {
          customerDTO.setContact(repairOrderDTO.getContact());
          customerRecordDTO1.setContact(repairOrderDTO.getContact());
        }
        if (null != repairOrderDTO.getLandLine()) {
          customerDTO.setLandLine(repairOrderDTO.getLandLine());

        }
        if (null != repairOrderDTO.getLicenceNo()) {
          customerRecordDTO1.setLicenceNo(repairOrderDTO.getLicenceNo());
        }
        customerRecordDTO1.setLastBill("洗车");
        customerRecordDTO1.setLastBillShort("洗车");
        userService.updateCustomer(customerDTO);
        userService.updateCustomerRecord(customerRecordDTO1);
      } else {     //新客户
        customerDTO = new CustomerDTO();
        customerDTO.setMobile(repairOrderDTO.getMobile());
        customerDTO.setLandLine(repairOrderDTO.getLandLine());
        customerDTO.setContact(repairOrderDTO.getContact());
        customerDTO.setName(repairOrderDTO.getCustomerName() == null ? repairOrderDTO.getLicenceNo() : repairOrderDTO.getCustomerName());
        customerDTO.setShopId(shopId);
        userService.createCustomer(customerDTO);
        CustomerRecordDTO customerRecordDTO = new CustomerRecordDTO();
        customerRecordDTO.setShopId(shopId);
        customerRecordDTO.setCustomerId(customerDTO.getId());
        customerRecordDTO.setName(repairOrderDTO.getCustomerName());
        customerRecordDTO.setMobile(repairOrderDTO.getMobile());
        customerRecordDTO.setLicenceNo(repairOrderDTO.getLicenceNo());
        customerRecordDTO.setBrand(repairOrderDTO.getBrand());
        customerRecordDTO.setModel(repairOrderDTO.getModel());
        customerRecordDTO.setContact(repairOrderDTO.getContact());
        customerRecordDTO.setYear(repairOrderDTO.getYear());
        customerRecordDTO.setEngine(repairOrderDTO.getEngine());
        customerRecordDTO.setLastBill("洗车");
        customerRecordDTO.setLastBillShort("洗车");
        customerRecordDTO.setTotalAmount(0); //repairOrderDTO.getTotal()
        userService.createCustomerRecord(customerRecordDTO);
      }
      List<VehicleDTO> vehicles = userService.getVehicleByLicenceNo(shopId, repairOrderDTO.getLicenceNo());
      Long vehicleId=null;
      if (vehicles == null || vehicles.size() <= 0) {  //新增车辆
        VehicleDTO vehicle = new VehicleDTO();
        vehicle.setShopId(shopId);
        vehicle.setLicenceNo(repairOrderDTO.getLicenceNo());
        vehicle.setBrandId(repairOrderDTO.getBrandId());
        vehicle.setModelId(repairOrderDTO.getModelId());
        vehicle.setYearId(repairOrderDTO.getYearId());
        vehicle.setEngineId(repairOrderDTO.getEngineId());
        vehicle.setBrand(repairOrderDTO.getBrand());
        vehicle.setModel(repairOrderDTO.getModel());
        vehicle.setYear(repairOrderDTO.getYear());
        vehicle.setEngine(repairOrderDTO.getEngine());
        userService.createVehicle(vehicle);
        userService.addVehicleToCustomer(vehicle.getId(), customerDTO.getId());
        vehicleId=vehicle.getId();
      } else {
        // 如果车子一开始是洗车的可能没有车辆信息，这儿也要添加后更新车辆信息 BCSHOP-2024
        VehicleDTO vehicleDTO = vehicles.get(0);
        vehicleDTO.setLicenceNoRevert(new StringBuffer(repairOrderDTO.getLicenceNo()).reverse().toString().toUpperCase());
        if (!StringUtil.isEmpty(repairOrderDTO.getBrand())) {
          vehicleDTO.setBrand(repairOrderDTO.getBrand());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getModel())) {
          vehicleDTO.setModel(repairOrderDTO.getModel());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getYear())) {
          vehicleDTO.setYear(repairOrderDTO.getYear());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getMemo())) {
          vehicleDTO.setMemo(repairOrderDTO.getMemo());
        }
        if (!StringUtil.isEmpty(repairOrderDTO.getEngine())) {
          vehicleDTO.setEngine(repairOrderDTO.getEngine());
        }
        if (repairOrderDTO.getBrandId() != null) {
          vehicleDTO.setBrandId(repairOrderDTO.getBrandId());
        }
        if (repairOrderDTO.getModelId() != null) {
          vehicleDTO.setBrandId(repairOrderDTO.getModelId());
        }
        if (repairOrderDTO.getYearId() != null) {
          vehicleDTO.setYearId(repairOrderDTO.getYearId());
        }
        if (repairOrderDTO.getEngineId() != null) {
          vehicleDTO.setEngineId(repairOrderDTO.getEngineId());
        }
        userService.updateVehicle(vehicleDTO);
        vehicleId=vehicleDTO.getId();
      }

      //如果是会员洗车
      WashOrderDTO washOrderDTO = null;
      if (washType != null && washType.trim().equals("member")) {
        //判断该客户是否有洗车卡
        List<CustomerCardDTO> customerCardDTOs = userService.getCustomerCardByCustomerIdAndCardType(shopId, customerDTO.getId(), 0);
        CustomerCardDTO customerCardDTO = null;
        if (null != customerCardDTOs && customerCardDTOs.size() > 0) {
          washOrderDTO = new WashOrderDTO();
          customerCardDTO = customerCardDTOs.get(0);
          customerCardDTO.setWashRemain(customerCardDTO.getWashRemain() - 1);
          userService.updateCustomerCard(customerCardDTO);

          //判断洗车次数是否还剩2次，如果是发送短信提醒
          if (customerCardDTO.getWashRemain() == 2 && repairOrderDTO.getMobile() != null && !repairOrderDTO.getMobile().trim().equals("")) {
            ISmsService smsService = ServiceManager.getService(ISmsService.class);
            IConfigService configService = ServiceManager.getService(IConfigService.class);
            ShopDTO shopDTO = configService.getShopById(new Long(shopId));

            StringBuffer sendStr = new StringBuffer();
            sendStr.append("尊敬的").append(repairOrderDTO.getLicenceNo()).append("车主");
            if (repairOrderDTO.getCustomerName() != null && !repairOrderDTO.getCustomerName().trim().equals(""))
              sendStr.append(repairOrderDTO.getCustomerName());
            sendStr.append("您好！感谢您对本店的一贯照顾，您的洗车卡消费还剩余2次，为了不影响您的后续消费，麻烦方便的时候来本店充值，详请咨询0512-66778899,")
                .append(shopDTO.getName()).append("店敬启。");
            INotificationService notificationService = ServiceManager.getService(INotificationService.class);
            SmsJobDTO smsJobDTO = new SmsJobDTO();
            smsJobDTO.setSmsChannel(SmsChannel.INDUSTRY);
            smsJobDTO.setShopId(shopId);
            smsJobDTO.setName(repairOrderDTO.getCustomerName());
            smsJobDTO.setVehicleLicense(repairOrderDTO.getLicenceNo());
            smsJobDTO.setContent(sendStr.toString());
            smsJobDTO.setReceiveMobile(repairOrderDTO.getMobile());
            smsJobDTO.setType(SmsConstant.SMS_TYPE_WASH_CARD_ALERT);
            smsJobDTO.setStartTime(System.currentTimeMillis());
            smsJobDTO.setSender(SenderType.Shop);
            notificationService.sendSmsAsync(smsJobDTO);
          }
          washOrderDTO.setOrderType(OrderTypes.WASH_MEMBER);
          washOrderDTO.setShopId(shopId);
          washOrderDTO.setCardId(customerCardDTO.getId());
          washOrderDTO.setWashTimes(1l);
          washOrderDTO.setCustomerId(customerDTO.getId());

          model.addAttribute("customerCardDTO", customerCardDTO);
          washMsg = "会员卡洗车";

          washOrderDTO.setVehicle(repairOrderDTO.getLicenceNo());
          if (!StringUtil.isEmpty(request.getParameter("customerStr"))) {
            washOrderDTO.setCustomer(request.getParameter("customerStr"));
          } else {
            washOrderDTO.setCustomer(repairOrderDTO.getCustomerName() == null ? repairOrderDTO.getLicenceNo() : repairOrderDTO.getCustomerName());
          }
          if (!StringUtil.isEmpty(request.getParameter("washWorkerStr"))) {
            washOrderDTO.setWashWorker(request.getParameter("washWorkerStr"));
          }

          if (!StringUtil.isEmpty(request.getParameter("mobileStr"))) {
            washOrderDTO.setContactNum(request.getParameter("mobileStr"));
          }
          washOrderDTO.setCreationDate(repairOrderDTO.getStartDateStr());
        }
        if(washOrderDTO==null){
           return "/txn/invoicing";
        }
        washOrderDTO.setVestDate(vestDate);
        washOrderDTO.setVestDateStr(vestDateStr);

        if (null != washOrderDTO) {
          txnService.createWashOrder(washOrderDTO);
        }

        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setOrderId(washOrderDTO.getId());
        itemIndex.setCustomerId(washOrderDTO.getCustomerId());
        itemIndex.setShopId(washOrderDTO.getShopId());

        itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
        Long timeLong = new Date().getTime();
        itemIndex.setOrderTimeCreated(timeLong);
        itemIndex.setPaymentTime(timeLong);
        itemIndex.setOrderTypeEnum(OrderTypes.WASH_MEMBER);
        itemIndex.setItemTypeEnum(ItemTypes.WASH_MEMBER);
        itemIndex.setOrderStatusEnum(OrderStatus.WASH_SETTLED);
        itemIndex.setItemName("会员卡洗车");
        itemIndex.setCustomerCardId(washOrderDTO.getCardId());
        itemIndex.setItemPrice(washOrderDTO.getCashNum());
        itemIndex.setOrderTotalAmount(repairOrderDTO.getTotal());
        itemIndex.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
        itemIndex.setOrderTotalAmount(0.0);
        //itemIndex itemCout改为double类型
        itemIndex.setItemCount(1d);
        searchService.addItemIndex(itemIndex);

      } else if (washType != null && washType.trim().equals("normal")) {
        double cashNum = repairOrderDTO.getTotal();

        washOrderDTO = new WashOrderDTO();
        washOrderDTO.setOrderType(OrderTypes.WASH);
        washOrderDTO.setCashNum(cashNum);
        washOrderDTO.setShopId(shopId);
        washOrderDTO.setCustomerId(customerDTO.getId());

        washOrderDTO.setCreationDate(repairOrderDTO.getStartDateStr());
        washOrderDTO.setVehicle(repairOrderDTO.getLicenceNo());
        if (!StringUtil.isEmpty(request.getParameter("customerStr"))) {
          washOrderDTO.setCustomer(request.getParameter("customerStr"));
        } else {
          washOrderDTO.setCustomer(repairOrderDTO.getCustomerName() == null ? repairOrderDTO.getLicenceNo() : repairOrderDTO.getCustomerName());
        }
        if (!StringUtil.isEmpty(request.getParameter("washWorkerStr"))) {
          washOrderDTO.setWashWorker(request.getParameter("washWorkerStr"));
        }
        if (!StringUtil.isEmpty(request.getParameter("mobileStr"))) {
          washOrderDTO.setContactNum(request.getParameter("mobileStr"));
        }


        washMsg = "普通付款洗车";

        List<CustomerCardDTO> customerCardDTOs = userService.getCustomerCardByCustomerIdAndCardType(shopId, customerDTO.getId(), 0);
        CustomerCardDTO customerCardDTO = null;
        if (null != customerCardDTOs && customerCardDTOs.size() > 0) {
          model.addAttribute("customerCardDTO", customerCardDTOs.get(0));
        }
        //更新客户消费记录表 累计欠款
        updateCustomerRecord(shopId, customerDTO.getId(), cashNum, null);

        washOrderDTO.setVestDate(vestDate);
        washOrderDTO.setVestDateStr(vestDateStr);

        if (null != washOrderDTO) {
          washOrderDTO = txnService.createWashOrder(washOrderDTO);
        }


        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setOrderId(washOrderDTO.getId());
        itemIndex.setCustomerId(washOrderDTO.getCustomerId());
        itemIndex.setShopId(washOrderDTO.getShopId());
        itemIndex.setVehicle(repairOrderDTO.getLicenceNo());
        Long timeLong = new Date().getTime();
        itemIndex.setOrderTimeCreated(timeLong);
        itemIndex.setPaymentTime(timeLong);
        itemIndex.setOrderTypeEnum(OrderTypes.WASH);
        itemIndex.setItemTypeEnum(ItemTypes.WASH);
        itemIndex.setOrderStatus(3 + "");
        itemIndex.setOrderStatusEnum(OrderStatus.WASH_SETTLED);
        itemIndex.setItemName("非会员洗车");
        itemIndex.setItemPrice(washOrderDTO.getCashNum());
        itemIndex.setOrderTotalAmount(repairOrderDTO.getTotal());
        itemIndex.setItemCount(1d);
        searchService.addItemIndex(itemIndex);

      }

      //保存到order_index表
      WashOrderSavedEvent washOrderSavedEvent = new WashOrderSavedEvent(washOrderDTO);
      BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
      bcgogoEventPublisher.publisherWashOrderSaved(washOrderSavedEvent);

//      if (null != washOrderDTO)
//        txnService.createWashOrder(washOrderDTO);

      //增加服务次数
      //创建或更新当日服务次数
      //查询有没有服务记录
      //没有就创建
      //有就更新
      List<ServiceVehicleCount> serviceTimes = iscService.getServiceVehicleCountByTime(shopId, Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date())));//汽修单状态
      if (serviceTimes == null) {
        ServiceVehicleCountDTO svcDTO = new ServiceVehicleCountDTO(shopId, Long.parseLong(new SimpleDateFormat("yyyyMMdd").format(new Date())), 1l);
        iscService.saveServiceVehicleCount(svcDTO);
      } else {
        for (ServiceVehicleCount s : serviceTimes) {
          s.setCount(s.getCount() + 1);
          iscService.updateServiceVehicleCountByTime(s);
        }
      }

      model.addAttribute("washMsg", washMsg + "成功！");
      model.addAttribute("serviceType", 3);
      model.addAttribute("lastWashOrderId",washOrderDTO.getId());
      if (null != customerDTO) {
        getWashHistory(txnService, model, customerDTO.getId());
      }

      //剩余油量
      Map fuelNumberList = TxnConstant.getFuelNumberMap(request.getLocale());
      model.addAttribute("fuelNumberList", fuelNumberList);

      AppointServiceDTO appointServiceDTO=new AppointServiceDTO();
      appointServiceDTO.setShopId(shopId);
      appointServiceDTO.setCustomerId(String.valueOf(customerDTO.getId()));
      appointServiceDTO.setVehicleId(String.valueOf(customerDTO.getId()));
      appointServiceDTO.setMaintainTimeStr(com.bcgogo.utils.StringUtil.replaceBlankStr(repairOrderDTO.getMaintainTimeStr()));
      appointServiceDTO.setInsureTimeStr(com.bcgogo.utils.StringUtil.replaceBlankStr(repairOrderDTO.getInsureTimeStr()));
      appointServiceDTO.setExamineTimeStr(com.bcgogo.utils.StringUtil.replaceBlankStr(repairOrderDTO.getExamineTimeStr()));
      ServiceManager.getService(IUserService.class).addYuyueToCustomerVehicle(appointServiceDTO);

    } catch (Exception e) {
      LOG.debug("/wash.do");
      LOG.debug("method=washCar");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("washType:" + washType);
      LOG.debug(repairOrderDTO.toString());
      LOG.error(e.getMessage(), e);
    }

    return "/txn/invoicing";
  }

  /**
   * 老版本洗车打印小票 ，获取打印数据并且调用打印方法
   * @param modelMap
   * @param request
   * @param response
   * @param orderId
   */
  @RequestMapping(params = "method=printWashTicket")
  public void printWashTicket(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response,Long orderId)
  {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    try{
      ShopDTO shopDTO = configService.getShopById(shopId);
      if(null == orderId)
      {
        return;
      }

      List<ItemIndexDTO> itemIndexDTOs = searchService.getItemIndexDTOListByOrderId(shopId,orderId);
      ItemIndexDTO itemIndexDTO = CollectionUtils.isNotEmpty(itemIndexDTOs)?itemIndexDTOs.get(0):null;
      if(null != itemIndexDTO)
      {
        List<CustomerCardDTO> customerCardDTOs = userService.getCustomerCardByCustomerIdAndCardType(shopId,itemIndexDTO.getCustomerId(),0);
        CustomerCardDTO customerCardDTO = CollectionUtils.isNotEmpty(customerCardDTOs)?customerCardDTOs.get(0):null;
        CustomerDTO customerDTO = customerService.getCustomerById(itemIndexDTO.getCustomerId());
        itemIndexDTO.setOrderTimeCreatedStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,itemIndexDTO.getOrderTimeCreated()));
        String nowStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT_TO_AFTERNOON, System.currentTimeMillis());
        WashOrderDTO washOrderDTO = txnService.getLastWashOrderDTO(shopId,itemIndexDTO.getCustomerId());
        if(null != washOrderDTO)
        {
          washOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT_TO_AFTERNOON,washOrderDTO.getVestDate()));
        }
        if (customerDTO != null) {
          itemIndexDTO.setCustomerOrSupplierName(customerDTO.getName());
        }
        modelMap.addAttribute("shopName",shopDTO.getName());
        modelMap.addAttribute("executor",(String)request.getSession().getAttribute("userName"));
        modelMap.addAttribute("itemIndexDTO",itemIndexDTO);
        modelMap.addAttribute("nowStr",nowStr);
        modelMap.addAttribute("times",null != customerCardDTO?customerCardDTO.getWashRemain():0L);
        modelMap.addAttribute("washOrderDTO",washOrderDTO);

        toPrint(txnService,modelMap,request,response);

      }
    }catch (Exception e)
    {
      LOG.debug("method=printWashTicket");
      LOG.debug("orderId",orderId);
      LOG.debug(e.getMessage(),e);
    }
  }

  /**
   * 调用此方法来打印小票
   * @param txnService
   * @param model
   * @param request
   * @param response
   * @throws Exception
   */
  public void toPrint(ITxnService txnService,ModelMap model,HttpServletRequest request,HttpServletResponse response) throws Exception
  {

    IPrintService printService = ServiceManager.getService(IPrintService.class);
    PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(WebUtil.getShopId(request), OrderTypes.WASH_TICKET);

    PrintWriter out = response.getWriter();
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    try{
      if(null != printTemplateDTO)
      {
        byte bytes[]=printTemplateDTO.getTemplateHtml();
        String str = new String(bytes,"UTF-8");

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

        String myTemplateName = "washPrint"+String.valueOf(WebUtil.getShopId(request));

        String myTemplate =  str;

        //模板资源存放 资源库 中

        repo.putStringResource(myTemplateName, myTemplate);

        //从资源库中加载模板

        Template template = ve.getTemplate(myTemplateName);

        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();

        //把数据填入上下文
        String shopName = (String)model.get("shopName");
        ItemIndexDTO itemIndexDTO = (ItemIndexDTO)model.get("itemIndexDTO");
        String nowStr = (String)model.get("nowStr");
        CustomerCardDTO customerCardDTO= (CustomerCardDTO)model.get("customerCardDTO");
        WashOrderDTO washOrderDTO = (WashOrderDTO)model.get("washOrderDTO");
        Long times = (Long)model.get("times");
        context.put("shopName",shopName);
        context.put("itemIndexDTO",itemIndexDTO);
        context.put("nowStr",nowStr);
        context.put("times",times);
        context.put("customerCardDTO",customerCardDTO);
        context.put("washOrderDTO",washOrderDTO);
        context.put("executor",(String)model.get("executor"));
        //输出流
        StringWriter writer = new StringWriter();

        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }
      else
      {
        out.print(TxnConstant.NO_PRINT_TEMPLATE);
      }
    }
    catch(Exception e)
    {
      LOG.debug(e.getMessage(),e);
    }
    finally {
      out.close();
    }
  }
  //上次消费
  private void getWashHistory(ITxnService txnService, ModelMap model, long customerId) {
    try {
      List<WashOrderDTO> washOrderDTOs = txnService.getCustomerWashOrders(customerId);
      if (washOrderDTOs != null && washOrderDTOs.size() > 0)
        model.addAttribute("washOrderDTOs", washOrderDTOs.toArray(new WashOrderDTO[washOrderDTOs.size()]));

      //获取当天洗车次数
      model.addAttribute("todayWashTimes", txnService.getTodayWashTimes(customerId));
    } catch (Exception e) {
      LOG.debug("/wash.do");
      LOG.debug("customerId:" + customerId);
      LOG.error(e.getMessage(), e);
    }

  }

  //更新客户消费记录表 累计欠款
  private void updateCustomerRecord(Long shopId, Long customerId, double total, CustomerRecordDTO customerRecordDTO) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    //  CustomerDTO customerDTO = userService.getCustomerById(customerId);
    //  List<CustomerVehicleDTO> customerVehicleDTOs = userService.getVehicleByCustomerId(customerId);
    //  VehicleDTO vehicleDTO = userService.getVehicleById(customerVehicleDTOs.get(0).getVehicleId());
    // List<CustomerRecordDTO> customerRecordDTOs = userService.getShopCustomerRecordByLicenceNo(shopId,vehicleDTO.getLicenceNo());
    CustomerRecordDTO crDTO = null;
    List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(customerId);
    if (null != customerRecordDTOs && customerRecordDTOs.size() > 0) {
      crDTO = customerRecordDTOs.get(0);
    } else {
      crDTO = new CustomerRecordDTO();
    }
    saveCustomerRecordDTO(crDTO, total, customerRecordDTO, shopId);
  }

  private void saveCustomerRecordDTO(CustomerRecordDTO crDTO, double total, CustomerRecordDTO customerRecordDTO, Long shopId) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    crDTO.setLastDate(System.currentTimeMillis());
    crDTO.setLastAmount(total);
    crDTO.setTotalAmount(crDTO.getTotalAmount() + total);

    if (customerRecordDTO != null) {
      crDTO.setShopId(shopId);
      crDTO.setCustomerId(customerRecordDTO.getCustomerId());
      crDTO.setName(customerRecordDTO.getName());
      crDTO.setMobile(customerRecordDTO.getMobile());
      crDTO.setLicenceNo(customerRecordDTO.getLicenceNo());
      crDTO.setBrand(customerRecordDTO.getBrand());
      crDTO.setModel(customerRecordDTO.getModel());
      crDTO.setYear(customerRecordDTO.getYear());
      crDTO.setEngine(customerRecordDTO.getEngine());
    }
    userService.updateCustomerRecord(crDTO);
  }
}

