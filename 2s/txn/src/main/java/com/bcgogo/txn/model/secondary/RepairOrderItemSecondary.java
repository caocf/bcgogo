package com.bcgogo.txn.model.secondary;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.secondary.RepairOrderItemSecondaryDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "repair_order_item_secondary")
public class RepairOrderItemSecondary extends LongIdentifier {
  private Long repairOrderSecondaryId;
  private Long shopId;
  private String commodityCode;             //商品编号
  private String productName;               //品名
  private Long productId;
  private String brand;                     //品牌/产地
  private String spec;                      //规格
  private String model;                     //型号
  private Double price;                     //单价
  private Double amount;                    //数量
  private String unit;                      //单位
  private Double total;                     //小计
  private String storageBin;                //货位
  private Double inventoryAmount;         //库存数量

  public RepairOrderItemSecondary() {
  }

  public void fromDTO(RepairOrderItemSecondaryDTO repairOrderItemSecondaryDTO) {
    setCommodityCode(repairOrderItemSecondaryDTO.getCommodityCode());
    setProductId(repairOrderItemSecondaryDTO.getProductId());
    setProductName(repairOrderItemSecondaryDTO.getProductName());
    setBrand(repairOrderItemSecondaryDTO.getBrand());
    setSpec(repairOrderItemSecondaryDTO.getSpec());
    setModel(repairOrderItemSecondaryDTO.getModel());
    setPrice(repairOrderItemSecondaryDTO.getPrice());
    setAmount(repairOrderItemSecondaryDTO.getAmount());
    setUnit(repairOrderItemSecondaryDTO.getUnit());
    setTotal(repairOrderItemSecondaryDTO.getTotal());
  }

  public RepairOrderItemSecondaryDTO toDTO() {
    RepairOrderItemSecondaryDTO repairOrderItemSecondaryDTO = new RepairOrderItemSecondaryDTO();
    repairOrderItemSecondaryDTO.setId(getId());
    repairOrderItemSecondaryDTO.setShopId(shopId);
    repairOrderItemSecondaryDTO.setRepairOrderSecondaryId(repairOrderSecondaryId);
    repairOrderItemSecondaryDTO.setCommodityCode(commodityCode);
    repairOrderItemSecondaryDTO.setProductId(productId);
    repairOrderItemSecondaryDTO.setProductName(productName);
    repairOrderItemSecondaryDTO.setBrand(brand);
    repairOrderItemSecondaryDTO.setSpec(spec);
    repairOrderItemSecondaryDTO.setModel(model);
    repairOrderItemSecondaryDTO.setPrice(price);
    repairOrderItemSecondaryDTO.setAmount(amount);
    repairOrderItemSecondaryDTO.setUnit(unit);
    repairOrderItemSecondaryDTO.setTotal(total);
    return repairOrderItemSecondaryDTO;
  }


  @Column(name = "repair_order_secondary_id")
  public Long getRepairOrderSecondaryId() {
    return repairOrderSecondaryId;
  }

  public void setRepairOrderSecondaryId(Long repairOrderSecondaryId) {
    this.repairOrderSecondaryId = repairOrderSecondaryId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "commodity_code")
  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  @Column(name = "spec")
  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  @Column(name = "model")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
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

  @Column(name = "unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "storage_bin")
  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }

  @Column(name = "inventory_amount")
  public Double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }
}
