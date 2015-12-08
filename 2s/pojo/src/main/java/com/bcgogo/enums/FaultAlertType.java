package com.bcgogo.enums;

import com.bcgogo.enums.app.GsmPointType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 下午4:32
 */
public enum FaultAlertType {
  FAULT_CODE("故障"), //车辆故障码
  JX_ALERT("剪线"),   //剪线报警
  WY_ALERT("位移"),  //位移报警
  ZD_ALERT("震动"),  //震动报警
  OUT_ALERT("围栏"), //围栏报警
  PZ_ALERT("碰撞"),  //碰撞报警
  LPD_ALERT("低电压"); //低电压报警

  private String value;

  FaultAlertType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public static Map<GsmPointType, FaultAlertType> gsmPointType2FaultAlertTypeMap;

  public static Map<FaultAlertType, String> faultAlertTypeStringMap;

    static {
      if (gsmPointType2FaultAlertTypeMap == null) {
        gsmPointType2FaultAlertTypeMap = new HashMap<GsmPointType, FaultAlertType>();
        gsmPointType2FaultAlertTypeMap.put(GsmPointType.JX,JX_ALERT);
        gsmPointType2FaultAlertTypeMap.put(GsmPointType.WY,WY_ALERT);
        gsmPointType2FaultAlertTypeMap.put(GsmPointType.ZD,ZD_ALERT);
        gsmPointType2FaultAlertTypeMap.put(GsmPointType.OUT,OUT_ALERT);
        gsmPointType2FaultAlertTypeMap.put(GsmPointType.PZ,PZ_ALERT);
        gsmPointType2FaultAlertTypeMap.put(GsmPointType.LPD,LPD_ALERT);
      }
      if (faultAlertTypeStringMap == null) {
        faultAlertTypeStringMap = new HashMap<FaultAlertType, String>();
        faultAlertTypeStringMap.put(JX_ALERT,"您的车辆掉主电报警，请关注！");
        faultAlertTypeStringMap.put(WY_ALERT, "您的车辆被非法移动，自动撤防！");
        faultAlertTypeStringMap.put(ZD_ALERT, "有异常震动，请立即检查！");
        faultAlertTypeStringMap.put(OUT_ALERT,"您的车辆离开电子围栏！");
        faultAlertTypeStringMap.put(PZ_ALERT,"您的车辆发生碰撞！");
        faultAlertTypeStringMap.put(LPD_ALERT,"注意：剩余电量不足，请立即充电!");
      }


    }

    public static FaultAlertType parseFromGsmPointStr(GsmPointType gsmPointType) {
      if (gsmPointType2FaultAlertTypeMap != null) {
        return gsmPointType2FaultAlertTypeMap.get(gsmPointType);
      }
      return null;
    }

  public static String getContent(FaultAlertType faultAlertType) {
    if (faultAlertTypeStringMap != null) {
      return faultAlertTypeStringMap.get(faultAlertType);
    }
    return null;
  }
}
