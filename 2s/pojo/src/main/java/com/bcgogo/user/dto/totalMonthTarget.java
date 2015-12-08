package com.bcgogo.user.dto;

/**
 * Created by IntelliJ IDEA.
 * User: zyj
 * Date: 12-2-20
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public class totalMonthTarget {
  private double totalMonthTarget;
  private int month;

  private String year;

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public double getTotalMonthTarget() {
    return totalMonthTarget;
  }

  public void setTotalMonthTarget(double totalMonthTarget) {
    this.totalMonthTarget = totalMonthTarget;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }
}
