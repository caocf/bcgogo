package com.bcgogo.enums;

import com.bcgogo.utils.BcgogoI18N;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Jimuchen
 * Date: 12-6-19
 * Time: 下午8:31
 */
public enum CustInfoSettleType {
  CASH,
  MONTH,
  ARRIVAL,
  QUARTER;

  public static Map<CustInfoSettleType, String> getLocaleMap(Locale locale){
    Map<CustInfoSettleType, String> map = new LinkedHashMap<CustInfoSettleType, String>();
    CustInfoSettleType[] types = CustInfoSettleType.values();
    for(CustInfoSettleType type:types){
      map.put(type, BcgogoI18N.getMessageByKey("CustInfoSettleType_" + type.toString(), locale));
    }
    return map;
  }
}
