package com.bcgogo.enums;

public enum ProductRecommendType {
  FromNormalPreBuyOrder("根据普通求购"),
  FromOther("根据其他");

  private final String name;

  private ProductRecommendType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
