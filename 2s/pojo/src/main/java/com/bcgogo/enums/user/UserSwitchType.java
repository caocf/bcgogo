package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-10-22
 * Time: 下午7:30
 * To change this template use File | Settings | File Templates.
 */
public enum UserSwitchType {

  MOBILE_HIDDEN("手机号码部分隐藏"),
  REPAIR_PICKING("维修领料"),
  SCANNING_CARD("刷卡机功能"),
  SCANNING_BARCODE("扫描枪功能"),
  SETTLED_REMINDER("结算提醒");

  private String type;

  UserSwitchType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
