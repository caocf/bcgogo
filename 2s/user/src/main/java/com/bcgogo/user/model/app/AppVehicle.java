package com.bcgogo.user.model.app;

import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.OBDBindingDTO;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "app_vehicle")
public class  AppVehicle extends LongIdentifier {
  private String vehicleVin;//手机端车辆vin码  就是车架号
  private String mobile;//车辆手机号码
  private String contact;//车辆联系人
  private String appUserNo;//app用户账号
  private Status status;                 //车辆状态
  private String vehicleNo;//车牌号
  private String vehicleModel;//车型
  private Long vehicleModelId;//车型id
  private String vehicleBrand;//车辆品牌
  private Long vehicleBrandId;//车辆品牌id
  private Double nextMaintainMileage;//下次保养里程数
  private Integer nextMaintainMileagePushMessageRemindTimes = 0;//下次保养里程数 消息 提醒次数
  private Long nextMaintainTime;//下次保养时间
  private Long nextExamineTime;//下次验车时间
  private Long nextInsuranceTime;//下次保险时间
  private Long reportTime;//报告时间
  private String engineNo;//发动机编号
  private String registNo; //登记证书号

  //车况信息统计项
  private Double oilWear;//油耗
  private Double currentMileage;  //当前里程数
  private Long currentMileageLastUpdateTime;//当前里程最后更新时间
  private Double lastObdMileage;//OBD上报的上一次里程
  private String instantOilWear;//瞬时油耗 单位 ml/s
  private String oilWearPerHundred;//百公里油耗 l/100km
  private String oilMass;//油量 百分比%
  private String engineCoolantTemperature;//发动机水温 发动机冷却剂   单位 ℃
  private String batteryVoltage;//电瓶电压 单位 V
  private YesNo isDefault = YesNo.NO;


  private String coordinateLat;//车辆纬度信息
  private String coordinateLon;//车辆经度信息
  private Double maintainPeriod;//保养周期
  private Double lastMaintainMileage;//上次保养里程数

  private Double worstOilWear;  //最差平均油耗
  private Double bestOilWear;   //最佳平均油耗

  //里程提醒状态
//  private Status currentMileageStatus;
//
//
//  @Enumerated(EnumType.STRING)
//  @Column(name = "current_mileage_status")
//  public Status getCurrentMileageStatus() {
//    return currentMileageStatus;
//  }
//
//  public void setCurrentMileageStatus(Status currentMileageStatus) {
//    this.currentMileageStatus = currentMileageStatus;
//  }

  @Column(name = "insuranceCompanyId")
  public Long getInsuranceCompanyId() {
    return insuranceCompanyId;
  }

  public void setInsuranceCompanyId(Long insuranceCompanyId) {
    this.insuranceCompanyId = insuranceCompanyId;
  }

  private Double avgOilWear;   //全部平均油耗

  private String juheCityName;//该车牌号手机端查询设置的聚合城市名称
  private String juheCityCode;//该车牌号手机端查询设置的聚合城市编码

  private Long insuranceCompanyId;

  public AppVehicle() {
    super();
  }

