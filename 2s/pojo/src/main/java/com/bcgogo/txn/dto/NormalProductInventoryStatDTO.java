package com.bcgogo.txn.dto;

import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.utils.NumberUtil;

/**
 * 后台CRM标准产品采购分析统计（只统计入库单）封装类
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 13-1-2
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
public class NormalProductInventoryStatDTO {

  private Long id;
  private Long shopId;
  private Long normalProductId; //标准产品库id
  private double amount; //入库数量
  private double total;  //入库总额
  private double topPrice; //最高价
  private double bottomPrice; //最低价
  private double averagePrice;//均价
  private NormalProductStatType normalProductStatType;
  private double times; //入库次数
  private String priceStr;
  //标准产品相关信息
  private String commodityCode;//商品编码
  private String nameAndBrand;//品名、品牌
  private String specAndModel;//规格、型号
  private String productVehicleBrand;//车辆品牌 车型
  private String unit;//单位

  //店铺相关信息
  private String shopName;
  private String shopVersion;
  private Long productLocalInfoId; //统计到具体某个店的某个商品的采购

  //店铺采购统计信息
  private String areaInfo;//所在区域
  private double inventoryAmount =0D;//库存
  private String lastInventoryDate;//上次采购信息


  //判断最低价是否已经被设置
  private boolean bottomPriceSet;

  public boolean isBottomPriceSet() {
    return bottomPriceSet;
  }

  public void setBottomPriceSet(boolean bottomPriceSet) {
    this.bottomPriceSet = bottomPriceSet;
  }

  public String getPriceStr() {
    return priceStr;
  }

  public void setPriceStr(String priceStr) {
    this.priceStr = priceStr;
  }

  public double getTimes() {
    return times;
  }

  public void setTimes(double times) {
    this.times = times;
  }

  public NormalProductStatType getNormalProductStatType() {
    return normalProductStatType;
  }

  public void setNormalProductStatType(NormalProductStatType normalProductStatType) {
    this.normalProductStatType = normalProductStatType;
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

  public Long getNormalProductId() {
    return normalProductId;
  }

  public void setNormalProductId(Long normalProductId) {
    this.normalProductId = normalProductId;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = NumberUtil.round(amount);
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = NumberUtil.round(total);
  }

  public double getTopPrice() {
    return topPrice;
  }

  public void setTopPrice(double topPrice) {
    this.topPrice = NumberUtil.round(topPrice);
  }

  public double getBottomPrice() {
    return bottomPrice;
  }

  public void setBottomPrice(double bottomPrice) {
    this.bottomPrice = NumberUtil.round(bottomPrice);
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getSpecAndModel() {
    return specAndModel;
  }

  public void setSpecAndModel(String specAndModel) {
    this.specAndModel = specAndModel;
  }

  public String getNameAndBrand() {
    return nameAndBrand;
  }

  public void setNameAndBrand(String nameAndBrand) {
    this.nameAndBrand = nameAndBrand;
  }

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopVersion() {
    return shopVersion;
  }

  public void setShopVersion(String shopVersion) {
    this.shopVersion = shopVersion;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public void calculateFromPurchaseInventory(PurchaseInventoryItemDTO purchaseInventoryItemDTO,boolean isSetProductId) {
    if (!this.isBottomPriceSet()) {
      this.setBottomPrice(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice()));
      this.setBottomPriceSet(true);
    }
    this.setAmount(this.getAmount() + NumberUtil.doubleVal(purchaseInventoryItemDTO.getAmount()));
    this.setTotal(this.getTotal() + NumberUtil.doubleVal(purchaseInventoryItemDTO.getTotal()));
    this.setTopPrice(this.getTopPrice() > NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice()) ?
        this.getTopPrice() : NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice()));
    this.setBottomPrice(this.getBottomPrice() < NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice()) ?
        this.getBottomPrice() : NumberUtil.doubleVal(purchaseInventoryItemDTO.getPrice()));
    this.setTimes(this.getTimes() + 1);
    if (isSetProductId) {
      this.setProductLocalInfoId(purchaseInventoryItemDTO.getProductId());
    }

  }

  public double getAveragePrice() {
    return averagePrice;
  }

  public void setAveragePrice(double averagePrice) {
    this.averagePrice = NumberUtil.round(averagePrice);
  }

  public String getAreaInfo() {
    return areaInfo;
  }

  public void setAreaInfo(String areaInfo) {
    this.areaInfo = areaInfo;
  }

  public double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(double inventoryAmount) {
    this.inventoryAmount = NumberUtil.round(inventoryAmount);
  }

  public String getLastInventoryDate() {
    return lastInventoryDate;
  }

  public void setLastInventoryDate(String lastInventoryDate) {
    this.lastInventoryDate = lastInventoryDate;
  }
}
