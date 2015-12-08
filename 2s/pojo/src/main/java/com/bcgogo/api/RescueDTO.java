package com.bcgogo.api;

import com.bcgogo.enums.user.userGuide.SosStatus;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-16
 * Time: 上午11:59
 */
public class RescueDTO {
  private Long id;
  private String idStr;
  private String appUserNo;
  private String vehicleNo;
  private String mobile;
  private Long shopId;
  private String lat;
  private String lon;
  private Long uploadTime;
  private String uploadTimeStr;
  private Long uploadServerTime;
  private String addr;
  private String addrShort;  //地址简称
  private String rdtc;
  private String customerName;
  private String customerMobile;
  private String customerId;
  private String vehicleBrand;
  private String vehicleMobile;
  private String currentMileage;
  private String vehicleModel;
  private String vehicleContact;
  private String state;
  private SosStatus sosStatus;

  public SosStatus getSosStatus() {
    return sosStatus;
  }

  public void setSosStatus(SosStatus sosStatus) {
    this.sosStatus = sosStatus;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public String getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(String currentMileage) {
    this.currentMileage = currentMileage;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
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

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  public String getUploadTimeStr() {
    return uploadTimeStr;
  }

  public void setUploadTimeStr(String uploadTimeStr) {
    this.uploadTimeStr = uploadTimeStr;
  }

  public Long getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(Long uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
  }

  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  public String getAddrShort() {
    return addrShort;
  }

  public void setAddrShort(String addrShort) {
    this.addrShort = addrShort;
  }

  public String getRdtc() {
    return rdtc;
  }

  public void setRdtc(String rdtc) {
    this.rdtc = rdtc;
  }
}
