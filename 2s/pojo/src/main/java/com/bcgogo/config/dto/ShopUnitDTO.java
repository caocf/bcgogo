package com.bcgogo.config.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinyu
 * Date: 12-5-10
 * Time: 下午2:33
 * To change this template use File | Settings | File Templates.
 */
public class ShopUnitDTO implements Serializable {

//  private Long id;
//  private Long shopId;
//  private Long lastEditTime;
//  private Long useRate;
  private String unitName;

//  public Long getId() {
//    return id;
//  }
//
//  public void setId(Long id) {
//    this.id = id;
//  }
//
//  public Long getShopId() {
//    return shopId;
//  }
//
//  public void setShopId(Long shopId) {
//    this.shopId = shopId;
//  }
//
//  public Long getLastEditTime() {
//    return lastEditTime;
//  }
//
//  public void setLastEditTime(Long lastEditTime) {
//    this.lastEditTime = lastEditTime;
//  }
//
//  public Long getUseRate() {
//    return useRate;
//  }
//
//  public void setUseRate(Long useRate) {
//    this.useRate = useRate;
//  }

  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }
}
