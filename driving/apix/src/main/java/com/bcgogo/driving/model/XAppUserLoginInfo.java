package com.bcgogo.driving.model;

import com.bcgogo.driving.model.mongodb.XLongIdentifier;
import com.bcgogo.driving.model.mongodb.XNumberLong;
import com.bcgogo.pojox.api.AppPlatform;
import com.bcgogo.pojox.api.XAppUserLoginInfoDTO;
import com.bcgogo.pojox.enums.Status;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.pojox.enums.config.ImageVersion;
import com.bcgogo.pojox.util.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 上午11:48
 */
public class XAppUserLoginInfo extends XLongIdentifier {
  private String appUserNo;//用户账号
  private AppPlatform platform;//手机平台（IOS,ANDROID)
  private String platformVersion;//手机平台版本
  private String mobileModel;//手机型号
  private String appVersion;//app版本
  private ImageVersion imageVersion;//手机分辨率
  private XNumberLong loginTime;//登陆时间
  private String sessionId;// sessionId
  private XNumberLong sessionCreateTime;
  //  private XNumberLong logoutTime;//登出时间
  private Status status = Status.ACTIVE;// 登陆状态
  private AppUserType appUserType;//appUser Type

  public XAppUserLoginInfo() {
    super();
  }


//  public void from(LoginDTO dto) {
//    setAppUserNo(dto.getUserNo());
//    setPlatform(dto.getPlatform());
//    setPlatformVersion(dto.getPlatformVersion());
//    setAppVersion(dto.getAppVersion());
//    setImageVersion(dto.getImageVersionEnum());
//    setLoginTime(new XNumberLong(System.currentTimeMillis()));
//    setSessionId(dto.getSessionId());
//    setLogoutTime(new XNumberLong(-1L));
//    setSessionCreateTime(new XNumberLong(System.currentTimeMillis()));
//    setMobileModel(dto.getMobileModel());
//    setStatus(Status.active);
//    setAppUserType(dto.getAppUserType());
//  }

  public void fromDTO(XAppUserLoginInfoDTO loginInfoDTO) {
    this.set_id(loginInfoDTO.getId());
    this.setAppUserNo(loginInfoDTO.getAppUserNo());
    this.setPlatform(loginInfoDTO.getPlatform());
    this.setPlatformVersion(loginInfoDTO.getPlatformVersion());
    this.setAppVersion(loginInfoDTO.getAppVersion());
    this.setImageVersion(loginInfoDTO.getImageVersion());
    this.setLoginTime(new XNumberLong(loginInfoDTO.getLoginTime()));
    this.setSessionId(loginInfoDTO.getSessionId());
//    this.setLogoutTime(new XNumberLong(loginInfoDTO.getLogoutTime()));
    this.setSessionCreateTime(new XNumberLong(loginInfoDTO.getSessionCreateTime()));
    this.setMobileModel(loginInfoDTO.getMobileModel());
//    this.setStatus(loginInfoDTO.getStatus());
    this.setAppUserType(loginInfoDTO.getAppUserType());
  }

  public XAppUserLoginInfoDTO toDTO() {
    XAppUserLoginInfoDTO dto = new XAppUserLoginInfoDTO();
    dto.setId(get_id().get$oid());
    dto.setAppUserNo(getAppUserNo());
    dto.setPlatform(getPlatform());
    dto.setPlatformVersion(getPlatformVersion());
    dto.setAppVersion(getAppVersion());
    dto.setImageVersion(getImageVersion());
    dto.setSessionId(getSessionId());
    dto.setLoginTime(NumberUtil.longValue(this.getLoginTime().get$numberLong()));
    dto.setSessionCreateTime(NumberUtil.longValue(this.getSessionCreateTime().get$numberLong()));
//    dto.setLogoutTime(NumberUtil.longValue(this.getLogoutTime().get$numberLong()));
    dto.setMobileModel(getMobileModel());
//    dto.setStatus(getStatus());
    dto.setAppUserType(getAppUserType());
    return dto;
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

  public String getMobileModel() {
    return mobileModel;
  }

  public void setMobileModel(String mobileModel) {
    this.mobileModel = mobileModel;
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

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }

  public XNumberLong getLoginTime() {
    return loginTime;
  }

  public void setLoginTime(XNumberLong loginTime) {
    this.loginTime = loginTime;
  }

  public XNumberLong getSessionCreateTime() {
    return sessionCreateTime;
  }

  public void setSessionCreateTime(XNumberLong sessionCreateTime) {
    this.sessionCreateTime = sessionCreateTime;
  }

//  public XNumberLong getLogoutTime() {
//    return logoutTime;
//  }
//
//  public void setLogoutTime(XNumberLong logoutTime) {
//    this.logoutTime = logoutTime;
//  }
}
