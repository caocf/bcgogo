package com.bcgogo.notification.model;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.SmsConstant;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 3/4/12
 * Time: 12:31 PM
 */
@Entity
@Table(name = "sms_job")
public class SmsJob extends LongIdentifier {
  private Long shopId;
  private String receiveMobile;
  private String content;
  private Long userId;
  private SenderType sender;
  private Long priority;
  private int sendTimes;
  private Long lastSendTime;
  private Long startTime;
  private Long expireTime;
  private String smsSendId;
  private Long smsId;
  private String status;
  private Integer type;
  private String name;
  private String vehicleLicense;
  private String reponseReason;
  private String executeType;//区分是否是全体顾客
  private SmsChannel smsChannel;
  private SmsSendKind smsSendKind;
  private String shopName;
  private SmsSendScene smsSendScene;
  private SmsType smsType;
  private Long customerId;
  private String appUserNo;

  public SmsJob() {
  }

  public SmsJob fromDTO(SmsJobDTO smsJobDTO) {
    if (smsJobDTO == null)
      return this;
    this.receiveMobile = smsJobDTO.getReceiveMobile();
    this.shopId = smsJobDTO.getShopId();
    this.smsId=smsJobDTO.getSmsId();
    this.content = smsJobDTO.getContent();
    this.userId = smsJobDTO.getUserId();
    this.sender = smsJobDTO.getSender();
    this.priority = smsJobDTO.getPriority();
    this.sendTimes = smsJobDTO.getSendTimes();
    this.lastSendTime = smsJobDTO.getLastSendTime();
    this.startTime = smsJobDTO.getStartTime();
    this.expireTime = smsJobDTO.getExpireTime();
    this.status = smsJobDTO.getStatus();
    this.type = smsJobDTO.getType();
    this.name = smsJobDTO.getName();
    this.vehicleLicense = smsJobDTO.getVehicleLicense();
    this.reponseReason = smsJobDTO.getReponseReason();
    this.executeType = smsJobDTO.getExecuteType(); //区分
    this.smsChannel = smsJobDTO.getSmsChannel();
    this.setId(smsJobDTO.getId());
    this.setSmsSendKind(smsJobDTO.getSmsSendKind());
    this.setShopName(smsJobDTO.getShopName());
    this.smsSendScene = smsJobDTO.getSmsSendScene();
    this.customerId=smsJobDTO.getCustomerId();
    this.appUserNo=smsJobDTO.getAppUserNo();
    return this;
  }

  public SmsJobDTO toDTO() {
    SmsJobDTO smsJobDTO = new SmsJobDTO();
    smsJobDTO.setContent(content);
    smsJobDTO.setShopId(shopId);
    smsJobDTO.setReceiveMobile(receiveMobile);
    smsJobDTO.setUserId(userId);
    smsJobDTO.setSender(sender);
    smsJobDTO.setPriority(priority);
    smsJobDTO.setSendTimes(sendTimes);
    smsJobDTO.setLastSendTime(lastSendTime);
    smsJobDTO.setStartTime(startTime);
    smsJobDTO.setExpireTime(expireTime);
    smsJobDTO.setSmsSendId(smsSendId);
    smsJobDTO.setStatus(status);
    smsJobDTO.setType(type);
    smsJobDTO.setName(name);
    smsJobDTO.setVehicleLicense(vehicleLicense);
    smsJobDTO.setExecuteType(executeType);
    smsJobDTO.setReponseReason(reponseReason);
    smsJobDTO.setSmsChannel(smsChannel);
    smsJobDTO.setId(this.getId());
    smsJobDTO.setSmsSendKind(this.getSmsSendKind());
    smsJobDTO.setShopName(this.getShopName());
    smsJobDTO.setSmsSendScene(smsSendScene);
    smsJobDTO.setEditDateStr(DateUtil.convertDateLongToDateString(DateUtil.ALL,this.getCreationDate()));
    smsJobDTO.setSmsId(smsId);
    smsJobDTO.setCustomerId(customerId);
    smsJobDTO.setAppUserNo(appUserNo);
    return smsJobDTO;
  }

