package com.bcgogo.product.ProductCategory;

import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.user.dto.CheckNode;
import com.bcgogo.user.dto.Node;

import java.io.Serializable;

/**
 * 产品分类专用
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-18
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public class ProductCategoryDTO implements Serializable {
  private Long id;
  private String idStr;
  private String name;
  private Long parentId;
  private String parentIdStr;
  private ProductCategoryStatus status;
  private ProductCategoryType categoryType;
  private Long shopId;
  private Boolean checked=false;

  private String firstCategoryName;
  private String secondCategoryName;
  private String thirdCategoryName;
  private Long firstCategoryId;
  private Long secondCategoryId;
  private Long thirdCategoryId;

  private String firstCategoryIdStr;
  private String secondCategoryIdStr;
  private String thirdCategoryIdStr;

  private Long creationDate;

  public String getFirstCategoryIdStr() {
    return firstCategoryIdStr;
  }

  public void setFirstCategoryIdStr(String firstCategoryIdStr) {
    this.firstCategoryIdStr = firstCategoryIdStr;
  }

  public String getSecondCategoryIdStr() {
    return secondCategoryIdStr;
  }

  public void setSecondCategoryIdStr(String secondCategoryIdStr) {
    this.secondCategoryIdStr = secondCategoryIdStr;
  }

  public String getThirdCategoryIdStr() {
    return thirdCategoryIdStr;
  }

  public void setThirdCategoryIdStr(String thirdCategoryIdStr) {
    this.thirdCategoryIdStr = thirdCategoryIdStr;
  }

  public String getParentIdStr() {
    return parentIdStr;
  }

  public void setParentIdStr(String parentIdStr) {
    this.parentIdStr = parentIdStr;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public String getFirstCategoryName() {
    return firstCategoryName;
  }

  public void setFirstCategoryName(String firstCategoryName) {
    this.firstCategoryName = firstCategoryName;
  }

  public String getSecondCategoryName() {
    return secondCategoryName;
  }

  public void setSecondCategoryName(String secondCategoryName) {
    this.secondCategoryName = secondCategoryName;
  }

  public Long getFirstCategoryId() {
    return firstCategoryId;
  }

  public void setFirstCategoryId(Long firstCategoryId) {
    if(firstCategoryId!=null) this.firstCategoryIdStr=firstCategoryId.toString();
    this.firstCategoryId = firstCategoryId;
  }

  public String getThirdCategoryName() {
    return thirdCategoryName;
  }

  public void setThirdCategoryName(String thirdCategoryName) {
    this.thirdCategoryName = thirdCategoryName;
  }

  public Long getSecondCategoryId() {
    return secondCategoryId;
  }

  public void setSecondCategoryId(Long secondCategoryId) {
    if(secondCategoryId!=null) this.secondCategoryIdStr=secondCategoryId.toString();
    this.secondCategoryId = secondCategoryId;
  }

  public Long getThirdCategoryId() {
    return thirdCategoryId;
  }

  public void setThirdCategoryId(Long thirdCategoryId) {
    if(thirdCategoryId!=null) this.thirdCategoryIdStr=thirdCategoryId.toString();
    this.thirdCategoryId = thirdCategoryId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null) this.idStr=id.toString();
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    if(parentId!=null) this.parentIdStr=parentId.toString();
    this.parentId = parentId;
  }

  public ProductCategoryStatus getStatus() {
    return status;
  }

  public void setStatus(ProductCategoryStatus status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ProductCategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(ProductCategoryType categoryType) {
    this.categoryType = categoryType;
  }

  public Node toNode() {
    Node node = new Node();
    node.setId(this.getId());
    node.setText(this.getName());
    node.setParentId(this.getParentId());
    node.setShopId(this.getShopId());
    if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.TOP_CATEGORY))) {
      node.setType(Node.Type.TOP_CATEGORY);
      node.setLeaf(false);
    } else if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.FIRST_CATEGORY))) {
      node.setType(Node.Type.FIRST_CATEGORY);
      node.setLeaf(false);
    } else if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.SECOND_CATEGORY))) {
      node.setType(Node.Type.SECOND_CATEGORY);
      node.setLeaf(false);
    } else if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.THIRD_CATEGORY))) {
      node.setType(Node.Type.THIRD_CATEGORY);
      node.setLeaf(true);
    }
    node.setSort(this.getCreationDate());
    node.setIconCls("icon-hr");
    return node;
  }

  public Boolean getChecked() {
    return checked;
  }

  public void setChecked(Boolean checked) {
    this.checked = checked;
  }

  public CheckNode toCheckNode() {
    CheckNode node = new CheckNode();
    node.setId(this.getId());
    node.setText(this.getName());
    node.setValue(this.getName());
    node.setParentId(this.getParentId());
    node.setShopId(this.getShopId());
    if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.TOP_CATEGORY))) {
      node.setType(Node.Type.TOP_CATEGORY);
      node.setLeaf(false);
    } else if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.FIRST_CATEGORY))) {
      node.setType(Node.Type.FIRST_CATEGORY);
      node.setLeaf(false);
    } else if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.SECOND_CATEGORY))) {
      node.setType(Node.Type.SECOND_CATEGORY);
      node.setLeaf(false);
    } else if (this.getCategoryType().toString().equals(String.valueOf(Node.Type.THIRD_CATEGORY))) {
      node.setType(Node.Type.THIRD_CATEGORY);
      node.setLeaf(true);
    }
    node.setChecked(this.getChecked());
    node.setSort(this.getCreationDate());
//    node.setIconCls("icon-hr");
    return node;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProductCategoryDTO that = (ProductCategoryDTO) o;

    if (id != null ? !id.equals(that.id) : that.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
    result = 31 * result + (status != null ? status.hashCode() : 0);
    result = 31 * result + (categoryType != null ? categoryType.hashCode() : 0);
    result = 31 * result + (shopId != null ? shopId.hashCode() : 0);
    return result;
  }
}
