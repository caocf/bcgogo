package com.bcgogo.user.dto.permission;

import java.io.Serializable;

/**
 * userGro
 */
public class UserGroupShopDTO implements Serializable {
  private Long userGroupId;
  private Long shopVersionId;
  private Long shopId;
  private Long id;

  public UserGroupShopDTO() {
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

}
