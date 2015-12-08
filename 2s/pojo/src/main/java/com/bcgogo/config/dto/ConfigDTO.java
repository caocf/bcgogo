package com.bcgogo.config.dto;

import java.io.Serializable;
/**
 * Created by IntelliJ IDEA.
 * User: dongnan
 * Date: 12-7-10
 * Time: 下午3:51
 * To change this template use File | Settings | File Templates.
 */
public class ConfigDTO implements Serializable {
  private Long shopId;
  private String name;
  private String value;
  private  String valueStr;
  private String description;
  private String descriptionStr;
  private Long syncTime;

  public ConfigDTO() {
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  public String getValueStr() {
    return valueStr;
  }

  public void setValueStr(String valueStr) {
    this.valueStr = valueStr;
  }

    public String getDescriptionStr() {
        return descriptionStr;
    }

    public void setDescriptionStr(String descriptionStr) {
        this.descriptionStr = descriptionStr;
    }
}
