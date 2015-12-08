package com.bcgogo.common;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-2-15
 * Time: 下午7:38
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  public static Long nullToObject(Object t) {
    if (t != null) {
      if ("null".equals(t.toString())) {
        return null;
      } else if (t instanceof Long) {
        return (Long) t;
      }
    }
    return null;
  }
}
