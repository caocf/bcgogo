package com.bcgogo.user.model;

import com.bcgogo.common.TreeMenuDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-9
 * Time: 下午5:24
 * menu 菜单
 */
@Entity
@Table(name = "tree_menu")
public class TreeMenu extends LongIdentifier {
  private String text; //系统管理
  private String component;//组件类型 Ext.app.Panel
  private String description; //描述
  private String type;       // URL or COMPONENT
  private String iconCls;   //css class
  private Long sort;   //同级顺序
  private Long parentId;
  private Long roleId;
  private String leaf;

  public TreeMenuDTO toDTO() {
    TreeMenuDTO treeMenuDTO = new TreeMenuDTO();
    treeMenuDTO.setId(this.getId());
    treeMenuDTO.setText(this.getText());
    treeMenuDTO.setComponent(this.getComponent());
    treeMenuDTO.setDescription(this.getDescription());
    treeMenuDTO.setIconCls(this.getIconCls());
    treeMenuDTO.setParentId(this.getParentId());
    treeMenuDTO.setRoleId(this.getRoleId());
    treeMenuDTO.setSort(this.getSort());
    treeMenuDTO.setType(this.getType());
    treeMenuDTO.setLeaf(this.getLeaf());
    return treeMenuDTO;
  }

  public void fromDTO(TreeMenuDTO treeMenuDTO) {
    this.setId(treeMenuDTO.getId());
    this.setText(treeMenuDTO.getText());
    this.setComponent(treeMenuDTO.getComponent());
    this.setDescription(treeMenuDTO.getDescription());
    this.setIconCls(treeMenuDTO.getIconCls());
    this.setParentId(treeMenuDTO.getParentId());
    this.setRoleId(treeMenuDTO.getRoleId());
    this.setSort(treeMenuDTO.getSort());
    this.setType(treeMenuDTO.getType());
    this.setLeaf(treeMenuDTO.getLeaf());
  }

  @Column(name = "text", length = 50)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Column(name = "component", length = 50)
  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  @Column(name = "description", length = 100)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "type", length = 50)
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Column(name = "icon_class", length = 50)
  public String getIconCls() {
    return iconCls;
  }

  public void setIconCls(String iconCls) {
    this.iconCls = iconCls;
  }

  @Column(name = "sort")
  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getLeaf() {
    return leaf;
  }

  public void setLeaf(String leaf) {
    this.leaf = leaf;
  }

  @Column(name = "role_id")
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }
}
