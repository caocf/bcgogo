package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.user.AppUserWXQRCodeDTO;

import javax.persistence.*;

/**
 * appUser和wxQRCode关联关系
 * Author: ndong
 * Date: 2015-4-24
 * Time: 18:06
 */
@Entity
@Table(name = "app_user_wx_qr_code")
public class AppUserWXQRCode extends LongIdentifier {
  private Long qrCodeId;
  private String publicNo;
  private String appUserNo;
  private DeletedType deleted = DeletedType.FALSE;

  public AppUserWXQRCodeDTO toDTO() {
    AppUserWXQRCodeDTO codeDTO = new AppUserWXQRCodeDTO();
    codeDTO.setId(this.getId());
    codeDTO.setQrCodeId(this.getQrCodeId());
    codeDTO.setPublicNo(this.getPublicNo());
    codeDTO.setAppUserNo(this.getAppUserNo());
    codeDTO.setDeleted(this.getDeleted());
    return codeDTO;
  }

  public void fromDTO(AppUserWXQRCodeDTO codeDTO) {
    this.setId(codeDTO.getId());
    this.setQrCodeId(codeDTO.getQrCodeId());
    this.setPublicNo(codeDTO.getPublicNo());
    this.setAppUserNo(codeDTO.getAppUserNo());
    this.setDeleted(codeDTO.getDeleted());
  }

  @Column(name = "qr_code_id")
  public Long getQrCodeId() {
    return qrCodeId;
  }

  public void setQrCodeId(Long qrCodeId) {
    this.qrCodeId = qrCodeId;
  }

  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
