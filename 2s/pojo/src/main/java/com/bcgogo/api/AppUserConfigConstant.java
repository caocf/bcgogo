package com.bcgogo.api;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-22
 * Time: 上午11:16
 */
public class AppUserConfigConstant {
  public static final String OIL_PRICE = "oil_price"; //油价
  public static final String OIL_KIND = "oil_kind";  //油品
  public static final String OIL_KIND_93 = "93号";  //93号油品名称
  public static final String FIRST_DRIVE_LOG_CREATE_TIME = "first_drive_log_time";  //第一条行车日志创建时间
  public static final String LAST_DRIVE_LOG_UPDATE_TIME = "last_drive_log_update_time";//行车日志最后更新时间
  public static Set<String> configNameSet = new HashSet<String>();

  static{
    configNameSet.add(OIL_PRICE);
    configNameSet.add(OIL_KIND);
  }

}
