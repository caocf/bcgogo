package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.enums.txn.finance.ReceivableMethod;

/**
 * User: ZhangJuntao
 * Date: 13-3-20
 * Time: 上午8:57
 */
public class BcgogoReceivableDTO {
  private Long bcgogoReceivableOrderRecordRelationId;
  private Long bcgogoReceivableOrderId;
  private Long instalmentPlanAlgorithmId;
  private Long submitterId;      //接到款项并确认支付的提交人（一般为出纳）
  private Long payeeId;     //从客户处收款的收款人（一般为业务员）
  private PaymentMethod paymentMethod;
  private Long paymentTime;            //操作时间
  private ReceivableMethod receivableMethod;  // INSTALLMENT, FULL

  private Double totalAmount;
  private Double paidAmount;
  private Long shopId;

  private String relationReceivableMethod;  // INSTALLMENT, FULL
  private String orderPaymentType;
  private Double orderReceivedAmount;
  private Double orderReceivableAmount;
  private Double orderTotalAmount;



  private PaymentType paymentType;
  private String paymentTypeStr;
  private String paymentTypeStrs;
  private String paymentTimeStr;
  private String payeeName;

  public String getRelationReceivableMethod() {
    return relationReceivableMethod;
  }

  public void setRelationReceivableMethod(String relationReceivableMethod) {
    this.relationReceivableMethod = relationReceivableMethod;
  }

  public String getOrderPaymentType() {
    return orderPaymentType;
  }

  public void setOrderPaymentType(String orderPaymentType) {
    this.orderPaymentType = orderPaymentType;
  }

  public Double getOrderReceivedAmount() {
    return orderReceivedAmount;
  }

  public void setOrderReceivedAmount(Double orderReceivedAmount) {
    this.orderReceivedAmount = orderReceivedAmount;
  }

  public Double getOrderReceivableAmount() {
    return orderReceivableAmount;
  }

  public void setOrderReceivableAmount(Double orderReceivableAmount) {
    this.orderReceivableAmount = orderReceivableAmount;
  }

  public Double getOrderTotalAmount() {
    return orderTotalAmount;
  }

  public void setOrderTotalAmount(Double orderTotalAmount) {
    this.orderTotalAmount = orderTotalAmount;
  }

  public String getPaymentTypeStrs() {
    return paymentTypeStrs;
  }

  public void setPaymentTypeStrs(String paymentTypeStrs) {
    this.paymentTypeStrs = paymentTypeStrs;
  }

  public Long getBcgogoReceivableOrderId() {
    return bcgogoReceivableOrderId;
  }

  public void setBcgogoReceivableOrderId(Long bcgogoReceivableOrderId) {
    this.bcgogoReceivableOrderId = bcgogoReceivableOrderId;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  public Long getBcgogoReceivableOrderRecordRelationId() {
    return bcgogoReceivableOrderRecordRelationId;
  }

  public void setBcgogoReceivableOrderRecordRelationId(Long bcgogoReceivableOrderRecordRelationId) {
    this.bcgogoReceivableOrderRecordRelationId = bcgogoReceivableOrderRecordRelationId;
    }

  public Long getSubmitterId() {
    return submitterId;
  }

  public void setSubmitterId(Long submitterId) {
    this.submitterId = submitterId;
  }

  public ReceivableMethod getReceivableMethod() {
    return receivableMethod;
  }

  public void setReceivableMethod(ReceivableMethod receivableMethod) {
    this.receivableMethod = receivableMethod;
  }

  public Long getInstalmentPlanAlgorithmId() {
    return instalmentPlanAlgorithmId;
  }

  public void setInstalmentPlanAlgorithmId(Long instalmentPlanAlgorithmId) {
    this.instalmentPlanAlgorithmId = instalmentPlanAlgorithmId;
  }

  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public String getPaymentTypeStr() {
    return paymentTypeStr;
  }

  public void setPaymentTypeStr(String paymentTypeStr) {
    this.paymentTypeStr = paymentTypeStr;
  }

  public String getPaymentTimeStr() {
    return paymentTimeStr;
  }

  public void setPaymentTimeStr(String paymentTimeStr) {
    this.paymentTimeStr = paymentTimeStr;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
  }

  public String getPayeeName() {
    return payeeName;
  }
}
