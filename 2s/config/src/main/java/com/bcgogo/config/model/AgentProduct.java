package com.bcgogo.config.model;

import com.bcgogo.config.dto.AgentProductDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-17
 * Time: 下午2:19
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "agent_product")
public class AgentProduct extends LongIdentifier {
  private Long shopId;
  private String name;
  private String description;
  private DeletedType deleted = DeletedType.FALSE;

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

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  public AgentProductDTO toDTO(){
    AgentProductDTO agentProductDTO=new AgentProductDTO();
    agentProductDTO.setId(this.getId());
    agentProductDTO.setName(this.getName());
    agentProductDTO.setDescription(this.getDescription());
    return agentProductDTO;
  }

}
