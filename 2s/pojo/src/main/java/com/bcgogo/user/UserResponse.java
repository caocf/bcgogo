package com.bcgogo.user;

import com.bcgogo.user.dto.UserDTO;

import java.io.Serializable;

/**
 * User: Xiao Jian
 * Date: 12-1-31
 */
public class UserResponse extends UserDTO implements Serializable {
  private UserDTO userDTO;
  private Long userGroupId;
  private String userGroupName;

  public UserResponse(UserDTO userDTO) {
    this.userDTO = userDTO;
  }

  public UserDTO getUserDTO() {
    return this.userDTO;
  }

  public Long getUserGroupId() {
    return this.userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  public String getUserGroupName() {
    return this.userGroupName;
  }

  public void setUserGroupName(String userGroupName) {
    this.userGroupName = userGroupName;
  }

}
