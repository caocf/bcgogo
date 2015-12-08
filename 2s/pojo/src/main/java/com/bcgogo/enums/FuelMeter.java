package com.bcgogo.enums;

import com.bcgogo.utils.BcgogoI18N;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Jimuchen
 * Date: 12-6-18
 * Time: 下午4:03
 */
public enum FuelMeter {
  FEW,
  ONEQT,
  HALF,
  THREEQT,
  FULL;

  public static Map<FuelMeter, String> getLocaleMap(Locale locale){
    Map<FuelMeter, String> map = new LinkedHashMap<FuelMeter, String>();
    FuelMeter[] meters = FuelMeter.values();
    for(FuelMeter meter:meters){
      map.put(meter, BcgogoI18N.getMessageByKey("FuelMeter_" + meter.toString(), locale));
    }
    return map;
  }
}
