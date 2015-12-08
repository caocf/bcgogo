package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RunningStatDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-8-30
 * Time: 上午10:47
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "running_stat")
public class RunningStat extends LongIdentifier {

  public RunningStat() {
  }

  public RunningStat fromDTO(RunningStatDTO runningStatDTO, boolean setId) {
    if (runningStatDTO == null) {
      return null;
    }
    if (setId) {
      setId(runningStatDTO.getId());
    }
    this.shopId = runningStatDTO.getShopId();
    this.statYear = runningStatDTO.getStatYear();
    this.statMonth = runningStatDTO.getStatMonth();
    this.statDay = runningStatDTO.getStatDay();
    this.statDate = runningStatDTO.getStatDate();

    this.runningSum = runningStatDTO.getRunningSum();
    this.incomeSum = runningStatDTO.getIncomeSum();
    this.cashIncome = runningStatDTO.getCashIncome();
    this.chequeIncome = runningStatDTO.getChequeIncome();
    this.unionPayIncome = runningStatDTO.getUnionPayIncome();
    this.expenditureSum = runningStatDTO.getExpenditureSum();
    this.cashExpenditure = runningStatDTO.getCashExpenditure();
    this.chequeExpenditure = runningStatDTO.getChequeExpenditure();
    this.unionPayExpenditure = runningStatDTO.getUnionPayExpenditure();
    this.memberPayIncome = runningStatDTO.getMemberPayIncome();
    this.debtNewExpenditure = runningStatDTO.getDebtNewExpenditure();
    this.debtNewIncome = runningStatDTO.getDebtNewIncome();
    // add by zhuj
    this.customerDepositPayIncome = runningStatDTO.getCustomerDepositPayIncome();
    this.customerDepositPayExpenditure = runningStatDTO.getCustomerDepositExpenditure();
    this.depositPayIncome = runningStatDTO.getDepositPayIncome();
    this.depositPayExpenditure = runningStatDTO.getDepositPayExpenditure();
    this.debtWithdrawalExpenditure = runningStatDTO.getDebtWithdrawalExpenditure();
    this.debtWithdrawalIncome = runningStatDTO.getDebtWithdrawalIncome();
    this.strikeAmountExpenditure = runningStatDTO.getStrikeAmountExpenditure();
    this.strikeAmountIncome = runningStatDTO.getStrikeAmountIncome();
    this.customerDebtDiscount = runningStatDTO.getCustomerDebtDiscount();
    this.supplierDebtDiscount = runningStatDTO.getSupplierDebtDiscount();
    this.customerReturnDebt = runningStatDTO.getCustomerReturnDebt();
    this.supplierReturnDebt = runningStatDTO.getSupplierReturnDebt();
    this.couponIncome = runningStatDTO.getCouponIncome();
    this.couponExpenditure = runningStatDTO.getCouponExpenditure();
    return this;
  }

  public RunningStatDTO toDTO() {
    RunningStatDTO runningStatDTO = new RunningStatDTO();

    runningStatDTO.setId(this.getId());
    runningStatDTO.setShopId(this.getShopId());
    runningStatDTO.setStatYear(this.getStatYear());
    runningStatDTO.setStatMonth(this.getStatMonth());
    runningStatDTO.setStatDay(this.getStatDay());
    runningStatDTO.setRunningStatDateStr(this.getStatYear() + "年" + this.getStatMonth() + "月" + this.getStatDay() + "日");
    runningStatDTO.setStatDate(this.getStatDate());

    runningStatDTO.setRunningSum(this.getRunningSum());
    runningStatDTO.setIncomeSum(this.getIncomeSum());
    runningStatDTO.setCashIncome(this.getCashIncome());
    runningStatDTO.setChequeIncome(this.getChequeIncome());
    runningStatDTO.setUnionPayIncome(this.getUnionPayIncome());
    runningStatDTO.setExpenditureSum(this.getExpenditureSum());
    runningStatDTO.setCashExpenditure(this.getCashExpenditure());
    runningStatDTO.setChequeExpenditure(this.getChequeExpenditure());
    runningStatDTO.setUnionPayExpenditure(this.getUnionPayExpenditure());

    runningStatDTO.setMemberPayIncome(this.getMemberPayIncome());
    runningStatDTO.setDebtNewExpenditure(this.getDebtNewExpenditure());
    runningStatDTO.setDebtNewIncome(this.getDebtNewIncome());
    // add by zhuj
    runningStatDTO.setCustomerDepositPayIncome(this.getCustomerDepositPayIncome());
    runningStatDTO.setCustomerDepositExpenditure(this.getCustomerDepositPayExpenditure());
    runningStatDTO.setDepositPayExpenditure(this.getDepositPayExpenditure());
    runningStatDTO.setDepositPayIncome(this.getDepositPayIncome());

    runningStatDTO.setDebtWithdrawalExpenditure(this.getDebtWithdrawalExpenditure());
    runningStatDTO.setDebtWithdrawalIncome(this.getDebtWithdrawalIncome());
    runningStatDTO.setStrikeAmountExpenditure(this.getStrikeAmountExpenditure());
    runningStatDTO.setStrikeAmountIncome(this.getStrikeAmountIncome());
    runningStatDTO.setCustomerDebtDiscount(this.getCustomerDebtDiscount());
    runningStatDTO.setSupplierDebtDiscount(this.getSupplierDebtDiscount());
    runningStatDTO.setCustomerReturnDebt(this.getCustomerReturnDebt());
    runningStatDTO.setSupplierReturnDebt(this.getSupplierReturnDebt());
    runningStatDTO.setCouponIncome(this.getCouponIncome());
    runningStatDTO.setCouponExpenditure(this.getCouponExpenditure());
    return runningStatDTO;
  }


