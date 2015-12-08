package com.bcgogo.txn.model.recommend;

import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.SalesInventoryWeekStatDTO;
import com.bcgogo.txn.dto.recommend.ShopProductMatchResultDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 汽修版上周销量统计 品名+品牌作为唯一值
 */
@Entity
@Table(name = "shop_product_match_result")
public class ShopProductMatchResult extends LongIdentifier {
  private Long shopId;
  private Long matchedId;  //sales_inventory_week_id
  private String productName; //商品名称
  private String productBrand;//商品品牌
  private Double salesAmount;//销量
  private String salesUnit; //销售单位
  private Double inventoryAmount; //入库数量
  private String inventoryUnit; //入库单位
  private Long statTime; //统计时间
  private ShopKind shopKind;//店铺类型
  private Integer weekOfYear;//当年时间的周数
  private Integer statMonth;//月份
  private Integer statDay;//日期
  private Integer statYear;//年份



  @Column(name = "stat_month")
  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name = "stat_day")
  public Integer getStatDay() {
    return statDay;
  }

  public void setStatDay(Integer statDay) {
    this.statDay = statDay;
  }

  @Column(name = "stat_year")
  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "matched_id")
  public Long getMatchedId() {
    return matchedId;
  }

  public void setMatchedId(Long matchedId) {
    this.matchedId = matchedId;
  }

  @Column(name = "week_of_year")
  public Integer getWeekOfYear() {
    return weekOfYear;
  }

  public void setWeekOfYear(Integer weekOfYear) {
    this.weekOfYear = weekOfYear;
  }

  @Column(name = "product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name = "product_brand")
  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  @Column(name = "sales_amount")
  public Double getSalesAmount() {
    return salesAmount;
  }

  public void setSalesAmount(Double salesAmount) {
    this.salesAmount = salesAmount;
  }

  @Column(name = "sales_unit")
  public String getSalesUnit() {
    return salesUnit;
  }

  public void setSalesUnit(String salesUnit) {
    this.salesUnit = salesUnit;
  }

  @Column(name = "inventory_amount")
  public Double getInventoryAmount() {
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  @Column(name = "inventory_unit")
  public String getInventoryUnit() {
    return inventoryUnit;
  }

  public void setInventoryUnit(String inventoryUnit) {
    this.inventoryUnit = inventoryUnit;
  }

  @Column(name = "stat_time")
  public Long getStatTime() {
    return statTime;
  }

  public void setStatTime(Long statTime) {
    this.statTime = statTime;
  }

  @Column(name = "shop_kind")
  @Enumerated(EnumType.STRING)
  public ShopKind getShopKind() {
    return shopKind;
  }

  public void setShopKind(ShopKind shopKind) {
    this.shopKind = shopKind;
  }

  public ShopProductMatchResult fromDTO(ShopProductMatchResultDTO shopProductMatchResultDTO) {
    this.setId(shopProductMatchResultDTO.getId());
    this.setProductName(shopProductMatchResultDTO.getProductName());
    this.setProductBrand(shopProductMatchResultDTO.getProductBrand());
    this.setSalesAmount(shopProductMatchResultDTO.getSalesAmount());
    this.setSalesUnit(shopProductMatchResultDTO.getSalesUnit());
    this.setInventoryAmount(shopProductMatchResultDTO.getInventoryAmount());
    this.setInventoryUnit(shopProductMatchResultDTO.getInventoryUnit());
    this.setStatTime(shopProductMatchResultDTO.getStatTime());
    this.setShopKind(shopProductMatchResultDTO.getShopKind());
    this.setMatchedId(shopProductMatchResultDTO.getMatchedId());
    this.setWeekOfYear(shopProductMatchResultDTO.getWeekOfYear());
    this.setShopId(shopProductMatchResultDTO.getShopId());
    this.setStatYear(shopProductMatchResultDTO.getStatYear());
    this.setStatMonth(shopProductMatchResultDTO.getStatMonth());
    this.setStatDay(shopProductMatchResultDTO.getStatDay());
    return this;
  }

   public ShopProductMatchResult fromDTO(SalesInventoryWeekStatDTO salesInventoryWeekStatDTO) {
     this.setProductName(salesInventoryWeekStatDTO.getProductName());
     this.setProductBrand(salesInventoryWeekStatDTO.getProductBrand());
     this.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount());
     this.setSalesUnit(salesInventoryWeekStatDTO.getSalesUnit());
     this.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount());
     this.setInventoryUnit(salesInventoryWeekStatDTO.getInventoryUnit());
     this.setStatTime(salesInventoryWeekStatDTO.getStatTime());
     this.setShopKind(salesInventoryWeekStatDTO.getShopKind());
     this.setMatchedId(salesInventoryWeekStatDTO.getId());
     this.setWeekOfYear(salesInventoryWeekStatDTO.getWeekOfYear());
     this.setStatYear(salesInventoryWeekStatDTO.getStatYear());
     this.setStatMonth(salesInventoryWeekStatDTO.getStatMonth());
     this.setStatDay(salesInventoryWeekStatDTO.getStatDay());
     return this;
   }

  public ShopProductMatchResultDTO toDTO() {
    ShopProductMatchResultDTO shopProductMatchResultDTO = new ShopProductMatchResultDTO();
    shopProductMatchResultDTO.setId(this.getId());
    shopProductMatchResultDTO.setProductName(this.getProductName());
    shopProductMatchResultDTO.setProductBrand(this.getProductBrand());
    shopProductMatchResultDTO.setSalesAmount(this.getSalesAmount());
    shopProductMatchResultDTO.setSalesUnit(this.getSalesUnit());
    shopProductMatchResultDTO.setInventoryAmount(this.getInventoryAmount());
    shopProductMatchResultDTO.setInventoryUnit(this.getInventoryUnit());
    shopProductMatchResultDTO.setStatTime(this.getStatTime());
    shopProductMatchResultDTO.setShopKind(this.getShopKind());
    shopProductMatchResultDTO.setMatchedId(this.getMatchedId());
    shopProductMatchResultDTO.setWeekOfYear(this.getWeekOfYear());
    shopProductMatchResultDTO.setShopId(this.getShopId());
    shopProductMatchResultDTO.setStatYear(this.getStatYear());
    shopProductMatchResultDTO.setStatMonth(this.getStatMonth());
    shopProductMatchResultDTO.setStatDay(this.getStatDay());
    return shopProductMatchResultDTO;
  }

}