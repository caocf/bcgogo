package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-1
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class SalesRemindEventDTO implements Serializable {
  public SalesRemindEventDTO() {
  }

  private Long id;
  private Long shopId;
  private Long salesOrderId;
  /**
   * 1:还款
   */
  private Long eventType;
  /**
   * 当eventType=1 还款时间
   */
  private Long eventContent;
  private String eventContentStr;

  public String getEventContentStr() {
    return eventContentStr;
  }

  public void setEventContentStr(String eventContentStr) {
    this.eventContentStr = eventContentStr;
  }

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

  public Long getSalesOrderId() {
    return salesOrderId;
  }

  public void setSalesOrderId(Long salesOrderId) {
    this.salesOrderId = salesOrderId;
  }

  public Long getEventType() {
    return eventType;
  }

  public void setEventType(Long eventType) {
    this.eventType = eventType;
  }

  public Long getEventContent() {
    return eventContent;
  }

  public void setEventContent(Long eventContent) {
    this.eventContent = eventContent;
  }
}
