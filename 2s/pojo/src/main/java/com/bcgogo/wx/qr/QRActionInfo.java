package com.bcgogo.wx.qr;

import java.util.HashMap;
import java.util.Map;

/**
 * 生成带场景的二维码请求参数
 * User: ndong
 * Date: 14-8-30
 * Time: 上午1:01
 * To change this template use File | Settings | File Templates.
 */
public class QRActionInfo {
  private  QRType action_name;
  private String expire_seconds;
  private Map<String,Map<String,String>> action_info=new HashMap<String, Map<String, String>>();

  public QRType getAction_name() {
    return action_name;
  }

  public void setAction_name(QRType action_name) {
    this.action_name = action_name;
  }

  public String getExpire_seconds() {
    return expire_seconds;
  }

  public void setExpire_seconds(String expire_seconds) {
    this.expire_seconds = expire_seconds;
  }

  public Map<String, Map<String, String>> getAction_info() {
    return action_info;
  }

  public void setAction_info(Map<String, Map<String, String>> action_info) {
    this.action_info = action_info;
  }
}
