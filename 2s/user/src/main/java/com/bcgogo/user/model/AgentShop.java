package com.bcgogo.user.model;

import com.bcgogo.enums.user.AgentShopStatus;
import com.bcgogo.enums.user.Status;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by XinyuQiu on 14-6-16.
 */
@Entity
@Table(name = "agent_shop")
public class AgentShop extends LongIdentifier {
  private Long agentUserId;
  private Long shopId;
  private Long agentDate;
  private AgentShopStatus status;

  @Column(name = "agent_user_id")
  public Long getAgentUserId() {
    return agentUserId;
  }

  public void setAgentUserId(Long agentUserId) {
    this.agentUserId = agentUserId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "agent_date")
  public Long getAgentDate() {
    return agentDate;
  }

  public void setAgentDate(Long agentDate) {
    this.agentDate = agentDate;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public AgentShopStatus getStatus() {
    return status;
  }

  public void setStatus(AgentShopStatus status) {
    this.status = status;
  }
}
