package com.bcgogo.txn.dto;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.utils.StringUtil;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
public class PromotionsProductDTO {
  private Long id;
  private String idStr;
  private Long shopId;
  private Long promotionsId;
  private String promotionsIdStr;
  private Long productLocalInfoId;
  private String productLocalInfoIdStr;
  private DeletedType deleted;

  //下面属性是特价促销才用的到
  private PromotionsEnum.PromotionsTypes promotionsType;
  private PromotionsEnum.BargainType  bargainType;
  private Double discountAmount;
  private Double limitAmount;     //限购数量
  private boolean bargainFlag;   //前台是否选择了该商品的促销.判断用，不存储
  private boolean limitFlag;   //判断用，不存储

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    if(promotionsId!=null) promotionsIdStr = promotionsId.toString();
    this.promotionsId = promotionsId;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null) idStr = id.toString();
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
    this.setProductLocalInfoIdStr(StringUtil.valueOf(productLocalInfoId));
  }

  public String getProductLocalInfoIdStr() {
    return productLocalInfoIdStr;
  }

  public void setProductLocalInfoIdStr(String productLocalInfoIdStr) {
    this.productLocalInfoIdStr = productLocalInfoIdStr;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getPromotionsIdStr() {
    return promotionsIdStr;
  }

  public void setPromotionsIdStr(String promotionsIdStr) {
    this.promotionsIdStr = promotionsIdStr;
  }

  public PromotionsEnum.PromotionsTypes getPromotionsType() {
    return promotionsType;
  }

  public void setPromotionsType(PromotionsEnum.PromotionsTypes promotionsType) {
    this.promotionsType = promotionsType;
  }

  public PromotionsEnum.BargainType getBargainType() {
    return bargainType;
  }

  public void setBargainType(PromotionsEnum.BargainType bargainType) {
    this.bargainType = bargainType;
  }

  public Double getLimitAmount() {
    return limitAmount;
  }

  public void setLimitAmount(Double limitAmount) {
    this.limitAmount = limitAmount;
  }

  public boolean isBargainFlag() {
    return bargainFlag;
  }

  public void setBargainFlag(boolean bargainFlag) {
    this.bargainFlag = bargainFlag;
  }

  public boolean  isLimitFlag() {
    return limitFlag;
  }

  public void setLimitFlag(boolean limitFlag) {
    this.limitFlag = limitFlag;
  }

  public Double getDiscountAmount() {
    return discountAmount;
  }

  public void setDiscountAmount(Double discountAmount) {
    this.discountAmount = discountAmount;
  }
}
