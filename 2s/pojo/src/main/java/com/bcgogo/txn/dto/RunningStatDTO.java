package com.bcgogo.txn.dto;

import com.bcgogo.utils.NumberUtil;

/**
 * 流水统计表对应DTO类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-8-30
 * Time: 上午11:06
 * To change this template use File | Settings | File Templates.
 */
public class RunningStatDTO {

  private Long id;
  private Long shopId; //统计的shop_id
  private Long statYear; //统计的年份
  private Long statMonth; //统计的月份
  private Long statDay; //统计的日期
  private Long statDate;//统计时间
  private double currentDebtSum; //当前欠款总和

  private Long accountDate;//页面传来的单据结算日期

  private double runningSum; //流水总和：收入总和 - 支出总和

  private double incomeSum;  //收入总和：现金 + 银行卡 + 支票
  private double cashIncome; //现金收入总和
  private double chequeIncome;    //支票收入总和
  private double unionPayIncome; //银联收入总和


  private double expenditureSum; //支出总和：现金 + 银行卡 + 支票
  private double cashExpenditure; //现金支出总和
  private double chequeExpenditure; //支票支出总和
  private double unionPayExpenditure; //银联支出总和

  private String runningStatDateStr;

  private double memberPayIncome;  //会员支付总和
  private double debtNewIncome; //客户新增欠款
  private double debtWithdrawalIncome; //客户欠款回笼

  private double customerDepositExpenditure; // 客户预收款使用统计
  private double customerDepositPayIncome; // 客户预收款充值统计

  private double depositPayIncome;  // 定金充值 供应商详细信息充值部分

  private double depositPayExpenditure; //供应商订金总和 是指 在入库单 使用定金的总和 而不是 付给供应商定金总和
  private double debtNewExpenditure; //供应商新增欠款
  private double debtWithdrawalExpenditure; //供应商欠款回笼总和

  private double strikeAmountExpenditure; //冲账总和 入库退货单冲账
  private double strikeAmountIncome;//冲账总和 销售退货单总账

  private double customerDebtDiscount;//客户欠款结算时产生的折扣
  private double supplierDebtDiscount; //供应商欠款结算时产生的折扣

  private double customerReturnDebt; //店铺欠客户money
  private double customerTotalReceivable;
  private double customerTotalPayable;
  private double supplierTotalReceivable;
  private double supplierTotalPayable;


  private double supplierReturnDebt;  //店铺欠供应商money

  private Double couponIncome;//代金券收入总和
  private Double couponExpenditure;//代金券支出总和

  public double getCustomerTotalReceivable() {
    return customerTotalReceivable;
  }

  public void setCustomerTotalReceivable(double customerTotalReceivable) {
    this.customerTotalReceivable = customerTotalReceivable;
  }

  public double getCustomerTotalPayable() {
    return customerTotalPayable;
  }

  public void setCustomerTotalPayable(double customerTotalPayable) {
    this.customerTotalPayable = customerTotalPayable;
  }

  public double getSupplierTotalReceivable() {
    return supplierTotalReceivable;
  }

  public void setSupplierTotalReceivable(double supplierTotalReceivable) {
    this.supplierTotalReceivable = supplierTotalReceivable;
  }

  public double getSupplierTotalPayable() {
    return supplierTotalPayable;
  }

  public void setSupplierTotalPayable(double supplierTotalPayable) {
    this.supplierTotalPayable = supplierTotalPayable;
  }

  public double getCustomerDebtDiscount() {
    return customerDebtDiscount;
  }

  public void setCustomerDebtDiscount(double customerDebtDiscount) {
    this.customerDebtDiscount = NumberUtil.toReserve(customerDebtDiscount, NumberUtil.MONEY_PRECISION);
  }

  public double getSupplierDebtDiscount() {
    return supplierDebtDiscount;
  }

  public void setSupplierDebtDiscount(double supplierDebtDiscount) {
    this.supplierDebtDiscount = NumberUtil.toReserve(supplierDebtDiscount, NumberUtil.MONEY_PRECISION);
  }

  public String getRunningStatDateStr() {
    return runningStatDateStr;
  }

  public void setRunningStatDateStr(String runningStatDateStr) {
    this.runningStatDateStr = runningStatDateStr;
  }

