package com.bcgogo.product.model;

import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.user.dto.Node;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-12-18
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_category")
public class ProductCategory extends LongIdentifier {
  private String name;
  private Long parentId;
  private ProductCategoryStatus status;
  private ProductCategoryType categoryType;
  private Long shopId;

  public ProductCategory(){

  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
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

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ProductCategoryStatus getStatus() {
    return status;
  }

  public void setStatus(ProductCategoryStatus status) {
    this.status = status;
  }

  @Column(name = "category_type")
  @Enumerated(EnumType.STRING)
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

  public ProductCategoryDTO toDTO(){
    ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
    productCategoryDTO.setId(getId());
    productCategoryDTO.setName(getName());
    productCategoryDTO.setParentId(getParentId());
    productCategoryDTO.setStatus(getStatus());
    productCategoryDTO.setCategoryType(getCategoryType());
    productCategoryDTO.setShopId(getShopId());
    return productCategoryDTO;
  }

  public ProductCategory(ProductCategoryDTO productCategoryDTO) {
    this.setId(productCategoryDTO.getId());
    this.setName(productCategoryDTO.getName());
    this.setParentId(productCategoryDTO.getParentId());
    this.setShopId(productCategoryDTO.getShopId());
    this.setStatus(productCategoryDTO.getStatus());
    this.setCategoryType(productCategoryDTO.getCategoryType());
  }

  public ProductCategory fromDTO(ProductCategoryDTO productCategoryDTO,boolean setId) {
    if (setId) {
      this.setId(productCategoryDTO.getId());
    }
    this.setName(productCategoryDTO.getName());
    this.setParentId(productCategoryDTO.getParentId());
    this.setShopId(productCategoryDTO.getShopId());
    this.setStatus(productCategoryDTO.getStatus());
    this.setCategoryType(productCategoryDTO.getCategoryType());
    return this;
  }

  public static Map<Long,ProductCategory> listToMap(List<ProductCategory> productCategoryList)
  {
    Map<Long,ProductCategory> map = new HashMap<Long, ProductCategory>();

    if(CollectionUtils.isEmpty(productCategoryList))
    {
      return map;
    }

    for(ProductCategory productCategory : productCategoryList)
    {
      map.put(productCategory.getId(),productCategory);
    }

    return map;
  }
}
