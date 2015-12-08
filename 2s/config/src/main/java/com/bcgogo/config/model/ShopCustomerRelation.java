package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopCustomerRelationDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-13
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_customer_relation")
public class ShopCustomerRelation extends LongIdentifier {
  private Long shopId;
  private Long customerId;

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

  public ShopCustomerRelationDTO toDTO(){
    ShopCustomerRelationDTO dto = new ShopCustomerRelationDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setCustomerId(this.getCustomerId());
    return dto;
  }
}
