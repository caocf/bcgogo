package com.bcgogo.wx;

/**
 * 微信基础支持 access_token
 * User: ndong
 * Date: 14-8-6
 * Time: 上午9:30
 * To change this template use File | Settings | File Templates.
 */
public class AccessToken extends ErrCode{
  private String access_token;
  private String expires_in;

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public String getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(String expires_in) {
    this.expires_in = expires_in;
  }
}
