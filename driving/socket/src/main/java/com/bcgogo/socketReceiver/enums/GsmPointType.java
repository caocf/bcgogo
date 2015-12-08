package com.bcgogo.socketReceiver.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 上午10:46
 */
public enum GsmPointType {
  AUT,
  JX,
  WY,
  ZD,
  OUT,
  PZ,
  LPD;

  public static Map<String, GsmPointType> gsmPointTypeMap;

  static {
    if (gsmPointTypeMap == null) {
      gsmPointTypeMap = new HashMap<String, GsmPointType>();
      for (GsmPointType gsmPointType : GsmPointType.values()) {
        gsmPointTypeMap.put(gsmPointType.name(), gsmPointType);
      }
    }
  }

  public static GsmPointType parseValue(String statusStr) {
    if (gsmPointTypeMap != null) {
      return gsmPointTypeMap.get(statusStr);
    }
    return null;
  }
}
