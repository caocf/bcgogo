package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * 用户登录信息封装类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-2-19
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
public class UserLoginLogDTO implements Serializable {
  private Long id;
  private Long shopId;
  private String userNo;
  private Long loginTime; //登入时间
  private Long logoutTime; //登出时间
  private String loginIP; //登录ip地址
  private String sessionId;//登录session id
  private String finger;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public Long getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Long loginTime) {
    this.loginTime = loginTime;
  }

  public Long getLogoutTime() {
    return logoutTime;
  }

  public void setLogoutTime(Long logoutTime) {
    this.logoutTime = logoutTime;
  }

  public String getLoginIP() {
    return loginIP;
  }

  public void setLoginIP(String loginIP) {
    this.loginIP = loginIP;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getFinger() {
    return finger;
  }

  public void setFinger(String finger) {
    this.finger = finger;
  }

  @Override
  public String toString() {
    return "UserLoginLogDTO{" +
        "shopId=" + shopId +
        ", userNo='" + userNo + '\'' +
        ", loginTime=" + loginTime +
        ", logoutTime=" + logoutTime +
        ", loginIP='" + loginIP + '\'' +
        ", sessionId='" + sessionId + '\'' +
        '}';
  }
}
