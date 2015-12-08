package com.bcgogo.config.dto;

import com.bcgogo.enums.config.RecentlyUsedDataType;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-8-7
 * Time: 下午1:32
 * To change this template use File | Settings | File Templates.
 */
public class RecentlyUsedDataDTO{
  private Long id;
  private Long shopId;
  private Long dataId;
  private Double count;
  private RecentlyUsedDataType type;
  private Long time;

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

  public Double getCount() {
    return count;
  }

  public void setCount(Double count) {
    this.count = count;
  }

  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  public RecentlyUsedDataType getType() {
    return type;
  }

  public void setType(RecentlyUsedDataType type) {
    this.type = type;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }
}
