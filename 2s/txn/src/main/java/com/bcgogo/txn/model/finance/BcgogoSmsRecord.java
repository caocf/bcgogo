package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.BcgogoSmsRecordDTO;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 * bcgogo 短信 记录
 */
@Entity
@Table(name = "bcgogo_sms_record")
public class BcgogoSmsRecord extends LongIdentifier {
  private Long operatorId;
  private Long operateTime;
  private SmsCategory smsCategory;
  private Double balance = 0.0d;
  private Long number = 0L;
  private Long rechargeTime; //充值时间


  public BcgogoSmsRecord() {
    super();
  }

  public BcgogoSmsRecord(BcgogoSmsRecordDTO dto) {
    this.setBalance(dto.getBalance());
    this.setNumber(dto.getNumber());
    this.setSmsCategory(SmsCategory.BCGOGO_RECHARGE);
    this.setOperateTime(System.currentTimeMillis());
    this.setOperatorId(dto.getOperatorId());
    this.setRechargeTime(dto.getRechargeTime());
  }

  public BcgogoSmsRecord(ShopSmsRecordDTO dto) {
    this.setBalance(dto.getBalance());
    this.setNumber(dto.getNumber());
    this.setSmsCategory(dto.getSmsCategory());
    this.setOperateTime(dto.getOperateTime() == null ? System.currentTimeMillis() : dto.getOperateTime());
    this.setOperatorId(dto.getOperatorId());
  }

  public BcgogoSmsRecord(Double balance, Long number) {
    this.setBalance(balance);
    this.setNumber(number);
  }

  public BcgogoSmsRecordDTO toDTO() {
    BcgogoSmsRecordDTO dto = new BcgogoSmsRecordDTO();
    dto.setId(this.getId());
    dto.setBalance(this.getBalance());
    dto.setNumber(this.getNumber());
    dto.setSmsCategory(this.getSmsCategory());
    dto.setOperateTime(this.getOperateTime());
    dto.setOperatorId(this.getOperatorId());
    dto.setRechargeTime(this.getRechargeTime());
    return dto;
  }

  public void fromDTO(BcgogoSmsRecordDTO dto) {
    this.setId(dto.getId());
    this.setBalance(dto.getBalance());
    this.setNumber(dto.getNumber());
    this.setSmsCategory(dto.getSmsCategory());
    this.setOperateTime(dto.getOperateTime());
    this.setOperatorId(dto.getOperatorId());
    this.setRechargeTime(dto.getRechargeTime());
  }

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "operate_time")
  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_category")
  public SmsCategory getSmsCategory() {
    return smsCategory;
  }

  public void setSmsCategory(SmsCategory smsCategory) {
    this.smsCategory = smsCategory;
  }

  @Column(name = "balance")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Column(name = "number")
  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  @Column(name = "recharge_time")
  public Long getRechargeTime() {
    return rechargeTime;
  }

  public void setRechargeTime(Long rechargeTime) {
    this.rechargeTime = rechargeTime;
  }
}
