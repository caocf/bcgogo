package com.bcgogo.enums.notification;

public enum SmsChannel {
  INDUSTRY("行业"),
  MARKETING("营销");
  private String value;

  private SmsChannel(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}