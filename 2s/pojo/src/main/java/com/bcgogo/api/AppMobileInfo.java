package com.bcgogo.api;

import com.bcgogo.enums.app.AppPlatform;

/**
 * app手机信息
 * Created by Hans on 13-12-25.
 */
public class AppMobileInfo {
  protected AppPlatform platform;   //用户手机系统平台类型        *
  protected String appVersion;      //APP版本号                  *
  protected String platformVersion; //用户手机系统平台版本
  protected String mobileModel;     //用户手机型号
  protected String imageVersion;

  public AppPlatform getPlatform() {
    return platform;
  }

  public void setPlatform(AppPlatform platform) {
    this.platform = platform;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
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

  public String getImageVersion() {
    return imageVersion;
  }

  public void setImageVersion(String imageVersion) {
    this.imageVersion = imageVersion;
  }

  @Override
  public String toString() {
    return "AppMobileInfo{" +
        "platform=" + platform +
        ", appVersion='" + appVersion + '\'' +
        ", platformVersion='" + platformVersion + '\'' +
        ", mobileModel='" + mobileModel + '\'' +
        ", imageVersion='" + imageVersion + '\'' +
        '}';
  }
}
