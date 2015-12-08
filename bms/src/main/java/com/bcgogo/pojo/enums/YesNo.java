package com.bcgogo.pojo.enums;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-29
 * Time: 上午9:37
 */
public enum YesNo {
  YES,
  NO;

  private static Map<String,YesNo> yesNoMap = new HashMap<String, YesNo>();

  static {
    for(YesNo yesNo : YesNo.values()){
      yesNoMap.put(yesNo.toString(), yesNo);
    }
  }



  public static YesNo convertYesNo(String yesNoStr){
    if(StringUtils.isNotBlank(yesNoStr)){
      return yesNoMap.get(yesNoStr);
    }
    return null;
  }
}
