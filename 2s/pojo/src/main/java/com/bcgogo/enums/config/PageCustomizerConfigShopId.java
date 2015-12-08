package com.bcgogo.enums.config;

/**
 * User: ZhangJuntao
 * Date: 13-5-27
 * Time: 上午9:22
 */
public enum PageCustomizerConfigShopId {
  FULL(-1l),
  DEFAULT(0l);

  Long value;

  PageCustomizerConfigShopId(Long value) {
    this.value = value;
  }

  public Long getValue() {
    return this.value;
  }

  public static boolean isNotSystemShopId(Long shopId) {
    return !FULL.getValue().equals(shopId) && !DEFAULT.getValue().equals(shopId);
  }
}
