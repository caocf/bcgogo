package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IUserCacheService;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:57
 * 分期：每一期结束都会生成下一期的收款纪录
 */
@Entity
@Table(name = "bcgogo_receivable_record")
public class BcgogoReceivableRecord extends LongIdentifier {
  private Long shopId;
  private Double paymentAmount;    //支付金额
  private Double paidAmount;        //已付金额
  private Long operatorId;          //操作人
  private Long operatorTime;        //操作时间
  private Long payeeId;              //收款人（只有线下才会有）
  private String payeeName;              //收款人（只有线下才会有）
  private Long paymentTime;         //收款时间
  private Long submitterId;         //提交人
  private Long submitTime;          //提交日期
  private Long auditorId;           //审核人
  private Long auditTime;           //审核日期
  private BcgogoReceivableStatus status;  //状态  （待支付/待审核/已支付）
  private String memo;

  public BcgogoReceivableRecord() {
    super();
  }

  //硬件新增费用
  public BcgogoReceivableRecord(Long operatorId,BcgogoReceivableOrderDTO dto) {
    this.setOperatorId(operatorId);
    this.setOperatorTime(System.currentTimeMillis());
    this.setShopId(dto.getShopId());
    this.setPaymentAmount(dto.getTotalAmount());
    this.setPaidAmount(0.0);
    this.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
  }

  //软件新增待支付
  public void createUnconstrainedSoftwareReceivable(UnconstrainedSoftwareReceivableDTO dto) {
    this.setOperatorId(dto.getOperatorId());
    this.setOperatorTime(System.currentTimeMillis());
    this.setPayeeId(dto.getPayeeId());
    this.setPayeeName(dto.getPayeeName());
    this.setPaymentTime(dto.getReceivingTime());
    this.setShopId(dto.getShopId());
    this.setPaidAmount(0.0);
    this.setPaymentAmount(dto.getReceivingAmount());
    this.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
  }  //软件新增待支付

  public void createUnconstrainedSoftwareReceived(UnconstrainedSoftwareReceivableDTO dto) {
    this.setOperatorId(dto.getOperatorId());
    this.setOperatorTime(System.currentTimeMillis());
    this.setPayeeId(dto.getPayeeId());
    this.setPayeeName(dto.getPayeeName());
    this.setPaymentTime(dto.getReceivedTime());
    this.setShopId(dto.getShopId());
    this.setPaymentAmount(0.0);
    this.setPaidAmount(dto.getReceivedAmount());
    this.setPaymentAmount(dto.getReceivedAmount());
    this.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
  }

  //软件费用
  public void createSoftwareRecord(long shopId) {
    this.setShopId(shopId);
    this.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
    this.setPaidAmount(0.0);
  }

  //硬件全额付款
  public void fromHardwareOfflineFullPayment(BcgogoReceivableDTO dto) {
    this.setPaymentTime(dto.getPaymentTime());
    this.setPayeeId(dto.getPayeeId());
    this.setPayeeName(dto.getPayeeName());
    this.setSubmitterId(dto.getSubmitterId());
    this.setSubmitTime(System.currentTimeMillis());
    this.setPaidAmount(dto.getPaidAmount());
    this.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
  }

  //软件全额付款
  public void fromSoftwareFullPayment(BcgogoReceivableDTO dto) {
    this.setPaymentTime(dto.getPaymentTime());
    this.setSubmitterId(dto.getSubmitterId());
    this.setSubmitTime(System.currentTimeMillis());
    this.setPayeeId(dto.getPayeeId());
    this.setPayeeName(dto.getPayeeName());
    this.setPaidAmount(dto.getPaidAmount());
    this.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
  }

  //软件剩余付款
  public void fromSoftwareSurplusPayment(BcgogoReceivableDTO dto) {
    this.setPaymentTime(dto.getPaymentTime());
    this.setSubmitterId(dto.getSubmitterId());
    this.setSubmitTime(System.currentTimeMillis());
    this.setPayeeId(dto.getPayeeId());
    this.setPayeeName(dto.getPayeeName());
    this.setPaidAmount(dto.getPaidAmount());
    this.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
  }

