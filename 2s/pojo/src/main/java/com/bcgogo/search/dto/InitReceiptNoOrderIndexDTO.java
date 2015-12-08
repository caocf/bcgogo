package com.bcgogo.search.dto;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-18
 * Time: 下午8:50
 * To change this template use File | Settings | File Templates.
 */
public class InitReceiptNoOrderIndexDTO {
  private Long orderId;
  private String receiptNo;
  private Long shopId;
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
