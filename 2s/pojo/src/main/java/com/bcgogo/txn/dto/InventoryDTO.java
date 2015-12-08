package com.bcgogo.txn.dto;

import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.utils.JsonUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-10-11
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
public class InventoryDTO implements Serializable {
  public InventoryDTO() {
  }

  private Long id;
  private Long shopId;
  private Double amount;
  private String unit;
  private Double lowerLimit;
  private Double upperLimit;
	private Double noOrderInventory;
    //销售价
  private Double salesPrice;

  //最近入库价
  private Double latestInventoryPrice;

  private Long lastStorageTime;
  //库存平均价
  private Double inventoryAveragePrice;

  private Long version;

  private StoreHouseInventoryDTO storeHouseInventoryDTO;

  private AchievementType achievementType;//提成方式 按金额 按比率
  private Double achievementAmount;//提成数额

  public void fromProductDTO(ProductDTO productDTO) {
    if(productDTO != null){
      this.setId(productDTO.getProductLocalInfoId());
      this.setShopId(productDTO.getShopId());
      this.setAmount(productDTO.getInventoryNum());
      this.setUnit(productDTO.getSellUnit());
      this.setUpperLimit(productDTO.getUpperLimit());
      this.setLowerLimit(productDTO.getLowerLimit());
      this.setNoOrderInventory(productDTO.getInventoryNum());
      this.setInventoryAveragePrice(productDTO.getPurchasePrice());
      this.setSalesPrice(productDTO.getRecommendedPrice());
    }
  }

  public AchievementType getAchievementType() {
    return achievementType;
  }

  public void setAchievementType(AchievementType achievementType) {
    this.achievementType = achievementType;
  }

  public Double getAchievementAmount() {
    return achievementAmount;
  }

  public void setAchievementAmount(Double achievementAmount) {
    this.achievementAmount = achievementAmount;
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

  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

	public Double getNoOrderInventory() {
		return noOrderInventory;
	}

	public void setNoOrderInventory(Double noOrderInventory) {
		this.noOrderInventory = noOrderInventory;
	}

  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
  }

  public Double getLatestInventoryPrice() {
    return latestInventoryPrice;
  }

  public void setLatestInventoryPrice(Double latestInventoryPrice) {
    this.latestInventoryPrice = latestInventoryPrice;
  }

  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  @Override
	public String toString() {
		return JsonUtil.objectToJson(this);
	}

  public StoreHouseInventoryDTO getStoreHouseInventoryDTO() {
    return storeHouseInventoryDTO;
  }

  public void setStoreHouseInventoryDTO(StoreHouseInventoryDTO storeHouseInventoryDTO) {
    this.storeHouseInventoryDTO = storeHouseInventoryDTO;
  }

  public Long getLastStorageTime() {
    return lastStorageTime;
  }

  public void setLastStorageTime(Long lastStorageTime) {
    this.lastStorageTime = lastStorageTime;
  }


}
