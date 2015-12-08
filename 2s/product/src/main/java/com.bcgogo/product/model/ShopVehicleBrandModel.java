package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;

import javax.persistence.*;

/**
 * 店铺注册时填写的车辆品牌、车型.主营车型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_vehicle_brand_model")
public class ShopVehicleBrandModel extends LongIdentifier {

  private Long shopId;
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

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public ShopVehicleBrandModelDTO toDTO() {
    ShopVehicleBrandModelDTO shopVehicleBrandModelDTO = new ShopVehicleBrandModelDTO();
    shopVehicleBrandModelDTO.setId(getId());
    shopVehicleBrandModelDTO.setShopId(getShopId());
    shopVehicleBrandModelDTO.setFirstLetter(getFirstLetter());
    shopVehicleBrandModelDTO.setBrandName(getBrandName());
    shopVehicleBrandModelDTO.setBrandId(getBrandId());
    shopVehicleBrandModelDTO.setModelId(getModelId());
    shopVehicleBrandModelDTO.setModelName(getModelName());
    shopVehicleBrandModelDTO.setDeleted(getDeleted());
    return shopVehicleBrandModelDTO;
  }

  public ShopVehicleBrandModel fromDTO(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO) {
    this.setShopId(shopVehicleBrandModelDTO.getShopId());
    this.setFirstLetter(shopVehicleBrandModelDTO.getFirstLetter());
    this.setBrandName(shopVehicleBrandModelDTO.getBrandName());
    this.setBrandId(shopVehicleBrandModelDTO.getBrandId());
    this.setModelId(shopVehicleBrandModelDTO.getModelId());
    this.setModelName(shopVehicleBrandModelDTO.getModelName());
    this.setId(shopVehicleBrandModelDTO.getId());
    this.setDeleted(shopVehicleBrandModelDTO.getDeleted());
    return this;
  }
}
