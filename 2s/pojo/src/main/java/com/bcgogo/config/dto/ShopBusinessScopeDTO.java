package com.bcgogo.config.dto;

/**
 * 店铺经营范围
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-17
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public class ShopBusinessScopeDTO {
  private Long id;
  private Long shopId;
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

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }
}
