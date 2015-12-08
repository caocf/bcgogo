package com.bcgogo.enums;

/**
 * User: Jimuchen
 * Date: 12-6-21
 * Time: 下午12:38
 */
public enum DebtStatus {
  ARREARS("欠款"),
  SETTLED("结清"),
  REPEAL("作废");

  private final String name;
  private DebtStatus(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
