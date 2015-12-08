package com.bcgogo.user;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.UploadStatus;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-31
 * Time: 17:52
 */
public class ImpactVideoDTO {
  private Long id;
  private String uuid;
  private String name;
  private String path;
  private String backupPath;
  //视频大小
  private Long size;
  //视频分块数量
  private Long blockNum;
  //上传时间
  private Long uploadTime;
  private UploadStatus uploadStatus;
  private DeletedType deleted;
  private String appUserNo;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getBackupPath() {
    return backupPath;
  }

  public void setBackupPath(String backupPath) {
    this.backupPath = backupPath;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Long getBlockNum() {
    return blockNum;
  }

  public void setBlockNum(Long blockNum) {
    this.blockNum = blockNum;
  }

  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  public UploadStatus getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(UploadStatus uploadStatus) {
    this.uploadStatus = uploadStatus;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
