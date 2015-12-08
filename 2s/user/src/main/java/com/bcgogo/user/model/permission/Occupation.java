package com.bcgogo.user.model.permission;

import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.OccupationDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-11-21
 * Time: 下午3:42
 * 职位
 */
@Entity
@Table(name = "occupation")
public class Occupation extends LongIdentifier {
  private String name;          //职位名称
  private String description; //描述
  private Status status;       //状态
  private Long shopId;         //shopId
  private Long departmentId;       //部门Id
  private Long sort;   //同级顺序


  public Occupation() {
  }

  public Occupation(OccupationDTO occupationDTO) {
    this.setId(occupationDTO.getId());
    this.setName(occupationDTO.getName());
    this.setDepartmentId(occupationDTO.getDepartmentId());
    this.setDescription(occupationDTO.getDescription());
    this.setShopId(occupationDTO.getShopId());
    this.setStatus(occupationDTO.getStatus());
    this.setSort(occupationDTO.getSort());
  }

  public OccupationDTO toDTO() {
    OccupationDTO occupationDTO = new OccupationDTO();
    occupationDTO.setId(this.getId());
    occupationDTO.setName(this.getName());
    occupationDTO.setDepartmentId(this.getDepartmentId());
    occupationDTO.setDescription(this.getDescription());
    occupationDTO.setShopId(this.getShopId());
    occupationDTO.setStatus(this.getStatus());
    occupationDTO.setSort(this.getSort());
    return occupationDTO;
  }

  public Node toNode() {
    Node node = new Node();
    node.setId(this.getId());
    node.setText(this.getName());
    node.setParentId(this.getDepartmentId());
    node.setShopId(this.getShopId());
    node.setType(Node.Type.OCCUPATION);
    node.setLeaf(true);
    node.setSort(this.getSort());
    node.setIconCls("icon-user");
    return node;
  }

  public void fromDTO(OccupationDTO occupationDTO) {
    this.setId(occupationDTO.getId());
    this.setName(occupationDTO.getName());
    this.setDepartmentId(occupationDTO.getDepartmentId());
    this.setDescription(occupationDTO.getDescription());
    this.setShopId(occupationDTO.getShopId());
    this.setStatus(occupationDTO.getStatus());
    this.setSort(occupationDTO.getSort());
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "department_id")
  public Long getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(Long departmentId) {
    this.departmentId = departmentId;
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

  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }
}
