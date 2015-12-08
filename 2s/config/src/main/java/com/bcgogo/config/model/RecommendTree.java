package com.bcgogo.config.model;

import com.bcgogo.config.dto.RecommendTreeDTO;
import com.bcgogo.enums.shop.RecommendTreeStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-8-19.
 */
@Entity
@Table(name = "recommend_tree")
public class RecommendTree extends LongIdentifier {
  private String name;
  private Long parentId;
  private Integer sort;
  private Long imageId;
  private RecommendTreeStatus status;

  public RecommendTreeDTO toDTO() {
    RecommendTreeDTO recommendTreeDTO = new RecommendTreeDTO();
    recommendTreeDTO.setId(getId());
    recommendTreeDTO.setName(getName());
    recommendTreeDTO.setId(getId());
    recommendTreeDTO.setParentId(getParentId());
    recommendTreeDTO.setSort(getSort());
    recommendTreeDTO.setImageId(getImageId());
    recommendTreeDTO.setStatus(getStatus());
    return recommendTreeDTO;
  }

  public void fromDTO(RecommendTreeDTO recommendTreeDTO) {
    this.setName(recommendTreeDTO.getName());
    this.setId(recommendTreeDTO.getId());
    this.setParentId(recommendTreeDTO.getParentId());
    this.setSort(recommendTreeDTO.getSort());
    this.setImageId(recommendTreeDTO.getImageId());
    this.setStatus(recommendTreeDTO.getStatus());
  }

  @Column(name="name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @Column(name="sort")
  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  @Column(name="image_id")
  public Long getImageId() {
    return imageId;
  }

  public void setImageId(Long imageId) {
    this.imageId = imageId;
  }

  @Column(name="status")
  @Enumerated(EnumType.STRING)
  public RecommendTreeStatus getStatus() {
    return status;
  }

  public void setStatus(RecommendTreeStatus status) {
    this.status = status;
  }



}
