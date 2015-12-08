package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;


public class OutBoxDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Integer type;
  private String sendMobile;
  private String content;
  private Long userId;
  private String userName;
  private SenderType sender;
  private Long priority;
  private String sendTime;
  private Long editDate;
  private String smsId;
  private String status;
  private String name;
  private String licenceNo;
  private String sendTimeStr;
  private String smsType;
  private String contentHTMLStr;
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private SmsSendScene smsSendScene;

  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }


  public String getSmsType() {
    if (this.getType() != null) {
      switch (this.getType().intValue()) {
        case 0: {
          return "欠款";
        }
        case 1: {
          return "生日";
        }
      }
    }
    return "未分类";
  }

  public void setSmsType(String smsType) {
    this.smsType = smsType;
  }

  public String getSendTimeStr() {
    return sendTime;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String licenceNo) {
    this.licenceNo = licenceNo;
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

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getSendMobile() {
    return sendMobile;
  }

  public void setSendMobile(String sendMobile) {
    this.sendMobile = sendMobile;
  }

  public String getContent() {
    return content;

  }

  public void setContent(String content) {
    this.content = content;
    this.contentHTMLStr = StringEscapeUtils.escapeHtml(content);
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

  public String getSendTime() {
    return sendTime;
  }

  public void setSendTime(String sendTime) {
    this.sendTime = sendTime;
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

  public String getContentHTMLStr() {
    return contentHTMLStr;
  }

  public void setContentHTMLStr(String contentHTMLStr) {
    this.contentHTMLStr = contentHTMLStr;
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
