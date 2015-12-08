package com.bcgogo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-13
 * Time: 11:51
 */
public class StringUtil {
  private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);

  private static Pattern PATTERN_DIGITAL_ALPHA = Pattern.compile("^[A-Za-z\\d]+$");//字母数字

  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }

  /**
   * 判断字符串能否转为数值
   *
   * @param value
   * @return
   */
  public static boolean isNumber(String value) {
    if (StringUtil.isEmpty(value)) {
      return false;
    }
    try {
      Double.parseDouble(value);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  /**
   * 字符串为字母或数字
   * @param alphaOrDigital
   * @return
   */
  public static boolean isAlphaOrDigital(String alphaOrDigital) {
    return !isEmpty(alphaOrDigital) && PATTERN_DIGITAL_ALPHA.matcher(alphaOrDigital).matches();
  }

  /**
   * 比较当前的版本和服务器版本，如果当前版本小于服务器版本 return true ，表示要更新
   * @param currentVersion
   * @param lastVersion
   * @return
   */
    public static boolean compareVersion(String currentVersion, String lastVersion) {
      if (isEmpty(lastVersion)) {
        return false;
      }
      if (isEmpty(currentVersion)) {
        return true;
      }
      Pattern pattern1 = Pattern.compile("\\(");
      Pattern pattern2 = Pattern.compile("\\.");

      String[] currentVersionArr = pattern1.split(currentVersion, 0);
      String[] lastVersionArr = pattern1.split(lastVersion, 0);
      String[] currentVersionNumberStrArr = pattern2.split(currentVersionArr[0], 0);
      String[] lastVersionNumberStrArr = pattern2.split(lastVersionArr[0], 0);

      int currentLen = currentVersionNumberStrArr.length;
      int lastLen = lastVersionNumberStrArr.length;
      for (int i = 0; i < currentLen || i < lastLen; i++) {
        int currentVal = 0;
        int lastVal = 0;
        if (i < currentLen && NumberUtil.isNumber(currentVersionNumberStrArr[i])) {
          currentVal = Integer.parseInt(currentVersionNumberStrArr[i]);
        }
        if (i < lastLen && NumberUtil.isNumber(lastVersionNumberStrArr[i])) {
          lastVal = Integer.parseInt(lastVersionNumberStrArr[i]);
        }

        if (currentVal < lastVal) {
          return true;
        } else if (currentVal > lastVal) {
          return false;
        }
      }
      int currentVersionBuild = 0;
      int lastVersionBuild = 0;
      if (currentVersionArr.length > 1) {
        currentVersionBuild = NumberUtil.intValue(currentVersionArr[1].split("\\)")[0]);
      }
      if (lastVersionArr.length > 1) {
        lastVersionBuild = NumberUtil.intValue(lastVersionArr[1].split("\\)")[0]);
      }
      return currentVersionBuild < lastVersionBuild;
    }

}
