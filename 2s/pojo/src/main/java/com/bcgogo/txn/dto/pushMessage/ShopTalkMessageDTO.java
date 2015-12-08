package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-26
 * Time: 下午2:53
 */
public class ShopTalkMessageDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private String fromUserName;
  private String appUserNo;
  private String vehicleNo;
  private Long customerId;
  private String customerIdStr;
  private String customer;
  private Long sendTime;
  private String sendTimeStr;
  private String content;
  private Long replyTime;
  private String replyTimeStr;
  private String replyContent;
  private String customerName;
  private String customerMobile;
  private String vehicleContact;
  private String vehicleMobile;

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

  public String getVehicleContact() {
    return vehicleContact;
  }

  public void setVehicleContact(String vehicleContact) {
    this.vehicleContact = vehicleContact;
  }

  public String getVehicleMobile() {
    return vehicleMobile;
  }

  public void setVehicleMobile(String vehicleMobile) {
    this.vehicleMobile = vehicleMobile;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getFromUserName() {
    return fromUserName;
  }

  public void setFromUserName(String fromUserName) {
    this.fromUserName = fromUserName;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
    this.customerIdStr= StringUtil.valueOf(customerId);
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
    this.sendTimeStr= DateUtil.convertDateLongToString(sendTime,DateUtil.ALL);
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getReplyTime() {
    return replyTime;
  }

  public void setReplyTime(Long replyTime) {
    this.replyTime = replyTime;
    this.replyTimeStr= DateUtil.convertDateLongToString(replyTime,DateUtil.ALL);
  }

  public String getReplyTimeStr() {
    return replyTimeStr;
  }

  public void setReplyTimeStr(String replyTimeStr) {
    this.replyTimeStr = replyTimeStr;
  }

  public String getReplyContent() {
    return replyContent;
  }

  public void setReplyContent(String replyContent) {
    this.replyContent = replyContent;
  }
}
