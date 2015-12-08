package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;

/**
 *后视镜user与二维码关联表
 * Author: ndong
 * Date: 2015-4-28
 * Time: 14:38
 */
public class AppUserWXQRCodeDTO {
  private Long id;
  private Long qrCodeId;
  private String publicNo;
  private String appUserNo;
  private DeletedType deleted;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getQrCodeId() {
    return qrCodeId;
  }

  public void setQrCodeId(Long qrCodeId) {
    this.qrCodeId = qrCodeId;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
