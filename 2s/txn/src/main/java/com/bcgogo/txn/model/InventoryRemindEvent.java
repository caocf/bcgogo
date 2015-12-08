package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InventoryRemindEventDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-11-16
 * Time: 下午2:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "inventory_remind_event")
public class InventoryRemindEvent extends LongIdentifier {
  public InventoryRemindEvent() {
  }

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

  @Column(name = "deliver_time")
  public Long getDeliverTime() {
    return deliverTime;
  }

  public void setDeliverTime(Long deliverTime) {
    this.deliverTime = deliverTime;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Column(name = "supplier")
  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "product_brand")
  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  @Column(name = "product_spec")
  public String getProductSpec() {
    return productSpec;
  }

  public void setProductSpec(String productSpec) {
    this.productSpec = productSpec;
  }

  @Column(name = "product_model")
  public String getProductModel() {
    return productModel;
  }

  public void setProductModel(String productModel) {
    this.productModel = productModel;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "purchase_order_id")
  public Long getPurchaseOrderId() {
    return purchaseOrderId;
  }

  public void setPurchaseOrderId(Long purchaseOrderId) {
    this.purchaseOrderId = purchaseOrderId;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public InventoryRemindEventDTO toDTO() {
    InventoryRemindEventDTO inventoryRemindEventDTO = new InventoryRemindEventDTO();
    inventoryRemindEventDTO.setId(getId());
    inventoryRemindEventDTO.setShopId(getShopId());
    inventoryRemindEventDTO.setPurchaseOrderId(getPurchaseOrderId());
    inventoryRemindEventDTO.setContent(getContent());
    inventoryRemindEventDTO.setSupplier(getSupplier());
    inventoryRemindEventDTO.setProductName(getProductName());
    inventoryRemindEventDTO.setProductBrand(getProductBrand());
    inventoryRemindEventDTO.setProductSpec(getProductSpec());
    inventoryRemindEventDTO.setProductModel(getProductModel());
    inventoryRemindEventDTO.setPrice(getPrice());
    inventoryRemindEventDTO.setAmount(getAmount());
    inventoryRemindEventDTO.setDeliverTime(getDeliverTime());
    inventoryRemindEventDTO.setUnit(getUnit());
    return inventoryRemindEventDTO;
  }

  public InventoryRemindEvent fromDTO(InventoryRemindEventDTO inventoryRemindEventDTO){
    if(inventoryRemindEventDTO == null)
      return this;
    setId(inventoryRemindEventDTO.getId());
    this.shopId = inventoryRemindEventDTO.getShopId();
    this.purchaseOrderId = inventoryRemindEventDTO.getPurchaseOrderId();
    this.content = inventoryRemindEventDTO.getContent();
    this.supplier = inventoryRemindEventDTO.getSupplier();
    this.productName = inventoryRemindEventDTO.getProductName();
    this.productBrand = inventoryRemindEventDTO.getProductBrand();
    this.productSpec = inventoryRemindEventDTO.getProductSpec();
    this.productModel = inventoryRemindEventDTO.getProductModel();
    this.price = inventoryRemindEventDTO.getPrice();
    this.amount = inventoryRemindEventDTO.getAmount();
    this.deliverTime = inventoryRemindEventDTO.getDeliverTime();
    this.unit = inventoryRemindEventDTO.getUnit();
    return this;
  }
}
