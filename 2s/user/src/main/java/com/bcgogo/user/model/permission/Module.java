package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.SystemType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.user.dto.permission.RoleDTO;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:49
 * 权限资源在配置时，用户需要参考该权限资源属于哪个模块。例如：“入库单保存功能”资源属于“进销存”模块
 */
@Entity
@Table(name = "module")
public class Module extends LongIdentifier {
  private String name;
  private String value;
  private Long parentId;
  private SystemType type;
  private Long sort;   //同级顺序
  private List<Role> roles = new ArrayList<Role>();

  public Module() {
  }


  public Module(Module module,List<Role> roles) {
    this.copy(module);
    this.roles = roles;
  }

  public Module copy(Module module) {
    this.setId(module.getId());
    this.setName(module.getName());
    this.setParentId(module.getParentId());
    this.setCreationDate(module.getCreationDate());
    this.setLastModified(module.getLastModified());
    this.setRoles(module.getRoles());
    this.setValue(module.getValue());
    this.setType(module.getType());
    return this;
  }

  public void fromDTO(ModuleDTO module) {
    this.setId(module.getId());
    this.setName(module.getName());
    this.setParentId(module.getParentId());
    this.setValue(module.getValue());
    this.setType(module.getType());
    this.setSort(module.getSort());
  }

  public ModuleDTO toDTO() {
    ModuleDTO moduleDTO = new ModuleDTO();
    moduleDTO.setId(this.getId());
    moduleDTO.setName(this.getName());
    moduleDTO.setParentId(this.getParentId());
    moduleDTO.setType(this.getType());
    moduleDTO.setValue(this.getValue());
    if (CollectionUtils.isNotEmpty(this.getRoles())) {
      List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();
      for (Role role : this.getRoles())
        roleDTOList.add(role.toDTO());
      moduleDTO.setRoles(roleDTOList);
    }
    return moduleDTO;
  }

  public Node toNode() {
    Node node = new Node();
    node.setId(this.getId());
    node.setText(this.getValue());
    node.setName(this.getName());
    node.setValue(this.getValue());
    node.setParentId(this.getParentId());
    node.setType(Node.Type.MODULE);
    node.setSystemType(this.getType());
    node.setLeaf(false);
    node.setSort(this.getSort());
    return node;
  }

  public CheckNode toCheckNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getId());
    node.setText(this.getValue());
    node.setName(this.getName());
    node.setValue(this.getValue());
    node.setParentId(this.getParentId());
    node.setType(Node.Type.MODULE);
    node.setLeaf(false);
    node.setSystemType(this.getType());
    node.setSort(this.getSort());
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

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  public SystemType getType() {
    return type;
  }


  @Column(name = "sort")
  public void setType(SystemType type) {
    this.type = type;
  }

  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  @Transient
  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

}
