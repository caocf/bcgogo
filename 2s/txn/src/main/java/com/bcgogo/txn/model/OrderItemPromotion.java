package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.OrderItemPromotionDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-30
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "order_item_promotion")
public class OrderItemPromotion extends LongIdentifier {
  private Long promotionOrderRecordId;
  private Long orderItemId;
  private OrderTypes orderTypes;

  @Column(name = "promotion_order_record_id")
  public Long getPromotionOrderRecordId() {
    return promotionOrderRecordId;
  }

  public void setPromotionOrderRecordId(Long promotionOrderRecordId) {
    this.promotionOrderRecordId = promotionOrderRecordId;
  }

  @Column(name = "order_item_id")
  public Long getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Long orderItemId) {
    this.orderItemId = orderItemId;
  }

  @Column(name = "order_types")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(OrderTypes orderTypes) {
    this.orderTypes = orderTypes;
  }

  public OrderItemPromotionDTO toDTO() {
    OrderItemPromotionDTO orderItemPromotionDTO = new OrderItemPromotionDTO();
    orderItemPromotionDTO.setOrderItemId(orderItemId);
    orderItemPromotionDTO.setOrderTypes(orderTypes);
    orderItemPromotionDTO.setPromotionOrderRecordId(promotionOrderRecordId);
    return orderItemPromotionDTO;
  }

}
