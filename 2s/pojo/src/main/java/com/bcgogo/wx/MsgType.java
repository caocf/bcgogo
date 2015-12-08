package com.bcgogo.wx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

/**
 * 微信消息类型
 * User: ndong
 * Date: 14-8-13
 * Time: 上午9:55
 * To change this template use File | Settings | File Templates.
 */
public enum MsgType {
  text("文本"),
  image("图片"),
  thumb("缩略图"),
  voice("语音"),
  video("视频"),
  location("地理位置"),
  link("链接"),
  news("图文消息"),
  mpnews("图文消息"), //发送massMsg用到
  event("事件"),
  transfer_customer_service("多客服消息"),
  ;
  private String name;
  private static Map<String,MsgType> typeMap;
  private static List<MsgType> unSupportTypes;

  MsgType(String name){
    this.name=name;
  }

  static {
    if(typeMap==null){
      typeMap=new HashMap<String, MsgType>();
      typeMap.put(MsgType.text.toString(),MsgType.text);
      typeMap.put(MsgType.image.toString(),MsgType.image);
      typeMap.put(MsgType.voice.toString(),MsgType.voice);
      typeMap.put(MsgType.video.toString(),MsgType.video);
      typeMap.put(MsgType.location.toString(),MsgType.location);
      typeMap.put(MsgType.link.toString(),MsgType.link);
      typeMap.put(MsgType.event.toString(),MsgType.event);
    }
    if(unSupportTypes==null){
      unSupportTypes=new ArrayList<MsgType>();
      unSupportTypes.add(MsgType.voice);
      unSupportTypes.add(MsgType.video);
      unSupportTypes.add(MsgType.link);
      unSupportTypes.add(MsgType.news);
    }
  }

  public static MsgType getType(String typeStr){
    if(typeMap==null){
      return null;
    }
    return typeMap.get(typeStr);
  }

  public static List<MsgType> getUnSupportTypes() {
    return unSupportTypes;
  }
}
