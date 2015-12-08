package com.bcgogo.api;

import com.bcgogo.enums.app.EnquiryShopResponseStatus;
import com.bcgogo.enums.app.EnquiryTargetShopStatus;
import com.bcgogo.enums.common.ObjectStatus;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-17
 * Time: 上午11:15
 * 询价单发送给店铺记录
 */
public class EnquiryTargetShopDTO implements Serializable {
  private Long id;
  private Long enquiryId;
  private Long targetShopId;
  private String targetShopName;
  private int sequence;//发送店铺的顺序，前台展示用
  private EnquiryTargetShopStatus status;  //appUserNo处理的状态
  private String receiptNo;//发送给店家后生成的单据号
  private Long sendTime;//发送时间
  private EnquiryShopResponseStatus shopResponseStatus; //shop处理的状态
  private Long lastResponseTime;//最后报价时间


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getEnquiryId() {
    return enquiryId;
  }

  public void setEnquiryId(Long enquiryId) {
    this.enquiryId = enquiryId;
  }

  public Long getTargetShopId() {
    return targetShopId;
  }

  public void setTargetShopId(Long targetShopId) {
    this.targetShopId = targetShopId;
  }

  public String getTargetShopName() {
    return targetShopName;
  }

  public void setTargetShopName(String targetShopName) {
    this.targetShopName = targetShopName;
  }

  public int getSequence() {
    return sequence;
  }

  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

  public EnquiryTargetShopStatus getStatus() {
    return status;
  }

  public void setStatus(EnquiryTargetShopStatus status) {
    this.status = status;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  public boolean validSaveOrUpdate() {
    return getTargetShopId() != null && getStatus() != null;
  }

  public boolean validToSent(){
    return getTargetShopId() != null && EnquiryTargetShopStatus.SENT == getStatus();
  }

  public EnquiryShopResponseStatus getShopResponseStatus() {
    return shopResponseStatus;
  }

  public void setShopResponseStatus(EnquiryShopResponseStatus shopResponseStatus) {
    this.shopResponseStatus = shopResponseStatus;
  }

  public Long getLastResponseTime() {
    return lastResponseTime;
  }

  public void setLastResponseTime(Long lastResponseTime) {
    this.lastResponseTime = lastResponseTime;
  }
}
