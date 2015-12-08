package com.bcgogo.txn.model.recommend;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "shop_recommend_trace")
public class ShopRecommendTrace extends LongIdentifier{
  private Long shopId;
  private Long recommendShopId;
  private Double customScore;
  private Long editDate;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="custom_score")
  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }

  @Column(name="recommend_shop_id")
  public Long getRecommendShopId() {
    return recommendShopId;
  }

  public void setRecommendShopId(Long recommendShopId) {
    this.recommendShopId = recommendShopId;
  }
  @Column(name="edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  public void fromShopRecommendDTO(ShopRecommendDTO shopRecommendDTO) {
    this.setId(shopRecommendDTO.getId());
    this.setShopId(shopRecommendDTO.getShopId());
    this.setRecommendShopId(shopRecommendDTO.getRecommendShopId());
    this.setCustomScore(shopRecommendDTO.getCustomScore());
    this.setEditDate(shopRecommendDTO.getEditDate());
  }
}
