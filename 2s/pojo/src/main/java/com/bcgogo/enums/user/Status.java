package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-14
 * Time: 下午2:21
 * user库中 统一的状态
 */
public enum Status {
  all("全部状态"),
  active("启用"),
  inActive("禁用"),
  deleted("已删除");

  private String value;

  Status(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
