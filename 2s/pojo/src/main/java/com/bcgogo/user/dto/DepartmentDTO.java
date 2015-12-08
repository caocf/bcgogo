package com.bcgogo.user.dto;

import com.bcgogo.enums.user.Status;
import com.bcgogo.utils.NumberUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-7
 * Time: 下午8:13
 */
public class DepartmentDTO {
  private Long id;
  private String idStr;
  private String label; //下拉建议使用
  private String name;
  private Long parentId;
  private Long shopId;
  private Status status;
  private Long sort;   //同级顺序
  private List<DepartmentDTO> children = new ArrayList<DepartmentDTO>();

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if (id != null) this.setIdStr(id.toString());
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    this.setLabel(name);
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public List<DepartmentDTO> getChildren() {
    return children;
  }

  public void setChildren(List<DepartmentDTO> children) {
    this.children = children;
  }

  //根据列表设置当前节点的子节点
  private void filterChildren(List<DepartmentDTO> departmentDTOList) {
    Iterator iterator = departmentDTOList.iterator();
    DepartmentDTO departmentDTO;
    while (iterator.hasNext()) {
      departmentDTO = (DepartmentDTO) iterator.next();
      if (NumberUtil.isEqual(departmentDTO.getParentId(), this.getId())) {
        this.getChildren().add(departmentDTO);
      }
    }
  }

  /**
   * 创建各节点的树状关系（递归方式设置各节点的子节点）
   */
  public void buildTree(DepartmentDTO departmentDTO, List<DepartmentDTO> departmentDTOList) {
    this.filterChildren(departmentDTOList);
    for (DepartmentDTO dto : this.getChildren()) {
      dto.buildTree(dto, departmentDTOList);
    }
  }

  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
