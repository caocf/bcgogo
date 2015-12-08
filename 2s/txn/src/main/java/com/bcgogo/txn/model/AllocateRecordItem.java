package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.AllocateRecordItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "allocate_record_item")
public class AllocateRecordItem extends LongIdentifier {
  private Long allocateRecordId;
  private Long productId;
  private Long productHistoryId;
  private Double amount;
  private Double costPrice;
  private Double totalCostPrice;
  private String unit;
  private String inStorageBin;

  @Column(name = "allocate_record_id")
  public Long getAllocateRecordId() {
    return allocateRecordId;
  }

  public void setAllocateRecordId(Long allocateRecordId) {
    this.allocateRecordId = allocateRecordId;
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

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public AllocateRecordItemDTO toDTO(){
    AllocateRecordItemDTO allocateRecordItemDTO = new AllocateRecordItemDTO();
    allocateRecordItemDTO.setProductId(this.getProductId());
    allocateRecordItemDTO.setProductHistoryId(this.getProductHistoryId());
    allocateRecordItemDTO.setAllocateRecordId(this.getAllocateRecordId());
    allocateRecordItemDTO.setAmount(this.getAmount());
    allocateRecordItemDTO.setId(this.getId());
    allocateRecordItemDTO.setCostPrice(this.getCostPrice());
    allocateRecordItemDTO.setTotalCostPrice(this.getTotalCostPrice());
    allocateRecordItemDTO.setUnit(this.getUnit());
    allocateRecordItemDTO.setInStorageBin(this.getInStorageBin());
    return allocateRecordItemDTO;
  }

  public AllocateRecordItem fromDTO(AllocateRecordItemDTO allocateRecordItemDTO){
    this.setAllocateRecordId(allocateRecordItemDTO.getAllocateRecordId());
    this.setProductId(allocateRecordItemDTO.getProductId());
    this.setProductHistoryId(allocateRecordItemDTO.getProductHistoryId());
    this.setAmount(allocateRecordItemDTO.getAmount());
    this.setCostPrice(allocateRecordItemDTO.getCostPrice());
    this.setTotalCostPrice(allocateRecordItemDTO.getTotalCostPrice());
    this.setUnit(allocateRecordItemDTO.getUnit());
    this.setInStorageBin(allocateRecordItemDTO.getInStorageBin());
    return this;
  }

  @Column(name = "in_storage_bin")
  public String getInStorageBin() {
    return inStorageBin;
  }

  public void setInStorageBin(String inStorageBin) {
    this.inStorageBin = inStorageBin;
  }
}
