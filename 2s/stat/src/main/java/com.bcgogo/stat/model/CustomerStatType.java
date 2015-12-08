package com.bcgogo.stat.model;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

public enum CustomerStatType {

  TOTAL("TOTAL"),
  PHONE("PHONE"),
  INSURANCE("INSURANCE"),
  INSPECTION("INSPECTION"),
  BIRTHDAY("BIRTHDAY");

  String name;

  CustomerStatType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
