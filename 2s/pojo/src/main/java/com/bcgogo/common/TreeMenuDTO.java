package com.bcgogo.common;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-9
 * Time: 下午5:25
 * menu 菜单
 */
public class TreeMenuDTO {
  private Long id;
  private String text; //系统管理
  private String component;//组件类型 Ext.app.Panel
  private String description; //描述
  private String type;       // URL or COMPONENT
  private String iconCls;
  private Long sort;   //同级顺序
  private Long parentId;
  private Long roleId;
  private String leaf;

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public String getLeaf() {
    return leaf;
  }

  public void setLeaf(String leaf) {
    this.leaf = leaf;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getIconCls() {
    return iconCls;
  }

  public void setIconCls(String iconCls) {
    this.iconCls = iconCls;
  }

  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }
}
