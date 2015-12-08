package com.bcgogo.txn;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.search.service.suggestion.ISearchSuggestionService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.WashOrderSavedEvent;
import com.bcgogo.txn.bcgogoListener.publisher.BcgogoEventPublisher;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.Customer;
import com.bcgogo.user.model.Member;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.AppUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-17
 * Time: 上午9:15
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/washBeauty.do")
public class WashBeautyController {

  private static final Logger LOG = LoggerFactory.getLogger(WashBeautyController.class);
  private static final int DEFAULT_WASH_BEAUTY_ITEM_SIZE = 2;//洗车美容单默认显示服务的数量
  @Autowired
  private RFITxnService rfiTxnService ;
  @Autowired
  private ICustomerService customerService;
  @Autowired
  private ITxnService txnService;
  @Autowired
  private ISearchService searchService;
  @Autowired
  private IOrderSolrWriterService orderSolrWriterService;
  @Autowired
  private IUserService userService;
  @Autowired
  private IVehicleService vehicleService;

  public RFITxnService getRfiTxnService() {
    if(rfiTxnService == null){
      return ServiceManager.getService(RFITxnService.class);
    }
    return rfiTxnService;
  }

  public void setRfiTxnService(RFITxnService rfiTxnService) {
    this.rfiTxnService = rfiTxnService;
  }

  public ICustomerService getCustomerService() {
    if(customerService == null){
      customerService = ServiceManager.getService(ICustomerService.class);
    }
    return customerService;
  }

  public void setCustomerService(ICustomerService customerService) {
    this.customerService = customerService;
  }

  public ITxnService getTxnService() {
    if(txnService == null){
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public void setTxnService(ITxnService txnService) {
    this.txnService = txnService;
  }

  public ISearchService getSearchService() {
    if(searchService == null){
      searchService = ServiceManager.getService(ISearchService.class);
    }
    return searchService;
  }

  public void setSearchService(ISearchService searchService) {
    this.searchService = searchService;
  }

  public IOrderSolrWriterService getOrderSolrWriterService() {
    if(orderSolrWriterService == null){
      orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);
    }
    return orderSolrWriterService;
  }

  public void setOrderSolrWriterService(IOrderSolrWriterService orderSolrWriterService) {
    this.orderSolrWriterService = orderSolrWriterService;
  }

  public IUserService getUserService() {
    if(userService == null){
      userService = ServiceManager.getService(IUserService.class);
    }
    return userService;
  }

  public void setUserService(IUserService userService) {
    this.userService = userService;
  }

  public IVehicleService getVehicleService() {
    if(vehicleService == null){
      vehicleService = ServiceManager.getService(IVehicleService.class);
    }
    return vehicleService;
  }

  public void setVehicleService(IVehicleService vehicleService) {
    this.vehicleService = vehicleService;
  }

  @RequestMapping(params = "method=createWashBeautyOrder")
  public String createWashBeautyOrder(ModelMap model,HttpServletRequest request,RepairOrderDTO repairOrderDTO){
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try{
      WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
      washBeautyOrderDTO.setShopId(shopId);
      if(repairOrderDTO != null){

        //根据repairOrderDTO.customerId获取客户和会员信息
        setMemberAndCustomerInfo(washBeautyOrderDTO, null, repairOrderDTO.getCustomerId());
        //如果没有获取到信息
        if(washBeautyOrderDTO.getCustomerId() == null){
          washBeautyOrderDTO.setCustomer(repairOrderDTO.getCustomerName());
          washBeautyOrderDTO.setContact(repairOrderDTO.getContact());
          washBeautyOrderDTO.setMobile(repairOrderDTO.getMobile());
          washBeautyOrderDTO.setLandLine(repairOrderDTO.getLandLine());
        }
        boolean vehicleExist = false;
        if(StringUtils.isNotBlank(repairOrderDTO.getLicenceNo()) && repairOrderDTO.getCustomerId()!=null){
          List<VehicleDTO> vehicleDTOs = vehicleService.getVehicleListByCustomerId(repairOrderDTO.getCustomerId());
          if(CollectionUtils.isNotEmpty(vehicleDTOs)){
            for(VehicleDTO vehicleDTO : vehicleDTOs){
              if(vehicleDTO.getLicenceNo().trim().equalsIgnoreCase(repairOrderDTO.getLicenceNo().trim())){
                washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
                vehicleExist = true;
                break;
              }
            }
          }
        }
        if(!vehicleExist){
          washBeautyOrderDTO.setLicenceNo(repairOrderDTO.getLicenceNo());
          washBeautyOrderDTO.setBrand(repairOrderDTO.getBrand());
          washBeautyOrderDTO.setModel(repairOrderDTO.getModel());
        }
        if(washBeautyOrderDTO.getCustomerId() != null){
          washBeautyOrderDTO.setMemberDTO(membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId()));
        }
        if(washBeautyOrderDTO.getMemberDTO() != null && washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs() != null){
          for(MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()){
            ServiceDTO service = txnService.getServiceById(memberServiceDTO.getServiceId());
            if(service != null){
              memberServiceDTO.setServiceName(service.getName());
            }
          }
        }
      }
      //消费时间，即单据归属时间，默认为系统当前时间
      if(null == washBeautyOrderDTO.getVestDate()){
        washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      }else{
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      }
      washBeautyOrderDTO.setSalesManDTOs(getUserService().getSalesManList(shopId));

      if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
        setUseTimesMostService(washBeautyOrderDTO);
      }
      model.put("washBeautyOrderDTO",washBeautyOrderDTO);
      model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    }catch (Exception e){
      LOG.debug("/washBeauty.do");
      LOG.debug("method=createWashBeautyOrder");
      LOG.error(e.getMessage(),e);
      e.printStackTrace();
    }
    return "/txn/carWash";
  }

  @Deprecated
  @RequestMapping(params = "method=addNewWashBeautyOrderItemDTO")
  public String addNewWashBeautyOrderItemDTO(ModelMap model,HttpServletRequest request,WashBeautyOrderDTO washBeautyOrderDTO){
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    try {
      if(washBeautyOrderDTO != null){
        //过滤掉空行。
        if (washBeautyOrderDTO.getWashBeautyOrderItemDTOs() != null && washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length > 0) {
          List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>();
          for (WashBeautyOrderItemDTO w : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
            if (w.getServiceId() != null) {
              washBeautyOrderItemDTOs.add(w);
            }
          }
          washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs.toArray(new WashBeautyOrderItemDTO[washBeautyOrderItemDTOs.size()]));
        }
        boolean flag= true;
        Double total = 0d;
        int size = ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())?0:washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length;
        washBeautyOrderDTO.setServiceDTOs(txnService.getServiceByWashBeauty(shopId,washBeautyOrderDTO.getMemberDTO()));
        if(washBeautyOrderDTO.getServiceDTOs() == null){
          ServiceDTO[] serviceDTOs = new ServiceDTO[1];
          serviceDTOs[0] = new ServiceDTO();
          serviceDTOs[0].setName("无服务");
          washBeautyOrderDTO.setServiceDTOs(serviceDTOs);
        }

        if(!ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())){
          for(WashBeautyOrderItemDTO w : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()){
            if(w.getServiceId() == null){
              w.setSurplusTimes(washBeautyOrderDTO.getServiceDTOs()[0].getSurplusTimes());
              w.setPrice(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
              flag = false;
            } else {
              for(ServiceDTO serviceDTO : washBeautyOrderDTO.getServiceDTOs()){
                if(w.getServiceId().equals(serviceDTO.getId())){
                  //                w.setPrice(serviceDTO.getPrice());
                  w.setSurplusTimes(serviceDTO.getSurplusTimes());
                  break;
                }
              }
            }
            if(!ConsumeType.TIMES.equals(w.getConsumeTypeStr())){
              total += (null == w.getPrice()?0:w.getPrice());
            }
          }
        }
        if(flag){
          total = 0d;
          WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[size + 1];
          for(int i = 0;i < size;i++){
            washBeautyOrderItemDTOs[i] = washBeautyOrderDTO.getWashBeautyOrderItemDTOs()[i];
            for (ServiceDTO serviceDTO : washBeautyOrderDTO.getServiceDTOs()){
              if(washBeautyOrderItemDTOs[i].getServiceId().equals(serviceDTO.getId())){
//                washBeautyOrderItemDTOs[i].setPrice(serviceDTO.getPrice());
                washBeautyOrderItemDTOs[i].setSurplusTimes(serviceDTO.getSurplusTimes());
                break;
              }
            }
            if(!ConsumeType.TIMES.equals(washBeautyOrderItemDTOs[i].getConsumeTypeStr())){
              total += (null==washBeautyOrderItemDTOs[i].getPrice()?0:washBeautyOrderItemDTOs[i].getPrice());
            }
          }
          washBeautyOrderItemDTOs[size] = new WashBeautyOrderItemDTO();
          washBeautyOrderItemDTOs[size].setSurplusTimes(washBeautyOrderDTO.getServiceDTOs()[0].getSurplusTimes());
          washBeautyOrderItemDTOs[size].setPrice(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
          total += (null == washBeautyOrderItemDTOs[size].getPrice()?0:washBeautyOrderItemDTOs[size].getPrice());
          washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
        }
        washBeautyOrderDTO.setTotal(total);
        if(null != washBeautyOrderDTO.getCustomerId())
        {
          CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId,washBeautyOrderDTO.getCustomerId());
          if(null != customerRecordDTO)
          {
            washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(),0D));
          }
          else
          {
            washBeautyOrderDTO.setTotalReturnDebt(0D);
          }
        }

        washBeautyOrderDTO.setSalesManDTOs(userService.getSalesManList(shopId));
        model.put("washBeautyOrderDTO",washBeautyOrderDTO);
      }
    } catch (Exception e){
      LOG.debug("/washBeauty.do");
      LOG.debug("method=addNewWashBeautyOrderItemDTO");
      LOG.error(e.getMessage(),e);
      e.printStackTrace();
    }
    return "/txn/carWash";
  }


  @RequestMapping(params = "method=getCustomerInfoByName")
  public String getCustomerInfoByName(ModelMap modelMap,HttpServletRequest request, @RequestParam(required=false) String licenceNo,
                                      @RequestParam(required = false) String brand, @RequestParam(required=false) String model){
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = WebUtil.getShopId(request);
    Long customerId = Long.parseLong(request.getParameter("customerId"));
    try{
      WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
      washBeautyOrderDTO.setShopId(shopId);
      if(StringUtils.isNotBlank(licenceNo)){                       //如果传了车牌号,先查看是否存在,不存在(即新车)则可直接扔回页面.
        List<VehicleDTO> vehicleDTOList = userService.getVehicleByLicenceNo(shopId, licenceNo);
        if(CollectionUtils.isEmpty(vehicleDTOList)){
          washBeautyOrderDTO.setLicenceNo(licenceNo);
        }
      }
      if(StringUtils.isBlank(washBeautyOrderDTO.getLicenceNo())){           //如果处理后车牌号为空, 则直接获取此客户的车辆信息
        List<VehicleDTO> vehicleDTOs = vehicleService.getVehicleListByCustomerId(customerId);
        if(CollectionUtils.isNotEmpty(vehicleDTOs) && vehicleDTOs.size() == 1){
          washBeautyOrderDTO.setVehicleDTO(vehicleDTOs.get(0));
        }else if(CollectionUtils.isNotEmpty(vehicleDTOs) && vehicleDTOs.size() > 1 && StringUtils.isNotBlank(licenceNo)){
          for(VehicleDTO vehicleDTO : vehicleDTOs){
            if(vehicleDTO.getLicenceNo().trim().equalsIgnoreCase(licenceNo.trim())){
              washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
              break;
            }
          }
        }
      }
      if(StringUtils.isNotBlank(brand)) {
        washBeautyOrderDTO.setBrand(brand);
      }
      if(StringUtils.isNotBlank(model)) {
        washBeautyOrderDTO.setModel(model);
      }
      String vehicleColor = request.getParameter("vehicleColor");
      String vehicleChassisNo = request.getParameter("vehicleChassisNo");
      String vehicleEngineNo = request.getParameter("vehicleEngineNo");
      if (StringUtils.isNotBlank(vehicleColor)) {
        washBeautyOrderDTO.setVehicleColor(vehicleColor);
      }
      if (StringUtils.isNotBlank(vehicleChassisNo)) {
        washBeautyOrderDTO.setVehicleChassisNo(vehicleChassisNo);
      }
      if (StringUtils.isNotBlank(vehicleEngineNo)) {
        washBeautyOrderDTO.setVehicleEngineNo(vehicleEngineNo);
      }
      if(StringUtils.isNotBlank(request.getParameter("vehicleContact"))) {
        washBeautyOrderDTO.setVehicleContact(request.getParameter("vehicleContact"));
      }
      if(StringUtils.isNotBlank(request.getParameter("vehicleMobile"))) {
        washBeautyOrderDTO.setVehicleMobile(request.getParameter("vehicleMobile"));
      }
      if(StringUtils.isNotBlank(request.getParameter("appointOrderId")) && NumberUtil.isNumber(request.getParameter("appointOrderId"))) {
        washBeautyOrderDTO.setAppointOrderId(Long.parseLong(request.getParameter("appointOrderId")));
      }
      setMemberAndCustomerInfo(washBeautyOrderDTO, null, customerId);
      if(StringUtils.isNotBlank(request.getParameter("receiptNo")) && StringUtils.isBlank(washBeautyOrderDTO.getReceiptNo()))
      {
        washBeautyOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
      }

      //消费时间，即单据归属时间，默认为系统当前时间
      if(null == washBeautyOrderDTO.getVestDate()){
        washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      }else{
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      }
      if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
        setUseTimesMostService(washBeautyOrderDTO);
      }

      washBeautyOrderDTO.setSalesManDTOs(getUserService().getSalesManList(shopId));
      modelMap.put("washBeautyOrderDTO",washBeautyOrderDTO);
    } catch (Exception e){
      LOG.error("/washBeauty.do?method=getCustomerInfoByName"+e.getMessage(),e);
    }
