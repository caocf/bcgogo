package com.bcgogo.enums.config;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-9
 * Time: 下午8:32
 * To change this template use File | Settings | File Templates.
 */
public enum ServiceCategoryType {
  FIRST_CATEGORY("一级分类"),
  SECOND_CATEGORY("二级分类");
  String type;
  ServiceCategoryType(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }
}
