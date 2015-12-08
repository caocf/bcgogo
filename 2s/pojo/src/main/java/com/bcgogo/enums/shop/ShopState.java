package com.bcgogo.enums.shop;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-28
 * Time: 下午10:53
 * 店铺操作状态
 */
public enum ShopState {
  ACTIVE("启用"),
  IN_ACTIVE("禁用"),
  ARREARS("欠费"),
  OVERDUE("过期"),
  DELETED("已删除");

  private String value;

  ShopState(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
