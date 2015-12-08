package com.bcgogo.user.dto;


import com.bcgogo.enums.user.VehicleBrandModelDataType;

/**
 * 主营车辆品牌、车型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class VehicleBrandModelRelationDTO {

  private Long id;
  private Long shopId;
  private Long dataId;
  private VehicleBrandModelDataType dataType;
  private String firstLetter;//车辆品牌首字母（不一定是首个汉字的首字母）
  private String brandName;  //车辆品牌
  private String modelName; //车型
  private Long brandId;  //车辆品牌id
  private Long modelId; //车型id

  public Long getDataId() {
    return dataId;
  }

  public void setDataId(Long dataId) {
    this.dataId = dataId;
  }

  public VehicleBrandModelDataType getDataType() {
    return dataType;
  }

  public void setDataType(VehicleBrandModelDataType dataType) {
    this.dataType = dataType;
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

  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }
}
