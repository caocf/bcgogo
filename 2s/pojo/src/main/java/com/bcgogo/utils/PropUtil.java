package com.bcgogo.utils;

import com.bcgogo.common.CommonUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 上午10:50
 */
public class PropUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PropUtil.class);
  //工程属性文件
  private static final String FILE_PROJECT_PROP = "prop.properties";
  //jar包外摄像头配置文件
  private static final String FILE_MONGO_DB_PROP = "mongodb.properties";

  public static String getOSDisk() {
    return System.getProperty("os.name").toLowerCase().startsWith("win") ? "\\" : "/";
  }

//  /**
//   * 读工程项目配置文件
//   *
//   * @param name
//   * @return
//   * @throws java.io.IOException
//   */
//  public static String read(String name) throws IOException {
//    String path=System.getProperty("mongodb.prop.dir");
//    LOG.info("mongo db prop path:{}",path);
//    return readPropertyFile(name, path);
//  }

//   public static void main(String[] args) throws IOException {
//   String  DB_NAME = PropUtil.read("DB_NAME");
//    System.out.println(DB_NAME);
//  }

  public static String readPropertyFile(String name, String file) throws IOException {
    if (StringUtil.isEmpty(name)) return null;
    InputStream is = null;
    try {
      is = new FileInputStream(file);
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
      is = new FileInputStream(name);
      Properties p = new Properties();
      p.load(is);
      return p.getProperty(name);
    } finally {
      if (is != null)
        is.close();
    }
  }

  /**
   * 获取tomcat的lib路径
   * @return
   * @throws IOException
   */
  public static String getLPath() throws IOException {
    String path = null;
    boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");
    if (CommonUtil.isDevMode() || isWin) {
      path = System.getProperty("user.dir");
      path = path.replace("bin", "lib")+"\\";
    } else {
      Properties properties = new Properties();
      File file = ResourceUtils.getFile("classpath:cfg.properties");
      properties.load(new FileInputStream(file));
      path = properties.getProperty("PROP.CFG.PATH");
    }
    return path;
  }

  /**
   * 获取tomcat的lib路径
   * @return
   * @throws IOException
   */
  public static String getProductPath() throws IOException {
    String path = null;
    boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");
    if (CommonUtil.isDevMode() || isWin) {
      path = "c://product/product.xml";
    } else {
      LOG.info("start filePlace");
      path = "/home/product/product.xml";
    }
    return path;
  }



}
