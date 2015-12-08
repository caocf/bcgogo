package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.message.WXBaseMsg;

import java.util.*;

/**
 * 群发图文消息
 * User: ndong
 * Date: 14-9-23
 * Time: 下午1:57
 * To change this template use File | Settings | File Templates.
 */
public class MassNews extends WXBaseMsg {
  protected Map<String,String> mpnews=new HashMap<String, String>();

  public Map<String, String> getMpnews() {
    return mpnews;
  }

  public void setMpnews(Map<String, String> mpnews) {
    this.mpnews = mpnews;
  }
}
