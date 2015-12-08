package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午3:31
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageSourceDTO {
  private Long id;
  private Long shopId;
  private Long messageId;
  private Long sourceId;
  private PushMessageSourceType type;
  private Long createTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public Long getSourceId() {
    return sourceId;
  }

  public void setSourceId(Long sourceId) {
    this.sourceId = sourceId;
  }

  public PushMessageSourceType getType() {
    return type;
  }

  public void setType(PushMessageSourceType type) {
    this.type = type;
  }

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Override
  public String toString() {
    return "PushMessageSourceDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", messageId=" + messageId +
        ", sourceId=" + sourceId +
        ", type=" + type +
        ", createTime=" + createTime +
        '}';
  }
}
