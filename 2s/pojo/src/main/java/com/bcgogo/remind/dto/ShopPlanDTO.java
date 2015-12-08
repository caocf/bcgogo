package com.bcgogo.remind.dto;

import com.bcgogo.enums.PlansRemindStatus;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-4-9
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
public class ShopPlanDTO {
  private Long id;
  private Long shopId;
  private String remindType;
  private String content;
  private String customerIds;
  private String customerNames;
  private String customerType;
  private Long remindTime;
  private PlansRemindStatus status;
  private String remindTimeStr;
  private String idStr;
  private String contact;
  private SmsChannel smsChannel;
  private String userInfo;

  public SmsJobDTO toSmsJobDTO() {
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setContent(this.getContent());
    smsJobDTO.setShopId(this.getShopId());
    smsJobDTO.setStatus(this.getStatus().toString());
    smsJobDTO.setSmsChannel(this.getSmsChannel());
    return smsJobDTO;
  }

  public String getIdStr() {
    return String.valueOf(id);
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = String.valueOf(id);
  }

  public PlansRemindStatus getStatus() {
    return status;
  }

  public void setStatus(PlansRemindStatus status) {
    this.status = status;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getRemindType() {
    return remindType;
  }

  public void setRemindType(String remindType) {
    this.remindType = remindType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(String customerIds) {
    this.customerIds = customerIds;
  }

  public String getCustomerNames() {
    return customerNames;
  }

  public void setCustomerNames(String customerNames) {
    this.customerNames = customerNames;
  }

  public String getCustomerType() {
    return customerType;
  }

  public void setCustomerType(String customerType) {
    this.customerType = customerType;
  }

  public Long getRemindTime() {
    return remindTime;
  }

  public void setRemindTime(Long remindTime) {
    this.remindTime = remindTime;
    this.remindTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, remindTime);
  }

    public void setRemindTime(String remindTimeStr) throws ParseException {
    this.remindTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, remindTimeStr);
  }

  public String getRemindTimeStr() {
    if (remindTime != null && StringUtils.isNotBlank(remindTimeStr)) {
      return DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE, remindTime);
    }
    return remindTimeStr;
  }

  public void setRemindTimeStr(String remindTimeStr) {
    this.remindTimeStr = remindTimeStr;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(String userInfo) {
    this.userInfo = userInfo;
  }
}