//    //reindex customer in solr
//    ServiceManager.getService(ICustomerService.class).reindexCustomerByCustomerId(customerId);
    return "/txn/carWash";
  }

  @RequestMapping(params = "method=getCustomerInfoByLicenceNo")
  public String getCustomerInfoByLicenceNo(ModelMap model, HttpServletRequest request, @RequestParam(required = false) String customer,
                                           @RequestParam(required = false) String mobile, @RequestParam(required = false) String landLine) {
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    String licenceNo = request.getParameter("licenceNo");
    String customerIdStr = request.getParameter("customerId");
    Long customerId = NumberUtil.longValue(customerIdStr);

    try {
      List<VehicleDTO> vehicleDTOs = userService.getVehicleByLicenceNo(shopId, licenceNo);
      List<CustomerDTO> customerDTOs = userService.getCustomerByLicenceNo(shopId, licenceNo); //先根据shopId和车牌号找客户
      WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
      CustomerDTO customerDTO = null;
      if ("customer".equals(request.getParameter("type"))) {
        if (customerDTOs != null && customerDTOs.size() > 0) {
          customerDTO = customerDTOs.get(0);
        } else if (customerId != null) {        //如果没找到再根据customerId找客户(如果customerId存在
          customerDTO = userService.getCustomerDTOByCustomerId(customerId, shopId);
        }
      } else {
        if (customerDTOs != null && customerDTOs.size() > 0) {
          customerDTO = customerDTOs.get(0);
        }
      }

      if (vehicleDTOs != null && vehicleDTOs.size() > 0) {
        washBeautyOrderDTO.setVehicleDTO(vehicleDTOs.get(0));
      } else {
        if ("customer".equals(request.getParameter("type"))) {
          washBeautyOrderDTO.setBrand(request.getParameter("brand"));
          washBeautyOrderDTO.setModel(request.getParameter("model"));
          washBeautyOrderDTO.setVehicleContact(request.getParameter("vehicleContact"));
          washBeautyOrderDTO.setVehicleMobile(request.getParameter("vehicleMobile"));
          washBeautyOrderDTO.setVehicleColor(request.getParameter("vehicleColor"));
          washBeautyOrderDTO.setVehicleChassisNo(request.getParameter("vehicleChassisNo"));
          washBeautyOrderDTO.setVehicleEngineNo(request.getParameter("vehicleEngineNo"));
        }
      }
      if (customerDTO != null) {
        washBeautyOrderDTO.setCustomerDTO(customerDTO);
        CustomerRecordDTO customerRecordDTO = userService.getCustomerRecordDTOByCustomerIdAndShopId(shopId,customerDTO.getId());
        if(null != customerRecordDTO)
        {
          washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(),0D));
        }
        else
        {
          washBeautyOrderDTO.setTotalReturnDebt(0D);
        }
        if (washBeautyOrderDTO.getCustomerId() != null) {

          MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId());

          if (null != memberDTO) {
            memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
            memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
          }

          washBeautyOrderDTO.setMemberDTO(memberDTO);
        }
      } else {
        if ("customer".equals(request.getParameter("type"))) {
          washBeautyOrderDTO.setCustomer(customer);
          washBeautyOrderDTO.setMobile(mobile);
          washBeautyOrderDTO.setLandLine(landLine);
        } else if (null == customerDTO) {
          if (null != customerId) {

            List<CustomerVehicleDTO> customerVehicleDTOList = userService.getVehicleByCustomerId(customerId);
            if (CollectionUtils.isEmpty(customerVehicleDTOList)) {
              washBeautyOrderDTO.setCustomer(customer);
              washBeautyOrderDTO.setMobile(mobile);
              washBeautyOrderDTO.setLandLine(landLine);
              washBeautyOrderDTO.setCustomerId(customerId);
              MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId());
              if (null != memberDTO) {
                memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
                memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
              }
              washBeautyOrderDTO.setMemberDTO(memberDTO);
            }
          }
        }
      }
      if (washBeautyOrderDTO.getMemberDTO() != null && washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs() != null) {
        for (MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()) {
          Service service = txnService.getServiceById(memberServiceDTO.getServiceId());
          if (service != null) {
            memberServiceDTO.setServiceName(service.getName());
          }
        }
      }
      washBeautyOrderDTO.setServiceDTOs(txnService.getServiceByWashBeauty(shopId, washBeautyOrderDTO.getMemberDTO()));
      if (washBeautyOrderDTO.getServiceDTOs() == null) {
        ServiceDTO[] serviceDTOs = new ServiceDTO[1];
        serviceDTOs[0] = new ServiceDTO();
        serviceDTOs[0].setName("无服务");
        washBeautyOrderDTO.setServiceDTOs(serviceDTOs);
      }
