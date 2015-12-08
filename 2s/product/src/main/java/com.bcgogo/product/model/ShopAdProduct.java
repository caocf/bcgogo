package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by XinyuQiu on 14-7-22.
 */
@Entity
@Table(name = "shop_ad_product")
public class ShopAdProduct  extends LongIdentifier {
  private Long productLocalInfoId;
  private Long shopId;

  @Column(name = "product_local_info_id")
  public Long getProductLocalInfoId() {
    return productLocalInfoId;
  }

  public void setProductLocalInfoId(Long productLocalInfoId) {
    this.productLocalInfoId = productLocalInfoId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
