package com.bcgogo.enums.app;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by XinyuQiu on 14-7-14.
 */
public enum ObdReturnMsg {
  RETURN_SAMPLE("样品归还"),
  CHANGE_DEFECTIVE("残次品换货"),
  RETURN_STORAGE("退货");

  private final String name;

  private ObdReturnMsg(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Map<String,ObdReturnMsg> obdReturnMsgMap;
  static {
    obdReturnMsgMap = new HashMap<String, ObdReturnMsg>();
    for(ObdReturnMsg obdReturnMsg : ObdReturnMsg.values()){
      obdReturnMsgMap.put(obdReturnMsg.name(),obdReturnMsg);
    }

  }

  public static ObdReturnMsg getEnumByName(String enumName){
    if(StringUtils.isNotEmpty(enumName)){
      return obdReturnMsgMap.get(enumName);
    }
    return null;
  }

}
