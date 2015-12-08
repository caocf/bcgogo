package com.bcgogo.txn.model.recommend;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.PreBuyOrderItemRecommendDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "pre_buy_order_item_recommend_trace")
public class PreBuyOrderItemRecommendTrace extends LongIdentifier{
  private Long shopId;

  private String seedProductName;
  private String seedProductBrand;
  private String seedProductSpec;
  private String seedProductModel;
  private String seedProductVehicleModel;
  private String seedProductVehicleBrand;

  private String matchedProductName;
  private String matchedProductBrand;
  private String matchedProductSpec;
  private String matchedProductModel;
  private String matchedProductVehicleModel;
  private String matchedProductVehicleBrand;

  private Long preBuyOrderItemId;
  private Long preBuyOrderShopId;
  private Long preBuyOrderId;
  private Double customScore;
  private Long editDate;
  private String matchingRule;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


  @Column(name="seed_product_name")
  public String getSeedProductName() {
    return seedProductName;
  }

  public void setSeedProductName(String seedProductName) {
    this.seedProductName = seedProductName;
  }

  @Column(name="seed_product_brand")
  public String getSeedProductBrand() {
    return seedProductBrand;
  }

  public void setSeedProductBrand(String seedProductBrand) {
    this.seedProductBrand = seedProductBrand;
  }

  @Column(name="seed_product_spec")
  public String getSeedProductSpec() {
    return seedProductSpec;
  }

  public void setSeedProductSpec(String seedProductSpec) {
    this.seedProductSpec = seedProductSpec;
  }

  @Column(name="seed_product_model")
  public String getSeedProductModel() {
    return seedProductModel;
  }

  public void setSeedProductModel(String seedProductModel) {
    this.seedProductModel = seedProductModel;
  }

  @Column(name="seed_product_vehicle_model")
  public String getSeedProductVehicleModel() {
    return seedProductVehicleModel;
  }

  public void setSeedProductVehicleModel(String seedProductVehicleModel) {
    this.seedProductVehicleModel = seedProductVehicleModel;
  }

  @Column(name="seed_product_vehicle_brand")
  public String getSeedProductVehicleBrand() {
    return seedProductVehicleBrand;
  }

  public void setSeedProductVehicleBrand(String seedProductVehicleBrand) {
    this.seedProductVehicleBrand = seedProductVehicleBrand;
  }

  @Column(name="matched_product_name")
  public String getMatchedProductName() {
    return matchedProductName;
  }

  public void setMatchedProductName(String matchedProductName) {
    this.matchedProductName = matchedProductName;
  }

  @Column(name="matched_product_brand")
  public String getMatchedProductBrand() {
    return matchedProductBrand;
  }

  public void setMatchedProductBrand(String matchedProductBrand) {
    this.matchedProductBrand = matchedProductBrand;
  }

  @Column(name="matched_product_spec")
  public String getMatchedProductSpec() {
    return matchedProductSpec;
  }

  public void setMatchedProductSpec(String matchedProductSpec) {
    this.matchedProductSpec = matchedProductSpec;
  }

  @Column(name="matched_product_model")
  public String getMatchedProductModel() {
    return matchedProductModel;
  }

  public void setMatchedProductModel(String matchedProductModel) {
    this.matchedProductModel = matchedProductModel;
  }

  @Column(name="matched_product_vehicle_model")
  public String getMatchedProductVehicleModel() {
    return matchedProductVehicleModel;
  }

  public void setMatchedProductVehicleModel(String matchedProductVehicleModel) {
    this.matchedProductVehicleModel = matchedProductVehicleModel;
  }

  @Column(name="matched_product_vehicle_brand")
  public String getMatchedProductVehicleBrand() {
    return matchedProductVehicleBrand;
  }

  public void setMatchedProductVehicleBrand(String matchedProductVehicleBrand) {
    this.matchedProductVehicleBrand = matchedProductVehicleBrand;
  }

  @Column(name="pre_buy_order_item_id")
  public Long getPreBuyOrderItemId() {
    return preBuyOrderItemId;
  }

  public void setPreBuyOrderItemId(Long preBuyOrderItemId) {
    this.preBuyOrderItemId = preBuyOrderItemId;
  }

  @Column(name="pre_buy_order_shop_id")
  public Long getPreBuyOrderShopId() {
    return preBuyOrderShopId;
  }

  public void setPreBuyOrderShopId(Long preBuyOrderShopId) {
    this.preBuyOrderShopId = preBuyOrderShopId;
  }

  @Column(name="pre_buy_order_id")
  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  @Column(name="custom_score")
  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }
  @Column(name="edit_date")
  public Long getEditDate() {
    return editDate;
  }

  public void setEditDate(Long editDate) {
    this.editDate = editDate;
  }

  @Column(name="matching_rule")
  public String getMatchingRule() {
    return matchingRule;
  }

  public void setMatchingRule(String matchingRule) {
    this.matchingRule = matchingRule;
  }
  public void fromPreBuyOrderItemRecommendDTO(PreBuyOrderItemRecommendDTO preBuyOrderItemRecommendDTO) {
    this.setId(preBuyOrderItemRecommendDTO.getId());

    this.setSeedProductName(preBuyOrderItemRecommendDTO.getSeedProductName());
    this.setSeedProductBrand(preBuyOrderItemRecommendDTO.getSeedProductBrand());
    this.setSeedProductModel(preBuyOrderItemRecommendDTO.getSeedProductModel());
    this.setSeedProductSpec(preBuyOrderItemRecommendDTO.getSeedProductSpec());
    this.setSeedProductVehicleBrand(preBuyOrderItemRecommendDTO.getSeedProductVehicleBrand());
    this.setSeedProductVehicleModel(preBuyOrderItemRecommendDTO.getSeedProductVehicleModel());

    this.setMatchedProductName(preBuyOrderItemRecommendDTO.getMatchedProductName());
    this.setMatchedProductBrand(preBuyOrderItemRecommendDTO.getMatchedProductBrand());
    this.setMatchedProductModel(preBuyOrderItemRecommendDTO.getMatchedProductModel());
    this.setMatchedProductSpec(preBuyOrderItemRecommendDTO.getMatchedProductSpec());
    this.setMatchedProductVehicleBrand(preBuyOrderItemRecommendDTO.getMatchedProductVehicleBrand());
    this.setMatchedProductVehicleModel(preBuyOrderItemRecommendDTO.getMatchedProductVehicleModel());

    this.setShopId(preBuyOrderItemRecommendDTO.getShopId());
    this.setPreBuyOrderId(preBuyOrderItemRecommendDTO.getPreBuyOrderId());
    this.setPreBuyOrderItemId(preBuyOrderItemRecommendDTO.getPreBuyOrderItemId());
    this.setPreBuyOrderShopId(preBuyOrderItemRecommendDTO.getPreBuyOrderShopId());
    this.setCustomScore(preBuyOrderItemRecommendDTO.getCustomScore());
    this.setEditDate(preBuyOrderItemRecommendDTO.getEditDate());
    this.setMatchingRule(preBuyOrderItemRecommendDTO.getMatchingRule());

  }
}
