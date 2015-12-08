package com.bcgogo.admin.dao;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-29
 * Time: 下午6:23
 * 后视镜任务相关常量
 */
public final class TaskConstant {
  //tid任务类型
  public static final String TID_UNKNOWN_TYPE  = "0";   //未知类型
  public static final String TID_CALIBRATE_OBD = "1";   //标定OBD
  public static final String TID_UPLOAD_LOG = "2";      //上传Log

  //status任务状态
  public static final String STATUS_UNTREATED = "0";    //未处理
  public static final String STATUS_FAILED = "-1";      //任务已处理失败
  public static final String STATUS_SUCCESS = "1";      //任务已处理成功

  //任务处理结果（由设备端汇报过来）
  public static final String RESULT_SUCCESS = "0";      //处理成功
  public static final String RESULT_FAILED = "1";       //处理失败

  //IMEI长度
  public static final int IMEI_LENGTH = 15;

  public static final String DOCUMENT_X_MIRROR_TASK="XMirrorTask";
}
