package com.bcgogo.txn.model.recommend;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;

import javax.persistence.*;

@Entity
@Table(name = "shop_recommend")
public class ShopRecommend extends LongIdentifier{
  private Long shopId;
  private Long recommendShopId;
  private Double customScore;
  private Long editDate;
  private DeletedType deleted = DeletedType.FALSE;

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
  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
  public void fromDTO(ShopRecommendDTO shopRecommendDTO){
    this.setShopId(shopRecommendDTO.getShopId());
    this.setRecommendShopId(shopRecommendDTO.getRecommendShopId());
    this.setCustomScore(shopRecommendDTO.getCustomScore());
    this.setEditDate(shopRecommendDTO.getEditDate());
  }

  public ShopRecommendDTO toDTO(){
    ShopRecommendDTO shopRecommendDTO = new ShopRecommendDTO();
    shopRecommendDTO.setShopId(this.getShopId());
    shopRecommendDTO.setId(this.getId());
    shopRecommendDTO.setCustomScore(this.getCustomScore());
    shopRecommendDTO.setRecommendShopId(this.getRecommendShopId());
    shopRecommendDTO.setEditDate(this.getEditDate());
    return shopRecommendDTO;
  }

}
