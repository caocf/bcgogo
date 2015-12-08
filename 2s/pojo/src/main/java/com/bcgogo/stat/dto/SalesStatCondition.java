package com.bcgogo.stat.dto;

import com.bcgogo.utils.StatConstant;
import com.bcgogo.utils.StringUtil;

/**
 * 畅销品统计查询条件
 * User: lw
 * Date: 12-10-27
 * Time: 上午2:50
 * To change this template use File | Settings | File Templates.
 */

public class SalesStatCondition {

  private static final String ALL_MONTH = "所有月份";

  private Integer year;  //查询年份
  private String yearStr;  //查询年份

  private String monthStr; //查询月份
  private Integer month;    //查询月份
  private Boolean allYear;  //所有月份
  private Boolean queryProduct;//滞销品统计是否查询的是商品

  private String moneyOrAmount; //按金额或者数量进行统计
  private String queryPeriodStr;//查询周期: 1月、3月、半年、1年周期
  private Long lastSaleTime = 2592000000L;  //上次消费时间 1月、3月、半年、1年周期  默认为一个月
  private String productOrSupplier;//供应商退货统计 按商品or按供应商进行统计
  private String returnQueryStr = StatConstant.PRODUCT; //按商品或者供应商统计

  public String getReturnQueryStr() {
    return returnQueryStr;
  }

  public void setReturnQueryStr(String returnQueryStr) {
    this.returnQueryStr = returnQueryStr;
  }

  public String getProductOrSupplier() {
    return productOrSupplier;
  }

  public void setProductOrSupplier(String productOrSupplier) {
    if (StringUtil.isEmpty(productOrSupplier)) {
      this.returnQueryStr = StatConstant.PRODUCT;
      this.queryProduct = true;
    } else if (productOrSupplier.equals(StatConstant.QUERY_BY_SUPPLIER)){
      this.returnQueryStr = StatConstant.SUPPLIER;
      queryProduct = false;
    }else{
      this.returnQueryStr = StatConstant.PRODUCT;
      queryProduct = true;
    }
    this.productOrSupplier = productOrSupplier;
  }

  public Long getLastSaleTime() {
    return lastSaleTime;
  }

  public void setLastSaleTime(Long lastSaleTime) {
    this.lastSaleTime = lastSaleTime;
  }

  public String getQueryPeriodStr() {
    return queryPeriodStr;
  }

  public void setQueryPeriodStr(String queryPeriodStr) {
    if (StringUtil.isEmpty(queryPeriodStr)) {
      queryPeriodStr = StatConstant.ONE_MONTH;
    }
    if (queryPeriodStr.equals(StatConstant.THREE_MONTH)) {
      lastSaleTime = lastSaleTime * 3;
    } else if (queryPeriodStr.equals(StatConstant.SIX_MONTH)) {
      lastSaleTime = lastSaleTime * 6;
    }
    if (queryPeriodStr.equals(StatConstant.ONE_YEAR)) {
      lastSaleTime = lastSaleTime * 12;
    }

    this.queryPeriodStr = queryPeriodStr;
  }



  public String getMoneyOrAmount() {
    return moneyOrAmount;
  }

  public void setMoneyOrAmount(String moneyOrAmount) {
    this.moneyOrAmount = moneyOrAmount;
  }

  public String getYearStr() {
    return yearStr;
  }

  public void setYearStr(String yearStr) {
    this.yearStr = yearStr;
  }



  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public String getMonthStr() {
    if (allYear) {
      monthStr = ALL_MONTH;
    } else {
      monthStr = month.toString() +"月";
    }
    return monthStr;
  }

  public void setMonthStr(String monthStr) {
    if (monthStr.equals(ALL_MONTH)) {
      allYear = true;
    } else {
      allYear = false;
      month = Integer.parseInt(monthStr.replace("月", ""));
    }
    this.monthStr = monthStr;
  }

  public Integer getMonth() {
    return month;
  }

  public void setMonth(Integer month) {
    this.month = month;
    if (allYear) {
      this.monthStr = ALL_MONTH;
    } else {
      this.monthStr = month.toString() + "月";
    }
  }

  public Boolean getAllYear() {
    return allYear;
  }

  public void setAllYear(Boolean allYear) {
    this.allYear = allYear;
  }

  public Boolean getQueryProduct() {
    return queryProduct;
  }

  public void setQueryProduct(Boolean queryProduct) {
    this.queryProduct = queryProduct;
  }

}