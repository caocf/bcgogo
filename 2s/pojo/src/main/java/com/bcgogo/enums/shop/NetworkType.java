package com.bcgogo.enums.shop;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-28
 * Time: 下午10:53
 * 店铺状态
 */
public enum NetworkType {
  TELECOM("电信"),
  MOBILE("移动"),
  UNICOM("联通"),
  TIE_TONG("铁通"),
  CABLE("有线通");

  private String value;

  NetworkType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
