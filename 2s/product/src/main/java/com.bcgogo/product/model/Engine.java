package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午5:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "engine")
public class Engine extends LongIdentifier {
  private Long brandId;
  private Long mfrId;
  private Long modelId;
  private Long yearId;
  private String engine;
  private Long state;
  private String memo;
  private Long shopId;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "engine",length = 10)
  public String getEngine() {
    return engine;
  }

  public void setEngine(String engine) {
    this.engine = engine;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "memo", length = 2000)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}
