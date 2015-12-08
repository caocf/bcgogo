package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午9:17
 * CRM 权限模块module/ TreeMenu 相对应
 */
public enum ModuleType {
  CRM_SYS_MANAGE("系统管理"),
  CRM_SUPPLIER_MANAGE("供应商管理");

  private String value;

  ModuleType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
