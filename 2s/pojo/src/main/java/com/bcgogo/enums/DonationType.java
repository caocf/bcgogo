package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-2-28
 * Time: 下午5:13
 */
public enum DonationType {
  SMS_BACKGROUND_DONATION("后台赠送短信"),
  REGISTER_ACTIVATE("注册激活"),
  INVITE_PAID("邀请注册并付款");

  private String name;

  private DonationType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
