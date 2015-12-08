package com.bcgogo.txn.model.pushMessage;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.pushMessage.PushMessageReceiverMatchRecordDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "push_message_receiver_match_record")
public class PushMessageReceiverMatchRecord extends LongIdentifier {
  private Long messageId;
  private Long pushMessageReceiverId;

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

  private String matchingRule;
  private Long createTime;
  private Double customScore;

  @Column(name="seed_product_shop_id")
  public Long getSeedProductShopId() {
    return seedProductShopId;
  }

  public void setSeedProductShopId(Long seedProductShopId) {
    this.seedProductShopId = seedProductShopId;
  }

  @Column(name="message_id")
  public Long getMessageId() {
    return messageId;
  }

  public void setMessageId(Long messageId) {
    this.messageId = messageId;
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
  @Column(name="matching_rule")
  public String getMatchingRule() {
    return matchingRule;
  }

  public void setMatchingRule(String matchingRule) {
    this.matchingRule = matchingRule;
  }
  @Column(name="create_time")
  public Long getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Long createTime) {
    this.createTime = createTime;
  }

  @Column(name="push_message_receiver_id")
  public Long getPushMessageReceiverId() {
    return pushMessageReceiverId;
  }

  public void setPushMessageReceiverId(Long pushMessageReceiverId) {
    this.pushMessageReceiverId = pushMessageReceiverId;
  }
  @Column(name="custom_score")
  public Double getCustomScore() {
    return customScore;
  }

  public void setCustomScore(Double customScore) {
    this.customScore = customScore;
  }
  @Column(name="matched_product_shop_id")
  public Long getMatchedProductShopId() {
    return matchedProductShopId;
  }

  public void setMatchedProductShopId(Long matchedProductShopId) {
    this.matchedProductShopId = matchedProductShopId;
  }

  public void fromDTO(PushMessageReceiverMatchRecordDTO pushMessageReceiverMatchRecordDTO){
    this.setCreateTime(pushMessageReceiverMatchRecordDTO.getCreateTime());
    this.setPushMessageReceiverId(pushMessageReceiverMatchRecordDTO.getPushMessageReceiverId());
    this.setMessageId(pushMessageReceiverMatchRecordDTO.getMessageId());
    this.setSeedProductShopId(pushMessageReceiverMatchRecordDTO.getSeedProductShopId());
    this.setSeedProductName(pushMessageReceiverMatchRecordDTO.getSeedProductName());
    this.setSeedProductBrand(pushMessageReceiverMatchRecordDTO.getSeedProductBrand());
    this.setSeedProductModel(pushMessageReceiverMatchRecordDTO.getSeedProductModel());
    this.setSeedProductSpec(pushMessageReceiverMatchRecordDTO.getSeedProductSpec());
    this.setSeedProductVehicleBrand(pushMessageReceiverMatchRecordDTO.getSeedProductVehicleBrand());
    this.setSeedProductVehicleModel(pushMessageReceiverMatchRecordDTO.getSeedProductVehicleModel());

    this.setMatchedProductName(pushMessageReceiverMatchRecordDTO.getMatchedProductName());
    this.setMatchedProductBrand(pushMessageReceiverMatchRecordDTO.getMatchedProductBrand());
    this.setMatchedProductModel(pushMessageReceiverMatchRecordDTO.getMatchedProductModel());
    this.setMatchedProductSpec(pushMessageReceiverMatchRecordDTO.getMatchedProductSpec());
    this.setMatchedProductVehicleBrand(pushMessageReceiverMatchRecordDTO.getMatchedProductVehicleBrand());
    this.setMatchedProductVehicleModel(pushMessageReceiverMatchRecordDTO.getMatchedProductVehicleModel());
    this.setMatchedProductShopId(pushMessageReceiverMatchRecordDTO.getMatchedProductShopId());
    this.setMatchingRule(pushMessageReceiverMatchRecordDTO.getMatchingRule());
    this.setCustomScore(pushMessageReceiverMatchRecordDTO.getCustomScore());
  }

  public PushMessageReceiverMatchRecordDTO toDTO(){
    PushMessageReceiverMatchRecordDTO pushMessageReceiverMatchRecordDTO = new PushMessageReceiverMatchRecordDTO();
    pushMessageReceiverMatchRecordDTO.setCreateTime(this.getCreateTime());
    pushMessageReceiverMatchRecordDTO.setPushMessageReceiverId(this.getPushMessageReceiverId());
    pushMessageReceiverMatchRecordDTO.setMessageId(this.getMessageId());
    pushMessageReceiverMatchRecordDTO.setSeedProductShopId(this.getSeedProductShopId());
    pushMessageReceiverMatchRecordDTO.setSeedProductName(this.getSeedProductName());
    pushMessageReceiverMatchRecordDTO.setSeedProductBrand(this.getSeedProductBrand());
    pushMessageReceiverMatchRecordDTO.setSeedProductModel(this.getSeedProductModel());
    pushMessageReceiverMatchRecordDTO.setSeedProductSpec(this.getSeedProductSpec());
    pushMessageReceiverMatchRecordDTO.setSeedProductVehicleBrand(this.getSeedProductVehicleBrand());
    pushMessageReceiverMatchRecordDTO.setSeedProductVehicleModel(this.getSeedProductVehicleModel());

    pushMessageReceiverMatchRecordDTO.setMatchedProductName(this.getMatchedProductName());
    pushMessageReceiverMatchRecordDTO.setMatchedProductBrand(this.getMatchedProductBrand());
    pushMessageReceiverMatchRecordDTO.setMatchedProductModel(this.getMatchedProductModel());
    pushMessageReceiverMatchRecordDTO.setMatchedProductSpec(this.getMatchedProductSpec());
    pushMessageReceiverMatchRecordDTO.setMatchedProductVehicleBrand(this.getMatchedProductVehicleBrand());
    pushMessageReceiverMatchRecordDTO.setMatchedProductVehicleModel(this.getMatchedProductVehicleModel());
    pushMessageReceiverMatchRecordDTO.setId(this.getId());
    pushMessageReceiverMatchRecordDTO.setMatchingRule(this.getMatchingRule());
    pushMessageReceiverMatchRecordDTO.setCustomScore(this.getCustomScore());
    pushMessageReceiverMatchRecordDTO.setMatchedProductShopId(this.getMatchedProductShopId());
    return pushMessageReceiverMatchRecordDTO;
  }
}
