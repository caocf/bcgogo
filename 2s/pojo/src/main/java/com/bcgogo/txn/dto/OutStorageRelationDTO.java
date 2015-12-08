package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OutStorageSupplierType;
import com.bcgogo.enums.OutStorageType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ObjectUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class OutStorageRelationDTO implements Serializable,Cloneable{
  private Long id;
  private Long shopId;
  private Long outStorageOrderId;
  private Long relationTime;//出入库关联时间
  private OrderTypes outStorageOrderType;
  private Long outStorageItemId;
  private Long productId;
  private Double outStorageItemAmount;
  private String outStorageUnit;
  private Long outStorageStoreHouseId;//出库时的仓库

  private OrderTypes relatedOrderType;
  private Long relatedOrderId;
  private Long relatedItemId;
  private Double useRelatedAmount;
  private String relatedUnit;
  private Long relatedStoreHouseId;
  private Long relatedSupplierId;
  private String relatedSupplierName;
  private String relatedSupplierIdStr;
  private Double relatedSupplierInventory;
  private Double relatedSupplierAveragePrice;//关联供应商的平均价

  private OutStorageType outStorageType;    //出库类型，（系统自动指定，客户手选）
  private OutStorageSupplierType supplierType;
  private YesNo disabled;//库存增加时保存InStorageRecord disabled 为N 作废时disabled为Y

  private Double supplierRelatedAmount;//选定的供应商使用量

  private Double averageStoragePrice;//供应商平均价 用于调拨单

  public String getRelatedSupplierName() {
    return relatedSupplierName;
  }

  public void setRelatedSupplierName(String relatedSupplierName) {
    this.relatedSupplierName = relatedSupplierName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getOutStorageOrderId() {
    return outStorageOrderId;
  }

  public void setOutStorageOrderId(Long outStorageOrderId) {
    this.outStorageOrderId = outStorageOrderId;
  }

  public OrderTypes getOutStorageOrderType() {
    return outStorageOrderType;
  }

  public void setOutStorageOrderType(OrderTypes outStorageOrderType) {
    this.outStorageOrderType = outStorageOrderType;
  }

  public Long getOutStorageItemId() {
    return outStorageItemId;
  }

  public void setOutStorageItemId(Long outStorageItemId) {
    this.outStorageItemId = outStorageItemId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Double getOutStorageItemAmount() {
    return outStorageItemAmount;
  }

  public void setOutStorageItemAmount(Double outStorageItemAmount) {
    this.outStorageItemAmount = outStorageItemAmount;
  }

  public String getOutStorageUnit() {
    return outStorageUnit;
  }

  public void setOutStorageUnit(String outStorageUnit) {
    this.outStorageUnit = outStorageUnit;
  }

  public OrderTypes getRelatedOrderType() {
    return relatedOrderType;
  }

  public void setRelatedOrderType(OrderTypes relatedOrderType) {
    this.relatedOrderType = relatedOrderType;
  }

  public Long getRelatedOrderId() {
    return relatedOrderId;
  }

  public void setRelatedOrderId(Long relatedOrderId) {
    this.relatedOrderId = relatedOrderId;
  }

  public Long getRelatedItemId() {
    return relatedItemId;
  }

  public void setRelatedItemId(Long relatedItemId) {
    this.relatedItemId = relatedItemId;
  }

  public Double getUseRelatedAmount() {
    return useRelatedAmount;
  }

  public void setUseRelatedAmount(Double useRelatedAmount) {
    this.useRelatedAmount = useRelatedAmount;
  }

  public Long getRelatedSupplierId() {
    if (relatedSupplierId != null) {
      return relatedSupplierId;
    }
    if (NumberUtil.isLongNumber(getRelatedSupplierIdStr())) {
      return Long.valueOf(getRelatedSupplierIdStr());
    }
    return null;
  }

  public void setRelatedSupplierId(Long relatedSupplierId) {
    this.relatedSupplierId = relatedSupplierId;
    if(relatedSupplierId==null){
      this.setRelatedSupplierIdStr("");
    }else{
      this.setRelatedSupplierIdStr(String.valueOf(relatedSupplierId));
    }
  }

  public String getRelatedSupplierIdStr() {
    return relatedSupplierIdStr;
  }

  public void setRelatedSupplierIdStr(String relatedSupplierIdStr) {
    this.relatedSupplierIdStr = relatedSupplierIdStr;
  }

  public Double getRelatedSupplierInventory() {
    return relatedSupplierInventory;
  }

  public void setRelatedSupplierInventory(Double relatedSupplierInventory) {
    this.relatedSupplierInventory = relatedSupplierInventory;
  }

  public OutStorageType getOutStorageType() {
    return outStorageType;
  }

  public void setOutStorageType(OutStorageType outStorageType) {
    this.outStorageType = outStorageType;
  }

  public OutStorageSupplierType getSupplierType() {
    return supplierType;
  }

  public void setSupplierType(OutStorageSupplierType supplierType) {
    this.supplierType = supplierType;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getRelationTime() {
    return relationTime;
  }

  public void setRelationTime(Long relationTime) {
    this.relationTime = relationTime;
  }


  public Long getOutStorageStoreHouseId() {
    return outStorageStoreHouseId;
  }

  public void setOutStorageStoreHouseId(Long outStorageStoreHouseId) {
    this.outStorageStoreHouseId = outStorageStoreHouseId;
  }

  public String getRelatedUnit() {
    return relatedUnit;
  }

  public void setRelatedUnit(String relatedUnit) {
    this.relatedUnit = relatedUnit;
  }

  public Long getRelatedStoreHouseId() {
    return relatedStoreHouseId;
  }

  public void setRelatedStoreHouseId(Long relatedStoreHouseId) {
    this.relatedStoreHouseId = relatedStoreHouseId;
  }

  public YesNo getDisabled() {
    return disabled;
  }

  public void setDisabled(YesNo disabled) {
    this.disabled = disabled;
  }

  public Double getSupplierRelatedAmount() {
    return supplierRelatedAmount;
  }

  public void setSupplierRelatedAmount(Double supplierRelatedAmount) {
    this.supplierRelatedAmount = supplierRelatedAmount;
  }

  public Double getRelatedSupplierAveragePrice() {
    return relatedSupplierAveragePrice;
  }

  public void setRelatedSupplierAveragePrice(Double relatedSupplierAveragePrice) {
    this.relatedSupplierAveragePrice = relatedSupplierAveragePrice;
  }

  public Double getAverageStoragePrice() {
    return averageStoragePrice;
  }

  public void setAverageStoragePrice(Double averageStoragePrice) {
    this.averageStoragePrice = averageStoragePrice;
  }

  @Override
  public String toString() {
    return "OutStorageRelationDTO{" +
        "id=" + id +
        ", shopId=" + shopId +
        ", outStorageOrderId=" + outStorageOrderId +
        ", relationTime=" + relationTime +
        ", outStorageOrderType=" + outStorageOrderType +
        ", outStorageItemId=" + outStorageItemId +
        ", productId=" + productId +
        ", outStorageItemAmount=" + outStorageItemAmount +
        ", outStorageUnit='" + outStorageUnit + '\'' +
        ", outStorageStoreHouseId=" + outStorageStoreHouseId +
        ", relatedOrderType=" + relatedOrderType +
        ", relatedOrderId=" + relatedOrderId +
        ", relatedItemId=" + relatedItemId +
        ", useRelatedAmount=" + useRelatedAmount +
        ", relatedUnit='" + relatedUnit + '\'' +
        ", relatedStoreHouseId=" + relatedStoreHouseId +
        ", relatedSupplierId=" + relatedSupplierId +
        ", relatedSupplierName='" + relatedSupplierName + '\'' +
        ", relatedSupplierIdStr='" + relatedSupplierIdStr + '\'' +
        ", relatedSupplierInventory=" + relatedSupplierInventory +
        ", relatedSupplierAveragePrice=" + relatedSupplierAveragePrice +
        ", outStorageType=" + outStorageType +
        ", supplierType=" + supplierType +
        ", disabled=" + disabled +
        ", supplierRelatedAmount=" + supplierRelatedAmount +
        ", averageStoragePrice=" + averageStoragePrice +
        '}';
  }

  public OutStorageRelationDTO clone() throws CloneNotSupportedException{
    return (OutStorageRelationDTO)super.clone();
  }


}

