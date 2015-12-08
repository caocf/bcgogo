package com.bcgogo.txn.dto.recommend;

import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 各个店铺所关心的商品每周销量、入库量统计 作为供求中心首页显示
 */
public class ShopProductMatchResultDTO {
  private Long id;
  private Long shopId;
  private String productName; //商品名称
  private String productBrand;//商品品牌
  private Double salesAmount;//销量
  private String salesUnit; //销售单位
  private Double inventoryAmount; //入库数量
  private String inventoryUnit; //入库单位
  private Long statTime; //统计时间
  private ShopKind shopKind;//店铺类型
  private Integer weekOfYear;//当年时间的周数

  private Long productLocalInfoId;
  private Long matchedId;  //sales_inventory_week_id

  private Integer statMonth;//月份
  private Integer statDay;//日期
  private Integer statYear;//年份

  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  public Integer getStatDay() {
    return statDay;
  }

  public void setStatDay(Integer statDay) {
    this.statDay = statDay;
  }

  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  public ShopProductMatchResultDTO() {
    salesUnit = "个";
    inventoryUnit = "个";
    salesAmount = 0D;
    inventoryAmount = 0D;
  }

  public Long getMatchedId() {
    return matchedId;
  }

  public void setMatchedId(Long matchedId) {
    this.matchedId = matchedId;
  }

  public Integer getWeekOfYear() {
    return weekOfYear;
  }

  public void setWeekOfYear(Integer weekOfYear) {
    this.weekOfYear = weekOfYear;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  public Double getSalesAmount() {
    return salesAmount;
  }

  public void setSalesAmount(Double salesAmount) {
    this.salesAmount = NumberUtil.toReserve(salesAmount, NumberUtil.PRECISION);
  }

  public String getSalesUnit() {
    return salesUnit;
  }

  public void setSalesUnit(String salesUnit) {
    this.salesUnit = salesUnit;
  }

  public Double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = NumberUtil.toReserve(inventoryAmount, NumberUtil.PRECISION);
  }

  public String getInventoryUnit() {
    return inventoryUnit;
  }

  public void setInventoryUnit(String inventoryUnit) {
    this.inventoryUnit = inventoryUnit;
  }

  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
