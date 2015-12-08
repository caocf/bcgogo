package com.bcgogo.notification.client;

/**
 * User: ZhangJuntao
 * Date: 13-5-7
 * Time: 下午2:33
 * 不包括业务数据 发送参数
 */
public class SmsParam {
  private String phone;
  private String message;

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
