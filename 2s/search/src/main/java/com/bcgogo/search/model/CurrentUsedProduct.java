package com.bcgogo.search.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.search.dto.CurrentUsedProductDTO;
import com.bcgogo.utils.SearchConstant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-27
 * Time: 下午4:38
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "current_used_product")
public class CurrentUsedProduct extends LongIdentifier {
  private Long shopId;
//  private Long productId;
  private Long timeOrder;
  private String productName;
  private String brand;
  private String type;

  public CurrentUsedProduct() {
  }

  public CurrentUsedProduct(CurrentUsedProductDTO currentUsedProductDTO) {
    this.shopId = currentUsedProductDTO.getShopId();
//    this.productId = currentUsedProductDTO.getProductId();
    this.timeOrder = currentUsedProductDTO.getTimeOrder();
    this.brand = currentUsedProductDTO.getBrand();
    this.productName = currentUsedProductDTO.getProductName();
    this.type = currentUsedProductDTO.getType();
  }

  public CurrentUsedProduct fromDTO(CurrentUsedProductDTO currentUsedProductDTO) {
    this.shopId = currentUsedProductDTO.getShopId();
//    this.productId = currentUsedProductDTO.getProductId();
    this.timeOrder = currentUsedProductDTO.getTimeOrder();
    this.brand = currentUsedProductDTO.getBrand();
    this.productName = currentUsedProductDTO.getProductName();
    this.type = currentUsedProductDTO.getType();
    return this;
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

//  @Column(name = "product_id")
//  public Long getProductId() {
//    return productId;
//  }
//
//  public void setProductId(Long productId) {
//    this.productId = productId;
//  }

  @Column(name = "time_order")
  public Long getTimeOrder() {
    return timeOrder;
  }

  public void setTimeOrder(Long timeOrder) {
    this.timeOrder = timeOrder;
  }

  @Column(name = "product_name", length = 200)
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

  public boolean equals(CurrentUsedProductDTO currentUsedProductDTO, String type) {
    if (currentUsedProductDTO == null) return false;
    if (type.equals(SearchConstant.PRODUCT_BRAND)) {
      if (currentUsedProductDTO.getBrand() == null || this.getBrand() == null) return false;
      return this.getBrand().equals(currentUsedProductDTO.getBrand()) ? true : false;
    } else {
      if (currentUsedProductDTO.getProductName() == null || this.getProductName() == null) return false;
      return this.getProductName().equals(currentUsedProductDTO.getProductName()) ? true : false;
    }
  }

  @Column(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
