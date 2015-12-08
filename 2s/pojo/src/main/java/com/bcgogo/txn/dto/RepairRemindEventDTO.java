package com.bcgogo.txn.dto;


import com.bcgogo.enums.RepairRemindEventTypes;

import java.io.Serializable;

public class RepairRemindEventDTO implements Serializable {
  public RepairRemindEventDTO() {
  }
  private Long id;
  private Long shopId;
  private Long repairOrderId;
  /**
   * 1:待交付
   * 2:缺料待修
   * 5:来料待修
   */
  private RepairRemindEventTypes eventType;
  /**
   * 当eventType=1 为空
   * 当eventType=2 缺料产品的product_local_info
   * 5:来料待修 来料产品的product_local_info
   */
  private Long eventContent;
  private String eventContentStr;
  private Long customerId;
  private String service;

  private Long productId;
  private Long finishTime;

  private Double amount;
  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;

  private String vehicle;
  private String customer;
  private String mobile;

  private String unit;
  private Long createTime;
  private String receiptNo;

  public Long getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(Long finishTime) {
    this.finishTime = finishTime;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
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

  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getEventContentStr() {
    return eventContentStr;
  }

  public void setEventContentStr(String eventContentStr) {
    this.eventContentStr = eventContentStr;
  }

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

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  public RepairRemindEventTypes getEventType() {
    return eventType;
  }

  public void setEventType(RepairRemindEventTypes eventType) {
    this.eventType = eventType;
  }

  public Long getEventContent() {
    return eventContent;
  }

  public void setEventContent(Long eventContent) {
    this.eventContent = eventContent;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }
}
