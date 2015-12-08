package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.YesNo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class InStorageRecordDTO implements Serializable {
  private Long id;
  private Long shopId;
  private Long inStorageOrderId;
  private OrderTypes inStorageOrderType;
  private OrderStatus inStorageOrderStatus;
  private Long inStorageItemId;
  private Long productId;
  private Double inStorageItemAmount;
  private String inStorageUnit;
  private Long supplierId;
  private String supplierName;
  private Double remainAmount;
  private Long storehouseId;
  private String storehouseName;
  private Double price;//库存增加时的价格

  private Double useRelatedAmount;//被使用的记录
  private Double supplierRelatedAmount;//选定的供应商使用量

  private YesNo disabled;//当入库单作废时 该单据的 disabled为Y
  private OutStorageSupplierType supplierType;//供应商类型

  private Double averageStoragePrice;//供应商平均价 用于调拨单

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
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

  public Long getInStorageOrderId() {
    return inStorageOrderId;
  }

  public void setInStorageOrderId(Long inStorageOrderId) {
    this.inStorageOrderId = inStorageOrderId;
  }

  public OrderTypes getInStorageOrderType() {
    return inStorageOrderType;
  }

  public void setInStorageOrderType(OrderTypes inStorageOrderType) {
    this.inStorageOrderType = inStorageOrderType;
  }

  public OrderStatus getInStorageOrderStatus() {
    return inStorageOrderStatus;
  }

  public void setInStorageOrderStatus(OrderStatus inStorageOrderStatus) {
    this.inStorageOrderStatus = inStorageOrderStatus;
  }

  public Long getInStorageItemId() {
    return inStorageItemId;
  }

  public void setInStorageItemId(Long inStorageItemId) {
    this.inStorageItemId = inStorageItemId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Double getInStorageItemAmount() {
    return inStorageItemAmount;
  }

  public void setInStorageItemAmount(Double inStorageItemAmount) {
    this.inStorageItemAmount = inStorageItemAmount;
  }

  public String getInStorageUnit() {
    return inStorageUnit;
  }

  public void setInStorageUnit(String inStorageUnit) {
    this.inStorageUnit = inStorageUnit;
  }

  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public Double getRemainAmount() {
    return remainAmount;
  }

  public void setRemainAmount(Double remainAmount) {
    this.remainAmount = remainAmount;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getUseRelatedAmount() {
    return useRelatedAmount;
  }

  public void setUseRelatedAmount(Double useRelatedAmount) {
    this.useRelatedAmount = useRelatedAmount;
  }

  public YesNo getDisabled() {
    return disabled;
  }

  public void setDisabled(YesNo disabled) {
    this.disabled = disabled;
  }

  public OutStorageSupplierType getSupplierType() {
    return supplierType;
  }

  public void setSupplierType(OutStorageSupplierType supplierType) {
    this.supplierType = supplierType;
  }

  public Double getSupplierRelatedAmount() {
    return supplierRelatedAmount;
  }

  public void setSupplierRelatedAmount(Double supplierRelatedAmount) {
    this.supplierRelatedAmount = supplierRelatedAmount;
  }

  public Double getAverageStoragePrice() {
    return averageStoragePrice;
  }

  public void setAverageStoragePrice(Double averageStoragePrice) {
    this.averageStoragePrice = averageStoragePrice;
  }

  @Override

  public String toString() {
    return "InStorageRecordDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", inStorageOrderId=" + inStorageOrderId +
        ", inStorageOrderType=" + inStorageOrderType +
        ", inStorageOrderStatus=" + inStorageOrderStatus +
        ", inStorageItemId=" + inStorageItemId +
        ", productId=" + productId +
        ", inStorageItemAmount=" + inStorageItemAmount +
        ", inStorageUnit='" + inStorageUnit + '\'' +
        ", supplierId=" + supplierId +
        ", supplierName='" + supplierName + '\'' +
        ", remainAmount=" + remainAmount +
        ", storehouseId=" + storehouseId +
        ", storehouseName='" + storehouseName + '\'' +
        ", price=" + price +
        ", useRelatedAmount=" + useRelatedAmount +
        ", supplierRelatedAmount=" + supplierRelatedAmount +
        ", disabled=" + disabled +
        ", supplierType=" + supplierType +
        ", averageStoragePrice=" + averageStoragePrice +
        '}';
  }
}
