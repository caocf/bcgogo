package com.bcgogo.api;

import com.bcgogo.enums.DataKind;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.user.Status;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午12:50
 */
public class XAppUserLoginInfoDTO implements Serializable {
  private String id;
  private Long appUserId;
  private String appUserNo;//用户账号
  private AppPlatform platform;//手机平台（ios还是android)
  private String platformVersion;//手机平台版本
  private String appVersion;//app版本
  private String mobileModel;//手机型号
  private ImageVersion imageVersion;//手机分辨率
  private Long loginTime;//登陆时间
  private String sessionId;// sessionId
  private Long sessionCreateTime;
  private Long logoutTime;//登出时间
  private Status status;// 登陆状态
  private Long timeoutFlag = System.currentTimeMillis();
  private DataKind dataKind;
  private AppUserType appUserType;//appUser Type

  public void from(AppUserDTO dto) {
    if (dto != null) {
      this.setDataKind(dto.getDataKind());
      this.setAppUserId(dto.getId());
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public AppPlatform getPlatform() {
    return platform;
  }

  public void setPlatform(AppPlatform platform) {
    this.platform = platform;
  }

  public String getPlatformVersion() {
    return platformVersion;
  }

  public void setPlatformVersion(String platformVersion) {
    this.platformVersion = platformVersion;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public ImageVersion getImageVersion() {
    return imageVersion;
  }

  public void setImageVersion(ImageVersion imageVersion) {
    this.imageVersion = imageVersion;
  }

  public Long getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(Long loginTime) {
    this.loginTime = loginTime;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Long getLogoutTime() {
    return logoutTime;
  }

  public void setLogoutTime(Long logoutTime) {
    this.logoutTime = logoutTime;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getSessionCreateTime() {
    return sessionCreateTime;
  }

  public void setSessionCreateTime(Long sessionCreateTime) {
    this.sessionCreateTime = sessionCreateTime;
  }

  public String getMobileModel() {
    return mobileModel;
  }

  public void setMobileModel(String mobileModel) {
    this.mobileModel = mobileModel;
  }

  public Long getTimeoutFlag() {
    return timeoutFlag;
  }

  public void setTimeoutFlag(Long timeoutFlag) {
    this.timeoutFlag = timeoutFlag;
  }

  public DataKind getDataKind() {
    return dataKind;
  }

  public void setDataKind(DataKind dataKind) {
    this.dataKind = dataKind;
  }


  public Long getAppUserId() {
    return appUserId;
  }

  public void setAppUserId(Long appUserId) {
    this.appUserId = appUserId;
  }

  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }
}
