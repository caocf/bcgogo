package com.bcgogo.product.dto;

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 13-8-30
 * Time: 上午11:54
 * To change this template use File | Settings | File Templates.
 */
public class ProductCategoryRelationDTO {

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public Long getShopId() {

    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  private Long shopId;
  private Long productCategoryId;
  private Long productLocalInfoId;


}
