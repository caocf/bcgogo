package com.bcgogo.txn.model;

import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.InventoryDTO;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-19
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "inventory")
public class Inventory extends LongIdentifier {
  public Inventory() {
  }

  public Inventory fromDTO(InventoryDTO inventoryDTO){
    if(inventoryDTO == null){
      return this;
    }
    this.setAmount(inventoryDTO.getAmount());
    this.setShopId(inventoryDTO.getShopId());
    this.setId(inventoryDTO.getId());
    this.setUnit(inventoryDTO.getUnit());
    this.setUpperLimit(inventoryDTO.getUpperLimit());
    this.setLowerLimit(inventoryDTO.getLowerLimit());
    this.setNoOrderInventory(inventoryDTO.getNoOrderInventory());
    this.setSalesPrice(inventoryDTO.getSalesPrice());
    this.setLatestInventoryPrice(inventoryDTO.getLatestInventoryPrice());
    this.setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
    this.setLastStorageTime(inventoryDTO.getLastStorageTime());

    this.setSalesTotalAchievementAmount(inventoryDTO.getAchievementAmount());
    this.setSalesTotalAchievementType(inventoryDTO.getAchievementType());

    return this;
  }

  public InventoryDTO toDTO() {
    InventoryDTO inventoryDTO = new InventoryDTO();
    inventoryDTO.setId(this.getId());
    inventoryDTO.setAmount(this.getAmount());
    inventoryDTO.setShopId(this.getShopId());
    inventoryDTO.setUnit(this.getUnit());
    inventoryDTO.setUpperLimit(this.getUpperLimit());
    inventoryDTO.setLowerLimit(this.getLowerLimit());
    inventoryDTO.setNoOrderInventory(this.getNoOrderInventory());
    inventoryDTO.setInventoryAveragePrice(this.getInventoryAveragePrice());
    inventoryDTO.setLatestInventoryPrice(this.getLatestInventoryPrice());
    inventoryDTO.setSalesPrice(this.getSalesPrice());
    inventoryDTO.setVersion(getVersion());
    inventoryDTO.setLastStorageTime(getLastStorageTime());

    inventoryDTO.setAchievementAmount(getSalesTotalAchievementAmount());
    inventoryDTO.setAchievementType(getSalesTotalAchievementType());
    return inventoryDTO;
  }

  public void setId(Long id){
    super.setId(id);
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name = "unit" , length = 20)
  public String getUnit(){
    return unit;
  }

  public void setUnit(String unit){
    if(StringUtils.isEmpty(unit)){
      this.unit = null;
    }else {
      this.unit = unit;
    }
  }

  @Column(name = "lower_limit")
  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  @Column(name = "upper_limit")
  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

  @Column(name = "no_order_inventory")
  public Double getNoOrderInventory() {
    return noOrderInventory;
  }

  public void setNoOrderInventory(Double noOrderInventory) {
    this.noOrderInventory = noOrderInventory;
  }

  private Long shopId;
  private double amount;
  private String unit;
  private Double lowerLimit;
  private Double upperLimit;
  private Double noOrderInventory;
  private Long lastSalesTime; //上次销售时间 滞销品统计使用
  private Long lastStorageTime;//最新入库时间
  //销售价
  private Double salesPrice;
  //最近入库价
  private Double latestInventoryPrice;
  //库存平均价
  private Double inventoryAveragePrice;

  //员工业绩统计
  private AchievementType salesTotalAchievementType;//商品销售额提成方式 按金额 按比率
  private Double salesTotalAchievementAmount;//商品销售额提成数额
  private AchievementType salesProfitAchievementType;      //商品销售利润配置方式 按金额 按比率
  private Double salesProfitAchievementAmount;   //商品销售利润配置金额



  @Column(name = "sales_total_achievement_type")
  @Enumerated(EnumType.STRING)
  public AchievementType getSalesTotalAchievementType() {
    return salesTotalAchievementType;
  }

  public void setSalesTotalAchievementType(AchievementType salesTotalAchievementType) {
    this.salesTotalAchievementType = salesTotalAchievementType;
  }

  @Column(name = "sales_total_achievement_amount")
  public Double getSalesTotalAchievementAmount() {
    return salesTotalAchievementAmount;
  }

  public void setSalesTotalAchievementAmount(Double salesTotalAchievementAmount) {
    this.salesTotalAchievementAmount = salesTotalAchievementAmount;
  }

  @Column(name = "last_sales_time")
  public Long getLastSalesTime() {
    return lastSalesTime;
  }

  public void setLastSalesTime(Long lastSalesTime) {
    this.lastSalesTime = lastSalesTime;
  }



  @Column(name = "sales_price")
  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
  }

  @Column(name = "latest_inventory_price")
  public Double getLatestInventoryPrice() {
    return latestInventoryPrice;
  }

  public void setLatestInventoryPrice(Double latestInventoryPrice) {
    this.latestInventoryPrice = latestInventoryPrice;
  }

  @Column(name = "inventory_average_price")
  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  @Column(name="last_storage_time")
  public Long getLastStorageTime() {
    return lastStorageTime;
  }

  public void setLastStorageTime(Long lastStorageTime) {
    this.lastStorageTime = lastStorageTime;
  }


  @Column(name = "sales_profit_achievement_amount")
  public Double getSalesProfitAchievementAmount() {
    return salesProfitAchievementAmount;
  }

  public void setSalesProfitAchievementAmount(Double salesProfitAchievementAmount) {
    this.salesProfitAchievementAmount = salesProfitAchievementAmount;
  }

  @Column(name = "sales_profit_achievement_type")
  @Enumerated(EnumType.STRING)
  public AchievementType getSalesProfitAchievementType() {
    return salesProfitAchievementType;
  }

  public void setSalesProfitAchievementType(AchievementType salesProfitAchievementType) {
    this.salesProfitAchievementType = salesProfitAchievementType;
  }
}
