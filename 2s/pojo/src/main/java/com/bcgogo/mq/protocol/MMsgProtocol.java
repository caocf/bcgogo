package com.bcgogo.mq.protocol;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 11:10
 */


import com.bcgogo.utils.ByteUtil;

/**
 * 后视镜消息协议
 *
 * @author Simple
 */
public class MMsgProtocol {
  private byte[] len;
  private byte type;
  private byte[] data;

  public MMsgProtocol() {
  }

  public MMsgProtocol(byte type, byte[] data) {
    this.type = type;
    this.data = data;
  }

  public byte[] toProtocol() {
    len = ByteUtil.intToReverseByte(data.length);
    byte[] head = new byte[5];
    head[0] = len[0];
    head[1] = len[1];
    head[2] = len[2];
    head[3] = len[3];
    head[4] = type;
    if (data == null) {
      data = new byte[0];
    }
    return ByteUtil.byteMerger(head, data);
  }

  public byte[] getLen() {
    return len;
  }

  public void setLen(byte[] len) {
    this.len = len;
  }

  public byte getType() {
    return type;
  }

  public void setType(byte type) {
    this.type = type;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }
}
