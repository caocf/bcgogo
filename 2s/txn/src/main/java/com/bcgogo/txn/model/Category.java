package com.bcgogo.txn.model;

import com.bcgogo.enums.CategoryStatus;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.CategoryDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午11:08
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "category")
public class Category extends LongIdentifier{
  private Long shopId;
  private String categoryName;
  private CategoryType categoryType;
  private CategoryStatus status;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "category_name",length = 50)
  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "category_type",length = 50)
  public CategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="status")
  public CategoryStatus getStatus() {
    return status;
  }

  public void setStatus(CategoryStatus status) {
    this.status = status;
  }

  public Category(){

  }

  public Category(Long shopId, String categoryName, CategoryType categoryType, CategoryStatus status) {
    this.shopId = shopId;
    this.categoryName = categoryName;
    this.categoryType = categoryType;
    this.status = status;
  }

  public Category(CategoryDTO categoryDTO){
    if(categoryDTO != null){
      this.setCategoryName(categoryDTO.getCategoryName());
      this.setCategoryType(categoryDTO.getCategoryType());
      this.setShopId(categoryDTO.getShopId());
      this.setStatus(categoryDTO.getStatus());
      if(categoryDTO.getId() != null){
        this.setId(categoryDTO.getId());
      }
    }
  }

  public void fromDTO(CategoryDTO categoryDTO){
    if(categoryDTO != null){
      this.setCategoryName(categoryDTO.getCategoryName());
      this.setCategoryType(categoryDTO.getCategoryType());
      this.setShopId(categoryDTO.getShopId());
      this.setStatus(categoryDTO.getStatus());
      if(categoryDTO.getId() != null){
        this.setId(categoryDTO.getId());
      }
    }
  }

  public CategoryDTO toDTO(){
    CategoryDTO categoryDTO = new CategoryDTO();
    categoryDTO.setCategoryName(this.getCategoryName());
    categoryDTO.setCategoryType(this.getCategoryType());
    categoryDTO.setShopId(this.getShopId());
    categoryDTO.setId(this.getId());
    categoryDTO.setStatus(this.getStatus());
    return categoryDTO;
  }
}
