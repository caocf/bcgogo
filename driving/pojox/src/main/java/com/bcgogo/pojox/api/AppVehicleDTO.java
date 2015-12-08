package com.bcgogo.pojox.api;

import com.bcgogo.pojox.enums.AppUserStatus;
import com.bcgogo.pojox.enums.YesNo;
import com.bcgogo.pojox.util.DateUtil;
import com.bcgogo.pojox.util.StringUtil;

import java.io.Serializable;

/**
 * 手机端车辆相关信息
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 上午11:10
 */
public class AppVehicleDTO implements Serializable {
  private Long createdTime;
  private Long appUserId;
  private Long vehicleId;
  private String mobile;            //车辆手机号码
  private String email;                  //电子邮件
  private String contact;                //车辆联系人
  private AppUserStatus status;                 //车辆状态
  private String vehicleNo;              //车牌号
  private String vehicleModel;           //车型
  private Long vehicleModelId;         //车型id
  private String vehicleBrand;           //车辆品牌
  private Long vehicleBrandId;         //车辆品牌id
  private Double nextMaintainMileage;    //下次保养里程数
  private Long nextMaintainTime;       //下次保养时间
  private String nextMaintainTimeStr;       //下次保养时间
  private Long nextExamineTime;        //下次验车时间
  private String nextExamineTimeStr;       //下次保养时间
  private Long nextInsuranceTime;      //下次保险时间
  private String nextInsuranceTimeStr;       //下次保养时间
  private Double currentMileage;  //当前里程数
  private Long currentMileageLastUpdateTime;//当前里程最后更新时间
  private Double lastObdMileage;//OBD上报的上一次里程
  private Double oilWear;                //油耗
  private Long reportTime;             //报告时间

  private String obdSN;//当前车辆所安装的obd的唯一标识号
  private String userNo;//用户账号

  //增加车况信息统计项
  private String instantOilWear;//瞬时油耗 单位 ml/s
  private String oilWearPerHundred;//百公里油耗 l/100km
  private String oilMass;//油量 百分比%
  private String engineCoolantTemperature;//发动机水温 发动机冷却剂   单位 ℃
  private String batteryVoltage;//电瓶电压 单位 V
  private YesNo isDefault;
  private Long recommendShopId;//推荐店铺id
  private String recommendShopName;
  private Long bindingShopId;
  private Long orgBindingShopId;

  private String vehicleVin;//手机端车辆vin码
  private String registNo; //登记证书号
  private String engineNo; //发动机编号


  private String coordinateLat;//车辆纬度信息
  private String coordinateLon;//车辆经度信息

  private Double maintainPeriod;//保养周期
  private Double lastMaintainMileage;//上次保养里程数
  private Double oilPrice;//油价 用以Gsm卡用户更新用户油价

  private Double worstOilWear;  //最差平均油耗
  private Double bestOilWear;   //最佳平均油耗
  private Double avgOilWear;   //全部平均油耗

  private String juheCityName;//该车牌号手机端查询设置的聚合城市名称
  private String juheCityCode;//该车牌号手机端查询设置的聚合城市编码

  private Long insuranceCompanyId; //保险公司id
  private String insuranceCompanyName; //保险公司名字
  private String shopName;//店铺名称
  private String imei;
  private String gasoline_price;//当前油价
  private String appUserNo;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getGasoline_price() {
    return gasoline_price;
  }

