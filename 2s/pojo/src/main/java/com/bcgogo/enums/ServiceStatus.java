package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-28
 * Time: 上午11:20
 * To change this template use File | Settings | File Templates.
 */
public enum ServiceStatus {
  ENABLED("有效"),
  DISABLED("无效");

  String status;

  ServiceStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return status;
  }
}
