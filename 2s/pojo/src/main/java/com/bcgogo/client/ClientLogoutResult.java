package com.bcgogo.client;

/**
 * User: ZhangJuntao
 * Date: 13-9-18
 * Time: 下午2:22
 */
public class ClientLogoutResult {
  private Boolean isSuccess;      //是否登录成功
  private String sessionId;       //设计预留

  public ClientLogoutResult() {
  }

  public ClientLogoutResult(Boolean success) {
    isSuccess = success;
  }

  public Boolean getSuccess() {
    return isSuccess;
  }

  public void setSuccess(Boolean success) {
    isSuccess = success;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
