package com.bcgogo.common;

import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created with IntelliJ IDEA.
 * User: terry
 * Date: 13-8-8
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
public class Node {

  private Long id;
  private Long pId;
  private String name;
  private boolean isParent = false; // 为true标示没有子节点 leaf
  private boolean isOpen;

  private String categoryIdStr;

  public Long getId() {
    return id;
  }

  public Node(){

  }

  public Node(Long id, Long pId, String name, boolean parent, boolean open) {
    this.id = id;
    this.pId = pId;
    this.name = name;
    isParent = parent;
    isOpen = open;
  }

  public Node(Long id, Long pId, String name, boolean open, boolean parent, String categoryIdStr) {
    this.id = id;
    this.pId = pId;
    this.name = name;
    isParent = parent;
    isOpen = open;
    this.categoryIdStr = categoryIdStr;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getpId() {
    return pId;
  }

  public void setpId(Long pId) {
    this.pId = pId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isParent() {
    return isParent;
  }

  public void setParent(boolean parent) {
    isParent = parent;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public void setOpen(boolean open) {
    isOpen = open;
  }

  public String getCategoryIdStr() {
    return categoryIdStr;
  }

  public void setCategoryIdStr(String categoryIdStr) {
    this.categoryIdStr = categoryIdStr;
  }

  public void fromProductCategoryDTO(ProductCategoryDTO productCategoryDTO){
    setOpen(true);
    setId(productCategoryDTO.getId());
    setCategoryIdStr(productCategoryDTO.getId().toString());
    setName(productCategoryDTO.getName());
    if (productCategoryDTO.getCategoryType() == ProductCategoryType.SECOND_CATEGORY) {
      setpId(0L);
      setParent(true);
    }else if(productCategoryDTO.getCategoryType() == ProductCategoryType.THIRD_CATEGORY){
      setpId(productCategoryDTO.getParentId());
    }
  }

  /*  public String toSimpleJsonString() {
    StringBuilder stringBuilder = new StringBuilder("{");
    stringBuilder.append("id:" + id + ",");
    stringBuilder.append("pId:" + pId + ",");
    stringBuilder.append("name:" + name + ",");
    stringBuilder.append("isParent:" + isParent);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }*/

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}

/*
  public static final void main(String[] args) {
    Node node = new Node();
    node.setId(1L);
    node.setName("111");
    node.setpId(0L);
    node.setParent(true);
    System.out.println(node.toSimpleJsonString());
  }
*/
