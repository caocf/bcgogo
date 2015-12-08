package com.bcgogo.user.dto;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-7
 * Time: 16:42
 */
public class AccidentSpecialistDTO {
  private Long id;
  private String idStr;
  private String name;
  private String mobile;
  private Long shopId;
  private String openId;
  private String wxName;
  private String wxNickName;
  private DeletedType deleted;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr= StringUtil.valueOf(id);
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public String getWxName() {
    return wxName;
  }

  public void setWxName(String wxName) {
    this.wxName = wxName;
  }

  public String getWxNickName() {
    return wxNickName;
  }

  public void setWxNickName(String wxNickName) {
    this.wxNickName = wxNickName;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
