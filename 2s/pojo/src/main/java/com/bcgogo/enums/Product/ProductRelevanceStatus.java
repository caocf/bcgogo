package com.bcgogo.enums.Product;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-1-5
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public enum ProductRelevanceStatus {
  ALL("所有状态"),
  UN_CHECKED("待复核"),
  YES("已标准"),
  NO("未标准");
  private String status;

  private ProductRelevanceStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
