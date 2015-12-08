package com.bcgogo.txn.dto;

import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-1-11
 * Time: 下午5:44
 * To change this template use File | Settings | File Templates.
 */
public class CarDTO implements Serializable {
  private String id;
  private String shopId;
  private String licenceNo;
  private String brand;
  private String model;
  private String year;
  private String engine;
  private String color;
  private long carDate;
  private String carno;
  private Double startMileage;
  private Double obdMileage;
  private String chassisNumber;//机架号
  private String dateString;
  private String engineNo;//发动机号
  private String contact;
  private String mobile;
  private String gsmObdImei;
  private String gsmObdImeiMoblie;
  private Double maintainMileagePeriod;//保养里程周期

  public CarDTO(){

  }
  public CarDTO(CustomerVehicleResponse response) {
    this.setId(response.getVehicleId() == null ? "" : response.getVehicleId().toString());
    this.setLicenceNo(response.getLicenceNo());
    this.setBrand(response.getBrand());
    this.setModel(response.getModel());
    this.setYear(response.getYear());
    this.setEngine(response.getEngine());
    this.setColor(response.getColor());
    if (response.getCarDate() != null) {
      this.setCarDate(response.getCarDate());
    }
    this.setStartMileage(response.getStartMileage());
    this.setChassisNumber(response.getVin());
    this.setEngineNo(response.getEngineNo());
    this.setContact(response.getContact());
    this.setMobile(response.getMobile());
    this.setGsmObdImei(response.getGsmObdImei());
    this.setGsmObdImeiMoblie(response.getGsmObdImeiMoblie());
    this.setObdMileage(response.getObdMileage());
    this.setMaintainMileagePeriod(response.getMaintainMileagePeriod());

  }

  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getContact() {

    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public Double getStartMileage() {
    return startMileage;
  }

  public void setStartMileage(Double startMileage) {
    this.startMileage = startMileage;
  }

  public String getCarno() {
    return carno;
  }

  public void setCarno(String carno) {
    this.carno = carno;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
    this.carno = licenceNo;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
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

  public long getCarDate() {
    return carDate;
  }

  public void setCarDate(long carDate) {
    this.carDate = carDate;
  }

  public String getDateString() {
    return dateString;
  }

  public void setDateString(String dateString) {
    this.dateString = dateString;
  }

  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
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

  public Double getMaintainMileagePeriod() {
    return maintainMileagePeriod;
  }

  public void setMaintainMileagePeriod(Double maintainMileagePeriod) {
    this.maintainMileagePeriod = maintainMileagePeriod;
  }
}
