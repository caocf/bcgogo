package com.bcgogo.driving.model;

import com.bcgogo.driving.model.base.LongIdentifier;
import com.bcgogo.pojox.api.VehicleDTO;
import com.bcgogo.pojox.enums.VehicleStatus;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-11-26
 * Time: 09:48
 */
@Entity
@Table(name = "vehicle")
public class Vehicle extends LongIdentifier {
  private Long shopId;
  private Long licenceAreaId;
  private String licenceNo;
  private String licenceNoRevert;
  private String engineNo;
  private String vin;
  private Long carDate;
  private Long carId;
  private String brand;
  private String mfr;
  private String model;
  private String year;
  private String engine;
  private String trim;
  private String color;
  private String memo;
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private Long engineId;
  private Double startMileage;    //上次进厂里程
  private VehicleStatus status;
  private String contact; //车辆联系人
  private String mobile;  //车辆联系方式
  private String chassisNumber;
  private Double obdMileage;  //OBD最新里程
  private Long mileageLastUpdateTime;//里程最后更新时间
  private Double lastObdMileage;//OBD上报的上一次里程
  private Long obdId;
  private String gsmObdImei;
  private String gsmObdImeiMoblie;

  public Vehicle fromDTO(VehicleDTO vehicleDTO) {
    this.setId(vehicleDTO.getId());
    this.setShopId(vehicleDTO.getShopId());
    this.setLicenceAreaId(vehicleDTO.getLicenceAreaId());
    this.setLicenceNo(vehicleDTO.getLicenceNo());
    this.setLicenceNoRevert(vehicleDTO.getLicenceNoRevert());
    this.setEngineNo(vehicleDTO.getEngineNo());
    this.setVin(vehicleDTO.getVin());
    this.setCarDate(vehicleDTO.getCarDate());
    this.setCarId(vehicleDTO.getCarId());
    this.setBrand(vehicleDTO.getBrand());
    this.setMfr(vehicleDTO.getMfr());
    this.setModel(vehicleDTO.getModel());
    this.setYear(vehicleDTO.getYear());
    this.setTrim(vehicleDTO.getTrim());
    this.setColor(vehicleDTO.getColor());
    this.setMemo(vehicleDTO.getMemo());
    this.setEngine(vehicleDTO.getEngine());
    this.setStartMileage(vehicleDTO.getStartMileage());
    this.setBrandId(vehicleDTO.getBrandId());
    this.setMfrId(vehicleDTO.getMfrId());
    this.setModelId(vehicleDTO.getModelId());
    this.setYearId(vehicleDTO.getYearId());
    this.setEngineId(vehicleDTO.getEngineId());
    this.setChassisNumber(vehicleDTO.getChassisNumber());
    this.setStatus(vehicleDTO.getStatus());
    this.setContact(vehicleDTO.getContact());
    this.setMobile(vehicleDTO.getMobile());
    this.setObdMileage(vehicleDTO.getObdMileage());
    this.setObdId(vehicleDTO.getObdId());
    this.setGsmObdImei(vehicleDTO.getGsmObdImei());
    this.setGsmObdImeiMoblie(vehicleDTO.getGsmObdImeiMoblie());
    this.setLastObdMileage(vehicleDTO.getLastObdMileage());
    return this;
  }

