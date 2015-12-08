package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ExpendDetailDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-5-5
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "expend_detail")
public class ExpendDetail extends LongIdentifier {

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

  @Column(name = "other_fee_year")
  public double getOtherFeeYear() {
    return otherFeeYear;
  }

  public void setOtherFeeYear(double otherFeeYear) {
    this.otherFeeYear = otherFeeYear;
  }

  @Column(name = "other_fee_month")
  public double getOtherFeeMonth() {
    return otherFeeMonth;
  }

  public void setOtherFeeMonth(double otherFeeMonth) {
    this.otherFeeMonth = otherFeeMonth;
  }

  @Column(name = "other_fee_day")
  public double getOtherFeeDay() {
    return otherFeeDay;
  }

  public void setOtherFeeDay(double otherFeeDay) {
    this.otherFeeDay = otherFeeDay;
  }

  @Column(name = "other_expend_year")
  public double getOtherExpendYear() {
    return otherExpendYear;
  }

  public void setOtherExpendYear(double otherExpendYear) {
    this.otherExpendYear = otherExpendYear;
  }

  @Column(name = "other_expend_month")
  public double getOtherExpendMonth() {
    return otherExpendMonth;
  }

  public void setOtherExpendMonth(double otherExpendMonth) {
    this.otherExpendMonth = otherExpendMonth;
  }

  @Column(name = "other_expend_day")
  public double getOtherExpendDay() {
    return otherExpendDay;
  }

  public void setOtherExpendDay(double otherExpendDay) {
    this.otherExpendDay = otherExpendDay;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "year")
  public Long getYear() {
    return year;
  }

  public void setYear(Long year) {
    this.year = year;
  }

  @Column(name = "month")
  public Long getMonth() {
    return month;
  }

  public void setMonth(Long month) {
    this.month = month;
  }

  @Column(name = "day")
  public Long getDay() {
    return day;
  }

  public void setDay(Long day) {
    this.day = day;
  }

  @Column(name = "rent_year")
  public double getRentYear() {
    return rentYear;
  }

  public void setRentYear(double rentYear) {
    this.rentYear = rentYear;
  }

  @Column(name = "rent_month")
  public double getRentMonth() {
    return rentMonth;
  }

  public void setRentMonth(double rentMonth) {
    this.rentMonth = rentMonth;
  }

  @Column(name = "rent_day")
  public double getRentDay() {
    return rentDay;
  }

  public void setRentDay(double rentDay) {
    this.rentDay = rentDay;
  }

  @Column(name = "labor_year")
  public double getLaborYear() {
    return laborYear;
  }

  public void setLaborYear(double laborYear) {
    this.laborYear = laborYear;
  }

  @Column(name = "labor_month")
  public double getLaborMonth() {
    return laborMonth;
  }

  public void setLaborMonth(double laborMonth) {
    this.laborMonth = laborMonth;
  }

  @Column(name = "labor_day")
  public double getLaborDay() {
    return laborDay;
  }

  public void setLaborDay(double laborDay) {
    this.laborDay = laborDay;
  }

  @Column(name = "other_year")
  public double getOtherYear() {
    return otherYear;
  }

  public void setOtherYear(double otherYear) {
    this.otherYear = otherYear;
  }

  @Column(name = "other_month")
  public double getOtherMonth() {
    return otherMonth;
  }

  public void setOtherMonth(double otherMonth) {
    this.otherMonth = otherMonth;
  }

  @Column(name = "other_day")
  public double getOtherDay() {
    return otherDay;
  }

  public void setOtherDay(double otherDay) {
    this.otherDay = otherDay;
  }

  @Column(name = "total_year")
  public double getTotalYear() {
    return totalYear;
  }

  public void setTotalYear(double totalYear) {
    this.totalYear = totalYear;
  }

  @Column(name = "total_month")
  public double getTotalMonth() {
    return totalMonth;
  }

  public void setTotalMonth(double totalMonth) {
    this.totalMonth = totalMonth;
  }

  @Column(name = "total_day")
  public double getTotalDay() {
    return totalDay;
  }

  public void setTotalDay(double totalDay) {
    this.totalDay = totalDay;
  }

  public ExpendDetail(){}

  public ExpendDetail fromDTO(ExpendDetailDTO expendDetailDTO,boolean setId) {
    if(setId){
    this.setId(expendDetailDTO.getId());
    }
    this.setShopId(expendDetailDTO.getShopId());

    this.setYear(expendDetailDTO.getYear());
    this.setMonth(expendDetailDTO.getMonth());
    this.setDay(expendDetailDTO.getDay());

    this.setRentYear(expendDetailDTO.getRentYear());
    this.setRentMonth(expendDetailDTO.getRentMonth());
    this.setRentDay(expendDetailDTO.getRentDay());

    this.setLaborYear(expendDetailDTO.getLaborYear());
    this.setLaborMonth(expendDetailDTO.getLaborMonth());
    this.setLaborDay(expendDetailDTO.getLaborDay());

    this.setOtherYear(expendDetailDTO.getOtherYear());
    this.setOtherMonth(expendDetailDTO.getOtherMonth());
    this.setOtherDay(expendDetailDTO.getOtherDay());

    this.setTotalYear(expendDetailDTO.getTotalYear());
    this.setTotalMonth(expendDetailDTO.getTotalMonth());
    this.setTotalDay(expendDetailDTO.getTotalDay());

    this.setOtherFeeDay(expendDetailDTO.getOtherFeeDay());
    this.setOtherFeeMonth(expendDetailDTO.getOtherFeeMonth());
    this.setOtherFeeYear(expendDetailDTO.getOtherFeeYear());

    this.setOtherExpendDay(expendDetailDTO.getOtherExpendDay());
    this.setOtherExpendMonth(expendDetailDTO.getOtherExpendMonth());
    this.setOtherExpendYear(expendDetailDTO.getOtherExpendYear());

    return this;
  }



  public ExpendDetailDTO toDTO() {
    ExpendDetailDTO expendDetailDTO = new ExpendDetailDTO();

    expendDetailDTO.setId(this.getId());
    expendDetailDTO.setShopId(this.getShopId());

    expendDetailDTO.setYear(this.getYear());
    expendDetailDTO.setMonth(this.getMonth());
    expendDetailDTO.setDay(this.getDay());

    expendDetailDTO.setRentYear(this.getRentYear());
    expendDetailDTO.setRentMonth(this.getRentMonth());
    expendDetailDTO.setRentDay(this.getRentDay());

    expendDetailDTO.setLaborYear(this.getLaborYear());
    expendDetailDTO.setLaborMonth(this.getLaborMonth());
    expendDetailDTO.setLaborDay(this.getLaborDay());

    expendDetailDTO.setOtherYear(this.getOtherYear());
    expendDetailDTO.setOtherMonth(this.getOtherMonth());
    expendDetailDTO.setOtherDay(this.getOtherDay());

    expendDetailDTO.setTotalYear(this.getTotalYear());
    expendDetailDTO.setTotalMonth(this.getTotalMonth());
    expendDetailDTO.setTotalDay(this.getTotalDay());

    expendDetailDTO.setOtherFeeDay(this.getOtherFeeDay());
    expendDetailDTO.setOtherFeeMonth(this.getOtherFeeMonth());
    expendDetailDTO.setOtherFeeYear(this.getOtherFeeYear());

    expendDetailDTO.setOtherExpendDay(this.getOtherExpendDay());
    expendDetailDTO.setOtherExpendMonth(this.getOtherExpendMonth());
    expendDetailDTO.setOtherExpendYear(this.getOtherExpendYear());

    return expendDetailDTO;
  }

}
