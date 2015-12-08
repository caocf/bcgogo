package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.txn.finance.SmsCategory;

/**
 * User: ZhangJuntao
 * Date: 13-3-26
 * Time: 下午3:13
 */
public class BcgogoSmsRecordDTO {
  private Long id;
  private Long operatorId;
  private Long operateTime;
  private SmsCategory smsCategory;
  private Double balance = 0.0d;
  private Long number = 0L;
  private Long rechargeTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SmsCategory getSmsCategory() {
    return smsCategory;
  }

  public void setSmsCategory(SmsCategory smsCategory) {
    this.smsCategory = smsCategory;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }


  public Long getRechargeTime() {
    return rechargeTime;
  }

  public void setRechargeTime(Long rechargeTime) {
    this.rechargeTime = rechargeTime;
  }
}
