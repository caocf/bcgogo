package com.bcgogo.pojox.enums;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午3:20
 */
public enum AppUserStatus {
   all("全部状态"),
  active("启用"),
  inActive("禁用"),
  deleted("已删除");

  private String value;

  AppUserStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
