package com.bcgogo.enums.Product;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-12-18
 * Time: 下午3:56
 * To change this template use File | Settings | File Templates.
 */
public enum ProductCategoryStatus {
  ENABLED("有效"),
  DISABLED("无效");

  private String status;

  private ProductCategoryStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
