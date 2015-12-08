package com.bcgogo.enums.user.userGuide;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 下午4:32
 */
public enum SosStatus {
  UNTREATED("未处理"),
  TREATED("已处理"),
  DELETED("已删除");


  private String value;

  SosStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }


}
