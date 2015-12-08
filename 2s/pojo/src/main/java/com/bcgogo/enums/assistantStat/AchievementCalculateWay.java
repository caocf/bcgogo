package com.bcgogo.enums.assistantStat;

/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-11-18
 * Time: 上午11:01
 * To change this template use File | Settings | File Templates.
 */
//员工业绩两种类型的计算方式
public enum AchievementCalculateWay {
  CALCULATE_BY_ASSISTANT("按员工提成设置"),
  CALCULATE_BY_DETAIL("按详细提成设置");

  private final String name;

  private AchievementCalculateWay(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }


}
