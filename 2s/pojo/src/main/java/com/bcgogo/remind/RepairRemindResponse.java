package com.bcgogo.remind;

import com.bcgogo.utils.DateUtil;
import com.bcgogo.enums.RepairRemindEventTypes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: lijie
 * Date: 11-11-17
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
public class RepairRemindResponse {

  private RepairRemindEventTypes eventType;
  private String remindType;
  private Long estimateTime;
  private String estimateTimeStr;
  private String licenceNo;
  private String name;
  private String mobile;
  private String brand;
  private String model;
  private String content;
  private Calendar comeTime;
  private String comeTimeStr;
  private double totalMoney;
  private String remindWay;
  private Long customerId;
  private String customerIdStr;
  private Long repairOrderId;
  private String repairOrderIdStr;
  private String productIds[];
  private String productName;
  private String productIds1;
  private String receiptNo;

  public String getProductIds1() {
    return productIds1;
  }

  public void setProductIds1(String productIds1) {
    this.productIds1 = productIds1;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }


  public String[] getProductIds() {
    return productIds;
  }

  public void setProductIds(String[] productIds) {
    this.productIds = productIds;
  }

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
    if(repairOrderId !=null){
      repairOrderIdStr = String.valueOf(repairOrderId);
    }else {
      repairOrderIdStr = null;
    }
  }

  public String getRepairOrderIdStr() {
    return repairOrderIdStr;
  }

  public void setRepairOrderIdStr(String repairOrderIdStr) {
    this.repairOrderIdStr = repairOrderIdStr;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    if (customerIdStr == null) {
      customerIdStr = String.valueOf(customerId);
    }
    this.customerId = customerId;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getEstimateTimeStr() {
    return this.estimateTimeStr;
  }

  public void setEstimateTimeStr(String estimateTimeStr) {
    this.estimateTimeStr = estimateTimeStr;
  }

  public String getComeTimeStr() {
    Calendar time = this.getComeTime();
    if (time == null) {
      return null;
    }
    Date d = new Date(time.getTimeInMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    return sdf.format(d);
  }

  public void setComeTimeStr(String comeTimeStr) {
    this.comeTimeStr = comeTimeStr;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getRemindType() {
    return remindType;
  }

  public void setRemindType(String remindType) {
    this.remindType = remindType;
  }

  public Long getEstimateTime() {
    return estimateTime;
  }

  public void setEstimateTime(Long estimateTime) {
    this.estimateTime = estimateTime;
    this.estimateTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY,estimateTime);
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Calendar getComeTime() {
    return comeTime;
  }

  public void setComeTime(Calendar comeTime) {
    this.comeTime = comeTime;
  }

  public double getTotalMoney() {
    return totalMoney;
  }

  public void setTotalMoney(double totalMoney) {
    this.totalMoney = totalMoney;
  }

  public String getRemindWay() {
    return remindWay;
  }

  public void setRemindWay(String remindWay) {
    this.remindWay = remindWay;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public RepairRemindEventTypes getEventType() {
    return eventType;
  }

  public void setEventType(RepairRemindEventTypes eventType) {
    this.eventType = eventType;
  }
}
