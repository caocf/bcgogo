package com.bcgogo.txn.dto.finance;

/**
 * User: ZhangJuntao
 * Date: 13-4-2
 * Time: 下午2:17
 */
public class UnconstrainedSoftwareReceivableDTO {
  private Long shopId;
  private Long payeeId;
  private String payeeName;
  private Long operatorId;
  private Long operateTime;
  private Long submitterId;
  private Long submitTime;
  private Double softPrice;
  private Double receivedAmount;
  private Long receivedTime;
  private Double receivingAmount;
  private Long receivingTime;

  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public Double getSoftPrice() {
    return softPrice;
  }

  public void setSoftPrice(Double softPrice) {
    this.softPrice = softPrice;
  }

  public Double getReceivedAmount() {
    return receivedAmount;
  }

  public void setReceivedAmount(Double receivedAmount) {
    this.receivedAmount = receivedAmount;
  }

  public Long getReceivedTime() {
    return receivedTime;
  }

  public void setReceivedTime(Long receivedTime) {
    this.receivedTime = receivedTime;
  }

  public Double getReceivingAmount() {
    return receivingAmount;
  }

  public void setReceivingAmount(Double receivingAmount) {
    this.receivingAmount = receivingAmount;
  }

  public Long getReceivingTime() {
    return receivingTime;
  }

  public void setReceivingTime(Long receivingTime) {
    this.receivingTime = receivingTime;
  }

  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  public Long getSubmitterId() {
    return submitterId;
  }

  public void setSubmitterId(Long submitterId) {
    this.submitterId = submitterId;
  }

  public Long getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(Long submitTime) {
    this.submitTime = submitTime;
  }
}
