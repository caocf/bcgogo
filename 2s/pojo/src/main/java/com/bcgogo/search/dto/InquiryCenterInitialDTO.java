package com.bcgogo.search.dto;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-8-24
 * Time: 上午10:10
 * 进销存 跳转 到查询中心时  把一些初始查询条件带到 查询中心
 * notice 此dto中的数据被转义了
 */
public class InquiryCenterInitialDTO {
  private String pageType;
  //客户或 供应商信息
  private String customerOrSupplier;
  private String contact;
  private String mobile;
  //客户车辆信息
  private Long vehicleId;
  private String vehicleNumber;
  private String vehicleModel;
  private String vehicleBrand;
  private String vehicleColor;
  //商品信息信息
  private String commodityCode;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private String productVehicleModel;
  private String productVehicleBrand;
  //销售
  private String salesman;
  private String receiptNo;

  //会员
  private String memberNo;


  private String sort;

  private String startDateStr = "今天";
  private String endDateStr = "今天";
  private String orderTypes;

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public String getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(String orderTypes) {
    this.orderTypes = orderTypes;
  }

  public String getEndDateStr() {

    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getPageType() {
    return pageType;
  }

  public void setPageType(String pageType) {
    this.pageType = pageType;
  }

  public String getCustomerOrSupplier() {
    return customerOrSupplier;
  }

  public void setCustomerOrSupplier(String customerOrSupplier) {
    this.customerOrSupplier = customerOrSupplier;
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

  public String getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = StringEscapeUtils.escapeHtml(vehicleNumber);
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = StringEscapeUtils.escapeHtml(vehicleModel);
  }

  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = StringEscapeUtils.escapeHtml(vehicleBrand);
  }

  public String getSalesman() {
    return salesman;
  }

  public void setSalesman(String salesman) {
    this.salesman = salesman;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = StringEscapeUtils.escapeHtml(productName);
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = StringEscapeUtils.escapeHtml(productBrand);
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = StringEscapeUtils.escapeHtml(productSpec);
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = StringEscapeUtils.escapeHtml(productModel);
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = StringEscapeUtils.escapeHtml(productVehicleModel);
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = StringEscapeUtils.escapeHtml(productVehicleBrand);
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public String getVehicleColor() {
    return vehicleColor;
  }

  public void setVehicleColor(String vehicleColor) {
    this.vehicleColor = vehicleColor;
  }
}
