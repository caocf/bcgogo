package com.bcgogo.stat.dto;

import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.CalculateType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.utils.NumberUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-10-29
 * Time: 下午4:59
 * To change this template use File | Settings | File Templates.
 */
public class BusinessCategoryStatDTO implements Serializable {
  //店面Id
  private Long id;
  private Long shopId;
  private String businessCategory;
  private Long businessCategoryId;
  private Long statDate;
  private Long statDay;
  private Long statMonth;
  private Long statYear;
  private BusinessCategoryStatType statType;
  private Double checkAmount;
  private Double unionPay;
  private Double cash;
  private Double total;
  private MoneyCategory moneyCategory;

  private Double dayTotal;
  private Double monthTotal;
  private Double yearTotal;

  public BusinessCategoryStatDTO() {
    setCheckAmount(0D);
    setUnionPay(0D);
    setCash(0D);
    setTotal(0D);
    setDayTotal(0D);
    setMonthTotal(0D);
    setYearTotal(0D);
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

  public String getBusinessCategory() {
    return businessCategory;
  }

  public void setBusinessCategory(String businessCategory) {
    this.businessCategory = businessCategory;
  }

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public Long getStatDate() {
    return statDate;
  }

  public void setStatDate(Long statDate) {
    this.statDate = statDate;
  }

  public Long getStatDay() {
    return statDay;
  }

  public void setStatDay(Long statDay) {
    this.statDay = statDay;
  }

  public Long getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Long statMonth) {
    this.statMonth = statMonth;
  }

  public Long getStatYear() {
    return statYear;
  }

  public void setStatYear(Long statYear) {
    this.statYear = statYear;
  }

  public BusinessCategoryStatType getStatType() {
    return statType;
  }

  public void setStatType(BusinessCategoryStatType statType) {
    this.statType = statType;
  }

  public Double getCheckAmount() {
    return checkAmount;
  }

  public void setCheckAmount(Double checkAmount) {
    this.checkAmount = checkAmount;
  }

  public Double getUnionPay() {
    return unionPay;
  }

  public void setUnionPay(Double unionPay) {
    this.unionPay = unionPay;
  }

  public Double getCash() {
    return cash;
  }

  public void setCash(Double cash) {
    this.cash = cash;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public MoneyCategory getMoneyCategory() {
    return moneyCategory;
  }

  public void setMoneyCategory(MoneyCategory moneyCategory) {
    this.moneyCategory = moneyCategory;
  }

  public Double getDayTotal() {
    return dayTotal;
  }

  public void setDayTotal(Double dayTotal) {
    this.dayTotal = dayTotal;
  }

  public Double getMonthTotal() {
    return monthTotal;
  }

  public void setMonthTotal(Double monthTotal) {
    this.monthTotal = monthTotal;
  }

  public Double getYearTotal() {
    return yearTotal;
  }

  public void setYearTotal(Double yearTotal) {
    this.yearTotal = yearTotal;
  }

  public BusinessCategoryStatDTO calculateFromBusinessAccountDTO(BusinessAccountDTO accountDTO,Long statDate,Long statYear,Long statMonth,Long statDay,
                                                           BusinessCategoryStatType statType,CalculateType calculateType) {
    this.setShopId(accountDTO.getShopId());
    this.setBusinessCategory(accountDTO.getAccountCategory());
    this.setBusinessCategoryId(accountDTO.getAccountCategoryId());
    this.setStatDate(statDate);
    this.setStatYear(statYear);
    this.setStatMonth(statMonth);
    this.setStatDay(statDay);
    this.setStatType(statType);
    this.setMoneyCategory(accountDTO.getMoneyCategory());

    if (calculateType == CalculateType.ADD) {
      this.setCheckAmount(NumberUtil.toReserve(NumberUtil.doubleVal(this.getCheckAmount()) + NumberUtil.doubleVal(accountDTO.getCheck()), NumberUtil.MONEY_PRECISION));
      this.setUnionPay(NumberUtil.toReserve(NumberUtil.doubleVal(this.getUnionPay()) + NumberUtil.doubleVal(accountDTO.getUnionpay()), NumberUtil.MONEY_PRECISION));
      this.setCash(NumberUtil.toReserve(NumberUtil.doubleVal(this.getCash()) + NumberUtil.doubleVal(accountDTO.getCash()), NumberUtil.MONEY_PRECISION));
      this.setTotal(NumberUtil.toReserve(NumberUtil.doubleVal(this.getTotal()) + NumberUtil.doubleVal(accountDTO.getTotal()), NumberUtil.MONEY_PRECISION));
    } else if (calculateType == CalculateType.MINUS) {
      this.setCheckAmount(NumberUtil.toReserve(NumberUtil.doubleVal(this.getCheckAmount()) - NumberUtil.doubleVal(accountDTO.getCheck()), NumberUtil.MONEY_PRECISION));
      this.setUnionPay(NumberUtil.toReserve(NumberUtil.doubleVal(this.getUnionPay()) - NumberUtil.doubleVal(accountDTO.getUnionpay()), NumberUtil.MONEY_PRECISION));
      this.setCash(NumberUtil.toReserve(NumberUtil.doubleVal(this.getCash()) - NumberUtil.doubleVal(accountDTO.getCash()), NumberUtil.MONEY_PRECISION));
      this.setTotal(NumberUtil.toReserve(NumberUtil.doubleVal(this.getTotal()) - NumberUtil.doubleVal(accountDTO.getTotal()), NumberUtil.MONEY_PRECISION));
    }
    return this;
  }

}
