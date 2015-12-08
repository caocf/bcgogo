package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public enum ShopConfigStatus {
  OFF("关闭"),
  ON("开启");

  private String status;

   ShopConfigStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
