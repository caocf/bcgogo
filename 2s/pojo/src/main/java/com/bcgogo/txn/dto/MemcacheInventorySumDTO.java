package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Xinyu Qiu
 * Date: 12-6-14
 * Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
public class MemcacheInventorySumDTO  implements Serializable {

  private Long shopId;
  private Integer inventoryCount;    //库存商品种类
  private Double inventorySum;       //库存商品入库总金额
  private Long storageTime;           //mamcache记录时间
	private Double inventoryProductAmount;//库存商品总数量

	public Double getInventoryProductAmount() {
		return inventoryProductAmount;
	}

	public void setInventoryProductAmount(Double inventoryProductAmount) {
		this.inventoryProductAmount = inventoryProductAmount;
	}

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Integer getInventoryCount() {
    return inventoryCount;
  }

  public void setInventoryCount(Integer inventoryCount) {
    this.inventoryCount = inventoryCount;
  }

  public Double getInventorySum() {
    return inventorySum;
  }

  public void setInventorySum(Double inventorySum) {
    this.inventorySum = inventorySum;
  }

  public Long getStorageTime() {
    return storageTime;
  }

  public void setStorageTime(Long storageTime) {
    this.storageTime = storageTime;
  }

	public void setInventoryInfoCounts(Double[] counts) {
		if(counts==null||counts.length==0){
			return;
		}
		this.setInventoryCount(counts[0].intValue());
		this.setInventoryProductAmount(counts[1].doubleValue());
		this.setInventorySum(counts[2].doubleValue());
	}
}
