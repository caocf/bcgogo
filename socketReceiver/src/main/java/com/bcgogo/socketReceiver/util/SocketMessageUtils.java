package com.bcgogo.socketReceiver.util;

import org.apache.commons.lang.StringUtils;

/**
 * User: ZhangJuntao
 * Date: 14-3-6
 * Time: 下午1:28
 */
public class SocketMessageUtils {
  public static String trim(String info) {
    if (StringUtils.isBlank(info)) return "";
    info = StringUtils.trim(info);
    while (info.startsWith("#")) {
      info = info.substring(1, info.length());
      if (StringUtils.isBlank(info)) return "";
    }
    while (info.endsWith("#")) {
      info = info.substring(0, info.length() - 1);
      if (StringUtils.isBlank(info)) return "";
    }
    return stripControlChars(info);
  }

  /**
   * Function to strip control characters from a string.
   * Any character below a space will be stripped from the string.
   *
   * @param iString the input string to be stripped.
   * @return a string containing the characters from iString minus any control characters.
   */
  public static String stripControlChars(String iString) {
    return iString.replaceAll("[^\\p{Print}]", "");
//    StringBuilder result = new StringBuilder(iString);
//    int idx = result.length();
//    while (idx-- > 0) {
//      if (result.charAt(idx) < 0x20 && result.charAt(idx) != 0x9 &&
//          result.charAt(idx) != 0xA && result.charAt(idx) != 0xD) {
//        result.deleteCharAt(idx);
//      }
//    }
//    return result.toString();
  }

  public static void main(String[] args) {
    System.out.println(stripControlChars("#<Load: 2.0%,ECT: -25ßC,SHRTFT1: 76.6"));
  }

}
