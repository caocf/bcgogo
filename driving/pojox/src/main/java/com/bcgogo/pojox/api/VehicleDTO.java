package com.bcgogo.pojox.api;

import com.bcgogo.pojox.enums.VehicleStatus;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-11-26
 * Time: 14:28
 */
public class VehicleDTO {
  private Long id;
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
    private String  chassisNumber;
    private Double obdMileage;  //OBD最新里程
    private Long mileageLastUpdateTime;//里程最后更新时间
    private Double lastObdMileage;//OBD上报的上一次里程
    private Long obdId;
    private String gsmObdImei;
    private String gsmObdImeiMoblie;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getLicenceAreaId() {
    return licenceAreaId;
  }

  public void setLicenceAreaId(Long licenceAreaId) {
    this.licenceAreaId = licenceAreaId;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getLicenceNoRevert() {
    return licenceNoRevert;
  }

  public void setLicenceNoRevert(String licenceNoRevert) {
    this.licenceNoRevert = licenceNoRevert;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public Long getCarDate() {
    return carDate;
  }

  public void setCarDate(Long carDate) {
    this.carDate = carDate;
  }

  public Long getCarId() {
    return carId;
  }

  public void setCarId(Long carId) {
    this.carId = carId;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getMfr() {
    return mfr;
  }

  public void setMfr(String mfr) {
    this.mfr = mfr;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  public String getTrim() {
    return trim;
  }

  public void setTrim(String trim) {
    this.trim = trim;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public VehicleStatus getStatus() {
    return status;
  }

  public void setStatus(VehicleStatus status) {
    this.status = status;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }

  public Long getMileageLastUpdateTime() {
    return mileageLastUpdateTime;
  }

  public void setMileageLastUpdateTime(Long mileageLastUpdateTime) {
    this.mileageLastUpdateTime = mileageLastUpdateTime;
  }

  public Double getLastObdMileage() {
    return lastObdMileage;
  }

  public void setLastObdMileage(Double lastObdMileage) {
    this.lastObdMileage = lastObdMileage;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public String getGsmObdImei() {
    return gsmObdImei;
  }

  public void setGsmObdImei(String gsmObdImei) {
    this.gsmObdImei = gsmObdImei;
  }

  public String getGsmObdImeiMoblie() {
    return gsmObdImeiMoblie;
  }

  public void setGsmObdImeiMoblie(String gsmObdImeiMoblie) {
    this.gsmObdImeiMoblie = gsmObdImeiMoblie;
  }
}
