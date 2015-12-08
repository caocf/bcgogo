package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.ReceivableMethod;

/**
 * User: ZhangJuntao
 * Date: 13-3-20
 * Time: 上午10:31
 * 订单
 */
public class BcgogoReceivableOrderRecordRelationDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long bcgogoReceivableOrderId;
  private Long bcgogoReceivableRecordId;
  private Long instalmentPlanId;                //分期id
  private String instalmentPlanIdStr;                //分期id
  private Long instalmentPlanItemId;          //还款item期Id
  private ReceivableMethod receivableMethod;    //收款方式（分期/全额）
  private PaymentMethod paymentMethod;          //支付方式（在线支付/银联转账/上门收取）
  private Double amount;                         //金额
  private String memo;

  public String getInstalmentPlanIdStr() {
    return instalmentPlanIdStr;
  }

  public void setInstalmentPlanIdStr(String instalmentPlanIdStr) {
    this.instalmentPlanIdStr = instalmentPlanIdStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getBcgogoReceivableOrderId() {
    return bcgogoReceivableOrderId;
  }

  public void setBcgogoReceivableOrderId(Long bcgogoReceivableOrderId) {
    this.bcgogoReceivableOrderId = bcgogoReceivableOrderId;
  }

  public Long getBcgogoReceivableRecordId() {
    return bcgogoReceivableRecordId;
  }

  public void setBcgogoReceivableRecordId(Long bcgogoReceivableRecordId) {
    this.bcgogoReceivableRecordId = bcgogoReceivableRecordId;
  }

  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
    if(instalmentPlanId!=null) instalmentPlanIdStr = instalmentPlanId.toString();
  }

  public Long getInstalmentPlanItemId() {
    return instalmentPlanItemId;
  }

  public void setInstalmentPlanItemId(Long instalmentPlanItemId) {
    this.instalmentPlanItemId = instalmentPlanItemId;
  }

  public ReceivableMethod getReceivableMethod() {
    return receivableMethod;
  }

  public void setReceivableMethod(ReceivableMethod receivableMethod) {
    this.receivableMethod = receivableMethod;
  }

  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
