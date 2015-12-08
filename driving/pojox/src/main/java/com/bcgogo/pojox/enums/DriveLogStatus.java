package com.bcgogo.pojox.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-10
 * Time: 下午3:52
 */
public enum DriveLogStatus {
  ENABLED,DISABLED,DRIVING;

  public static Map<String,DriveLogStatus> driveLogStatusMap ;
   static {
       if(driveLogStatusMap == null){
         driveLogStatusMap = new HashMap<String, DriveLogStatus>();
         for(DriveLogStatus driveLogStatus : DriveLogStatus.values()){
           driveLogStatusMap.put(driveLogStatus.name(),driveLogStatus);
         }
       }
   }

  public static DriveLogStatus parseValue(String statusStr) {
    if (driveLogStatusMap != null) {
      return driveLogStatusMap.get(statusStr);
    }
    return null;
  }
}
