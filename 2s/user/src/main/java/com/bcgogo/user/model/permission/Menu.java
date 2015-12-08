package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.MenuDTO;
import com.bcgogo.user.dto.permission.ResourceDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-4-10
 * Time: 下午5:49
 * 特殊的菜单
 */
@Entity
@Table(name = "menu")
public class Menu extends LongIdentifier {
  private Long resourceId;
  private Long parentId;
  private String menuName;
  private String label;
  private String href;
  private Integer grade;
  private Integer sort;

  public MenuDTO toDTO() {
    MenuDTO menuDTO = new MenuDTO();
    menuDTO.setMenuId(this.getId());
    menuDTO.setResourceId(this.getResourceId());
    menuDTO.setLabel(this.getLabel());
    menuDTO.setHref(this.getHref());
    menuDTO.setGrade(this.getGrade());
    menuDTO.setParentId(this.getParentId());
    menuDTO.setSort(this.getSort());
    menuDTO.setMenuName(this.getMenuName());
    return menuDTO;
  }

  public void fromDTO(MenuDTO dto) {
    this.setId(dto.getMenuId());
    this.setResourceId(dto.getResourceId());
    this.setLabel(dto.getLabel());
    this.setHref(dto.getHref());
    this.setGrade(dto.getGrade());
    this.setParentId(dto.getParentId());
    this.setMenuName(dto.getMenuName());
    this.setSort(dto.getSort());
  }

  @Column(name = "resource_id")
  public Long getResourceId() {
    return resourceId;
  }

  public void setResourceId(Long resourceId) {
    this.resourceId = resourceId;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name = "href")
  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  @Column(name = "grade")
  public Integer getGrade() {
    return grade;
  }

  public void setGrade(Integer grade) {
    this.grade = grade;
  }

  @Column(name = "label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Column(name = "sort")
  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  @Column(name = "menu_name")
  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }
}
