package com.bcgogo.api;

import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.user.Status;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;

import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午4:19
 */
public class OBDBindingDTO {
  private Long appUserId;
  private String userNo;     //用户账号*
  private String obdSN;        //obd硬件唯一标识号*
  private String vehicleVin;
  private String engineNo;//发动机编号
  private String registNo; //登记证书号
  private Long vehicleId;
  private String vehicleNo;//车牌号
  private String vehicleModel;//车型
  private Long vehicleModelId;//车型ID
  private String vehicleBrand;//车辆品牌
  private Long vehicleBrandId;//车辆品牌ID
  private Double nextMaintainMileage;  //下次保养里程数
  private Long nextInsuranceTime;
  private Long nextExamineTime;  //下次验车时间
  private Double currentMileage;  //当前里程数
  private Long sellShopId;  //obd销售店铺的id
  private Set<String> updatedVehicleNoSet;

  public OBDBindingDTO() {
    super();
  }

  public String validate() {
    if (StringUtil.isEmpty(userNo)) {
      return ValidateMsg.APP_USER_NO_EMPTY.getValue();
    }
    if (StringUtil.isEmpty(obdSN)) {
      return ValidateMsg.OBD_SN_EMPTY.getValue();
    }
    if(StringUtil.isEmpty(vehicleNo)){
      return ValidateMsg.VEHICLE_NO_EMPTY.getValue();
    }
    if (!RegexUtils.isVehicleNo(vehicleNo)) {
      return ValidateMsg.VEHICLE_NO_ILLEGAL.getValue();
    }
//    if (vehicleVin == null) {
//      return ValidateMsg.OBD_VEHICLE_VIN_EMPTY.getValue();
//    }
    if (vehicleModel == null && vehicleModelId == null) {
      return ValidateMsg.APP_VEHICLE_MODEL_NOT_EMPTY.getValue();
    }
    if (vehicleBrand == null && vehicleBrandId == null) {
      return ValidateMsg.APP_VEHICLE_BRAND_NOT_EMPTY.getValue();
    }
    return "";
  }

  public void filter() {
    if (StringUtil.isNotEmpty(vehicleVin)) {
      if (vehicleVin.equals("null") || vehicleVin.equals("NULL")) {
        setVehicleVin("");
      }
    }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      if (vehicleNo.equals("null") || vehicleNo.equals("NULL")) {
        setVehicleNo("");
      }
    }
  }

  public AppVehicleDTO toAppVehicleDTO() {
    AppVehicleDTO dto = new AppVehicleDTO();
    dto.setVehicleVin(getVehicleVin());
    dto.setVehicleBrand(getVehicleBrand());
    dto.setVehicleBrandId(getVehicleBrandId());
    dto.setVehicleModelId(getVehicleModelId());
    dto.setVehicleModel(getVehicleModel());
    dto.setVehicleNo(getVehicleNo());
    dto.setUserNo(getUserNo());
    dto.setNextInsuranceTime(getNextInsuranceTime());
    dto.setNextMaintainMileage(getNextMaintainMileage());
    dto.setNextExamineTime(getNextExamineTime());
    dto.setCurrentMileage(getCurrentMileage());
    dto.setStatus(Status.active);
    dto.setRegistNo(getRegistNo());
    dto.setEngineNo(getEngineNo());
    return dto;
  }

  public Set<String> getUpdatedVehicleNoSet() {
    return updatedVehicleNoSet;
  }

  public void setUpdatedVehicleNoSet(Set<String> updatedVehicleNoSet) {
    this.updatedVehicleNoSet = updatedVehicleNoSet;
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getObdSN() {
    return obdSN;
  }

  public void setObdSN(String obdSN) {
    this.obdSN = obdSN;
  }

  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public Double getNextMaintainMileage() {
    return nextMaintainMileage;
  }

  public void setNextMaintainMileage(Double nextMaintainMileage) {
    this.nextMaintainMileage = nextMaintainMileage;
  }

  public Long getNextInsuranceTime() {
    return nextInsuranceTime;
  }

  public void setNextInsuranceTime(Long nextInsuranceTime) {
    this.nextInsuranceTime = nextInsuranceTime;
  }

  public Long getNextExamineTime() {
    return nextExamineTime;
  }

  public void setNextExamineTime(Long nextExamineTime) {
    this.nextExamineTime = nextExamineTime;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  public Long getSellShopId() {
    return sellShopId;
  }

  public void setSellShopId(Long sellShopId) {
    this.sellShopId = sellShopId;
  }

  public Long getAppUserId() {
    return appUserId;
  }

  public void setAppUserId(Long appUserId) {
    this.appUserId = appUserId;
  }


  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getRegistNo() {
    return registNo;
  }

  public void setRegistNo(String registNo) {
    this.registNo = registNo;
  }

  @Override
  public String toString() {
    return "OBDBindingDTO{" +
        "userNo='" + userNo + '\'' +
        ", obdSN='" + obdSN + '\'' +
        ", vehicleVin='" + vehicleVin + '\'' +
        ", registNo='" + registNo + '\'' +
        ", engineNo='" + engineNo + '\'' +
        ", vehicleId=" + vehicleId +
        ", vehicleNo='" + vehicleNo + '\'' +
        ", vehicleModel='" + vehicleModel + '\'' +
        ", vehicleModelId=" + vehicleModelId +
        ", vehicleBrand='" + vehicleBrand + '\'' +
        ", vehicleBrandId=" + vehicleBrandId +
        ", nextMaintainMileage=" + nextMaintainMileage +
        ", nextInsuranceTime=" + nextInsuranceTime +
        ", nextExamineTime=" + nextExamineTime +
        ", currentMileage=" + currentMileage +
        ", sellShopId=" + sellShopId +
        '}';
  }

}
