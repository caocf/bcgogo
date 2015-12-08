package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.PromotionsProductDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.*;

/**
 * 现在个性促销和商品是一对一的关系
 * 全场促销和商品是一对多的关系
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "promotions_product")
public class PromotionsProduct extends LongIdentifier {
  private Long shopId;
  private Long promotionsId;
  private Long productLocalInfoId;
  private DeletedType deleted = DeletedType.FALSE;
  //下面属性是特价促销才用的到
  private PromotionsEnum.PromotionsTypes promotionsType;
  private PromotionsEnum.BargainType  bargainType;
  private Double discountAmount;
  private Double limitAmount;     //限购数量

  @Column(name = "promotions_id")
  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name="promotions_type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionsTypes getPromotionsType() {
    return promotionsType;
  }

  public void setPromotionsType(PromotionsEnum.PromotionsTypes promotionsType) {
    this.promotionsType = promotionsType;
  }

  @Column(name="bargain_type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.BargainType getBargainType() {
    return bargainType;
  }

  public void setBargainType(PromotionsEnum.BargainType bargainType) {
    this.bargainType = bargainType;
  }

  @Column(name="limit_amount")
  public Double getLimitAmount() {
    return limitAmount;
  }

  public void setLimitAmount(Double limitAmount) {
    this.limitAmount = limitAmount;
  }

    @Column(name="discount_amount")
  public Double getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(Double discountAmount) {
    this.discountAmount = discountAmount;
  }

  public PromotionsProductDTO toDTO(){
    PromotionsProductDTO promotionsProductDTO = new PromotionsProductDTO();
    promotionsProductDTO.setDeleted(this.getDeleted());
    promotionsProductDTO.setId(this.getId());
    promotionsProductDTO.setProductLocalInfoId(this.getProductLocalInfoId());
    promotionsProductDTO.setPromotionsId(this.getPromotionsId());
    promotionsProductDTO.setShopId(this.getShopId());
    promotionsProductDTO.setBargainType(this.getBargainType());
    promotionsProductDTO.setPromotionsType(this.getPromotionsType());
    promotionsProductDTO.setDiscountAmount(this.getDiscountAmount());
    promotionsProductDTO.setLimitAmount(this.getLimitAmount());
    if(NumberUtil.doubleVal(promotionsProductDTO.getLimitAmount())>0){
      promotionsProductDTO.setLimitFlag(true);
    }
    return promotionsProductDTO;
  }

  public PromotionsProduct fromDTO(PromotionsProductDTO dto){
    this.setPromotionsId(dto.getPromotionsId());
    this.setProductLocalInfoId(dto.getProductLocalInfoId());
    this.setBargainType(dto.getBargainType());
    this.setLimitAmount(dto.getLimitAmount());
    this.setDiscountAmount(dto.getDiscountAmount());
    this.setPromotionsType(dto.getPromotionsType());
    return this;
  }

}
