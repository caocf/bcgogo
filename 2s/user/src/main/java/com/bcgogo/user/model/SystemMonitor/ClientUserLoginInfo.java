package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserDTO;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-9-17
 * Time: 下午3:51
 */
@Entity
@Table(name = "client_user_login_info")
public class ClientUserLoginInfo extends LongIdentifier {
  private Long shopId;//店铺
  private Long userId;//用户账号
  private String clientVersion;//客户端版本
  private Long loginTime;//登陆时间
  private String sessionId;// sessionId
  private Long sessionCreateTime;
  private Long logoutTime;//登出时间
  private Status status = Status.active;// 登陆状态

  public ClientUserLoginInfo() {
  }

  public void createClientUserLoginInfo(UserDTO user) {
    setUserId(user.getId());
    setShopId(user.getShopId());
  }
  public void createClientUserLoginInfo(UserDTO user, String clientVersion) {
    createClientUserLoginInfo(user);
    setClientVersion(clientVersion);
    setLoginTime(System.currentTimeMillis());
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "client_version")
  public String getClientVersion() {
    return clientVersion;
  }

  public void setClientVersion(String clientVersion) {
    this.clientVersion = clientVersion;
  }

  @Column(name = "login_time")
  public Long getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Long loginTime) {
    this.loginTime = loginTime;
  }

  @Column(name = "session_id")
  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  @Column(name = "session_create_time")
  public Long getSessionCreateTime() {
    return sessionCreateTime;
  }

  public void setSessionCreateTime(Long sessionCreateTime) {
    this.sessionCreateTime = sessionCreateTime;
  }

  @Column(name = "logout_time")
  public Long getLogoutTime() {
    return logoutTime;
  }

  public void setLogoutTime(Long logoutTime) {
    this.logoutTime = logoutTime;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }


}
