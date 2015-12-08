package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-18
 * Time: 下午3:34
 * To change this template use File | Settings | File Templates.
 */
public class InventoryAlarmDTO implements Serializable {
  public InventoryAlarmDTO() {
  }

  private Long id;
  private Long shopId;
  private Long productId;
  private double amountAlarm;
  private String memo;

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

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public double getAmountAlarm() {
    return amountAlarm;
  }

  public void setAmountAlarm(double amountAlarm) {
    this.amountAlarm = amountAlarm;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
