package com.bcgogo.user.model;

import com.bcgogo.enums.user.UserSwitchType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserSwitchDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: WeiLingfeng
 * Date: 12-10-22
 * Time: 下午7:22
 */
@Entity
@Table(name = "user_switch")
public class UserSwitch extends LongIdentifier {

  private Long shopId;
  private Long menuId;
  private String scene;
  private String status;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "scene")
  public String getScene() {
    return scene;
  }

  public void setScene(String scene) {
    this.scene = scene;
  }

  @Column(name = "status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Column(name = "menu_id")
  public Long getMenuId() {
    return menuId;
  }

  public void setMenuId(Long menuId) {
    this.menuId = menuId;
  }

  public UserSwitchDTO toDTO() {
    UserSwitchDTO userSwitchDTO = new UserSwitchDTO();
    userSwitchDTO.setId(this.getId());
    userSwitchDTO.setShopId(this.getShopId());
    userSwitchDTO.setScene(this.getScene());
    userSwitchDTO.setStatus(this.getStatus());
    userSwitchDTO.setMenuId(this.getMenuId());
    userSwitchDTO.setName(UserSwitchType.valueOf(userSwitchDTO.getScene()).getType());
    return userSwitchDTO;
  }

}
