package com.bcgogo.enums.txn.preBuyOrder;

public enum QuotedResult {
  Orders("已下单"),
  NotOrders("未下单");

  private final String name;

  private QuotedResult(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
