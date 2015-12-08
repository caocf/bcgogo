package com.bcgogo.enums.assistantStat;

/**
 * 员工业绩提成方式：按销售量 按销售额
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:09
 * To change this template use File | Settings | File Templates.
 */
public enum AchievementMemberType {
  CARD_AMOUNT("按销售量"),
  CARD_TOTAL("按销售额");
  private String name;

  private AchievementMemberType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }


}
