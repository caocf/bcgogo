package com.bcgogo.pojox.enums.app;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-6-16.
 */
public enum OBDStatus {
  UN_ASSEMBLE("未组装"),
  WAITING_OUT_STORAGE("待出库"),
  PICKED("已领出"),
  AGENT("已代理"),
  ON_SELL("销售中"),//对于店铺来说，待入口
  SOLD("已售出"),   //对于店铺来说，已安装到车上
  DISABLED("已删除"),
  ;
  private final String name;

  private OBDStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  private static Map<String,OBDStatus> OBDStatusMap = new HashMap<String, OBDStatus>();

  public static Set<OBDStatus> EnabledStatusSet = new HashSet<OBDStatus>();

  public static Set<OBDStatus> returnableStatusSet = new HashSet<OBDStatus>();

  public static OBDStatus[] EnabledStatusArr ;
  public static String[] EnabledStatusStrArr ;

  public static Set<OBDStatus> EditStatusSet = new HashSet<OBDStatus>();


  static {
    for(OBDStatus obdStatus : OBDStatus.values()){
      OBDStatusMap.put(obdStatus.toString(),obdStatus);
    }

    EnabledStatusSet.add(UN_ASSEMBLE);
    EnabledStatusSet.add(WAITING_OUT_STORAGE);
    EnabledStatusSet.add(PICKED);
    EnabledStatusSet.add(AGENT);
    EnabledStatusSet.add(ON_SELL);
    EnabledStatusSet.add(SOLD);

    EnabledStatusArr = new OBDStatus[6];
    EnabledStatusArr[0] = UN_ASSEMBLE;
    EnabledStatusArr[1] = WAITING_OUT_STORAGE;
    EnabledStatusArr[2] = PICKED;
    EnabledStatusArr[3] = AGENT;
    EnabledStatusArr[4] = ON_SELL;
    EnabledStatusArr[5] = SOLD;

    EnabledStatusStrArr = new String[6];
    EnabledStatusStrArr[0] = UN_ASSEMBLE.toString();
    EnabledStatusStrArr[1] = WAITING_OUT_STORAGE.toString();
    EnabledStatusStrArr[2] = PICKED.toString();
    EnabledStatusStrArr[3] = AGENT.toString();
    EnabledStatusStrArr[4] = ON_SELL.toString();
    EnabledStatusStrArr[5] = SOLD.toString();




    EditStatusSet.add(UN_ASSEMBLE);
    EditStatusSet.add(WAITING_OUT_STORAGE);
    EditStatusSet.add(PICKED);
    EditStatusSet.add(AGENT);
    EditStatusSet.add(ON_SELL);

    returnableStatusSet.add(PICKED);
    returnableStatusSet.add(AGENT);
    returnableStatusSet.add(ON_SELL);

  }

  public static OBDStatus convertOBDStatus(String obdStatusStr) {
    if (StringUtils.isEmpty(obdStatusStr)) {
      return null;
    }
    return OBDStatusMap.get(obdStatusStr);
  }

  public static OBDStatus[] convertOBDStatus(String[] obdSimStatusStrArr) {
    if (!ArrayUtils.isEmpty(obdSimStatusStrArr)) {
      Set<OBDStatus> OBDStatusSet = new HashSet<OBDStatus>();
      for (String obdSimStatusStr : obdSimStatusStrArr) {
        if (StringUtils.isNotBlank(obdSimStatusStr)) {
          if (OBDStatusMap.get(obdSimStatusStr) != null) {
            OBDStatusSet.add(OBDStatusMap.get(obdSimStatusStr));
          }
        }
      }
      if (CollectionUtils.isNotEmpty(OBDStatusSet)) {
        return OBDStatusSet.toArray(new OBDStatus[OBDStatusSet.size()]);
      }
    }
    return null;
  }

  public static String[] filterOBDStatus(String[] obdSimStatusStrArr) {
    if (!ArrayUtils.isEmpty(obdSimStatusStrArr)) {
      Set<String> OBDStatusSet = new HashSet<String>();
      for (String obdSimStatusStr : obdSimStatusStrArr) {
        if (StringUtils.isNotBlank(obdSimStatusStr)) {
          if (OBDStatusMap.get(obdSimStatusStr) != null) {
            OBDStatusSet.add(OBDStatusMap.get(obdSimStatusStr).toString());
          }
        }
      }
      if (CollectionUtils.isNotEmpty(OBDStatusSet)) {
        return OBDStatusSet.toArray(new String[OBDStatusSet.size()]);
      }
    }
    return null;
  }

}
