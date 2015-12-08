package com.bcgogo.enums.Product;

/**
 * Created by IntelliJ IDEA.
 * User: LiuWei
 * Date: 12-12-18
 * Time: 下午3:58
 * To change this template use File | Settings | File Templates.
 */
public enum ProductCategoryType {
  TOP_CATEGORY("苏州统购"),
  FIRST_CATEGORY("一级分类"),
  SECOND_CATEGORY("二级分类"),
  THIRD_CATEGORY("三级分类");
  String type;

  ProductCategoryType(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }
}
