package com.bcgogo.pojox.enums.config;

/**
 * User: ZhangJuntao
 * Date: 13-11-25
 * Time: 下午3:27
 */
public enum JuheStatus {
  ACTIVE("启用"),
  IN_ACTIVE("禁用");

  private String value;

  JuheStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
