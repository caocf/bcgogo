package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-14
 * Time: 下午3:44
 * To change this template use File | Settings | File Templates.
 */
public enum CustomerShopStatus {
  NONE_REGISTERED("未注册"),
  APPROVALING("待审核"),
  REGISTERED("已注册");

  String status;

  CustomerShopStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return this.status;
  }
}
