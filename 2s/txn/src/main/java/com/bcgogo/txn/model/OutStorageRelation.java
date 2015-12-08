package com.bcgogo.txn.model;

import com.bcgogo.enums.*;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.OutStorageRelationDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "out_storage_relation")
public class OutStorageRelation extends LongIdentifier {
  private Long outStorageOrderId;
  private Long shopId;
  private Long relationTime;//出入库关联时间
  private OrderTypes outStorageOrderType;
  private Long outStorageItemId;
  private Long productId;
  private Double outStorageItemAmount;
  private String outStorageUnit;
  private OrderTypes relatedOrderType;
  private Long relatedOrderId;
  private Long relatedItemId;
  private Double useRelatedAmount;
  private Long relatedSupplierId;
  private String relatedSupplierName;
  private OutStorageType outStorageType;    //出库类型，（系统自动指定，客户手选）
  private OutStorageSupplierType supplierType;
  private String relatedUnit;//被使用商品的单位

  private YesNo disabled;//当入库单作废时 关联该入库单的OutstorageRelation disabled为Y
  private Double supplierRelatedAmount;//选定的供应商使用量

  private OrderStatus orderStatus;


  public OutStorageRelationDTO toDTO() {
    OutStorageRelationDTO outStorageRelationDTO = new OutStorageRelationDTO();

    outStorageRelationDTO.setId(getId());
    outStorageRelationDTO.setShopId(getShopId());
    outStorageRelationDTO.setRelationTime(getRelationTime());
    outStorageRelationDTO.setOutStorageOrderId(getOutStorageOrderId());
    outStorageRelationDTO.setOutStorageOrderType(getOutStorageOrderType());
    outStorageRelationDTO.setOutStorageItemId(getOutStorageItemId());
    outStorageRelationDTO.setProductId(getProductId());
    outStorageRelationDTO.setOutStorageItemAmount(getOutStorageItemAmount());
    outStorageRelationDTO.setOutStorageUnit(getOutStorageUnit());
    outStorageRelationDTO.setRelatedOrderType(getRelatedOrderType());
    outStorageRelationDTO.setRelatedOrderId(getRelatedOrderId());
    outStorageRelationDTO.setRelatedItemId(getRelatedItemId());
    outStorageRelationDTO.setUseRelatedAmount(getUseRelatedAmount());
    outStorageRelationDTO.setRelatedSupplierId(getRelatedSupplierId());
    outStorageRelationDTO.setOutStorageType(getOutStorageType());
    outStorageRelationDTO.setSupplierType(getSupplierType());
    outStorageRelationDTO.setRelatedSupplierName(getRelatedSupplierName());
    outStorageRelationDTO.setRelatedUnit(getRelatedUnit());
    outStorageRelationDTO.setDisabled(getDisabled() == null ? YesNo.NO : getDisabled());
    outStorageRelationDTO.setSupplierRelatedAmount(getSupplierRelatedAmount());

    return outStorageRelationDTO;
  }

  public void fromDTO(OutStorageRelationDTO outStorageRelationDTO) {
    if (outStorageRelationDTO == null) {
      return;
    }
    this.setId(outStorageRelationDTO.getId());
    this.setShopId(outStorageRelationDTO.getShopId());
    this.setRelationTime(outStorageRelationDTO.getRelationTime());
    this.setOutStorageOrderId(outStorageRelationDTO.getOutStorageOrderId());
    this.setOutStorageOrderType(outStorageRelationDTO.getOutStorageOrderType());
    this.setOutStorageItemId(outStorageRelationDTO.getOutStorageItemId());
    this.setProductId(outStorageRelationDTO.getProductId());
    this.setOutStorageItemAmount(outStorageRelationDTO.getOutStorageItemAmount());
    this.setOutStorageUnit(outStorageRelationDTO.getOutStorageUnit());
    this.setRelatedOrderType(outStorageRelationDTO.getRelatedOrderType());
    this.setRelatedOrderId(outStorageRelationDTO.getRelatedOrderId());
    this.setRelatedItemId(outStorageRelationDTO.getRelatedItemId());
    this.setUseRelatedAmount(outStorageRelationDTO.getUseRelatedAmount());
    this.setRelatedSupplierId(outStorageRelationDTO.getRelatedSupplierId());
    this.setOutStorageType(outStorageRelationDTO.getOutStorageType());
    this.setSupplierType(outStorageRelationDTO.getSupplierType());
    this.setRelatedSupplierName(outStorageRelationDTO.getRelatedSupplierName());
    this.setRelatedUnit(outStorageRelationDTO.getRelatedUnit());
    this.setDisabled(outStorageRelationDTO.getDisabled() == null ? YesNo.NO : outStorageRelationDTO.getDisabled());
    this.setSupplierRelatedAmount(outStorageRelationDTO.getSupplierRelatedAmount());
  }

