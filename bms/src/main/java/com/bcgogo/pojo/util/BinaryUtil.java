package com.bcgogo.pojo.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-18
 * Time: 下午4:08
 */
public class BinaryUtil {


  public static void main(String[] args) throws UnsupportedEncodingException {
//    test();
    testResp();
  }

  public static void testResp() throws UnsupportedEncodingException {
   String rep="*HQ,8636003725,V4,V1,20150915060259#";
    String checksum = checksum(rep);
        System.out.println(checksum);

  }

  public static void test() throws UnsupportedEncodingException {
    String src = "~*HQ,8636000652,V1,000444,V,3116.1355,N,12044.0039,E,000.00,000,010113,FFFBDFFF,ED,3000,460,01,17695,48516,C6#$~";
    String hexString = BinaryUtil.byte2HexString(src.getBytes());
    System.out.println(hexString);
    //去包头包尾
    hexString = hexString.substring(2, hexString.length() - 2);
    System.out.println(hexString2String(hexString));
    //转义还原
    hexString = hexString.replaceAll("7D01", "7D").replaceAll("7D02", "7E");
    System.out.println("转义还原:"+hexString2String(hexString));
    //去截取校验结果
    hexString=hexString.substring(0,hexString.length()-2);
    System.out.println("去截取校验结果:"+hexString2String(hexString));
    //验证校验码
    int tmp = hexChecksum(hexString)^'~';
    System.out.println((char)tmp);
  }


  public static char hexChecksum(String hexString) throws UnsupportedEncodingException {
    if (StringUtil.isEmpty(hexString) || hexString.length() % 2 != 0) {
      return '\0';
    }
    //取第一个字符
    char result = hexString2String(hexString.substring(0, 2)).charAt(0);
    for (int i = 2; i < hexString.length() ; i += 2) {
      char temp = hexString2String(hexString.substring(i, i + 2)).charAt(0);
      result ^= temp;
    }
    int tmp = result ^ '~';
    return (char) tmp;
  }

  public static String checksum(String str) {
    if (StringUtil.isEmpty(str)) return null;
    char result = str.charAt(0);
    for (int i = 1; i < str.length(); i++) {
      result ^= str.charAt(i);
    }
     int tmp = result ^ '~';
    return String.valueOf((char) tmp);
  }


  /**
   * 字符串 --> 16进制
   *
   * @param s
   * @return
   */
  public static String toHexString(String s) {
    String str = "";
    for (int i = 0; i < s.length(); i++) {
      int ch = (int) s.charAt(i);
      String s4 = Integer.toHexString(ch);
      str = str + s4;
    }
    return str.toUpperCase();
  }

  /**
   * byte --> 16进制字符串
   *
   * @param b
   * @return
   */
  public static String byte2HexString(byte[] b) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < b.length; i++) {
      String hex = Integer.toHexString(b[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      sb.append(hex);
    }
    return sb.toString().toUpperCase();
  }

  /**
   * 二进制 --> 16进制
   *
   * @param bString
   * @return
   */
  public static String binaryString2hexString(String bString) {
    if (bString == null || bString.equals("") || bString.length() % 8 != 0) {
      return null;
    }
    StringBuffer tmp = new StringBuffer();
    int iTmp = 0;
    for (int i = 0; i < bString.length(); i += 4) {
      iTmp = 0;
      for (int j = 0; j < 4; j++) {
        iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
      }
      tmp.append(Integer.toHexString(iTmp));
    }
    return tmp.toString();
  }

  /**
   * 16进制 --> 二进制Array
   *
   * @param hexString
   * @return
   */
  public static String[] hexString2binaryArray(String hexString) {
    String binStr = hexString2binaryString(hexString);
    if (StringUtil.isEmpty(binStr)) return null;
    String[] result = new String[binStr.length() / 8];
    for (int i = 0, count = 0; i < binStr.length(); i += 8, count++) {
      result[count] = binStr.substring(i, i + 8);
    }
    return result;
  }

  /**
   * 16进制 --> 二进制
   *
   * @param hexString
   * @return
   */
  public static String hexString2binaryString(String hexString) {
    if (hexString == null || hexString.length() % 2 != 0) {
      return null;
    }
    String bString = "", tmp;
    for (int i = 0; i < hexString.length(); i++) {
      tmp = "0000"
        + Integer.toBinaryString(Integer.parseInt(hexString
        .substring(i, i + 1), 16));
      bString += tmp.substring(tmp.length() - 4);
    }
    return bString;
  }


  /**
   * 16进制 --> 字符串
   *
   * @param hexString
   * @return
   * @throws UnsupportedEncodingException
   */
  public static String hexString2String(String hexString) throws UnsupportedEncodingException {
    byte[] baKeyword = hexString2Byte(hexString);
    return new String(baKeyword, "utf-8");//UTF-16le:Not
  }

   /**
   * 16进制 --> byte
   *
   * @param hexString
   * @return
   * @throws UnsupportedEncodingException
   */
  public static byte [] hexString2Byte(String hexString) throws UnsupportedEncodingException {
    byte[] baKeyword = new byte[hexString.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      baKeyword[i] = (byte) (0xff & Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16));
    }
    return baKeyword;
  }

}
