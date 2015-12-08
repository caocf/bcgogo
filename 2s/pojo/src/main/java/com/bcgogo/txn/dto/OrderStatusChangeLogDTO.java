package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
public class OrderStatusChangeLogDTO {
  private Long id;
  private Long shopId;
  private Long creationDate;
  private Long userId;
  private OrderStatus curOrderStatus;
  private OrderStatus preOrderStatus;
  private Long orderId;
  private OrderTypes orderType;

  public OrderStatusChangeLogDTO() {
  }

  public OrderStatusChangeLogDTO(Long shopId, Long userId, OrderStatus curOrderStatus, OrderStatus preOrderStatus, Long orderId, OrderTypes orderType) {
    this.shopId = shopId;
    this.userId = userId;
    this.curOrderStatus = curOrderStatus;
    this.preOrderStatus = preOrderStatus;
    this.orderId = orderId;
    this.orderType = orderType;
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

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public OrderStatus getCurOrderStatus() {
    return curOrderStatus;
  }

  public void setCurOrderStatus(OrderStatus curOrderStatus) {
    this.curOrderStatus = curOrderStatus;
  }

  public OrderStatus getPreOrderStatus() {
    return preOrderStatus;
  }

  public void setPreOrderStatus(OrderStatus preOrderStatus) {
    this.preOrderStatus = preOrderStatus;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }
}
