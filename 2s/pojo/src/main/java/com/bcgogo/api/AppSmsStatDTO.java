package com.bcgogo.api;

/**
 * Created by XinyuQiu on 14-6-16.
 */
public class AppSmsStatDTO {

  private String appUserNo;
  private int year;
  private int month;
  private int smsCount;
  private int smsLimit;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getSmsCount() {
    return smsCount;
  }

  public void setSmsCount(int smsCount) {
    this.smsCount = smsCount;
  }

  public int getSmsLimit() {
    return smsLimit;
  }

  public void setSmsLimit(int smsLimit) {
    this.smsLimit = smsLimit;
  }
}
