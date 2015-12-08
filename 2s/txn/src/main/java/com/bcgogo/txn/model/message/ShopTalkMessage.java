package com.bcgogo.txn.model.message;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.ShopTalkMessageDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-26
 * Time: 下午4:02
 */
@Entity
@Table(name = "shop_talk_message")
public class ShopTalkMessage extends LongIdentifier {
  private Long shopId;
  private String fromUserName;
  private String appUserNo;
  private String vehicleNo;
  private Long customerId;
  private String customer;
  private Long sendTime;
  private String content;
  private Long replyTime;
  private String replyContent;

  public void fromDTO(ShopTalkMessageDTO messageDTO) {
    this.setId(messageDTO.getId());
    this.setShopId(messageDTO.getShopId());
    this.setFromUserName(messageDTO.getFromUserName());
    this.setAppUserNo(messageDTO.getAppUserNo());
    this.setVehicleNo(messageDTO.getVehicleNo());
    this.setCustomerId(messageDTO.getCustomerId());
    this.setCustomer(messageDTO.getCustomer());
    this.setSendTime(messageDTO.getSendTime());
    this.setContent(messageDTO.getContent());
    this.setReplyTime(messageDTO.getReplyTime());
    this.setReplyContent(messageDTO.getReplyContent());
  }

  public ShopTalkMessageDTO toDTO() {
    ShopTalkMessageDTO messageDTO = new ShopTalkMessageDTO();
    messageDTO.setId(this.getId());
    messageDTO.setShopId(this.getShopId());
    messageDTO.setFromUserName(this.getFromUserName());
    messageDTO.setAppUserNo(this.getAppUserNo());
    messageDTO.setVehicleNo(this.getVehicleNo());
    messageDTO.setCustomerId(this.getCustomerId());
    messageDTO.setCustomer(this.getCustomer());
    messageDTO.setSendTime(this.getSendTime());
    messageDTO.setContent(this.getContent());
    messageDTO.setReplyTime(this.getReplyTime());
    messageDTO.setReplyContent(this.getReplyContent());
    return messageDTO;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

   @Column(name = "from_user_name")
  public String getFromUserName() {
    return fromUserName;
  }

  public void setFromUserName(String fromUserName) {
    this.fromUserName = fromUserName;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

   @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "customer")
  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
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

  @Column(name = "reply_time")
  public Long getReplyTime() {
    return replyTime;
  }

  public void setReplyTime(Long replyTime) {
    this.replyTime = replyTime;
  }

  @Column(name = "reply_content")
  public String getReplyContent() {
    return replyContent;
  }

  public void setReplyContent(String replyContent) {
    this.replyContent = replyContent;
  }
}
