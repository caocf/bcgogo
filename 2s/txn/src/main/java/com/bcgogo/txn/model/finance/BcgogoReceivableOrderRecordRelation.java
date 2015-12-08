package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.enums.txn.finance.ReceivableMethod;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableOrderRecordRelationDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 */
@Entity
@Table(name = "bcgogo_receivable_order_record_relation")
public class BcgogoReceivableOrderRecordRelation extends LongIdentifier {
  private Long shopId;
  private Long bcgogoReceivableOrderId;
  private Long bcgogoReceivableRecordId;
  private Long instalmentPlanId;                //分期id
  private Long instalmentPlanItemId;          //还款item期Id
  private ReceivableMethod receivableMethod;    //收款方式（分期/全额）
  private PaymentMethod paymentMethod;          //支付方式（在线支付/银联转账/上门收取）
  @Deprecated
  private PaymentType paymentType;              //支付类型（软件/硬件）
  private Double amount;                         //金额
  private String memo;
  private Long smsRechargeId;         //短信充值记录的Id
  public BcgogoReceivableOrderRecordRelation() {
    super();
  }

  //硬件新增费用
  public BcgogoReceivableOrderRecordRelation(BcgogoReceivableOrderDTO dto) {
    this.setShopId(dto.getShopId());
    this.setReceivableMethod(ReceivableMethod.FULL);
    this.setPaymentType(PaymentType.HARDWARE);
    this.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
    this.setAmount(0.0d);
  }

  //软件新增费用
  public void createSoftwareRelation(long shopId) {
    this.setShopId(shopId);
    this.setReceivableMethod(ReceivableMethod.FULL);
    this.setPaymentType(PaymentType.SOFTWARE);
    this.setAmount(0.0d);
  }

  public BcgogoReceivableOrderRecordRelation(Long shopId, ReceivableMethod receivableMethod, Double amount) {
    this.setShopId(shopId);
    this.setReceivableMethod(receivableMethod);
    this.setAmount(amount);
  }

  @Column(name = "bcgogo_receivable_order_id")
  public Long getBcgogoReceivableOrderId() {
    return bcgogoReceivableOrderId;
  }

  public void setBcgogoReceivableOrderId(Long bcgogoReceivableOrderId) {
    this.bcgogoReceivableOrderId = bcgogoReceivableOrderId;
  }

  @Column(name = "bcgogo_receivable_record_id")
  public Long getBcgogoReceivableRecordId() {
    return bcgogoReceivableRecordId;
  }

  public void setBcgogoReceivableRecordId(Long bcgogoReceivableRecordId) {
    this.bcgogoReceivableRecordId = bcgogoReceivableRecordId;
  }

  @Column(name = "instalment_plan_id")
  public Long getInstalmentPlanId() {
    return instalmentPlanId;
  }

  public void setInstalmentPlanId(Long instalmentPlanId) {
    this.instalmentPlanId = instalmentPlanId;
  }


  @Column(name = "instalment_plan_item_id")
  public Long getInstalmentPlanItemId() {
    return instalmentPlanItemId;
  }

  public void setInstalmentPlanItemId(Long instalmentPlanItemId) {
    this.instalmentPlanItemId = instalmentPlanItemId;
  }

  @Column(name = "receivable_method")
  @Enumerated(EnumType.STRING)
  public ReceivableMethod getReceivableMethod() {
    return receivableMethod;
  }

  public void setReceivableMethod(ReceivableMethod receivableMethod) {
    this.receivableMethod = receivableMethod;
  }

  @Column(name = "payment_method")
  @Enumerated(EnumType.STRING)
  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "payment_type")
  @Enumerated(EnumType.STRING)
  public PaymentType getPaymentType() {
    return paymentType;
  }

  public void setPaymentType(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "sms_recharge_id")
  public Long getSmsRechargeId() {
    return smsRechargeId;
  }

  public void setSmsRechargeId(Long smsRechargeId) {
    this.smsRechargeId = smsRechargeId;
  }

  public BcgogoReceivableOrderRecordRelationDTO toDTO() {
    BcgogoReceivableOrderRecordRelationDTO bcgogoReceivableOrderRecordRelationDTO = new BcgogoReceivableOrderRecordRelationDTO();
    bcgogoReceivableOrderRecordRelationDTO.setInstalmentPlanId(this.getInstalmentPlanId());
    bcgogoReceivableOrderRecordRelationDTO.setAmount(this.getAmount());
    bcgogoReceivableOrderRecordRelationDTO.setBcgogoReceivableOrderId(this.getBcgogoReceivableOrderId());
    bcgogoReceivableOrderRecordRelationDTO.setBcgogoReceivableRecordId(this.getBcgogoReceivableRecordId());
    bcgogoReceivableOrderRecordRelationDTO.setId(this.getId());
    bcgogoReceivableOrderRecordRelationDTO.setInstalmentPlanItemId(this.getInstalmentPlanItemId());
    bcgogoReceivableOrderRecordRelationDTO.setMemo(this.getMemo());
    bcgogoReceivableOrderRecordRelationDTO.setPaymentMethod(this.getPaymentMethod());
    bcgogoReceivableOrderRecordRelationDTO.setReceivableMethod(this.getReceivableMethod());
    bcgogoReceivableOrderRecordRelationDTO.setShopId(this.getShopId());
    return bcgogoReceivableOrderRecordRelationDTO;
  }

}
