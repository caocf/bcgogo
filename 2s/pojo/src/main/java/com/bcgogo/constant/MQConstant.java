package com.bcgogo.constant;

import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 09:46
 */
public class MQConstant {

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
  public static final byte MIRROR_MSG_ERROR =0x64;

//  public static final byte WEB_SOCKET_TALK_FROM_MIRROR =0x65;

//  public static final byte WEB_SOCKET_TALK_FROM_WX =0x66;

  public static String domain = "http://192.168.1.100:8080";


  public static final String URL_MIRROR_LOGIN = domain + "/api/mirror/login/{IMEI}";

  public static final String CLIENT_LOGIN = "_msg_center_login_from_client";

//检测客户端是否在线
  public static final String URL_MQ_HTTP_IS_ONLINE = "/mq/mirror/isOnLine/{USER_NAME}";
  //mq推送消息接口
  public static final String URL_MQ_HTTP_PUSH = "/mq/mirror/push";
//   //对话消息 微信端-->客户端
//  public static final int MSG_FROM_WX_USER = 0;
//  //对话消息 4s店铺-->客户端
//  public static final int MSG_FROM_SHOP = 1;

  //推送消息转化成int
  public static Map<String,Integer> pushMessageTypeMap=new HashMap<String, Integer>();

  static {
     pushMessageTypeMap.put(PushMessageType.MSG_FROM_WX_USER_TO_MIRROR.toString(),0);
//     pushMessageTypeMap.put(PushMessageType.MSG_FROM_SHOP.toString(),1);
     pushMessageTypeMap.put(PushMessageType.SHOP_CHANGE_APPOINT.toString(),11);
     pushMessageTypeMap.put(PushMessageType.SHOP_FINISH_APPOINT.toString(),12);
     pushMessageTypeMap.put(PushMessageType.SHOP_ACCEPT_APPOINT.toString(),13);
     pushMessageTypeMap.put(PushMessageType.CUSTOM_MESSAGE_2_APP.toString(),14);
     pushMessageTypeMap.put(PushMessageType.SHOP_REJECT_APPOINT.toString(),15);
     pushMessageTypeMap.put(PushMessageType.SHOP_CANCEL_APPOINT.toString(),16);
     pushMessageTypeMap.put(PushMessageType.OVERDUE_APPOINT_TO_APP.toString(),17);
     pushMessageTypeMap.put(PushMessageType.SOON_EXPIRE_APPOINT_TO_APP.toString(),18);
     pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_MAINTAIN_MILEAGE.toString(),19);
     pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_MAINTAIN_TIME.toString(),20);
     pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_INSURANCE_TIME.toString(),21);
     pushMessageTypeMap.put(PushMessageType.APP_VEHICLE_EXAMINE_TIME.toString(),22);
     pushMessageTypeMap.put(PushMessageType.SHOP_QUOTE_TO_APP.toString(),23);
     pushMessageTypeMap.put(PushMessageType.VEHICLE_FAULT_2_APP.toString(),24);
     pushMessageTypeMap.put(PushMessageType.SHOP_ADVERT_TO_APP.toString(),25);
     pushMessageTypeMap.put(PushMessageType.VIOLATE_REGULATION_RECORD_2_APP.toString(),26);
  }


}
