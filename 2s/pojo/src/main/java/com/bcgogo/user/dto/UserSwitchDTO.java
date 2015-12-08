package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-10-22
 * Time: 下午8:53
 * To change this template use File | Settings | File Templates.
 */
public class UserSwitchDTO implements Serializable {
  public UserSwitchDTO(){}
  
  private Long id;
  private Long menuId;
  private Long shopId;
  private String scene;
  private String status;
  private String name;

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

  public String getScene() {
    return scene;
  }

  public void setScene(String scene) {
    this.scene = scene;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getMenuId() {
    return menuId;
  }

  public void setMenuId(Long menuId) {
    this.menuId = menuId;
  }
}
