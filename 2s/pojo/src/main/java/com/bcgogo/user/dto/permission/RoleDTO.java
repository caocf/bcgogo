package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.SystemType;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 上午9:21
 */
public class RoleDTO {
  private Long id;
  private String name;
  private String status;
  private Long sort;
  private String memo;
  private SystemType type;
  private String value;
  private Long moduleId;
  private Long userGroupId;
  private boolean hasCheckedByUserGroup; //根据UserGroup

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public SystemType getType() {
    return type;
  }

  public void setType(SystemType type) {
    this.type = type;
  }

  public boolean equals(Object o) {
    return !(o == null || ((RoleDTO) o).id == null || this.id == null) && (((RoleDTO) o).id.intValue() == this.id.intValue());
  }

  public int hashCode() {
    if (id != null)
      return id.intValue();
    else
      return 0;
  }

  public Long getModuleId() {
    return moduleId;
  }

  public void setModuleId(Long moduleId) {
    this.moduleId = moduleId;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }

  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  public boolean isHasCheckedByUserGroup() {
    return hasCheckedByUserGroup;
  }

  public void setHasCheckedByUserGroup(boolean hasCheckedByUserGroup) {
    this.hasCheckedByUserGroup = hasCheckedByUserGroup;
  }
}
