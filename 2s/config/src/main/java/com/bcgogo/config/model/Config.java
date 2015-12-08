package com.bcgogo.config.model;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.config.dto.ConfigDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 10/2/11
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "config")
public class Config extends LongIdentifier implements Cacheable {
  private Long shopId;
  private String name;
  private String value;
  private Long syncTime;
  private String description;


  public Config() {
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "name", length = 100)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "value", length = 200)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  @Column(name = "description", length = 100)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
    return "config_" + name + "_" + String.valueOf(shopId);
  }

  public ConfigDTO toDTO() {
    ConfigDTO configDTO = new ConfigDTO();
    configDTO.setName(this.getName());
    if (this.getValue()!=null&&this.getValue().length() > 30) {
      configDTO.setValueStr(this.getValue().substring(0, 30) + "...");
    } else {
      configDTO.setValueStr(this.getValue());
    }
    configDTO.setValue(this.getValue());
    configDTO.setShopId(this.getShopId());
    if (this.getDescription()!=null&&this.getDescription().length() > 30) {
      configDTO.setDescriptionStr(this.getDescription().substring(0, 30) + "...");
    } else {
      configDTO.setDescriptionStr(this.getDescription());
    }
    configDTO.setDescription(this.getDescription());
    configDTO.setSyncTime(this.getSyncTime());
    return configDTO;
  }
}
