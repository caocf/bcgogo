package com.bcgogo.config.dto;

/**
 * Created by XinyuQiu on 14-7-28.
 */
public class ShopAdAreaDTO {
  private Long shopId;
  private Long province;     //省
  private Long city;          //市
  private Long region;        //区域
  private Long areaId;        //省市区最终Id

  private String areaName;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }
}
