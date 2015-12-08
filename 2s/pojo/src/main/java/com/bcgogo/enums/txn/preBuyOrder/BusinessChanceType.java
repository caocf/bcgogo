package com.bcgogo.enums.txn.preBuyOrder;

public enum BusinessChanceType {
  Normal("求购"),
  SellWell("畅销"),
  Lack("缺料");

  private final String name;

  private BusinessChanceType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
