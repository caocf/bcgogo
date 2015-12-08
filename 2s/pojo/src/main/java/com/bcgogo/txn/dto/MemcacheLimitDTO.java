package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-6-8
 * Time: 上午8:56
 * To change this template use File | Settings | File Templates.
 */
public class MemcacheLimitDTO implements Serializable{
  private Long shopId;
  private Integer currentLowerLimitAmount;
  private Integer currentUpperLimitAmount;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Integer getCurrentLowerLimitAmount() {
    return currentLowerLimitAmount;
  }

  public void setCurrentLowerLimitAmount(Integer currentLowerLimitAmount) {
    this.currentLowerLimitAmount = currentLowerLimitAmount;
  }

  public Integer getCurrentUpperLimitAmount() {
    return currentUpperLimitAmount;
  }

  public void setCurrentUpperLimitAmount(Integer currentUpperLimitAmount) {
    this.currentUpperLimitAmount = currentUpperLimitAmount;
  }
}
