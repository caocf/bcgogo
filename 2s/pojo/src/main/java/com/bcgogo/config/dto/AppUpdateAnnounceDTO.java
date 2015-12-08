package com.bcgogo.config.dto;

import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUpdateType;
import com.bcgogo.enums.app.AppUserType;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-20
 * Time: 下午2:43
 */
public class AppUpdateAnnounceDTO {

  private String description;
  private AppPlatform appPlatform;
  private String appVersion;
  private AppUpdateType appUpdateType;
  private AppUserType appUserType;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AppPlatform getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(AppPlatform appPlatform) {
    this.appPlatform = appPlatform;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public AppUpdateType getAppUpdateType() {
    return appUpdateType;
  }

  public void setAppUpdateType(AppUpdateType appUpdateType) {
    this.appUpdateType = appUpdateType;
  }

  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }
}
