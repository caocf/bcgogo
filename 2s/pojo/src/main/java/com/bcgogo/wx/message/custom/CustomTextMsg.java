package com.bcgogo.wx.message.custom;

import com.bcgogo.wx.MsgType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-23
 * Time: 上午9:33
 * To change this template use File | Settings | File Templates.
 */
public class CustomTextMsg extends BaseCustomMsg{
  private Map<String,String> text=new HashMap<String, String>();

  public CustomTextMsg(String touser,String text){
    super.setMsgtype(MsgType.text);
    super.setTouser(touser);
    this.text.put("content", text);
  }

}
