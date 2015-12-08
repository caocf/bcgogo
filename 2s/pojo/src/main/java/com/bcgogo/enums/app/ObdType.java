package com.bcgogo.enums.app;

import com.bcgogo.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * User: lw
 * Date: 14-3-12
 * Time: 下午5:29
 */
public enum ObdType {
  BLUE_TOOTH,
  GSM,
  SGSM,
  POBD,
  MIRROR;
  private static Map<String, ObdType> obdTypeMap = new HashMap<String, ObdType>();

  static {
    obdTypeMap.put(BLUE_TOOTH.toString(), BLUE_TOOTH);
    obdTypeMap.put(GSM.toString(), GSM);
    obdTypeMap.put(MIRROR.toString(), MIRROR);
  }

  public static ObdType getObdTypeByStr(String str) {
    if (StringUtil.isEmpty(str)) return null;
    return obdTypeMap.get(str);
  }
}
