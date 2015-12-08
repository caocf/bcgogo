package com.bcgogo.stat.dto;

import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhuj
 * Date: 13-5-21
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
public class DepositStatConditionDTO {

  private Long shopId;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  // 起始时间
  private Long startTime;
  // 结束时间
  private Long endTime;
  private String startTimeStr;
  private String endTimeStr;
  // 用户姓名
  private String customerName;
  // 用户手机
  private String customerMobile;
  private Long customerId;
  private Long supplierId;
  // 收款/取用标识 @see InOutFlag
  private Long inOut;
  // 预存款、预付款类型标识
  private String type;
  private String supplierName;
  private String supplierMobile;
  private String sortName;
  private String sortFlag;

  public List<Long> getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(List<Long> customerIds) {
    this.customerIds = customerIds;
  }

  public List<Long> getSupplierIds() {
    return supplierIds;
  }

  public void setSupplierIds(List<Long> supplierIds) {
    this.supplierIds = supplierIds;
  }

  private List<Long> customerIds;
  private List<Long> supplierIds;

  public String getSortName() {
    return sortName;
  }

  public void setSortName(String sortName) {
    this.sortName = sortName;
  }

  public String getSortFlag() {
    return sortFlag;
  }

  public void setSortFlag(String sortFlag) {
    this.sortFlag = sortFlag;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getSupplierMobile() {
    return supplierMobile;
  }

  public void setSupplierMobile(String supplierMobile) {
    this.supplierMobile = supplierMobile;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }


  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
    try {
      this.startTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", startTimeStr);
    } catch (ParseException e) {

    }
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
    try {
      this.endTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", endTimeStr);
    } catch (ParseException e) {

    }
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }


  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getInOut() {
    return inOut;
  }

  public void setInOut(Long inOut) {
    this.inOut = inOut;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
