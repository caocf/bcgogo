package com.bcgogo.txn.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
public class CategoryServiceSearchDTO implements Serializable{
  private String serviceName;
  private String categoryName;
  private Double price;
  private Double percentageAmount;
  private ServiceDTO[] serviceDTOs;
  private CategoryDTO[] categoryDTOs;
//  private ServiceDTO[] hiddenServiceDTOs;
  private String url;

  private Long serviceId;
  private Long categoryId;
  private CategoryServiceType categoryServiceType;      //查询类型全部、已分类、未分类

  public enum CategoryServiceType {
    NO_CATEGORY_SERVICE,
    HAS_CATEGORY_SERVICE;
  }


  public CategoryServiceSearchDTO(){
    this.setUrl("category.do?method=getCategoryItemSearch");
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Double getPercentageAmount() {
    return percentageAmount;
  }

  public void setPercentageAmount(Double percentageAmount) {
    this.percentageAmount = percentageAmount;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public ServiceDTO[] getServiceDTOs() {
    return serviceDTOs;
  }

  public void setServiceDTOs(ServiceDTO[] serviceDTOs) {
    this.serviceDTOs = serviceDTOs;
  }

  public CategoryDTO[] getCategoryDTOs() {
    return categoryDTOs;
  }

  public void setCategoryDTOs(CategoryDTO[] categoryDTOs) {
    this.categoryDTOs = categoryDTOs;
  }

//  public ServiceDTO[] getHiddenServiceDTOs() {
//    return hiddenServiceDTOs;
//  }
//
//  public void setHiddenServiceDTOs(ServiceDTO[] hiddenServiceDTOs) {
//    this.hiddenServiceDTOs = hiddenServiceDTOs;
//  }


  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public CategoryServiceType getCategoryServiceType() {
    return categoryServiceType;
  }

  public void setCategoryServiceType(CategoryServiceType categoryServiceType) {
    this.categoryServiceType = categoryServiceType;
  }
}
