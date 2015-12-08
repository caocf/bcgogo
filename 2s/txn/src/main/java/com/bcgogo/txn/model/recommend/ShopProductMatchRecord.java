package com.bcgogo.txn.model.recommend;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.recommend.ShopProductMatchRecordDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 各个店铺所关心的商品每周销量、入库量统计 作为供求中心首页显示
 */
@Entity
@Table(name = "shop_product_match_record")
public class ShopProductMatchRecord extends LongIdentifier {
  private Long shopId;
  private String seedProductName; //商品名称(种子)   中间数据 来源：销量前十的商品、注册填写的商品、经营范围的商品
  private String seedProductBrand;//商品品牌（种子） 中间数据 来源：销量前十的商品、注册填写的商品、经营范围的商品
  private Long productId;//匹配的种子里有可能是product_id
  private Long productLocalInfoId;//匹配的种子的product_local_info表的id
  private Long productCategoryId;//匹配的种子里有可能是product_category_id(三级分类)

  private String matchedProductName; //匹配结果的商品名称 sales_inventory_week_stat表中的
  private String matchedProductBrand;//匹配结果的商品名称  sales_inventory_week_stat表中的
  private Long matchedId;//匹配到id sales_inventory_week_stat_id
  private Long matchTime; //匹配时间
  private Integer weekOfYear;   //周数

  private Integer statMonth;//月份
  private Integer statDay;//日期
  private Integer statYear;//年份

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  @Column(name = "product_category_id")
  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

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

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "seed_product_name")
  public String getSeedProductName() {
    return seedProductName;
  }

  public void setSeedProductName(String seedProductName) {
    this.seedProductName = seedProductName;
  }

  @Column(name = "seed_product_brand")
  public String getSeedProductBrand() {
    return seedProductBrand;
  }

  public void setSeedProductBrand(String seedProductBrand) {
    this.seedProductBrand = seedProductBrand;
  }

  @Column(name = "matched_product_name")
  public String getMatchedProductName() {
    return matchedProductName;
  }

  public void setMatchedProductName(String matchedProductName) {
    this.matchedProductName = matchedProductName;
  }

  @Column(name = "matched_product_brand")
  public String getMatchedProductBrand() {
    return matchedProductBrand;
  }

  public void setMatchedProductBrand(String matchedProductBrand) {
    this.matchedProductBrand = matchedProductBrand;
  }

  @Column(name = "matched_id")
  public Long getMatchedId() {
    return matchedId;
  }

  public void setMatchedId(Long matchedId) {
    this.matchedId = matchedId;
  }

  @Column(name = "match_time")
  public Long getMatchTime() {
    return matchTime;
  }

  public void setMatchTime(Long matchTime) {
    this.matchTime = matchTime;
  }


  public ShopProductMatchRecordDTO toDTO() {
    ShopProductMatchRecordDTO recordDTO = new ShopProductMatchRecordDTO();
    recordDTO.setShopId(getShopId());
    recordDTO.setId(getId());
    recordDTO.setSeedProductBrand(getSeedProductBrand());
    recordDTO.setSeedProductName(getSeedProductName());
    recordDTO.setMatchedId(getMatchedId());
    recordDTO.setMatchedProductName(getMatchedProductName());
    recordDTO.setMatchedProductBrand(getMatchedProductBrand());
    recordDTO.setWeekOfYear(getWeekOfYear());
    recordDTO.setMatchTime(getMatchTime());
    recordDTO.setStatYear(getStatYear());
    recordDTO.setStatMonth(getStatMonth());
    recordDTO.setStatDay(getStatDay());
    recordDTO.setProductId(getProductId());
    recordDTO.setProductCategoryId(getProductCategoryId());
    recordDTO.setProductLocalInfoId(getProductLocalInfoId());
    return recordDTO;
  }

  public ShopProductMatchRecord fromDTO(ShopProductMatchRecordDTO recordDTO) {
    this.setShopId(recordDTO.getShopId());
    this.setId(recordDTO.getId());
    this.setSeedProductBrand(recordDTO.getSeedProductBrand());
    this.setSeedProductName(recordDTO.getSeedProductName());
    this.setMatchedId(recordDTO.getMatchedId());
    this.setMatchedProductName(recordDTO.getMatchedProductName());
    this.setMatchedProductBrand(recordDTO.getMatchedProductBrand());
    this.setWeekOfYear(recordDTO.getWeekOfYear());
    this.setMatchTime(recordDTO.getMatchTime());
    this.setStatYear(recordDTO.getStatYear());
    this.setStatMonth(recordDTO.getStatMonth());
    this.setStatDay(recordDTO.getStatDay());
    this.setProductCategoryId(recordDTO.getProductCategoryId());
    this.setProductId(recordDTO.getProductId());
    this.setProductLocalInfoId(recordDTO.getProductLocalInfoId());

    return this;
  }
}