  @Override
  public String toString() {
    return "RunningStatDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", statYear=" + statYear +
        ", statMonth=" + statMonth +
        ", statDay=" + statDay +
        ", statDate=" + statDate +
        ", currentDebtSum=" + currentDebtSum +
        ", accountDate=" + accountDate +
        ", runningSum=" + runningSum +
        ", incomeSum=" + incomeSum +
        ", cashIncome=" + cashIncome +
        ", chequeIncome=" + chequeIncome +
        ", unionPayIncome=" + unionPayIncome +
        ", expenditureSum=" + expenditureSum +
        ", cashExpenditure=" + cashExpenditure +
        ", chequeExpenditure=" + chequeExpenditure +
        ", unionPayExpenditure=" + unionPayExpenditure +
        ", runningStatDateStr='" + runningStatDateStr + '\'' +
        ", memberPayIncome=" + memberPayIncome +
        ", debtNewIncome=" + debtNewIncome +
        ", debtWithdrawalIncome=" + debtWithdrawalIncome +
        ", customerDepositExpenditure=" + customerDepositExpenditure +
        ", customerDepositPayIncome=" + customerDepositPayIncome +
        ", depositPayIncome=" + depositPayIncome +
        ", depositPayExpenditure=" + depositPayExpenditure +
        ", debtNewExpenditure=" + debtNewExpenditure +
        ", debtWithdrawalExpenditure=" + debtWithdrawalExpenditure +
        ", strikeAmountExpenditure=" + strikeAmountExpenditure +
        ", strikeAmountIncome=" + strikeAmountIncome +
        ", customerDebtDiscount=" + customerDebtDiscount +
        ", supplierDebtDiscount=" + supplierDebtDiscount +
        ", customerReturnDebt=" + customerReturnDebt +
        ", customerTotalReceivable=" + customerTotalReceivable +
        ", customerTotalPayable=" + customerTotalPayable +
        ", supplierTotalReceivable=" + supplierTotalReceivable +
        ", supplierTotalPayable=" + supplierTotalPayable +
        ", supplierReturnDebt=" + supplierReturnDebt +
        '}';
  }

  public double getMemberPayIncome() {
    return memberPayIncome;
  }

