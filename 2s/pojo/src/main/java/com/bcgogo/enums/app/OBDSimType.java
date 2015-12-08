package com.bcgogo.enums.app;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-6-19.
 */
public enum OBDSimType {
  SINGLE_GSM_OBD,   //老一代obd
  SINGLE_GSM_POBD, //彭奥迪的obd
  SINGLE_GSM_SOBD, //彭奥迪的obd
  SINGLE_MIRROR_OBD,
  SINGLE_SIM,
  COMBINE_GSM_OBD_SIM,
  COMBINE_GSM_POBD_SIM,
  COMBINE_MIRROR_OBD_SIM,
  COMBINE_GSM_OBD_SSIM,
  SINGLE_BLUETOOTH_OBD;

  private static Map<String,OBDSimType> obdSimTypeMap = new HashMap<String, OBDSimType>();

  static {
    for(OBDSimType obdSimType : OBDSimType.values()){
      obdSimTypeMap.put(obdSimType.toString(),obdSimType);
    }
  }

  public static OBDSimType[] convertObdSimTypes(String[] obdSimTypeStrArr) {
    if (!ArrayUtils.isEmpty(obdSimTypeStrArr)) {
      Set<OBDSimType> obdSimTypeSet = new HashSet<OBDSimType>();
      for (String obdSimStatusStr : obdSimTypeStrArr) {
        if (StringUtils.isNotBlank(obdSimStatusStr)) {
          if (obdSimTypeMap.get(obdSimStatusStr) != null) {
            obdSimTypeSet.add(obdSimTypeMap.get(obdSimStatusStr));
          }
        }
      }
      if (CollectionUtils.isNotEmpty(obdSimTypeSet)) {
        return obdSimTypeSet.toArray(new OBDSimType[obdSimTypeSet.size()]);
      }
    }
    return null;
  }

  public static OBDSimType convertObdSimType(String obdSimTypeStr){
    if(StringUtils.isNotBlank(obdSimTypeStr)){
      return obdSimTypeMap.get(obdSimTypeStr);
    }
    return null;
  }
}
