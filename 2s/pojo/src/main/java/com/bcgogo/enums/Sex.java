package com.bcgogo.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-25
 * Time: 上午10:44
 * To change this template use File | Settings | File Templates.
 */
public enum Sex {
  MALE("男"),    //男性
  FEMALE("女");   //女性

  String name;

  Sex(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  private static Map<String, Sex> lookup = new HashMap<String, Sex>();
  static{
    for(Sex sex : Sex.values()){
      lookup.put(sex.getName(), sex);
    }
  }

  public static Sex parseName(String sexStr) {
    Sex sex = lookup.get(sexStr);
    return sex;
  }
}
