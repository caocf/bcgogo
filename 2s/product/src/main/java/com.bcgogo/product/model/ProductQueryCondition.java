package com.bcgogo.product.model;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.ProductStatus;

/**
 * 产品查询的条件类，需要进行动态的查询 请添加字段和修改相应的SQL文件里面的方法
 * User: terry
 * Date: 13-8-9
 * Time: 下午1:59
 */
public class ProductQueryCondition {

  // 店铺Id
  private Long shopId;
  // 产品状态
  private ProductStatus productStatus;
  // 分页
  private Pager pager;

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public ProductStatus getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }

  @Override
  public String toString() {
    return "ProductQueryCondition{" +
        "shopId=" + shopId +
        ", productStatus=" + productStatus +
        '}';
  }
}
