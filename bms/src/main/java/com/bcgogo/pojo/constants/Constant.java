package com.bcgogo.pojo.constants;

import com.bcgogo.pojo.enums.PushMessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 09:46
 */
public class Constant {

  //心跳包
  public static final byte MIRROR_MSG_TYPE_HEART_BEAT = 1;
  //登陆包
  public static final byte MIRROR_MSG_LOGIN = 2;
  //登陆确认
  public static final byte MIRROR_MSG_LOGIN_ACK = 3;
  //退出登陆
  public static final byte MIRROR_MSG_QUIT = 4;
  //字符串数据
  public static final byte MIRROR_MSG_DATA = 5;
  //数据确认包
  public static final byte MIRROR_MSG_DATA_ACK = 6;

  //用户信息异常
  public static final byte MIRROR_MSG_ERROR = 0x64;

  public static final byte WEB_SOCKET_TALK_FROM_WX = 0x66;

  public static  String imei = "356824200008005";
  public static String openId = "oCFjjt2gpABhzgNAjkR1qsB_r6B8";


  public static String SERVER_IP = "127.0.0.1";
  public static String SOCKET_SERVER_IP = "61.177.55.242";
  public static int SERVER_PORT = 8080;
  public static int SOCKET_SERVER_PORT = 60112;

  public static String domain = "http://" + SERVER_IP + ":" + SERVER_PORT;;
  public static String URL_MSG_MIRROR_LOGIN = domain + "/api/msg/mirror/login";;
  public static String URL_MIRROR_LOGIN = domain + "/api/mirror/login/{IMEI}";;

  public static void init(String evn) {

    if ("TEST".equals(evn)) {
      SERVER_IP = "61.177.55.242";
      openId = "ovjSts-ublkv2I4__xoU8Nt0f3iw";
      SERVER_PORT = 8035;
      SOCKET_SERVER_PORT = 60111;
    } else if ("SHOP".equals(evn)) {
      SERVER_IP = "221.6.167.67";
      SOCKET_SERVER_IP = "42.121.98.170";
      openId = "ovjSts-ublkv2I4__xoU8Nt0f3iw";
      SERVER_PORT = 48080;
      SOCKET_SERVER_PORT = 60111;
      imei = "864881022129538";
    }
    domain = "http://" + SERVER_IP + ":" + SERVER_PORT;
    URL_MSG_MIRROR_LOGIN = domain + "/api/msg/mirror/login";
    URL_MIRROR_LOGIN = domain + "/api/mirror/login/{IMEI}";
  }


  //推送消息转化成int
  public static Map<String, Integer> pushMessageTypeMap = new HashMap<String, Integer>();

  static {
    pushMessageTypeMap.put(PushMessageType.MSG_FROM_WX_USER.toString(), 0);
    pushMessageTypeMap.put(PushMessageType.MSG_FROM_SHOP.toString(), 1);
    pushMessageTypeMap.put(PushMessageType.MSG_FROM_MIRROR_TO_WX_USER.toString(), 2);
    pushMessageTypeMap.put(PushMessageType.SHOP_CHANGE_APPOINT.toString(), 11);
    pushMessageTypeMap.put(PushMessageType.SHOP_FINISH_APPOINT.toString(), 12);
    pushMessageTypeMap.put(PushMessageType.SHOP_ACCEPT_APPOINT.toString(), 13);
    pushMessageTypeMap.put(PushMessageType.CUSTOM_MESSAGE_2_APP.toString(), 14);
    pushMessageTypeMap.put(PushMessageType.SHOP_REJECT_APPOINT.toString(), 15);
    pushMessageTypeMap.put(PushMessageType.SHOP_CANCEL_APPOINT.toString(), 16);
    pushMessageTypeMap.put(PushMessageType.OVERDUE_APPOINT_TO_APP.toString(), 17);
    pushMessageTypeMap.put(PushMessageType.SOON_EXPIRE_APPOINT_TO_APP.toString(), 18);
    pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_MAINTAIN_MILEAGE.toString(), 19);
    pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_MAINTAIN_TIME.toString(), 20);
    pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_INSURANCE_TIME.toString(), 21);
    pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_EXAMINE_TIME.toString(), 22);
    pushMessageTypeMap.put(PushMessageType.SHOP_QUOTE_TO_APP.toString(), 23);
    pushMessageTypeMap.put(PushMessageType.VEHICLE_FAULT_2_APP.toString(), 24);
    pushMessageTypeMap.put(PushMessageType.SHOP_ADVERT_TO_APP.toString(), 25);
    pushMessageTypeMap.put(PushMessageType.VIOLATE_REGULATION_RECORD_2_APP.toString(), 26);
  }

}
