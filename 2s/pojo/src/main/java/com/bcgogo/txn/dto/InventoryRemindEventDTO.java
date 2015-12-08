package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-16
 * Time: 下午2:19
 * To change this template use File | Settings | File Templates.
 */
public class InventoryRemindEventDTO implements Serializable {
  public InventoryRemindEventDTO() {
  }

  private Long id;
  private Long shopId;
  private Long purchaseOrderId;
  private String content;

  private String supplier;
  private String productName;
  private String productBrand;
  private String productSpec;
  private String productModel;
  private Double price;
  private Double amount;
  private Long deliverTime;
  private String unit;
  private Long createdTime;

  public Long getDeliverTime() {
    return deliverTime;
  }

  public void setDeliverTime(Long deliverTime) {
    this.deliverTime = deliverTime;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  private PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO();

  public PurchaseOrderDTO getPurchaseOrderDTO() {
    return purchaseOrderDTO;
  }

  public void setPurchaseOrderDTO(PurchaseOrderDTO purchaseOrderDTO) {
    this.purchaseOrderDTO = purchaseOrderDTO;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUnit(){
    return unit;
  }

  public void setUnit(String unit){
    this.unit = unit;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }
}
