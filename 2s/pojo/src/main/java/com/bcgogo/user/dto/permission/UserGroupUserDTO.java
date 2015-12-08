package com.bcgogo.user.dto.permission;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-10-28
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class UserGroupUserDTO implements Serializable {
  private Long userGroupId;
  private Long userId;
  private Long id;

  public UserGroupUserDTO() {
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

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
