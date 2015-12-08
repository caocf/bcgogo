package com.bcgogo.product.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PromotionOrderRecordDTO;

import javax.persistence.*;

/**
 * 单据参加促销的记录。记录都是符合条件的单据
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-3
 * Time: 下午12:52
 */
@Entity
@Table(name = "promotion_order_record")
public class PromotionOrderRecord extends LongIdentifier {
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
  private PromotionsEnum.PromotionsTypes promotionsType;
  private DeletedType deleted = DeletedType.FALSE;

  //  private Double promotionsPrice;
  //  private String promotionsInfo;
  //  private Double minAmount;
//  private Double discountAmount;
//  private PromotionsEnum.GiftType giftType;  //满就送特有的字段
//  private PromotionsEnum.PostType postType= PromotionsEnum.PostType.UN_POST;  //包邮促销特有的字段.默认不包邮

  @Column(name = "customer_shop_id")
  public Long getCustomerShopId() {
    return customerShopId;
  }

  public void setCustomerShopId(Long customerShopId) {
    this.customerShopId = customerShopId;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "supplier_shop_id")
  public Long getSupplierShopId() {
    return supplierShopId;
  }

  public void setSupplierShopId(Long supplierShopId) {
    this.supplierShopId = supplierShopId;
  }

  @Column(name = "item_id")
  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "promotions_json")
  public String getPromotionsJson() {
    return promotionsJson;
  }

  public void setPromotionsJson(String promotionsJson) {
    this.promotionsJson = promotionsJson;
  }

  @Column(name = "order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  @Column(name = "order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Column(name = "promotions_id")
  public Long getPromotionsId() {
    return promotionsId;
  }

  public void setPromotionsId(Long promotionsId) {
    this.promotionsId = promotionsId;
  }


  @Column(name = "promotions_type")
  @Enumerated(EnumType.STRING)
  public PromotionsEnum.PromotionsTypes getPromotionsType() {
    return promotionsType;
  }

  public void setPromotionsType(PromotionsEnum.PromotionsTypes promotionsType) {
    this.promotionsType = promotionsType;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public PromotionOrderRecord fromDTO(PromotionOrderRecordDTO recordDTO) {
    this.setCustomerShopId(recordDTO.getCustomerShopId());
    this.setSupplierShopId(recordDTO.getSupplierShopId());
    this.setProductId(recordDTO.getProductId());
    this.setItemId(recordDTO.getItemId());
    this.setOrderId(recordDTO.getOrderId());
    this.setOrderType(recordDTO.getOrderType());
    this.setOrderStatus(recordDTO.getOrderStatus());
    this.setAmount(recordDTO.getAmount());
    this.setPromotionsId(recordDTO.getPromotionsId());
    this.setPromotionsJson(recordDTO.getPromotionsJson());
    this.setPromotionsType(recordDTO.getPromotionsType());
    return this;
  }

  public PromotionOrderRecordDTO toDTO(){
    PromotionOrderRecordDTO recordDTO=new PromotionOrderRecordDTO();
    recordDTO.setId(this.getId());
    recordDTO.setCustomerShopId(this.getCustomerShopId());
    recordDTO.setSupplierShopId(this.getSupplierShopId());
    recordDTO.setProductId(this.getProductId());
    recordDTO.setItemId(this.getItemId());
    recordDTO.setOrderId(this.getOrderId());
    recordDTO.setOrderType(this.getOrderType());
    recordDTO.setOrderStatus(this.getOrderStatus());
    recordDTO.setAmount(this.getAmount());
    recordDTO.setPromotionsId(this.getPromotionsId());
    recordDTO.setPromotionsJson(this.getPromotionsJson());
    recordDTO.setPromotionsType(this.getPromotionsType());
    return recordDTO;
  }

}
