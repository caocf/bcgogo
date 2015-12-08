package com.bcgogo.config.dto;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-13
 * Time: 下午5:39
 * To change this template use File | Settings | File Templates.
 */
public class ShopCustomerRelationDTO {
  private Long id;
  private Long shopId;
  private Long customerId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }
}
