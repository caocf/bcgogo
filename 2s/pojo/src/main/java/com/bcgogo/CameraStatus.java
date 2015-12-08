package com.bcgogo;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-10-10
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public enum CameraStatus {
  ENABLED("已绑定"),
  DISABLED("未绑定");

  String status;

  CameraStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
