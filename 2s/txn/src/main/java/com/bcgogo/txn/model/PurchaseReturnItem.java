package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PurchaseReturnItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-9
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "purchase_return_item")
public class PurchaseReturnItem extends LongIdentifier {
  public PurchaseReturnItem(){
  }

  @Column(name = "purchase_return_id")
  public Long getPurchaseReturnId() {
    return purchaseReturnId;
  }

  public void setPurchaseReturnId(Long purchaseReturnId) {
    this.purchaseReturnId = purchaseReturnId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name="product_history_id")
  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "price")
  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  @Column(name = "total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }
  @Column(name = "reserved")
  public Double getReserved() {
    return reserved;
  }

  public void setReserved(Double reserved) {
    this.reserved = reserved;
  }
  private Long purchaseReturnId;
  private Long productId;
  private Long productHistoryId;
  private double amount;
  private double price;
  private double total;
  private String memo;
  private String unit;
  private Double reserved;

  public PurchaseReturnItemDTO toDTO() {
    PurchaseReturnItemDTO purchaseReturnItemDTO = new PurchaseReturnItemDTO();
    purchaseReturnItemDTO.setId(this.getId());
    purchaseReturnItemDTO.setPurchaseReturnId(this.getPurchaseReturnId());
    purchaseReturnItemDTO.setProductId(this.getProductId());
    purchaseReturnItemDTO.setProductHistoryId(getProductHistoryId());
    purchaseReturnItemDTO.setAmount(this.amount);
    purchaseReturnItemDTO.setPrice(this.getPrice());
    purchaseReturnItemDTO.setTotal(this.getTotal());
    purchaseReturnItemDTO.setMemo(this.getMemo());
    purchaseReturnItemDTO.setUnit(this.getUnit());
    purchaseReturnItemDTO.setReserved(this.getReserved());
    return purchaseReturnItemDTO;

  }
}
