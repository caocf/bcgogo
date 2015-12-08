package com.bcgogo.user.dto.permission;

import com.bcgogo.enums.SystemType;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-14
 * Time: 上午9:22
 */
public class ModuleDTO {
  private Long id;
  private String name;
  private Long parentId;
  private Long sort;   //同级顺序
  private String value;
  private SystemType type;
  private List<RoleDTO> roles;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
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

  public List<RoleDTO> getRoles() {
    return roles;
  }

  public void setRoles(List<RoleDTO> roles) {
    this.roles = roles;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public SystemType getType() {
    return type;
  }

  public void setType(SystemType type) {
    this.type = type;
  }
}
