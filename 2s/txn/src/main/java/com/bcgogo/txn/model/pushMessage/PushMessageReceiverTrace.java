package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.pushMessage.PushMessagePushStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageShowStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Zhangjuntao
 * Date: 13-7-1
 * Time: 下午4:25
 */
@Entity
@Table(name = "push_message_receiver_trace")
public class PushMessageReceiverTrace extends LongIdentifier {
  private Long shopId;
  private Long receiverId;
  private OperatorType receiverType;
  private Long messageId;
  private ShopKind shopKind;
  private PushMessageReceiverStatus status;
  private PushMessagePushStatus pushStatus;
  private PushMessageShowStatus showStatus;

  //MessageReceiver 移植来
  private String localReceiverName;//客户
  private Long localReceiverId; //客户Id
  private String senderName;//发件人对应本店供应商名字
  private Long senderId;//发件人对应本店供应商Id

  @Column(name="local_receiver_name")
  public String getLocalReceiverName() {
    return localReceiverName;
  }

  public void setLocalReceiverName(String localReceiverName) {
    this.localReceiverName = localReceiverName;
  }
  @Column(name="local_receiver_id")
  public Long getLocalReceiverId() {
    return localReceiverId;
  }

  public void setLocalReceiverId(Long localReceiverId) {
    this.localReceiverId = localReceiverId;
  }
  @Column(name="show_status")
  @Enumerated(EnumType.STRING)
  public PushMessageShowStatus getShowStatus() {
    return showStatus;
  }

  public void setShowStatus(PushMessageShowStatus showStatus) {
    this.showStatus = showStatus;
  }

  @Column(name="push_status")
  @Enumerated(EnumType.STRING)
  public PushMessagePushStatus getPushStatus() {
    return pushStatus;
  }

  public void setPushStatus(PushMessagePushStatus pushStatus) {
    this.pushStatus = pushStatus;
  }

  @Column(name="sender_name")
  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  @Column(name="sender_id")
  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "message_id")
  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public PushMessageReceiverStatus getStatus() {
    return status;
  }

  public void setStatus(PushMessageReceiverStatus status) {
    this.status = status;
  }

  @Column(name = "shop_kind")
  @Enumerated(EnumType.STRING)
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  @Column(name = "receiver_id")
  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  @Column(name = "receiver_type")
  @Enumerated(EnumType.STRING)
  public OperatorType getReceiverType() {
    return receiverType;
  }

  public void setReceiverType(OperatorType receiverType) {
    this.receiverType = receiverType;
  }

  public PushMessageReceiverTrace(PushMessageReceiverDTO pushMessageReceiverDTO) {
    this.setMessageId(pushMessageReceiverDTO.getMessageId());
    this.setShopId(pushMessageReceiverDTO.getShopId());
    this.setShopKind(pushMessageReceiverDTO.getShopKind());
    this.setStatus(pushMessageReceiverDTO.getStatus());
    this.setId(pushMessageReceiverDTO.getId());
    this.setReceiverType(pushMessageReceiverDTO.getReceiverType());
    this.setReceiverId(pushMessageReceiverDTO.getReceiverId());
    this.setSenderId(pushMessageReceiverDTO.getSenderId());
    this.setSenderName(pushMessageReceiverDTO.getSenderName());
    this.setPushStatus(pushMessageReceiverDTO.getPushStatus());
    this.setLocalReceiverId(pushMessageReceiverDTO.getLocalReceiverId());
    this.setLocalReceiverName(pushMessageReceiverDTO.getLocalReceiverName());
    this.setShowStatus(pushMessageReceiverDTO.getShowStatus());
  }
}
