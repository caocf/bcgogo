package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.user.Status;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-10-28
 * Time: 上午11:11
 */
public class UserGroupRoleDTO implements Serializable {
  private Long userGroupId;
  private Long roleId;
  private Long id;
  private Status status;

  public UserGroupRoleDTO() {
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

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
