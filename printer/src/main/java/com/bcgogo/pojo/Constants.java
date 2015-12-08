package com.bcgogo.pojo;

import com.bcgogo.util.ConfigUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-4
 * Time: 16:30
 */
public class Constants {

  public static String URL_SERVER_DOMAIN;

  public static String URL_GET_PRINTER_SERIAL_NO = null;
  //获取打印客户端最新的版本
  public static String URL_GET_YUN_PRINT_CLIENT_VERSION = null;
  public static final String TEST_PRINTER_SERIAL_NO = "1234567890";
  //SUBJECT前缀
  public static final String PREFIX_SUBJECT_PRINT = "PRINT.";
  public static final String PREFIX_SUBJECT_PUSH="PUSH." ;

  static {
    try {
      URL_SERVER_DOMAIN = ConfigUtil.read("URL.SERVER.DOMAIN");
      URL_GET_PRINTER_SERIAL_NO = URL_SERVER_DOMAIN + "/web/cameraRecord.do?method=getPrinterSerialNo&cameraSerialNo=";
      URL_GET_YUN_PRINT_CLIENT_VERSION=URL_SERVER_DOMAIN+"/web/print.do?method=getYunPrintClientVersion";
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
