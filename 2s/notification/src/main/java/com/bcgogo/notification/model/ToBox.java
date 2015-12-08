package com.bcgogo.notification.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.ToBoxDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@Entity
@Table(name = "to_box")
public class ToBox extends LongIdentifier {
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

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "type")
  public Long getType() {
    return type;
  }

  public void setType(Long type) {
    this.type = type;
  }

  @Column(name = "receive_mobile", length = 20)
  public String getReceiveMobile() {
    return receiveMobile;
  }

  public void setReceiveMobile(String receiveMobile) {
    this.receiveMobile = receiveMobile;
  }

  @Column(name = "content", length = 500)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "sender", length = 20)
  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  @Column(name = "priority")
  public Long getPriority() {
    return priority;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  @Column(name = "send_times")
  public Long getSendTimes() {
    return sendTimes;
  }

  public void setSendTimes(Long sendTimes) {
    this.sendTimes = sendTimes;
  }

  @Column(name = "last_send_time")
  public Calendar getLastSendTime() {
    return lastSendTime;
  }

  public void setLastSendTime(Calendar lastSendTime) {
    this.lastSendTime = lastSendTime;
  }

  @Column(name = "last_send_channel")
  public Long getLastSendChannel() {
    return lastSendChannel;
  }

  public void setLastSendChannel(Long lastSendChannel) {
    this.lastSendChannel = lastSendChannel;
  }

  @Column(name = "start_time")
  public Calendar getStartTime() {
    return startTime;
  }

  public void setStartTime(Calendar startTime) {
    this.startTime = startTime;
  }

  @Column(name = "expire_time")
  public Calendar getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Calendar expireTime) {
    this.expireTime = expireTime;
  }

  @Column(name = "raw_data")
  public String getRawData() {
    return rawData;
  }

  public void setRawData(String rawData) {
    this.rawData = rawData;
  }

  @Column(name = "sms_id", length = 50)
  public String getSmsId() {
    return smsId;
  }

  public void setSmsId(String smsId) {
    this.smsId = smsId;
  }

  @Column(name = "status")
  public Long getStatus() {
    return status;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

  public ToBox() {
  }

  public ToBox(ToBoxDTO toBoxDTO) {
    this.setId(toBoxDTO.getId());
    this.setContent(toBoxDTO.getContent());
    this.setExpireTime(toBoxDTO.getExpireTime());
    this.setLastSendChannel(toBoxDTO.getLastSendChannel());
    this.setLastSendTime(toBoxDTO.getLastSendTime());
    this.setPriority(toBoxDTO.getPriority());
    this.setReceiveMobile(toBoxDTO.getReceiveMobile());
    this.setSendTimes(toBoxDTO.getSendTimes());
    this.setSender(toBoxDTO.getSender());
    this.setSendTimes(toBoxDTO.getSendTimes());
    this.setShopId(toBoxDTO.getShopId());
    this.setType(toBoxDTO.getType());
    this.setStartTime(toBoxDTO.getStartTime());
    this.setUserId(toBoxDTO.getUserId());
    this.setSmsId(toBoxDTO.getSmsId());
    this.setRawData(toBoxDTO.getRawData());
    this.setStatus(toBoxDTO.getStatus());
  }

  public ToBox fromDTO(ToBoxDTO toBoxDTO) {
    this.setId(toBoxDTO.getId());
    this.setContent(toBoxDTO.getContent());
    this.setExpireTime(toBoxDTO.getExpireTime());
    this.setLastSendChannel(toBoxDTO.getLastSendChannel());
    this.setLastSendTime(toBoxDTO.getLastSendTime());
    this.setPriority(toBoxDTO.getPriority());
    this.setReceiveMobile(toBoxDTO.getReceiveMobile());
    this.setSendTimes(toBoxDTO.getSendTimes());
    this.setSender(toBoxDTO.getSender());
    this.setSendTimes(toBoxDTO.getSendTimes());
    this.setShopId(toBoxDTO.getShopId());
    this.setType(toBoxDTO.getType());
    this.setStartTime(toBoxDTO.getStartTime());
    this.setUserId(toBoxDTO.getUserId());
    this.setSmsId(toBoxDTO.getSmsId());
    this.setRawData(toBoxDTO.getRawData());
    this.setStatus(toBoxDTO.getStatus());
    return this;
  }

  public ToBoxDTO toDTO() {
    ToBoxDTO toBoxDTO = new ToBoxDTO();
    toBoxDTO.setId(this.getId());
    toBoxDTO.setType(this.getType());
    toBoxDTO.setContent(this.getContent());
    toBoxDTO.setExpireTime(this.getExpireTime());
    toBoxDTO.setLastSendChannel(this.getLastSendChannel());
    toBoxDTO.setLastSendTime(this.getLastSendTime());
    toBoxDTO.setPriority(this.getPriority());
    toBoxDTO.setReceiveMobile(this.getReceiveMobile());
    toBoxDTO.setSender(this.getSender());
    toBoxDTO.setSendTimes(this.getSendTimes());
    toBoxDTO.setShopId(this.getShopId());
    toBoxDTO.setStartTime(this.getStartTime());
    toBoxDTO.setStatus(this.getStatus());
    toBoxDTO.setUserId(this.getUserId());
    toBoxDTO.setSmsId(this.getSmsId());
    toBoxDTO.setRawData(this.getRawData());
    return toBoxDTO;
  }

}