package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SalesReturnItemDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-13
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "sales_return_item")
public class SalesReturnItem extends LongIdentifier {
  public SalesReturnItem(){
  }

  @Column(name = "sales_return_id")
  public Long getSalesReturnId() {
    return salesReturnId;
  }

  public void setSalesReturnId(Long salesReturnId) {
    this.salesReturnId = salesReturnId;
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

  @Column(name = "memo", length = 200)
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

  @Column(name="customer_order_item_id")
  public Long getCustomerOrderItemId() {
    return customerOrderItemId;
  }

  public void setCustomerOrderItemId(Long customerOrderItemId) {
    this.customerOrderItemId = customerOrderItemId;
  }

  @Column(name="cost_price")
  public Double getCostPrice() {
    return costPrice;
  }

  public void setCostPrice(Double costPrice) {
    this.costPrice = costPrice;
  }

  @Column(name="total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
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

  private Long salesReturnId;
  private Long productId;
  private Long productHistoryId;
  private Long customerOrderItemId;
  private Double amount;
  private Double price;
  private Double total;
  private String memo;
  private String unit;
  private Double costPrice;
  private Double totalCostPrice;
  private Long businessCategoryId;
  private String businessCategoryName;
  public SalesReturnItemDTO toDTO() {
    SalesReturnItemDTO salesReturnItemDTO = new SalesReturnItemDTO();
    salesReturnItemDTO.setId(this.getId());
    salesReturnItemDTO.setSalesReturnId(this.getSalesReturnId());
    salesReturnItemDTO.setProductId(this.getProductId());
    salesReturnItemDTO.setProductHistoryId(this.getProductHistoryId());
    salesReturnItemDTO.setAmount(this.getAmount());
    salesReturnItemDTO.setPrice(this.getPrice());
    salesReturnItemDTO.setTotal(this.getTotal());
    salesReturnItemDTO.setMemo(this.getMemo());
    salesReturnItemDTO.setUnit(this.getUnit());
    salesReturnItemDTO.setCustomerOrderItemId(this.getCustomerOrderItemId());
    salesReturnItemDTO.setCostPrice(getCostPrice());
    salesReturnItemDTO.setTotalCostPrice(getTotalCostPrice());
    salesReturnItemDTO.setBusinessCategoryId(getBusinessCategoryId());
    salesReturnItemDTO.setBusinessCategoryName(getBusinessCategoryName());
    return salesReturnItemDTO;

  }

  public SalesReturnItem fromDTO(SalesReturnItemDTO salesReturnItemDTO){
    if(salesReturnItemDTO == null)
      return this;
    setId(salesReturnItemDTO.getId());
    this.salesReturnId = salesReturnItemDTO.getSalesReturnId();
    this.productId = salesReturnItemDTO.getProductId();
    this.productHistoryId = salesReturnItemDTO.getProductHistoryId();
    this.amount = salesReturnItemDTO.getAmount();
    this.price = salesReturnItemDTO.getPrice();
    this.total = salesReturnItemDTO.getTotal();
    this.memo = salesReturnItemDTO.getMemo();
    this.unit = salesReturnItemDTO.getUnit();
    this.customerOrderItemId = salesReturnItemDTO.getCustomerOrderItemId();
    this.costPrice = salesReturnItemDTO.getCostPrice();
    this.totalCostPrice = salesReturnItemDTO.getTotalCostPrice();
    this.businessCategoryId = salesReturnItemDTO.getBusinessCategoryId();
    this.businessCategoryName = salesReturnItemDTO.getBusinessCategoryName();
    return this;
  }
}