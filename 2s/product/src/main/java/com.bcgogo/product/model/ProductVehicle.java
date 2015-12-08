package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午5:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_vehicle")
public class ProductVehicle extends LongIdentifier {
  private Long productId;
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private Long trimId;
  private Long shopId;
  private Long engineId;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "brand_id")
  public Long getBrandId() {
    return brandId;
  }

  public void setBrandId(Long brandId) {
    this.brandId = brandId;
  }

  @Column(name = "mfr_id")
  public Long getMfrId() {
    return mfrId;
  }

  public void setMfrId(Long mfrId) {
    this.mfrId = mfrId;
  }

  @Column(name = "model_id")
  public Long getModelId() {
    return modelId;
  }

  public void setModelId(Long modelId) {
    this.modelId = modelId;
  }

  @Column(name = "year_id")
  public Long getYearId() {
    return yearId;
  }

  public void setYearId(Long yearId) {
    this.yearId = yearId;
  }

  @Column(name = "trim_id")
  public Long getTrimId() {
    return trimId;
  }

  public void setTrimId(Long trimId) {
    this.trimId = trimId;
  }

  @Column(name = "engine_id")
  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }
}
