package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InnerReturnItemDTO;
import com.bcgogo.utils.NumberUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-4
 * Time: 上午11:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inner_return_item")
public class InnerReturnItem extends LongIdentifier {
  private Long innerReturnId;
  private Long productId;
  private Double amount;
  private Double price;   //库存均价
  private Double total;
  private String unit;
  private Long productHistoryId;

  public InnerReturnItemDTO toDTO() {
    InnerReturnItemDTO innerReturnItemDTO = new InnerReturnItemDTO();
    innerReturnItemDTO.setInnerReturnId(this.getInnerReturnId());
    innerReturnItemDTO.setProductId(this.getProductId());
    innerReturnItemDTO.setPrice(this.getPrice());
    innerReturnItemDTO.setAmount(this.getAmount());
    innerReturnItemDTO.setPrice(this.getPrice());
    innerReturnItemDTO.setTotal(NumberUtil.round(this.getTotal(), 2));
    innerReturnItemDTO.setUnit(this.getUnit());
    innerReturnItemDTO.setId(this.getId());
    innerReturnItemDTO.setProductHistoryId(this.getProductHistoryId());
    return innerReturnItemDTO;
  }

  public void fromDTO(InnerReturnItemDTO innerReturnItemDTO) {
    if (innerReturnItemDTO == null) {
      return;
    }
    this.setInnerReturnId(innerReturnItemDTO.getInnerReturnId());
    this.setProductId(innerReturnItemDTO.getProductId());
    this.setPrice(innerReturnItemDTO.getPrice());
    this.setAmount(innerReturnItemDTO.getAmount());
    this.setPrice(innerReturnItemDTO.getPrice());
    this.setTotal(innerReturnItemDTO.getTotal());
    this.setUnit(innerReturnItemDTO.getUnit());
    this.setId(innerReturnItemDTO.getId());
    this.setProductHistoryId(innerReturnItemDTO.getProductHistoryId());
  }

  @Column(name = "inner_return_id")
  public Long getInnerReturnId() {
    return innerReturnId;
  }

  public void setInnerReturnId(Long innerReturnId) {
    this.innerReturnId = innerReturnId;
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
