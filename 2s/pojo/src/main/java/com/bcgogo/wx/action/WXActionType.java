package com.bcgogo.wx.action;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-3
 * Time: 上午11:08
 * To change this template use File | Settings | File Templates.
 */
public enum WXActionType {

  COMMAND("command"),
  EVENT("event"),
  ADMIN_OPERATE("admin_operate"),
  ;

  private String type;
  WXActionType(String type){
      this.type=type;
  }

  public String getType() {
    return type;
  }
}
