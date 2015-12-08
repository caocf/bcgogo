package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverRecordDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "push_message_receiver_record")
public class PushMessageReceiverRecord extends LongIdentifier {
  private Long shopId;
  private Long messageId;
  private Long pushMessageReceiverId;
  private Long pushTime;


  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
  @Column(name="message_id")
  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }
  @Column(name="push_message_receiver_id")
  public Long getPushMessageReceiverId() {
    return pushMessageReceiverId;
  }

  public void setPushMessageReceiverId(Long pushMessageReceiverId) {
    this.pushMessageReceiverId = pushMessageReceiverId;
  }
  @Column(name="push_time")
  public Long getPushTime() {
    return pushTime;
  }

  public void setPushTime(Long pushTime) {
    this.pushTime = pushTime;
  }

  public void fromDTO(PushMessageReceiverRecordDTO pushMessageReceiverRecordDTO){
    this.setMessageId(pushMessageReceiverRecordDTO.getMessageId());
    this.setShopId(pushMessageReceiverRecordDTO.getShopId());
    this.setPushMessageReceiverId(pushMessageReceiverRecordDTO.getPushMessageReceiverId());
    this.setPushTime(pushMessageReceiverRecordDTO.getPushTime());
  }

  public PushMessageReceiverRecordDTO toDTO(){
    PushMessageReceiverRecordDTO pushMessageReceiverRecordDTO = new PushMessageReceiverRecordDTO();
    pushMessageReceiverRecordDTO.setMessageId(this.getMessageId());
    pushMessageReceiverRecordDTO.setShopId(this.getShopId());
    pushMessageReceiverRecordDTO.setPushMessageReceiverId(this.getPushMessageReceiverId());
    pushMessageReceiverRecordDTO.setPushTime(this.getPushTime());
    pushMessageReceiverRecordDTO.setId(this.getId());
    return pushMessageReceiverRecordDTO;
  }
}
