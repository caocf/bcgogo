package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-20
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
public class ReceiptNoDTO {

  private Long id;
  private Long shopId;
  private Long orderId;
  private OrderTypes types;
  private String receiptNo;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public OrderTypes getTypes() {
    return types;
  }

  public void setTypes(OrderTypes types) {
    this.types = types;
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
