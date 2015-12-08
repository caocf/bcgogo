package com.bcgogo.util;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-18
 * Time: 18:07
 */
public class NumberUtil {

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

  public static Integer intValue(Object numObj) {
      if (numObj==null||!isNumber(numObj.toString())) {
        return 0;
      }
      return Integer.parseInt(String.valueOf(numObj));
    }

}
