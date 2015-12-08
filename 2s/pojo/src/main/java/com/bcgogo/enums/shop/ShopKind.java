package com.bcgogo.enums.shop;

import com.bcgogo.enums.DataKind;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-21
 * Time: 下午1:17
 */
public enum ShopKind {
  TEST("测试店"),
  OFFICIAL("正式店");
  private String value;

  ShopKind(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static ShopKind dataKindMapping(DataKind dataKind) {
    return DataKind.TEST == dataKind ? TEST : OFFICIAL;
  }
}
