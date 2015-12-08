package com.bcgogo.enums.config;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:11
 * To change this template use File | Settings | File Templates.
 */
public enum DataType {
  PRODUCT("商品"),
  PRODUCT_HISTORY("商品历史"),
  SHOP("店铺"),
  CUSTOMER("客户"),
  APP_ENQUIRY("询价单"),
  APP_USER_BILL("账单"),
 ORDER("单据"),       //目前只有求购单的图片跟着单据

  SHOP_ADVERT("宣传"),
  RECOMMEND_TREE("店铺推广类目图"),
  SHOP_WX_MSG_IMAGE("店铺自定义微信消息图"),

  //
  ;

  private String value;

  private DataType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
