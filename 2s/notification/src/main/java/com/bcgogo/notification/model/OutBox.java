package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.notification.StatStatus;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.OutBoxDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

@Entity
@Table(name = "out_box")
public class OutBox extends LongIdentifier {
  private Long shopId;
  private Integer type;
  private String sendMobile;
  private String content;
  private Long userId;
  private SenderType sender;
  private Long priority;
  private String sendTime;
  private String smsId;
  private String status;
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private SmsSendScene smsSendScene;
  private StatStatus statStatus = StatStatus.PENDDING;//统计状态，用于CRM后台统计短信账单

  public OutBox(SmsJob job) {
    this.setContent(job.getContent());
    this.setPriority(job.getPriority());
    this.setSmsId(job.getSmsSendId());
    this.setSender(job.getSender());
    this.setSendMobile(job.getReceiveMobile());
    this.setSendTime(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    this.setShopId(job.getShopId());
    this.setStatus(job.getStatus());
    this.setType(job.getType());
    this.setUserId(job.getUserId());
    this.setSmsChannel(job.getSmsChannel());
    this.setSmsSendKind(job.getSmsSendKind());
    this.setSmsSendScene(job.getSmsSendScene());
  }
  public OutBox(SmsJobDTO job) {
    this.setContent(job.getContent());
    this.setPriority(job.getPriority());
    this.setSmsId(StringUtil.valueOf(job.getSmsId()));
    this.setSender(job.getSender());
    this.setSendMobile(job.getReceiveMobile());
    this.setSendTime(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    this.setShopId(job.getShopId());
    this.setStatus(job.getStatus());
    this.setType(job.getType());
    this.setUserId(job.getUserId());
    this.setSmsChannel(job.getSmsChannel());
    this.setSmsSendKind(job.getSmsSendKind());
    this.setSmsSendScene(job.getSmsSendScene());
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_channel")
  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "type")
  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  @Column(name = "send_mobile", length = 2000)
  public String getSendMobile() {
    return sendMobile;
  }

  public void setSendMobile(String sendMobile) {
    this.sendMobile = sendMobile;
  }

  @Column(name = "content", length = 2000)
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

  @Enumerated(EnumType.STRING)
  @Column(name = "sender")
  public SenderType getSender() {
    return sender;
  }

  public void setSender(SenderType sender) {
    this.sender = sender;
  }

  @Column(name = "priority")
  public Long getPriority() {
    return priority;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  @Column(name = "send_time", length = 50)
  public String getSendTime() {
    return sendTime;
  }

  public void setSendTime(String sendTime) {
    this.sendTime = sendTime;
  }

  @Column(name = "sms_id", length = 50)
  public String getSmsId() {
    return smsId;
  }

  public void setSmsId(String smsId) {
    this.smsId = smsId;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_send_kind")
  public SmsSendKind getSmsSendKind() {
    return smsSendKind;
  }

  public void setSmsSendKind(SmsSendKind smsSendKind) {
    this.smsSendKind = smsSendKind;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="sms_send_scene")
  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="stat_status")
  public StatStatus getStatStatus() {
    return statStatus;
  }

  public void setStatStatus(StatStatus statStatus) {
    this.statStatus = statStatus;
  }

  public OutBox() {
  }

  public OutBox(OutBoxDTO outBoxDTO) {
    this.setId(outBoxDTO.getId());
    this.setContent(outBoxDTO.getContent());
    this.setPriority(outBoxDTO.getPriority());
    this.setSmsId(outBoxDTO.getSmsId());
    this.setSender(outBoxDTO.getSender());
    this.setSendMobile(outBoxDTO.getSendMobile());
    this.setSendTime(outBoxDTO.getSendTime());
    this.setShopId(outBoxDTO.getShopId());
    this.setStatus(outBoxDTO.getStatus());
    this.setType(outBoxDTO.getType());
    this.setUserId(outBoxDTO.getUserId());
    this.setSmsChannel(outBoxDTO.getSmsChannel());
    this.setSmsSendKind(outBoxDTO.getSmsSendKind());
    this.setSmsSendScene(outBoxDTO.getSmsSendScene());
  }

  public OutBox fromDTO(OutBoxDTO outBoxDTO) {
    this.setId(outBoxDTO.getId());
    this.setContent(outBoxDTO.getContent());
    this.setPriority(outBoxDTO.getPriority());
    this.setSmsId(outBoxDTO.getSmsId());
    this.setSender(outBoxDTO.getSender());
    this.setSendMobile(outBoxDTO.getSendMobile());
    this.setSendTime(outBoxDTO.getSendTime());
    this.setShopId(outBoxDTO.getShopId());
    this.setStatus(outBoxDTO.getStatus());
    this.setType(outBoxDTO.getType());
    this.setUserId(outBoxDTO.getUserId());
    this.setSmsChannel(outBoxDTO.getSmsChannel());
    this.setSmsSendKind(outBoxDTO.getSmsSendKind());
    this.setSmsSendScene(outBoxDTO.getSmsSendScene());
    return this;
  }

  public OutBoxDTO toDTO() {
    OutBoxDTO outBoxDTO = new OutBoxDTO();
    outBoxDTO.setId(this.getId());
    outBoxDTO.setContent(this.getContent());
    outBoxDTO.setPriority(this.getPriority());
    outBoxDTO.setSmsId(this.getSmsId());
    outBoxDTO.setSender(this.getSender());
    outBoxDTO.setSendMobile(this.getSendMobile());
    outBoxDTO.setSendTime(this.getSendTime());
    outBoxDTO.setEditDate(this.getCreationDate());
    outBoxDTO.setShopId(this.getShopId());
    outBoxDTO.setStatus(this.getStatus());
    outBoxDTO.setType(this.getType());
    outBoxDTO.setUserId(this.getUserId());
    outBoxDTO.setSmsChannel(this.getSmsChannel());
    outBoxDTO.setSmsSendKind(this.getSmsSendKind());
    outBoxDTO.setSmsSendScene(this.getSmsSendScene());
    return outBoxDTO;
  }


}