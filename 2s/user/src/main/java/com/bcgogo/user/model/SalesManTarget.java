package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-16
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sales_man_target")
public class SalesManTarget extends LongIdentifier {
  private Long SalesManId;
  private int month;
  private double monthTarget;
  private String year;
  private double yearTarget;
  @Column(name = "year_target")
  public double getYearTarget() {
    return yearTarget;
  }

  public void setYearTarget(double yearTarget) {
    this.yearTarget = yearTarget;
  }

  @Column(name = "month")
  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }
 @Column(name = "month_target")
  public double getMonthTarget() {
    return monthTarget;
  }

  public void setMonthTarget(double monthTarget) {
    this.monthTarget = monthTarget;
  }

  @Column(name = "sales_man_id")
  public Long getSalesManId() {
    return SalesManId;
  }
  public void setSalesManId(Long salesManId) {
    SalesManId = salesManId;
  }
   @Column(name = "year")
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }
}
