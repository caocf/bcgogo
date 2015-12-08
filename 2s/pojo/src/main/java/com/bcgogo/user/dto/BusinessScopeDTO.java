package com.bcgogo.user.dto;

/**
 * 客户或者供应商经营范围
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-19
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public class BusinessScopeDTO {
  private Long id;
  private Long shopId;
  private Long customerId;
  private Long supplierId;
  private Long productCategoryId;

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

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }
}
