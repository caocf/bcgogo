package com.bcgogo.txn.dto;

import com.bcgogo.stat.dto.BusinessCategoryStatDTO;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-5-5
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
public class BusinessStatDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Long statYear;
  private Long statMonth;
  private Long statDay;
  private double sales; //销售金额，包含施工单中的销售金额
  private double wash;  //洗车金额
  private double service; //服务金额：即 施工单中的施工费
  private double statSum; // 销售 + 洗车 + 服务
  private double productCost; // 商品成本: 销售单和施工单所卖出的产品的成本
  private Long statTime;//每次统计的时间
  private double memberIncome;//会员卡购卡续卡收入
  private double otherIncome;//营业外收入

  //小型记账相关
  private double rentExpenditure; //对应小型记账 房租支出
  private double utilitiesExpenditure; //对应小型记账 水电费支出
  private double salaryExpenditure;//对应小型记账 工资提成
  private double otherExpenditure;//对应小型记账 其他支出
  private double otherExpenditureTotal;//营业外支出总和
  private double orderOtherIncomeCost;//施工单或者销售单 其他费用成本统计

  //营业统计页面 营业外收入
  private Collection<BusinessCategoryStatDTO> incomeList = new ArrayList<BusinessCategoryStatDTO>();
  private Collection<BusinessCategoryStatDTO> expenditureList = new ArrayList<BusinessCategoryStatDTO>();


  public double getOtherExpenditureTotal() {
    return otherExpenditureTotal;
  }

  public void setOtherExpenditureTotal(double otherExpenditureTotal) {
    this.otherExpenditureTotal = NumberUtil.toReserve(otherExpenditureTotal, NumberUtil.MONEY_PRECISION);
  }



  public double getRentExpenditure() {
    return rentExpenditure;
  }

  public void setRentExpenditure(double rentExpenditure) {
    this.rentExpenditure = NumberUtil.toReserve(rentExpenditure,NumberUtil.MONEY_PRECISION);
  }

  public double getUtilitiesExpenditure() {
    return utilitiesExpenditure;
  }

  public void setUtilitiesExpenditure(double utilitiesExpenditure) {
    this.utilitiesExpenditure = NumberUtil.toReserve(utilitiesExpenditure,NumberUtil.MONEY_PRECISION);
  }

  public double getSalaryExpenditure() {
    return salaryExpenditure;
  }

  public void setSalaryExpenditure(double salaryExpenditure) {
    this.salaryExpenditure = NumberUtil.toReserve(salaryExpenditure,NumberUtil.MONEY_PRECISION);
  }

  public double getOtherExpenditure() {
    return otherExpenditure;
  }

  public void setOtherExpenditure(double otherExpenditure) {
    this.otherExpenditure = NumberUtil.toReserve(otherExpenditure,NumberUtil.MONEY_PRECISION);
  }

  public double getOtherIncome() {
    return otherIncome;
  }

  public void setOtherIncome(double otherIncome) {
    this.otherIncome = NumberUtil.toReserve(otherIncome,NumberUtil.MONEY_PRECISION);
  }

  public double getMemberIncome() {
    return memberIncome;
  }

  public void setMemberIncome(double memberIncome) {
    this.memberIncome = NumberUtil.toReserve(memberIncome,NumberUtil.MONEY_PRECISION);
  }

   public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  public void setSales(double sales) {
    this.sales = NumberUtil.toReserve(sales,NumberUtil.MONEY_PRECISION);
  }

  public void setWash(double wash) {
    this.wash = NumberUtil.toReserve(wash,NumberUtil.MONEY_PRECISION);
  }

  public void setService(double service) {
    this.service = NumberUtil.toReserve(service,NumberUtil.MONEY_PRECISION);
  }

  public void setProductCost(double productCost) {
    this.productCost = NumberUtil.toReserve(productCost,NumberUtil.MONEY_PRECISION);
  }

  public double getSales() {
    return sales;
  }

  public double getWash() {
    return wash;
  }

  public double getService() {
    return service;
  }

  public double getProductCost() {
    return productCost;
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

  public double getStatSum() {
    return statSum;
  }

  public void setStatSum(double statSum) {
    this.statSum = NumberUtil.toReserve(statSum,NumberUtil.MONEY_PRECISION);
  }

  public double getOrderOtherIncomeCost() {
    return orderOtherIncomeCost;
  }

  public void setOrderOtherIncomeCost(double orderOtherIncomeCost) {
    this.orderOtherIncomeCost = orderOtherIncomeCost;
  }

  public Collection<BusinessCategoryStatDTO> getIncomeList() {
    return incomeList;
  }

  public void setIncomeList(Collection<BusinessCategoryStatDTO> incomeList) {
    this.incomeList = incomeList;
  }

  public Collection<BusinessCategoryStatDTO> getExpenditureList() {
    return expenditureList;
  }

  public void setExpenditureList(Collection<BusinessCategoryStatDTO> expenditureList) {
    this.expenditureList = expenditureList;
  }

  public void setExpenditureList(List<BusinessCategoryStatDTO> expenditureList) {
    this.expenditureList = expenditureList;
  }

  @Override
  public String toString() {
    return "BusinessStatDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", statYear=" + statYear +
        ", statMonth=" + statMonth +
        ", statDay=" + statDay +
        ", sales=" + sales +
        ", wash=" + wash +
        ", service=" + service +
        ", statSum=" + statSum +
        ", productCost=" + productCost +
        ", statTime=" + statTime +
        ", memberIncome=" + memberIncome +
        ", otherIncome=" + otherIncome +
        ", rentExpenditure=" + rentExpenditure +
        ", utilitiesExpenditure=" + utilitiesExpenditure +
        ", salaryExpenditure=" + salaryExpenditure +
        ", otherExpenditure=" + otherExpenditure +
        ", otherExpenditureTotal=" + otherExpenditureTotal +
        ", orderOtherIncomeCost=" + orderOtherIncomeCost +
        ", incomeList=" + incomeList +
        ", expenditureList=" + expenditureList +
        '}';
  }
}
