package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.user.AppWXUserDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-28
 * Time: 17:43
 */
@Entity
@Table(name = "app_wx_user")
public class AppWXUser extends LongIdentifier {
  private String appUserNo;
  private String openId;
  private DeletedType deleted = DeletedType.FALSE;

  public void fromDTO(AppWXUserDTO dto) {
    this.setId(dto.getId());
    this.setAppUserNo(dto.getAppUserNo());
    this.setOpenId(dto.getOpenId());
    this.setDeleted(dto.getDeleted());
  }

  public AppWXUserDTO toDTO() {
    AppWXUserDTO dto = new AppWXUserDTO();
    dto.setId(getId());
    dto.setAppUserNo(this.getAppUserNo());
    dto.setOpenId(this.getOpenId());
    dto.setDeleted(getDeleted());
    return dto;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }


  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
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
