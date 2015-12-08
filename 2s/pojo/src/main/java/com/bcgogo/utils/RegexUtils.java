package com.bcgogo.utils;


import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-7
 * Time: 下午12:06
 */
public class RegexUtils {
  private static Pattern PATTERN_EMAIL = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");// 邮件地址
  private static Pattern PATTERN_TEL = Pattern.compile("^([0-9]{3,4}-)?[0-9]{7,8}$");// 固定电话
  /**
   * 2013-11-05 手机号校验添加14开头的，by qxy
   * 2015-03-25 手机号校验添加17开头的，by qxy
   */
  private static Pattern PATTERN_MOBILE = Pattern.compile("^(\\+86)?0?1[0|3|4|5|7|8]\\d{9,11}$");// 移动电话
  private static Pattern PATTERN_ALPHA = Pattern.compile("^[A-Za-z]+$");// 字母
  private static Pattern PATTERN_DIGITAL = Pattern.compile("^\\d+$");// 数字
  private static Pattern PATTERN_DIGITAL_ALPHA = Pattern.compile("^[A-Za-z\\d]+$");//字母数字
  private static Pattern PATTERN_CHINESE = Pattern.compile("^[\\u4E00-\\u9FA5]+$");// 中文
  /**
   * 1.常规车牌号：仅允许以汉字开头，后面可录入六个字符，由大写英文字母和阿拉伯数字组成。如：粤B12345；(^[\\u4E00-\\u9FA5][A-Z0-9]{6}$)
   * 2.武警车牌：允许前两位为大写英文字母，后面可录入七个字符，由大写英文字母和阿拉伯数字组成，其中第三位可录汉字也可录大写英文字母及阿拉伯数字，如：WJ01警0081、WJ0112345。 (^[A-Z]{2}[A-Z0-9]{2}[A-Z0-9\u4E00-\u9FA5][A-Z0-9]{4}$)
   * 3.最后一个为汉字的车牌：允许以汉字开头，后面可录入六个字符，前五位字符，由大写英文字母和阿拉伯数字组成，而最后一个字符为汉字，汉字包括“挂”、“学”、“警”、“军”、“港”、“澳”。如：粤Z1234港。(^[\u4E00-\u9FA5][A-Z0-9]{5}[挂学警军港澳]$)
   * 4.新军车牌：以两位为大写英文字母开头，后面以5位阿拉伯数字组成。如：BA12345。  (^[A-Z]{2}[0-9]{5}$)
   * 5.黑龙江车牌存在08或38开头的情况   (^(08|38)[A-Z0-9]{4}[A-Z0-9挂学警军港澳]$)
   * 6.警备2a32/京安2a32    (^[\\u4e00-\\u9fa5]{2}[a-zA-Z\\d]{4}$)
   * 7.苏0213E15（农用1）  江苏C13E12（农用2） (^[\\u4e00-\\u9fa5][a-zA-Z\\d]{7}$)|(^[\\u4e00-\\u9fa5]{2,3}[a-zA-Z][a-zA-Z\\d]{5}$)
   * 8.领A231C             (^\\u9886[a-zA-Z][a-zA-Z\\d]{4}$)
   * 9. WJ沪5005X,XH沪5005X 上海武警，校车(^[a-zA-Z]{2}[\\u4e00-\\u9fa5]{1}[a-zA-Z\\d]{5}$)
   */
  private static Pattern VEHICLE_NO = Pattern.compile("(^[\\u4E00-\\u9FA5][A-Z0-9]{6}$)|(^[A-Z]{2}[A-Z0-9]{2}[A-Z0-9\u4E00-\u9FA5][A-Z0-9]{4}$)|(^[\u4E00-\u9FA5][A-Z0-9]{5}[挂学警军港澳]$)|(^[A-Z]{2}[0-9]{5}$)|(^(08|38)[A-Z0-9]{4}[A-Z0-9挂学警军港澳]$)|(^[\\u4e00-\\u9fa5]{2}[a-zA-Z\\d]{4}$)|(^[\\u4e00-\\u9fa5][a-zA-Z\\d]{7}$)|(^[\\u4e00-\\u9fa5]{2,3}[a-zA-Z][a-zA-Z\\d]{5}$)|(^\\u9886[a-zA-Z][a-zA-Z\\d]{4}$)|(^[a-zA-Z]{2}[\\u4e00-\\u9fa5]{1}[a-zA-Z\\d]{5}$)");// 验车牌号
  private static Pattern SPECIFIC_CHAR = Pattern.compile("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]");// 特殊字符

  public RegexUtils() {
  }

  /**
   * 过滤非法字符
   */
  public static String format(String str) {
    return SPECIFIC_CHAR.matcher(str).replaceAll("").trim();
  }

  /**
   * 校验email格式
   *
   * @param email email
   */
  public static boolean isEmail(String email) {
    return email != null && PATTERN_EMAIL.matcher(email).matches();
  }

  public static boolean isNotEmail(String email) {
    return !isEmail(email);
  }

  public static boolean isTelephone(String telephone) {
    return telephone != null && PATTERN_TEL.matcher(telephone).matches();
  }

  public static boolean isMobile(String mobile) {
    return mobile != null && PATTERN_MOBILE.matcher(mobile.trim()).matches();
  }

  public static boolean isNotMobile(String mobile) {
    return !isMobile(mobile);
  }


  public static boolean isAlpha(String alpha) {
    return alpha != null && PATTERN_ALPHA.matcher(alpha).matches();
  }

  public static boolean isAlphaOrDigital(String alphaOrDigital) {
    return alphaOrDigital != null && PATTERN_DIGITAL_ALPHA.matcher(alphaOrDigital).matches();
  }

  public static boolean isDigital(String digital) {
    return digital != null && PATTERN_DIGITAL.matcher(digital).matches();
  }

  public static boolean isChinese(String chinese) {
    return chinese != null && PATTERN_CHINESE.matcher(chinese).matches();
  }

  public static boolean isNotVehicleNo(String vehicle) {
    return !isVehicleNo(vehicle);
  }
  public static boolean isVehicleNo(String vehicle) {
    return StringUtils.isNotBlank(vehicle) && VEHICLE_NO.matcher(vehicle).matches();
  }

  /**
   * 检验车架号
   */

  public static boolean isChassisNumber(String chassisNumber) {
    return !StringUtils.isBlank(chassisNumber) && Pattern.matches("^[A-Za-z\\d]+$", chassisNumber);

  }

  public static void main(String[] args) {
  }
}
