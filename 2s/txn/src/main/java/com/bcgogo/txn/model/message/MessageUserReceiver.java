package com.bcgogo.txn.model.message;

import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午4:51
 * 消息中心-站内消息-针对user
 */
@Deprecated
@Entity
@Table(name = "message_user_receiver")
public class MessageUserReceiver extends LongIdentifier {
  private Long receiverUserId;
  private Long messageReceiverId;
  private ReceiverStatus status;

  public MessageUserReceiver() {
  }

  public MessageUserReceiver(Long receiverUserId, Long messageReceiverId, ReceiverStatus status) {
    this.receiverUserId = receiverUserId;
    this.messageReceiverId = messageReceiverId;
    this.status = status;
  }

  @Column(name = "receiver_user_id")
  public Long getReceiverUserId() {
    return receiverUserId;
  }

  public void setReceiverUserId(Long receiverUserId) {
    this.receiverUserId = receiverUserId;
  }

  @Column(name = "message_receiver_id")
  public Long getMessageReceiverId() {
    return messageReceiverId;
  }

  public void setMessageReceiverId(Long messageReceiverId) {
    this.messageReceiverId = messageReceiverId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ReceiverStatus getStatus() {
    return status;
  }

  public void setStatus(ReceiverStatus status) {
    this.status = status;
  }
}
