package com.bcgogo.remind.dto;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-5
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
public class RemindEventDTO implements Serializable {
  public RemindEventDTO(){}

  private Long id;
  private Long shopId;
  private Long orderId;
  private String orderType;
  private String receiptNo;
  private String material;      //材料内容 - 品名拼接
  private String service;       //服务内容 - 服务名拼接
  private String eventType;     //提醒类型 (维修美容、进销存、欠款、客服)
  private String eventStatus;   //事件状态 （例如维修美容的：PENDING 待交付, LACK 缺料待修, DEBT 还款, FINISH 短信, INCOMING 来料待修）
  private Long remindTime;      //到期提醒时间
  private String remindTimeStr; //格式化提醒时间
  private String remindStatus;  //提醒状态 （reminded已提醒、activity未提醒、canceled已删除）
  private Long customerId;
  private String customerName;
  private Long supplierId;
  private String supplierName;
  private String contact;
  private String mobile;
  private Long objectId;          //可以代表 debtId、vehicleId、serviceId、customerServiceJobId、productId
  private Double debt;            //欠款金额
  private Long appointServiceId; //预约服务ID，用于客户服务提醒
  private Long serviceId;         //服务项目ID，用于会员服务到期提醒
  private String licenceNo;
  private Long oldRemindEventId;  //原有提醒表的ID，如repair_remind_event，inventory_remind_event
  private Long remindMileage;//下次保养里程
  private DeletedType deletedType;

  private String openId;

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public DeletedType getDeletedType() {
    return deletedType;
  }

  public void setDeletedType(DeletedType deletedType) {
    this.deletedType = deletedType;
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

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public String getEventStatus() {
    return eventStatus;
  }

  public void setEventStatus(String eventStatus) {
    this.eventStatus = eventStatus;
  }

  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
    if(remindTime!=null){
      this.remindTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,remindTime);
    }
  }

  public String getRemindTimeStr() {
    return remindTimeStr;
  }

  public void setRemindTimeStr(String remindTimeStr) {
    this.remindTimeStr = remindTimeStr;
  }

  public String getRemindStatus() {
    return remindStatus;
  }

  public void setRemindStatus(String remindStatus) {
    this.remindStatus = remindStatus;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
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

  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public Long getAppointServiceId() {
    return appointServiceId;
  }

  public void setAppointServiceId(Long appointServiceId) {
    this.appointServiceId = appointServiceId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
  }

  public Long getOldRemindEventId() {
    return oldRemindEventId;
  }

  public void setOldRemindEventId(Long oldRemindEventId) {
    this.oldRemindEventId = oldRemindEventId;
  }

  public Long getRemindMileage() {
    return remindMileage;
  }

  public void setRemindMileage(Long remindMileage) {
    this.remindMileage = remindMileage;
  }
}
