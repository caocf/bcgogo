package com.bcgogo.remind.dto;

import com.bcgogo.enums.txn.message.ReceiverStatus;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-9
 * Time: 上午10:32
 * To change this template use File | Settings | File Templates.
 */
public class MessageReceiverDTO {

  private Long id;

    //接收消息ID
  private Long messageId;

  //接收人ID
  private Long receiverId;
  private Long receiverShopId;
  //接收人名称
  private String receiverName;

  private String receiveMobile;
  private String  senderName;//发件人对应本店供应商名字
  private Long senderId;//发件人对应本店供应商Id


  public String getReceiverName() {
    return receiverName;
  }

  public void setReceiverName(String receiverName) {
    this.receiverName = receiverName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public Long getReceiverShopId() {
    return receiverShopId;
  }

  public void setReceiverShopId(Long receiverShopId) {
    this.receiverShopId = receiverShopId;
  }

  public String getReceiveMobile() {
    return receiveMobile;
  }

  public void setReceiveMobile(String receiveMobile) {
    this.receiveMobile = receiveMobile;
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
}
