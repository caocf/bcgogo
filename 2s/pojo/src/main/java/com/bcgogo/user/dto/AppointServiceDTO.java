package com.bcgogo.user.dto;

import com.bcgogo.base.BaseDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-10
 * Time: 上午1:57
 * To change this template use File | Settings | File Templates.
 */
public class AppointServiceDTO extends BaseDTO{

  private Long shopId;
  private String customerId;
  private String vehicleId;
  private String appointName;
  private String appointDate;
  private String operateType;

  private String maintainTimeStr;
  private String insureTimeStr;
  private String examineTimeStr;
  private Long maintainMileage;//保养里程

  private Double maintainMileagePeriod;//保养里程周期
  private Double obdMileage;//当前里程

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(String vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getAppointName() {
    return appointName;
  }

  public void setAppointName(String appointName) {
    this.appointName = appointName;
  }

  public String getAppointDate() {
    return appointDate;
  }

  public void setAppointDate(String appointDate) {
    this.appointDate = appointDate;
  }

  public String getMaintainTimeStr() {
    return maintainTimeStr;
  }

  public void setMaintainTimeStr(String maintainTimeStr) {
    this.maintainTimeStr = maintainTimeStr;
  }

  public String getInsureTimeStr() {
    return insureTimeStr;
  }

  public void setInsureTimeStr(String insureTimeStr) {
    this.insureTimeStr = insureTimeStr;
  }

  public String getExamineTimeStr() {
    return examineTimeStr;
  }

  public void setExamineTimeStr(String examineTimeStr) {
    this.examineTimeStr = examineTimeStr;
  }

  public String getOperateType() {
    return operateType;
  }

  public void setOperateType(String operateType) {
    this.operateType = operateType;
  }

  public Long getMaintainMileage() {
    return maintainMileage;
  }

  public void setMaintainMileage(Long maintainMileage) {
    this.maintainMileage = maintainMileage;
  }

  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }

  public Double getMaintainMileagePeriod() {
    return maintainMileagePeriod;
  }

  public void setMaintainMileagePeriod(Double maintainMileagePeriod) {
    this.maintainMileagePeriod = maintainMileagePeriod;
  }
}