  public AppVehicle(AppVehicleDTO dto) {
    this.setId(dto.getVehicleId());
    this.setMobile(dto.getMobile());
    this.setContact(dto.getContact());
    this.setAppUserNo(dto.getUserNo());
    this.setStatus(dto.getStatus());
    this.setVehicleNo(dto.getVehicleNo());
    this.setVehicleBrand(dto.getVehicleBrand());
    this.setVehicleBrandId(dto.getVehicleBrandId());
    this.setVehicleModelId(dto.getVehicleModelId());
    this.setVehicleModel(dto.getVehicleModel());
    this.setNextExamineTime(dto.getNextExamineTime());
    this.setNextInsuranceTime(dto.getNextInsuranceTime());
    this.setNextMaintainMileage(dto.getNextMaintainMileage());
    this.setNextMaintainTime(dto.getNextMaintainTime());
    this.setOilWear(dto.getOilWear());
    this.setReportTime(dto.getReportTime());
    this.setReportTime(dto.getReportTime());
    this.setCurrentMileage(dto.getCurrentMileage());
    this.setInstantOilWear(dto.getInstantOilWear());
    this.setOilWearPerHundred(dto.getOilWearPerHundred());
    this.setOilMass(dto.getOilMass());
    this.setEngineCoolantTemperature(dto.getEngineCoolantTemperature());
    this.setBatteryVoltage(dto.getBatteryVoltage());
    this.setRegistNo(StringUtils.trim(dto.getRegistNo()));
    this.setVehicleVin(StringUtils.trim(dto.getVehicleVin()));
    this.setEngineNo(StringUtils.trim(dto.getEngineNo()));
    this.setCurrentMileageLastUpdateTime(dto.getCurrentMileageLastUpdateTime());

    this.setCoordinateLat(dto.getCoordinateLat());
    this.setCoordinateLon(dto.getCoordinateLon());

    this.setLastMaintainMileage(dto.getLastMaintainMileage());
    this.setMaintainPeriod(dto.getMaintainPeriod());
    this.setAvgOilWear(dto.getAvgOilWear());
    this.setBestOilWear(dto.getBestOilWear());
    this.setWorstOilWear(dto.getWorstOilWear());
    this.setLastObdMileage(dto.getLastObdMileage());

    this.setJuheCityCode(dto.getJuheCityCode());
    this.setJuheCityName(dto.getJuheCityName());
  }

  public AppVehicle fromDTO(AppVehicleDTO dto) {
    this.setId(dto.getVehicleId());
    this.setMobile(dto.getMobile());
    this.setContact(dto.getContact());
    this.setAppUserNo(dto.getUserNo());
    this.setStatus(dto.getStatus());
    this.setVehicleNo(dto.getVehicleNo());
    this.setVehicleBrand(dto.getVehicleBrand());
    this.setVehicleBrandId(dto.getVehicleBrandId());
    this.setVehicleModelId(dto.getVehicleModelId());
    this.setVehicleModel(dto.getVehicleModel());
    this.setNextExamineTime(dto.getNextExamineTime());
    this.setNextInsuranceTime(dto.getNextInsuranceTime());
    this.setNextMaintainMileage(dto.getNextMaintainMileage());
    this.setNextMaintainTime(dto.getNextMaintainTime());
    this.setOilWear(dto.getOilWear());
    this.setReportTime(dto.getReportTime());
    this.setReportTime(dto.getReportTime());
    this.setCurrentMileage(dto.getCurrentMileage());
    this.setInstantOilWear(dto.getInstantOilWear());
    this.setOilWearPerHundred(dto.getOilWearPerHundred());
    this.setOilMass(dto.getOilMass());
    this.setEngineCoolantTemperature(dto.getEngineCoolantTemperature());
    this.setBatteryVoltage(dto.getBatteryVoltage());
    this.setIsDefault(dto.getIsDefault());
    this.setRegistNo(StringUtils.trim(dto.getRegistNo()));
    this.setVehicleVin(StringUtils.trim(dto.getVehicleVin()));
    this.setEngineNo(StringUtils.trim(dto.getEngineNo()));
    this.setCurrentMileageLastUpdateTime(dto.getCurrentMileageLastUpdateTime());

    this.setCoordinateLat(dto.getCoordinateLat());
    this.setCoordinateLon(dto.getCoordinateLon());

    this.setLastMaintainMileage(dto.getLastMaintainMileage());
    this.setMaintainPeriod(dto.getMaintainPeriod());
    this.setAvgOilWear(dto.getAvgOilWear());
    this.setBestOilWear(dto.getBestOilWear());
    this.setWorstOilWear(dto.getWorstOilWear());
    this.setLastObdMileage(dto.getLastObdMileage());

    this.setJuheCityCode(dto.getJuheCityCode());
    this.setJuheCityName(dto.getJuheCityName());
    return this;
  }

