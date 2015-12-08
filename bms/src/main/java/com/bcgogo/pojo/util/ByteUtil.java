package com.bcgogo.pojo.util;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 15:55
 */
public class ByteUtil {


  /**
   * int转化成byte 反转
   *
   * @param i
   * @return
   */
  public static byte[] intToReverseByte(int i) {
    int temp;
    byte[] aByte = new byte[4];
    aByte[0] = (byte) ((0xff000000 & i) >> 24);
    aByte[1] = (byte) ((0xff0000 & i) >> 16);
    aByte[2] = (byte) ((0xff00 & i) >> 8);
    aByte[3] = (byte) (0xff & i);
    return aByte;
  }


  public static int reverseByteToInt(byte[] bytes) {
    int addr = bytes[3] & 0xFF;
    addr |= ((bytes[2] << 8) & 0xFF00);
    addr |= ((bytes[1] << 16) & 0xFF0000);
    addr |= ((bytes[0] << 24) & 0xFF000000);
    return addr;
  }


  /**
   * 截取子数组
   *
   * @param src
   * @param begin
   * @param count
   * @return
   */
  public static byte[] subBytes(byte[] src, int begin, int count) {
    byte[] bs = new byte[count];
    for (int i = begin; i < begin + count; i++) {
      bs[i - begin] = src[i];
    }
    return bs;
  }

  /**
   * 合并两个byte数组
   *
   * @param byte_1
   * @param byte_2
   * @return
   */
  public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
    byte[] byte_3 = new byte[byte_1.length + byte_2.length];
    System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
    System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
    return byte_3;
  }

  public static byte[] complementZero(byte[] byte_1) {
    byte[] byte_2 = new byte[1];
    byte_2[0] = '\0';
    return byteMerger(byte_1, byte_2);
  }


  public static byte[] intToByte(int i) {
    byte[] aByte = new byte[4];
    aByte[0] = (byte) (0xff & i);
    aByte[1] = (byte) ((0xff00 & i) >> 8);
    aByte[2] = (byte) ((0xff0000 & i) >> 16);
    aByte[3] = (byte) ((0xff000000 & i) >> 24);
    return aByte;

  }


}
