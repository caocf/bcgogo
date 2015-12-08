package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * 客户付款信息封装类
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-8
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
public class ReceivableHistoryDTO  implements Serializable {
  private Long id;
  /*店面ID*/
  private Long shopId;
  private Double total;
  /* 扣款*/
  private Double discount;
  /*欠款挂账*/
  private Double debt;
  /*现金*/
  private Double cash;
  /*银行卡*/
  private Double bankCardAmount;
  /*支票*/
  private Double checkAmount;
  /*支票号码*/
  private String checkNo;
  /** 预收款支付 */
  private Double deposit;
  /*会员支付*/
  private Double memberBalancePay;
  /*会员id*/
  private Long memberId;
  /*会员号码*/
  private String memberNo;
  /*冲账*/
  private Double strikeAmount;
  /*实收*/
  private Double settledAmount;
  /*客户id*/
  private Long customerId;
  /*付款时间*/
  private Long receivableDate;

  private String receiver;     //结算人
  private Long receiverId;//结算人id

  public ReceivableHistoryDTO(){

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public Double getDebt() {
    return debt;
  }

  public void setDebt(Double debt) {
    this.debt = debt;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getBankCardAmount() {
    return bankCardAmount;
  }

  public void setBankCardAmount(Double bankCardAmount) {
    this.bankCardAmount = bankCardAmount;
  }

  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  public String getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(String checkNo) {
    this.checkNo = checkNo;
  }

  public Double getMemberBalancePay() {
    return memberBalancePay;
  }

  public void setMemberBalancePay(Double memberBalancePay) {
    this.memberBalancePay = memberBalancePay;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public String getMemberNo() {
    return memberNo;
  }

  public void setMemberNo(String memberNo) {
    this.memberNo = memberNo;
  }

  public Double getStrikeAmount() {
    return strikeAmount;
  }

  public void setStrikeAmount(Double strikeAmount) {
    this.strikeAmount = strikeAmount;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getReceivableDate() {
    return receivableDate;
  }

  public void setReceivableDate(Long receivableDate) {
    this.receivableDate = receivableDate;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public Long getReceiverId() {
    return receiverId;
  }

  public void setReceiverId(Long receiverId) {
    this.receiverId = receiverId;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }
}
