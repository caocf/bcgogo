package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 13-9-2
 * Time: 上午10:17
 * To change this template use File | Settings | File Templates.
 */
public class OrderItemPromotionDTO {

  public Long getPromotionOrderRecordId() {
    return promotionOrderRecordId;
  }

  public void setPromotionOrderRecordId(Long promotionOrderRecordId) {
    this.promotionOrderRecordId = promotionOrderRecordId;
  }

  public Long getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Long orderItemId) {
    this.orderItemId = orderItemId;
  }

  public OrderTypes getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(OrderTypes orderTypes) {
    this.orderTypes = orderTypes;
  }

  private Long promotionOrderRecordId;
  private Long orderItemId;
  private OrderTypes orderTypes;


  @Override
  public String toString() {
    return "OrderItemPromotionDTO{" +
        "promotionOrderRecordId=" + promotionOrderRecordId +
        ", orderItemId=" + orderItemId +
        ", orderTypes=" + orderTypes +
        '}';
  }

}

