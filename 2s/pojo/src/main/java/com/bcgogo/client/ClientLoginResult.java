package com.bcgogo.client;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午1:31
 */
public class ClientLoginResult {
  private Boolean isSuccess;      //是否登录成功
  private Long shopId;
  private String shopName;        //店铺名字
  private String userNo;
  private String userName;        //用户名字
  private String sessionId;       //设计预留

  public ClientLoginResult() {
    super();
  }

  public ClientLoginResult(Boolean isSuccess,String userNo) {
    this.setSuccess(isSuccess);
    this.setUserNo(userNo);
  }

  public Boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(Boolean success) {
    isSuccess = success;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
