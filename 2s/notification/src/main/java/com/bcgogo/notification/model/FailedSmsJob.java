package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 3/4/12
 * Time: 9:43 PM
 */
@Entity
@Table(name = "failed_sms_job")
public class FailedSmsJob extends LongIdentifier {
  private Long shopId;
  private String receiveMobile;
  private String content;
  private Long userId;
  private SenderType sender;
  private int sendTimes;
  private Long lastSendTime;
  private Long startTime;
  private String smsId;
  private String status;
  private String reponseReason;
  private Integer type;
  private String name;
  private String vehicleLicense;
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private SmsSendScene smsSendScene;

  public FailedSmsJob(SmsSendDTO smsSendDTO) {
    this.setContent(smsSendDTO.getContent());
    this.setShopId(smsSendDTO.getShopId());
    this.setReceiveMobile(smsSendDTO.getReceiveMobile());
    this.setUserId(smsSendDTO.getUserId());
    this.setSender(smsSendDTO.getSender());
    this.setSendTimes(smsSendDTO.getSendTimes());
    this.setLastSendTime(smsSendDTO.getLastSendTime());
    this.setStartTime(smsSendDTO.getStartTime());
    this.setSmsId(smsSendDTO.getSmsId());
    this.setStatus(smsSendDTO.getStatus());
    this.setType(smsSendDTO.getType());
    this.setName(smsSendDTO.getName());
    this.setVehicleLicense(smsSendDTO.getVehicleLicense());
    this.setSmsChannel(smsSendDTO.getSmsChannel());
    this.setSmsSendKind(smsSendDTO.getSmsSendKind());
    this.setSmsSendScene(smsSendDTO.getSmsSendScene());
  }

  public FailedSmsJob(SmsJobDTO smsJobDTO) {
    this.setContent(smsJobDTO.getContent());
    this.setShopId(smsJobDTO.getShopId());
    this.setReceiveMobile(smsJobDTO.getReceiveMobile());
    this.setUserId(smsJobDTO.getUserId());
    this.setSender(smsJobDTO.getSender());
    this.setSendTimes(smsJobDTO.getSendTimes());
    this.setLastSendTime(smsJobDTO.getLastSendTime());
    this.setStartTime(smsJobDTO.getStartTime());
    this.setSmsId(StringUtil.valueOf(smsJobDTO.getSmsId()));
    this.setStatus(smsJobDTO.getStatus());
    this.setType(smsJobDTO.getType());
    this.setName(smsJobDTO.getName());
    this.setVehicleLicense(smsJobDTO.getVehicleLicense());
    this.setSmsChannel(smsJobDTO.getSmsChannel());
    this.setSmsSendKind(smsJobDTO.getSmsSendKind());
    this.setSmsSendScene(smsJobDTO.getSmsSendScene());
    this.reponseReason = smsJobDTO.getReponseReason();
  }

  public FailedSmsJob(SmsJob job) {
    this.sender = job.getSender();
    this.content = job.getContent();
    this.shopId = job.getShopId();
    this.receiveMobile = job.getReceiveMobile();
    this.userId = job.getUserId();
    this.sendTimes = job.getSendTimes();
    this.lastSendTime = job.getLastSendTime();
    this.startTime = job.getStartTime();
    this.smsId = job.getSmsSendId();
    this.status = job.getStatus();
    this.type = job.getType();
    this.reponseReason = job.getReponseReason();
    this.smsChannel = job.getSmsChannel();
    this.setSmsSendKind(job.getSmsSendKind());
    this.setSmsSendScene(job.getSmsSendScene());
  }