  private Long shopId; //统计的shop_id
  private Long statYear; //统计的年份
  private Long statMonth; //统计的月份
  private Long statDay; //统计的日期
  private Long statDate;//统计时间

  /**收入相关*/
  private double runningSum; //流水总和：收入总和 - 支出总和

  private double incomeSum;  //收入总和：现金 + 银行卡 + 支票

  private double cashIncome; //现金收入总和
  private double chequeIncome;    //支票收入总和
  private double unionPayIncome; //银联收入总和

  private double memberPayIncome;  //会员支付总和

  private double debtNewIncome; //客户新增欠款
  private double debtWithdrawalIncome; //客户欠款回笼

  private double depositPayIncome; // 供应商定金充值总额

  /**支出相关*/
  private double expenditureSum; //支出总和：现金 + 银行卡 + 支票

  private double cashExpenditure; //现金支出总和
  private double chequeExpenditure; //支票支出总和
  private double unionPayExpenditure; //银联支出总和

  private double debtNewExpenditure; //供应商新增欠款
  private double debtWithdrawalExpenditure; //供应商欠款回笼总和

  private double depositPayExpenditure; //供应商订金总和


  private double customerDepositPayExpenditure; // 客户使用预收款总和
  private double customerDepositPayIncome; // 客户预收款充值总和

  private double strikeAmountExpenditure; //供应商冲账
  private double strikeAmountIncome;//冲账总和 销售退货单总账

  private double customerDebtDiscount;//客户欠款结算时产生的折扣
  private double supplierDebtDiscount; //供应商欠款结算时产生的折扣

  private double customerReturnDebt;

  private double supplierReturnDebt;

  private Double couponIncome;//代金券收入总和
  private Double couponExpenditure;//代金券支出总和

  @Column(name = "customer_debt_discount")
  public double getCustomerDebtDiscount() {
    return customerDebtDiscount;
  }

  public void setCustomerDebtDiscount(double customerDebtDiscount) {
    this.customerDebtDiscount = customerDebtDiscount;
  }

  @Column(name = "supplier_debt_discount")
  public double getSupplierDebtDiscount() {
    return supplierDebtDiscount;
  }

  public void setSupplierDebtDiscount(double supplierDebtDiscount) {
    this.supplierDebtDiscount = supplierDebtDiscount;
  }



  @Column(name = "member_pay_income")
  public double getMemberPayIncome() {
    return memberPayIncome;
  }

  public void setMemberPayIncome(double memberPayIncome) {
    this.memberPayIncome = memberPayIncome;
  }

  @Column(name = "debt_new_income")
  public double getDebtNewIncome() {
    return debtNewIncome;
  }

  public void setDebtNewIncome(double debtNewIncome) {
    this.debtNewIncome = debtNewIncome;
  }

  @Column(name = "debt_withdrawal_income")
  public double getDebtWithdrawalIncome() {
    return debtWithdrawalIncome;
  }

  public void setDebtWithdrawalIncome(double debtWithdrawalIncome) {
    this.debtWithdrawalIncome = debtWithdrawalIncome;
  }

  @Column(name = "deposit_pay_income")
  public double getDepositPayIncome() {
    return depositPayIncome;
  }

  public void setDepositPayIncome(double depositPayIncome) {
    this.depositPayIncome = depositPayIncome;
  }

  @Column(name = "deposit_pay_expenditure")
  public double getDepositPayExpenditure() {
    return depositPayExpenditure;
  }

  public void setDepositPayExpenditure(double depositPayExpenditure) {
    this.depositPayExpenditure = depositPayExpenditure;
  }

  @Column(name = "customer_deposit_pay_expenditure")
  public double getCustomerDepositPayExpenditure() {
    return customerDepositPayExpenditure;
  }

  public void setCustomerDepositPayExpenditure(double customerDepositPayExpenditure) {
    this.customerDepositPayExpenditure = NumberUtil.toReserve(customerDepositPayExpenditure,NumberUtil.MONEY_PRECISION);
  }