  public void setGasoline_price(String gasoline_price) {
    this.gasoline_price = gasoline_price;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public YesNo getDefault() {
    return isDefault;
  }

  public void setDefault(YesNo aDefault) {
    isDefault = aDefault;
  }

  public String getInsuranceCompanyName() {
    return insuranceCompanyName;
  }

  public void setInsuranceCompanyName(String insuranceCompanyName) {
    this.insuranceCompanyName = insuranceCompanyName;
  }

  public Long getInsuranceCompanyId() {
    return insuranceCompanyId;
  }

  public void setInsuranceCompanyId(Long insuranceCompanyId) {
    this.insuranceCompanyId = insuranceCompanyId;
  }

  public AppVehicleDTO() {
  }

  public AppVehicleDTO(Long id, String vehicleNo, String vehicleModel, Long vehicleModelId, String vehicleBrand, Long vehicleBrandId) {
    this.vehicleId = id;
    this.vehicleNo = vehicleNo;
    this.vehicleModel = vehicleModel;
    this.vehicleModelId = vehicleModelId;
    this.vehicleBrand = vehicleBrand;
    this.vehicleBrandId = vehicleBrandId;
  }


  @Override
  public String toString() {
    return "AppVehicleDTO{" +
      "vehicleId=" + vehicleId +
      ", vehicleVin='" + vehicleVin + '\'' +
      ", mobile='" + mobile + '\'' +
      ", email='" + email + '\'' +
      ", contact='" + contact + '\'' +
      ", status=" + status +
      ", vehicleNo='" + vehicleNo + '\'' +
      ", vehicleModel='" + vehicleModel + '\'' +
      ", vehicleModelId=" + vehicleModelId +
      ", vehicleBrand='" + vehicleBrand + '\'' +
      ", vehicleBrandId=" + vehicleBrandId +
      ", nextMaintainMileage=" + nextMaintainMileage +
      ", nextMaintainTime=" + nextMaintainTime +
      ", nextExamineTime=" + nextExamineTime +
      ", nextInsuranceTime=" + nextInsuranceTime +
      ", currentMileage=" + currentMileage +
      ", oilWear=" + oilWear +
      ", reportTime=" + reportTime +
      ", engineNo='" + engineNo + '\'' +
      ", obdSN='" + obdSN + '\'' +
      ", userNo='" + userNo + '\'' +
      ", instantOilWear='" + instantOilWear + '\'' +
      ", oilWearPerHundred='" + oilWearPerHundred + '\'' +
      ", oilMass='" + oilMass + '\'' +
      ", engineCoolantTemperature='" + engineCoolantTemperature + '\'' +
      ", batteryVoltage='" + batteryVoltage + '\'' +
      '}';
  }

  public Long getAppUserId() {
    return appUserId;
  }

  public void setAppUserId(Long appUserId) {
    this.appUserId = appUserId;
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public boolean isFail(String result) {
    return !isSuccess(result);
  }

  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public AppUserStatus getStatus() {
    return status;
  }

  public void setStatus(AppUserStatus status) {
    this.status = status;
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

  public Double getNextMaintainMileage() {
    return nextMaintainMileage;
  }

  public void setNextMaintainMileage(Double nextMaintainMileage) {
    this.nextMaintainMileage = nextMaintainMileage;
  }

  public Long getNextMaintainTime() {
    return nextMaintainTime;
  }

  public void setNextMaintainTime(Long nextMaintainTime) {
    this.nextMaintainTime = nextMaintainTime;
    if (nextMaintainTime != null) {
      setNextMaintainTimeStr(DateUtil.convertDateLongToString(nextMaintainTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getNextMaintainTimeStr() {
    return nextMaintainTimeStr;
  }

  public void setNextMaintainTimeStr(String nextMaintainTimeStr) {
    this.nextMaintainTimeStr = nextMaintainTimeStr;
  }

  public Long getNextExamineTime() {
    return nextExamineTime;
  }

  public void setNextExamineTime(Long nextExamineTime) {
    this.nextExamineTime = nextExamineTime;
    if (this.nextExamineTime != null) {
      setNextExamineTimeStr(DateUtil.convertDateLongToString(this.nextExamineTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getNextExamineTimeStr() {
    return nextExamineTimeStr;
  }

  public void setNextExamineTimeStr(String nextExamineTimeStr) {
    this.nextExamineTimeStr = nextExamineTimeStr;
  }

  public String getNextInsuranceTimeStr() {
    return nextInsuranceTimeStr;
  }

  public void setNextInsuranceTimeStr(String nextInsuranceTimeStr) {
    this.nextInsuranceTimeStr = nextInsuranceTimeStr;
  }

  public Long getNextInsuranceTime() {
    return nextInsuranceTime;
  }

  public void setNextInsuranceTime(Long nextInsuranceTime) {
    this.nextInsuranceTime = nextInsuranceTime;
    if (this.nextInsuranceTime != null) {
      setNextInsuranceTimeStr(DateUtil.convertDateLongToString(this.nextInsuranceTime, DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  public Long getReportTime() {
    return reportTime;
  }

  public void setReportTime(Long reportTime) {
    this.reportTime = reportTime;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getObdSN() {
    return obdSN;
  }

  public void setObdSN(String obdSN) {
    this.obdSN = obdSN;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  public String getInstantOilWear() {
    return instantOilWear;
  }

  public void setInstantOilWear(String instantOilWear) {
    this.instantOilWear = instantOilWear;
  }

  public String getOilWearPerHundred() {
    return oilWearPerHundred;
  }

  public void setOilWearPerHundred(String oilWearPerHundred) {
    this.oilWearPerHundred = oilWearPerHundred;
  }

  public String getOilMass() {
    return oilMass;
  }

  public void setOilMass(String oilMass) {
    this.oilMass = oilMass;
  }

  public String getEngineCoolantTemperature() {
    return engineCoolantTemperature;
  }

  public void setEngineCoolantTemperature(String engineCoolantTemperature) {
    this.engineCoolantTemperature = engineCoolantTemperature;
  }

  public String getBatteryVoltage() {
    return batteryVoltage;
  }

  public void setBatteryVoltage(String batteryVoltage) {
    this.batteryVoltage = batteryVoltage;
  }

  public YesNo getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(YesNo isDefault) {
    this.isDefault = isDefault;
  }

  public void setAppUserDTO(AppUserDTO dto) {
    if (dto != null) {
      this.setContact(dto.getName());
      this.setMobile(dto.getMobile());
    }
  }

  public Long getRecommendShopId() {
    return recommendShopId;
  }

  public void setRecommendShopId(Long recommendShopId) {
    this.recommendShopId = recommendShopId;
  }

  public String getRecommendShopName() {
    return recommendShopName;
  }

  public void setRecommendShopName(String recommendShopName) {
    this.recommendShopName = recommendShopName;
  }

  public String getRegistNo() {
    return registNo;
  }

  public void setRegistNo(String registNo) {
    this.registNo = registNo;
  }

  public Long getBindingShopId() {
    return bindingShopId;
  }

  public void setBindingShopId(Long bindingShopId) {
    this.bindingShopId = bindingShopId;
  }

  public Long getOrgBindingShopId() {
    return orgBindingShopId;
  }

  public void setOrgBindingShopId(Long orgBindingShopId) {
    this.orgBindingShopId = orgBindingShopId;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public Long getCurrentMileageLastUpdateTime() {
    return currentMileageLastUpdateTime;
  }

  public void setCurrentMileageLastUpdateTime(Long currentMileageLastUpdateTime) {
    this.currentMileageLastUpdateTime = currentMileageLastUpdateTime;
  }

  public String getCoordinateLat() {
    return coordinateLat;
  }

  public void setCoordinateLat(String coordinateLat) {
    this.coordinateLat = coordinateLat;
  }

  public String getCoordinateLon() {
    return coordinateLon;
  }

  public void setCoordinateLon(String coordinateLon) {
    this.coordinateLon = coordinateLon;
  }

  public Double getMaintainPeriod() {
    return maintainPeriod;
  }

  public void setMaintainPeriod(Double maintainPeriod) {
    this.maintainPeriod = maintainPeriod;
  }

  public Double getLastMaintainMileage() {
    return lastMaintainMileage;
  }

  public void setLastMaintainMileage(Double lastMaintainMileage) {
    this.lastMaintainMileage = lastMaintainMileage;
  }

  public Double getOilPrice() {
    return oilPrice;
  }

  public void setOilPrice(Double oilPrice) {
    this.oilPrice = oilPrice;
  }

  public Double getWorstOilWear() {
    return worstOilWear;
  }

  public void setWorstOilWear(Double worstOilWear) {
    this.worstOilWear = worstOilWear;
  }

  public Double getBestOilWear() {
    return bestOilWear;
  }

  public void setBestOilWear(Double bestOilWear) {
    this.bestOilWear = bestOilWear;
  }

  public Double getAvgOilWear() {
    return avgOilWear;
  }

  public void setAvgOilWear(Double avgOilWear) {
    this.avgOilWear = avgOilWear;
  }

  public Double getLastObdMileage() {
    return lastObdMileage;
  }

  public void setLastObdMileage(Double lastObdMileage) {
    this.lastObdMileage = lastObdMileage;
  }

  public String getJuheCityName() {
    return juheCityName;
  }

  public void setJuheCityName(String juheCityName) {
    this.juheCityName = juheCityName;
  }

  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }
}
