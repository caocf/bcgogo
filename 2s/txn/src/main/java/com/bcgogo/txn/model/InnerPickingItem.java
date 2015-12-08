package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InnerPickingItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-28
 * Time: 上午10:18
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inner_picking_item")
public class InnerPickingItem extends LongIdentifier {

  private Long innerPickingId;
  private Long productId;
  private Double amount;
  private Double price;   //库存均价
  private Double total;
  private String unit;
  private Long productHistoryId;

  public InnerPickingItemDTO toDTO(){
    InnerPickingItemDTO innerPickingItemDTO = new InnerPickingItemDTO();
    innerPickingItemDTO.setInnerPickingId(this.getInnerPickingId());
    innerPickingItemDTO.setProductId(this.getProductId());
    innerPickingItemDTO.setPrice(this.getPrice());
    innerPickingItemDTO.setAmount(this.getAmount());
    innerPickingItemDTO.setPrice(this.getPrice());
    innerPickingItemDTO.setTotal(NumberUtil.round(this.getTotal(), 2));
    innerPickingItemDTO.setUnit(this.getUnit());
    innerPickingItemDTO.setId(this.getId());
    innerPickingItemDTO.setProductHistoryId(this.getProductHistoryId());
    return innerPickingItemDTO;
  }

  public void fromDTO(InnerPickingItemDTO innerPickingItemDTO){
    if (innerPickingItemDTO == null){
      return;
    }
    this.setInnerPickingId(innerPickingItemDTO.getInnerPickingId());
    this.setProductId(innerPickingItemDTO.getProductId());
    this.setPrice(innerPickingItemDTO.getPrice());
    this.setAmount(innerPickingItemDTO.getAmount());
    this.setPrice(innerPickingItemDTO.getPrice());
    this.setTotal(innerPickingItemDTO.getTotal());
    this.setUnit(innerPickingItemDTO.getUnit());
    this.setId(innerPickingItemDTO.getId());
    this.setProductHistoryId(innerPickingItemDTO.getProductHistoryId());
  }

  @Column(name = "inner_picking_id")
  public Long getInnerPickingId() {
    return innerPickingId;
  }

  public void setInnerPickingId(Long innerPickingId) {
    this.innerPickingId = innerPickingId;
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

  public void setUnit(String unit) {
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
