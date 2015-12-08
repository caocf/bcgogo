package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-10-10
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public enum CustomerStatus {
  ENABLED("存在"),
  DISABLED("删除");

  String status;

  CustomerStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
