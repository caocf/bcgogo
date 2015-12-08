package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.ShopStatus;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-7-18
 * Time: 上午10:00
 */
public class MaintainShopLogDTO {
  private Long id;
  private Long userId;
  private Long shopId;
  private ShopStatus shopStatus;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public ShopStatus getShopStatus() {
    return shopStatus;
  }

  public void setShopStatus(ShopStatus shopStatus) {
    this.shopStatus = shopStatus;
  }
}
