package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-20
 * Time: 下午7:45
 * To change this template use File | Settings | File Templates.
 */
public enum BusinessAccountEnum {

  STATUS_DELETE("delete"),
  STATUS_SAVE("save"),
  CATEGORY_ACCOUNT("account"),
  CATEGORY_BUSINESS("business"),
  STATUS_UPDATE("update");  //更新营业外记账

  private String name;

  BusinessAccountEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
