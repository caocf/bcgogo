package com.bcgogo.stat.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: xiaojian
 * Date: 12-1-5
 * Time: 上午11:16
 * To change this template use File | Settings | File Templates.
 */
public class BizStatDTO implements Serializable {
  private Long id;
  private Long shopId;
  private String statType;
  private Long statYear;
  private Long statMonth;
  private Long statDay;
  private Long statWeek;
  private double statSum;

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

  public String getStatType() {
    return statType;
  }

  public void setStatType(String statType) {
    this.statType = statType;
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

  public Long getStatWeek() {
    return statWeek;
  }

  public void setStatWeek(Long statWeek) {
    this.statWeek = statWeek;
  }

  public double getStatSum() {
    return statSum;
  }

  public void setStatSum(double statSum) {
    this.statSum = statSum;
  }
}
