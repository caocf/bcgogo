package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-6
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public class FailedSmsJobDTO {
  private String  id;
   private Long shopId;
  private String receiveMobile;
  private String content;
  private String userId;
  private SenderType sender;
  private int sendTimes;
  private String  lastSendTime;
  private String startTime;
  private String smsId;
  private String status;
  private String reponseReason;
  private Integer type;
  private String name;
  private String vehicleLicense;
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private SmsSendScene smsSendScene;

  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public SenderType getSender() {
    return sender;
  }

  public void setSender(SenderType sender) {
    this.sender = sender;
  }

  public int getSendTimes() {
    return sendTimes;
  }

  public void setSendTimes(int sendTimes) {
    this.sendTimes = sendTimes;
  }

  public String getLastSendTime() {
    return lastSendTime;
  }

  public void setLastSendTime(String lastSendTime) {
    this.lastSendTime = lastSendTime;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
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

  public String getReponseReason() {
    return reponseReason;
  }

  public void setReponseReason(String reponseReason) {
    this.reponseReason = reponseReason;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
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

  public SmsSendKind getSmsSendKind() {
    return smsSendKind;
  }

  public void setSmsSendKind(SmsSendKind smsSendKind) {
    this.smsSendKind = smsSendKind;
  }

  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }
}
