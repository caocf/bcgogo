package com.bcgogo.enums.notification;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-12-23
 * Time: 上午10:05
 * To change this template use File | Settings | File Templates.
 */
public enum SmsType {
  SMS_SENT("已发送短信"),
  SMS_SEND("未发送短信"),
  SMS_DRAFT("草稿短信");
  private String name;

  private SmsType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
