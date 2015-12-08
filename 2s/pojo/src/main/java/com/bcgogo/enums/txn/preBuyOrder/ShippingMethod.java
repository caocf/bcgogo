package com.bcgogo.enums.txn.preBuyOrder;

public enum ShippingMethod {
  DELIVERY_HOME("送货上门");

  private final String name;

  private ShippingMethod(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
