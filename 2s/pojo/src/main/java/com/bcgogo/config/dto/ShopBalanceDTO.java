package com.bcgogo.config.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-21
 * Time: 上午10:37
 * To change this template use File | Settings | File Templates.
 */
public class ShopBalanceDTO implements Serializable {
  public ShopBalanceDTO(){ }

  public Long id;
  private Long shopId;
  private Double smsBalance;
  private Double rechargeTotal;

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

  public Double getSmsBalance() {
    return smsBalance;
  }

  public void setSmsBalance(Double smsBalance) {
    this.smsBalance = smsBalance;
  }

  public Double getRechargeTotal() {
    return rechargeTotal;
  }

  public void setRechargeTotal(Double rechargeTotal) {
    this.rechargeTotal = rechargeTotal;
  }
}
