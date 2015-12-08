package com.bcgogo.user.model.permission;

import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.DepartmentDTO;
import com.bcgogo.user.dto.Node;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-21
 * Time: 下午3:36
 * 部门
 */
@Entity
@Table(name = "department")
public class Department extends LongIdentifier {
  private String name;          //部门名
  private Long parentId;       //父ID
  private String description; //描述
  private Status status;       //状态
  private Long shopId;         //shopId
  private Long sort;   //同级顺序

  public Department() {
  }

  public DepartmentDTO toDTO() {
    DepartmentDTO departmentDTO = new DepartmentDTO();
    departmentDTO.setId(this.getId());
    departmentDTO.setName(this.getName());
    departmentDTO.setParentId(this.getParentId());
    departmentDTO.setShopId(this.getShopId());
    departmentDTO.setStatus(this.getStatus());
    departmentDTO.setSort(this.getSort());
    return departmentDTO;
  }

  public void fromDTO(DepartmentDTO departmentDTO) {
    this.setName(departmentDTO.getName());
    this.setParentId(departmentDTO.getParentId());
    this.setShopId(departmentDTO.getShopId());
    this.setStatus(departmentDTO.getStatus());
    this.setSort(departmentDTO.getSort());
  }

  public Department(DepartmentDTO departmentDTO) {
    this.setId(departmentDTO.getId());
    this.setName(departmentDTO.getName());
    this.setParentId(departmentDTO.getParentId());
    this.setShopId(departmentDTO.getShopId());
    this.setStatus(departmentDTO.getStatus());
    this.setSort(departmentDTO.getSort());
  }

  public Node toNode() {
    Node node = new Node();
    node.setId(this.getId());
    node.setText(this.getName());
    node.setParentId(this.getParentId());
    node.setShopId(this.getShopId());
    node.setType(Node.Type.DEPARTMENT);
    node.setLeaf(false);
    node.setSort(this.getSort());
    node.setIconCls("icon-hr");
    return node;
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

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "sort")
  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }
}
