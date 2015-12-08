package com.bcgogo.txn.dto;

import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * 缺料维修保存虚拟字段的临时DTO
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-11
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
public class LackMaterialDTO implements Serializable {
  public LackMaterialDTO() {
  }

  private String service;
  private Double total;
  private Long id;
  private String vechicle;      //
  private String customer;      //
  private String mobile;        //
  private double amount;       //
  private Long startDate;   //
  private Long endDate;     //
  private Long productId;
  private String productName;   //
  private String productSpec; //
  private String productModel;  //
  private String vehicleBrand;     //
  private String vehicleModel;        //
  private String vehicleYear;         //
  private String vehicleEngine;        //
  private String startDateStr;
  private String endDateStr;
  private String unit;

  public String getStartDateStr() {
    if (startDate != null) {
      return DateUtil.convertDateLongToDateString("yyyy-MM-dd", startDate);
    } else {
      return startDateStr;
    }
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    if (endDate != null) {
      return DateUtil.convertDateLongToDateString("yyyy-MM-dd", endDate);
    } else {
      return endDateStr;
    }
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVechicle() {
    return vechicle;
  }

  public void setVechicle(String vechicle) {
    this.vechicle = vechicle;
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

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
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

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }
}
