package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageFeedbackType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageFeedbackRecordDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "push_message_feedback_record")
public class PushMessageFeedbackRecord extends LongIdentifier {
  private Long shopId;
  private Long messageId;
  private PushMessageFeedbackType type;
  private Long createTime;
  public PushMessageFeedbackRecord(){
    super();
  }

  public PushMessageFeedbackRecord(Long shopId, Long messageId, PushMessageFeedbackType type, Long createTime) {
    this.shopId = shopId;
    this.messageId = messageId;
    this.type = type;
    this.createTime = createTime;
  }

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

  @Column(name = "create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name="type")
  @Enumerated(EnumType.STRING)
  public PushMessageFeedbackType getType() {
    return type;
  }

  public void setType(PushMessageFeedbackType type) {
    this.type = type;
  }

  public void fromDTO(PushMessageFeedbackRecordDTO pushMessageFeedbackRecordDTO){
    this.setShopId(pushMessageFeedbackRecordDTO.getShopId());
    this.setCreateTime(pushMessageFeedbackRecordDTO.getCreateTime());
    this.setMessageId(pushMessageFeedbackRecordDTO.getMessageId());
    this.setType(pushMessageFeedbackRecordDTO.getType());
  }

  public PushMessageFeedbackRecordDTO toDTO(){
    PushMessageFeedbackRecordDTO pushMessageFeedbackRecordDTO = new PushMessageFeedbackRecordDTO();
    pushMessageFeedbackRecordDTO.setId(this.getId());
    pushMessageFeedbackRecordDTO.setShopId(this.getShopId());
    pushMessageFeedbackRecordDTO.setCreateTime(this.getCreateTime());
    pushMessageFeedbackRecordDTO.setMessageId(this.getMessageId());
    pushMessageFeedbackRecordDTO.setType(this.getType());

    return pushMessageFeedbackRecordDTO;
  }
}
