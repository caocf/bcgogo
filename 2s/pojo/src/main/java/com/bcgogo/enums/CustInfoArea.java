package com.bcgogo.enums;

import com.bcgogo.utils.BcgogoI18N;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Jimuchen
 * Date: 12-6-19
 * Time: 下午8:17
 */
public enum CustInfoArea {
  LOCAL,
  NONLOCAL;

  public static Map<CustInfoArea, String> getLocaleMap(Locale locale){
    Map<CustInfoArea, String> map = new LinkedHashMap<CustInfoArea, String>();
    CustInfoArea[] areas = CustInfoArea.values();
    for(CustInfoArea area:areas){
      map.put(area, BcgogoI18N.getMessageByKey("CustInfoArea_" + area.toString(), locale));
    }
    return map;
  }
}
