package com.bcgogo.txn.dto.pushMessage.faultCode;

import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-8
 * Time: 下午5:17
 */
public class FaultInfoToShopDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long appVehicleFaultInfoId;
  private Long faultCodeInfoId;
  private String faultCode;
  private String faultCodeCategory;
  private String faultCodeDescription;

  private String appUserNo;
  private Long appVehicleId;
  private String vehicleNo;
  private String vehicleBrand;
  private String vehicleModel;
  private String mobile;

  private String customerName;
  private String customerMobile;
  private Long customerId;
  private String customerIdStr;
  private Long vehicleId;
  private String vehicleIdStr;

  private Long faultCodeReportTime;
  private String faultCodeReportTimeStr;

  private YesNo isSendMessage;
  private YesNo isCreateAppointOrder;
  private Status status;

  private Long appointOrderId;
  private String appointOrderIdStr;
  private FaultAlertType faultAlertType;
  private String faultAlertTypeValue;
  private String lon; //经度
  private String lat; //纬度



  public void fromCustomerDTO(CustomerDTO customerDTO) {
    if (customerDTO != null) {
      this.setCustomerId(customerDTO.getId());
      this.setCustomerName(customerDTO.getName());
      this.setCustomerMobile(customerDTO.getMobile());
//      if (ArrayUtils.isNotEmpty(customerDTO.getContacts())) {
//        setCustomerMobile(customerDTO.getContactDTOList().get(0).getMobile());
//      }
    }
  }

  public void fromAppVehicleFaultInfoDTO(AppVehicleFaultInfoDTO appVehicleFaultInfoDTO) {
    if (appVehicleFaultInfoDTO != null) {
      setAppUserNo(appVehicleFaultInfoDTO.getAppUserNo());
      setAppVehicleId(appVehicleFaultInfoDTO.getAppVehicleId());
      setAppVehicleFaultInfoId(appVehicleFaultInfoDTO.getId());
      setFaultCodeReportTime(appVehicleFaultInfoDTO.getReportTime());
      setFaultCode(appVehicleFaultInfoDTO.getErrorCode());
      setFaultCodeDescription(appVehicleFaultInfoDTO.getContent());
      setFaultCodeCategory(appVehicleFaultInfoDTO.getCategory());
    }
  }

  public Long getAppVehicleFaultInfoId() {
    return appVehicleFaultInfoId;
  }

  public void setAppVehicleFaultInfoId(Long appVehicleFaultInfoId) {
    this.appVehicleFaultInfoId = appVehicleFaultInfoId;
  }

  public Long getFaultCodeInfoId() {
    return faultCodeInfoId;
  }

  public void setFaultCodeInfoId(Long faultCodeInfoId) {
    this.faultCodeInfoId = faultCodeInfoId;
  }

  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  public String getFaultCodeCategory() {
    return faultCodeCategory;
  }

  public void setFaultCodeCategory(String faultCodeCategory) {
    this.faultCodeCategory = faultCodeCategory;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
    if(vehicleId != null){
      setVehicleIdStr(vehicleId.toString());
    }else{
      this.setVehicleIdStr("");
    }
  }

  public String getVehicleIdStr() {
    return vehicleIdStr;
  }

  public void setVehicleIdStr(String vehicleIdStr) {
    this.vehicleIdStr = vehicleIdStr;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
    if (customerId != null) {
      this.setCustomerIdStr(customerId.toString());
    }else{
      this.setCustomerIdStr("");
    }
  }

  public Long getFaultCodeReportTime() {
    return faultCodeReportTime;
  }

  public void setFaultCodeReportTime(Long faultCodeReportTime) {
    this.faultCodeReportTime = faultCodeReportTime;
    this.setFaultCodeReportTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, faultCodeReportTime));
  }

  public String getFaultCodeReportTimeStr() {
    return faultCodeReportTimeStr;
  }

  public void setFaultCodeReportTimeStr(String faultCodeReportTimeStr) {
    this.faultCodeReportTimeStr = faultCodeReportTimeStr;
  }

  public YesNo getIsSendMessage() {
    return isSendMessage;
  }

  public void setIsSendMessage(YesNo isSendMessage) {
    this.isSendMessage = isSendMessage;
  }

  public YesNo getIsCreateAppointOrder() {
    return isCreateAppointOrder;
  }

  public void setIsCreateAppointOrder(YesNo isCreateAppointOrder) {
    this.isCreateAppointOrder = isCreateAppointOrder;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
    if(appointOrderId!= null){
      setAppointOrderIdStr(appointOrderId.toString());
    } else{
      setAppointOrderIdStr("");
    }
  }

  public String getAppointOrderIdStr() {
    return appointOrderIdStr;
  }

  public void setAppointOrderIdStr(String appointOrderIdStr) {
    this.appointOrderIdStr = appointOrderIdStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null) {
      this.setIdStr(id.toString());
    }
  }

  public String getFaultCodeDescription() {
    return faultCodeDescription;
  }

  public void setFaultCodeDescription(String faultCodeDescription) {
    this.faultCodeDescription = faultCodeDescription;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public FaultAlertType getFaultAlertType() {
    return faultAlertType;
  }

  public void setFaultAlertType(FaultAlertType faultAlertType) {
    this.faultAlertType = faultAlertType;
      if(faultAlertType!=null)
          setFaultAlertTypeValue(faultAlertType.getValue());
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

    public String getFaultAlertTypeValue() {
        return faultAlertTypeValue;
    }

    public void setFaultAlertTypeValue(String faultAlertTypeValue) {
        this.faultAlertTypeValue = faultAlertTypeValue;
    }
}
