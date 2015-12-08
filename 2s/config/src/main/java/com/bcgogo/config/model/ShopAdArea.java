package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopAdAreaDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by XinyuQiu on 14-7-22.
 */
@Entity
@Table(name = "shop_ad_area")
public class ShopAdArea  extends LongIdentifier {
  private Long shopId;
  private Long province;     //省
  private Long city;          //市
  private Long region;        //区域
  private Long areaId;        //省市区最终Id

  public ShopAdAreaDTO toDTO() {
    ShopAdAreaDTO dto = new ShopAdAreaDTO();
    dto.setShopId(getShopId());
    dto.setProvince(getProvince());
    dto.setCity(getCity());
    dto.setRegion(getRegion());
    dto.setAreaId(getAreaId());
    return dto;
  }

  public void fromDTO(ShopAdAreaDTO dto) {
    if(dto != null){
      this.setShopId(dto.getShopId());
      this.setProvince(dto.getProvince());
      this.setCity(dto.getCity());
      this.setRegion(dto.getRegion());
      this.setAreaId(dto.getAreaId());
    }

  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "province")
  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  @Column(name = "city")
  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  @Column(name = "region")
  public Long getRegion() {
    return region;
  }

  public void setRegion(Long region) {
    this.region = region;
  }

  @Column(name = "area_id")
  public Long getAreaId() {
    return areaId;
  }

  public void setAreaId(Long areaId) {
    this.areaId = areaId;
  }

}
