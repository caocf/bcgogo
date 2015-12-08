package com.bcgogo.product.productManage;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.ProductStatus;

/**
 * product DB 查询条件
 * User: terry
 * Date: 13-8-19
 * Time: 上午9:59
 */
public class ProductQuery {

  private Pager pager;
  private ProductStatus productStatus;

  public Pager getPager() {
    return pager;
  }

  public void setPager(Pager pager) {
    this.pager = pager;
  }

  public ProductStatus getProductStatus() {
    return productStatus;
  }

  public void setProductStatus(ProductStatus productStatus) {
    this.productStatus = productStatus;
  }


}
