package com.bcgogo.user.model.app;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "app_version_history")
public class AppVersionHistory extends LongIdentifier {
  private String platform;//手机端平台ios或者android
  private String appVersion;//app版本
  private Long publishTime;//发布时间

  public AppVersionHistory() {
    super();
  }

  @Column(name = "platform")
  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  @Column(name = "app_version")
  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  @Column(name = "publish_time")
  public Long getPublishTime() {
    return publishTime;
  }

  public void setPublishTime(Long publishTime) {
    this.publishTime = publishTime;
  }
}
