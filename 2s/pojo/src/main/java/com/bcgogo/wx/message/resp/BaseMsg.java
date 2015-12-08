package com.bcgogo.wx.message.resp;

import com.bcgogo.wx.MsgType;

/**
 * 消息基类（公众帐号 -> 普通用户）
 * User: ndong
 * Date: 14-8-7
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */
public class BaseMsg {
  private String ToUserName;
  private String FromUserName;
  private long CreateTime;
  private MsgType MsgType;
  // 位0x0001被标志时，星标刚收到的消息
  private int FuncFlag;

  public String getToUserName() {
    return ToUserName;
  }

  public void setToUserName(String toUserName) {
    ToUserName = toUserName;
  }

  public String getFromUserName() {
    return FromUserName;
  }

  public void setFromUserName(String fromUserName) {
    FromUserName = fromUserName;
  }

  public long getCreateTime() {
    return CreateTime;
  }

  public void setCreateTime(long createTime) {
    CreateTime = createTime;
  }

  public MsgType getMsgType() {
    return MsgType;
  }

  public void setMsgType(MsgType msgType) {
    MsgType = msgType;
  }

  public int getFuncFlag() {
    return FuncFlag;
  }

  public void setFuncFlag(int funcFlag) {
    FuncFlag = funcFlag;
  }
}
