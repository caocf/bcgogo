package com.bcgogo.txn.dto.pushMessage;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageReceiverRecordDTO {
  private Long id;
  private Long shopId;
  private Long messageId;
  private Long pushMessageReceiverId;
  private Long pushTime;

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

  public Long getPushMessageReceiverId() {
    return pushMessageReceiverId;
  }

  public void setPushMessageReceiverId(Long pushMessageReceiverId) {
    this.pushMessageReceiverId = pushMessageReceiverId;
  }

  public Long getPushTime() {
    return pushTime;
  }

  public void setPushTime(Long pushTime) {
    this.pushTime = pushTime;
  }
}
