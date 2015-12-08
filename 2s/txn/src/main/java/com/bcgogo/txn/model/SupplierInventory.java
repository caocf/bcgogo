package com.bcgogo.txn.model;

import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.SupplierInventoryDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午11:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "supplier_inventory")
public class SupplierInventory extends LongIdentifier {
  private Long productId;
  private Long supplierId;
  private Long shopId;
  private Long storehouseId;
  private Double totalInStorageAmount;
  private Double remainAmount;
  private String unit;
  private Double maxStoragePrice;
  private Double minStoragePrice;
  private Double averageStoragePrice;
  private Long lastStorageTime;
  private Double lastStoragePrice;
  private OutStorageSupplierType supplierType;
  private String supplierName;
  private String supplierContact;
  private String supplierMobile;
  private Long lastPurchaseInventoryOrderId;
  private Double lastStorageAmount;
  private YesNo disabled;//供应商删除,产品删除，仓库删除时disabled为YES


  public void fromDTO(SupplierInventoryDTO supplierInventoryDTO) {
    if (supplierInventoryDTO != null) {
      this.setId(supplierInventoryDTO.getId());
      this.setProductId(supplierInventoryDTO.getProductId());
      this.setSupplierId(supplierInventoryDTO.getSupplierId());
      this.setShopId(supplierInventoryDTO.getShopId());
      this.setStorehouseId(supplierInventoryDTO.getStorehouseId());
      this.setTotalInStorageAmount(supplierInventoryDTO.getTotalInStorageAmount());
      this.setRemainAmount(supplierInventoryDTO.getRemainAmount());
      this.setUnit(supplierInventoryDTO.getUnit());
      this.setMaxStoragePrice(supplierInventoryDTO.getMaxStoragePrice());
      this.setMinStoragePrice(supplierInventoryDTO.getMinStoragePrice());
      this.setAverageStoragePrice(supplierInventoryDTO.getAverageStoragePrice());
      this.setLastStoragePrice(supplierInventoryDTO.getLastStoragePrice());
      this.setLastStorageTime(supplierInventoryDTO.getLastStorageTime());
      this.setSupplierType(supplierInventoryDTO.getSupplierType());
      this.setSupplierName(supplierInventoryDTO.getSupplierName());
      this.setSupplierContact(supplierInventoryDTO.getSupplierContact());
      this.setSupplierMobile(supplierInventoryDTO.getSupplierMobile());
      this.setLastPurchaseInventoryOrderId(supplierInventoryDTO.getLastPurchaseInventoryOrderId());
      this.setLastStorageAmount(supplierInventoryDTO.getLastStorageAmount());
      this.setDisabled(supplierInventoryDTO.getDisabled());
    }
  }

