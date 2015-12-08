package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-13
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public enum ShopRelationStatus {
  ENABLED("存在"),
  DISABLED("删除");

  String status;

  ShopRelationStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return this.status;
  }
}
