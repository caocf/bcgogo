package com.bcgogo.txn.dto;

import com.bcgogo.utils.DateUtil;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-15
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class QualifiedCredentialsDTO {
  private Long id;
  private Long shopId;
  private String customer;  //托修方
  private Long customerId;
  private String licenseNo;  //车牌号
  private String engineNo;   //发动机号
  private String chassisNumber; //车架号
  private String repairType; //维修类型
  private String repairContractNo;  //维修合同编号
  private String producedMileage;  //出产里程
  private String brand;  //车辆品牌
  private String model;  //车型
  private String qualityInspectors;  //质量检测员
  private String shopName;  //承修单位
  private String startDateStr;  //进厂日期
  private Long startDate;
  private String endDateStr;   //出产日期
  private Long endDate;
  private Long orderId;   //施工单id;
  private String no;  //合格证编号
  private String repairInvoiceNumber;   //维修发票号
  private String travelDate;    //质量保证行驶日期
  private String travelLength;  //质量保证行驶距离

  private boolean print;
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

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getLicenseNo() {
    return licenseNo;
  }

  public void setLicenseNo(String licenseNo) {
    this.licenseNo = licenseNo;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public String getChassisNumber() {
    return chassisNumber;
  }

  public void setChassisNumber(String chassisNumber) {
    this.chassisNumber = chassisNumber;
  }

  public String getRepairType() {
    return repairType;
  }

  public void setRepairType(String repairType) {
    this.repairType = repairType;
  }

  public String getRepairContractNo() {
    return repairContractNo;
  }

  public void setRepairContractNo(String repairContractNo) {
    this.repairContractNo = repairContractNo;
  }

  public String getProducedMileage() {
    return producedMileage;
  }

  public void setProducedMileage(String producedMileage) {
    this.producedMileage = producedMileage;
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

  public String getQualityInspectors() {
    return qualityInspectors;
  }

  public void setQualityInspectors(String qualityInspectors) {
    this.qualityInspectors = qualityInspectors;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public Long getStartDate() {
    return startDate;
  }

  public void setStartDate(Long startDate) {
    this.startDate = startDate;
    if(null != this.startDate)
    {
      this.startDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,this.startDate);
    }
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
    if(null != this.endDate)
    {
      this.endDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN,this.endDate);
    }
  }

  public Long getEndDate() {
    return endDate;
  }

  public void setEndDate(Long endDate) {
    this.endDate = endDate;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public String getRepairInvoiceNumber() {
    return repairInvoiceNumber;
  }

  public void setRepairInvoiceNumber(String repairInvoiceNumber) {
    this.repairInvoiceNumber = repairInvoiceNumber;
  }

  public String getTravelDate() {
    return travelDate;
  }

  public void setTravelDate(String travelDate) {
    this.travelDate = travelDate;
  }

  public String getTravelLength() {
    return travelLength;
  }

  public void setTravelLength(String travelLength) {
    this.travelLength = travelLength;
  }

  public boolean isPrint() {
    return print;
  }

  public void setPrint(boolean print) {
    this.print = print;
  }
}
