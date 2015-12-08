package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.OrderStatusChangeLogDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "order_status_change_log")
public class OrderStatusChangeLog extends LongIdentifier {
  private Long shopId;
  private Long userId;
  private OrderStatus curOrderStatus;
  private OrderStatus preOrderStatus;
  private Long orderId;
  private OrderTypes orderType;

  public OrderStatusChangeLog() {
  }

  public OrderStatusChangeLog(OrderStatusChangeLogDTO orderStatusChangeLogDTO) {
    this.setCurOrderStatus(orderStatusChangeLogDTO.getCurOrderStatus());
    this.setOrderId(orderStatusChangeLogDTO.getOrderId());
    this.setOrderType(orderStatusChangeLogDTO.getOrderType());
    this.setPreOrderStatus(orderStatusChangeLogDTO.getPreOrderStatus());
    this.setShopId(orderStatusChangeLogDTO.getShopId());
    this.setUserId(orderStatusChangeLogDTO.getUserId());
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name="order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "pre_order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getPreOrderStatus() {
    return preOrderStatus;
  }

  public void setPreOrderStatus(OrderStatus preOrderStatus) {
    this.preOrderStatus = preOrderStatus;
  }

  @Column(name = "cur_order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getCurOrderStatus() {
    return curOrderStatus;
  }

  public void setCurOrderStatus(OrderStatus curOrderStatus) {
    this.curOrderStatus = curOrderStatus;
  }
}
