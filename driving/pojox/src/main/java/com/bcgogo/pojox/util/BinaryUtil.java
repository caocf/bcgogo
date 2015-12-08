package com.bcgogo.pojox.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-16
 * Time: 上午9:28
 */
public class BinaryUtil {



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
   * @throws java.io.UnsupportedEncodingException
   */
  public static String hexString2String(String hexString) throws UnsupportedEncodingException {
    byte[] baKeyword = hexString2Byte(hexString);
    return new String(baKeyword, "utf-8");//UTF-16le:Not
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
   * 16进制 --> byte
   *
   * @param hexString
   * @return
   * @throws java.io.UnsupportedEncodingException
   */
  public static byte [] hexString2Byte(String hexString) throws UnsupportedEncodingException {
    byte[] baKeyword = new byte[hexString.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      baKeyword[i] = (byte) (0xff & Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16));
    }
    return baKeyword;
  }


}
