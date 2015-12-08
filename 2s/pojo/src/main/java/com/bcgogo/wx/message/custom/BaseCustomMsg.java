package com.bcgogo.wx.message.custom;

import com.bcgogo.wx.MsgType;
import com.bcgogo.wx.message.WXBaseMsg;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-23
 * Time: 上午9:29
 * To change this template use File | Settings | File Templates.
 */
public class BaseCustomMsg extends WXBaseMsg{
  private String touser;

  public String getTouser() {
    return touser;
  }

  public void setTouser(String touser) {
    this.touser = touser;
  }

}
