package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserLoginLogDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户登录记录表
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-19
 * Time: 下午3:46
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user_login_log")
public class UserLoginLog extends LongIdentifier {
  private Long shopId;
  private String userNo;
  private Long loginTime; //登入时间
  private Long logoutTime; //登出时间
  private String loginIP; //登录ip地址
  private String sessionId;//登录session id
  private String finger;//登录设备指纹

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "user_no")
  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  @Column(name = "login_time")
  public Long getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Long loginTime) {
    this.loginTime = loginTime;
  }

  @Column(name = "logout_time")
  public Long getLogoutTime() {
    return logoutTime;
  }

  public void setLogoutTime(Long logoutTime) {
    this.logoutTime = logoutTime;
  }

  @Column(name = "login_ip")
  public String getLoginIP() {
    return loginIP;
  }

  public void setLoginIP(String loginIP) {
    this.loginIP = loginIP;
  }

  @Column(name = "session_id")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Column(name = "finger")
  public String getFinger() {
    return finger;
  }

  public void setFinger(String finger) {
    this.finger = finger;
  }

  public UserLoginLog(){}


  public UserLoginLog(UserLoginLogDTO userLoginLogDTO) {
    this.setId(userLoginLogDTO.getId());
    this.setShopId(userLoginLogDTO.getShopId());
    this.setUserNo(userLoginLogDTO.getUserNo());
    this.setLoginTime(userLoginLogDTO.getLoginTime());
    this.setLogoutTime(userLoginLogDTO.getLogoutTime());
    this.setLoginIP(userLoginLogDTO.getLoginIP());
    this.setSessionId(userLoginLogDTO.getSessionId());
    this.setFinger(userLoginLogDTO.getFinger());
  }

  public UserLoginLogDTO toDTO() {
    UserLoginLogDTO userLoginLogDTO = new UserLoginLogDTO();
    userLoginLogDTO.setId(getId());
    userLoginLogDTO.setShopId(getShopId());
    userLoginLogDTO.setUserNo(getUserNo());
    userLoginLogDTO.setLoginTime(getLoginTime());
    userLoginLogDTO.setLogoutTime(getLogoutTime());
    userLoginLogDTO.setLoginIP(getLoginIP());
    userLoginLogDTO.setSessionId(getSessionId());
    userLoginLogDTO.setFinger(getFinger());
    return userLoginLogDTO;
  }
}
