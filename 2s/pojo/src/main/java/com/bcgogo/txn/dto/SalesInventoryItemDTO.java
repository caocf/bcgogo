package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-10
 * Time: 下午7:13
 * To change this template use File | Settings | File Templates.
 */
public class SalesInventoryItemDTO implements Serializable {
  public SalesInventoryItemDTO() {
  }

  private Long id;
  private Long salesProductId;
  private Long productId;
  private double amount;
  private double price;
  private double total;
  private String memo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSalesProductId() {
    return salesProductId;
  }

  public void setSalesProductId(Long salesProductId) {
    this.salesProductId = salesProductId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
