package com.bcgogo.enums.Product;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-27
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
public enum SearchInputType {
  PRODUCT_NAME("产品名称查询框"),
  BRAND("品牌查询框"),
  SPEC("规格查询框"),
  MODEL("型号查询框"),
  VEHICLE_BRAND("车辆品牌查询框"),
  VEHICLE_MODEL("车型查询框"),
  COMMODITY_CODE("产品编码查询框");

  String type;

  SearchInputType(String type) {
    this.type = type;
  }

  public String getType() {
    return this.type;
  }
}
