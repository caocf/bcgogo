package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.pushMessage.PushMessagePushStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;
import com.bcgogo.enums.txn.pushMessage.PushMessageShowStatus;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageReceiverDTO {
  private String idStr;
  private Long id;
  private Long shopId;
  private Long receiverId;
  private OperatorType receiverType;
  private Long messageId;
  private ShopKind shopKind;
  private PushMessageReceiverStatus status = PushMessageReceiverStatus.UNREAD;
  private PushMessagePushStatus pushStatus = PushMessagePushStatus.UN_PUSH;
  private PushMessageShowStatus showStatus = PushMessageShowStatus.UN_ACTIVE;
  private PushMessageReceiverMatchRecordDTO pushMessageReceiverMatchRecordDTO;

  //MessageReceiver 移植来
  private String localReceiverName;//客户
  private Long localReceiverId; //客户Id
  private String senderName;//发件人对应本店供应商名字
  private Long senderId;//发件人对应本店供应商Id

  public PushMessageReceiverDTO() {
  }

  public String getLocalReceiverName() {
    return localReceiverName;
  }

  public void setLocalReceiverName(String localReceiverName) {
    this.localReceiverName = localReceiverName;
  }

  public Long getLocalReceiverId() {
    return localReceiverId;
  }

  public void setLocalReceiverId(Long localReceiverId) {
    this.localReceiverId = localReceiverId;
  }

  public PushMessagePushStatus getPushStatus() {
    return pushStatus;
  }

  public void setPushStatus(PushMessagePushStatus pushStatus) {
    this.pushStatus = pushStatus;
  }

  public PushMessageReceiverDTO(Long shopId, ShopKind shopKind,Long receiverId,OperatorType receiverType) {
    this.shopId = shopId;
    this.shopKind = shopKind;
    this.receiverId = receiverId;
    this.receiverType = receiverType;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null) setIdStr(id.toString());
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public PushMessageReceiverStatus getStatus() {
    return status;
  }

  public void setStatus(PushMessageReceiverStatus status) {
    this.status = status;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public PushMessageReceiverMatchRecordDTO getPushMessageReceiverMatchRecordDTO() {
    return pushMessageReceiverMatchRecordDTO;
  }

  public void setPushMessageReceiverMatchRecordDTO(PushMessageReceiverMatchRecordDTO pushMessageReceiverMatchRecordDTO) {
    this.pushMessageReceiverMatchRecordDTO = pushMessageReceiverMatchRecordDTO;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public OperatorType getReceiverType() {
    return receiverType;
  }

  public void setReceiverType(OperatorType receiverType) {
    this.receiverType = receiverType;
  }

  @Override
  public String toString() {
    return "PushMessageReceiverDTO{" +
        "idStr='" + idStr + '\'' +
        ", id=" + id +
        ", shopId=" + shopId +
        ", receiverId=" + receiverId +
        ", receiverType=" + receiverType +
        ", messageId=" + messageId +
        ", shopKind=" + shopKind +
        ", status=" + status +
        ", pushMessageReceiverMatchRecordDTO=" + pushMessageReceiverMatchRecordDTO +
        '}';
  }

  public PushMessageShowStatus getShowStatus() {
    return showStatus;
  }

  public void setShowStatus(PushMessageShowStatus showStatus) {
    this.showStatus = showStatus;
  }
}
