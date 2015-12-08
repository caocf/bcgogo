package com.bcgogo.stat.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-11
 * Time: 下午6:27
 * To change this template use File | Settings | File Templates.
 */
public class CustomerStatDTO implements Serializable {
  private Long id;
  private Long shopId;
  private String customerType;
  private Long amount;

  public CustomerStatDTO() {
  }

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

  public String getCustomerType() {
    return customerType;
  }

  public void setCustomerType(String customerType) {
    this.customerType = customerType;
  }

  public Long getAmount() {
    return amount;
  }

  public void setAmount(Long amount) {
    this.amount = amount;
  }
}
