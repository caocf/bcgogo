package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: li jinlong
 * Date: 12-10-11
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public enum RepairOrderTemplateStatus {

  ENABLED("有效"),
  DISABLED("无效");

  String status;

  RepairOrderTemplateStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
