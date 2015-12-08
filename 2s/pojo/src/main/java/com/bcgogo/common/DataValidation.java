package com.bcgogo.common;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-23
 * Time: 下午3:15
 * To change this template use File | Settings | File Templates.
    */
public class DataValidation {
  public static boolean mobileValidation(String mobile) {

    if (mobile!=null&&!mobile.isEmpty() && mobile.substring(0, 1).equals("1") && mobile.indexOf("-") == -1 && mobile.length() == 11) {      return true;
    } else {
      return false;
    }
  }

}
