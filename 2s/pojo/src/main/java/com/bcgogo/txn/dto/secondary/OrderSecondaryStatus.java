package com.bcgogo.txn.dto.secondary;

public enum OrderSecondaryStatus {
  REPAIR_SETTLED("已结算"),
  REPAIR_DEBT("欠款结算"),
  REPAIR_REPEAL("已作废");

  OrderSecondaryStatus(String name) {
    this.name = name;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
