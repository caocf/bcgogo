package com.bcgogo.pojo.protocol;

import com.bcgogo.pojo.enums.ProtocolType;

/**
 * 消息协议
 * Author: ndong
 * Date: 2015-6-4
 * Time: 11:08
 */

public class MinaProtocol {
  private ProtocolType type;
  private byte[] protocol;

  public MinaProtocol() {
  }

  public MinaProtocol(ProtocolType type, byte[] protocol) {
    this.type = type;
    this.protocol = protocol;
  }


  public ProtocolType getType() {
    return type;
  }

  public void setType(ProtocolType type) {
    this.type = type;
  }

  public byte[] getProtocol() {
    return protocol;
  }

  public void setProtocol(byte[] protocol) {
    this.protocol = protocol;
  }
}
