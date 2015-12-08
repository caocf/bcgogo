package com.bcgogo.user.model.permission;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.UserGroupUserDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 */

@Entity
@Table(name = "user_group_user")
public class UserGroupUser extends LongIdentifier  {
  private Long userGroupId;
  private Long userId;

  public UserGroupUser() {
  }

  public UserGroupUser(UserGroupUserDTO userGroupUserDTO) {
    this.setId(userGroupUserDTO.getId());
    this.setUserGroupId(userGroupUserDTO.getUserGroupId());
    this.setUserId(userGroupUserDTO.getUserId());
  }

  public UserGroupUser fromDTO(UserGroupUserDTO userGroupUserDTO) {
    this.setId(userGroupUserDTO.getId());
    this.setUserGroupId(userGroupUserDTO.getUserGroupId());
    this.setUserId(userGroupUserDTO.getUserId());
    return this;
  }

  public UserGroupUserDTO toDTO() {
    UserGroupUserDTO userGroupUserDTO = new UserGroupUserDTO();
    userGroupUserDTO.setId(this.getId());
    userGroupUserDTO.setUserGroupId(this.getUserGroupId());
    userGroupUserDTO.setUserId(this.getUserId());

    return userGroupUserDTO;
  }

  @Column(name = "user_group_id")
  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

}
