package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.bcgogoApp.ApiAppointListResponse;
import com.bcgogo.api.bcgogoApp.ApiCRemindListResponse;
import com.bcgogo.api.bcgogoApp.ApiVFaultInfoListResponse;
import com.bcgogo.api.response.*;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.notification.SmsHelper;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.AppointOrderDTO;
import com.bcgogo.txn.dto.AppointOrderSearchCondition;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.service.IAppointOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.pushMessage.faultCode.IShopFaultInfoService;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.IContactService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

/**
 * 手机端车辆相关controller
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 上午11:07
 */
@Controller
public class BcgogoAppController {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoAppController.class);

  /**
   * 根据类型获取短信内容
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/type/{type}/idStr/{idStr}",
      method = RequestMethod.GET)
  public ApiResponse getMsgTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable("type") String type,
                                    @PathVariable("idStr") String idStr) throws Exception {
    ApiMsgContentResponse apiMsgContentResponse = null;

    try {
      if (StringUtil.isEmpty(type) || StringUtil.isEmpty(idStr) || !NumberUtil.isLongNumber(idStr)) {
        apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_FAIL));
        return apiMsgContentResponse;
      }
      if (type.equals("customerRemind")) {
        CustomerRemindSms customerRemindSms = this.getCustomerRemindSms(Long.valueOf(idStr));

        if (customerRemindSms != null) {
          apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_SUCCESS));
          apiMsgContentResponse.setMsgContent(customerRemindSms.getContent());
        } else {
          apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_FAIL));
        }
      } else if (type.equals("faultInfo")) {
        IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
        FaultInfoToShopDTO faultInfoToShopDTO = shopFaultInfoService.getShopFaultInfo(Long.valueOf(idStr));
        if (faultInfoToShopDTO == null) {
          apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_FAIL));
          return apiMsgContentResponse;
        }
        Map<String, Object> map = shopFaultInfoService.getSopFaultInfoMsgContent(faultInfoToShopDTO.getShopId(), faultInfoToShopDTO.getFaultCode(), faultInfoToShopDTO.getFaultCodeReportTimeStr(),
            faultInfoToShopDTO.getFaultAlertType(), faultInfoToShopDTO.getFaultAlertType().getValue(), faultInfoToShopDTO.getVehicleNo());
        if (MapUtils.isEmpty(map) || map.get("content") == null) {
          apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_FAIL));
          return apiMsgContentResponse;
        }
        apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_SUCCESS));
        apiMsgContentResponse.setMsgContent((String) map.get("content"));
      } else {
        apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_FAIL, "参数不正确"));
      }
      return apiMsgContentResponse;


    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_MSG_CONTENT_EXCEPTION));
    }
    return apiMsgContentResponse;
  }

  private CustomerRemindSms getCustomerRemindSms(Long id) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IContactService contactService = ServiceManager.getService(IContactService.class);
    RemindEventDTO remindEventDTO = txnService.getRemindEventById(id);
    Set<Long> customerIds = new HashSet<Long>();
    CustomerRemindSms customerRemindSms = new CustomerRemindSms();
    customerRemindSms.setLicenceNo(remindEventDTO.getLicenceNo());
    customerRemindSms.setName(remindEventDTO.getCustomerName());
    customerRemindSms.setYear(String.valueOf(DateUtil.getYear(remindEventDTO.getRemindTime())));
    customerRemindSms.setMonth(String.valueOf(DateUtil.getMonth(remindEventDTO.getRemindTime())));
    customerRemindSms.setDay(String.valueOf(DateUtil.getDay(remindEventDTO.getRemindTime())));

    Map<Long, List<ContactDTO>> customerContactDTOsMap = contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerIds), "customer");

    //保养里程
    if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.MAINTAIN_TIME) && NumberUtil.longValue(remindEventDTO.getRemindMileage()) > 0) {
      customerRemindSms.setType(UserConstant.MAINTAIN_MILEAGE.intValue());
    } else if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.MAINTAIN_TIME)) {
      customerRemindSms.setType(UserConstant.MAINTAIN_TIME.intValue());
    } else if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.EXAMINE_TIME)) {
      customerRemindSms.setType(UserConstant.EXAMINE_TIME.intValue());
    } else if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.INSURE_TIME)) {
      customerRemindSms.setType(UserConstant.INSURE_TIME.intValue());
    } else if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.BIRTH_TIME)) {
      customerRemindSms.setType(UserConstant.BIRTH_TIME.intValue());
    } else if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.APPOINT_SERVICE)) {
      customerRemindSms.setType(UserConstant.APPOINT_SERVICE.intValue());
    } else if (remindEventDTO.getEventStatus().equals(UserConstant.CustomerRemindType.MEMBER_SERVICE)) {
      customerRemindSms.setType(UserConstant.MEMBER_SERVICE.intValue());
    }
    ContactDTO contactDTO = SmsHelper.getFirstHasMobileContactDTO(customerContactDTOsMap.get(remindEventDTO.getCustomerId()));
    if (contactDTO != null) {
      customerRemindSms.setMobile(contactDTO.getMobile());
    }
    //客户预约提醒
    if (RemindEventType.CUSTOMER_SERVICE.toString().equals(remindEventDTO.getEventType())) {
      //自定义服务，需要从预约服务表读取
      if (UserConstant.CustomerRemindType.APPOINT_SERVICE.equals(remindEventDTO.getEventStatus())) {
        AppointServiceDTO appointServiceDTO = userService.getAppointServiceById(remindEventDTO.getAppointServiceId());
        if (appointServiceDTO != null) {
          customerRemindSms.setAppointName(appointServiceDTO.getAppointName());
        }
      } else {
        customerRemindSms.setAppointName(remindEventDTO.getEventStatus());
      }
    }
    //会员服务到期提醒
    else if (RemindEventType.MEMBER_SERVICE.toString().equals(remindEventDTO.getEventType())) {
      ServiceDTO serviceDTO = txnService.getServiceById(remindEventDTO.getServiceId());
      if (serviceDTO != null) {
        customerRemindSms.setAppointName(serviceDTO.getName());
      }
    }
    customerRemindSms.setShopId(remindEventDTO.getShopId());
    customerRemindSms = ServiceManager.getService(ISmsService.class).sendCustomerServiceRemindMessage(customerRemindSms);
    return customerRemindSms;
  }

  /**
   * 根据类型获取短信内容
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/sendMsg",
      method = RequestMethod.POST)
  public ApiResponse sendMsg(HttpServletRequest request, HttpServletResponse response, String type, String idStr) throws Exception {
    ApiMsgContentResponse apiMsgContentResponse = null;

    try {
      if (StringUtil.isEmpty(type) || StringUtil.isEmpty(idStr) || !NumberUtil.isLongNumber(idStr)) {
        apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_FAIL, "参数不正确"));
        return apiMsgContentResponse;
      }
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);


      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      if (type.equals("customerRemind")) {
        CustomerRemindSms customerRemindSms = this.getCustomerRemindSms(Long.valueOf(idStr));
        customerRemindSms.setSmsFlag(true);
        customerRemindSms.setAppFlag(false);

        if (customerRemindSms != null) {
          Result result = rfiTxnService.bcgogoAppSendMsg(customerRemindSms.getShopId(), userDTO.getId(), Long.valueOf(idStr), customerRemindSms);
          if (result != null && result.isSuccess()) {
            apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_SUCCESS));
          }
        }
      } else if (type.equals("faultInfo")) {
        IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
        FaultInfoToShopDTO faultInfoToShopDTO = shopFaultInfoService.getShopFaultInfo(Long.valueOf(idStr));
        if (faultInfoToShopDTO != null) {
          Map<String, Object> map = shopFaultInfoService.getSopFaultInfoMsgContent(faultInfoToShopDTO.getShopId(), faultInfoToShopDTO.getFaultCode(), faultInfoToShopDTO.getFaultCodeReportTimeStr(),
              faultInfoToShopDTO.getFaultAlertType(), faultInfoToShopDTO.getFaultAlertType().getValue(), faultInfoToShopDTO.getVehicleNo());
          if (MapUtils.isNotEmpty(map)) {
            ISendSmsService sendSmsService = ServiceManager.getService(ISendSmsService.class);
            Result result = sendSmsService.sendSms(faultInfoToShopDTO.getShopId(), userDTO.getId(), (String) map.get("content"), false, true, true, appUserNo, faultInfoToShopDTO.getMobile());
            if (result != null && result.isSuccess()) {
              apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_SUCCESS));
              ServiceManager.getService(IShopFaultInfoService.class).updateShopFaultInfo2SendMessage(faultInfoToShopDTO.getId());
            }
          }
        }
      } else {
        apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_FAIL, "参数不正确"));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_EXCEPTION));
    }
    if (apiMsgContentResponse == null) {
      apiMsgContentResponse = new ApiMsgContentResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_FAIL, "请重试"));
    }
    return apiMsgContentResponse;
  }

  /**
   * 处理事故故障为已发送短信、客户服务提醒为已提醒
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/remindHandle", method = RequestMethod.POST)
  public ApiResponse remindHandle(HttpServletRequest request, HttpServletResponse response, String type, String idStr) throws Exception {
    ApiResponse apiResponse = null;
    try {
      if (StringUtil.isEmpty(type) || StringUtil.isEmpty(idStr) || !NumberUtil.isLongNumber(idStr)) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_FAIL, "参数不正确"));
        return apiResponse;
      }
      if (type.equals("customerRemind")) {
        ITxnService txnService = ServiceManager.getService(ITxnService.class);
        RemindEventDTO remindEventDTO = txnService.getRemindEventById(Long.valueOf(idStr));
        if (remindEventDTO == null) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_FAIL, "请刷新列表后重试"));
          return apiResponse;
        }
        remindEventDTO.setRemindStatus(UserConstant.Status.REMINDED);
        txnService.updateRemindEvent(remindEventDTO);
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_SUCCESS));
        return apiResponse;
      } else if (type.equals("faultInfo")) {
        IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
        FaultInfoToShopDTO faultInfoToShopDTO = shopFaultInfoService.getShopFaultInfo(Long.valueOf(idStr));
        if (faultInfoToShopDTO == null) {
          apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_FAIL, "请刷新列表后重试"));
          return apiResponse;
        }
        shopFaultInfoService.updateShopFaultInfo2SendMessage(Long.valueOf(idStr));
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_SUCCESS));
        return apiResponse;
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_SEND_MSG_FAIL, "参数不正确"));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_EXCEPTION, "参数不正确"));
    }
    if (apiResponse == null) {
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_FAIL, "请刷新列表后重试"));
    }

    return apiResponse;
  }

  /**
   * 接受预约单
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/acceptAppoint", method = RequestMethod.POST)
  public ApiResponse acceptAppoint(HttpServletRequest request, HttpServletResponse response, String idStr) throws Exception {
    ApiResponse apiResponse = null;
    try {
      if (StringUtil.isEmpty(idStr) || !NumberUtil.isLongNumber(idStr)) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_ACCEPT_APPOINT_FAIL, "参数不正确"));
        return apiResponse;
      }

      String appUserNo = SessionUtil.getAppUserNo(request, response);
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);
      AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
      appointOrderDTO.setShopId(userDTO.getShopId());
      appointOrderDTO.setUserId(userDTO.getId());
      appointOrderDTO.setId(Long.valueOf(idStr));
      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      Result result = appointOrderService.validateAcceptAppointOrder(appointOrderDTO);
      if (result != null && result.isSuccess()) {
        appointOrderService.handleAcceptAppointOrder(appointOrderDTO);
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_ACCEPT_APPOINT_SUCCESS));
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_FAIL, "请刷新列表后重试"));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_EXCEPTION, "参数不正确"));
    }
    if (apiResponse == null) {
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_REMIND_HANDLE_FAIL, "请刷新列表后重试"));
    }

    return apiResponse;
  }


  /**
   * 更改服务时间并接受预约单
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/changeAppointTime", method = RequestMethod.POST)
  public ApiResponse changeAppointTime(HttpServletRequest request, HttpServletResponse response, String idStr, String appointTimeStr) throws Exception {
    ApiResponse apiResponse = null;
    try {
      if (StringUtil.isEmpty(idStr) || !NumberUtil.isLongNumber(idStr) || StringUtil.isEmpty(appointTimeStr)) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CHANGE_APPOINT_TIME_FAIL, "参数不正确"));
        return apiResponse;
      }

      Long appointTime = null;
      try {
        appointTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, appointTimeStr);
      } catch (ParseException e) {
        appointTime = null;
      }
      if (appointTime == null) {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CHANGE_APPOINT_TIME_FAIL, "时间格式不正确"));
        return apiResponse;
      }

      String appUserNo = SessionUtil.getAppUserNo(request, response);
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);
      AppointOrderDTO appointOrderDTO = new AppointOrderDTO();
      appointOrderDTO.setShopId(userDTO.getShopId());
      appointOrderDTO.setUserId(userDTO.getId());
      appointOrderDTO.setId(Long.valueOf(idStr));

      appointOrderDTO.setAppointTimeStr(appointTimeStr);
      appointOrderDTO.setAppointTime(appointTime);
      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      Result result = appointOrderService.validateAcceptAppointOrder(appointOrderDTO);
      if (result != null && result.isSuccess()) {
        appointOrderService.handleAcceptAppointOrder(appointOrderDTO);
        appointOrderService.updateAppointOrderTime(appointOrderDTO,appointTime);
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CHANGE_APPOINT_TIME_SUCCESS));
      } else {
        apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CHANGE_APPOINT_TIME_FAIL, "请刷新列表后重试"));
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CHANGE_APPOINT_TIME_EXCEPTION, "请刷新列表后重试"));
    }
    if (apiResponse == null) {
      apiResponse = new ApiResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CHANGE_APPOINT_TIME_FAIL, "请刷新列表后重试"));
    }

    return apiResponse;
  }


  /**
   * 手机端分页获取店铺宣传列表
   *
   * @param request
   * @param response
   * @param pageNo
   * @param pageSize
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/vehicleFaultInfoList/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse vehicleFaultInfoList(HttpServletRequest request, HttpServletResponse response, @PathVariable String pageNo, @PathVariable String pageSize) throws Exception {
    try {
      ApiVFaultInfoListResponse apiVFaultInfoListResponse = new ApiVFaultInfoListResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_VEHICLE_FAULT_LIST_SUCCESS));

      if (StringUtil.isEmptyAppGetParameter(pageNo) || !NumberUtil.isLongNumber(pageNo)) {
        pageNo = "1";
      }
      if (StringUtil.isEmptyAppGetParameter(pageSize) || !NumberUtil.isLongNumber(pageSize)) {
        pageSize = "5";
      }

      String appUserNo = SessionUtil.getAppUserNo(request, response);
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);

      IShopFaultInfoService shopFaultInfoService = ServiceManager.getService(IShopFaultInfoService.class);
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);


      FaultInfoSearchConditionDTO searchConditionDTO = new FaultInfoSearchConditionDTO();
      searchConditionDTO.setIsUntreated(YesNo.YES);
      searchConditionDTO.setShopId(userDTO.getShopId());
      searchConditionDTO.setStartPageNo(Integer.valueOf(pageNo));
      searchConditionDTO.setMaxRows(Integer.valueOf(pageSize));
      int totalRows = shopFaultInfoService.countShopFaultInfoList(searchConditionDTO);
      Pager pager = new Pager(totalRows, searchConditionDTO.getStartPageNo(), searchConditionDTO.getMaxRows());

      List<FaultInfoToShopDTO> faultInfoToShopDTOs = shopFaultInfoService.getFaultInfoListByCondition(searchConditionDTO);
      apiVFaultInfoListResponse.setPager(pager);
      apiVFaultInfoListResponse.setFaultInfoToShopDTOList(faultInfoToShopDTOs);
      if (CollectionUtil.isEmpty(faultInfoToShopDTOs)) {
        return apiVFaultInfoListResponse;

      }

      Set<String> vehicleNos = new HashSet<String>();

      for (FaultInfoToShopDTO dto : faultInfoToShopDTOs) {
        if (StringUtils.isNotEmpty(dto.getVehicleNo())) {
          vehicleNos.add(dto.getVehicleNo());
        }
      }
      Map<String, VehicleDTO> vehicleDTOMap = vehicleService.getVehicleDTOMapByLicenceNo(userDTO.getShopId(), vehicleNos);

      Set<Long> toGetCustomerVehicleIds = new HashSet<Long>();
      for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
        VehicleDTO vehicleDTO = vehicleDTOMap.get(faultInfoToShopDTO.getVehicleNo());
        if (vehicleDTO != null) {
          faultInfoToShopDTO.setVehicleId(vehicleDTO.getId());
          toGetCustomerVehicleIds.add(vehicleDTO.getId());
        }
      }

      Map<Long, CustomerDTO> vehicleIdCustomerMap = userService.getVehicleIdCustomerMapByVehicleIds(userDTO.getShopId(), toGetCustomerVehicleIds);

      for (FaultInfoToShopDTO faultInfoToShopDTO : faultInfoToShopDTOs) {
        if (faultInfoToShopDTO != null) {
          if (faultInfoToShopDTO.getVehicleId() != null) {
            CustomerDTO customerDTO = vehicleIdCustomerMap.get(faultInfoToShopDTO.getVehicleId());
            if (customerDTO != null) {
              faultInfoToShopDTO.fromCustomerDTO(customerDTO);
            }
          }
        }
      }
      return apiVFaultInfoListResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.BCGOGO_VEHICLE_FAULT_LIST_EXCEPTION);
    }
  }


  /**
   * 手机端分页获取店铺宣传列表
   *
   * @param request
   * @param response
   * @param pageNo
   * @param pageSize
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/customerRemindList/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse customerRemindList(HttpServletRequest request, HttpServletResponse response, @PathVariable String pageNo, @PathVariable String pageSize) throws Exception {
    try {
      ApiCRemindListResponse apiCRemindListResponse = new ApiCRemindListResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_CUSTOMER_REMIND_LIST_SUCCESS));

      if (StringUtil.isEmptyAppGetParameter(pageNo) || !NumberUtil.isLongNumber(pageNo)) {
        pageNo = "1";
      }
      if (StringUtil.isEmptyAppGetParameter(pageSize) || !NumberUtil.isLongNumber(pageSize)) {
        pageSize = "5";
      }

      String appUserNo = SessionUtil.getAppUserNo(request, response);
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);

      RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
      IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
      IContactService contactService = ServiceManager.getService(IContactService.class);
      int totalRows = rfiTxnService.countRemindMileageCustomerRemind(userDTO.getShopId());
      Pager pager = new Pager(totalRows, Integer.valueOf(pageNo), Integer.valueOf(pageSize));

      List<RemindEventDTO> remindEventDTOList = rfiTxnService.getRemindMileageCustomerRemind(userDTO.getShopId(), pager);

      List<CustomerServiceJobDTO> customerServiceJobDTOs = new ArrayList<CustomerServiceJobDTO>();

      if (CollectionUtil.isNotEmpty(remindEventDTOList)) {
        Set<String> licenceNoSet = new HashSet<String>();
        Set<Long> customerIds = new HashSet<Long>();

        for (RemindEventDTO remindEventDTO : remindEventDTOList) {
          if (StringUtil.isNotEmpty(remindEventDTO.getLicenceNo())) {
            licenceNoSet.add(remindEventDTO.getLicenceNo());
          }
          if (remindEventDTO.getCustomerId() != null) {
            customerIds.add(remindEventDTO.getCustomerId());
          }
        }
        Map<String, VehicleDTO> vehicleDTOMap = vehicleService.getVehicleDTOMapByLicenceNo(userDTO.getShopId(), licenceNoSet);
        Map<Long,List<ContactDTO>> customerContactDTOsMap = contactService.getContactsByCustomerOrSupplierIds(new ArrayList<Long>(customerIds), "customer");

        for (RemindEventDTO remindEventDTO : remindEventDTOList) {
          if (StringUtil.isNotEmpty(remindEventDTO.getLicenceNo())) {
            VehicleDTO vehicleDTO = vehicleDTOMap.get(remindEventDTO.getLicenceNo());
            if (vehicleDTO == null) {
              continue;
            }
            CustomerServiceJobDTO customerServiceJobDTO = new CustomerServiceJobDTO();
            customerServiceJobDTO.setRemindMileage(remindEventDTO.getRemindMileage());
            customerServiceJobDTO.setCustomerName(remindEventDTO.getCustomerName());
            customerServiceJobDTO.setLicenceNo(remindEventDTO.getLicenceNo());
            customerServiceJobDTO.setId(remindEventDTO.getId());
            customerServiceJobDTO.setCurrentMileage(NumberUtil.compareDouble(vehicleDTO.getStartMileage(), vehicleDTO.getObdMileage()) ? vehicleDTO.getStartMileage() : vehicleDTO.getObdMileage());
            customerServiceJobDTOs.add(customerServiceJobDTO);

            if (remindEventDTO.getCustomerId() != null) {
              ContactDTO contactDTO = SmsHelper.getFirstHasMobileContactDTO(customerContactDTOsMap.get(remindEventDTO.getCustomerId()));
              if (contactDTO != null) {
                customerServiceJobDTO.setMobile(contactDTO.getMobile());
                customerServiceJobDTO.setContact(contactDTO.getName());
                customerServiceJobDTO.setContactId(contactDTO.getId());
              }
            }

            if (StringUtil.isEmpty(customerServiceJobDTO.getMobile())) {
              customerServiceJobDTO.setMobile(vehicleDTO.getMobile());
            }
          }
        }

      }
      apiCRemindListResponse.setPager(pager);
      apiCRemindListResponse.setServiceJobDTOList(customerServiceJobDTOs);
      return apiCRemindListResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.BCGOGO_VEHICLE_FAULT_LIST_EXCEPTION);
    }
  }


  /**
   * 手机端分页获取预约列表
   *
   * @param request
   * @param response
   * @param pageNo
   * @param pageSize
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/bcgogoApp/appointOrderList/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse appointOrderList(HttpServletRequest request, HttpServletResponse response, @PathVariable String pageNo, @PathVariable String pageSize) throws Exception {
    try {
      ApiAppointListResponse apiAppointListResponse = new ApiAppointListResponse(MessageCode.toApiResponse(MessageCode.BCGOGO_APPOINT_ORDER_LIST_SUCCESS));

      if (StringUtil.isEmptyAppGetParameter(pageNo) || !NumberUtil.isLongNumber(pageNo)) {
        pageNo = "1";
      }
      if (StringUtil.isEmptyAppGetParameter(pageSize) || !NumberUtil.isLongNumber(pageSize)) {
        pageSize = "5";
      }


      String appUserNo = SessionUtil.getAppUserNo(request, response);
      UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserInfo(appUserNo);

      AppointOrderSearchCondition condition = new AppointOrderSearchCondition();
      condition.setShopId(userDTO.getShopId());
      condition.setStartPageNo(Integer.valueOf(pageNo));
      condition.setMaxRows(Integer.valueOf(pageSize));
      AppointOrderStatus[] status = new AppointOrderStatus[1];
      status[0] = AppointOrderStatus.PENDING;
      condition.setAppointOrderStatus(status);

      IAppointOrderService appointOrderService = ServiceManager.getService(IAppointOrderService.class);
      int totalRows = appointOrderService.countAppointOrderDTOs(condition);
      List<AppointOrderDTO> appointOrderDTOs = appointOrderService.searchAppointOrderDTOs(condition);
      Pager pager = new Pager(totalRows, condition.getStartPageNo(), condition.getMaxRows());

      apiAppointListResponse.setPager(pager);
      apiAppointListResponse.setAppointOrderDTOList(appointOrderDTOs);
      return apiAppointListResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.BCGOGO_APPOINT_ORDER_LIST_EXCEPTION);
    }
  }


}
