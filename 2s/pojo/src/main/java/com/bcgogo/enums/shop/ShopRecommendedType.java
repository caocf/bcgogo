package com.bcgogo.enums.shop;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-3
 * Time: 下午3:23
 */
public enum ShopRecommendedType {
  NOT_RECOMMENDED,
  NORMAL_RECOMMENDED;

  static List<String> RECOMMENDED;

  static {
    if (RECOMMENDED == null) {
      RECOMMENDED = new ArrayList<String>();
      RECOMMENDED.add(NORMAL_RECOMMENDED.toString());
    }
  }

  public static List<String> getRecommendedType() {
    return RECOMMENDED;
  }
}
