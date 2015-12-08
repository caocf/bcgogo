package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-18
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inventory_alarm")
public class InventoryAlarm extends LongIdentifier {
  public InventoryAlarm() {
  }

  private Long shopId;
  private Long productId;
  private double amountAlarm;
  private String memo;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "amount_alarm")
  public double getAmountAlarm() {
    return amountAlarm;
  }

  public void setAmountAlarm(double amountAlarm) {
    this.amountAlarm = amountAlarm;
  }

  @Column(name = "memo",length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
