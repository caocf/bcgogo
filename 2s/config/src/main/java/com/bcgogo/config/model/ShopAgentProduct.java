package com.bcgogo.config.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-17
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_agent_product")
public class ShopAgentProduct extends LongIdentifier {
  private Long shopId;
  private Long agentProductId;
  private DeletedType deleted = DeletedType.FALSE;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "agent_product_id")
  public Long getAgentProductId() {
    return agentProductId;
  }

  public void setAgentProductId(Long agentProductId) {
    this.agentProductId = agentProductId;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
