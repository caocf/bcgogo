package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-18
 * Time: 下午2:37
 * To change this template use File | Settings | File Templates.
 */
public class ShopPrintTemplateDTO {
  private Long id;
  private Long shopId;
  private Long templateId;
  private String displayName;
  private OrderTypes orderType;


  public Long getId() {
    return id;
  }

  public Long getShopId() {
    return shopId;
  }

  public Long getTemplateId() {
    return templateId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
