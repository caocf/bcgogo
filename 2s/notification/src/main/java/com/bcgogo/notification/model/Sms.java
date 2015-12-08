package com.bcgogo.notification.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.dto.SmsDTO;

import javax.persistence.*;

/**
 * 一条短信，区别于smsjob的一个联系人一条数据
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-24
 * Time: 上午11:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sms")
public class Sms extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private Long editDate;
  private String contactGroupIds;
  private String contactIds;
  private String content;
  private Long sendTime;
  private SmsType smsType;
  private SmsSendScene smsSendScene;
  private Boolean appFlag;
  private Boolean smsFlag;
  private Integer countSmsSent;
  private Integer countAppSent;
  private DeletedType deleted=DeletedType.FALSE;



  public Sms(){}

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name = "send_time")
  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "contact_ids")
  public String getContactIds() {
    return contactIds;
  }

  public void setContactIds(String contactIds) {
    this.contactIds = contactIds;
  }

  @Column(name = "contact_group_ids")
  public String getContactGroupIds() {
    return contactGroupIds;
  }

  public void setContactGroupIds(String contactGroupIds) {
    this.contactGroupIds = contactGroupIds;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_type")
  public SmsType getSmsType() {
    return smsType;
  }

  public void setSmsType(SmsType smsType) {
    this.smsType = smsType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="sms_send_scene")
  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  @Column(name="app_flag")
  public Boolean getAppFlag() {
    return appFlag;
  }

  public void setAppFlag(Boolean appFlag) {
    this.appFlag = appFlag;
  }

  @Column(name="sms_flag")
  public Boolean getSmsFlag() {
    return smsFlag;
  }

  public void setSmsFlag(Boolean smsFlag) {
    this.smsFlag = smsFlag;
  }

   @Column(name="count_sms_sent")
  public Integer getCountSmsSent() {
    return countSmsSent;
  }

  public void setCountSmsSent(Integer countSmsSent) {
    this.countSmsSent = countSmsSent;
  }

   @Column(name="count_app_sent")
  public Integer getCountAppSent() {
    return countAppSent;
  }

  public void setCountAppSent(Integer countAppSent) {
    this.countAppSent = countAppSent;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "deleted")
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public SmsDTO toDTO(){
    SmsDTO smsDTO=new SmsDTO();
    smsDTO.setSendTime(this.getSendTime());
    smsDTO.setContent(this.getContent());
    smsDTO.setShopId(this.getShopId());
    smsDTO.setSmsType(this.getSmsType());
    smsDTO.setUserId(this.getUserId());
    smsDTO.setContactGroupIds(this.getContactGroupIds());
    smsDTO.setContactIds(this.getContactIds());
    smsDTO.setEditDate(this.getEditDate());
    smsDTO.setAppFlag(this.appFlag);
    smsDTO.setSmsFlag(this.smsFlag);
    smsDTO.setId(this.getId());
    smsDTO.setSmsSendScene(this.getSmsSendScene());
    smsDTO.setCountAppSent(this.countAppSent);
    smsDTO.setCountSmsSent(this.countSmsSent);
    return smsDTO;
  }

  public void fromDTO(SmsDTO smsDTO){
    this.sendTime=smsDTO.getSendTime();
    this.content=smsDTO.getContent();
    this.shopId=smsDTO.getShopId();
    this.smsType=smsDTO.getSmsType();
    this.smsSendScene=smsDTO.getSmsSendScene();
    this.userId=smsDTO.getUserId();
    this.contactGroupIds=smsDTO.getContactGroupIds();
    this.contactIds=smsDTO.getContactIds();
    this.editDate=smsDTO.getEditDate();
    this.appFlag=smsDTO.getAppFlag();
    this.smsFlag=smsDTO.getSmsFlag();
    this.countAppSent=smsDTO.getCountAppSent();
    this.countSmsSent=smsDTO.getCountSmsSent();
  }

}
