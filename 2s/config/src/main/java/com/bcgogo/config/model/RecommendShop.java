package com.bcgogo.config.model;

import com.bcgogo.config.dto.RecommendShopDTO;
import com.bcgogo.enums.shop.RecommendShopStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.ShopRecommendDTO;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-8-19.
 */
@Entity
@Table(name = "recommend_shop")
public class RecommendShop extends LongIdentifier {
  private Long recommendId;
  private Long shopId;
  private RecommendShopStatus status;

  public RecommendShop(){

  }

  public RecommendShop(Long recommendId,Long shopId){
    this.recommendId = recommendId;
    this.shopId = shopId;
    this.status = RecommendShopStatus.ENABLED;
  }

  public RecommendShopDTO toDTO() {
    RecommendShopDTO recommendShopDTO = new RecommendShopDTO();
    recommendShopDTO.setRecommendId(getRecommendId());
    recommendShopDTO.setShopId(getShopId());
    recommendShopDTO.setStatus(getStatus());
    return recommendShopDTO;
  }

  @Column(name="recommend_id")
  public Long getRecommendId() {
    return recommendId;
  }

  public void setRecommendId(Long recommendId) {
    this.recommendId = recommendId;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="status")
  @Enumerated(EnumType.STRING)
  public RecommendShopStatus getStatus() {
    return status;
  }

  public void setStatus(RecommendShopStatus status) {
    this.status = status;
  }


}
