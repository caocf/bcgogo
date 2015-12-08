package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * 用于结算的时候是否验证密码
 * User: cfl
 * Date: 12-7-19
 * Time: 下午2:21
 * To change this template use File | Settings | File Templates.
 */
public enum PasswordValidateStatus {
  VALIDATE("验证"),//消费的时候密码需要验证
  UNVALIDATE("不验证");

  String status;

  PasswordValidateStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
