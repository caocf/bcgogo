package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-12
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "product_category_relation")
public class ProductCategoryRelation extends LongIdentifier {

  private Long shopId;
  private Long productCategoryId;
  private Long productLocalInfoId;

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

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  public ProductCategoryRelationDTO toDTO() {
    ProductCategoryRelationDTO productCategoryRelationDTO = new ProductCategoryRelationDTO();
    productCategoryRelationDTO.setShopId(this.shopId);
    productCategoryRelationDTO.setProductCategoryId(this.productCategoryId);
    productCategoryRelationDTO.setProductLocalInfoId(this.productLocalInfoId);
    return productCategoryRelationDTO;
  }

}
