package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-12
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class PrintTemplateDTO {
  private Long id;
  private Long shopId;
  private Long shopPrintTemplateId;
  private String name;
  private String displayName;
  private OrderTypes orderType;
  private byte[] templateHtml;
  private String idStr;

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(String.valueOf(id));
  }

  public Long getShopId() {
    return shopId;
  }

  public Long getShopPrintTemplateId() {
    return shopPrintTemplateId;
  }

  public void setShopPrintTemplateId(Long shopPrintTemplateId) {
    this.shopPrintTemplateId = shopPrintTemplateId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public byte[] getTemplateHtml() {
    return templateHtml;
  }

  public void setTemplateHtml(byte[] templateHtml) {
    this.templateHtml = templateHtml;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
