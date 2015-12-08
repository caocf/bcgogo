package com.bcgogo.txn.dto.secondary;

import com.bcgogo.txn.dto.RepairOrderItemDTO;
import org.apache.commons.lang.StringUtils;

public class RepairOrderItemSecondaryDTO {
  public RepairOrderItemSecondaryDTO() {
  }

  private Long repairOrderSecondaryId;
  private Long shopId;
  private Long id;
  private String commodityCode;             //商品编号
  private Long productId;
  private String productName;               //品名
  private String brand;                     //品牌/产地
  private String spec;                      //规格
  private String model;                     //型号
  private Double price;                     //单价
  private Double amount;                    //数量
  private String unit;                      //单位
  private String storageUnit;               //单位
  private String sellUnit;                  //单位
  private Long rate;                        //单位
  private Double total;                     //小计

  public void fromRepairOrderItemDTO(RepairOrderItemDTO repairOrderItemDTO) {
    setShopId(repairOrderItemDTO.getShopId());
    setCommodityCode(repairOrderItemDTO.getCommodityCode());
    setProductId(repairOrderItemDTO.getProductId());
    setProductName(repairOrderItemDTO.getProductName());
    setBrand(repairOrderItemDTO.getBrand());
    setSpec(repairOrderItemDTO.getSpec());
    setUnit(repairOrderItemDTO.getUnit());
    setStorageUnit(repairOrderItemDTO.getStorageUnit());
    setSellUnit(repairOrderItemDTO.getSellUnit());
    setRate(repairOrderItemDTO.getRate());
    setModel(repairOrderItemDTO.getModel());
    setAmount(repairOrderItemDTO.getAmount());
    setPrice(repairOrderItemDTO.getPrice());
    setTotal(repairOrderItemDTO.getTotal());
  }

  public boolean isValidator() {
    return StringUtils.isNotEmpty(productName);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }
}
