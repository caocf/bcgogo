package com.bcgogo.txn.model;

import com.bcgogo.enums.PaymentWay;
import com.bcgogo.enums.RechargeMethod;
import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SmsRechargeDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-19
 * Time: 上午11:13
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sms_recharge")
public class SmsRecharge extends LongIdentifier {
  public SmsRecharge(){}

  public SmsRecharge(SmsRechargeDTO smsRechargeDTO) {

    this.setId(smsRechargeDTO.getId());
    this.setShopId(smsRechargeDTO.getShopId());
    this.setRechargeTime(smsRechargeDTO.getRechargeTime());
    this.setRechargeNumber(smsRechargeDTO.getRechargeNumber());
    this.setSmsBalance(smsRechargeDTO.getSmsBalance());
    this.setRechargeAmount(smsRechargeDTO.getRechargeAmount());
    this.setPayTime(smsRechargeDTO.getPayTime());
    this.setUserId(smsRechargeDTO.getUserId());
    this.setState(smsRechargeDTO.getState());
    this.setReceiptNo(smsRechargeDTO.getReceiptNo());
    this.setRechargeMethod(smsRechargeDTO.getRechargeMethod());
    this.setStatus(smsRechargeDTO.getStatus());
    this.setPaymentWay(smsRechargeDTO.getPaymentWay());
    this.setPresentAmount(smsRechargeDTO.getPresentAmount());
    this.setPayeeId(smsRechargeDTO.getPayeeId());
    this.setAuditTime(smsRechargeDTO.getAuditTime());
  }

  public SmsRecharge fromDTO(SmsRechargeDTO smsRechargeDTO) {

    this.setId(smsRechargeDTO.getId());
    this.setShopId(smsRechargeDTO.getShopId());
    this.setRechargeTime(smsRechargeDTO.getRechargeTime());
    this.setRechargeNumber(smsRechargeDTO.getRechargeNumber());
    this.setSmsBalance(smsRechargeDTO.getSmsBalance());
    this.setRechargeAmount(smsRechargeDTO.getRechargeAmount());
    this.setPayTime(smsRechargeDTO.getPayTime());
    this.setUserId(smsRechargeDTO.getUserId());
    this.setState(smsRechargeDTO.getState());
    this.setReceiptNo(smsRechargeDTO.getReceiptNo());
    this.setStatus(smsRechargeDTO.getStatus());
    this.setRechargeMethod(smsRechargeDTO.getRechargeMethod());
    this.setPaymentWay(smsRechargeDTO.getPaymentWay());
    this.setPresentAmount(smsRechargeDTO.getPresentAmount());
    this.setPayeeId(smsRechargeDTO.getPayeeId());
    this.setAuditTime(smsRechargeDTO.getAuditTime());
    return this;
  }

  public SmsRechargeDTO toDTO() {
    SmsRechargeDTO smsRechargeDTO = new SmsRechargeDTO();

    smsRechargeDTO.setId(this.getId());
    smsRechargeDTO.setShopId(this.getShopId());
    smsRechargeDTO.setRechargeTime(this.getRechargeTime());
    smsRechargeDTO.setRechargeNumber(this.getRechargeNumber());
    smsRechargeDTO.setSmsBalance(this.getSmsBalance());
    smsRechargeDTO.setRechargeAmount(this.getRechargeAmount());
    smsRechargeDTO.setPayTime(this.getPayTime());
    smsRechargeDTO.setUserId(this.getUserId());
    smsRechargeDTO.setState(this.getState());
    smsRechargeDTO.setReceiptNo(this.getReceiptNo());
    smsRechargeDTO.setStatusDesc();
    smsRechargeDTO.setRechargeMethod(this.getRechargeMethod());
    smsRechargeDTO.setStatus(this.getStatus());
    smsRechargeDTO.setPaymentWay(this.getPaymentWay());
    smsRechargeDTO.setPresentAmount(this.getPresentAmount());
    smsRechargeDTO.setPayeeId(this.getPayeeId());
    smsRechargeDTO.setAuditTime(this.getAuditTime());
    return smsRechargeDTO;
  }

  private Long shopId;
  private Long rechargeTime;
  private String rechargeNumber;
  private Double smsBalance;
  private Double rechargeAmount;
  private Long payTime;
  private Long userId;
  private Long state;
  private String receiptNo;
  private RechargeMethod rechargeMethod;   //充值方式：客户充值   后台充值
  private BcgogoReceivableStatus status;    //状态   待审核  已入账
  private PaymentWay paymentWay;               //支付方式 银联  现金
  private Double presentAmount;            //赠送金额
  private Long payeeId;              //收款人（只有后台充值才会有）
  private Long auditTime;


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "recharge_time")
  public Long getRechargeTime() {
    return rechargeTime;
  }

  public void setRechargeTime(Long rechargeTime) {
    this.rechargeTime = rechargeTime;
  }

  @Column(name = "recharge_number", length = 50)
  public String getRechargeNumber() {
    return rechargeNumber;
  }

  public void setRechargeNumber(String rechargeNumber) {
    this.rechargeNumber = rechargeNumber;
  }

  @Column(name = "sms_balance")
  public Double getSmsBalance() {
    return smsBalance;
  }

  public void setSmsBalance(Double smsBalance) {
    this.smsBalance = smsBalance;
  }

  @Column(name = "recharge_amount")
  public Double getRechargeAmount() {
    return rechargeAmount;
  }

  public void setRechargeAmount(Double rechargeAmount) {
    this.rechargeAmount = rechargeAmount;
  }

  @Column(name = "pay_time")
  public Long getPayTime() {
    return payTime;
  }

  public void setPayTime(Long payTime) {
    this.payTime = payTime;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "receiptNo", length = 50)

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")

  public BcgogoReceivableStatus getStatus() {
    return status;
  }

  public void setStatus(BcgogoReceivableStatus status) {
    this.status = status;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "recharge_method")

  public RechargeMethod getRechargeMethod() {
    return rechargeMethod;
  }

  public void setRechargeMethod(RechargeMethod rechargeMethod) {
    this.rechargeMethod = rechargeMethod;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_way")

  public PaymentWay getPaymentWay() {
    return paymentWay;
  }

  public void setPaymentWay(PaymentWay paymentWay) {
    this.paymentWay = paymentWay;
  }

  @Column(name = "present_amount")

  public Double getPresentAmount() {
    return presentAmount;
  }

  public void setPresentAmount(Double presentAmount) {
    this.presentAmount = presentAmount;
  }

  @Column(name = "payee_id")

  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  @Column(name = "audit_time")

  public Long getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
  }

}
