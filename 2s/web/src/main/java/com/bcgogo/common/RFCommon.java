package com.bcgogo.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class RFCommon {

  public static void reThrow(Logger logger, Exception e) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.error(e.getMessage(), e);
    }
    throw e;
  }//todo luyi 页面显示错误信息

  private static Object getSeesion(HttpServletRequest request, String key) throws Exception {
    Object value = request.getSession().getAttribute(key);
    if (value == null) {
      throw new Exception(key + " == null");
    }
    return value;
  }

  public static Long getShopId(HttpServletRequest request) throws Exception {
    return (Long) getSeesion(request, "shopId");
  }

  public static Long getUserId(HttpServletRequest request) throws Exception {
    return (Long) getSeesion(request, "userId");
  }

  public static String getUserName(HttpServletRequest request) throws Exception {
    return (String) getSeesion(request, "userName");
  }

  public static boolean isChinese(char c) {
    return c >= 0x0391 && c <= 0xFFE5;
  }

  public static Long parseLong(String str) {
    if (str == null) return null;
    str = str.trim();
    if (StringUtils.isBlank(str)) return null;
    try {
      return Long.parseLong(str);
    } catch (Exception e) {
      return null;
    }
  }

  public static Double parseDouble(String str) {
    if (str == null) return null;
    str = str.trim();
    if (StringUtils.isBlank(str)) return null;
    try {
      return Double.parseDouble(str);
    } catch (Exception e) {
      return null;
    }
  }

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
}
