package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageFeedbackType;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageFeedbackRecordDTO {
  private Long Id;
  private Long shopId;
  private Long messageId;
  private PushMessageFeedbackType type;
  private Long createTime;

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
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

  public PushMessageFeedbackType getType() {
    return type;
  }

  public void setType(PushMessageFeedbackType type) {
    this.type = type;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }
}
