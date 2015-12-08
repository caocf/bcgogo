package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-29
 * Time: 下午9:27
 * CRM 操作类型
 */
public enum OperateType {
  LOGIN("登录"),
  ADD_USER_GROUP("增加用户组"),
  UPDATE_USER_GROUP("修改用户组"),
  USER_REGISTER("用户注册");

  private String value;

  OperateType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
