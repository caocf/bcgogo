package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-9-16
 * Time: 下午1:17
 */
public enum DataKind {
  TEST("测试店"),
  OFFICIAL("正式店");
  private String value;

  DataKind(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
