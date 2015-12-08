package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.BorrowOrderItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 上午8:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "borrow_order_item")
public class BorrowOrderItem extends LongIdentifier{
  private Long shopId;
  private Long orderId;
  private Long productId;
  private Double amount;
  private Double returnAmount; //归还总数

  private Double price;   //成本均价
  private Double total;   //借调总数
  private String unit;
  private Long productHistoryId;

  public BorrowOrderItemDTO toDTO(){
    BorrowOrderItemDTO itemDTO = new BorrowOrderItemDTO();
    itemDTO.setId(this.getId());
    itemDTO.setOrderId(this.getOrderId());
    itemDTO.setShopId(this.getShopId());
    itemDTO.setProductId(this.getProductId());
    itemDTO.setPrice(this.getPrice());
    itemDTO.setAmount(this.getAmount());
    itemDTO.setReturnAmount(this.getReturnAmount());
    itemDTO.setPrice(this.getPrice());
    itemDTO.setTotal(NumberUtil.round(this.getAmount()*this.getPrice(), 2));
    itemDTO.setUnit(this.getUnit());
    itemDTO.setId(this.getId());
    itemDTO.setProductHistoryId(this.getProductHistoryId());
    itemDTO.setUnReturnAmount(NumberUtil.doubleVal(this.getAmount())-NumberUtil.doubleVal(this.getReturnAmount()));
    return itemDTO;
  }

  public void fromDTO(BorrowOrderItemDTO itemDTO){
    if (itemDTO == null){
      return;
    }
    this.setShopId(itemDTO.getShopId());
    this.setOrderId(itemDTO.getOrderId());
    this.setProductId(itemDTO.getProductId());
    this.setPrice(itemDTO.getPrice());
    this.setAmount(itemDTO.getAmount());
    this.setPrice(itemDTO.getPrice());
    this.setTotal(itemDTO.getTotal());
    this.setUnit(itemDTO.getUnit());
    this.setId(itemDTO.getId());
    this.setProductHistoryId(itemDTO.getProductHistoryId());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }


  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

   @Column(name = "return_amount")
  public Double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(Double returnAmount) {
    this.returnAmount = returnAmount;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit){
    this.unit = unit;
  }

  @Column(name = "product_history_id")
  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }


}
