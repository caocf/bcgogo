package com.bcgogo.enums.assistantStat;

/**
 * 员工业绩提成方式：按金额 或者按比例
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:09
 * To change this template use File | Settings | File Templates.
 */
public enum AchievementType {
  AMOUNT("按金额"),
  RATIO("按比率");
  private String name;

  private AchievementType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }


}
