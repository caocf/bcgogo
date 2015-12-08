package com.bcgogo.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-10-8
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class RQUtilTest {
  public static final String HTTP_42_121_113_26 = "http://42.121.113.26";

  public static void main(String[] args) {
    try {
      File file = new File("D:\\3.0\\bcgogoAppDownload1.jpg");

      String str = "test1234";
      str = new String(str.getBytes(),"utf-8");

      RQUtil.getRQWriteFile("str", 200, file);
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }
}
