package com.bcgogo.user.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.UploadStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.ImpactVideoDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-30
 * Time: 15:44
 */
@Entity
@Table(name = "impact_video")
public class ImpactVideo extends LongIdentifier {
  private String uuid;//碰撞uuid
  private String name;
  private String path;
  //视频大小
  private Long size;
  //视频分块数量
  private Long blockNum;
  //上传时间
  private Long uploadTime;
  private UploadStatus uploadStatus;
  private DeletedType deleted = DeletedType.FALSE;
  private String appUserNo;

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "uuid")
  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }


  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Column(name = "size")
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  @Column(name = "block_num")
  public Long getBlockNum() {
    return blockNum;
  }

  public void setBlockNum(Long blockNum) {
    this.blockNum = blockNum;
  }

  @Column(name = "upload_time")
  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "upload_status")
  public UploadStatus getUploadStatus() {
    return uploadStatus;
  }

  public void setUploadStatus(UploadStatus uploadStatus) {
    this.uploadStatus = uploadStatus;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "deleted")
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }


  public void fromDTO(ImpactVideoDTO impactVideoDTO) {
    this.setId(impactVideoDTO.getId());
    this.setUuid(impactVideoDTO.getUuid());
    this.setName(impactVideoDTO.getName());
    this.setPath(impactVideoDTO.getPath());
    this.setSize(impactVideoDTO.getSize());
    this.setUploadTime(impactVideoDTO.getUploadTime());
    this.setUploadStatus(impactVideoDTO.getUploadStatus());
    this.setBlockNum(impactVideoDTO.getBlockNum());
    this.setAppUserNo(impactVideoDTO.getAppUserNo());
    this.setDeleted(impactVideoDTO.getDeleted());
    this.setAppUserNo(impactVideoDTO.getAppUserNo());
  }

  public ImpactVideoDTO toDTO() {
    ImpactVideoDTO impactVideoDTO = new ImpactVideoDTO();
    impactVideoDTO.setId(this.getId());
    impactVideoDTO.setUuid(this.getUuid());
    impactVideoDTO.setName(this.getName());
    impactVideoDTO.setPath(this.getPath());
    impactVideoDTO.setSize(this.getSize());
    impactVideoDTO.setUploadTime(this.getUploadTime());
    impactVideoDTO.setUploadStatus(this.getUploadStatus());
    impactVideoDTO.setBlockNum(this.getBlockNum());
    impactVideoDTO.setDeleted(this.getDeleted());
    impactVideoDTO.setAppUserNo(this.getAppUserNo());
    return impactVideoDTO;
  }

}