  public AppVehicleDTO toDTO() {
    AppVehicleDTO dto = new AppVehicleDTO();
    dto.setVehicleId(getId());
    dto.setCreatedTime(getCreationDate());
    dto.setMobile(getMobile());
    dto.setContact(getContact());
    dto.setUserNo(getAppUserNo());
    dto.setStatus(getStatus());
    dto.setVehicleNo(getVehicleNo());
    dto.setVehicleBrand(getVehicleBrand());
    dto.setVehicleBrandId(getVehicleBrandId());
    dto.setVehicleModelId(getVehicleModelId());
    dto.setVehicleModel(getVehicleModel());
    dto.setNextExamineTime(getNextExamineTime());
    dto.setNextInsuranceTime(getNextInsuranceTime());
    dto.setNextMaintainMileage(getNextMaintainMileage());
    dto.setNextMaintainTime(getNextMaintainTime());
    dto.setOilWear(getOilWear());
    dto.setReportTime(getReportTime());
    dto.setReportTime(getReportTime());
    dto.setCurrentMileage(getCurrentMileage());
    dto.setInstantOilWear(this.getInstantOilWear());
    dto.setOilWearPerHundred(this.getOilWearPerHundred());
    dto.setOilMass(this.getOilMass());
    dto.setEngineCoolantTemperature(this.getEngineCoolantTemperature());
    dto.setBatteryVoltage(this.getBatteryVoltage());
    dto.setIsDefault(this.getIsDefault());
    dto.setRegistNo(StringUtils.trim(this.getRegistNo()));
    dto.setVehicleVin(StringUtils.trim(this.getVehicleVin()));
    dto.setEngineNo(StringUtils.trim(this.getEngineNo()));

    dto.setCoordinateLat(this.getCoordinateLat());
    dto.setCoordinateLon(this.getCoordinateLon());
    dto.setCurrentMileageLastUpdateTime(this.getCurrentMileageLastUpdateTime());
    dto.setLastMaintainMileage(this.getLastMaintainMileage());
    dto.setMaintainPeriod(this.getMaintainPeriod());
    dto.setAvgOilWear(this.getAvgOilWear());
    dto.setBestOilWear(this.getBestOilWear());
    dto.setWorstOilWear(this.getWorstOilWear());
    dto.setLastObdMileage(this.getLastObdMileage());

    dto.setJuheCityCode(this.getJuheCityCode());
    dto.setJuheCityName(this.getJuheCityName());
    dto.setInsuranceCompanyId(this.getInsuranceCompanyId());
    return dto;
  }

  public void fromDTO(OBDBindingDTO dto) {
    setVehicleVin(dto.getVehicleVin());
    setVehicleBrand(dto.getVehicleBrand());
    setVehicleBrandId(dto.getVehicleBrandId());
    setVehicleModelId(dto.getVehicleModelId());
    setVehicleModel(dto.getVehicleModel());
    setVehicleNo(dto.getVehicleNo());
    setAppUserNo(dto.getUserNo());
    setNextExamineTime(dto.getNextExamineTime());
    setNextMaintainMileage(dto.getNextMaintainMileage());
    setNextInsuranceTime(dto.getNextInsuranceTime());
    setCurrentMileage(dto.getCurrentMileage());
    setEngineNo(dto.getEngineNo());
    setRegistNo(dto.getRegistNo());
  }

  public void updateAppVehicleFromApp(AppVehicleDTO appVehicleDTO) {
    setVehicleBrand(appVehicleDTO.getVehicleBrand());
    setVehicleBrandId(appVehicleDTO.getVehicleBrandId());
    setVehicleModelId(appVehicleDTO.getVehicleModelId());
    setVehicleModel(appVehicleDTO.getVehicleModel());
    setVehicleNo(appVehicleDTO.getVehicleNo());

    setVehicleVin(StringUtils.trim(appVehicleDTO.getVehicleVin()));//车架号
    setRegistNo(StringUtils.trim(appVehicleDTO.getRegistNo()));    //等级证书
    setEngineNo(StringUtils.trim(appVehicleDTO.getEngineNo()));    //发动机号

    setNextExamineTime(appVehicleDTO.getNextExamineTime());
    setNextInsuranceTime(appVehicleDTO.getNextInsuranceTime());
    setNextMaintainTime(appVehicleDTO.getNextMaintainTime());
    setNextMaintainMileage(appVehicleDTO.getNextMaintainMileage());
    setCurrentMileage(appVehicleDTO.getCurrentMileage());
  }

