package com.bcgogo.txn.model;

import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.enums.txn.preBuyOrder.ShippingMethod;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "quoted_pre_buy_order_item")
public class QuotedPreBuyOrderItem extends LongIdentifier {
  private Long shopId;
  private Long preBuyOrderItemId;
  private Long preBuyOrderId;
  private String includingTax;
  private ShippingMethod shippingMethod;
  private Integer arrivalTime;
  private String memo;
  private QuotedResult quotedResult;

  private Long quotedPreBuyOrderId;
  private Long productId;
  private Double price;
  private String unit;

  @Column(name = "pre_buy_order_item_id")
  public Long getPreBuyOrderItemId() {
    return preBuyOrderItemId;
  }

  public void setPreBuyOrderItemId(Long preBuyOrderItemId) {
    this.preBuyOrderItemId = preBuyOrderItemId;
  }
  @Column(name = "including_tax")
  public String getIncludingTax() {
    return includingTax;
  }

  public void setIncludingTax(String includingTax) {
    this.includingTax = includingTax;
  }
  @Column(name = "shipping_method")
  @Enumerated(EnumType.STRING)
  public ShippingMethod getShippingMethod() {
    return shippingMethod;
  }

  public void setShippingMethod(ShippingMethod shippingMethod) {
    this.shippingMethod = shippingMethod;
  }
  @Column(name = "arrival_time")
  public Integer getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(Integer arrivalTime) {
    this.arrivalTime = arrivalTime;
  }
  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "quoted_result")
  @Enumerated(EnumType.STRING)
  public QuotedResult getQuotedResult() {
    return quotedResult;
  }

  public void setQuotedResult(QuotedResult quotedResult) {
    this.quotedResult = quotedResult;
  }
  @Column(name = "quoted_pre_buy_order_id")
  public Long getQuotedPreBuyOrderId() {
    return quotedPreBuyOrderId;
  }

  public void setQuotedPreBuyOrderId(Long quotedPreBuyOrderId) {
    this.quotedPreBuyOrderId = quotedPreBuyOrderId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


  @Column(name = "pre_buy_order_id")
  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
  }

  public QuotedPreBuyOrderItemDTO toDTO(){
    QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO = new QuotedPreBuyOrderItemDTO();
    quotedPreBuyOrderItemDTO.setProductId(this.getProductId());
    quotedPreBuyOrderItemDTO.setQuotedPreBuyOrderId(this.getQuotedPreBuyOrderId());
    quotedPreBuyOrderItemDTO.setPreBuyOrderItemId(this.getPreBuyOrderItemId());
    quotedPreBuyOrderItemDTO.setPrice(this.getPrice());
    quotedPreBuyOrderItemDTO.setId(this.getId());
    quotedPreBuyOrderItemDTO.setUnit(this.getUnit());

    quotedPreBuyOrderItemDTO.setIncludingTax(this.getIncludingTax());
    quotedPreBuyOrderItemDTO.setShippingMethod(this.getShippingMethod());
    quotedPreBuyOrderItemDTO.setArrivalTime(this.getArrivalTime());
    quotedPreBuyOrderItemDTO.setMemo(this.getMemo());
    quotedPreBuyOrderItemDTO.setQuotedResult(this.getQuotedResult());
    quotedPreBuyOrderItemDTO.setShopId(this.getShopId());
    quotedPreBuyOrderItemDTO.setPreBuyOrderId(this.getPreBuyOrderId());
    return quotedPreBuyOrderItemDTO;
  }

  public QuotedPreBuyOrderItem fromDTO(QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO){
    this.setProductId(quotedPreBuyOrderItemDTO.getProductId());
    this.setQuotedPreBuyOrderId(quotedPreBuyOrderItemDTO.getQuotedPreBuyOrderId());
    this.setPrice(quotedPreBuyOrderItemDTO.getPrice());
    this.setUnit(quotedPreBuyOrderItemDTO.getUnit());

    this.setPreBuyOrderItemId(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());
    this.setIncludingTax(quotedPreBuyOrderItemDTO.getIncludingTax());
    this.setShippingMethod(quotedPreBuyOrderItemDTO.getShippingMethod());
    this.setArrivalTime(quotedPreBuyOrderItemDTO.getArrivalTime());
    this.setMemo(quotedPreBuyOrderItemDTO.getMemo());
    this.setQuotedResult(quotedPreBuyOrderItemDTO.getQuotedResult());
    this.setShopId(quotedPreBuyOrderItemDTO.getShopId());
    this.setPreBuyOrderId(quotedPreBuyOrderItemDTO.getPreBuyOrderId());
    return this;
  }

  public QuotedPreBuyOrderItem(){
  }

  public QuotedPreBuyOrderItem(QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO){
    this.setId(quotedPreBuyOrderItemDTO.getId());
    this.setProductId(quotedPreBuyOrderItemDTO.getProductId());
    this.setQuotedPreBuyOrderId(quotedPreBuyOrderItemDTO.getQuotedPreBuyOrderId());
    this.setPrice(quotedPreBuyOrderItemDTO.getPrice());
    this.setUnit(quotedPreBuyOrderItemDTO.getUnit());
    this.setPreBuyOrderItemId(quotedPreBuyOrderItemDTO.getPreBuyOrderItemId());
    this.setIncludingTax(quotedPreBuyOrderItemDTO.getIncludingTax());
    this.setShippingMethod(quotedPreBuyOrderItemDTO.getShippingMethod());
    this.setArrivalTime(quotedPreBuyOrderItemDTO.getArrivalTime());
    this.setMemo(quotedPreBuyOrderItemDTO.getMemo());
    this.setQuotedResult(quotedPreBuyOrderItemDTO.getQuotedResult());
    this.setShopId(quotedPreBuyOrderItemDTO.getShopId());
    this.setPreBuyOrderId(quotedPreBuyOrderItemDTO.getPreBuyOrderId());
  }


}
