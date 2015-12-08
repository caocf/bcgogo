package com.bcgogo.enums.payment;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-23
 * Time: 下午5:59  \
 * 支付场景
 */
public enum ChinaPayScene {
  SMS_RECHARGE("短信充值"),
  SOFTWARE_PAY("软件付款"),
  HARDWARE_PAY("硬件付款"),
  COMBINED_PAY("合并付款"),
  LONA_TRANSFERS(" 货款转账");

  private String value;

  ChinaPayScene(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
