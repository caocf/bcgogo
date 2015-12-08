package com.bcgogo.api.util;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 15-1-5
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public  class CameraConstant {

  /****************************************************** memcached KEY ****************************************************************************/

  //心跳包数据key
  public static final String KEY_HEART_BEAT_DATA="_heart_beat_data_";

  //心跳包数据key
  public static final String KEY_LICENSE_REPORT_DATA="_license_report_data_";

  /**************************** memcached data expirationTime *******************************************/
  //事件接口处理完成时间。可设大点 防止接口被多次调用
  public static final Long M_EXPIRE_RESP_HANDLE_START=60*1000l;  // 5min

  public static final Long M_EXPIRE_RESP_HANDLE_FINISH=60*1000l;  // 30s
  //缓存上次操作记录 5分钟
  public static final Long M_EXPIRE_USER_LAST_ACTION=5*60*1000l;
  //ACCESS_TOKEN缓存时间 30分钟
  public static final Long M_EXPIRE_ACCESS_TOKEN=30*60*1000l;
  //ACCOUNT缓存时间 24小时
  public static final Long M_EXPIRE_ACCOUNT=24*60*60*1000l;
  //心跳数据缓存时间 24小时
  public static final Long M_TIME_HEART_BEAT_DATA=24*60*60*1000l;
  //报警数据（车牌上报数据）缓存时间 24小时
  public static final Long M_TIME_LICENSE_REPORT_DATA=24*60*60*1000l;


}