  @Column(name = "customer_deposit_pay_income")
  public double getCustomerDepositPayIncome() {
    return customerDepositPayIncome;
  }

   public void setCustomerDepositPayIncome(double customerDepositPayIncome) {
     this.customerDepositPayIncome = NumberUtil.toReserve(customerDepositPayIncome,NumberUtil.MONEY_PRECISION);
   }

  @Column(name = "debt_new_expenditure")
  public double getDebtNewExpenditure() {
    return debtNewExpenditure;
  }

  public void setDebtNewExpenditure(double debtNewExpenditure) {
    this.debtNewExpenditure = debtNewExpenditure;
  }

  @Column(name = "debt_withdrawal_expenditure")
  public double getDebtWithdrawalExpenditure() {
    return debtWithdrawalExpenditure;
  }

  public void setDebtWithdrawalExpenditure(double debtWithdrawalExpenditure) {
    this.debtWithdrawalExpenditure = debtWithdrawalExpenditure;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "stat_year")
  public Long getStatYear() {
    return statYear;
  }

  public void setStatYear(Long statYear) {
    this.statYear = statYear;
  }

  @Column(name = "stat_month")
  public Long getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Long statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name = "stat_day")
  public Long getStatDay() {
    return statDay;
  }

  public void setStatDay(Long statDay) {
    this.statDay = statDay;
  }

  @Column(name = "stat_date")
  public Long getStatDate() {
    return statDate;
  }

  public void setStatDate(Long statDate) {
    this.statDate = statDate;
  }

  @Column(name = "running_sum")
  public double getRunningSum() {
    return runningSum;
  }

  public void setRunningSum(double runningSum) {
    this.runningSum = runningSum;
  }

  @Column(name = "income_sum")
  public double getIncomeSum() {
    return incomeSum;
  }

  public void setIncomeSum(double incomeSum) {
    this.incomeSum = incomeSum;
  }

  @Column(name = "cash_income")
  public double getCashIncome() {
    return cashIncome;
  }

  public void setCashIncome(double cashIncome) {
    this.cashIncome = cashIncome;
  }

  @Column(name = "cheque_income")
  public double getChequeIncome() {
    return chequeIncome;
  }

  public void setChequeIncome(double chequeIncome) {
    this.chequeIncome = chequeIncome;
  }

  @Column(name = "union_pay_income")
  public double getUnionPayIncome() {
    return unionPayIncome;
  }

  public void setUnionPayIncome(double unionPayIncome) {
    this.unionPayIncome = unionPayIncome;
  }

  @Column(name = "expenditure_sum")
  public double getExpenditureSum() {
    return expenditureSum;
  }

  public void setExpenditureSum(double expenditureSum) {
    this.expenditureSum = expenditureSum;
  }

  @Column(name = "cash_expenditure")
  public double getCashExpenditure() {
    return cashExpenditure;
  }

  public void setCashExpenditure(double cashExpenditure) {
    this.cashExpenditure = cashExpenditure;
  }

  @Column(name = "cheque_expenditure")
  public double getChequeExpenditure() {
    return chequeExpenditure;
  }

  public void setChequeExpenditure(double chequeExpenditure) {
    this.chequeExpenditure = chequeExpenditure;
  }

  @Column(name = "union_pay_expenditure")
  public double getUnionPayExpenditure() {
    return unionPayExpenditure;
  }

  public void setUnionPayExpenditure(double unionPayExpenditure) {
    this.unionPayExpenditure = unionPayExpenditure;
  }

  @Column(name="strike_amount_expenditure")
  public double getStrikeAmountExpenditure() {
    return strikeAmountExpenditure;
  }

  public void setStrikeAmountExpenditure(double strikeAmountExpenditure) {
    this.strikeAmountExpenditure = strikeAmountExpenditure;
  }

  @Column(name="strike_amount_income")
  public double getStrikeAmountIncome() {
    return NumberUtil.doubleVal(strikeAmountIncome);
  }

  public void setStrikeAmountIncome(double strikeAmountIncome) {
    this.strikeAmountIncome = strikeAmountIncome;
  }

  @Column(name="customer_return_debt")
  public double getCustomerReturnDebt() {
    return customerReturnDebt;
  }

  public void setCustomerReturnDebt(double customerReturnDebt) {
    this.customerReturnDebt = customerReturnDebt;
  }

  @Column(name="supplier_return_debt")
  public double getSupplierReturnDebt() {
    return supplierReturnDebt;
  }

  public void setSupplierReturnDebt(double supplierReturnDebt) {
    this.supplierReturnDebt = supplierReturnDebt;
  }

  @Column(name="coupon_income")
  public Double getCouponIncome() {
    if(null==couponIncome){
      return 0D;
    }
    return couponIncome;
  }

  public void setCouponIncome(Double couponIncome) {
    this.couponIncome = couponIncome;
  }

  @Column(name="coupon_expenditure")
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
