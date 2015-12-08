package com.bcgogo.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-2
 * Time: 15:14
 */
public class ConfigUtil {
    //工程属性文件
  private static final String FILE_PROJECT_PROP = "/prop.properties";
  //jar包外摄像头配置文件
  private static final String FILE_CAMERA_PROP = "camera.properties";
  //客户端序列号
  private static final String FILE_CLIENT_NO = "client.sn";

  /**
   * 读工程项目配置文件
   *
   * @param name
   * @return
   * @throws IOException
   */
  public static String read(String name) throws IOException {
    return readPropertyFile(name, FILE_PROJECT_PROP);
  }

  public static void saveSerialNo(String printerSerialNo) throws IOException {
    if (StringUtil.isEmpty(printerSerialNo)) return;
    OutputStream out = null;
    try {
      out = new FileOutputStream(FILE_CLIENT_NO);
      IOUtil.write(printerSerialNo, out);
    } finally {
      if (out != null)
        out.close();
    }
  }

  /**
   * 获取客户端序列号
   *
   * @return
   * @throws IOException
   */
  public static String readSerialNo() throws Exception {
    InputStream in = null;
    try {
      in = new FileInputStream(FILE_CLIENT_NO);;
      StringWriter writer = new StringWriter();
      IOUtil.copy(in, writer, "UTF-8");
      return writer.toString();
    } finally {
      if (in != null)
        in.close();
    }
  }


  public static String readPropertyFile(String name, String file) throws IOException {
    if (StringUtil.isEmpty(name)) return null;
    InputStream is = null;
    try {
      is = Object.class.getResourceAsStream(file);
      Properties prop = new Properties();
      prop.load(is);
      return prop.getProperty(name);
    } finally {
      if (is != null)
        is.close();
    }

  }

  /**
   * 读取jar包以外的工程配置
   *
   * @param name
   * @return
   * @throws IOException
   */
  public static String readOutPropertyFile(String name) throws IOException {
    if (StringUtil.isEmpty(name)) return null;
    InputStream is = null;
    try {
      is = new FileInputStream(FILE_CAMERA_PROP);
      Properties p = new Properties();
      p.load(is);
      return p.getProperty(name);
    } finally {
      if (is != null)
        is.close();
    }
  }

}
