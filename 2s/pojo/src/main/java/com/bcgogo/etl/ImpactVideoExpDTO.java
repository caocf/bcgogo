package com.bcgogo.etl;

import com.bcgogo.enums.UploadStatus;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-17
 * Time: 17:54
 */
public class ImpactVideoExpDTO {
  private Long impactVideoId;
  private String impactVideoIdStr;
  private String uuid;
  private String appUserNo;//app用户账号
  private Long uploadTime;
  private String uploadTimeDateStr;//long to Date转化
  private String uploadTimeStr;   //Long to String 转化
  private String longitude;
  private String latitude;
  private String altitude;
  private String path;
  private String vehicleNo; //车牌号
  private String address;//碰撞地址
  private String url;//视频地址
  private String customerId;
  private String customerName;
  private String customerMobile;
  private String status;
  private String vehicleBrand;
  private String vehicleModel;
  private Long impactId;
  private String  impactIdStr;
  private String uploadStatus;

  public String getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(String uploadStatus) {
    this.uploadStatus = uploadStatus;
  }

  public String getUploadTimeDateStr() {
    return uploadTimeDateStr;
  }

  public void setUploadTimeDateStr(String uploadTimeDateStr) {
    this.uploadTimeDateStr = uploadTimeDateStr;
  }

  public Long getImpactVideoId() {
    return impactVideoId;
  }

  public void setImpactVideoId(Long impactVideoId) {
    this.impactVideoId = impactVideoId;
    this.impactVideoIdStr=String.valueOf(impactVideoId);
  }

  public String getImpactVideoIdStr() {
    return impactVideoIdStr;
  }

  public void setImpactVideoIdStr(String impactVideoIdStr) {
    this.impactVideoIdStr = impactVideoIdStr;
  }

  public Long getImpactId() {
    return impactId;
  }

  public void setImpactId(Long impactId) {
    this.impactId = impactId;
  }

  public String getImpactIdStr() {
    return impactIdStr;
  }

  public void setImpactIdStr(String impactIdStr) {
    this.impactIdStr = impactIdStr;
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

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }


  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
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

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getAltitude() {
    return altitude;
  }

  public void setAltitude(String altitude) {
    this.altitude = altitude;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
