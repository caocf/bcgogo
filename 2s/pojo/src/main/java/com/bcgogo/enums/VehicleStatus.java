package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-10-15
 * Time: 上午10:02
 * To change this template use File | Settings | File Templates.
 */
public enum VehicleStatus {
  ENABLED("存在"),
  DISABLED("删除");

  String status;

  VehicleStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
