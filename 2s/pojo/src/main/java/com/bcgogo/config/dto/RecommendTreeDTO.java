package com.bcgogo.config.dto;

import com.bcgogo.enums.shop.RecommendTreeStatus;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;
import com.bcgogo.utils.NumberUtil;

import java.util.List;

/**
 * Created by XinyuQiu on 14-8-19.
 * 推荐商户树形菜单
 */

public class RecommendTreeDTO {
  private Long id;
  private Long idStr;
  private String name;
  private Long parentId;
  private Integer sort;
  private Long imageId;
  private RecommendTreeStatus status;
  private List<RecommendTreeDTO> childRenDTOs;
  private List<ShopDTO> shopDTOs;
  private String imageUrl;

  public CheckNode toNode() {
    CheckNode node = new CheckNode();
    node.setSort(NumberUtil.longValue(getSort()));
    node.setId(this.getId());
    node.setText(this.getName());
    node.setValue(this.getImageUrl());
    node.setParentId(getParentId());
    node.setType(Node.Type.RECOMMEND_SHOP);
    node.setLeaf(false);
    node.setExpanded(true);
    return node;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getIdStr() {
    return idStr;
  }

  public void setIdStr(Long idStr) {
    this.idStr = idStr;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public Long getImageId() {
    return imageId;
  }

  public void setImageId(Long imageId) {
    this.imageId = imageId;
  }

  public RecommendTreeStatus getStatus() {
    return status;
  }

  public void setStatus(RecommendTreeStatus status) {
    this.status = status;
  }

  public List<RecommendTreeDTO> getChildRenDTOs() {
    return childRenDTOs;
  }

  public void setChildRenDTOs(List<RecommendTreeDTO> childRenDTOs) {
    this.childRenDTOs = childRenDTOs;
  }

  public List<ShopDTO> getShopDTOs() {
    return shopDTOs;
  }

  public void setShopDTOs(List<ShopDTO> shopDTOs) {
    this.shopDTOs = shopDTOs;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
