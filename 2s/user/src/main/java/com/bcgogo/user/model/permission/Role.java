package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.SystemType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.RoleDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:52
 * 某种角色在系统中被赋予一批读写权限，若干个角色被添加给某个用户组后，这个用户组就享有这些角色所拥有权限的并集
 */
@Entity
@Table(name = "role")
public class Role extends LongIdentifier implements Cacheable {
  private String name;
  private String status;
  private Long sort;
  private String memo;
  private SystemType type;
  private String value;
  private Long moduleId;
  private Boolean hasThisRole;

  public Role() {
  }

  public RoleDTO toDTO() {
    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setName(this.getName());
    roleDTO.setStatus(this.getStatus());
    roleDTO.setId(this.getId());
    roleDTO.setMemo(this.getMemo());
    roleDTO.setType(this.getType());
    roleDTO.setValue(this.getValue());
    roleDTO.setModuleId(this.getModuleId());
    return roleDTO;
  }

  public void fromDTO(RoleDTO roleDTO) {
    this.setName(roleDTO.getName());
    this.setStatus(roleDTO.getStatus());
    this.setId(roleDTO.getId());
    this.setMemo(roleDTO.getMemo());
    this.setValue(roleDTO.getValue());
    this.setModuleId(roleDTO.getModuleId());
    this.setType(roleDTO.getType());
    this.setSort(roleDTO.getSort());
  }

  public Node toNode() {
    Node node = new Node();
    node.setId(this.getId());
    node.setText(this.getValue());
    node.setName(this.getName());
    node.setValue(this.getValue());
    node.setParentId(this.getModuleId());
    node.setType(Node.Type.ROLE);
    node.setSystemType(this.getType());
    node.setLeaf(true);
    node.setSort(this.getSort());
    node.setHasThisNode(this.getHasThisRole());
    return node;
  }

  public CheckNode toCheckNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getId());
    node.setText(this.getValue());
    node.setName(this.getName());
    node.setValue(this.getValue());
    node.setParentId(this.getModuleId());
    node.setType(Node.Type.ROLE);
    node.setSystemType(this.getType());
    node.setLeaf(true);
    node.setSort(this.getSort());
    node.setChecked(this.getHasThisRole());
    return node;
  }

  @Column(name = "value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Column(name = "sort")
  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  @Column(name = "memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Override
  public String assembleKey() {
    return MemcachePrefix.role.getValue() + String.valueOf(getId());
  }

  @Column(name = "module_id")
  public Long getModuleId() {
    return moduleId;
  }

  public void setModuleId(Long moduleId) {
    this.moduleId = moduleId;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public SystemType getType() {
    return type;
  }

  public void setType(SystemType type) {
    this.type = type;
  }

  public void setHasThisRole(Boolean hasThisRole) {
    this.hasThisRole = hasThisRole;
  }

  @Transient
  public Boolean getHasThisRole() {
    return hasThisRole;
  }
}