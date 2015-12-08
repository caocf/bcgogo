package com.bcgogo.config.model;

import com.bcgogo.api.AppUserImageDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:23
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "image_info")
public class ImageInfo extends LongIdentifier {
  private Long shopId;
  private String path;
  private ObjectStatus status;
  private Long createdTime;
  private String appUserNo;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  @Column(name = "created_time")
  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public ImageInfoDTO toDTO(){
    ImageInfoDTO imageInfoDTO = new ImageInfoDTO();
    imageInfoDTO.setShopId(this.getShopId());
    imageInfoDTO.setStatus(this.getStatus());
    imageInfoDTO.setId(this.getId());
    imageInfoDTO.setCreatedTime(this.getCreatedTime());
    imageInfoDTO.setPath(this.getPath());
    imageInfoDTO.setAppUserNo(this.getAppUserNo());
    return imageInfoDTO;
  }

  public void fromDTO(ImageInfoDTO imageInfoDTO){
    this.setShopId(imageInfoDTO.getShopId());
    this.setStatus(imageInfoDTO.getStatus());
    this.setCreatedTime(imageInfoDTO.getCreatedTime());
    this.setPath(imageInfoDTO.getPath());
    this.setAppUserNo(imageInfoDTO.getAppUserNo());
  }

  public void fromAppUserImageDTO(String appUserNo, AppUserImageDTO appUserImageDTO){
    setAppUserNo(appUserNo);
    if(appUserImageDTO != null){
      this.setPath(appUserImageDTO.getImagePath());
      this.setCreatedTime(System.currentTimeMillis());
      this.setStatus(ObjectStatus.ENABLED);
    }
  }
}
