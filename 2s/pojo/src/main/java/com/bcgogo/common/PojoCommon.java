package com.bcgogo.common;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-4-7
 * Time: 上午11:23
 * To change this template use File | Settings | File Templates.
 */
public class PojoCommon {
   public static String toJsonStr(String s) {
    if (StringUtils.isBlank(s) || s.equalsIgnoreCase("null")) {
      return "";
    } else return s;
  }

  public static String toJsonStr(Integer i) {
    if (i == null) {
      return "";
    } else {
      return i.toString();
    }
  }

  public static String toJsonStr(Double d) {
    if (d == null) {
      return "";
    } else {
      return d.toString();
    }
  }
    public static String toJsonStr(Long l) {
    if (l == null) {
      return "";
    } else {
      return l.toString();
    }
  }
    public static String toJsonStr(Float f) {
    if (f == null) {
      return "";
    } else {
      return f.toString();
    }
  }
}
