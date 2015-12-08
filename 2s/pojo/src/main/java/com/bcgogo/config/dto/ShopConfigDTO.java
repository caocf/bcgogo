package com.bcgogo.config.dto;

import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午2:49
 * To change this template use File | Settings | File Templates.
 */
public class ShopConfigDTO {
  private Long id;
  private Long shopId;
  private ShopConfigScene scene;
  private String value;
  private String description;
  private ShopConfigStatus status;
  private Long menuId;

  private String shopName;
  private String shopIdStr;
  private String sceneDescription;
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
    this.shopIdStr = shopId.toString();
  }

  public ShopConfigScene getScene() {
    return scene;
  }

  public void setScene(ShopConfigScene scene) {
    this.scene = scene;
    this.sceneDescription = scene.getScene();
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ShopConfigStatus getStatus() {
    return status;
  }

  public void setStatus(ShopConfigStatus status) {
    this.status = status;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public String getSceneDescription() {
    return sceneDescription;
  }

  public void setSceneDescription(String sceneDescription) {
    this.sceneDescription = sceneDescription;
  }

  public Long getMenuId() {
    return menuId;
  }

  public void setMenuId(Long menuId) {
    this.menuId = menuId;
  }
}
