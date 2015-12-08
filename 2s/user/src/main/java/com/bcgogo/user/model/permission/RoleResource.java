package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.RoleResourceDTO;

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
@Table(name = "role_resource")
public class RoleResource extends LongIdentifier implements Cacheable {
  private Long roleId;
  private Long resourceId;
  private Long syncTime;

  public RoleResource(Long roleId, Long resourceId) {
    this.roleId = roleId;
    this.resourceId = resourceId;
  }

  public RoleResource(RoleResourceDTO roleResourceDTO) {
    this.setId(roleResourceDTO.getId());
    this.setRoleId(roleResourceDTO.getRoleId());
    this.setResourceId(roleResourceDTO.getResourceId());
  }

  public RoleResource fromDTO(RoleResourceDTO roleResourceDTO) {
    this.setId(roleResourceDTO.getId());
    this.setResourceId(roleResourceDTO.getResourceId());
    this.setRoleId(roleResourceDTO.getRoleId());
    return this;
  }

  public RoleResourceDTO toDTO() {
    RoleResourceDTO roleResourceDTO = new RoleResourceDTO();
    roleResourceDTO.setResourceId(resourceId);
    roleResourceDTO.setRoleId(roleId);
    roleResourceDTO.setId(getId());
    return roleResourceDTO;
  }

  public RoleResource() {
  }

  @Column(name = "role_id")
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  @Column(name = "resource_id")
  public Long getResourceId() {
    return resourceId;
  }

  public void setResourceId(Long resourceId) {
    this.resourceId = resourceId;
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
    return MemcachePrefix.roleResource.getValue() + String.valueOf(getId());
  }

}
