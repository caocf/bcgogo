package com.bcgogo.txn.dto.pushMessage.enquiry;


import com.bcgogo.api.AppUserDTO;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.api.ObdDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.VehicleDTO;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-3
 * Time: 上午9:05
 */
public class VehicleFaultParameter {
  private String appUserNo;
  private String mobile;
  private String appUserName;
  private String vehicleNo;
  private String faultCode;
  private String description;
  private Long targetShopId;
  private Long vehicleFaultInfoId;
  private Long faultInfoToShopId;

  public VehicleFaultParameter(String appUserNo, String appUserName,
                               String vehicleNo, String faultCode, String description,
                               Long targetShopId, Long vehicleFaultInfoId, String mobile,Long faultInfoToShopId) {
    this.appUserNo = appUserNo;
    this.appUserName = appUserName;
    this.vehicleNo = vehicleNo;
    this.faultCode = faultCode;
    this.description = description;
    this.targetShopId = targetShopId;
    this.vehicleFaultInfoId = vehicleFaultInfoId;
    this.mobile = mobile;
    this.faultInfoToShopId = faultInfoToShopId;
  }

  public VehicleFaultParameter(AppUserDTO appUserDTO, AppVehicleFaultInfoDTO appVehicleFaultInfoDTO, Long targetShopId,
                               AppVehicleDTO appVehicleDTO, FaultInfoToShopDTO faultInfoToShopDTO) {
    this(appUserDTO.getUserNo(),
        appUserDTO.getName(),
        appVehicleDTO.getVehicleNo(),
        appVehicleFaultInfoDTO.getErrorCode(),
        appVehicleFaultInfoDTO.getContent(),
        targetShopId,
        appVehicleFaultInfoDTO.getId(),
        appUserDTO.getMobile(),faultInfoToShopDTO.getId());

  }

  public VehicleFaultParameter(VehicleDTO vehicleDTO,CustomerDTO customerDTO, FaultInfoToShopDTO faultInfoToShopDTO) {
    if(vehicleDTO !=null){
      this.vehicleNo = vehicleDTO.getLicenceNo();
      this.targetShopId = vehicleDTO.getShopId();
    }

    if(customerDTO != null){
      this.appUserName = customerDTO.getName();
      this.mobile = customerDTO.getMobile();
    }

    if(faultInfoToShopDTO != null){
      this.faultCode = faultInfoToShopDTO.getFaultCode();
      this.description = faultInfoToShopDTO.getFaultCodeDescription();
      this.faultInfoToShopId = faultInfoToShopDTO.getId();
    }

  }

  public String validate() {
//    if (mobile == null) return "mobile is null";
//    if (appUserNo == null) return "appUserNo is null";
//    if (appUserName == null) return "name is null";
    if (vehicleNo == null) return "vehicleNo is null";
    if (faultCode == null) return "faultCode is null";
    if (targetShopId == null) return "targetShopId is null";
//    if (vehicleFaultInfoId == null) return "vehicleFaultInfoId is null";
    if(faultInfoToShopId == null) return "faultInfoToShopId is null";
    return "";
  }

  public Long getVehicleFaultInfoId() {
    return vehicleFaultInfoId;
  }

  public void setVehicleFaultInfoId(Long vehicleFaultInfoId) {
    this.vehicleFaultInfoId = vehicleFaultInfoId;
  }

  public String getAppUserName() {
    return appUserName;
  }

  public void setAppUserName(String appUserName) {
    this.appUserName = appUserName;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getTargetShopId() {
    return targetShopId;
  }

  public void setTargetShopId(Long targetShopId) {
    this.targetShopId = targetShopId;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Long getFaultInfoToShopId() {
    return faultInfoToShopId;
  }

  public void setFaultInfoToShopId(Long faultInfoToShopId) {
    this.faultInfoToShopId = faultInfoToShopId;
  }
}
