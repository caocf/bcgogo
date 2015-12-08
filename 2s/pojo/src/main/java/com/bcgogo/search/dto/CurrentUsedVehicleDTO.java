package com.bcgogo.search.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-29
 * Time: 上午9:44
 * To change this template use File | Settings | File Templates.
 */
public class CurrentUsedVehicleDTO {
  private Long shopId;
  private Long timeOrder;
  private String brand;
//  private String type;
//  private String productName

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getTimeOrder() {
    return timeOrder;
  }

  public void setTimeOrder(Long timeOrder) {
    this.timeOrder = timeOrder;
  }


  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

//  public String getType() {
//    return type;
//  }
//
//  public void setType(String type) {
//    this.type = type;
//  }
//
//  public String getProductName() {
//    return productName;
//  }
//
//  public void setProductName(String productName) {
//    this.productName = productName;
//  }
}
