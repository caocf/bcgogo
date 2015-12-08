package com.bcgogo.wx.message.mass;

import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.message.WXBaseMsg;

import java.util.*;

/**
 * 群发文本消息.
 * User: ndong
 * Date: 14-9-1
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class MassTextMsg extends WXBaseMsg {
  List<String> touser=new ArrayList<String>();

  private Map<String,String> text;

  public MassTextMsg(String content,String... openIds){
    super.setMsgtype(MsgType.text);
    text=new HashMap<String, String>();
    text.put("content",content);
    getTouser().addAll(Arrays.asList(openIds));
  }

  public List<String> getTouser() {
    return touser;
  }

  public void setTouser(List<String> touser) {
    this.touser = touser;
  }
}
