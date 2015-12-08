package com.bcgogo.pojox.constant;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-20
 * Time: 下午2:45
 */
public class GSMConstant {

  public final static Integer ACC_ON = 1; //acc 开启
  public final static Integer ACC_OFF = 0; //acc 关闭

  public final static Long EFFECTIVE_TRAVEL_TIME = 2L * 60 * 1000;//有效的行程时间 单位ms 一段行程开始和结束时间差大于这个时间才判定为有效行程
  public final static Long EFFECTIVE_TRAVEL_INTERVAL_TIME = 20L * 60 * 1000;//有效的行程间隔时间 单位ms  两段行程开始和结束时间差大于这个时间才判定为两段有效行程
  public final static double DEF_OIL_PRICE = 7.5d; //默认油价
  public final static double MIN_CRASH_SPEED = 20d; //最小碰撞前速度
  public final static double MIN_CRASH_STOP_SPEED = 1d; //最大碰撞后停车速度
  public final static int MIN_CRASH_STRENGTH = 200; //最小碰撞值

  /************************** 数据实时上报状态，0实时，1补报，当数据产生时无网络连接，本地存储下来，下次有网络时上报数据数据标识为1 **********/
   public static final int UPLOAD_DATA_REAL_TIME = 0;
   public static final int UPLOAD_DATA_DELAY_TIME = 1;

   /************************** 定位数据有效值，A：实时定位，V：无效定位，取上一个有效定位数据 **********/
   public static final String GPS_DATA_VALID = "A";
   public static final String GPS_DATA_INVALID = "V";

   public static final String FIRE_UP = "0";  //点火
   public static final String CUTOFF = "1"; //熄火
   public static final String DRIVING = "2"; //行驶中
   public static final String AFTER_CUTOFF = "3"; //熄火后，上传的信息
   public static final String GV_IMPACT = "4"; //碰撞时的车况



   /************************** 是否上传标志**************************/
   public static final String UPLOAD_FLAG_YES = "0"; //上传
   public static final String UPLOAD_FLAG_NO = "1"; //不上传


   public static final String KEY_PREFIX_P_GSM_VEHICLE_DATA_START = "_P_GSM_VEHICLE_DATA_START"; //不上传

    //缓存时间 30分钟
  public static final Long M_EXPIRE_P_GSM_VEHICLE_DATA_START = 5 * 60 * 1000l;



}
