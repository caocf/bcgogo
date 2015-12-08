package com.bcgogo.user.model.app;

import com.bcgogo.api.AppUserLoginInfoDTO;
import com.bcgogo.api.LoginDTO;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "app_user_login_info")
public class AppUserLoginInfo extends LongIdentifier {
  private String appUserNo;//用户账号
  private AppPlatform platform;//手机平台（IOS,ANDROID)
  private String platformVersion;//手机平台版本
  private String mobileModel;//手机型号
  private String appVersion;//app版本
  private ImageVersion imageVersion;//手机分辨率
  private Long loginTime;//登陆时间
  private String sessionId;// sessionId
  private Long sessionCreateTime;
  private Long logoutTime;//登出时间
  private Status status = Status.active;// 登陆状态
  private AppUserType appUserType;//appUser Type


  public AppUserLoginInfo(LoginDTO dto) {
    setAppUserNo(dto.getUserNo());
    setPlatform(dto.getPlatform());
    setPlatformVersion(dto.getPlatformVersion());
    setAppVersion(dto.getAppVersion());
    setImageVersion(dto.getImageVersionEnum());
    setLoginTime(System.currentTimeMillis());
    setSessionId(dto.getSessionId());
    setLogoutTime(-1l);
    setSessionCreateTime(System.currentTimeMillis());
    setMobileModel(dto.getMobileModel());
    setStatus(Status.active);
    setAppUserType(dto.getAppUserType());
  }

  public void from(LoginDTO dto) {
    setAppUserNo(dto.getUserNo());
    setPlatform(dto.getPlatform());
    setPlatformVersion(dto.getPlatformVersion());
    setAppVersion(dto.getAppVersion());
    setImageVersion(dto.getImageVersionEnum());
    setLoginTime(System.currentTimeMillis());
    setSessionId(dto.getSessionId());
    setLogoutTime(-1l);
    setSessionCreateTime(System.currentTimeMillis());
    setMobileModel(dto.getMobileModel());
    setStatus(Status.active);
    setAppUserType(dto.getAppUserType());
  }

   public void fromDTO(AppUserLoginInfoDTO loginInfoDTO) {
     this.setId(loginInfoDTO.getId());
    this.setAppUserNo(loginInfoDTO.getAppUserNo());
    this.setPlatform(loginInfoDTO.getPlatform());
    this.setPlatformVersion(loginInfoDTO.getPlatformVersion());
    this.setAppVersion(loginInfoDTO.getAppVersion());
    this.setImageVersion(loginInfoDTO.getImageVersion());
    this.setLoginTime(loginInfoDTO.getLoginTime());
    this.setSessionId(loginInfoDTO.getSessionId());
    this.setLogoutTime(loginInfoDTO.getLogoutTime());
    this.setSessionCreateTime(loginInfoDTO.getSessionCreateTime());
    this.setMobileModel(loginInfoDTO.getMobileModel());
    this.setStatus(loginInfoDTO.getStatus());
    this.setAppUserType(loginInfoDTO.getAppUserType());
  }

  public AppUserLoginInfoDTO toDTO() {
    AppUserLoginInfoDTO dto = new AppUserLoginInfoDTO();
    dto.setId(getId());
    dto.setAppUserNo(getAppUserNo());
    dto.setPlatform(getPlatform());
    dto.setPlatformVersion(getPlatformVersion());
    dto.setAppVersion(getAppVersion());
    dto.setImageVersion(getImageVersion());
    dto.setLoginTime(getLoginTime());
    dto.setSessionId(getSessionId());
    dto.setLogoutTime(getLogoutTime());
    dto.setSessionCreateTime(getSessionCreateTime());
    dto.setMobileModel(getMobileModel());
    dto.setStatus(getStatus());
    dto.setAppUserType(getAppUserType());
    return dto;
  }

  public AppUserLoginInfo() {
    super();
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "platform")
  @Enumerated(EnumType.STRING)
  public AppPlatform getPlatform() {
    return platform;
  }

  public void setPlatform(AppPlatform platform) {
    this.platform = platform;
  }

  @Column(name = "platform_version")
  public String getPlatformVersion() {
    return platformVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  @Column(name = "app_version")
  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "image_version")
  public ImageVersion getImageVersion() {
    return imageVersion;
  }

  public void setImageVersion(ImageVersion imageVersion) {
    this.imageVersion = imageVersion;
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

  @Column(name = "logout_time")
  public Long getLogoutTime() {
    return logoutTime;
  }

  public void setLogoutTime(Long logoutTime) {
    this.logoutTime = logoutTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "session_create_time")
  public Long getSessionCreateTime() {
    return sessionCreateTime;
  }

  public void setSessionCreateTime(Long sessionCreateTime) {
    this.sessionCreateTime = sessionCreateTime;
  }

  @Column(name = "mobile_model")
  public String getMobileModel() {
    return mobileModel;
  }

  public void setMobileModel(String mobileModel) {
    this.mobileModel = mobileModel;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type")
  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }
}
