package com.bcgogo.notification.client.yimei;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.notification.client.SmsParam;

/**
 * User: ZhangJuntao
 * Date: 13-5-7
 * Time: 下午3:17
 */
public class YimeiSmsParam extends SmsParam {
  private SmsChannel smsChannel;
  private String cdkey;
  private String password;

  public SmsChannel getSmsChannel() {
    return smsChannel;
  }

  public void setSmsChannel(SmsChannel smsChannel) {
    this.smsChannel = smsChannel;
  }

  public String getCdkey() {
    return cdkey;
  }

  public void setCdkey(String cdkey) {
    this.cdkey = cdkey;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
