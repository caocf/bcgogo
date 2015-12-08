package com.bcgogo.wx;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信事件
 * User: ndong
 * Date: 14-9-4
 * Time: 下午1:13
 * To change this template use File | Settings | File Templates.
 */
public enum WXEvent {
  SUBSCRIBE("subscribe"),   //订阅公共号
  SCAN("SCAN"),   //关注后，扫描二维码
  UN_SUBSCRIBE("unsubscribe"),  //取消关注
  LOCATION("LOCATION"),    //定位
  MENU_CLICK("CLICK"),  //点击菜单
  MENU_VIEW("VIEW"),    //查看菜单
  MASS_SEND_JOB_FINISH("MASSSENDJOBFINISH"),    //群发成功回调事件
  TEMPLATE_SEND_JOB_FINISH("TEMPLATESENDJOBFINISH");  //发送模版消息成功

  private String name;
  private static Map<String,WXEvent> eventMap;

  WXEvent(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }

  public static WXEvent getWXEvent(String eventStr){
    return eventMap.get(eventStr);
  }

  static {

    if(eventMap==null){
      eventMap=new HashMap<String, WXEvent>();
      eventMap.put(WXEvent.SUBSCRIBE.getName(),WXEvent.SUBSCRIBE);
      eventMap.put(WXEvent.UN_SUBSCRIBE.getName(),WXEvent.UN_SUBSCRIBE);
      eventMap.put(WXEvent.LOCATION.getName(),WXEvent.LOCATION);
      eventMap.put(WXEvent.SCAN.getName(),WXEvent.SCAN);
      eventMap.put(WXEvent.MENU_CLICK.getName(),WXEvent.MENU_CLICK);
      eventMap.put(WXEvent.MENU_VIEW.getName(),WXEvent.MENU_VIEW);
      eventMap.put(WXEvent.TEMPLATE_SEND_JOB_FINISH.getName(),WXEvent.TEMPLATE_SEND_JOB_FINISH);
      eventMap.put(WXEvent.MASS_SEND_JOB_FINISH.getName(),WXEvent.MASS_SEND_JOB_FINISH);
    }
  }

}
