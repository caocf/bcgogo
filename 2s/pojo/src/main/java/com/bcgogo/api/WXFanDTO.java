package com.bcgogo.api;


/**
 * Created by JieZhang on 14-11-07.
 */
//注：微信粉丝绑定车辆信息及对应客户信息
public class WXFanDTO {
  private String licenceNo;    //绑定车牌号
  private String model;         //电话
  private String name;          //本店客户名
  private String mobile;        //车型
  private String brand;         //车辆品牌
  private String vehicleId;
  private String customerId;

  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
}
