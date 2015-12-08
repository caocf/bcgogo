package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 3/5/12
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SmsJobDTO implements Serializable,Cloneable {
  private Long id;
  private List<Long> sendSmsJobIds = new ArrayList<Long>();
  private Long shopId;
  private String receiveMobile;
  private String content;
  private Long userId;
  private String userName;
  private SenderType sender;
  private Long priority;
  private String editDateStr;
  private int sendTimes;
  private Long lastSendTime;
  private Long startTime;
  private String startTimeStr;
  private Long expireTime;
  private String smsSendId;
  private String status;
  private Integer type;
  private String name;
  private String vehicleLicense;
  private String reponseReason;
  private String executeType;//区分是否是全体顾客
  private String contentHTMLStr;
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private String shopName;
  private SmsSendScene smsSendScene;
  private Long smsId;
  private Long customerId;
  private String appUserNo;

  public SmsSendDTO toSmsSendDTO() {
    SmsSendDTO smsSendDTO = new SmsSendDTO();
    smsSendDTO.setId(this.getId());
    smsSendDTO.setContent(this.getContent());
    smsSendDTO.setShopId(this.getShopId());
    smsSendDTO.setReceiveMobile(this.getReceiveMobile());
    smsSendDTO.setUserId(this.getUserId());
    smsSendDTO.setSender(this.getSender());
    smsSendDTO.setPriority(this.getPriority());
    smsSendDTO.setSendTimes(this.getSendTimes());
    smsSendDTO.setLastSendTime(this.getLastSendTime());
    smsSendDTO.setStartTime(this.getStartTime());
    smsSendDTO.setExpireTime(this.getExpireTime());
    smsSendDTO.setSmsId(this.getSmsSendId());
    smsSendDTO.setStatus(this.getStatus());
    smsSendDTO.setType(this.getType());
    smsSendDTO.setName(this.getName());
    smsSendDTO.setShopName(this.getShopName());
    smsSendDTO.setVehicleLicense(this.getVehicleLicense());
    smsSendDTO.setSmsChannel(this.getSmsChannel());
    smsSendDTO.setSmsSendKind(this.getSmsSendKind());
    smsSendDTO.setSmsSendScene(getSmsSendScene());
    return smsSendDTO;
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

//  public String getStartTimeStr() {
//    return DateUtil.dateLongToStr(startTime, DateUtil.DATE_STRING_FORMAT_DEFAULT);
//  }


  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
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

  //页面显示转译
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
    this.setContentHTMLStr(StringEscapeUtils.escapeHtml(content));
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
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

  public String getEditDateStr() {
    return editDateStr;
  }

  public void setEditDateStr(String editDateStr) {
    this.editDateStr = editDateStr;
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
    this.startTimeStr=DateUtil.convertDateLongToDateString(DateUtil.ALL,startTime);
    this.startTime = startTime;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  public String getSmsSendId() {
    return smsSendId;
  }

  public void setSmsSendId(String smsSendId) {
    this.smsSendId = smsSendId;
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

  public String getExecuteType() {
    return executeType;
  }

  public void setExecuteType(String executeType) {
    this.executeType = executeType;
  }

  public String getReponseReason() {
    return reponseReason;
  }

  public void setReponseReason(String reponseReason) {
    this.reponseReason = reponseReason;
  }

  public String getContentHTMLStr() {
    return contentHTMLStr;
  }

  public void setContentHTMLStr(String contentHTMLStr) {
    this.contentHTMLStr = contentHTMLStr;
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

  public SmsSendKind getSmsSendKind() {
    return smsSendKind;
  }

  public void setSmsSendKind(SmsSendKind smsSendKind) {
    this.smsSendKind = smsSendKind;
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

  public Long getSmsId() {
    return smsId;
  }

  public void setSmsId(Long smsId) {
    this.smsId = smsId;
  }

   public SmsJobDTO clone() throws CloneNotSupportedException{
    return (SmsJobDTO)super.clone();
  }

   public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }
}
