package com.bcgogo.config.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-25
 * Time: 下午5:14
 * To change this template use File | Settings | File Templates.
 */
public class ShopBusinessDTO implements Serializable {
  public ShopBusinessDTO(){
  }

  private Long id;
  private Long shopId;
  private Long businessId;
  private Long state;
  private String memo;

  public Long getId() {
    return this.id;
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

  public Long getBusinessId() {
    return businessId;
  }

  public void setBusinessId(Long businessId) {
    this.businessId = businessId;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

}
