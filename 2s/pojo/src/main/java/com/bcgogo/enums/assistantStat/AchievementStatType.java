package com.bcgogo.enums.assistantStat;

/**
 * 员工业绩统计类型：按部门或者按员工
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-5-21
 * Time: 下午10:09
 * To change this template use File | Settings | File Templates.
 */
public enum AchievementStatType {
  DEPARTMENT("按部门"),
  ASSISTANT("按员工");
  private String name;

  private AchievementStatType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }


}
