package com.bcgogo.txn.dto.recommend;

import com.bcgogo.product.dto.ProductDTO;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-11-9
 * Time: 上午9:47
 * 各个店铺所关心的商品每周销量、入库量统计 作为供求中心首页显示
 */
public class ShopProductMatchRecordDTO {
  private Long id;
  private Long shopId;
  private String seedProductName; //商品名称(种子)   中间数据 来源：销量前十的商品、注册填写的商品、经营范围的商品
  private String seedProductBrand;//商品品牌（种子） 中间数据 来源：销量前十的商品、注册填写的商品、经营范围的商品
  private Long productId;//匹配的种子里有可能是product_id(product表的id)
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

  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

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

  public ShopProductMatchRecordDTO(){

  }

  public ShopProductMatchRecordDTO(ProductDTO productDTO, SalesInventoryWeekStatDTO salesInventoryWeekStatDTO) {
    this.setShopId(productDTO.getShopId());
    this.setSeedProductName(productDTO.getName());
    this.setSeedProductBrand(productDTO.getBrand());

    this.setProductCategoryId(productDTO.getNormalProductId());
    this.setProductId(productDTO.getId());
    this.setProductLocalInfoId(productDTO.getProductLocalInfoId());

    this.setMatchedProductBrand(salesInventoryWeekStatDTO.getProductBrand());
    this.setMatchedProductName(salesInventoryWeekStatDTO.getProductName());
    this.setMatchedId(salesInventoryWeekStatDTO.getId());
    this.setMatchTime(salesInventoryWeekStatDTO.getStatTime());
    this.setWeekOfYear(salesInventoryWeekStatDTO.getWeekOfYear());
    this.setStatYear(salesInventoryWeekStatDTO.getStatYear());
    this.setStatMonth(salesInventoryWeekStatDTO.getStatMonth());
    this.setStatDay(salesInventoryWeekStatDTO.getStatDay());

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

  public String getSeedProductName() {
    return seedProductName;
  }

  public void setSeedProductName(String seedProductName) {
    this.seedProductName = seedProductName;
  }

  public String getSeedProductBrand() {
    return seedProductBrand;
  }

  public void setSeedProductBrand(String seedProductBrand) {
    this.seedProductBrand = seedProductBrand;
  }

  public String getMatchedProductName() {
    return matchedProductName;
  }

  public void setMatchedProductName(String matchedProductName) {
    this.matchedProductName = matchedProductName;
  }

  public String getMatchedProductBrand() {
    return matchedProductBrand;
  }

  public void setMatchedProductBrand(String matchedProductBrand) {
    this.matchedProductBrand = matchedProductBrand;
  }

  public Long getMatchedId() {
    return matchedId;
  }

  public void setMatchedId(Long matchedId) {
    this.matchedId = matchedId;
  }

  public Long getMatchTime() {
    return matchTime;
  }

  public void setMatchTime(Long matchTime) {
    this.matchTime = matchTime;
  }

  public Integer getWeekOfYear() {
    return weekOfYear;
  }

  public void setWeekOfYear(Integer weekOfYear) {
    this.weekOfYear = weekOfYear;
  }
}
