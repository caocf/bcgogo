package com.bcgogo.txn.model.recommend;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.ProductRecommendDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "product_recommend_trace")
public class ProductRecommendTrace extends LongIdentifier{
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
  private Long matchedProductLocalInfoId;
  private Long matchedProductShopId;
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

  @Column(name="matched_product_local_info_id")
  public Long getMatchedProductLocalInfoId() {
    return matchedProductLocalInfoId;
  }

  public void setMatchedProductLocalInfoId(Long matchedProductLocalInfoId) {
    this.matchedProductLocalInfoId = matchedProductLocalInfoId;
  }

  @Column(name="matched_product_shop_id")
  public Long getMatchedProductShopId() {
    return matchedProductShopId;
  }

  public void setMatchedProductShopId(Long matchedProductShopId) {
    this.matchedProductShopId = matchedProductShopId;
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
  public void fromProductRecommendDTO(ProductRecommendDTO productRecommendDTO){
    this.setId(productRecommendDTO.getId());
    this.setShopId(productRecommendDTO.getShopId());

    this.setSeedProductName(productRecommendDTO.getSeedProductName());
    this.setSeedProductBrand(productRecommendDTO.getSeedProductBrand());
    this.setSeedProductModel(productRecommendDTO.getSeedProductModel());
    this.setSeedProductSpec(productRecommendDTO.getSeedProductSpec());
    this.setSeedProductVehicleBrand(productRecommendDTO.getSeedProductVehicleBrand());
    this.setSeedProductVehicleModel(productRecommendDTO.getSeedProductVehicleModel());

    this.setMatchedProductName(productRecommendDTO.getMatchedProductName());
    this.setMatchedProductBrand(productRecommendDTO.getMatchedProductBrand());
    this.setMatchedProductModel(productRecommendDTO.getMatchedProductModel());
    this.setMatchedProductSpec(productRecommendDTO.getMatchedProductSpec());
    this.setMatchedProductVehicleBrand(productRecommendDTO.getMatchedProductVehicleBrand());
    this.setMatchedProductVehicleModel(productRecommendDTO.getMatchedProductVehicleModel());

    this.setMatchedProductLocalInfoId(productRecommendDTO.getMatchedProductLocalInfoId());
    this.setMatchedProductShopId(productRecommendDTO.getMatchedProductShopId());
    this.setCustomScore(productRecommendDTO.getCustomScore());
    this.setEditDate(productRecommendDTO.getEditDate());
    this.setMatchingRule(productRecommendDTO.getMatchingRule());
  }
}
