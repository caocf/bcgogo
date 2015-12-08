package com.bcgogo.user.dto.permission;

import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-4-10
 * Time: 下午6:06
 */
public class MenuDTO extends ResourceDTO {
  private Long menuId;
  private Long parentId;
  private String label;
  private String href;
  private Integer grade;
  private Integer sort;
  private String menuName;
  private List<MenuDTO> children = new ArrayList<MenuDTO>();

  public void fromResourceDTO(ResourceDTO resourceDTO) {
    this.setName(resourceDTO.getName());
    this.setStatus(resourceDTO.getStatus());
    this.setSyncTime(resourceDTO.getSyncTime());
    this.setType(resourceDTO.getType());
    this.setValue(resourceDTO.getValue());
    this.setSystemType(resourceDTO.getSystemType());
    this.setMemo(resourceDTO.getMemo());
  }

  public ResourceDTO toResourceDTO() {
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setResourceId(this.getResourceId());
    resourceDTO.setName(this.getName());
    resourceDTO.setStatus(this.getStatus());
    resourceDTO.setSyncTime(this.getSyncTime());
    resourceDTO.setType(this.getType());
    resourceDTO.setValue(this.getValue());
    resourceDTO.setMemo(this.getMemo());
    resourceDTO.setSystemType(this.getSystemType());
    return resourceDTO;
  }

  //根据列表设置当前节点的子节点
  private void filterChildren(List<MenuDTO> menuDTOList) {
    Iterator iterator = menuDTOList.iterator();
    MenuDTO menuDTO;
    while (iterator.hasNext()) {
      menuDTO = (MenuDTO) iterator.next();
      if (NumberUtil.isEqual(menuDTO.getParentId(), this.getMenuId())) {
        this.getChildren().add(menuDTO);
      }
    }
  }

  //创建各节点的树状关系（递归方式设置各节点的子节点）
  public void buildTree(MenuDTO menuDTO, List<MenuDTO> menuDTOList) {
    this.filterChildren(menuDTOList);
    for (MenuDTO dto : this.getChildren()) {
      dto.buildTree(dto, menuDTOList);
    }
  }

  //排序
  public List<MenuDTO> sortChildren() {
    Collections.sort(this.getChildren(), new Comparator<MenuDTO>() {
      @Override
      public int compare(MenuDTO m1, MenuDTO m2) {
        if (m1.getSort() == null) {
          return -1;
        }
        if (m2.getSort() == null) {
          return 1;
        }
        try {
          return m1.getSort().compareTo(m2.getSort());
        } catch (Exception e) {
          return -1;
        }
      }
    });
    return children;
  }

  public boolean hasChildren() {
    return CollectionUtils.isNotEmpty(this.getChildren());
  }

  public void reBuildTreeForSort() {
    if (this.hasChildren()) {
      this.sortChildren();
      for (MenuDTO child : this.getChildren()) {
        child.reBuildTreeForSort();
      }
    }
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public Integer getGrade() {
    return grade;
  }

  public void setGrade(Integer grade) {
    this.grade = grade;
  }

  public List<MenuDTO> getChildren() {
    return children;
  }

  public void setChildren(List<MenuDTO> children) {
    this.children = children;
  }

  public Long getMenuId() {
    return menuId;
  }

  public void setMenuId(Long menuId) {
    this.menuId = menuId;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }
}