  public SupplierInventoryDTO toDTO() {
    SupplierInventoryDTO supplierInventoryDTO = new SupplierInventoryDTO();
    supplierInventoryDTO.setId(this.getId());
    supplierInventoryDTO.setProductId(this.getProductId());
    supplierInventoryDTO.setSupplierId(this.getSupplierId());
    supplierInventoryDTO.setShopId(this.getShopId());
    supplierInventoryDTO.setStorehouseId(this.getStorehouseId());
    supplierInventoryDTO.setTotalInStorageAmount(this.getTotalInStorageAmount());
    supplierInventoryDTO.setRemainAmount(this.getRemainAmount());
    supplierInventoryDTO.setUnit(this.getUnit());
    supplierInventoryDTO.setMaxStoragePrice(this.getMaxStoragePrice());
    supplierInventoryDTO.setMinStoragePrice(this.getMinStoragePrice());
    supplierInventoryDTO.setAverageStoragePrice(this.getAverageStoragePrice());
    supplierInventoryDTO.setLastStoragePrice(this.getLastStoragePrice());
    supplierInventoryDTO.setLastStorageTime(this.getLastStorageTime());
    supplierInventoryDTO.setSupplierType(this.getSupplierType());
    supplierInventoryDTO.setSupplierName(this.getSupplierName());
    supplierInventoryDTO.setSupplierContact(this.getSupplierContact());
    supplierInventoryDTO.setSupplierMobile(this.getSupplierMobile());
    supplierInventoryDTO.setLastPurchaseInventoryOrderId(this.getLastPurchaseInventoryOrderId());
    supplierInventoryDTO.setLastStorageAmount(this.getLastStorageAmount());
    supplierInventoryDTO.setDisabled(this.getDisabled());
    return supplierInventoryDTO;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  @Column(name = "total_in_storage_amount")
  public Double getTotalInStorageAmount() {
    return totalInStorageAmount;
  }

  public void setTotalInStorageAmount(Double totalInStorageAmount) {
    this.totalInStorageAmount = totalInStorageAmount;
  }

  @Column(name = "remain_amount")
  public Double getRemainAmount() {
    return remainAmount;
  }

  public void setRemainAmount(Double remainAmount) {
    this.remainAmount = remainAmount;
  }

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "max_storage_price")
  public Double getMaxStoragePrice() {
    return maxStoragePrice;
  }

  public void setMaxStoragePrice(Double maxStoragePrice) {
    this.maxStoragePrice = maxStoragePrice;
  }

  @Column(name = "min_storage_price")
  public Double getMinStoragePrice() {
    return minStoragePrice;
  }

  public void setMinStoragePrice(Double minStoragePrice) {
    this.minStoragePrice = minStoragePrice;
  }

  @Column(name = "average_storage_price")
  public Double getAverageStoragePrice() {
    return averageStoragePrice;
  }

  public void setAverageStoragePrice(Double averageStoragePrice) {
    this.averageStoragePrice = averageStoragePrice;
  }

  @Column(name = "last_storage_time")
  public Long getLastStorageTime() {
    return lastStorageTime;
  }

  public void setLastStorageTime(Long lastStorageTime) {
    this.lastStorageTime = lastStorageTime;
  }

  @Column(name = "last_storage_price")
  public Double getLastStoragePrice() {
    return lastStoragePrice;
  }

  public void setLastStoragePrice(Double lastStoragePrice) {
    this.lastStoragePrice = lastStoragePrice;
  }

  @Column(name = "supplier_type")
  @Enumerated(EnumType.STRING)
  public OutStorageSupplierType getSupplierType() {
    return supplierType;
  }

  public void setSupplierType(OutStorageSupplierType supplierType) {
    this.supplierType = supplierType;
  }

  @Column(name = "supplier_name")
  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  @Column(name = "supplier_contact")
  public String getSupplierContact() {
    return supplierContact;
  }

  public void setSupplierContact(String supplierContact) {
    this.supplierContact = supplierContact;
  }

  @Column(name = "supplier_mobile")
  public String getSupplierMobile() {
    return supplierMobile;
  }

  public void setSupplierMobile(String supplierMobile) {
    this.supplierMobile = supplierMobile;
  }

  @Column(name = "last_purchase_inventory_order_id")
  public Long getLastPurchaseInventoryOrderId() {
    return lastPurchaseInventoryOrderId;
  }

  public void setLastPurchaseInventoryOrderId(Long lastPurchaseInventoryOrderId) {
    this.lastPurchaseInventoryOrderId = lastPurchaseInventoryOrderId;
  }

  @Column(name = "last_storage_amount")
  public Double getLastStorageAmount() {
    return lastStorageAmount;
  }

  public void setLastStorageAmount(Double lastStorageAmount) {
    this.lastStorageAmount = lastStorageAmount;
  }

  @Column(name = "is_disabled")
  @Enumerated(EnumType.STRING)
  public YesNo getDisabled() {
    return disabled;
  }

  public void setDisabled(YesNo disabled) {
    this.disabled = disabled;
  }
}
