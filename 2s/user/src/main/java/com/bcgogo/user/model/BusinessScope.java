package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.BusinessScopeDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 客户或者供应商经营范围
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-6-18
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "business_scope")
public class BusinessScope extends LongIdentifier {
  private Long shopId;
  private Long customerId;
  private Long supplierId;
  private Long productCategoryId;


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "supplier_id")
  public Long getSupplierId() {
    return supplierId;
  }

  public void setSupplierId(Long supplierId) {
    this.supplierId = supplierId;
  }


  @Column(name = "product_category_id")
  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public BusinessScopeDTO toDTO() {
    BusinessScopeDTO businessScopeDTO = new BusinessScopeDTO();
    businessScopeDTO.setId(getId());
    businessScopeDTO.setShopId(getShopId());
    businessScopeDTO.setCustomerId(getCustomerId());
    businessScopeDTO.setSupplierId(getSupplierId());
    businessScopeDTO.setProductCategoryId(getProductCategoryId());
    return businessScopeDTO;
  }

  public BusinessScope fromDTO(BusinessScopeDTO businessScopeDTO) {
    this.setId(businessScopeDTO.getId());
    this.setShopId(businessScopeDTO.getShopId());
    this.setCustomerId(businessScopeDTO.getCustomerId());
    this.setSupplierId(businessScopeDTO.getSupplierId());
    this.setProductCategoryId(businessScopeDTO.getProductCategoryId());
    return this;
  }
}

