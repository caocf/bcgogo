package com.bcgogo.wx;

import com.bcgogo.wx.security.AesException;
import com.bcgogo.wx.security.WXSHA1;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-20
 * Time: 15:09
 */
public class WXJsApiTicketSign {
  private String appId;
  private String jsApiTicket;
  private String noncestr;
  private String timestamp;
  private String url;
  private String signature;

  public WXJsApiTicketSign(){}

  public WXJsApiTicketSign(String jsApiTicket,String noncestr,String timestamp,String url){
    this.jsApiTicket=jsApiTicket;
    this.noncestr=noncestr;
    this.timestamp=timestamp;
    this.url=url;
  }

  public String makeSignature() throws AesException {
    signature = WXSHA1.getSHA1(this);
    return signature;

  }


  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public String getJsApiTicket() {
    return jsApiTicket;
  }

  public void setJsApiTicket(String jsApiTicket) {
    this.jsApiTicket = jsApiTicket;
  }

  public String getNoncestr() {
    return noncestr;
  }

  public void setNoncestr(String noncestr) {
    this.noncestr = noncestr;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
