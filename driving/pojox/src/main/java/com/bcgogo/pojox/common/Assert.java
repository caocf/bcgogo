package com.bcgogo.pojox.common;


import com.bcgogo.pojox.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 上午11:28
 */
public class Assert extends org.springframework.util.Assert {


  public static void notEmpty(String str) {
    notEmpty(str, "[Assertion failed] - this string must not be empty");
  }


  public static void notEmpty(String str, String message) {
    if (StringUtil.isEmpty(str)) {
      throw new IllegalArgumentException(message);
    }
  }

}
