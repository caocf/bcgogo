package com.bcgogo.txn.dto.recommend;

import com.bcgogo.enums.ProductRecommendType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.DateUtil;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:12
 * To change this template use File | Settings | File Templates.
 */
public class ProductRecommendDTO {
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
  private Long matchedProductLocalInfoId;
  private Long matchedProductShopId;

  private Double customScore;
  private String matchingRule;
  private Long editDate;
  private ProductRecommendType productRecommendType;

  public ProductRecommendDTO() {

  }
  public ProductRecommendDTO(Long shopId,String matchingRule,ProductDTO matchedProductDTO,Long editDate,ProductRecommendType productRecommendType) {
    this.shopId = shopId;

    this.seedProductName = matchedProductDTO.getSearchConditionDTO().getProductName();
    this.seedProductBrand = matchedProductDTO.getSearchConditionDTO().getProductBrand();
    this.seedProductSpec = matchedProductDTO.getSearchConditionDTO().getProductSpec();
    this.seedProductModel = matchedProductDTO.getSearchConditionDTO().getProductModel();
    this.seedProductVehicleModel = matchedProductDTO.getSearchConditionDTO().getProductVehicleModel();
    this.seedProductVehicleBrand = matchedProductDTO.getSearchConditionDTO().getProductVehicleBrand();

    this.matchedProductName = matchedProductDTO.getName();
    this.matchedProductBrand = matchedProductDTO.getBrand();
    this.matchedProductSpec = matchedProductDTO.getSpec();
    this.matchedProductModel = matchedProductDTO.getModel();
    this.matchedProductVehicleModel = matchedProductDTO.getProductVehicleModel();
    this.matchedProductVehicleBrand = matchedProductDTO.getProductVehicleBrand();

    this.matchedProductLocalInfoId = matchedProductDTO.getProductLocalInfoId();
    this.matchedProductShopId = matchedProductDTO.getShopId();

    this.customScore = matchedProductDTO.getCustomScore();
    this.editDate= editDate;
    this.matchingRule= matchingRule;
    this.productRecommendType= productRecommendType;
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

  public ProductRecommendType getProductRecommendType() {
    return productRecommendType;
  }

  public void setProductRecommendType(ProductRecommendType productRecommendType) {
    this.productRecommendType = productRecommendType;
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

  public Long getMatchedProductLocalInfoId() {
    return matchedProductLocalInfoId;
  }

  public void setMatchedProductLocalInfoId(Long matchedProductLocalInfoId) {
    this.matchedProductLocalInfoId = matchedProductLocalInfoId;
  }

  public Long getMatchedProductShopId() {
    return matchedProductShopId;
  }

  public void setMatchedProductShopId(Long matchedProductShopId) {
    this.matchedProductShopId = matchedProductShopId;
  }

  public String getMatchingRule() {
    return matchingRule;
  }

  public void setMatchingRule(String matchingRule) {
    this.matchingRule = matchingRule;
  }
}