  //软件分期付款
  public void fromInstallmentPayment(BcgogoReceivableDTO dto) {
    this.setPaymentTime(dto.getPaymentTime());
    this.setSubmitterId(dto.getSubmitterId());
    this.setPayeeId(dto.getPayeeId());
    this.setSubmitTime(System.currentTimeMillis());
    this.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
    this.setPaidAmount(dto.getPaidAmount());
  }

  //根据分期
  public void fromInstalmentPlanItem(InstalmentPlanItem item) {
    this.setShopId(item.getShopId());
    this.setPaymentTime(item.getEndTime());
    this.setPaymentAmount(item.getPayableAmount());
    this.setPaidAmount(0.0d);
    this.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
  }

  @Column(name = "payee_name")
  public String getPayeeName() {
    return payeeName;
  }

  public void setPayeeName(String payeeName) {
    this.payeeName = payeeName;
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

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "operator_time")
  public Long getOperatorTime() {
    return operatorTime;
  }

  public void setOperatorTime(Long operatorTime) {
    this.operatorTime = operatorTime;
  }

  @Column(name = "payment_amount")
  public Double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  @Column(name = "paid_amount")
  public Double getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(Double paidAmount) {
    this.paidAmount = paidAmount;
  }

  @Column(name = "submitter_id")
  public Long getSubmitterId() {
    return submitterId;
  }

  public void setSubmitterId(Long submitterId) {
    this.submitterId = submitterId;
  }

  @Column(name = "submit_time")
  public Long getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(Long submitTime) {
    this.submitTime = submitTime;
  }

  @Column(name = "auditor_id")
  public Long getAuditorId() {
    return auditorId;
  }

  public void setAuditorId(Long auditorId) {
    this.auditorId = auditorId;
  }

  @Column(name = "audit_time")
  public Long getAuditTime() {
    return auditTime;
  }

  public void setAuditTime(Long auditTime) {
    this.auditTime = auditTime;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public BcgogoReceivableStatus getStatus() {
    return status;
  }

  public void setStatus(BcgogoReceivableStatus status) {
    this.status = status;
  }

  @Column(name = "payment_time")
  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  @Column(name = "payee_id")
  public Long getPayeeId() {
    return payeeId;
  }

  public void setPayeeId(Long payeeId) {
    this.payeeId = payeeId;
  }

  public BcgogoReceivableRecordDTO toDTO() {
    BcgogoReceivableRecordDTO bcgogoReceivableRecordDTO = new BcgogoReceivableRecordDTO();
    bcgogoReceivableRecordDTO.setShopId(this.getShopId());
    bcgogoReceivableRecordDTO.setRecordPaymentAmount(this.getPaymentAmount());
    bcgogoReceivableRecordDTO.setRecordPaidAmount(this.getPaidAmount());
    bcgogoReceivableRecordDTO.setOperatorId(this.getOperatorId());
    bcgogoReceivableRecordDTO.setOperatorTime(this.getOperatorTime());
    bcgogoReceivableRecordDTO.setPayeeId(this.getPayeeId());
    bcgogoReceivableRecordDTO.setRecordPaymentTime(this.getPaymentTime());
    bcgogoReceivableRecordDTO.setSubmitterId(this.getSubmitterId());
    bcgogoReceivableRecordDTO.setSubmitTime(this.getSubmitTime());
    bcgogoReceivableRecordDTO.setAuditorId(this.getAuditorId());
    bcgogoReceivableRecordDTO.setAuditTime(this.getAuditTime());
    bcgogoReceivableRecordDTO.setStatus(this.getStatus()==null?null:this.getStatus().toString());
    return bcgogoReceivableRecordDTO;

  }

  public void fromSmsRecharge(SmsRechargeDTO smsRechargeDTO) {
    if(smsRechargeDTO == null) return;
    this.setShopId(smsRechargeDTO.getShopId());
    this.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
    this.setPaymentAmount(smsRechargeDTO.getRechargeAmount());
    this.setPaidAmount(smsRechargeDTO.getRechargeAmount());
    this.setPayeeId(smsRechargeDTO.getPayeeId());
    this.setPaymentTime(smsRechargeDTO.getPayTime());
    this.setPayeeName(smsRechargeDTO.getPayeeName());
    this.setSubmitterId(smsRechargeDTO.getUserId());
    this.setSubmitTime(smsRechargeDTO.getPayTime());
  }
}
