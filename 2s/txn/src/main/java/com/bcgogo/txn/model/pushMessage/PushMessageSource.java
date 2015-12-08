package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageSourceDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "push_message_source")
public class PushMessageSource extends LongIdentifier {
  private Long shopId;
  private Long messageId;
  private Long sourceId;
  private Long createTime;
  private PushMessageSourceType type;

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
  public PushMessageSourceType getType() {
    return type;
  }

  public void setType(PushMessageSourceType type) {
    this.type = type;
  }

  @Column(name="source_id")
  public Long getSourceId() {
    return sourceId;
  }

  public void setSourceId(Long sourceId) {
    this.sourceId = sourceId;
  }


  public void fromDTO(PushMessageSourceDTO pushMessageSourceDTO){
    this.setShopId(pushMessageSourceDTO.getShopId());
    this.setCreateTime(pushMessageSourceDTO.getCreateTime());
    this.setSourceId(pushMessageSourceDTO.getSourceId());
    this.setMessageId(pushMessageSourceDTO.getMessageId());
    this.setType(pushMessageSourceDTO.getType());
  }

  public PushMessageSourceDTO toDTO(){
    PushMessageSourceDTO pushMessageSourceDTO = new PushMessageSourceDTO();
    pushMessageSourceDTO.setId(this.getId());
    pushMessageSourceDTO.setShopId(this.getShopId());
    pushMessageSourceDTO.setCreateTime(this.getCreateTime());
    pushMessageSourceDTO.setSourceId(this.getSourceId());
    pushMessageSourceDTO.setMessageId(this.getMessageId());
    pushMessageSourceDTO.setType(this.getType());

    return pushMessageSourceDTO;
  }

  @Override
  public String toString() {
    return "PushMessageSource{" +
        "shopId=" + shopId +
        ", messageId=" + messageId +
        ", sourceId=" + sourceId +
        ", createTime=" + createTime +
        ", type=" + type +
        '}';
  }
}
