package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.standardVehicleBrandModel.NormalProductVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;

import javax.persistence.*;

/**
 * 标准库的车辆品牌、车型
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-23
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "normal_product_vehicle_brand_model")
public class NormalProductVehicleBrandModel extends LongIdentifier {

  private Long normalProductId;
  private String firstLetter;//车辆品牌首字母（不一定是首个汉字的首字母）
  private String brandName;  //车辆品牌
  private String modelName; //车型
  private Long brandId;  //车辆品牌id
  private Long modelId; //车型id
  private DeletedType deleted = DeletedType.FALSE;

  @Column(name = "model_id")
  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  @Column(name = "brand_id")
  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  @Column(name = "model_name")
  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  @Column(name = "brand_name")
  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  @Column(name = "normal_product_id")
  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }



  @Column(name = "first_letter")
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public NormalProductVehicleBrandModelDTO toDTO() {
    NormalProductVehicleBrandModelDTO normalProductVehicleBrandModelDTO = new NormalProductVehicleBrandModelDTO();
    normalProductVehicleBrandModelDTO.setId(getId());
    normalProductVehicleBrandModelDTO.setNormalProductId(getNormalProductId());
    normalProductVehicleBrandModelDTO.setFirstLetter(getFirstLetter());
    normalProductVehicleBrandModelDTO.setBrandName(getBrandName());
    normalProductVehicleBrandModelDTO.setBrandId(getBrandId());
    normalProductVehicleBrandModelDTO.setModelId(getModelId());
    normalProductVehicleBrandModelDTO.setModelName(getModelName());
    normalProductVehicleBrandModelDTO.setDeleted(getDeleted());
    return normalProductVehicleBrandModelDTO;
  }

  public NormalProductVehicleBrandModel fromDTO(NormalProductVehicleBrandModelDTO normalProductVehicleBrandModelDTO) {
    this.setNormalProductId(normalProductVehicleBrandModelDTO.getNormalProductId());
    this.setFirstLetter(normalProductVehicleBrandModelDTO.getFirstLetter());
    this.setBrandName(normalProductVehicleBrandModelDTO.getBrandName());
    this.setBrandId(normalProductVehicleBrandModelDTO.getBrandId());
    this.setModelId(normalProductVehicleBrandModelDTO.getModelId());
    this.setModelName(normalProductVehicleBrandModelDTO.getModelName());
    this.setId(normalProductVehicleBrandModelDTO.getId());
    this.setDeleted(normalProductVehicleBrandModelDTO.getDeleted());
    return this;
  }
}
