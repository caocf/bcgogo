package com.bcgogo.txn.dto.pushMessage;

import com.bcgogo.product.dto.ProductDTO;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class PushMessageReceiverMatchRecordDTO {
  private Long id;
  private Long pushMessageReceiverId;
  private Long messageId;
  private Long seedProductShopId;
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
  private Long matchedProductShopId;

  private Long createTime;
  private String matchingRule;
  private Double customScore;

  public PushMessageReceiverMatchRecordDTO() {

  }
  public PushMessageReceiverMatchRecordDTO(Long createTime, String matchingRule, Double customScore, ProductDTO seedDTO, ProductDTO matchedDTO) {
    this.createTime = createTime;
    this.matchingRule = matchingRule;
    this.customScore = customScore;

    this.seedProductShopId = seedDTO.getShopId();
    this.seedProductName = seedDTO.getName();
    this.seedProductBrand = seedDTO.getBrand();
    this.seedProductSpec = seedDTO.getSpec();
    this.seedProductModel =seedDTO.getModel();
    this.seedProductVehicleModel = seedDTO.getProductVehicleModel();
    this.seedProductVehicleBrand = seedDTO.getProductVehicleBrand();

    this.matchedProductName = matchedDTO.getName();
    this.matchedProductBrand = matchedDTO.getBrand();
    this.matchedProductSpec = matchedDTO.getSpec();
    this.matchedProductModel = matchedDTO.getModel();
    this.matchedProductVehicleModel = matchedDTO.getProductVehicleModel();
    this.matchedProductVehicleBrand = matchedDTO.getProductVehicleBrand();
    this.matchedProductShopId = matchedDTO.getShopId();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSeedProductShopId() {
    return seedProductShopId;
  }

  public void setSeedProductShopId(Long seedProductShopId) {
    this.seedProductShopId = seedProductShopId;
  }

  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
  }

  public Long getPushMessageReceiverId() {
    return pushMessageReceiverId;
  }

  public void setPushMessageReceiverId(Long pushMessageReceiverId) {
    this.pushMessageReceiverId = pushMessageReceiverId;
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

  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  public String getMatchingRule() {
    return matchingRule;
  }

  public void setMatchingRule(String matchingRule) {
    this.matchingRule = matchingRule;
  }

  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }

  public Long getMatchedProductShopId() {
    return matchedProductShopId;
  }

  public void setMatchedProductShopId(Long matchedProductShopId) {
    this.matchedProductShopId = matchedProductShopId;
  }
}
