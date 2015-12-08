package com.bcgogo.product.dto;

import com.bcgogo.product.ProductVehicleRequest;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
public class ProductVehicleDTO implements Serializable {
  private Long productId;
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private Long trimId;
  private Long id;
  private Long shopId;
  private Long engineId;
  private String pvBrand;
  private String pvModel;
  private String pvYear;
  private String pvEngine;

  public ProductVehicleDTO() {
  }

  public ProductVehicleDTO(ProductVehicleRequest request) {
    setProductId(request.getProductId());
    setBrandId(request.getBrandId());
    setMfrId(request.getMfrId());
    setModelId(request.getModelId());
    setYearId(request.getYearId());
    setTrimId(request.getTrimId());
    setShopId(request.getShopId());
    setEngineId(request.getEngineId());
  }

  public String getPvBrand() {
    return pvBrand;
  }

  public void setPvBrand(String pvBrand) {
    this.pvBrand = pvBrand;
  }

  public String getPvModel() {
    return pvModel;
  }

  public void setPvModel(String pvModel) {
    this.pvModel = pvModel;
  }

  public String getPvYear() {
    return pvYear;
  }

  public void setPvYear(String pvYear) {
    this.pvYear = pvYear;
  }

  public String getPvEngine() {
    return pvEngine;
  }

  public void setPvEngine(String pvEngine) {
    this.pvEngine = pvEngine;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  public Long getTrimId() {
    return trimId;
  }

  public void setTrimId(Long trimId) {
    this.trimId = trimId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }
}
