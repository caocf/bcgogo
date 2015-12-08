package com.bcgogo.product.ProductCategory;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.Product.ProductCategoryType;

/**
 * Created by IntelliJ IDEA.
 * User: 商品分类 搜索条件封装类
 * Date: 12-12-19
 * Time: 下午4:40
 * To change this template use File | Settings | File Templates.
 */
public class ProdCategorySearchCondition {

  private boolean hasPager=true;
  private int start;
  private int limit;
  private Long productCategoryId;  //产品分类id
  private String productCategoryName;     //产品分类名字
  private Long shopId;
  private ProductCategoryType productCategoryType;

  private Pager pager;

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    pager.setRowStart(start);
    pager.setPageSize(limit);
    this.pager = pager;
  }

  public ProductCategoryType getProductCategoryType() {
    return productCategoryType;
  }

  public void setProductCategoryType(ProductCategoryType productCategoryType) {
    this.productCategoryType = productCategoryType;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public boolean isHasPager() {
    return hasPager;
  }

  public void setHasPager(boolean hasPager) {
    this.hasPager = hasPager;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public String getProductCategoryName() {
    return productCategoryName;
  }

  public void setProductCategoryName(String productCategoryName) {
    this.productCategoryName = productCategoryName;
  }
}
