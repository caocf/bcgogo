package com.bcgogo.enums.user;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-31
 * Time: 上午10:31
 */
public enum ResourceSuffix {
  SHOP_VERSION_BASE("_BASE");
  private String value;

  ResourceSuffix(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