  @Column(name = "out_storage_order_id")
  public Long getOutStorageOrderId() {
    return outStorageOrderId;
  }

  public void setOutStorageOrderId(Long outStorageOrderId) {
    this.outStorageOrderId = outStorageOrderId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "relation_time")
  public Long getRelationTime() {
    return relationTime;
  }

  public void setRelationTime(Long relationTime) {
    this.relationTime = relationTime;
  }

  @Column(name = "out_storage_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOutStorageOrderType() {
    return outStorageOrderType;
  }

  public void setOutStorageOrderType(OrderTypes outStorageOrderType) {
    this.outStorageOrderType = outStorageOrderType;
  }

  @Column(name = "out_storage_item_id")
  public Long getOutStorageItemId() {
    return outStorageItemId;
  }

  public void setOutStorageItemId(Long outStorageItemId) {
    this.outStorageItemId = outStorageItemId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "out_storage_item_amount")
  public Double getOutStorageItemAmount() {
    return outStorageItemAmount;
  }

  public void setOutStorageItemAmount(Double outStorageItemAmount) {
    this.outStorageItemAmount = outStorageItemAmount;
  }

  @Column(name = "out_storage_unit")
  public String getOutStorageUnit() {
    return outStorageUnit;
  }

  public void setOutStorageUnit(String outStorageUnit) {
    this.outStorageUnit = outStorageUnit;
  }

  @Column(name = "related_order_type")
  @Enumerated(EnumType.STRING)
  public OrderTypes getRelatedOrderType() {
    return relatedOrderType;
  }

  public void setRelatedOrderType(OrderTypes relatedOrderType) {
    this.relatedOrderType = relatedOrderType;
  }

  @Column(name = "related_order_id")
  public Long getRelatedOrderId() {
    return relatedOrderId;
  }

  public void setRelatedOrderId(Long relatedOrderId) {
    this.relatedOrderId = relatedOrderId;
  }

  @Column(name = "related_item_id")
  public Long getRelatedItemId() {
    return relatedItemId;
  }

  public void setRelatedItemId(Long relatedItemId) {
    this.relatedItemId = relatedItemId;
  }

  @Column(name = "use_related_amount")
  public Double getUseRelatedAmount() {
    return useRelatedAmount;
  }

  public void setUseRelatedAmount(Double useRelatedAmount) {
    this.useRelatedAmount = useRelatedAmount;
  }

  @Column(name = "related_supplier_id")
  public Long getRelatedSupplierId() {
    return relatedSupplierId;
  }

  public void setRelatedSupplierId(Long relatedSupplierId) {
    this.relatedSupplierId = relatedSupplierId;
  }

  @Column(name = "out_storage_type")
  @Enumerated(EnumType.STRING)
  public OutStorageType getOutStorageType() {
    return outStorageType;
  }

  public void setOutStorageType(OutStorageType outStorageType) {
    this.outStorageType = outStorageType;
  }

  @Column(name = "supplier_type")
  @Enumerated(EnumType.STRING)
  public OutStorageSupplierType getSupplierType() {
    return supplierType;
  }

  public void setSupplierType(OutStorageSupplierType supplierType) {
    this.supplierType = supplierType;
  }

  @Column(name = "related_supplier_name")
  public String getRelatedSupplierName() {
    return relatedSupplierName;
  }

  public void setRelatedSupplierName(String relatedSupplierName) {
    this.relatedSupplierName = relatedSupplierName;
  }

  @Column(name = "related_unit")
  public String getRelatedUnit() {
    return relatedUnit;
  }

  public void setRelatedUnit(String relatedUnit) {
    this.relatedUnit = relatedUnit;
  }

  @Column(name = "is_disabled")
  @Enumerated(EnumType.STRING)
  public YesNo getDisabled() {
    return disabled;
  }

  public void setDisabled(YesNo disabled) {
    this.disabled = disabled;
  }

  @Column(name = "supplier_related_amount")
  public Double getSupplierRelatedAmount() {
    return supplierRelatedAmount;
  }

  public void setSupplierRelatedAmount(Double supplierRelatedAmount) {
    this.supplierRelatedAmount = supplierRelatedAmount;
  }

  @Column(name = "order_status")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

}
