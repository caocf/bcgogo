package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairOrderItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-14
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "repair_order_item")
public class RepairOrderItem extends LongIdentifier {
  public RepairOrderItem(){
  }

  public RepairOrderItem fromDTO(RepairOrderItemDTO repairOrderItemDTO) {
    if(repairOrderItemDTO == null)
      return this;
    this.setId(repairOrderItemDTO.getId());
    this.setShopId(repairOrderItemDTO.getShopId());

    this.setRepairOrderId(repairOrderItemDTO.getRepairOrderId());
    this.setProductId(repairOrderItemDTO.getProductId());
    this.setProductHistoryId(repairOrderItemDTO.getProductHistoryId());
    this.setAmount(repairOrderItemDTO.getAmount());

    this.setReserved(repairOrderItemDTO.getReserved());
    this.setPrice(repairOrderItemDTO.getPrice());
    this.setTotal(repairOrderItemDTO.getTotal());
    this.setMemo(repairOrderItemDTO.getMemo());
    this.setUnit(repairOrderItemDTO.getUnit());
    this.setCostPrice(repairOrderItemDTO.getCostPrice());
    this.setTotalCostPrice(repairOrderItemDTO.getTotalCostPrice());
    this.setBusinessCategoryId(repairOrderItemDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName());
    return this;
  }

  @Column(name = "repair_order_id")
  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
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

  @Column(name = "memo", length = 100)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Column(name = "reserved")
  public Double getReserved() {
    if(reserved == null) return 0d;
    return reserved;
  }

  public void setReserved(Double reserved) {
    this.reserved = reserved;
  }

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit){
    this.unit = unit;
  }

  private Long repairOrderId;
  private Long productId;
  private Long productHistoryId;
  private double amount;
  private Double reserved;
  private double price;
  private double total;
  private String memo;
  private Long shopId;
  private String unit;
  private Double costPrice;
  private Double totalCostPrice;
  private Double percentage;
  private Double percentageAmount;
  private Long businessCategoryId;
  private String businessCategoryName;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }
  @Column(name = "cost_price")
  public Double getCostPrice() {
    return costPrice;
  }
  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }
  @Column(name="percentage")
  public Double getPercentage() {
    return percentage;
  }
  @Column(name="percentage_amount")
  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public RepairOrderItemDTO toDTO() {
    RepairOrderItemDTO repairOrderItemDTO = new RepairOrderItemDTO();

    repairOrderItemDTO.setId(this.getId());
    repairOrderItemDTO.setShopId(this.getShopId());

    repairOrderItemDTO.setRepairOrderId(this.getRepairOrderId());
    repairOrderItemDTO.setProductId(this.getProductId());
    repairOrderItemDTO.setProductHistoryId(getProductHistoryId());
    repairOrderItemDTO.setAmount(this.getAmount());

    repairOrderItemDTO.setReserved(this.getReserved());
    repairOrderItemDTO.setPrice(this.getPrice());
    repairOrderItemDTO.setTotal(this.getTotal());
    repairOrderItemDTO.setMemo(this.getMemo());
    repairOrderItemDTO.setUnit(this.getUnit());
    repairOrderItemDTO.setCostPrice(this.getCostPrice());
    repairOrderItemDTO.setTotalCostPrice(this.getTotalCostPrice());
    repairOrderItemDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    repairOrderItemDTO.setBusinessCategoryName(this.getBusinessCategoryName());

    return repairOrderItemDTO;
  }

}