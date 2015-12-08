package com.bcgogo.config.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-9-17
 * Time: 下午2:47
 * To change this template use File | Settings | File Templates.
 */
public class AgentProductDTO {
  private Long id;
  private Long shopId;
  private String name;
  private String description;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
