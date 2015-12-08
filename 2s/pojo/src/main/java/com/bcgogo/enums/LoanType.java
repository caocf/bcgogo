package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-22
 * Time: 下午3:47
 * 货款 分类
 */
public enum LoanType {
  FIRST_PAYMENT("首次付款"),
  UPGRADE_PAYMENT("升级付款"),
  OTHER_PAYMENT("其他");

  private String value;

  LoanType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