//      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
//      washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
//      washBeautyOrderItemDTOs[0].setSurplusTimes(washBeautyOrderDTO.getServiceDTOs()[0].getSurplusTimes());
//      washBeautyOrderItemDTOs[0].setPrice(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
//      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
//      washBeautyOrderDTO.setTotal(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
      washBeautyOrderDTO.setLicenceNo(licenceNo);
      washBeautyOrderDTO.setSalesManDTOs(userService.getSalesManList(shopId));
      setTotalDebtAndConsume(washBeautyOrderDTO);

      String vestDateStr = request.getParameter("vestDateStr");
      if(StringUtil.isNotEmpty(vestDateStr)){
        washBeautyOrderDTO.setVestDateStr(vestDateStr);
      }else{
        washBeautyOrderDTO.setVestDateStr(DateUtil.getNowTimeStr(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
      }
      washBeautyOrderDTO.setShopId(shopId);
      if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
        setUseTimesMostService(washBeautyOrderDTO);
      }
      model.put("washBeautyOrderDTO", washBeautyOrderDTO);
//      model.put("washBeautyOrderDTOJson",JsonUtil.objectToJson(washBeautyOrderDTO));
    } catch (Exception e) {
      LOG.debug("/washBeauty.do");
      LOG.debug("method=getCustomerInfoByLicenceNo");
      e.printStackTrace();
    }
    return "/txn/carWash";
  }


  @RequestMapping(params = "method=getCustomerInfoByMemberNo")
  public String getCustomerInfoByMemberNo(ModelMap model, HttpServletRequest request) {

    try {
      IUserService userService = ServiceManager.getService(IUserService.class);
      String memberNo = request.getParameter("memberNo");
      if (StringUtils.isBlank(memberNo)) {
        return createWashBeautyOrder(model, request, null);
      }
      if (memberNo.startsWith("\u0010")) {
        memberNo = memberNo.replace("\u0010", "");
      }
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
      washBeautyOrderDTO.setShopId(shopId);

      this.setMemberAndCustomerInfo(washBeautyOrderDTO, memberNo, null);
      this.setVehicleInfo(washBeautyOrderDTO, washBeautyOrderDTO.getCustomerId());

      washBeautyOrderDTO.setSalesManDTOs(getUserService().getSalesManList(shopId));

      if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
        setUseTimesMostService(washBeautyOrderDTO);
      }
      model.put("washBeautyOrderDTO", washBeautyOrderDTO);
    } catch (Exception e) {
      LOG.debug("/washBeauty.do");
      LOG.debug("method=getCustomerInfoByMemberNo");
      e.printStackTrace();
    }

    return "/txn/carWash";

  }



  @RequestMapping(params = "method=washBeautyAccount")
  public String washBeautyAccount(HttpServletRequest request) throws Exception{
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    String customerIdStr = request.getParameter("customerId");
    Long shopId = WebUtil.getShopId(request);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    if (StringUtils.isNotBlank(customerIdStr) && NumberUtil.isLongNumber(customerIdStr)) {
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
    if(messageSwitchDTO == null || (messageSwitchDTO != null && MessageSwitchStatus.ON.equals(messageSwitchDTO.getStatus()))) {
      request.setAttribute("smsSwitch",true);
    } else {
      request.setAttribute("smsSwitch",false);
    }
    return "/txn/washBeautyAccount1";
  }

  @RequestMapping(params = "method=validateWashBeautyOrder")
  public void validateRepairOrder(HttpServletRequest request, HttpServletResponse response, WashBeautyOrderDTO washBeautyOrderDTO) {
    String resultStr = "";
    IMemberCheckerService memberCheckerService = ServiceManager.getService(IMemberCheckerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    try {
      Long shopId = (Long) request.getSession().getAttribute("shopId");

      resultStr = memberCheckerService.checkWashBeautyOrderDTO(shopId, washBeautyOrderDTO);
    } catch (Exception e) {
      resultStr = MemberConstant.SUBMIT_EXCEPTION;
      LOG.error("/washBeauty.do?method=validateWashBeautyOrder" + e.getMessage(), e);
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(resultStr);
      writer.close();
    } catch (IOException e) {
      LOG.error("/washBeauty.do?method=validateWashBeautyOrder" + e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=saveWashBeautyOrder")
  public String saveWashBeautyOrder(ModelMap model,HttpServletRequest request,WashBeautyOrderDTO washBeautyOrderDTO)throws Exception{
    Long shopId = WebUtil.getShopId(request);
    LOG.info("saveWashBeautyOrder 开始,shopId:{}",shopId);
    long begin = System.currentTimeMillis();
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    IWashBeautyService washBeautyService = ServiceManager.getService(IWashBeautyService.class);
    IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
    Long userId = WebUtil.getUserId(request);
    if(shopId == null || washBeautyOrderDTO == null ||
      ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) || StringUtils.isEmpty(washBeautyOrderDTO.getLicenceNo())){
      return "/txn/carWash";
    }
    try{
      //组装洗车单静态信息
      prepareWashBeauty(request,washBeautyOrderDTO);
      //单据号
      if (StringUtils.isBlank(washBeautyOrderDTO.getReceiptNo())) {
        washBeautyOrderDTO.setReceiptNo(txnService.getReceiptNo(shopId, OrderTypes.WASH_BEAUTY, null));
      }
      //保存车型基本信息
      rfiTxnService.populateWashBeautyOrderDTO(washBeautyOrderDTO);
      //处理客户信息
      LOG.info("saveWashBeautyOrder--doCustomerAndVehicle处理客户信息");
      rfiTxnService.doCustomerAndVehicle(shopId, userId, washBeautyOrderDTO.getCustomerId(), washBeautyOrderDTO);
      //处理施工人信息
      washBeautyService.setServiceWorks(washBeautyOrderDTO);
      LOG.info("saveWashBeautyOrder--do start");
      //保存洗车单
      washBeautyOrderDTO = washBeautyService.saveWashBeautyOrder(shopId, userId, washBeautyOrderDTO);
      //会员结算
      washBeautyService.accountMemberWithWashBeauty(washBeautyOrderDTO);
      //更新关联预约单的状态
      appointOrderService.handelAppointOrderAfterSaveWashBeauty(washBeautyOrderDTO);
      model.put("resultMsg", "success");
      //洗车美容营业统计
      BcgogoEventPublisher bcgogoEventPublisher  = new BcgogoEventPublisher();
      WashOrderSavedEvent washOrderSavedEvent = new WashOrderSavedEvent(washBeautyOrderDTO);
      bcgogoEventPublisher.publisherWashBeautyOrderSaved(washOrderSavedEvent);
      //每新增一张单据，就要将同一个客户里面的欠款提醒的状态改为未提醒
      ServiceManager.getService(ITxnService.class).updateRemindEventStatus(washBeautyOrderDTO.getShopId(),washBeautyOrderDTO.getCustomerId(),"customer");
      //发送微信账单到车主
      ServiceManager.getService(IWXTxnService.class).sendConsumeMsg(washBeautyOrderDTO);
      //更新代金券消费记录的相关信息
      washBeautyService.updateConsumingRecordFromRepairOrder(washBeautyOrderDTO);
       LOG.info("saveWashBeautyOrder--success");
    } catch (Exception e){
      LOG.error("/washBeauty.do method=saveWashBeautyOrder");
      LOG.error(e.getMessage(), e);
      LOG.error(washBeautyOrderDTO.toString());
      model.put("resultMsg", "failure");
      return "redirect:/washBeauty.do?method=getCustomerInfoByLicenceNo&licenceNo="+ URLEncoder.encode(washBeautyOrderDTO.getLicenceNo(),"UTF-8")+"&customerId="+washBeautyOrderDTO.getCustomerId();
    }
    LOG.debug("AOP_Ctr:saveWashBeautyOrder 结束，用时：{}ms",System.currentTimeMillis()- begin);
    return "redirect:/washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId="+washBeautyOrderDTO.getId()+"&print="+washBeautyOrderDTO.getPrint();
  }

  private void prepareWashBeauty(HttpServletRequest request, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception {
    washBeautyOrderDTO.setShopId(WebUtil.getShopId(request));
    washBeautyOrderDTO.setUserId(WebUtil.getUserId(request));
    washBeautyOrderDTO.setUserName(WebUtil.getUserName(request));
    //保存消费时间
    String vestDateStr = washBeautyOrderDTO.getVestDateStr();
    if (StringUtil.isNotEmpty(vestDateStr)) {
      Long vestDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, vestDateStr);
      washBeautyOrderDTO.setVestDate(vestDate);
    } else {
      washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
      washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, washBeautyOrderDTO.getVestDate()));
    }
    washBeautyOrderDTO.setEditDate(System.currentTimeMillis());
    //过滤掉空行,去掉营业分类首位空格。
    if (!ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
      List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOs = new ArrayList<WashBeautyOrderItemDTO>();
      for (WashBeautyOrderItemDTO w : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if (w.getServiceId() != null) {
          if (StringUtils.isNotBlank(w.getBusinessCategoryName())) {
            w.setBusinessCategoryName(w.getBusinessCategoryName().trim());
          }
          washBeautyOrderItemDTOs.add(w);
        }
      }
      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs.toArray(new WashBeautyOrderItemDTO[washBeautyOrderItemDTOs.size()]));
    }
    if (washBeautyOrderDTO.getAfterMemberDiscountTotal() == null) {
      washBeautyOrderDTO.setAfterMemberDiscountTotal(washBeautyOrderDTO.getTotal());
    }
    //还款时间
    washBeautyOrderDTO.setRepaymentTime(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", washBeautyOrderDTO.getHuankuanTime()));
    washBeautyOrderDTO.setVechicle(washBeautyOrderDTO.getLicenceNo());
  }

//  public void getTotalDebts(Long customerId, Long shopId, ModelMap model) throws BcgogoException {
//    IUserService userService = ServiceManager.getService(IUserService.class);
//    double total = 0;
//    if (customerId != null) {
//      List<CustomerRecordDTO> custs = userService.getCustomerRecordByCustomerId(customerId);
//      if (custs.size() == 1){
//        total = custs.get(0).getTotalReceivable();
//    }
//  }
//    model.addAttribute("totalDebt", NumberUtil.round(total,NumberUtil.MONEY_PRECISION));
//  }
//
//  public void getTotalConsume(Long customerId, Long shopId, ModelMap model) throws BcgogoException {
//    IUserService userService = ServiceManager.getService(IUserService.class);
//    Double totalConsume = 0d;
//    if(customerId != null){
//      List<CustomerRecordDTO> customerRecordDTOs = userService.getCustomerRecordByCustomerId(customerId);
//      if(customerRecordDTOs != null && customerRecordDTOs.size() > 0){
//        totalConsume = customerRecordDTOs.get(0).getTotalAmount();
//      }
//    }
//    model.put("totalConsume",totalConsume);
//  }

  @RequestMapping(params = "method=printWashBeautyTicket")
  public void printWashBeautyTicket(HttpServletRequest request,HttpServletResponse response,Long orderId) throws Exception
  {
    if(null == orderId)
    {
      return;
    }
    WashBeautyOrderDTO washBeautyOrderDTO = null;
    try{
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      Long shopId = (Long)request.getSession().getAttribute("shopId");

      ShopDTO shopDTO = configService.getShopById(shopId);

      String dataStr = DateUtil.convertDateLongToDateString(TxnConstant.FORMAT_STANDARD_YEAR_MONTH_DATE_HOUR_MINUTE,
        System.currentTimeMillis());

      washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId,orderId);

      washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,washBeautyOrderDTO.getVestDate()));
      washBeautyOrderDTO.setExecutor((String)request.getSession().getAttribute("userName"));

      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,null,washBeautyOrderDTO.getId());

      washBeautyOrderDTO.setReceivableDTO(receivableDTO);

      Long memberId = receivableDTO.getMemberId();

      MemberDTO memberDTO = null;
      Map<Long,MemberServiceDTO> memberServiceDTOMap = new HashMap<Long, MemberServiceDTO>();
      if(null != memberId)
      {
        memberDTO = membersService.getMemberDTOById(shopId,memberId);
        request.setAttribute("memberNo",memberDTO.getMemberNo());
        request.setAttribute("memberBalance",memberDTO.getBalance());
        if(CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs()))
        {
          memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
        }
      }
      else
      {
        memberDTO = membersService.getMemberByCustomerId(shopId,washBeautyOrderDTO.getCustomerId());
        if(null != memberDTO)
        {
          request.setAttribute("memberNo",memberDTO.getMemberNo());
          request.setAttribute("memberBalance",memberDTO.getBalance());
          if(CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs()))
          {
            memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
          }
        }
      }

      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = washBeautyOrderDTO.getWashBeautyOrderItemDTOs();

      if(null == washBeautyOrderItemDTOs || washBeautyOrderItemDTOs.length == 0)
      {
        return;
      }

      int consumeTimes = 0;

      for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOs)
      {
        ServiceDTO serviceDTO  = txnService.getServiceById(washBeautyOrderItemDTO.getServiceId());
        washBeautyOrderItemDTO.setServiceName(serviceDTO.getName());
        MemberServiceDTO memberServiceDTO = memberServiceDTOMap.get(washBeautyOrderItemDTO.getServiceId());
        washBeautyOrderItemDTO.setMemberServiceTime(null==memberServiceDTO?null:memberServiceDTO.getTimes());
        if(ConsumeType.TIMES == washBeautyOrderItemDTO.getPayType()){
          consumeTimes++;
        }

      }
      request.setAttribute("shopDTO",shopDTO);
      request.setAttribute("dataStr",dataStr);
      request.setAttribute("washBeautyOrderDTO",washBeautyOrderDTO);
      request.setAttribute("consumeTimes",consumeTimes);
      toPrint(txnService,request,response);
    }catch (Exception e)
    {
      LOG.error("printWashBeautyTicket");
      LOG.error("orderId",orderId);
      LOG.error("washBeautyOrderDTO",washBeautyOrderDTO);
      LOG.error(e.getMessage(),e);
      e.printStackTrace();
    }
  }

  public void toPrint(ITxnService txnService,HttpServletRequest request,HttpServletResponse response) throws Exception
  {
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.WASH_BEAUTY);

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

        String myTemplateName = "washBeautyPrint"+String.valueOf(shopId);

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
        ShopDTO shopDTO = (ShopDTO)request.getAttribute("shopDTO");
        String dataStr = (String)request.getAttribute("dataStr");
        String memberNo = (String)request.getAttribute("memberNo");
        Double memberBalance = (Double)request.getAttribute("memberBalance");
        WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO)request.getAttribute("washBeautyOrderDTO");
        for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs())  {
          if(ConsumeType.COUPON.equals(washBeautyOrderItemDTO.getConsumeTypeStr())) {
//            washBeautyOrderItemDTO.setConsumeTypeStr(ConsumeType.TIMES);
            washBeautyOrderItemDTO.setMemberServiceTime(null);
          }
        }
        context.put("memberNo",memberNo);
        context.put("dataStr",dataStr);
        context.put("shopDTO",shopDTO);
        context.put("washBeautyOrderDTO",washBeautyOrderDTO);
        context.put("memberBalance",memberBalance);
        context.put("consumeTimes",(Integer)request.getAttribute("consumeTimes"));
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
      LOG.debug(e.getMessage(), e);
    }
    finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=getWashBeautyOrder")
  public String getWashBeautyOrder(ModelMap model,HttpServletRequest request,HttpServletResponse response,@RequestParam("washBeautyOrderId") String washBeautyOrderIdStr)
  {
    if (StringUtils.isNotEmpty(washBeautyOrderIdStr) && !"null".equals(washBeautyOrderIdStr) && !"undefined".equals(washBeautyOrderIdStr)) {
      getWashBeautyOrderInfo(model, request, Long.valueOf(washBeautyOrderIdStr));
      List<ReceptionRecordDTO> receptionRecordDTOs = ServiceManager.getService(ITxnService.class).getSettledRecord(WebUtil.getShopId(request), OrderTypes.WASH_BEAUTY, Long.valueOf(washBeautyOrderIdStr));
      model.addAttribute("receptionRecordDTOs",receptionRecordDTOs);
      LOG.debug("washBeauty order {} ", washBeautyOrderIdStr);
    }
    return "/txn/carWashFinish";
  }

  public void getWashBeautyOrderInfo(ModelMap modelMap,HttpServletRequest request,Long washBeautyOrderId)
  {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IMembersService membersService = ServiceManager.getService(IMembersService.class);
    IServiceHistoryService serviceHistoryService = ServiceManager.getService(IServiceHistoryService.class);
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    String print = request.getParameter("print");
    WashBeautyOrderDTO washBeautyOrderDTO = null;

    try{
      washBeautyOrderDTO = txnService.getWashBeautyOrderDTOById(shopId,washBeautyOrderId);
      washBeautyOrderDTO.setPrint(print);
      List<VehicleDTO> vehicleDTOList ;
      if(washBeautyOrderDTO.getVechicleId()!=null){
        vehicleDTOList = userService.getVehicleByIds(shopId,washBeautyOrderDTO.getVechicleId());
      }else {
        vehicleDTOList = userService.getVehicleByLicenceNo(shopId,washBeautyOrderDTO.getVechicle());
      }
      washBeautyOrderDTO.setLicenceNo(washBeautyOrderDTO.getVechicle());
      modelMap.addAttribute("vehicleDTO", CollectionUtils.isNotEmpty(vehicleDTOList) ? vehicleDTOList.get(0) : null);
      Customer customer = userService.getCustomerByCustomerId(washBeautyOrderDTO.getCustomerId());
      if(customer!=null){
        modelMap.addAttribute("customerDTO", customer.toDTO());
      }
      List<CustomerRecordDTO> customerRecordDTOList = userService.getCustomerRecordByCustomerId(washBeautyOrderDTO.getCustomerId());
      Set<Long> serviceIds = washBeautyOrderDTO.getServiceIds();
      Map<Long,ServiceDTO> serviceDTOMap = new HashMap<Long, ServiceDTO>();
      double totalConsume = 0;
      double totalDebt = 0;

      if(CollectionUtils.isNotEmpty(customerRecordDTOList))
      {
        totalConsume = customerRecordDTOList.get(0).getTotalAmount();
        totalDebt = customerRecordDTOList.get(0).getTotalReceivable();
        washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTOList.get(0).getTotalPayable(),0D));
      }

      modelMap.addAttribute("totalConsume",totalConsume);
      modelMap.addAttribute("totalDebt",totalDebt);

      if(washBeautyOrderDTO.getCustomerId() != null){

        MemberDTO memberDTO = membersService.getMemberByCustomerId(shopId, washBeautyOrderDTO.getCustomerId());

        if(null != memberDTO)
        {
          memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
          memberDTO.setStatusStr(memberDTO.getStatus().getStatus());
        }

        washBeautyOrderDTO.setMemberDTO(memberDTO);
      }

      if(washBeautyOrderDTO.getMemberDTO() != null && washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs() != null){
        for(MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()){
          if(memberServiceDTO.getServiceId() != null) {
            serviceIds.add(memberServiceDTO.getServiceId());
          }
        }
      }
      serviceDTOMap = getRfiTxnService().getServiceDTOMapByIds(shopId,serviceIds);
      if (washBeautyOrderDTO.getMemberDTO() != null && washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs() != null) {
        for (MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()) {
          if (memberServiceDTO.getServiceId() != null) {
            serviceIds.add(memberServiceDTO.getServiceId());
          }
        }

        for (MemberServiceDTO memberServiceDTO : washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs()) {
          ServiceDTO serviceDTO = serviceDTOMap.get(memberServiceDTO.getServiceId());
          if (serviceDTO != null) {
            memberServiceDTO.setServiceName(serviceDTO.getName());
          }
        }
      }

      ServiceDTO[] serviceDTOs = null;
      Map<Long,MemberServiceDTO> memberServiceMap = MemberServiceDTO.listToMap(null==washBeautyOrderDTO.getMemberDTO()?null:washBeautyOrderDTO.getMemberDTO().getMemberServiceDTOs());
      if(!ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs()))
      {
        int i = 0;
        serviceDTOs = new ServiceDTO[washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length];
        for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs())
        {
          if(ConsumeType.TIMES == washBeautyOrderItemDTO.getPayType())
          {
            washBeautyOrderItemDTO.setPrice(0.0);
          }

          ServiceDTO service = serviceDTOMap.get(washBeautyOrderItemDTO.getServiceId());
          ServiceHistoryDTO serviceHistoryDTO = serviceHistoryService.getServiceHistoryById(washBeautyOrderItemDTO.getServiceHistoryId(), washBeautyOrderDTO.getShopId());

          if(serviceHistoryDTO != null){
            washBeautyOrderItemDTO.setServiceName(serviceHistoryDTO.getName());
          }else{
            washBeautyOrderItemDTO.setServiceName(service.getName());
          }
          if(service != null){
            serviceDTOs[i] = service;
            i++;
            if(null != memberServiceMap.get(service.getId()))
            {
              Integer times = memberServiceMap.get(service.getId()).getTimes();
              String surplusTimes = "";
              if(NumberUtil.isEqualNegativeOne(times))
              {
                surplusTimes = "无限次";
              }
              else
              {
                surplusTimes = String.valueOf(null==times?0:times.intValue());
              }
              washBeautyOrderItemDTO.setSurplusTimes(surplusTimes);
            }
          }
        }
      }

      ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,null,washBeautyOrderDTO.getId());

      washBeautyOrderDTO.setMemberAmount(receivableDTO.getMemberBalancePay());
      washBeautyOrderDTO.setBankAmount(receivableDTO.getBankCard());
      washBeautyOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
      washBeautyOrderDTO.setBankCheckAmount(receivableDTO.getCheque());
      washBeautyOrderDTO.setCashAmount(receivableDTO.getCash());
      washBeautyOrderDTO.setDebt(receivableDTO.getDebt());
      washBeautyOrderDTO.setOrderDiscount(receivableDTO.getDiscount());
      washBeautyOrderDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal()==null ? 0D:receivableDTO.getAfterMemberDiscountTotal());
      if(null != receivableDTO.getMemberDiscountRatio())
      {
        washBeautyOrderDTO.setMemberDiscountRatio(NumberUtil.round(receivableDTO.getMemberDiscountRatio()*10,1));
      }
      washBeautyOrderDTO.setPayee(receivableDTO.getLastPayee());

      List<ReceptionRecordDTO>  receptionRecordDTOs = txnService.getReceptionRecordByOrderId(shopId,washBeautyOrderDTO.getId(),OrderTypes.WASH_BEAUTY);

      if(CollectionUtils.isNotEmpty(receptionRecordDTOs))
      {
        washBeautyOrderDTO.setBankCheckNo(receptionRecordDTOs.get(0).getChequeNo());
      }

      if(null != receivableDTO.getMemberId())
      {
        MemberDTO memberDTO = membersService.getMemberDTOById(shopId,receivableDTO.getMemberId());
        washBeautyOrderDTO.setAccountMemberNo(memberDTO.getMemberNo());
      }
      washBeautyOrderDTO.setStrike(receivableDTO.getStrike());

      if(washBeautyOrderDTO.getDebt()>0)
      {
        List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(shopId, washBeautyOrderId);
        if(null != orderIndexDTOList)
        {
          washBeautyOrderDTO.setHuankuanTime(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,orderIndexDTOList.get(0).getPaymentTime()));
        }
      }

      washBeautyOrderDTO.setServiceDTOs(serviceDTOs);

      if(StringUtils.isNotBlank(request.getParameter("receiptNo")) && StringUtils.isBlank(washBeautyOrderDTO.getReceiptNo()))
      {
        washBeautyOrderDTO.setReceiptNo(request.getParameter("receiptNo"));
      }

      if(null != washBeautyOrderDTO.getVestDate())
      {
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,washBeautyOrderDTO.getVestDate()));
      }

      //获取代金券消费记录
      washBeautyOrderDTO.setCouponAmount(receivableDTO.getCoupon());
