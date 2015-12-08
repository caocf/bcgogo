package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.RecommendShopStatus;

/**
 * Created by XinyuQiu on 14-8-19.
 */
public class RecommendShopDTO {
  private Long recommendId;
  private Long shopId;
  private RecommendShopStatus status;

  public Long getRecommendId() {
    return recommendId;
  }

  public void setRecommendId(Long recommendId) {
    this.recommendId = recommendId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public RecommendShopStatus getStatus() {
    return status;
  }

  public void setStatus(RecommendShopStatus status) {
    this.status = status;
  }
}
