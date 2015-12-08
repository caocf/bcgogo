package com.bcgogo.wx.message;

import com.bcgogo.wx.MsgType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-1
 * Time: 上午11:36
 * To change this template use File | Settings | File Templates.
 */
public class WXBaseMsg {
  private MsgType msgtype;

  public MsgType getMsgtype() {
    return msgtype;
  }

  public void setMsgtype(MsgType msgtype) {
    this.msgtype = msgtype;
  }
}
