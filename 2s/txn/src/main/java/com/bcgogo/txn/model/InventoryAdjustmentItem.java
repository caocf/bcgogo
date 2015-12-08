package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "inventory_adjustment_item")
public class InventoryAdjustmentItem extends LongIdentifier {
  public InventoryAdjustmentItem(){
  }

  @Column(name = "inventory_adjustment_id")
  public Long getInventoryAdjustmentId() {
    return inventoryAdjustmentId;
  }

  public void setInventoryAdjustmentId(Long inventoryAdjustmentId) {
    this.inventoryAdjustmentId = inventoryAdjustmentId;
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

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  private Long inventoryAdjustmentId;
  private Long productId;
  private double amount;
  private double price;
  private double total;
  private String memo;

}
