package com.bcgogo.txn.dto;

import com.bcgogo.enums.CategoryStatus;
import com.bcgogo.enums.CategoryType;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 上午11:18
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDTO implements Serializable{
  private Long id;
  private Long shopId;
  private String categoryName;
  private CategoryType categoryType;
  private String idStr;
  private String label;
  private CategoryStatus status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.idStr = null==id?"":id.toString();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
    this.label = this.categoryName;
  }

  public CategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
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

  public CategoryStatus getStatus() {
    return status;
  }

  public void setStatus(CategoryStatus status) {
    this.status = status;
  }
}
