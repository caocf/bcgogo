package com.bcgogo.pojo.protocol;



/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-3
 * Time: 17:43
 */
public class MirrorMsgProtocol {
  private byte[] len;
  private byte type;
  private char[] content;

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

  public char[] getContent() {
    return content;
  }

  public void setContent(char[] content) {
    this.content = content;
  }

}