  public void updateAppVehicleFromGsmApp(AppVehicleDTO appVehicleDTO) {
    if(StringUtils.isNotBlank(appVehicleDTO.getVehicleNo())){
      setVehicleNo(appVehicleDTO.getVehicleNo());
    }
    setCurrentMileage(appVehicleDTO.getCurrentMileage());
    setMaintainPeriod(appVehicleDTO.getMaintainPeriod());
    setLastMaintainMileage(appVehicleDTO.getLastMaintainMileage());
    setNextMaintainTime(appVehicleDTO.getNextMaintainTime());
    setNextInsuranceTime(appVehicleDTO.getNextInsuranceTime());
    setNextExamineTime(appVehicleDTO.getNextExamineTime());
    setVehicleVin(appVehicleDTO.getVehicleVin());
    setRegistNo(appVehicleDTO.getRegistNo());
    setEngineNo(appVehicleDTO.getEngineNo());

    this.setJuheCityCode(appVehicleDTO.getJuheCityCode());
    this.setJuheCityName(appVehicleDTO.getJuheCityName());
  }


  @Enumerated(EnumType.STRING)
  @Column(name = "is_default")
  public YesNo getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(YesNo isDefault) {
    this.isDefault = isDefault;
  }

  @Column(name = "vehicle_vin")
  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "contact")
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "vehicle_model_id")
  public Long getVehicleModelId() {
    return vehicleModelId;
  }

  public void setVehicleModelId(Long vehicleModelId) {
    this.vehicleModelId = vehicleModelId;
  }

  @Column(name = "vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "vehicle_brand_id")
  public Long getVehicleBrandId() {
    return vehicleBrandId;
  }

  public void setVehicleBrandId(Long vehicleBrandId) {
    this.vehicleBrandId = vehicleBrandId;
  }

  @Column(name = "next_maintain_mileage")
  public Double getNextMaintainMileage() {
    return nextMaintainMileage;
  }

  public void setNextMaintainMileage(Double nextMaintainMileage) {
    this.nextMaintainMileage = nextMaintainMileage;
  }

  @Column(name = "next_maintain_time")
  public Long getNextMaintainTime() {
    return nextMaintainTime;
  }

  public void setNextMaintainTime(Long nextMaintainTime) {
    this.nextMaintainTime = nextMaintainTime;
  }

  @Column(name = "next_examine_time")
  public Long getNextExamineTime() {
    return nextExamineTime;
  }

  public void setNextExamineTime(Long nextExamineTime) {
    this.nextExamineTime = nextExamineTime;
  }

  @Column(name = "next_insurance_time")
  public Long getNextInsuranceTime() {
    return nextInsuranceTime;
  }

  public void setNextInsuranceTime(Long nextInsuranceTime) {
    this.nextInsuranceTime = nextInsuranceTime;
  }

  @Column(name = "oil_wear")
  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  @Column(name = "report_time")
  public Long getReportTime() {
    return reportTime;
  }

  public void setReportTime(Long reportTime) {
    this.reportTime = reportTime;
  }

  @Column(name = "engine_no")
  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name = "current_mileage")
  public Double getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(Double currentMileage) {
    this.currentMileage = currentMileage;
  }

  @Column(name = "instant_oil_wear")
  public String getInstantOilWear() {
    return instantOilWear;
  }

  public void setInstantOilWear(String instantOilWear) {
    this.instantOilWear = instantOilWear;
  }

  @Column(name = "oil_wear_per_hundred")
  public String getOilWearPerHundred() {
    return oilWearPerHundred;
  }

  public void setOilWearPerHundred(String oilWearPerHundred) {
    this.oilWearPerHundred = oilWearPerHundred;
  }

  @Column(name = "oil_mass")
  public String getOilMass() {
    return oilMass;
  }

  public void setOilMass(String oilMass) {
    this.oilMass = oilMass;
  }

  @Column(name = "engine_coolant_temperature")
  public String getEngineCoolantTemperature() {
    return engineCoolantTemperature;
  }

  public void setEngineCoolantTemperature(String engineCoolantTemperature) {
    this.engineCoolantTemperature = engineCoolantTemperature;
  }

  @Column(name = "battery_voltage")
  public String getBatteryVoltage() {
    return batteryVoltage;
  }

  public void setBatteryVoltage(String batteryVoltage) {
    this.batteryVoltage = batteryVoltage;
  }

  @Column(name = "next_maintain_mileage_push_message_remind_times")
  public Integer getNextMaintainMileagePushMessageRemindTimes() {
    return nextMaintainMileagePushMessageRemindTimes;
  }

  public void setNextMaintainMileagePushMessageRemindTimes(Integer nextMaintainMileagePushMessageRemindTimes) {
    this.nextMaintainMileagePushMessageRemindTimes = nextMaintainMileagePushMessageRemindTimes;
  }

  @Column(name = "regist_no")
  public String getRegistNo() {
    return registNo;
  }

  public void setRegistNo(String registNo) {
    this.registNo = registNo;
  }

  @Column(name = "current_mileage_last_update_time")
  public Long getCurrentMileageLastUpdateTime() {
    return currentMileageLastUpdateTime;
  }

  public void setCurrentMileageLastUpdateTime(Long currentMileageLastUpdateTime) {
    this.currentMileageLastUpdateTime = currentMileageLastUpdateTime;
  }

  @Column(name = "coordinate_lat")
  public String getCoordinateLat() {
    return coordinateLat;
  }

  public void setCoordinateLat(String coordinateLat) {
    this.coordinateLat = coordinateLat;
  }

  @Column(name = "coordinate_lon")
  public String getCoordinateLon() {
    return coordinateLon;
  }

  public void setCoordinateLon(String coordinateLon) {
    this.coordinateLon = coordinateLon;
  }

  @Column(name = "maintain_period")
  public Double getMaintainPeriod() {
    return maintainPeriod;
  }

  public void setMaintainPeriod(Double maintainPeriod) {
    this.maintainPeriod = maintainPeriod;
  }

  @Column(name = "last_maintain_mileage")
  public Double getLastMaintainMileage() {
    return lastMaintainMileage;
  }

  public void setLastMaintainMileage(Double lastMaintainMileage) {
    this.lastMaintainMileage = lastMaintainMileage;
  }

  @Column(name = "worst_oil_wear")
  public Double getWorstOilWear() {
    return worstOilWear;
  }

  public void setWorstOilWear(Double worstOilWear) {
    this.worstOilWear = worstOilWear;
  }

  @Column(name = "best_oil_wear")
  public Double getBestOilWear() {
    return bestOilWear;
  }

  public void setBestOilWear(Double bestOilWear) {
    this.bestOilWear = bestOilWear;
  }

  @Column(name = "avg_oil_wear")
  public Double getAvgOilWear() {
    return avgOilWear;
  }

  public void setAvgOilWear(Double avgOilWear) {
    this.avgOilWear = avgOilWear;
  }

  @Column(name = "last_obd_mileage")
  public Double getLastObdMileage() {
    return lastObdMileage;
  }

  public void setLastObdMileage(Double lastObdMileage) {
    this.lastObdMileage = lastObdMileage;
  }


  @Column(name = "juhe_city_name")
  public String getJuheCityName() {
    return juheCityName;
  }

  public void setJuheCityName(String juheCityName) {
    this.juheCityName = juheCityName;
  }

  @Column(name = "juhe_city_code")
  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }
}
