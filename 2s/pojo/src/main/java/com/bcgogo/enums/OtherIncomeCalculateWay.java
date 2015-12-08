package com.bcgogo.enums;

/**
 * 施工其他费用 材料管理费 计算方式
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-10-21
 * Time: 下午5:55
 * To change this template use File | Settings | File Templates.
 */
public enum OtherIncomeCalculateWay {
  AMOUNT("按金额"),
  RATIO("按比率");
  private String name;

  private OtherIncomeCalculateWay(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
