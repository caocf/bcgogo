package com.bcgogo.search.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-12
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class CurrentUsedProductDTO {
  private Long shopId;
//  private Long productId;
  private Long timeOrder;
  private String productName;
  private String brand;
  private String type;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

//  public Long getProductId() {
//    return productId;
//  }
//
//  public void setProductId(Long productId) {
//    this.productId = productId;
//  }

  public Long getTimeOrder() {
    return timeOrder;
  }

  public void setTimeOrder(Long timeOrder) {
    this.timeOrder = timeOrder;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }


//  public boolean equals(Object o) {
//    if (o == null || ((CurrentUsedProductDTO) o).productId == null || this.productId == null) return false;
//    return (((CurrentUsedProductDTO) o).productId == this.productId) ? true : false;
//  }
//
//  public int hashCode() {
//    if (productId != null)
//      return productId.intValue();
//    else
//      return 0;
//  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
