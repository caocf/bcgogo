package com.bcgogo.wx.message;

import com.bcgogo.wx.ErrCode;

/**
 * 发送mass msg 或者 template msg 微信端返回值
 * User: ndong
 * Date: 14-9-16
 * Time: 下午6:47
 * To change this template use File | Settings | File Templates.
 */
public class MsgErrorCode extends ErrCode {
  private String msgid;   //template msg
  private String msg_id;  //mass msg

  public String getMsgid() {
    return msgid;
  }

  public void setMsgid(String msgid) {
    this.msgid = msgid;
  }

  public String getMsg_id() {
    return msg_id;
  }

  public void setMsg_id(String msg_id) {
    this.msg_id = msg_id;
  }
}
