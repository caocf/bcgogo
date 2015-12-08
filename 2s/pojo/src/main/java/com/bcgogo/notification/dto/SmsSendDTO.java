package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.utils.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 3/28/12
 * Time: 5:16 PM
 */
public class SmsSendDTO implements Serializable {
  private Long id;
  private List<Long> sendSmsJobIds = new ArrayList<Long>();
  private Long shopId;
  private String receiveMobile;
  private String content;
  private Long userId;
  private SenderType sender;  //区分注册短信
  private Long priority;
  private int sendTimes;
  private Long lastSendTime;
  private Long startTime;
  private Long expireTime;
  private String smsId;
  private String status;
  private String responseCode;
  private Integer type;
  private String name;
  private String vehicleLicense;
  private String shopName;
  private int mobileNum;
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private SmsSendScene smsSendScene;
  @Deprecated
  private Long groupIdTemp;


  public SmsSendKind getSmsSendKind() {
    return smsSendKind;
  }

  public void setSmsSendKind(SmsSendKind smsSendKind) {
    this.smsSendKind = smsSendKind;
  }

  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVehicleLicense() {
    return vehicleLicense;
  }

  public void setVehicleLicense(String vehicleLicense) {
    this.vehicleLicense = vehicleLicense;
  }

  public String getSmsType() {
    if (this.getType() != null) {
      switch (this.getType().intValue()) {
        case 1003: {
          return "欠款";
        }
        case 1: {
          return "生日";
        }
      }
    }
    return "未分类";
  }

  public String getStartTimeStr() {
    return DateUtil.dateLongToStr(startTime, DateUtil.DATE_STRING_FORMAT_DEFAULT);
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getReceiveMobile() {
    return receiveMobile;
  }

  public void setReceiveMobile(String receiveMobile) {
    this.receiveMobile = receiveMobile;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public SenderType getSender() {
    return sender;
  }

  public void setSender(SenderType sender) {
    this.sender = sender;
  }

  public Long getPriority() {
    return priority;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  public int getSendTimes() {
    return sendTimes;
  }

  public void setSendTimes(int sendTimes) {
    this.sendTimes = sendTimes;
  }

  public Long getLastSendTime() {
    return lastSendTime;
  }

  public void setLastSendTime(Long lastSendTime) {
    this.lastSendTime = lastSendTime;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  public String getSmsId() {
    return smsId;
  }

  public void setSmsId(String smsId) {
    this.smsId = smsId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }

  public int getMobileNum() {
    return mobileNum;
  }

  public void setMobileNum(int mobileNum) {
    this.mobileNum = mobileNum;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  public List<Long> getSendSmsJobIds() {
    return sendSmsJobIds;
  }

  public void setSendSmsJobIds(List<Long> sendSmsJobIds) {
    this.sendSmsJobIds = sendSmsJobIds;
  }
}
