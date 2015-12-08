package com.bcgogo.txn.model.recommend;

import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.SalesInventoryWeekStatDTO;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 汽修版上周销量统计 品名+品牌作为唯一值
 */
@Entity
@Table(name = "sales_inventory_week_stat")
public class SalesInventoryWeekStat extends LongIdentifier {
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

  public SalesInventoryWeekStat fromDTO(SalesInventoryWeekStatDTO salesInventoryWeekStatDTO) {
    this.setId(salesInventoryWeekStatDTO.getId());
    this.setProductName(salesInventoryWeekStatDTO.getProductName());
    this.setProductBrand(StringUtil.isEmpty(salesInventoryWeekStatDTO.getProductBrand()) ? null : salesInventoryWeekStatDTO.getProductBrand());
    this.setSalesAmount(salesInventoryWeekStatDTO.getSalesAmount());
    this.setSalesUnit(salesInventoryWeekStatDTO.getSalesUnit());
    this.setInventoryAmount(salesInventoryWeekStatDTO.getInventoryAmount());
    this.setInventoryUnit(salesInventoryWeekStatDTO.getInventoryUnit());
    this.setStatTime(salesInventoryWeekStatDTO.getStatTime());
    this.setShopKind(salesInventoryWeekStatDTO.getShopKind());
    this.setWeekOfYear(salesInventoryWeekStatDTO.getWeekOfYear());
    this.setStatYear(salesInventoryWeekStatDTO.getStatYear());
    this.setStatMonth(salesInventoryWeekStatDTO.getStatMonth());
    this.setStatDay(salesInventoryWeekStatDTO.getStatDay());
    return this;
  }

  public SalesInventoryWeekStatDTO toDTO() {
    SalesInventoryWeekStatDTO salesInventoryWeekStatDTO = new SalesInventoryWeekStatDTO();
    salesInventoryWeekStatDTO.setId(this.getId());
    salesInventoryWeekStatDTO.setProductName(this.getProductName());
    salesInventoryWeekStatDTO.setProductBrand(this.getProductBrand());
    salesInventoryWeekStatDTO.setSalesAmount(this.getSalesAmount());
    salesInventoryWeekStatDTO.setSalesUnit(this.getSalesUnit());
    salesInventoryWeekStatDTO.setInventoryAmount(this.getInventoryAmount());
    salesInventoryWeekStatDTO.setInventoryUnit(this.getInventoryUnit());
    salesInventoryWeekStatDTO.setStatTime(this.getStatTime());
    salesInventoryWeekStatDTO.setShopKind(this.getShopKind());
    salesInventoryWeekStatDTO.setWeekOfYear(this.getWeekOfYear());
    salesInventoryWeekStatDTO.setStatYear(this.getStatYear());
    salesInventoryWeekStatDTO.setStatMonth(this.getStatMonth());
    salesInventoryWeekStatDTO.setStatDay(this.getStatDay());
    return salesInventoryWeekStatDTO;
  }

}