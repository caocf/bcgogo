package com.bcgogo.wx;

/**
 * 获取微信jsapi_ticket返回结果
 * Author: ndong
 * Date: 2015-5-20
 * Time: 14:30
 */
public class JSApiTicket {
  private String errcode;
  private String errmsg;
  private String ticket;
  private String expires_in;

  public String getErrcode() {
    return errcode;
  }

  public void setErrcode(String errcode) {
    this.errcode = errcode;
  }

  public String getErrmsg() {
    return errmsg;
  }

  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public String getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(String expires_in) {
    this.expires_in = expires_in;
  }
}
