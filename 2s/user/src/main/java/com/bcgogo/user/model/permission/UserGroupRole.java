package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.UserGroupRoleDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 */
@Entity
@Table(name = "user_group_role")
public class UserGroupRole extends LongIdentifier implements Cacheable {
  private Long userGroupId;
  private Long roleId;
  private Long syncTime;


  public UserGroupRole() {
  }

  public UserGroupRole(UserGroupRoleDTO userGroupRoleDTO) {
    this.setId(userGroupRoleDTO.getId());
    this.setRoleId(userGroupRoleDTO.getRoleId());
    this.setUserGroupId(userGroupRoleDTO.getUserGroupId());
  }

  public UserGroupRole fromDTO(UserGroupRoleDTO userGroupRoleDTO) {
    this.setId(userGroupRoleDTO.getId());
    this.setRoleId(userGroupRoleDTO.getRoleId());
    this.setUserGroupId(userGroupRoleDTO.getUserGroupId());

    return this;
  }

  public UserGroupRoleDTO toDTO() {
    UserGroupRoleDTO userGroupRoleDTO = new UserGroupRoleDTO();

    userGroupRoleDTO.setId(this.getId());
    userGroupRoleDTO.setRoleId(this.getRoleId());
    userGroupRoleDTO.setUserGroupId(this.getUserGroupId());

    return userGroupRoleDTO;
  }

  @Column(name = "user_group_id")
  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  @Column(name = "role_id")
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  @Transient
  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  @Override
  public String assembleKey() {
    return MemcachePrefix.userGroup.getValue() + String.valueOf(getId());
  }
}

