package com.bcgogo.enums.config;

public enum RecentlyUsedDataType {
  USED_SMS_CONTACT("最近发过短信的联系人"),//db中限制数量
  VISITED_PRODUCT("浏览过的商品"),//db中不限制数量
  VISITED_BUSINESS_CHANCE("浏览过的商机"),//db中不限制数量
  USED_PRODUCT_CATEGORY("使用过的商品分类");//db中限制数量  getRecentlyUsedProductCategoryNum

  private String status;

  private RecentlyUsedDataType(String status) {
    this.status = status;
  }

  public String getStatus() {
    return this.status;
  }
}
