package com.bcgogo.txn.dto.finance;

public class BcgogoReceivableOrderChinaPayResultDTO {
  private String paymentTimeStr;
  private String paymentTypeStr;
  private Long bcgogoReceivableOrderId;
  private String errorInfo;
  private Double currentPaidAmount;

  public Double getCurrentPaidAmount() {
    return currentPaidAmount;
  }

  public void setCurrentPaidAmount(Double currentPaidAmount) {
    this.currentPaidAmount = currentPaidAmount;
  }

  public String getErrorInfo() {
    return errorInfo;
  }

  public void setErrorInfo(String errorInfo) {
    this.errorInfo = errorInfo;
  }

  public String getPaymentTimeStr() {
    return paymentTimeStr;
  }

  public void setPaymentTimeStr(String paymentTimeStr) {
    this.paymentTimeStr = paymentTimeStr;
  }

  public String getPaymentTypeStr() {
    return paymentTypeStr;
  }

  public void setPaymentTypeStr(String paymentTypeStr) {
    this.paymentTypeStr = paymentTypeStr;
  }

  public Long getBcgogoReceivableOrderId() {
    return bcgogoReceivableOrderId;
  }

  public void setBcgogoReceivableOrderId(Long bcgogoReceivableOrderId) {
    this.bcgogoReceivableOrderId = bcgogoReceivableOrderId;
  }
}