  public void setMemberPayIncome(double memberPayIncome) {
    this.memberPayIncome = NumberUtil.toReserve(memberPayIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getDebtNewIncome() {
    return debtNewIncome;
  }

  public void setDebtNewIncome(double debtNewIncome) {
    this.debtNewIncome = NumberUtil.toReserve(debtNewIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getDebtWithdrawalIncome() {
    return debtWithdrawalIncome;
  }

  public void setDebtWithdrawalIncome(double debtWithdrawalIncome) {
    this.debtWithdrawalIncome = NumberUtil.toReserve(debtWithdrawalIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getDepositPayIncome() {
    return depositPayIncome;
  }

  public void setDepositPayIncome(double depositPayIncome) {
    this.depositPayIncome = NumberUtil.toReserve(depositPayIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getDepositPayExpenditure() {
    return depositPayExpenditure;
  }

  public void setDepositPayExpenditure(double depositPayExpenditure) {
    this.depositPayExpenditure = NumberUtil.toReserve(depositPayExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getCustomerDepositExpenditure() {
    return customerDepositExpenditure;
  }

  public void setCustomerDepositExpenditure(double customerDepositExpenditure) {
    this.customerDepositExpenditure = NumberUtil.toReserve(customerDepositExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getCustomerDepositPayIncome() {
    return customerDepositPayIncome;
  }

  public void setCustomerDepositPayIncome(double customerDepositPayIncome) {
    this.customerDepositPayIncome = customerDepositPayIncome;
  }


  public double getDebtNewExpenditure() {
    return debtNewExpenditure;
  }

  public void setDebtNewExpenditure(double debtNewExpenditure) {
    this.debtNewExpenditure = NumberUtil.toReserve(debtNewExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getDebtWithdrawalExpenditure() {
    return debtWithdrawalExpenditure;
  }

  public void setDebtWithdrawalExpenditure(double debtWithdrawalExpenditure) {
    this.debtWithdrawalExpenditure = NumberUtil.toReserve(debtWithdrawalExpenditure, NumberUtil.MONEY_PRECISION);
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

  public Long getStatYear() {
    return statYear;
  }

  public void setStatYear(Long statYear) {
    this.statYear = statYear;
  }

  public Long getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Long statMonth) {
    this.statMonth = statMonth;
  }

  public Long getStatDay() {
    return statDay;
  }

  public void setStatDay(Long statDay) {
    this.statDay = statDay;
  }

  public Long getStatDate() {
    return statDate;
  }

  public void setStatDate(Long statDate) {
    this.statDate = statDate;
  }

  public double getCurrentDebtSum() {
    return currentDebtSum;
  }

  public void setCurrentDebtSum(double currentDebtSum) {
    this.currentDebtSum = NumberUtil.toReserve(currentDebtSum, NumberUtil.MONEY_PRECISION);
  }

  public double getRunningSum() {
    return runningSum;
  }

  public void setRunningSum(double runningSum) {
    this.runningSum = NumberUtil.toReserve(runningSum, NumberUtil.MONEY_PRECISION);
  }

  public double getIncomeSum() {
    return incomeSum;
  }

  public void setIncomeSum(double incomeSum) {
    this.incomeSum = NumberUtil.toReserve(incomeSum, NumberUtil.MONEY_PRECISION);
  }

  public double getCashIncome() {
    return cashIncome;
  }

  public void setCashIncome(double cashIncome) {
    this.cashIncome = NumberUtil.toReserve(cashIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getChequeIncome() {
    return chequeIncome;
  }

  public void setChequeIncome(double chequeIncome) {
    this.chequeIncome = NumberUtil.toReserve(chequeIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getUnionPayIncome() {
    return unionPayIncome;
  }

  public void setUnionPayIncome(double unionPayIncome) {
    this.unionPayIncome = NumberUtil.toReserve(unionPayIncome, NumberUtil.MONEY_PRECISION);
  }

  public double getExpenditureSum() {
    return expenditureSum;
  }

  public void setExpenditureSum(double expenditureSum) {
    this.expenditureSum = NumberUtil.toReserve(expenditureSum, NumberUtil.MONEY_PRECISION);
  }

  public double getCashExpenditure() {
    return cashExpenditure;
  }

  public void setCashExpenditure(double cashExpenditure) {
    this.cashExpenditure = NumberUtil.toReserve(cashExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getChequeExpenditure() {
    return chequeExpenditure;
  }

  public void setChequeExpenditure(double chequeExpenditure) {
    this.chequeExpenditure = NumberUtil.toReserve(chequeExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getUnionPayExpenditure() {
    return unionPayExpenditure;
  }

  public void setUnionPayExpenditure(double unionPayExpenditure) {
    this.unionPayExpenditure = NumberUtil.toReserve(unionPayExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getStrikeAmountExpenditure() {
    return strikeAmountExpenditure;
  }

  public void setStrikeAmountExpenditure(double strikeAmountExpenditure) {
    this.strikeAmountExpenditure = NumberUtil.toReserve(strikeAmountExpenditure, NumberUtil.MONEY_PRECISION);
  }

  public double getStrikeAmountIncome() {
    return strikeAmountIncome;
  }

  public void setStrikeAmountIncome(double strikeAmountIncome) {
    this.strikeAmountIncome = NumberUtil.toReserve(strikeAmountIncome,NumberUtil.MONEY_PRECISION);
  }

  public double getCustomerReturnDebt() {
    return customerReturnDebt;
  }

  public void setCustomerReturnDebt(double customerReturnDebt) {
    this.customerReturnDebt = customerReturnDebt;
  }

  public double getSupplierReturnDebt() {
    return supplierReturnDebt;
  }

  public void setSupplierReturnDebt(double supplierReturnDebt) {
    this.supplierReturnDebt = supplierReturnDebt;
  }

  public Long getAccountDate() {
    return accountDate;
  }

  public void setAccountDate(Long accountDate) {
    this.accountDate = accountDate;
  }

  public Double getCouponIncome() {
    if(null==couponIncome){
      return 0D;
    }
    return couponIncome;
  }

  public void setCouponIncome(Double couponIncome) {
    this.couponIncome = couponIncome;
  }

  public Double getCouponExpenditure() {
    if(null==couponExpenditure){
      return 0D;
    }
    return couponExpenditure;
  }

  public void setCouponExpenditure(Double couponExpenditure) {
    this.couponExpenditure = couponExpenditure;
  }
}