  public VehicleDTO toDTO() {
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setId(this.getId());
    vehicleDTO.setShopId(this.getShopId());
    vehicleDTO.setLicenceAreaId(this.getLicenceAreaId());
    vehicleDTO.setLicenceNo(this.getLicenceNo());
    vehicleDTO.setLicenceNoRevert(this.getLicenceNoRevert());
    vehicleDTO.setEngineNo(this.getEngineNo());
    vehicleDTO.setVin(this.getVin());
    vehicleDTO.setCarDate(this.getCarDate());
    vehicleDTO.setCarId(this.getCarId());
    vehicleDTO.setBrand(this.getBrand());
    vehicleDTO.setMfr(this.getMfr());
    vehicleDTO.setModel(this.getModel());
    vehicleDTO.setYear(this.getYear());
    vehicleDTO.setTrim(this.getTrim());
    vehicleDTO.setColor(this.getColor());
    vehicleDTO.setMemo(this.getMemo());
    vehicleDTO.setEngine(this.getEngine());
    vehicleDTO.setStartMileage(this.getStartMileage());
    vehicleDTO.setBrandId(this.getBrandId());
    vehicleDTO.setMfrId(this.getMfrId());
    vehicleDTO.setModelId(this.getModelId());
    vehicleDTO.setYearId(this.getYearId());
    vehicleDTO.setEngineId(this.getEngineId());
    vehicleDTO.setChassisNumber(this.getChassisNumber());
    vehicleDTO.setStatus(this.getStatus());
    vehicleDTO.setContact(this.getContact());
    vehicleDTO.setMobile(this.getMobile());
    vehicleDTO.setColor(this.getColor());
    vehicleDTO.setObdMileage(this.getObdMileage());
    vehicleDTO.setMileageLastUpdateTime(this.getMileageLastUpdateTime());
    vehicleDTO.setObdId(this.getObdId());
    vehicleDTO.setGsmObdImei(this.getGsmObdImei());
    vehicleDTO.setGsmObdImeiMoblie(this.getGsmObdImeiMoblie());
    vehicleDTO.setLastObdMileage(this.getLastObdMileage());
    return vehicleDTO;
  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "licence_area_id")
  public Long getLicenceAreaId() {
    return licenceAreaId;
  }

  public void setLicenceAreaId(Long licenceAreaId) {
    this.licenceAreaId = licenceAreaId;
  }

  @Column(name = "licence_no", length = 20)
  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  @Column(name = "licence_no_revert", length = 20)
  public String getLicenceNoRevert() {
    return licenceNoRevert;
  }

  public void setLicenceNoRevert(String licenceNoRevert) {
    this.licenceNoRevert = licenceNoRevert;
  }

  @Column(name = "engine_no", length = 20)
  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  @Column(name = "vin", length = 20)
  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  @Column(name = "car_date")
  public Long getCarDate() {
    return carDate;
  }

  public void setCarDate(Long carDate) {
    this.carDate = carDate;
  }

  @Column(name = "car_id")
  public Long getCarId() {
    return carId;
  }

  public void setCarId(Long carId) {
    this.carId = carId;
  }

  @Column(name = "brand", length = 20)
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "mfr", length = 50)
  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
  }

  @Column(name = "model", length = 50)
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Column(name = "year", length = 20)
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  @Column(name = "trim", length = 50)
  public String getTrim() {
    return trim;
  }

  public void setTrim(String trim) {
    this.trim = trim;
  }

  @Column(name = "engine")
  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  @Column(name = "color", length = 20)
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "brand_id")
  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  @Column(name = "mfr_id")
  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  @Column(name = "model_id")
  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  @Column(name = "year_id")
  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  @Column(name = "engine_id")
  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }

  @Column(name = "chassis_number", length = 50)
  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  @Column(name = "start_mileage")
  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public VehicleStatus getStatus() {
    return status;
  }

  public void setStatus(VehicleStatus status) {
    this.status = status;
  }

  @Column(name = "contact", length = 50)
  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  @Column(name = "mobile", length = 20)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "obd_mileage")
  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }


  @Column(name = "mileage_last_update_time")
  public Long getMileageLastUpdateTime() {
    return mileageLastUpdateTime;
  }

  public void setMileageLastUpdateTime(Long mileageLastUpdateTime) {
    this.mileageLastUpdateTime = mileageLastUpdateTime;
  }

  @Column(name = "gsm_obd_imei")
  public String getGsmObdImei() {
    return gsmObdImei;
  }

  public void setGsmObdImei(String gsmObdImei) {
    this.gsmObdImei = gsmObdImei;
  }

  @Column(name = "gsm_obd_imei_mobile")
  public String getGsmObdImeiMoblie() {
    return gsmObdImeiMoblie;
  }

  public void setGsmObdImeiMoblie(String gsmObdImeiMoblie) {
    this.gsmObdImeiMoblie = gsmObdImeiMoblie;
  }

  @Column(name = "last_obd_mileage")
  public Double getLastObdMileage() {
    return lastObdMileage;
  }

  public void setLastObdMileage(Double lastObdMileage) {
    this.lastObdMileage = lastObdMileage;
  }
}
