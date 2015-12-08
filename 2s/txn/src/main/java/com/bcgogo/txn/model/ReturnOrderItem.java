package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.ReturnOrderItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-8
 * Time: 上午6:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "return_order_item")
public class ReturnOrderItem extends LongIdentifier {

  private Long orderId;
  private Long productId;
  private Double borrowAmount;
  private Double returnAmount;
  private String returner;
  private String unit;        //归还的单位
  private String borrowUnit; //借调的单位
  private Long productHistoryId;

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


  @Column(name = "product_history_id")
  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }

  @Column(name = "borrow_amount")
  public Double getBorrowAmount() {
    return borrowAmount;
  }

  public void setBorrowAmount(Double borrowAmount) {
    this.borrowAmount = borrowAmount;
  }

  @Column(name = "return_amount")
  public Double getReturnAmount() {
    return returnAmount;
  }

  public void setReturnAmount(Double returnAmount) {
    this.returnAmount = returnAmount;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

    @Column(name = "borrow_unit")
  public String getBorrowUnit() {
    return borrowUnit;
  }

  public void setBorrowUnit(String borrowUnit) {
    this.borrowUnit = borrowUnit;
  }

  @Column(name = "returner")
  public String getReturner() {
    return returner;
  }

  public void setReturner(String returner) {
    this.returner = returner;
  }

  public ReturnOrderItemDTO toDTO(){
    ReturnOrderItemDTO itemDTO = new ReturnOrderItemDTO();
    itemDTO.setOrderId(this.getOrderId());
    itemDTO.setProductId(this.getProductId());
    itemDTO.setProductHistoryId(this.getProductHistoryId());
    itemDTO.setBorrowAmount(this.getBorrowAmount());
    itemDTO.setReturnAmount(this.getReturnAmount());
    itemDTO.setReturner(this.getReturner());
    itemDTO.setUnit(this.getUnit());
    itemDTO.setBorrowUnit(this.getBorrowUnit());
    itemDTO.setId(this.getId());
    return itemDTO;
  }

  public void fromDTO(ReturnOrderItemDTO itemDTO){
    if (itemDTO == null){
      return;
    }
    this.setOrderId(itemDTO.getOrderId());
    this.setProductId(itemDTO.getProductId());
    this.setBorrowAmount(itemDTO.getBorrowAmount());
    this.setReturnAmount(itemDTO.getReturnAmount());
    this.setReturner(itemDTO.getReturner());
    this.setUnit(itemDTO.getUnit());
    this.setBorrowUnit(itemDTO.getBorrowUnit());
    this.setProductHistoryId(itemDTO.getProductHistoryId());
  }
}
