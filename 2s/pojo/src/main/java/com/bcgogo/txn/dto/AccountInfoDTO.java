package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * 结算信息封装类 用于供应商版本 销售单和入库单结算
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-11-21
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */

public class AccountInfoDTO implements Serializable {
  private Long orderId;   //单据id
  private String orderIdStr;//单据id字符串
  private Long shopId;//店铺id

  //销售单结算
  private double settledAmount;//实收金额
  private double cashAmount;//现金
  private double bankAmount; //银联
  private double bankCheckAmount;//支票
  private String bankCheckNo;  //支票号码
  private double accountDebtAmount;//欠款
  private double accountDiscount;//折扣
  private double memberAmount;//会员储值支付
  private String  accountMemberNo;//结算时使用的会员号码
  private String accountMemberPassword;//结算时使用的会员密码
  private String huankuanTime;//还款时间

  //入库单结算
  private double actuallyPaid;//实付金额
  private double cash;//现金
  private double bankCardAmount;//银联
  private double checkAmount; //支票
  private double checkNo; //支票号码
  private double depositAmount; //定金
  private double creditAmount;//挂账
  private double deduction;//折扣


  public String getHuankuanTime() {
    return huankuanTime;
  }

  public void setHuankuanTime(String huankuanTime) {
    this.huankuanTime = huankuanTime;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public double getCashAmount() {
    return cashAmount;
  }

  public void setCashAmount(double cashAmount) {
    this.cashAmount = cashAmount;
  }

  public double getBankAmount() {
    return bankAmount;
  }

  public void setBankAmount(double bankAmount) {
    this.bankAmount = bankAmount;
  }

  public double getBankCheckAmount() {
    return bankCheckAmount;
  }

  public void setBankCheckAmount(double bankCheckAmount) {
    this.bankCheckAmount = bankCheckAmount;
  }

  public String getBankCheckNo() {
    return bankCheckNo;
  }

  public void setBankCheckNo(String bankCheckNo) {
    this.bankCheckNo = bankCheckNo;
  }

  public double getAccountDebtAmount() {
    return accountDebtAmount;
  }

  public void setAccountDebtAmount(double accountDebtAmount) {
    this.accountDebtAmount = accountDebtAmount;
  }

  public double getAccountDiscount() {
    return accountDiscount;
  }

  public void setAccountDiscount(double accountDiscount) {
    this.accountDiscount = accountDiscount;
  }

  public double getMemberAmount() {
    return memberAmount;
  }

  public void setMemberAmount(double memberAmount) {
    this.memberAmount = memberAmount;
  }

  public String getAccountMemberNo() {
    return accountMemberNo;
  }

  public void setAccountMemberNo(String accountMemberNo) {
    this.accountMemberNo = accountMemberNo;
  }

  public String getAccountMemberPassword() {
    return accountMemberPassword;
  }

  public void setAccountMemberPassword(String accountMemberPassword) {
    this.accountMemberPassword = accountMemberPassword;
  }

  public double getActuallyPaid() {
    return actuallyPaid;
  }

  public void setActuallyPaid(double actuallyPaid) {
    this.actuallyPaid = actuallyPaid;
  }

  public double getCash() {
    return cash;
  }

  public void setCash(double cash) {
    this.cash = cash;
  }

  public double getBankCardAmount() {
    return bankCardAmount;
  }

  public void setBankCardAmount(double bankCardAmount) {
    this.bankCardAmount = bankCardAmount;
  }

  public double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(double checkAmount) {
    this.checkAmount = checkAmount;
  }

  public double getCheckNo() {
    return checkNo;
  }

  public void setCheckNo(double checkNo) {
    this.checkNo = checkNo;
  }

  public double getDepositAmount() {
    return depositAmount;
  }

  public void setDepositAmount(double depositAmount) {
    this.depositAmount = depositAmount;
  }

  public double getCreditAmount() {
    return creditAmount;
  }

  public void setCreditAmount(double creditAmount) {
    this.creditAmount = creditAmount;
  }

  public double getDeduction() {
    return deduction;
  }

  public void setDeduction(double deduction) {
    this.deduction = deduction;
  }

  @Override
  public String toString() {
    return "AccountInfoDTO{" +
        "orderId=" + orderId +
        ",shopId="+ shopId +
        ", orderIdStr='" + orderIdStr + '\'' +
        ", settledAmount=" + settledAmount +
        ", cashAmount=" + cashAmount +
        ", bankAmount=" + bankAmount +
        ", bankCheckAmount=" + bankCheckAmount +
        ", bankCheckNo='" + bankCheckNo + '\'' +
        ", accountDebtAmount=" + accountDebtAmount +
        ", accountDiscount=" + accountDiscount +
        ", memberAmount=" + memberAmount +
        ", accountMemberNo='" + accountMemberNo + '\'' +
        ", accountMemberPassword='" + accountMemberPassword + '\'' +
        ", actuallyPaid=" + actuallyPaid +
        ", cash=" + cash +
        ", bankCardAmount=" + bankCardAmount +
        ", checkAmount=" + checkAmount +
        ", checkNo=" + checkNo +
        ", depositAmount=" + depositAmount +
        ", creditAmount=" + creditAmount +
        ", deduction=" + deduction +
        ",huankuanTime:" + huankuanTime +
        '}';
  }
}
