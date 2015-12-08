package com.bcgogo.common;

import com.bcgogo.exception.BcgogoException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CommonUtil {


  public static boolean isDevMode() {
    String dev = System.getProperty("dev.mode");
    return dev != null && dev.equals("true");
  }

  public static <T> T first(Collection<T> coll) {
    if (coll == null || coll.isEmpty()) return null;
    return coll.iterator().next();
  }

  public static void reThrow(Logger logger, Exception e) throws Exception {
    if (logger.isDebugEnabled()) {
      logger.error(e.getMessage(), e);
    }
    throw new BcgogoException(e);
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

  public static Set<Long> convertToLong(String str, String regex) {
    Set<Long> ids = new HashSet<Long>();
    if (StringUtils.isBlank(str)) {
      return ids;
    }
    String[] strings = str.split(regex);
    for (String temStr : strings) {
      if (NumberUtils.isNumber(temStr)) {
        try {
          ids.add(Long.parseLong(temStr));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return ids;
  }
}
