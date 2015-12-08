package com.bcgogo.user.model;

import com.bcgogo.enums.user.VehicleBrandModelDataType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.user.dto.VehicleBrandModelRelationDTO;

import javax.persistence.*;

/**
 * 店铺注册时填写的车辆品牌、车型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "vehicle_brand_model_relation")
public class VehicleBrandModelRelation extends LongIdentifier {

  private Long shopId;
  private Long dataId;
  private VehicleBrandModelDataType dataType;
  private String firstLetter;//车辆品牌首字母（不一定是首个汉字的首字母）
  private String brandName;  //车辆品牌
  private String modelName; //车型
  private Long brandId;  //车辆品牌id
  private Long modelId; //车型id
  @Column(name = "data_id")
  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  @Column(name = "data_type")
  @Enumerated(EnumType.STRING)
  public VehicleBrandModelDataType getDataType() {
    return dataType;
  }

  public void setDataType(VehicleBrandModelDataType dataType) {
    this.dataType = dataType;
  }

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

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "first_letter")
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public VehicleBrandModelRelationDTO toDTO() {
    VehicleBrandModelRelationDTO vehicleBrandModelRelationDTO = new VehicleBrandModelRelationDTO();
    vehicleBrandModelRelationDTO.setId(getId());
    vehicleBrandModelRelationDTO.setShopId(getShopId());
    vehicleBrandModelRelationDTO.setFirstLetter(getFirstLetter());
    vehicleBrandModelRelationDTO.setBrandName(getBrandName());
    vehicleBrandModelRelationDTO.setBrandId(getBrandId());
    vehicleBrandModelRelationDTO.setModelId(getModelId());
    vehicleBrandModelRelationDTO.setModelName(getModelName());
    vehicleBrandModelRelationDTO.setDataId(vehicleBrandModelRelationDTO.getDataId());
    vehicleBrandModelRelationDTO.setDataType(vehicleBrandModelRelationDTO.getDataType());
    return vehicleBrandModelRelationDTO;
  }

  public VehicleBrandModelRelation fromDTO(VehicleBrandModelRelationDTO vehicleBrandModelRelationDTO) {
    this.setShopId(vehicleBrandModelRelationDTO.getShopId());
    this.setFirstLetter(vehicleBrandModelRelationDTO.getFirstLetter());
    this.setBrandName(vehicleBrandModelRelationDTO.getBrandName());
    this.setBrandId(vehicleBrandModelRelationDTO.getBrandId());
    this.setModelId(vehicleBrandModelRelationDTO.getModelId());
    this.setModelName(vehicleBrandModelRelationDTO.getModelName());
    this.setId(vehicleBrandModelRelationDTO.getId());
    this.setDataId(vehicleBrandModelRelationDTO.getDataId());
    this.setDataType(vehicleBrandModelRelationDTO.getDataType());
    return this;
  }
}
