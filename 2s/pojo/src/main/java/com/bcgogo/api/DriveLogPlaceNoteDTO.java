package com.bcgogo.api;

import com.bcgogo.enums.app.AppPlatform;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-23
 * Time: 上午11:36
 */
public class DriveLogPlaceNoteDTO implements Serializable {
  private String appUserNo;
  private Long driveLogId;
  private String placeNotes;//踩点信息
  private AppPlatform appPlatform;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getDriveLogId() {
    return driveLogId;
  }

  public void setDriveLogId(Long driveLogId) {
    this.driveLogId = driveLogId;
  }

  public String getPlaceNotes() {
    return placeNotes;
  }

  public void setPlaceNotes(String placeNotes) {
    this.placeNotes = placeNotes;
  }

  public AppPlatform getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(AppPlatform appPlatform) {
    this.appPlatform = appPlatform;
  }
}
