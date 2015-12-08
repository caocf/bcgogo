package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-5-5
 * Time: 下午2:00
 * To change this template use File | Settings | File Templates.
 */
public class ExpendDetailDTO implements Serializable {


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

  public Long getYear() {
    return year;
  }

  public void setYear(Long year) {
    this.year = year;
  }

  public Long getMonth() {
    return month;
  }

  public void setMonth(Long month) {
    this.month = month;
  }

  public Long getDay() {
    return day;
  }

  public void setDay(Long day) {
    this.day = day;
  }

  public double getRentYear() {
    return rentYear;
  }

  public void setRentYear(double rentYear) {
    this.rentYear = rentYear;
  }

  public double getRentMonth() {
    return rentMonth;
  }

  public void setRentMonth(double rentMonth) {
    this.rentMonth = rentMonth;
  }

  public double getRentDay() {
    return rentDay;
  }

  public void setRentDay(double rentDay) {
    this.rentDay = rentDay;
  }

  public double getLaborYear() {
    return laborYear;
  }

  public void setLaborYear(double laborYear) {
    this.laborYear = laborYear;
  }

  public double getLaborMonth() {
    return laborMonth;
  }

  public void setLaborMonth(double laborMonth) {
    this.laborMonth = laborMonth;
  }

  public double getLaborDay() {
    return laborDay;
  }

  public void setLaborDay(double laborDay) {
    this.laborDay = laborDay;
  }

  public double getOtherYear() {
    return otherYear;
  }

  public void setOtherYear(double otherYear) {
    this.otherYear = otherYear;
  }

  public double getOtherMonth() {
    return otherMonth;
  }

  public void setOtherMonth(double otherMonth) {
    this.otherMonth = otherMonth;
  }

  public double getOtherDay() {
    return otherDay;
  }

  public void setOtherDay(double otherDay) {
    this.otherDay = otherDay;
  }

  public double getTotalYear() {
    return totalYear;
  }

  public void setTotalYear(double totalYear) {
    this.totalYear = totalYear;
  }

  public double getTotalMonth() {
    return totalMonth;
  }

  public void setTotalMonth(double totalMonth) {
    this.totalMonth = totalMonth;
  }

  public double getTotalDay() {
    return totalDay;
  }

  public void setTotalDay(double totalDay) {
    this.totalDay = totalDay;
  }

  private Long id;
  private Long shopId;

  private Long year;
  private Long month;
  private Long day;

  private double rentYear;
  private double rentMonth;
  private double rentDay;

  private double laborYear;
  private double laborMonth;
  private double laborDay;

  private double otherYear;
  private double otherMonth;
  private double otherDay;

  private double otherFeeYear;
  private double otherFeeMonth;
  private double otherFeeDay;

  private double otherExpendYear;
  private double otherExpendMonth;
  private double otherExpendDay;

  private double totalYear;
  private double totalMonth;
  private double totalDay;

  public double getOtherFeeYear() {
    return otherFeeYear;
  }

  public void setOtherFeeYear(double otherFeeYear) {
    this.otherFeeYear = otherFeeYear;
  }

  public double getOtherFeeMonth() {
    return otherFeeMonth;
  }

  public void setOtherFeeMonth(double otherFeeMonth) {
    this.otherFeeMonth = otherFeeMonth;
  }

  public double getOtherFeeDay() {
    return otherFeeDay;
  }

  public void setOtherFeeDay(double otherFeeDay) {
    this.otherFeeDay = otherFeeDay;
  }

  public double getOtherExpendYear() {
    return otherExpendYear;
  }

  public void setOtherExpendYear(double otherExpendYear) {
    this.otherExpendYear = otherExpendYear;
  }

  public double getOtherExpendMonth() {
    return otherExpendMonth;
  }

  public void setOtherExpendMonth(double otherExpendMonth) {
    this.otherExpendMonth = otherExpendMonth;
  }

  public double getOtherExpendDay() {
    return otherExpendDay;
  }

  public void setOtherExpendDay(double otherExpendDay) {
    this.otherExpendDay = otherExpendDay;
  }

  @Override
  public String toString() {
    return "ExpendDetailDTO{" +
        "id=" + (id==null?"":id) +
        ", shopId=" + (shopId==null?"":shopId) +
        ", year=" + (year==null?"":year) +
        ", month=" + (month==null?"":month) +
        ", day=" + (day==null?"":day) +
        ", rentYear=" + rentYear +
        ", rentMonth=" + rentMonth +
        ", rentDay=" + rentDay +
        ", laborYear=" + laborYear +
        ", laborMonth=" + laborMonth +
        ", laborDay=" + laborDay +
        ", otherYear=" + otherYear +
        ", otherMonth=" + otherMonth +
        ", otherDay=" + otherDay +
        ", otherFeeYear=" + otherFeeYear +
        ", otherFeeMonth=" + otherFeeMonth +
        ", otherFeeDay=" + otherFeeDay +
        ", otherExpendYear=" + otherExpendYear +
        ", otherExpendMonth=" + otherExpendMonth +
        ", otherExpendDay=" + otherExpendDay +
        ", totalYear=" + totalYear +
        ", totalMonth=" + totalMonth +
        ", totalDay=" + totalDay +
        '}';
  }
}
