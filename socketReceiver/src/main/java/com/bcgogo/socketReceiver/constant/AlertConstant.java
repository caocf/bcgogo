package com.bcgogo.socketReceiver.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 上午10:07
 */
public class AlertConstant {

  public static final String JX_FLAG = "#JX#";
  public static final String WY_FLAG = "#WY#";
  public static final String ZD_FLAG = "#ZD#";
  public static final String OUT_FLAG = "#OUT#";
  public static final String PZ_FLAG = "#PZ#";
  public static final String LPD_FLAG = "#LPD#";

  public static final String JX_TYPE = "JX";
  public static final String WY_TYPE = "WY";
  public static final String ZD_TYPE = "ZD";
  public static final String OUT_TYPE = "OUT";
  public static final String PZ_TYPE = "PZ";
  public static final String LPD_TYPE = "LPD";

  //#008613717052335
  // #3717052335
  // #1
  // #1234
  // #JX
  // #1
  // #46001
  // #A
  // #11400.4268,E,2233.1050,N,000.00,000
  // #030314
  // #160131##
  public static final int IDX_ALERT_IMEI = 0;
  public static final int IDX_ALERT_STATE = 1;
  public static final int IDX_ALERT_TYPE = 2;
  public static final int IDX_ALERT_GROUP = 3;
  public static final int IDX_ALERT_CELLPOS = 4;
  public static final int IDX_ALERT_INFO = 6;
  public static final int IDX_ALERT_DAY = 7;
  public static final int IDX_ALERT_TIME = 8;
  public static final int IDX_ALERT_IMPACT_STRENGTH = 9;

  // #11400.4268,E,2233.1050,N,000.00,000
  public static final int IDX_ALERT_LON = 0;
  public static final int IDX_ALERT_LONDIR = 1;
  public static final int IDX_ALERT_LAT = 2;
  public static final int IDX_ALERT_LATDIR =3;
  public static final int IDX_ALERT_VELOCITY = 4;
  public static final int IDX_ALERT_HEADING = 5;



  public static List<String> AllAlertFlagList = new ArrayList<String>();
  static {
    AllAlertFlagList.add(JX_TYPE);
    AllAlertFlagList.add(WY_TYPE);
    AllAlertFlagList.add(ZD_TYPE);
    AllAlertFlagList.add(OUT_TYPE);
    AllAlertFlagList.add(PZ_TYPE);
    AllAlertFlagList.add(LPD_TYPE);
  }

}
