package com.bcgogo.config.model;


import com.bcgogo.config.dto.ShopConfigDTO;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-6
 * Time: 下午2:31
 */
@Entity
@Table(name = "shop_config")
public class ShopConfig extends LongIdentifier implements com.bcgogo.cache.Cacheable {
  private Long shopId;
  private ShopConfigScene scene;
  private String value;
  private String description;
  private Long syncTime;
  private ShopConfigStatus status;
  private Long menuId; //todo delete 没用到?

  public ShopConfigDTO toDTO() {
    ShopConfigDTO shopConfigDTO = new ShopConfigDTO();
    shopConfigDTO.setId(this.getId());
    shopConfigDTO.setScene(this.getScene());
    shopConfigDTO.setValue(getValue());
    shopConfigDTO.setDescription(getDescription());
    shopConfigDTO.setShopId(this.getShopId());
    shopConfigDTO.setStatus(this.getStatus());
    shopConfigDTO.setMenuId(this.getMenuId());
    return shopConfigDTO;
  }

  public void fromDTO(ShopConfigDTO configDTO) {
    this.setId(configDTO.getId());
    this.setShopId(configDTO.getShopId());
    this.setScene(configDTO.getScene());
    this.setValue(configDTO.getValue());
    this.setDescription(configDTO.getDescription());
    this.setStatus(configDTO.getStatus());
    this.setMenuId(configDTO.getMenuId());
  }


  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "scene")
  public ShopConfigScene getScene() {
    return scene;
  }

  public void setScene(ShopConfigScene scene) {
    this.scene = scene;
  }

  @Column(name = "value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public ShopConfigStatus getStatus() {
    return status;
  }

  public void setStatus(ShopConfigStatus status) {
    this.status = status;
  }

  @Column(name = "menu_id")
  public Long getMenuId() {
    return menuId;
  }

  public void setMenuId(Long menuId) {
    this.menuId = menuId;
  }

  @Transient
  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  @Override
  public String assembleKey() {
    return MemcachePrefix.shopConfigSyncTime.getValue() + scene.toString() + "_" + String.valueOf(shopId);
  }




}
