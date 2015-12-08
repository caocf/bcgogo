package com.bcgogo.enums;

import com.bcgogo.utils.BcgogoI18N;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Jimuchen
 * Date: 12-6-19
 * Time: 下午8:21
 */
public enum CustomerTypes {
  NORMAL("普通"),
  UNIT("单位"),
  BIG("大客户"),
  OTHER("其他");
  private final String name;
  private CustomerTypes(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }


}
