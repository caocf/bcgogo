package com.bcgogo.notification.dto;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: lijie
 * Date: 11-11-10
 * Time: 上午11:25
 * To change this template use File | Settings | File Templates.
 */
public class ToBoxDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Long type;
  private String receiveMobile;
  private String content;
  private Long userId;
  private String sender;
  private Long priority;
  private Long sendTimes;
  private Calendar lastSendTime;
  private Long lastSendChannel;
  private Calendar startTime;
  private Calendar expireTime;
  private String rawData;
  private String smsId;
  private Long status;

  private String licenceNo;
  private String name;
  private String sendTime;

  private String smsType;

    public String getSmsType() {
        if(this.getType()!=null){
            switch(this.getType().intValue()){
                case 0:{
                    return "欠款";
                }
                case 1:{
                    return "生日";
                }
            }
        }
        return "未分类";
    }

    public void setSmsType(String smsType) {
        this.smsType = smsType;
    }

    public String getSendTime() {
        if(startTime==null){
            return "";
        }
        int year = this.getStartTime().get(Calendar.YEAR);
        int month =  this.getStartTime().get(Calendar.MONTH)+1;
        int day = this.getStartTime().get(Calendar.DAY_OF_MONTH);
        int hour = this.getStartTime().get(Calendar.HOUR_OF_DAY);
        int minute = this.getStartTime().get(Calendar.MINUTE);
        int second = this.getStartTime().get(Calendar.SECOND);
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getLicenceNo() {
    return licenceNo;
  }

  public void setLicenceNo(String liceneceNo) {
    this.licenceNo = liceneceNo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Long getType() {
    return type;
  }

  public void setType(Long type) {
    this.type = type;
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

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Long getPriority() {
    return priority;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  public Long getSendTimes() {
    return sendTimes;
  }

  public void setSendTimes(Long sendTimes) {
    this.sendTimes = sendTimes;
  }

  public Calendar getLastSendTime() {
    return lastSendTime;
  }

  public void setLastSendTime(Calendar lastSendTime) {
    this.lastSendTime = lastSendTime;
  }

  public Long getLastSendChannel() {
    return lastSendChannel;
  }

  public void setLastSendChannel(Long lastSendChannel) {
    this.lastSendChannel = lastSendChannel;
  }

  public Calendar getStartTime() {
    return startTime;
  }

  public void setStartTime(Calendar startTime) {
    this.startTime = startTime;
  }

  public Calendar getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Calendar expireTime) {
    this.expireTime = expireTime;
  }

  public String getRawData() {
    return rawData;
  }

  public void setRawData(String rawData) {
    this.rawData = rawData;
  }

  public String getSmsId() {
    return smsId;
  }

  public void setSmsId(String smsId) {
    this.smsId = smsId;
  }

  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }
}
