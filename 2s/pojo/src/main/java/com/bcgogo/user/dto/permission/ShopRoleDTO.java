package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.user.Status;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-9-22
 * Time: 下午12:16
 */
public class ShopRoleDTO {
  private Long id;
  private Long roleId;
  private Long shopId;
  private Long shopVersionId;
  private Status status;


  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
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

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
