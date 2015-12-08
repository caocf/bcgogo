package com.bcgogo.wx.qr;

import com.bcgogo.wx.ErrCode;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-30
 * Time: 上午1:23
 * To change this template use File | Settings | File Templates.
 */
public class GetQRResult extends ErrCode {
  private String ticket;
  private String url;
  private String expire_seconds;

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getExpire_seconds() {
    return expire_seconds;
  }

  public void setExpire_seconds(String expire_seconds) {
    this.expire_seconds = expire_seconds;
  }
}
