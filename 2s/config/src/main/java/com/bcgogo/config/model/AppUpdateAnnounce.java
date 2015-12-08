package com.bcgogo.config.model;

import com.bcgogo.config.dto.AppUpdateAnnounceDTO;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUpdateType;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-20
 * Time: 下午2:21
 */

@Entity
@Table(name = "app_update_announced")
public class AppUpdateAnnounce extends LongIdentifier {

  private String description;
  private AppPlatform appPlatform;
  private String appVersion;
  private AppUpdateType appUpdateType;
  private AppUserType appUserType;

  public AppUpdateAnnounceDTO toDTO() {
    AppUpdateAnnounceDTO dto = new AppUpdateAnnounceDTO();
    dto.setDescription(getDescription());
    dto.setAppPlatform(getAppPlatform());
    dto.setAppUpdateType(getAppUpdateType());
    dto.setAppVersion(getAppVersion());
    dto.setAppUserType(getAppUserType());
    return dto;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "app_platform")
  @Enumerated(EnumType.STRING)
  public AppPlatform getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(AppPlatform appPlatform) {
    this.appPlatform = appPlatform;
  }

  @Column(name = "app_version")
  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  @Column(name = "app_update_type")
  @Enumerated(EnumType.STRING)
  public AppUpdateType getAppUpdateType() {
    return appUpdateType;
  }

  public void setAppUpdateType(AppUpdateType appUpdateType) {
    this.appUpdateType = appUpdateType;
  }

  @Column(name = "app_user_type")
  @Enumerated(EnumType.STRING)
  public AppUserType getAppUserType() {
    return appUserType;
  }

  public void setAppUserType(AppUserType appUserType) {
    this.appUserType = appUserType;
  }
}
