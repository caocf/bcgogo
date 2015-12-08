package com.bcgogo.txn.model.message;

import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.remind.dto.MessageReceiverDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-9
 * Time: 上午10:19
 * 站内消息-接收者
 */
@Deprecated
@Entity
@Table(name = "message_receiver")
public class MessageReceiver extends LongIdentifier {
  private Long messageId;     //接收消息ID
  private Long receiverId;    //接收人ID     customer or supplier id
  private Long receiverShopId;
  private String receiverName;    //接收人名称
  private String senderName;//发件人对应本店供应商名字
  private Long senderId;//发件人对应本店供应商Id


  public MessageReceiver(MessageReceiverDTO messageReceiverDTO) {
    if (messageReceiverDTO != null) {
      this.messageId = messageReceiverDTO.getMessageId();
      this.receiverId = messageReceiverDTO.getReceiverId();
      this.receiverName = messageReceiverDTO.getReceiverName();
      this.receiverShopId = messageReceiverDTO.getReceiverShopId();
      this.senderName = messageReceiverDTO.getSenderName();
      this.senderId = messageReceiverDTO.getSenderId();
    }
  }

  public MessageReceiverDTO toDTO() {
    MessageReceiverDTO messageReceiverDTO = new MessageReceiverDTO();
    messageReceiverDTO.setMessageId(this.getMessageId());
    messageReceiverDTO.setReceiverId(this.getReceiverId());
    messageReceiverDTO.setReceiverName(this.getReceiverName());
    messageReceiverDTO.setId(this.getId());
    messageReceiverDTO.setReceiverShopId(this.getReceiverShopId());
    return messageReceiverDTO;
  }

  public MessageReceiver() {
  }

  @Override
  public MessageReceiver clone() throws CloneNotSupportedException {
    return (MessageReceiver) super.clone();
  }

  @Column(name = "message_id")
  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  @Column(name = "receiver_id")
  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  @Column(name = "receiver_name")
  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }


  @Column(name = "receiver_shop_id")
  public Long getReceiverShopId() {
    return receiverShopId;
  }

  public void setReceiverShopId(Long receiverShopId) {
    this.receiverShopId = receiverShopId;
  }

  @Column(name = "sender_name")
  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  @Column(name = "sender_id")
  public Long getSenderId() {
    return senderId;
  }

  public void setSenderId(Long senderId) {
    this.senderId = senderId;
  }
}
