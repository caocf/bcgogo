package com.bcgogo.enums.app;

/**
 * User: ZhangJuntao
 * Date: 13-10-25
 * Time: 上午11:06
 */
public enum  AppUserBillStatus {
  DISABLED("已删除"),
  SAVED("已保存");

  private final String name;

  private AppUserBillStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
