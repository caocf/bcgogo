package com.bcgogo.driving.model;

import com.bcgogo.driving.model.base.LongIdentifier;
import com.bcgogo.pojox.api.AppPlatform;
import com.bcgogo.pojox.api.DriveLogDTO;
import com.bcgogo.pojox.api.DriveLogPlaceNoteDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-14
 * Time: 上午9:32
 */
@Entity
@Table(name = "drive_log_place_note")
public class DriveLogPlaceNote extends LongIdentifier {
  private String appUserNo;
  private Long driveLogId;
  private String placeNotes;//踩点信息
  private AppPlatform appPlatform;

  public void fromDriveLogDTO(DriveLogDTO driveLogDTO) {
    if (driveLogDTO != null) {
      this.setAppUserNo(driveLogDTO.getAppUserNo());
      this.setDriveLogId(driveLogDTO.getId());
      this.setPlaceNotes(driveLogDTO.getPlaceNotes());
      this.setAppPlatform(driveLogDTO.getAppPlatform());
    }
  }

  //后视镜专用
  public void fromDriveLogDTO_mirror(DriveLogDTO driveLogDTO) {
    if (driveLogDTO != null) {
      this.setAppUserNo(driveLogDTO.getAppUserNo());
      this.setDriveLogId(driveLogDTO.getId());
      this.setAppPlatform(AppPlatform.MIRROR);
    }
  }

  public DriveLogPlaceNoteDTO toDTO() {
    DriveLogPlaceNoteDTO dto = new DriveLogPlaceNoteDTO();
    dto.setAppUserNo(this.getAppUserNo());
    dto.setDriveLogId(this.getId());
    dto.setPlaceNotes(this.getPlaceNotes());
    dto.setAppPlatform(this.getAppPlatform());
    return dto;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "drive_log_id")
  public Long getDriveLogId() {
    return driveLogId;
  }

  public void setDriveLogId(Long driveLogId) {
    this.driveLogId = driveLogId;
  }

  @Column(name = "place_notes")
  public String getPlaceNotes() {
    return placeNotes;
  }

  public void setPlaceNotes(String placeNotes) {
    this.placeNotes = placeNotes;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "app_platform")
  public AppPlatform getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(AppPlatform appPlatform) {
    this.appPlatform = appPlatform;
  }


}