  public SmsJob(SmsSendDTO smsSendDTO) {
    this.setContent(smsSendDTO.getContent());
    this.setShopId(smsSendDTO.getShopId());
    this.setReceiveMobile(smsSendDTO.getReceiveMobile());
    this.setUserId(smsSendDTO.getUserId());
    this.setSender(smsSendDTO.getSender());
    this.setPriority(smsSendDTO.getPriority());
    this.setSendTimes(smsSendDTO.getSendTimes());
    this.setLastSendTime(smsSendDTO.getLastSendTime());
    this.setStartTime(smsSendDTO.getStartTime());
    this.setExpireTime(smsSendDTO.getExpireTime());
    this.setSmsSendId(smsSendDTO.getSmsId());
    this.setStatus(smsSendDTO.getStatus());
    this.setType(smsSendDTO.getType());
    this.setName(smsSendDTO.getName());
    this.setVehicleLicense(smsSendDTO.getVehicleLicense());
    this.setSmsChannel(smsSendDTO.getSmsChannel());
    this.setSmsSendKind(smsSendDTO.getSmsSendKind());
    this.setShopName(smsSendDTO.getShopName());
    this.setSmsSendScene(smsSendDTO.getSmsSendScene());
  }

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
    smsSendDTO.setVehicleLicense(this.getVehicleLicense());
    smsSendDTO.setSmsChannel(this.getSmsChannel());
    smsSendDTO.setShopName(this.getShopName());
    smsSendDTO.setSmsSendScene(this.getSmsSendScene());
    return smsSendDTO;
  }

  @Override
  public SmsJob clone() throws CloneNotSupportedException {
    SmsJob newJob = new SmsJob();
    newJob.setContent(content);
    newJob.setShopId(shopId);
    newJob.setReceiveMobile(receiveMobile);
    newJob.setUserId(userId);
    newJob.setSmsId(smsId);
    newJob.setSender(sender);
    newJob.setPriority(priority);
    newJob.setSendTimes(sendTimes);
    newJob.setLastSendTime(lastSendTime);
    newJob.setStartTime(startTime);
    newJob.setExpireTime(expireTime);
    newJob.setSmsSendId(smsSendId);
    newJob.setStatus(status);
    newJob.setType(type);
    newJob.setName(name);
    newJob.setVehicleLicense(vehicleLicense);
    newJob.setExecuteType(executeType);
    newJob.setReponseReason(reponseReason);
    newJob.setSmsChannel(smsChannel);
    newJob.setSmsChannel(this.getSmsChannel());
    newJob.setShopName(this.getShopName());
    newJob.setSmsSendScene(this.getSmsSendScene());
    newJob.setCustomerId(this.getCustomerId());
    return newJob;
  }



  @Override
  public String toString() {
    return "SmsJob{" +
      "shopId=" + shopId +
      ", receiveMobile='" + receiveMobile + '\'' +
      ", content='" + content + '\'' +
      ", userId=" + userId +
      ", sender=" + sender +
      ", priority=" + priority +
      ", sendTimes=" + sendTimes +
      ", lastSendTime=" + lastSendTime +
      ", startTime=" + startTime +
      ", expireTime=" + expireTime +
      ", smsSendId='" + smsSendId + '\'' +
      ", status='" + status + '\'' +
      ", type=" + type +
      ", name='" + name + '\'' +
      ", vehicleLicense='" + vehicleLicense + '\'' +
      ", reponseReason='" + reponseReason + '\'' +
      ", executeType='" + executeType + '\'' +
      ", smsChannel=" + smsChannel +
      ", smsSendKind=" + smsSendKind +
      '}';
  }

  @Column(name = "shop_name")
  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
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
    if (StringUtils.isNotBlank(content) && content.length() > 500) {
      content = content.substring(0, 490) + "...";
    }
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

  @Column(name = "priority")
  public Long getPriority() {
    return priority;
  }

  public void setPriority(Long priority) {
    this.priority = priority;
  }

  @Column(name = "start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Column(name = "expire_time")
  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
  }

  @Column(name = "sms_send_id", length = 50)
  public String getSmsSendId() {
    return smsSendId;
  }

  public void setSmsSendId(String smsSendId) {
    this.smsSendId = smsSendId;
  }

  @Column(name = "sms_id")
  public Long getSmsId() {
    return smsId;
  }

  public void setSmsId(Long smsId) {
    this.smsId = smsId;
  }

  @Column(name = "status")
  public String getStatus() {
    if (status == null) {
      status = SmsConstant.SMS_STATUS_WAITING;
    }
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

  public void setVehicleLicense(String vehicleLicense) {
    this.vehicleLicense = vehicleLicense;
  }

  @Column(name = "reponse_reason", length = 500)
  public String getReponseReason() {
    return reponseReason;
  }

  public void setReponseReason(String reponseReason) {
    this.reponseReason = reponseReason;
  }

  @Column(name = "execute_type")
  public String getExecuteType() {
    return executeType;
  }

  public void setExecuteType(String executeType) {
    this.executeType = executeType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_channel")
  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
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
  @Column(name="sms_type")
  public SmsType getSmsType() {
    return smsType;
  }

  public void setSmsType(SmsType smsType) {
    this.smsType = smsType;
  }

  @Column(name="customer_id")
  public Long getCustomerId(){
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name="app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }
}
