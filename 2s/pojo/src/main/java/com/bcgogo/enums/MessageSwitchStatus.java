package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-30
 * Time: 上午11:48
 * To change this template use File | Settings | File Templates.
 */
public enum MessageSwitchStatus {
  ON("开启"),
  OFF("关闭");

  String status;

  MessageSwitchStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return status;
  }
}
