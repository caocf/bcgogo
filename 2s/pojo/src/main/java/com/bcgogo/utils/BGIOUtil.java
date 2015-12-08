package com.bcgogo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by XinyuQiu on 14-6-27.
 */
public class BGIOUtil {

  /**
   * 从输入流中读取字节内容
   * @param inputStream
   * @return
   * @throws java.io.IOException
   */
  public static byte[] readFromStream(InputStream inputStream) throws IOException {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    int ch;
    while ((ch = inputStream.read()) != -1) {
      byteStream.write(ch);
    }
    byte data[] = byteStream.toByteArray();
    byteStream.close();
    return data;
  }
}
