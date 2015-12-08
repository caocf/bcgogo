package com.bcgogo.config.dto.image;

import com.bcgogo.enums.common.ObjectStatus;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-29
 * Time: 上午11:11
 * To change this template use File | Settings | File Templates.
 */
public class ImageInfoDTO implements Serializable {
  private Long id;
  private Long shopId;
  private String path;
  private ObjectStatus status;
  private Long createdTime;
  private String appUserNo;

  public ImageInfoDTO() {
  }

  public ImageInfoDTO(Long shopId, String path) {
    this.shopId = shopId;
    this.path = path;
    this.status = ObjectStatus.ENABLED;
    this.createdTime = System.currentTimeMillis();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ObjectStatus getStatus() {
    return status;
  }

  public void setStatus(ObjectStatus status) {
    this.status = status;
  }

  public Long getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }
}
