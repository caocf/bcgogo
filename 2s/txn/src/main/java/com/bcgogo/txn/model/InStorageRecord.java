package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InStorageRecordDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "in_storage_record")
public class InStorageRecord extends LongIdentifier {
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
  private String storehouseName;
  private Long storehouseId;
  private Double price;//库存增加时的价格
  private YesNo disabled;//当入库单作废时 该单据的 disabled为Y
  private OutStorageSupplierType supplierType;//供应商类型

  private Double supplierRelatedAmount;//选定的供应商使用量


  public InStorageRecordDTO toDTO() {
    InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();

    inStorageRecordDTO.setId(getId());
    inStorageRecordDTO.setShopId(getShopId());
    inStorageRecordDTO.setInStorageOrderId(getInStorageOrderId());
    inStorageRecordDTO.setInStorageOrderType(getInStorageOrderType());
    inStorageRecordDTO.setInStorageItemId(getInStorageItemId());
    inStorageRecordDTO.setProductId(getProductId());
    inStorageRecordDTO.setInStorageItemAmount(getInStorageItemAmount());
    inStorageRecordDTO.setInStorageUnit(getInStorageUnit());
    inStorageRecordDTO.setInStorageOrderStatus(getInStorageOrderStatus());
    inStorageRecordDTO.setRemainAmount(getRemainAmount());
    inStorageRecordDTO.setSupplierId(getSupplierId());
    inStorageRecordDTO.setSupplierName(getSupplierName());
    inStorageRecordDTO.setStorehouseId(getStorehouseId());
    inStorageRecordDTO.setStorehouseName(getStorehouseName());
    inStorageRecordDTO.setPrice(getPrice());
    inStorageRecordDTO.setDisabled(getDisabled() == null ? YesNo.NO : getDisabled());
    inStorageRecordDTO.setSupplierType(getSupplierType());

    inStorageRecordDTO.setSupplierRelatedAmount(getSupplierRelatedAmount());

    return inStorageRecordDTO;
  }

  public void fromDTO(InStorageRecordDTO inStorageRecordDTO) {
    if (inStorageRecordDTO == null) {
      return;
    }
    this.setId(inStorageRecordDTO.getId());
    this.setShopId(inStorageRecordDTO.getShopId());
    this.setInStorageOrderId(inStorageRecordDTO.getInStorageOrderId());
    this.setInStorageOrderType(inStorageRecordDTO.getInStorageOrderType());
    this.setInStorageItemId(inStorageRecordDTO.getInStorageItemId());
    this.setProductId(inStorageRecordDTO.getProductId());
    this.setInStorageItemAmount(inStorageRecordDTO.getInStorageItemAmount());
    this.setInStorageUnit(inStorageRecordDTO.getInStorageUnit());
    this.setInStorageOrderStatus(inStorageRecordDTO.getInStorageOrderStatus());
    this.setRemainAmount(inStorageRecordDTO.getRemainAmount());
    this.setSupplierId(inStorageRecordDTO.getSupplierId());
    this.setSupplierName(inStorageRecordDTO.getSupplierName());
    this.setStorehouseName(inStorageRecordDTO.getStorehouseName());
    this.setStorehouseId(inStorageRecordDTO.getStorehouseId());
    this.setPrice(inStorageRecordDTO.getPrice());
    this.setDisabled(inStorageRecordDTO.getDisabled() == null ? YesNo.NO : inStorageRecordDTO.getDisabled());
    this.setSupplierType(inStorageRecordDTO.getSupplierType());
    this.setSupplierRelatedAmount(inStorageRecordDTO.getSupplierRelatedAmount());
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "in_storage_order_id")
  public Long getInStorageOrderId() {
    return inStorageOrderId;
  }

  public void setInStorageOrderId(Long inStorageOrderId) {
    this.inStorageOrderId = inStorageOrderId;
  }

  @Column(name = "in_storage_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getInStorageOrderType() {
    return inStorageOrderType;
  }

  public void setInStorageOrderType(OrderTypes inStorageOrderType) {
    this.inStorageOrderType = inStorageOrderType;
  }

  @Column(name = "in_storage_order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getInStorageOrderStatus() {
    return inStorageOrderStatus;
  }

  public void setInStorageOrderStatus(OrderStatus inStorageOrderStatus) {
    this.inStorageOrderStatus = inStorageOrderStatus;
  }

  @Column(name = "in_storage_item_id")
  public Long getInStorageItemId() {
    return inStorageItemId;
  }

  public void setInStorageItemId(Long inStorageItemId) {
    this.inStorageItemId = inStorageItemId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "in_storage_item_amount")
  public Double getInStorageItemAmount() {
    return inStorageItemAmount;
  }

  public void setInStorageItemAmount(Double inStorageItemAmount) {
    this.inStorageItemAmount = inStorageItemAmount;
  }

  @Column(name = "in_storage_unit")
  public String getInStorageUnit() {
    return inStorageUnit;
  }

  public void setInStorageUnit(String inStorageUnit) {
    this.inStorageUnit = inStorageUnit;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }

  @Column(name = "supplier_name")
  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  @Column(name = "remain_amount")
  public Double getRemainAmount() {
    return remainAmount;
  }

  public void setRemainAmount(Double remainAmount) {
    this.remainAmount = remainAmount;
  }

  @Column(name = "store_house_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  @Column(name = "store_house_name")
  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Column(name = "is_disabled")
  @Enumerated(EnumType.STRING)
  public YesNo getDisabled() {
    return disabled;
  }

  public void setDisabled(YesNo disabled) {
    this.disabled = disabled;
  }

  @Column(name = "supplier_type")
  @Enumerated(EnumType.STRING)
  public OutStorageSupplierType getSupplierType() {
    return supplierType;
  }

  public void setSupplierType(OutStorageSupplierType supplierType) {
    this.supplierType = supplierType;
  }

  @Column(name = "supplier_related_amount")
  public Double getSupplierRelatedAmount() {
    return supplierRelatedAmount;
  }

  public void setSupplierRelatedAmount(Double supplierRelatedAmount) {
    this.supplierRelatedAmount = supplierRelatedAmount;
  }
}
