package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-9-9
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "sales_inventory_item")
public class SalesInventoryItem extends LongIdentifier {
  public SalesInventoryItem(){
  }

  @Column(name = "sales_inventory_id")
  public Long getSalesInventoryId() {
    return salesInventoryId;
  }

  public void setSalesInventoryId(Long salesInventoryId) {
    this.salesInventoryId = salesInventoryId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "sales_product_id")
  public Long getSalesProductId() {
    return salesProductId;
  }

  public void setSalesProductId(Long salesProductId) {
    this.salesProductId = salesProductId;
  }

  @Column(name = "memo", length = 500)

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  private Long salesInventoryId;
  private Long salesProductId;
  private Long productId;
  private double amount;
  private double price;
  private double total;
  private String memo;

}