//      CouponConsumeRecordDTO couponConsumeRecordDTO=null;
//      if(null!=washBeautyOrderDTO.getConsumingRecordId()){
//        couponConsumeRecordDTO=ServiceManager.getService(ConsumingService.class).getCouponConsumeRecordById(washBeautyOrderDTO.getConsumingRecordId());
//      }
//      else{
//        couponConsumeRecordDTO=new CouponConsumeRecordDTO();
//      }
      modelMap.addAttribute("washBeautyOrderDTO",washBeautyOrderDTO);
      modelMap.addAttribute("afterMemberDeduction",NumberUtil.round(washBeautyOrderDTO.getAfterMemberDiscountTotal()-washBeautyOrderDTO.getSettledAmount()-washBeautyOrderDTO.getDebt(),NumberUtil.MONEY_PRECISION));
      modelMap.addAttribute("receiveNo",ServiceManager.getService(ITxnService.class).getStatementAccountOrderNo(shopId, washBeautyOrderDTO.getStatementAccountOrderId()));
//      modelMap.addAttribute("couponConsumeRecordDTO",couponConsumeRecordDTO);
    }catch (Exception e){
      LOG.error("getWashBeautyOrderInfo");
      LOG.error("shopId",shopId);
      LOG.error("washBeautyOrderId",washBeautyOrderId);
      LOG.error("washBeautyOrderDTO",washBeautyOrderDTO);
      LOG.error(e.getMessage(),e);
    }
  }

  @RequestMapping(params = "method=washBeautyOrderRepeal")
  public String washBeautyOrderRepeal(ModelMap modelMap,HttpServletRequest request,Long washBeautyOrderId, HttpServletResponse response){
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      WashBeautyOrderDTO washBeautyOrderDTO = null;
      if (washBeautyOrderId != null && shopId != null) {
        getWashBeautyOrderInfo(modelMap, request, washBeautyOrderId);
      }
      washBeautyOrderDTO = (WashBeautyOrderDTO) modelMap.get("washBeautyOrderDTO");
      if (washBeautyOrderDTO == null || washBeautyOrderDTO.getStatus() == null) {
        return "redirect:/washBeauty.do?method=createWashBeautyOrder" ;
      }
      if(OrderStatus.WASH_REPEAL.equals(washBeautyOrderDTO.getStatus()) || washBeautyOrderDTO.getStatementAccountOrderId() != null) {
        return "redirect:/washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId=" + washBeautyOrderId;
      }else if(OrderStatus.WASH_SETTLED.equals(washBeautyOrderDTO.getStatus())) {
        ReceivableDTO strikeReceivableDTO = getTxnService().getReceivableByShopIdAndOrderTypeAndOrderId(shopId, OrderTypes.WASH_BEAUTY, washBeautyOrderId);
        if(strikeReceivableDTO != null && strikeReceivableDTO.getStrike() != null && strikeReceivableDTO.getStrike() > 0){
          WebUtil.addSimpleJsMsg(modelMap, new Result("作废失败", "该单据已被冲帐结算，无法作废。", false));
          return getWashBeautyOrder(modelMap, request, response, washBeautyOrderId.toString());
        }

        washBeautyOrderDTO.setStatus(OrderStatus.WASH_REPEAL);
        //作废洗车单，更新洗车单状态，更新欠款，实收状态
        getRfiTxnService().repealWashOrderById(shopId,washBeautyOrderId);

        //ad by WLF 保存洗车单的作废日志
        ServiceManager.getService(ITxnService.class).saveOperationLogTxnService(
          new OperationLogDTO(shopId, (Long)request.getSession().getAttribute("userId"), washBeautyOrderId, ObjectTypes.WASH_ORDER, OperationTypes.INVALID));

        //更新客户累计消费累计欠款，会员信息
        ReceivableDTO receivableDTO = getTxnService().getReceivableDTOByShopIdAndOrderId(shopId,washBeautyOrderId);
        getCustomerService().updateCustomerAfterRepealWashOrder(washBeautyOrderDTO,receivableDTO);

        //更新search库 orderIndex，itemIndex状态，
        getSearchService().updateOrderIndexAfterRepealWashOrder(washBeautyOrderDTO);

        //生成作废单
        getRfiTxnService().saveRepealOrderByOrderIdAndOrderType(shopId, washBeautyOrderId, OrderTypes.WASH_BEAUTY);
        //代金券消费记录作废
        if (null!=washBeautyOrderDTO.getConsumingRecordId()) {
          ServiceManager.getService(ConsumingService.class).consumingRecordRepeal(shopId, washBeautyOrderDTO.getConsumingRecordId());
        }
        //洗车美容营业统计   重做order solr
        BcgogoEventPublisher bcgogoEventPublisher = new BcgogoEventPublisher();
        WashOrderSavedEvent washOrderSavedEvent = new WashOrderSavedEvent(washBeautyOrderDTO);
        bcgogoEventPublisher.publisherWashBeautyOrderSaved(washOrderSavedEvent);
        washOrderSavedEvent.setMainFlag(true);
        request.setAttribute("UNIT_TEST", washOrderSavedEvent); //单元测试
        return "redirect:/washBeauty.do?method=getWashBeautyOrder&washBeautyOrderId="+washBeautyOrderId;
      }
    } catch (Exception e) {
      LOG.error("method=washBeautyOrderRepeal,shopId:{},orderId:{}",shopId,washBeautyOrderId);
      LOG.error(e.getMessage(),e);
      return "redirect:/washBeauty.do?method=createWashBeautyOrder";
    }
    return "redirect:/washBeauty.do?method=createWashBeautyOrder";
  }

  @RequestMapping(params = "method=washBeautyOrderCopy")
  public String washBeautyOrderCopy(ModelMap modelMap,HttpServletRequest request,Long washBeautyOrderId){
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      WashBeautyOrderDTO washBeautyOrderDTO = null;
      if (washBeautyOrderId != null && shopId != null) {
        getWashBeautyOrderInfo(modelMap, request, washBeautyOrderId);
      }
      washBeautyOrderDTO = (WashBeautyOrderDTO) modelMap.get("washBeautyOrderDTO");
      if (washBeautyOrderDTO == null || washBeautyOrderDTO.getStatus() == null) {
        return "redirect:/method=createWashBeautyOrder";
      }
      //如果客户/车辆信息已修改
      CustomerDTO historyCustomerDTO = washBeautyOrderDTO.generateCustomerDTO();
      boolean customerSame = getCustomerService().compareCustomerSameWithHistory(historyCustomerDTO, shopId);
      VehicleDTO historyVehicleDTO = washBeautyOrderDTO.generateVehicleDTO();
      boolean vehicleSame = getVehicleService().compareVehicleSameWithHistory(historyVehicleDTO, shopId);
      if(!customerSame || !vehicleSame){
        washBeautyOrderDTO.clearCustomerInfo();
        modelMap.remove("customerDTO");
        modelMap.remove("totalConsume");
        modelMap.remove("totalDebt");
        washBeautyOrderDTO.clearVehicleInfo();
        modelMap.remove("vehicleDTO");
        washBeautyOrderDTO.setMemberDTO(null);
      }

      //remove已经删除的或已修改过的施工项目
      String disabledServiceInfoStr = getRfiTxnService().removeDisabledAndChangedServiceInfo(washBeautyOrderDTO);
      //生成新的单据
      getRfiTxnService().doCopyWashBeautyOrderDTO(washBeautyOrderDTO);
      //设置service
      washBeautyOrderDTO.setServiceDTOs(getRfiTxnService().getServiceByWashBeauty(shopId,washBeautyOrderDTO.getMemberDTO()));
      washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
      washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      if (washBeautyOrderDTO.getWashBeautyOrderItemDTOs() == null || washBeautyOrderDTO.getWashBeautyOrderItemDTOs().length == 0) {
        WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
        washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
        washBeautyOrderItemDTOs[0].setSurplusTimes(washBeautyOrderDTO.getServiceDTOs()[0].getSurplusTimes());
        washBeautyOrderItemDTOs[0].setPrice(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
        washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
        washBeautyOrderDTO.setTotal(washBeautyOrderDTO.getServiceDTOs()[0].getPrice());
      }
      modelMap.put("washBeautyOrderDTO",washBeautyOrderDTO);
      modelMap.put("disabledServiceInfoStr",disabledServiceInfoStr);
      return "/txn/carWash";
    } catch (Exception e){
      LOG.error("method=washBeautyOrderCopy,orderId:{}",washBeautyOrderId);
      LOG.error(e.getMessage(),e);
      return "redirect:/method=createWashBeautyOrder";
    }
  }

  @RequestMapping(params = "method=validateCopy")
  @ResponseBody
  public Result validateCopy(ModelMap model,HttpServletRequest request,Long washBeautyOrderId){
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      if (shopId == null || washBeautyOrderId == null) {
        LOG.error("washBeauty.do?method=validateCopy, shopId:{}, washBeautyOrderId:{}", shopId, washBeautyOrderId);
        return new Result("验证失败", "验证失败，请重试！", false);
      }
      return rfiTxnService.validateWashBeautyCopy(washBeautyOrderId, shopId);
    }catch(Exception e){
      LOG.error("washBeauty.do?method=validateCopy. shopId:{}, userId:{}", shopId, userId);
      LOG.error(e.getMessage(), e);
      return new Result("验证失败", "验证失败，请重试！", false);
    }
  }

  @RequestMapping(params = "method=printWashBeautyNoId")
  public void printWashBeautyNoId(HttpServletRequest request,HttpServletResponse response, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception{
    if(null == washBeautyOrderDTO) return;
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
    PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.WASH_BEAUTY);
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    try{
      if(null != printTemplateDTO){
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
        String myTemplateName = "washBeautyPrint"+String.valueOf(shopId);
        String myTemplate =  str;
        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);
        //从资源库中加载模板
        Template template = ve.getTemplate(myTemplateName);
        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName,"UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        washBeautyOrderDTO.setExecutor((String)request.getSession().getAttribute("userName"));
        if(StringUtils.isBlank(washBeautyOrderDTO.getVechicle())){
          washBeautyOrderDTO.setVechicle(washBeautyOrderDTO.getLicenceNo());
        }
        //把数据填入上下文
        String dataStr = DateUtil.convertDateLongToDateString(TxnConstant.FORMAT_STANDARD_YEAR_MONTH_DATE_HOUR_MINUTE,System.currentTimeMillis());
        String memberNo = "";
        Double memberBalance = 0D;
        Map<Long,MemberServiceDTO> memberServiceDTOMap = new HashMap<Long, MemberServiceDTO>();
        if(null != washBeautyOrderDTO.getCustomerId()){
          MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(shopId,washBeautyOrderDTO.getCustomerId());
          if(null != memberDTO){
            memberNo = memberDTO.getMemberNo();
            memberBalance = memberDTO.getBalance();
            if(CollectionUtils.isNotEmpty(memberDTO.getMemberServiceDTOs())){
              memberServiceDTOMap = MemberServiceDTO.listToMap(memberDTO.getMemberServiceDTOs());
            }
          }

        }
        int consumeTimes = 0;
        double totalPrice = 0d;
        removeNullItems(washBeautyOrderDTO);
        for(WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()){
          ServiceDTO serviceDTO  = txnService.getServiceById(washBeautyOrderItemDTO.getServiceId());
          washBeautyOrderItemDTO.setServiceName(serviceDTO.getName());
          MemberServiceDTO memberServiceDTO = memberServiceDTOMap.get(washBeautyOrderItemDTO.getServiceId());

          washBeautyOrderItemDTO.setMemberServiceTime(null==memberServiceDTO?null:memberServiceDTO.getTimes());
          if(ConsumeType.MONEY.equals(washBeautyOrderItemDTO.getConsumeTypeStr())){
            totalPrice += NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice());
          }else if(ConsumeType.TIMES.equals(washBeautyOrderItemDTO.getConsumeTypeStr())){
            consumeTimes++;
            if(washBeautyOrderItemDTO.getMemberServiceTime()!=null && washBeautyOrderItemDTO.getMemberServiceTime()>0){
              washBeautyOrderItemDTO.setMemberServiceTime(washBeautyOrderItemDTO.getMemberServiceTime() - 1);
            }
          } else if (ConsumeType.COUPON.equals(washBeautyOrderItemDTO.getConsumeTypeStr())) {
//            consumeTimes++;
//            washBeautyOrderItemDTO.setConsumeTypeStr(ConsumeType.TIMES);
            washBeautyOrderItemDTO.setPrice(null);
            washBeautyOrderItemDTO.setMemberServiceTime(null);
          }
        }
        totalPrice = NumberUtil.round(totalPrice, 2);
        washBeautyOrderDTO.setSettledAmount(totalPrice);
        if(null != washBeautyOrderDTO.getCustomerId()){
          CustomerDTO customerDTO= ServiceManager.getService(ICustomerService.class).getCustomerById(washBeautyOrderDTO.getCustomerId());
          if(customerDTO!=null){
            washBeautyOrderDTO.setAddress(customerDTO.getAddress());
            if(StringUtil.isEmpty(customerDTO.getMobile())){
              washBeautyOrderDTO.setLandLine(customerDTO.getLandLine());
            }else {
              washBeautyOrderDTO.setLandLine(customerDTO.getMobile());
            }
          }
        }
        context.put("memberNo",memberNo);
        context.put("dataStr",dataStr);
        context.put("shopDTO",shopDTO);
        context.put("washBeautyOrderDTO",washBeautyOrderDTO);
        context.put("isDebug",System.getProperty("is.developer.debug"));
        context.put("memberBalance",memberBalance);
        context.put("consumeTimes",consumeTimes);
        //输出流
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      }else{
        out.print(TxnConstant.NO_PRINT_TEMPLATE);
      }
    }catch(Exception e){
      LOG.debug(e.getMessage(),e);
    }finally {
      out.close();
    }
  }

  public void removeNullItems(WashBeautyOrderDTO washBeautyOrderDTO){
    if(washBeautyOrderDTO == null){
      return;
    }
    if(ArrayUtils.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())){
      return;
    }
    WashBeautyOrderItemDTO[] result = new WashBeautyOrderItemDTO[0];
    for(WashBeautyOrderItemDTO itemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()){
      if(itemDTO == null || itemDTO.getServiceId() == null){
        continue;
      }
      result = (WashBeautyOrderItemDTO[])ArrayUtils.add(result, itemDTO);
    }
    washBeautyOrderDTO.setWashBeautyOrderItemDTOs(result);
  }

  @RequestMapping(params = "method=queryCouponType")
  @ResponseBody
  public List<Pair> queryCouponType(HttpServletRequest request, String keyWord){
    Long shopId = WebUtil.getShopId(request);
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    try{
      OrderSearchConditionDTO conditionDTO = new OrderSearchConditionDTO();
      conditionDTO.setShopId(shopId);
      conditionDTO.setConsumeType("COUPON");
      conditionDTO.setCouponType(keyWord);
      conditionDTO.setOrderType(new String[]{OrderTypes.WASH_BEAUTY.toString()});
      conditionDTO.setSort("order_created_time desc");
      OrderSearchResultListDTO searchResultListDTO = searchOrderService.queryOrderItems(conditionDTO);
      if(CollectionUtils.isEmpty(searchResultListDTO.getOrderItems())){
        return null;
      }
      Set<String> distinctResult = new HashSet<String>();
      for(OrderItemSearchResultDTO itemDTO: searchResultListDTO.getOrderItems()){
        if(itemDTO!=null && StringUtils.isNotBlank(itemDTO.getCouponType())){
          distinctResult.add(itemDTO.getCouponType());
        }
      }
      List<Pair> couponTypes = new ArrayList<Pair>();
      for(String result : distinctResult){
        couponTypes.add(new Pair(result, result));
      }
      return couponTypes;
    }catch(Exception e){
      LOG.error("washBeauty.do?method=queryCouponType error", e);
      return null;
    }
  }


  @RequestMapping(params = "method=ajaxGetWashBeautyOrderByParameter")
  @ResponseBody
  public Object ajaxGetWashBeautyOrderByCustomer(HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    WashBeautyOrderDTO washBeautyOrderDTO = new WashBeautyOrderDTO();
    washBeautyOrderDTO.setShopId(shopId);

    try {
      String memberNo = request.getParameter("memberNo");
      String customerIdStr = request.getParameter("customerId");
      String licenceNo = request.getParameter("licenceNo");
      if (StringUtils.isBlank(memberNo) && StringUtil.isEmpty(customerIdStr) && StringUtil.isEmpty(licenceNo)) {
        setUseTimesMostService(washBeautyOrderDTO);
        return washBeautyOrderDTO;
      }

      if (StringUtil.isNotEmpty(customerIdStr) && NumberUtil.isLongNumber(customerIdStr)) {
        this.setMemberAndCustomerInfo(washBeautyOrderDTO, null, Long.valueOf(customerIdStr));
        this.setVehicleInfo(washBeautyOrderDTO, washBeautyOrderDTO.getCustomerId());
      } else if (StringUtil.isNotEmpty(licenceNo)) {

        Long customerId = null;
        List<VehicleDTO> vehicleDTOs = getUserService().getVehicleByLicenceNo(shopId, licenceNo);
        if (CollectionUtil.isEmpty(vehicleDTOs)) {
          washBeautyOrderDTO.setLicenceNo(licenceNo);
        } else if (vehicleDTOs.size() == 1) {
          VehicleDTO vehicleDTO = CollectionUtil.getFirst(vehicleDTOs);
          washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
          List<CustomerVehicleDTO> customerVehicleDTOs = getUserService().getCustomerVehicleByVehicleId(vehicleDTO.getId());
          if (CollectionUtil.isNotEmpty(customerVehicleDTOs)) {
            this.setMemberAndCustomerInfo(washBeautyOrderDTO, null, CollectionUtil.getFirst(customerVehicleDTOs).getCustomerId());
          }
        } else {
          for (VehicleDTO vehicleDTO : vehicleDTOs) {
            if (vehicleDTO.getLicenceNo().trim().equalsIgnoreCase(licenceNo.trim())) {
              washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
              List<CustomerVehicleDTO> customerVehicleDTOs = getUserService().getCustomerVehicleByVehicleId(vehicleDTO.getId());
              if (CollectionUtil.isNotEmpty(customerVehicleDTOs)) {
                this.setMemberAndCustomerInfo(washBeautyOrderDTO, null, CollectionUtil.getFirst(customerVehicleDTOs).getCustomerId());
              }
              break;
            }
          }
        }


      } else if (StringUtil.isNotEmpty(memberNo)) {
        if (memberNo.startsWith("\u0010")) {
          memberNo = memberNo.replace("\u0010", "");
        }
        this.setMemberAndCustomerInfo(washBeautyOrderDTO, memberNo, null);
        this.setVehicleInfo(washBeautyOrderDTO, washBeautyOrderDTO.getCustomerId());
      }

      if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
        setUseTimesMostService(washBeautyOrderDTO);
      }

      washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
      washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, washBeautyOrderDTO.getVestDate()));
      washBeautyOrderDTO.setSalesManDTOs(getUserService().getSalesManList(shopId));
      return washBeautyOrderDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return washBeautyOrderDTO;
  }

  /**
   * 根据memberNo或者customerId获取洗车单会员信息
   *
   * @param washBeautyOrderDTO
   * @param memberNo
   * @param customerId
   */
  private void setMemberAndCustomerInfo(WashBeautyOrderDTO washBeautyOrderDTO, String memberNo, Long customerId) {
    if (StringUtil.isEmpty(memberNo) && customerId == null && washBeautyOrderDTO.getShopId() == null) {
      return;
    }

    try {
      IMembersService membersService = ServiceManager.getService(IMembersService.class);

      MemberDTO memberDTO = null;
      Long shopId = washBeautyOrderDTO.getShopId();
      CustomerDTO customerDTO = null;

      if (customerId != null) {

        customerDTO = getCustomerService().getCustomerById(customerId,shopId);
        memberDTO = membersService.getMemberByCustomerId(shopId, customerId);
      } else if (StringUtil.isNotEmpty(memberNo)) {
        Member member = membersService.getMemberByShopIdAndMemberNo(shopId, memberNo);
        if (member != null) {
          memberDTO = member.toDTO();
          List<MemberServiceDTO> memberServiceDTOs = membersService.getMemberServiceEnabledByMemberId(shopId,member.getId());
          memberDTO.setMemberServiceDTOs(memberServiceDTOs);
          customerDTO = getCustomerService().getCustomerById(memberDTO.getCustomerId(), shopId);
        }
      }

      if (customerDTO == null || customerDTO.getStatus() == CustomerStatus.DISABLED) {
        return;
      }
      washBeautyOrderDTO.setCustomerDTO(customerDTO);
      washBeautyOrderDTO.setMobile(customerDTO.getMobile());
      setTotalDebtAndConsume(washBeautyOrderDTO);


      if (memberDTO == null || memberDTO.getStatus() == MemberStatus.DISABLED) {
        return;
      }

      memberDTO.setStatus(membersService.getMemberStatusByMemberDTO(memberDTO));
      memberDTO.setStatusStr(memberDTO.getStatus().getStatus());

      washBeautyOrderDTO.setMemberDTO(memberDTO);
      List<MemberServiceDTO> memberServiceDTOs = memberDTO.getMemberServiceDTOs();

      Set<Long> serviceIdSet = new HashSet<Long>();
      Map<Long, ServiceDTO> serviceDTOMap = new HashMap<Long, ServiceDTO>();
      if (CollectionUtil.isNotEmpty(memberServiceDTOs)) {
        for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
          serviceIdSet.add(memberServiceDTO.getServiceId());
        }
      }
      if (CollectionUtil.isNotEmpty(serviceIdSet)) {
        serviceDTOMap = getTxnService().getServiceByServiceIdSet(shopId, serviceIdSet);
      }


      if (CollectionUtil.isNotEmpty(memberServiceDTOs)) {
        for (MemberServiceDTO memberServiceDTO : memberServiceDTOs) {
          ServiceDTO serviceDTO = serviceDTOMap.get(memberServiceDTO.getServiceId());
          if (serviceDTO != null) {
            memberServiceDTO.setServiceName(serviceDTO.getName());
          }
        }

      }
    } catch (Exception e) {
      LOG.debug("/washBeauty.do method=getMemberInfo memberNo:" + memberNo + ",customerId:" + customerId);
      LOG.error(e.getMessage(), e);
    }
  }


  public void setUseTimesMostService(WashBeautyOrderDTO washBeautyOrderDTO) {
    if (washBeautyOrderDTO == null || ArrayUtil.isNotEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) || washBeautyOrderDTO.getShopId() == null) {
      return;
    }

    List<ServiceDTO> serviceDTOs = getTxnService().getUseTimesMostService(washBeautyOrderDTO.getShopId());
    ServiceDTO serviceDTO = CollectionUtil.getFirst(serviceDTOs);
    if (serviceDTO != null) {
      WashBeautyOrderItemDTO[] washBeautyOrderItemDTOs = new WashBeautyOrderItemDTO[1];
      CategoryDTO categoryDTO = getRfiTxnService().getCateGoryByServiceId(washBeautyOrderDTO.getShopId(), serviceDTO.getId());
      if (categoryDTO != null) {
        serviceDTO.setCategoryDTO(categoryDTO);
      }
      washBeautyOrderItemDTOs[0] = new WashBeautyOrderItemDTO();
      washBeautyOrderItemDTOs[0].fromServiceDTO(serviceDTO);
      washBeautyOrderItemDTOs[0].setConsumeTypeStr(ConsumeType.MONEY);
//      washBeautyOrderItemDTOs[0].setCouponType(ConsumeType.MONEY.getType());

      washBeautyOrderDTO.setWashBeautyOrderItemDTOs(washBeautyOrderItemDTOs);
      washBeautyOrderDTO.setTotal(NumberUtil.toReserve(serviceDTO.getPrice(), NumberUtil.MONEY_PRECISION));
    }

  }

  public void setVehicleInfo(WashBeautyOrderDTO washBeautyOrderDTO,Long customerId) {
    if(customerId == null){
      return;
    }
    try {
      List<VehicleDTO> vehicleDTOs = getVehicleService().getVehicleListByCustomerId(customerId);
      if (CollectionUtils.isNotEmpty(vehicleDTOs) && vehicleDTOs.size() == 1) {
        washBeautyOrderDTO.setVehicleDTO(vehicleDTOs.get(0));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      LOG.info("customerId:" +customerId);
    }
  }

  public void setTotalDebtAndConsume(WashBeautyOrderDTO washBeautyOrderDTO) {
    if (washBeautyOrderDTO.getShopId() == null || washBeautyOrderDTO.getCustomerId() == null) {
      return;
    }

    CustomerRecordDTO customerRecordDTO = getUserService().getCustomerRecordDTOByCustomerIdAndShopId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getCustomerId());
    if (null != customerRecordDTO) {
      washBeautyOrderDTO.setTotalReturnDebt(NumberUtil.numberValue(customerRecordDTO.getTotalPayable(), 0D));
      washBeautyOrderDTO.setTotalReceivable(NumberUtil.numberValue(customerRecordDTO.getTotalReceivable(), 0D));
      washBeautyOrderDTO.setTotalConsume(NumberUtil.numberValue(customerRecordDTO.getTotalAmount(), 0D));
    } else {
      washBeautyOrderDTO.setTotalReturnDebt(0D);
    }
  }

  /**
   *
   * @param request
   * @param name
   * @return
   */
  @RequestMapping(params = "method=searchService")
  @ResponseBody
  public Object searchService(HttpServletRequest request, @RequestParam("name") String name) {
    try {
      Long shopId = WebUtil.getShopId(request);
      List<Map<String, String>> result = new ArrayList<Map<String, String>>();
      String customerIdStr = request.getParameter("customerId");
      List<SearchSuggestionDTO> searchSuggestionDTOList = ServiceManager.getService(ISearchSuggestionService.class).getRepairServiceSuggestion(shopId, name);

      if (StringUtil.isNotEmpty(name)) {
        for (SearchSuggestionDTO searchSuggestionDTO : searchSuggestionDTOList) {
          result.add(searchSuggestionDTO.toRepairServiceDropDownItemMap());
        }
        return result;
      }

      Set<Long> serviceIdSet = new HashSet<Long>();
      IMembersService membersService = ServiceManager.getService(IMembersService.class);
      MemberDTO memberDTO = null;

      if (NumberUtil.isLongNumber(customerIdStr)) {
        memberDTO = membersService.getMemberByCustomerId(shopId, Long.valueOf(customerIdStr));
        if (memberDTO != null && CollectionUtil.isNotEmpty(memberDTO.getMemberServiceDTOs())) {
          this.sortMemberServiceDTOsForDropList(memberDTO.getMemberServiceDTOs());
          for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
            if (memberServiceDTO.getServiceId() == null) {
              continue;
            }
            serviceIdSet.add(memberServiceDTO.getServiceId());
          }
        }
      }

      if (CollectionUtil.isNotEmpty(serviceIdSet)) {
        Map<Long, ServiceDTO> serviceDTOMap = txnService.getServiceByServiceIdSet(shopId, serviceIdSet);
        if (memberDTO != null && CollectionUtil.isNotEmpty(memberDTO.getMemberServiceDTOs())) {


          for (MemberServiceDTO memberServiceDTO : memberDTO.getMemberServiceDTOs()) {
            if (memberServiceDTO.getServiceId() == null) {
              continue;
            }
            ServiceDTO serviceDTO = serviceDTOMap.get(memberServiceDTO.getServiceId());
            if (serviceDTO == null) {
              continue;
            }
            result.add(serviceDTO.toRepairServiceDropDownItemMap(memberServiceDTO));
          }
        }
      }
      Set<Long> washBeautyService  = new HashSet<Long>();

      List<ServiceDTO> serviceDTOs = getTxnService().getAllServiceDTOOfTimesByShopId(shopId);
      if (CollectionUtil.isNotEmpty(serviceDTOs)) {
        for (ServiceDTO serviceDTO : serviceDTOs) {
          if (serviceIdSet.contains(serviceDTO.getId())) {
            continue;
          }
          washBeautyService.add(serviceDTO.getId());
          result.add(serviceDTO.toRepairServiceDropDownItemMap(null));
        }
      }


      for (SearchSuggestionDTO searchSuggestionDTO : searchSuggestionDTOList) {
        Map<String, String> map = searchSuggestionDTO.toRepairServiceDropDownItemMap();
        if (MapUtils.isNotEmpty(map) && NumberUtil.isLongNumber(map.get("id"))) {
          if (serviceIdSet.contains(NumberUtil.longValue(map.get("id"))) || washBeautyService.contains(NumberUtil.longValue(map.get("id")))) {
            continue;
          }
        }
        result.add(searchSuggestionDTO.toRepairServiceDropDownItemMap());
      }
      return result;

    } catch (Exception e) {
      LOG.debug("/washBeauty.do method=searchService");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 洗车单服务项目下拉
   * @param memberServiceDTOs
   */
  private void sortMemberServiceDTOsForDropList(List memberServiceDTOs) {

    //会员消费项目按照：根据次数排序
    Collections.sort(memberServiceDTOs, new Comparator<MemberServiceDTO>() {
      @Override
      public int compare(MemberServiceDTO o1, MemberServiceDTO o2) {
        if(o1.getTimes() == -1){
          return -1;
        }else if(o2.getTimes() == -1){
          return 1;
        } else {
          return o1.getTimes() >= o2.getTimes() ? -1 : 1;
        }
      }
    });

  }

  @RequestMapping(params = "method=getBlankWashBeautyOrder")
  public String getBlankWashBeautyOrderByOrderId(ModelMap model, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception{
    IConsumingService consumingService=ServiceManager.getService(ConsumingService.class);
    IAppUserService appUserService=ServiceManager.getService(AppUserService.class);
    Long consumingRecordId=null;
    Long orderId=null;
    String appUserNo=null;
    Long shopId=WebUtil.getShopId(request);
    CouponConsumeRecordDTO couponConsumeRecordDTO=null;
    CustomerDTO customerDTO=new CustomerDTO();
    VehicleDTO vehicleDTO=new VehicleDTO();
    WashBeautyOrderDTO washBeautyOrderDTO=new WashBeautyOrderDTO();
    try {
      if (shopId != null) {
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
                return "redirect:/method=createWashBeautyOrder";
              }
            }
            //代金券消费金额不为空，且没有单据ID（即还未被消费）则返回couponConsumeRecordDTO，和consumingRecordId（代金券消费记录id）
            if (null==orderId && !OrderStatus.REPEAL.equals(couponConsumeRecordDTO.getOrderStatus()) && null!=couponConsumeRecordDTO.getCoupon()) {
              model.addAttribute("couponConsumeRecordDTO", couponConsumeRecordDTO);
              //给洗车美容单DTO添加代金券消费记录id
              washBeautyOrderDTO.setConsumingRecordId(couponConsumeRecordDTO.getId());
              washBeautyOrderDTO.setCouponAmount(couponConsumeRecordDTO.getCoupon());
            }
          } else {
            couponConsumeRecordDTO = new CouponConsumeRecordDTO();
            model.addAttribute("couponConsumeRecordDTO", couponConsumeRecordDTO);
          }
          //准备查找用户和车辆信息
          if (!StringUtils.isBlank(appUserNo)) {
            //通过appUserNo获取AppUserCustomerDTO列表
            List<AppUserCustomerDTO> appUserCustomerDTOs = appUserService.getAppUserCustomerByAppUserNoAndShopId(appUserNo, shopId);
            //通过AppUserCustomerDTO列表查找对应的customer和vehicle
            //先找到customerId的集合
            if (appUserCustomerDTOs != null && appUserCustomerDTOs.size() > 0) {
              Set<Long> customerIdSet = new HashSet<Long>();
              for (AppUserCustomerDTO dto : appUserCustomerDTOs) {
                Long customerId = dto.getCustomerId();
                if (customerId != null) {
                  customerIdSet.add(customerId);
                }
              }
              //再通过customerId找到CustomerVehicleDTO
              if (customerIdSet.size() > 0) {
                List<CustomerVehicleDTO> customerVehicleDTOs = userService.getCustomerVehicleDTOByCustomerId(customerIdSet);
                //之后分别获取Customer和Vehicle，都有效的则保留
                if (customerVehicleDTOs != null && customerVehicleDTOs.size() > 0) {
                  for (CustomerVehicleDTO dto : customerVehicleDTOs) {
                    if (dto.getVehicleId() != null && dto.getCustomerId() != null && !VehicleStatus.DISABLED.equals(dto.getStatus())) {
                      customerDTO = userService.getCustomerById(dto.getCustomerId());
                      vehicleDTO = userService.getVehicleById(dto.getVehicleId());
                      if (!CustomerStatus.DISABLED.equals(customerDTO.getStatus()) && !VehicleStatus.DISABLED.equals(vehicleDTO.getStatus())) {

                        break;
                      }
                    }
                  }
                }
              }
            }
          }
          //存放客户信息
          if (customerDTO != null) {
            //通过customerId获取会员及用户信息
            washBeautyOrderDTO.setCustomerId(customerDTO.getId());
            setMemberAndCustomerInfo(washBeautyOrderDTO, null, customerDTO.getId());
          }
          //存放车辆信息
          if (vehicleDTO != null) {
            washBeautyOrderDTO.setVehicleDTO(vehicleDTO);
            washBeautyOrderDTO.setLicenceNo(vehicleDTO.getLicenceNo());
          }
        }
      }
      //消费时间，即单据归属时间，默认为系统当前时间
      if(null == washBeautyOrderDTO.getVestDate()){
        washBeautyOrderDTO.setVestDate(System.currentTimeMillis());
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      }else{
        washBeautyOrderDTO.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,washBeautyOrderDTO.getVestDate()));
      }
      washBeautyOrderDTO.setSalesManDTOs(getUserService().getSalesManList(shopId));

      if (washBeautyOrderDTO != null && ArrayUtil.isEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())) {
        setUseTimesMostService(washBeautyOrderDTO);
      }
      model.put("washBeautyOrderDTO",washBeautyOrderDTO);
      model.addAttribute("fourSShopVersions", ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request)));
    }catch (Exception e){
      LOG.debug("/washBeauty.do");
      LOG.debug("method=createWashBeautyOrder");
      LOG.error(e.getMessage(),e);
      e.printStackTrace();
    }
    return "/txn/carWash";
  }
}
