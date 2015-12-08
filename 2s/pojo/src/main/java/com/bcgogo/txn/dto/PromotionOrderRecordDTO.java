package com.bcgogo.txn.dto;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PromotionsEnum;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-13
 * Time: 下午3:49
 * To change this template use File | Settings | File Templates.
 */
public class PromotionOrderRecordDTO {
  private Long id;
  private String idStr;
  private Long customerShopId;
  private Long supplierShopId;
  private Long orderId;
  private Long itemId;
  private Long productId;
  private Long promotionsId;
  private String promotionsJson;
  private OrderTypes orderType;
  private OrderStatus orderStatus;
  private Double amount;
  private Double newPrice;
  private PromotionsEnum.PromotionsTypes promotionsType;
  private DeletedType deleted = DeletedType.FALSE;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }

  public String getPromotionsJson() {
    return promotionsJson;
  }

  public void setPromotionsJson(String promotionsJson) {
    this.promotionsJson = promotionsJson;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public Double getNewPrice() {
    return newPrice;
  }

  public void setNewPrice(Double newPrice) {
    this.newPrice = newPrice;
  }

  public PromotionsEnum.PromotionsTypes getPromotionsType() {
    return promotionsType;
  }

  public void setPromotionsType(PromotionsEnum.PromotionsTypes promotionsType) {
    this.promotionsType = promotionsType;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
