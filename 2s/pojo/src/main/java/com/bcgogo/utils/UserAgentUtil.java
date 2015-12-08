package com.bcgogo.utils;

/**
 * 分析移动端设备信息
 * Author: ndong
 * Date: 14-11-18
 * Time: 下午5:30
 */
public class UserAgentUtil {

  /**
   * android终端或uc浏览器
   * @param userAgent
   * @return
   */
  public static boolean android(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("Android") > -1 || userAgent.indexOf("Linux") > -1;
  }
  /**
   * 是否为iPhone或者QQHD浏览器
   * @param userAgent
   * @return
   */
  public static boolean iPhone(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("iPhone") > -1;
  }
  /**
   * 是否iPad
   * @param userAgent
   * @return
   */
  public static boolean iPad(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("iPad") > -1;
  }

  /**
   * 苹果、谷歌内核
   * @param userAgent
   * @return
   */
  public static boolean isWebKit(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("AppleWebKit") > -1;
  }

  /**
   * IE内核
   * @param userAgent
   * @return
   */
  public static boolean isTrident(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("Trident") > -1;
  }

  /**
   * 火狐内核
   * @param userAgent
   * @return
   */
  public static boolean isGecko(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("Gecko") > -1 && userAgent.indexOf("KHTML") == -1;
  }

//  /**
//   * 是否为移动终端
//   * @param userAgent
//   * @return
//   */
//  public static boolean isMobile(String userAgent){
//   if(StringUtil.isEmpty(userAgent)) return false;
//    return !!userAgent.match(/AppleWebKit.*Mobile.*/);
//  }

  /**
   * ios终端
   * @param userAgent
   * @return
   */
//  public static boolean ios(String userAgent){
//   if(StringUtil.isEmpty(userAgent)) return false;
//    return !!userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
//  }

  /**
   * 是否web应该程序，没有头部与底部
   * @param userAgent
   * @return
   */
  public static boolean isWebApp(String userAgent){
    if(StringUtil.isEmpty(userAgent)) return false;
    return userAgent.indexOf("Safari") > -1;
  }

}
