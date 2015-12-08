package com.bcgogo.txn.dto.recommend;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.OrderItemSearchResultDTO;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:12
 * To change this template use File | Settings | File Templates.
 */
public class PreBuyOrderItemRecommendDTO {
  private Long id;
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
  private String matchingRule;
  private Long editDate;

  public PreBuyOrderItemRecommendDTO() {

  }
  public PreBuyOrderItemRecommendDTO(Long shopId,String matchingRule, OrderItemSearchResultDTO orderItemSearchResultDTO,Long editDate) {
    this.shopId = shopId;

    this.seedProductName = orderItemSearchResultDTO.getOrderSearchConditionDTO().getProductName();
    this.seedProductBrand = orderItemSearchResultDTO.getOrderSearchConditionDTO().getProductBrand();
    this.seedProductSpec = orderItemSearchResultDTO.getOrderSearchConditionDTO().getProductSpec();
    this.seedProductModel = orderItemSearchResultDTO.getOrderSearchConditionDTO().getProductModel();
    this.seedProductVehicleModel = orderItemSearchResultDTO.getOrderSearchConditionDTO().getProductVehicleModel();
    this.seedProductVehicleBrand = orderItemSearchResultDTO.getOrderSearchConditionDTO().getProductVehicleBrand();

    this.matchedProductName = orderItemSearchResultDTO.getProductName();
    this.matchedProductBrand = orderItemSearchResultDTO.getProductBrand();
    this.matchedProductSpec = orderItemSearchResultDTO.getProductSpec();
    this.matchedProductModel = orderItemSearchResultDTO.getProductModel();
    this.matchedProductVehicleModel = orderItemSearchResultDTO.getProductVehicleModel();
    this.matchedProductVehicleBrand = orderItemSearchResultDTO.getProductVehicleBrand();

    this.preBuyOrderItemId = orderItemSearchResultDTO.getItemId();
    this.preBuyOrderId = orderItemSearchResultDTO.getOrderId();
    this.preBuyOrderShopId = orderItemSearchResultDTO.getShopId();
    this.customScore = orderItemSearchResultDTO.getCustomScore();
    this.editDate = editDate;
    this.matchingRule = matchingRule;
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

  public Long getPreBuyOrderItemId() {
    return preBuyOrderItemId;
  }

  public void setPreBuyOrderItemId(Long preBuyOrderItemId) {
    this.preBuyOrderItemId = preBuyOrderItemId;
  }

  public Long getPreBuyOrderShopId() {
    return preBuyOrderShopId;
  }

  public void setPreBuyOrderShopId(Long preBuyOrderShopId) {
    this.preBuyOrderShopId = preBuyOrderShopId;
  }

  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
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

  public String getSeedProductName() {
    return seedProductName;
  }

  public void setSeedProductName(String seedProductName) {
    this.seedProductName = seedProductName;
  }

  public String getSeedProductBrand() {
    return seedProductBrand;
  }

  public void setSeedProductBrand(String seedProductBrand) {
    this.seedProductBrand = seedProductBrand;
  }

  public String getSeedProductSpec() {
    return seedProductSpec;
  }

  public void setSeedProductSpec(String seedProductSpec) {
    this.seedProductSpec = seedProductSpec;
  }

  public String getSeedProductModel() {
    return seedProductModel;
  }

  public void setSeedProductModel(String seedProductModel) {
    this.seedProductModel = seedProductModel;
  }

  public String getSeedProductVehicleModel() {
    return seedProductVehicleModel;
  }

  public void setSeedProductVehicleModel(String seedProductVehicleModel) {
    this.seedProductVehicleModel = seedProductVehicleModel;
  }

  public String getSeedProductVehicleBrand() {
    return seedProductVehicleBrand;
  }

  public void setSeedProductVehicleBrand(String seedProductVehicleBrand) {
    this.seedProductVehicleBrand = seedProductVehicleBrand;
  }

  public String getMatchedProductName() {
    return matchedProductName;
  }

  public void setMatchedProductName(String matchedProductName) {
    this.matchedProductName = matchedProductName;
  }

  public String getMatchedProductBrand() {
    return matchedProductBrand;
  }

  public void setMatchedProductBrand(String matchedProductBrand) {
    this.matchedProductBrand = matchedProductBrand;
  }

  public String getMatchedProductSpec() {
    return matchedProductSpec;
  }

  public void setMatchedProductSpec(String matchedProductSpec) {
    this.matchedProductSpec = matchedProductSpec;
  }

  public String getMatchedProductModel() {
    return matchedProductModel;
  }

  public void setMatchedProductModel(String matchedProductModel) {
    this.matchedProductModel = matchedProductModel;
  }

  public String getMatchedProductVehicleModel() {
    return matchedProductVehicleModel;
  }

  public void setMatchedProductVehicleModel(String matchedProductVehicleModel) {
    this.matchedProductVehicleModel = matchedProductVehicleModel;
  }

  public String getMatchedProductVehicleBrand() {
    return matchedProductVehicleBrand;
  }

  public void setMatchedProductVehicleBrand(String matchedProductVehicleBrand) {
    this.matchedProductVehicleBrand = matchedProductVehicleBrand;
  }

  public String getMatchingRule() {
    return matchingRule;
  }

  public void setMatchingRule(String matchingRule) {
    this.matchingRule = matchingRule;
  }
}
