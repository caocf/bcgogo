package com.bcgogo.config.model;

import com.bcgogo.config.dto.ServiceCategoryDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.config.ServiceCategoryType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-9
 * Time: 下午8:20
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "service_category")
public class ServiceCategory extends LongIdentifier{
  private Long shopId;
  private String name;
  private Long parentId;
  private ServiceCategoryType categoryType;
  private DeletedType deleted = DeletedType.FALSE;
  private ServiceScope serviceScope;  //一级分类服务范围枚举值，与app端一致

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

  @Column(name = "category_type")
  @Enumerated(EnumType.STRING)
  public ServiceCategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(ServiceCategoryType categoryType) {
    this.categoryType = categoryType;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted(){
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name = "service_scope")
  @Enumerated(EnumType.STRING)
  public ServiceScope getServiceScope() {
    return serviceScope;
  }

  public void setServiceScope(ServiceScope serviceScope) {
    this.serviceScope = serviceScope;
  }

  public ServiceCategoryDTO toDTO(){
    ServiceCategoryDTO categoryDTO=new ServiceCategoryDTO();
    categoryDTO.setId(this.getId());
    categoryDTO.setParentId(this.getParentId());
    categoryDTO.setName(this.getName());
    categoryDTO.setCategoryType(this.getCategoryType());
    categoryDTO.setServiceScope(this.getServiceScope());
    return categoryDTO;
  }



}
