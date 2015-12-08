package com.bcgogo.pojo.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-16
 * Time: 上午10:19
 */
public class IOUtil extends IOUtils {


  /**
   * 将输入流按行读取并组成集合set
   * 读取时去除每行空格(" ")符
   *
   * @param in
   * @return
   * @throws java.io.IOException
   */
  public static Set<String> getStringsFromInputStream(InputStream in) throws IOException {
    Set<String> stringList = new HashSet<String>();
    InputStreamReader reader = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(reader);
    String line = "";

    while ((line = br.readLine()) != null) {
      stringList.add(line);
    }
    return stringList;
  }

  /**
   * 从输入流中获取数据
   *
   * @param inStream 输入流
   * @return
   * @throws Exception
   */
  public static byte[] readFromStream(InputStream inStream) throws Exception {
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = 0;
    while ((len = inStream.read(buffer)) != -1) {
      outStream.write(buffer, 0, len);
    }
    inStream.close();
    return outStream.toByteArray();
  }

  public static void main(String[] args) throws FileNotFoundException {
    String path = "C:\\test.mp4";
    InputStream in = new FileInputStream(path);
    try {
      ;
      System.out.println(IOUtil.readFromStream(in).length);
    } catch (Exception e) {
      e.printStackTrace();
      IOUtil.closeQuietly(in);
    }

  }

}
