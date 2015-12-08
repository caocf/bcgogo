package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;

/**
 * 微信用户(openId)和appUser的关联
 * Author: ndong
 * Date: 2015-4-28
 * Time: 17:45
 */
public class AppWXUserDTO {
  private Long id;
  private String appUserNo;
  private String openId;
  private String headImgUrl;
  private String name;
  private String nickName;
  private DeletedType deleted;
  private String vehicleNo;//车牌号，用于界面下拉列表


  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public String getHeadImgUrl() {
    return headImgUrl;
  }

  public void setHeadImgUrl(String headImgUrl) {
    this.headImgUrl = headImgUrl;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
