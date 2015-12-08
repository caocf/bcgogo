package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-8-27
 * Time: 上午10:46
 * To change this template use File | Settings | File Templates.
 */
public enum PayStatus {

  REPEAL("作废"),
  USE("可用");

  String type;

  PayStatus(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }

}
