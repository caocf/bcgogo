package com.bcgogo.stat.model;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

public enum BizStatType {

  INCOME("INCOME"),
  CARREPAIR("CARREPAIR"),
  SALES("SALES"),
  CARWASHING("CARWASHING"),
  PURCHASING("PURCHASING"),
  GROSSPROFIT("GROSSPROFIT");

  String name;

  BizStatType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
