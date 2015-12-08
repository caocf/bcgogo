package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopBusinessScopeDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 店铺经营范围 只存三级的经营范围
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-6-21
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_business_scope")
public class ShopBusinessScope extends LongIdentifier {
  private Long shopId;
  private Long productCategoryId;

  public ShopBusinessScope() {
  }


  public ShopBusinessScope(Long shopId, Long productCategoryId) {
    this.shopId = shopId;
    this.productCategoryId = productCategoryId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "product_category_id")
  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public ShopBusinessScope fromDTO(ShopBusinessScopeDTO shopBusinessScopeDTO) {
    this.setId(shopBusinessScopeDTO.getId());
    this.setProductCategoryId(shopBusinessScopeDTO.getProductCategoryId());
    this.setShopId(shopBusinessScopeDTO.getShopId());
    return this;
  }

  public ShopBusinessScopeDTO toDTO() {
    ShopBusinessScopeDTO shopBusinessScopeDTO = new ShopBusinessScopeDTO();
    shopBusinessScopeDTO.setId(getId());
    shopBusinessScopeDTO.setShopId(getShopId());
    shopBusinessScopeDTO.setProductCategoryId(getProductCategoryId());
    return shopBusinessScopeDTO;
  }
}
