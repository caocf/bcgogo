//package com.bcgogo.pojo.util;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//
///**
// * Created by IntelliJ IDEA.
// * Author: ndong
// * Date: 2015-3-2
// * Time: 15:14
// */
//public class ConfigUtil {
//
//  /**
//   * 读工程项目配置文件
//   *
//   * @param name
//   * @return
//   * @throws IOException
//   */
//  public static String read(String name) throws IOException {
//    return readPropertyFile(name, "/prop.properties");
//  }
//
//  public static String readPropertyFile(String name, String file) throws IOException {
//    if (StringUtil.isEmpty(name)) return null;
//    InputStream is = null;
//    try {
//      is = Object.class.getResourceAsStream(file);
//      Properties p = new Properties();
//      p.load(is);
//      return p.getProperty(name);
//    } finally {
//      if (is != null)
//        is.close();
//    }
//
//  }
//
//  /**
//   * 读取jar包以外的工程配置
//   *
//   * @param name
//   * @return
//   * @throws IOException
//   */
//  public static String readOutPropertyFile(String name) throws IOException {
//    if (StringUtil.isEmpty(name)) return null;
//    InputStream is = null;
//    try {
//      is = new FileInputStream("camera.properties");
//      Properties p = new Properties();
//      p.load(is);
//      return p.getProperty(name);
//    } finally {
//      if (is != null)
//        is.close();
//    }
//  }
//
//}
