package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.StoreHouseInventoryDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 */
@Entity
@Table(name = "storehouse_inventory")
public class StoreHouseInventory extends LongIdentifier {
  private Long storehouseId;
  private Long productLocalInfoId;
  private Double amount;
  private String storageBin;

  @Column(name="storehouse_id")
  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }
  @Column(name="product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }
  @Column(name="amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public StoreHouseInventoryDTO toDTO(){
    StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO();
    storeHouseInventoryDTO.setId(this.getId());
    storeHouseInventoryDTO.setAmount(this.getAmount());
    storeHouseInventoryDTO.setProductLocalInfoId(this.getProductLocalInfoId());
    storeHouseInventoryDTO.setStorehouseId(this.getStorehouseId());
    storeHouseInventoryDTO.setStorageBin(this.getStorageBin());
    return storeHouseInventoryDTO;
  }

  @Column(name="storage_bin")
  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }
}
