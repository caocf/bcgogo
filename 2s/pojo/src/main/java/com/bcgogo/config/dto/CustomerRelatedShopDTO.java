package com.bcgogo.config.dto;

import java.io.Serializable;

/**
 * User: ZhangJuntao
 * Date: 13-6-17
 * Time: 上午11:47
 * 客户管理的shop
 */
public class CustomerRelatedShopDTO implements Serializable {
  private Long shopId;
  private Long customerId;
  private String customerMobile;
  private Long relatedShopId;
  private String relatedShopName;
  private String customerName;

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


  public Long getRelatedShopId() {
    return relatedShopId;
  }

  public void setRelatedShopId(Long relatedShopId) {
    this.relatedShopId = relatedShopId;
  }

  public String getCustomerMobile() {
    return customerMobile;
  }

  public void setCustomerMobile(String customerMobile) {
    this.customerMobile = customerMobile;
  }

  public String getRelatedShopName() {
    return relatedShopName;
  }

  public void setRelatedShopName(String relatedShopName) {
    this.relatedShopName = relatedShopName;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
}
