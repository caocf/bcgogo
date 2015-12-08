package com.bcgogo.txn.dto;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class StoreHouseInventoryDTO {
  private Long id;
  private Long storehouseId;
  private Long productLocalInfoId;
  private Double amount;
  private String storageBin;//货位====原来的仓位

  private Double changeAmount;//用正负表达增减
  private String storeHouseName;//仓库名
  private String storehouseIdStr;
  private String productLocalInfoIdStr;

  public StoreHouseInventoryDTO() {
  }

  public StoreHouseInventoryDTO(Long storehouseId,String storageBin) {
    this.storehouseId = storehouseId;
    this.storageBin = storageBin;
  }
  public StoreHouseInventoryDTO(Long storehouseId, Long productLocalInfoId, Double amount,Double changeAmount,String storageBin) {
    this.storehouseId = storehouseId;
    this.productLocalInfoId = productLocalInfoId;
    this.amount = amount;
    this.storageBin = storageBin;
    this.changeAmount = changeAmount;
  }
  public StoreHouseInventoryDTO(Long storehouseId, Long productLocalInfoId, Double amount) {
    this.storehouseId = storehouseId;
    this.productLocalInfoId = productLocalInfoId;
    this.amount = amount;
  }
  public StoreHouseInventoryDTO(Long storehouseId, Long productLocalInfoId, Double amount,Double changeAmount) {
    this.storehouseId = storehouseId;
    this.productLocalInfoId = productLocalInfoId;
    this.amount = amount;
    this.changeAmount = changeAmount;
  }
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
    if(storehouseId != null){
      this.setStorehouseIdStr(storehouseId.toString());
    }else {
      this.setStorehouseIdStr("");
    }
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
    if(productLocalInfoId != null){
        this.setProductLocalInfoIdStr(productLocalInfoId.toString());
      }else {
        this.setProductLocalInfoIdStr("");
      }
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.changeAmount = null;
    this.amount = amount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getChangeAmount() {
    return changeAmount;
  }

  public void setChangeAmount(Double changeAmount) {
    this.changeAmount = changeAmount;
  }

  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }

  public String getStoreHouseName() {
    return storeHouseName;
  }

  public void setStoreHouseName(String storeHouseName) {
    this.storeHouseName = storeHouseName;
  }

  public String getStorehouseIdStr() {
    return storehouseIdStr;
  }

  public void setStorehouseIdStr(String storehouseIdStr) {
    this.storehouseIdStr = storehouseIdStr;
  }

  public String getProductLocalInfoIdStr() {
    return productLocalInfoIdStr;
  }

  public void setProductLocalInfoIdStr(String productLocalInfoIdStr) {
    this.productLocalInfoIdStr = productLocalInfoIdStr;
  }
}
