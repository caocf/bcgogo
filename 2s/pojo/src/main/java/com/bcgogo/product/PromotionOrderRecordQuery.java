package com.bcgogo.product;

/**
 * 促销记录查询
 * User: terry
 * Date: 13-8-13
 * Time: 下午5:29
 */
public class PromotionOrderRecordQuery {

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  private Long productId;
  private Long orderId;
  private Long shopId;
  private Long supplierShopId;

  @Override
  public String toString() {
    return "PromotionOrderRecordQuery{" +
        "productId=" + productId +
        ", orderId=" + orderId +
        ", shopId=" + shopId +
        ", supplierShopId=" + supplierShopId +
        '}';
  }

}
