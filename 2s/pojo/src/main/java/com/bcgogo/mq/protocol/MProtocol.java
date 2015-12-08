package com.bcgogo.mq.protocol;

import com.bcgogo.mq.enums.MProtocolType;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-6-12
 * Time: 下午4:40
 */
public class MProtocol {
  private MProtocolType type; //协议类型
  private byte[] btProtocol;  //消息包

  public MProtocol(MProtocolType type,byte[] btProtocol){
    this.btProtocol=btProtocol;
    this.type=type;
  }

  public MProtocolType getType() {
    return type;
  }

  public void setType(MProtocolType type) {
    this.type = type;
  }

  public byte[] getBtProtocol() {
    return btProtocol;
  }

  public void setBtProtocol(byte[] btProtocol) {
    this.btProtocol = btProtocol;
  }
}
