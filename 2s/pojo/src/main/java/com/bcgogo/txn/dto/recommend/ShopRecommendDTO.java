package com.bcgogo.txn.dto.recommend;

import com.bcgogo.config.dto.ShopDTO;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:12
 * To change this template use File | Settings | File Templates.
 */
public class ShopRecommendDTO {
  private Long id;
  private Long shopId;
  private Long recommendShopId;
  private Double customScore;
  private Long editDate;

  public ShopRecommendDTO() {

  }
  public ShopRecommendDTO(Long shopId, ShopDTO shopDTO,Long editDate) {
    this.shopId = shopId;
    this.recommendShopId = shopDTO.getId();
    this.customScore = shopDTO.getCustomScore();
    this.editDate = editDate;
  }

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

  public Long getRecommendShopId() {
    return recommendShopId;
  }

  public void setRecommendShopId(Long recommendShopId) {
    this.recommendShopId = recommendShopId;
  }

  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }

  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }
}
