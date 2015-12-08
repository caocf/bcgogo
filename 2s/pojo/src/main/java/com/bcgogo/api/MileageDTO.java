package com.bcgogo.api;

import java.io.Serializable;

/**
 * 里程提醒
 * Created with IntelliJ IDEA.
 * User: zj
 * Date: 15-6-23
 * Time: 上午11:10
 */
public class MileageDTO implements Serializable {
  private Long appVehicleId;
  private String mobile;            //车辆手机号码
  private String contact;                //车辆联系人
  private String vehicleNo;              //车牌号
  private String nextMaintainMileage;    //下次保养里程数
  private String currentMileage;  //当前里程数
  private String toNextMaintainMileage;//距保养里程数
  private String appUserNo;
  private String customerName;          //客户姓名
  private String customerMobile;        //客户电话
  private String customerId;

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

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
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

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getNextMaintainMileage() {
    return nextMaintainMileage;
  }

  public void setNextMaintainMileage(String nextMaintainMileage) {
    this.nextMaintainMileage = nextMaintainMileage;
  }

  public String getCurrentMileage() {
    return currentMileage;
  }

  public void setCurrentMileage(String currentMileage) {
    this.currentMileage = currentMileage;
  }

  public String getToNextMaintainMileage() {
    return toNextMaintainMileage;
  }

  public void setToNextMaintainMileage(String toNextMaintainMileage) {
    this.toNextMaintainMileage = toNextMaintainMileage;
  }
}
