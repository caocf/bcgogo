package com.bcgogo.wx;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-30
 * Time: 下午3:03
 */
public class OAuthAccessToken extends AccessToken{
  private String refresh_token;
  private String openid;
  private String scope;

  public String getRefresh_token() {
    return refresh_token;
  }

  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  public String getOpenid() {
    return openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }
}
