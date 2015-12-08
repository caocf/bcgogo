package com.bcgogo.config.dto;

import java.io.Serializable;

/**
 * User: ZhangJuntao
 * Date: 13-6-17
 * Time: 上午11:47
 * 客户管理的shop
 */
public class SupplierRelatedShopDTO implements Serializable {
  private Long shopId;
  private Long supplierId;
  private String supplierMobile;
  private Long relatedShopId;
  private String relatedShopName;
  private String supplierName;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getRelatedShopId() {
    return relatedShopId;
  }

  public void setRelatedShopId(Long relatedShopId) {
    this.relatedShopId = relatedShopId;
  }

  public String getSupplierMobile() {
    return supplierMobile;
  }

  public void setSupplierMobile(String supplierMobile) {
    this.supplierMobile = supplierMobile;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getRelatedShopName() {
    return relatedShopName;
  }

  public void setRelatedShopName(String relatedShopName) {
    this.relatedShopName = relatedShopName;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }
}