  public SmsJob toSmsJob() {
    SmsJob smsJob = new SmsJob();
    smsJob.setSender(this.getSender());
    smsJob.setContent(this.getContent());
    smsJob.setShopId(this.getShopId());
    smsJob.setReceiveMobile(this.getReceiveMobile());
    smsJob.setUserId(this.getUserId());
    smsJob.setStartTime(this.getStartTime());
    smsJob.setLastSendTime(this.getLastSendTime());
    smsJob.setStartTime(this.getStartTime());
    smsJob.setSmsSendId(this.getSmsId());
    smsJob.setStatus(this.getStatus());
    smsJob.setType(this.getType());
    smsJob.setReponseReason(this.getReponseReason());
    smsJob.setSmsChannel(this.getSmsChannel());
    smsJob.setSmsSendKind(this.getSmsSendKind());
    smsJob.setSmsSendScene(this.getSmsSendScene());
    return smsJob;
  }

  public FailedSmsJobDTO toDTO() {
    FailedSmsJobDTO failedSmsJobDTO = new FailedSmsJobDTO();
    failedSmsJobDTO.setId(this.getId() == null ? "" : this.getId().toString());
    failedSmsJobDTO.setShopId(this.getShopId());
    failedSmsJobDTO.setReceiveMobile(this.getReceiveMobile());
    failedSmsJobDTO.setContent(this.getContent());
    failedSmsJobDTO.setUserId(this.getUserId() == null ? "" : this.getUserId().toString());
    failedSmsJobDTO.setSender(this.getSender());
    failedSmsJobDTO.setSendTimes(this.getSendTimes());
    failedSmsJobDTO.setLastSendTime(this.getLastSendTime() == null ? "" : this.getLastSendTime().toString());
    failedSmsJobDTO.setStartTime(this.getStartTime() == null ? "" : this.getStartTime().toString());
    failedSmsJobDTO.setSmsId(this.getSmsId());
    failedSmsJobDTO.setStatus(this.getStatus());
    failedSmsJobDTO.setReponseReason(this.getReponseReason());
    failedSmsJobDTO.setType(this.getType());
    failedSmsJobDTO.setName(this.getName());
    failedSmsJobDTO.setVehicleLicense(this.getVehicleLicense());
    failedSmsJobDTO.setContent(this.getContent());
    failedSmsJobDTO.setSmsChannel(this.getSmsChannel());
    failedSmsJobDTO.setSmsSendKind(this.getSmsSendKind());
    failedSmsJobDTO.setSmsSendScene(this.getSmsSendScene());
    return failedSmsJobDTO;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_channel")
  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }

  public FailedSmsJob() {
  }


  @Enumerated(EnumType.STRING)
  @Column(name = "sms_send_kind")
  public SmsSendKind getSmsSendKind() {
    return smsSendKind;
  }

  public void setSmsSendKind(SmsSendKind smsSendKind) {
    this.smsSendKind = smsSendKind;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "receive_mobile", length = 2000)
  public String getReceiveMobile() {
    return receiveMobile;
  }

  public void setReceiveMobile(String receiveMobile) {
    this.receiveMobile = receiveMobile;
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
  @Column(name = "sender", length = 20)
  public SenderType getSender() {
    return sender;
  }

  public void setSender(SenderType sender) {
    this.sender = sender;
  }


  @Column(name = "type")
  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  @Column(name = "send_times")
  public Integer getSendTimes() {
    return sendTimes;
  }

  public void setSendTimes(Integer sendTimes) {
    this.sendTimes = sendTimes;
  }

  @Column(name = "last_send_time")
  public Long getLastSendTime() {
    return lastSendTime;
  }

  public void setLastSendTime(Long lastSendTime) {
    this.lastSendTime = lastSendTime;
  }

  @Column(name = "start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
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


  @Column(name = "name", length = 20)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "vehicle_license", length = 20)
  public String getVehicleLicense() {
    return vehicleLicense;
  }

  @Column(name = "reponse_reason", length = 500)
  public String getReponseReason() {
    return reponseReason;
  }

  public void setReponseReason(String reponseReason) {
    this.reponseReason = reponseReason;
  }

  public void setVehicleLicense(String vehicleLicense) {
    this.vehicleLicense = vehicleLicense;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="sms_send_scene")
  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }
}

