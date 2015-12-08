package com.bcgogo.config.dto;

import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.config.ServiceCategoryType;
import com.bcgogo.user.dto.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-10
 * Time: 上午10:05
 */
public class ServiceCategoryDTO {
  //TODO 为了照顾手机端
  public final static String WASH = "洗车";
  private Long id;
  private String idStr;
  private Long shopId;
  private String name;
  private Long parentId;
  private ServiceCategoryType categoryType;
  private ServiceScope serviceScope;
  private List<ServiceCategoryDTO> children = new ArrayList<ServiceCategoryDTO>();

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id!=null) idStr = id.toString();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  public ServiceCategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(ServiceCategoryType categoryType) {
    this.categoryType = categoryType;
  }

  public ServiceScope getServiceScope() {
    return serviceScope;
  }

  public void setServiceScope(ServiceScope serviceScope) {
    this.serviceScope = serviceScope;
  }

  public Node toNode() {
    Node node = new Node();
    node.setId(this.getId());
    node.setText(this.getName());
    node.setParentId(this.getParentId());
    node.setShopId(this.getShopId());
    ServiceCategoryType categoryType = getCategoryType();
    if (categoryType == null) {
      return node;
    }
    switch (categoryType) {
      case FIRST_CATEGORY:
        node.setType(Node.Type.FIRST_CATEGORY);
        node.setLeaf(false);
        break;
      case SECOND_CATEGORY:
        node.setType(Node.Type.SECOND_CATEGORY);
        node.setLeaf(true);
        break;
    }
    return node;
  }

  public List<ServiceCategoryDTO> getChildren() {
    return children;
  }

  public void setChildren(List<ServiceCategoryDTO> children) {
    this.children = children;
  }
}
