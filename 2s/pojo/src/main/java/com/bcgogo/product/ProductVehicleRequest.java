package com.bcgogo.product;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-28
 * Time: 上午11:21
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "product-vehicle")
@XmlAccessorType(XmlAccessType.NONE)
public class ProductVehicleRequest {
  @XmlElement(name = "productId")
  private Long productId;
  @XmlElement(name = "brandId")
  private Long brandId;
  @XmlElement(name = "mfrId")
  private Long mfrId;
  @XmlElement(name = "modelId")
  private Long modelId;
  @XmlElement(name = "yearId")
  private Long yearId;
  @XmlElement(name = "trimIdtrimId")
  private Long trimId;
  @XmlElement(name = "shopId")
  private Long shopId;
  @XmlElement(name = "engineId")
  private Long engineId;

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

  public Long getEngineId() {
    return engineId;
  }

  public void setEngineId(Long engineId) {
    this.engineId = engineId;
  }
}
