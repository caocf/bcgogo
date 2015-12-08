package com.bcgogo.pwd;

import liquibase.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;

/**
 * 加密工具类
 * User: Jimuchen
 * Date: 12-10-11
 * Time: 上午12:05
 */
public class EncryptionUtil {
  private static final Logger LOG = LoggerFactory.getLogger(EncryptionUtil.class);

  /**
   * 使用MD5加密，原先代码中直接使用了Liquibase提供的MD5Util。
   * @param source
   * @return
   */
  public static String computeMD5(String source) {
    return MD5Util.computeMD5(source);
  }

  /**
   * MD5Util提供的方法得到的结果长度并不是标准的32位，因为byte小于16时转为16进制字符时Java默认不带前缀0
   * 例： 0x0d 输出为 d 而不是 0d
   * 但是，此方法基本不会用到，因为数据库中的密码已经使用MD5Util生成，为保持一致性，加密MD5时仍使用computeMD5方法
   * @param source
   * @return
   */
  protected static String computeMD5Improved(String source){
    MessageDigest md;
    try{
      md = MessageDigest.getInstance("MD5");
      md.update(source.getBytes());
    }catch(Exception e){
      LOG.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
    byte byteData[] = md.digest();

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  /**
   * 使用SHA-256加密
   * @param source
   * @return
   */
  public static String computeSHA256(String source) {
    MessageDigest md;
    try{
      md = MessageDigest.getInstance("SHA-256");
      md.update(source.getBytes());
    }catch(Exception e){
      LOG.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
    byte byteData[] = md.digest();

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  /**
   * 原始的密码先使用MD5加密，与shopId组合后，再使用SHA256加密，得到数据库中存储的加密值
   * @param pwd
   * @return
   */
  public static String encryptPassword(String pwd, Long shopId) throws IllegalArgumentException{
    if(StringUtils.isBlank(pwd)){
      throw new IllegalArgumentException("创建密码时密码为空！");
    }
    if(shopId == null){
      throw new IllegalArgumentException("创建密码时shopId为空！");
    }
    return computeSHA256(computeMD5(pwd) + shopId);
  }
}
