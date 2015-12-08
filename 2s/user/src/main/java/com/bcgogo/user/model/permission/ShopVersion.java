package com.bcgogo.user.model.permission;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.SystemType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.permission.ResourceDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-11
 * Time: 下午8:49
 * shop版本
 */
@Entity
@Table(name = "shop_version")
public class ShopVersion extends LongIdentifier {
  private String name;
  private String value;
  private String description;
  private Integer softPrice;


  public ShopVersionDTO toDTO() {
    ShopVersionDTO shopVersionDTO = new ShopVersionDTO();
    shopVersionDTO.setId(this.getId());
    shopVersionDTO.setName(this.getName());
    shopVersionDTO.setValue(this.getValue());
    shopVersionDTO.setDescription(this.getDescription());
    shopVersionDTO.setSoftPrice(this.getSoftPrice());
    return shopVersionDTO;
  }

  public void fromDTO(ShopVersionDTO shopVersionDTO) {
    this.setId(shopVersionDTO.getId());
    this.setName(shopVersionDTO.getName());
    this.setValue(shopVersionDTO.getValue());
    this.setDescription(shopVersionDTO.getDescription());
    this.setSoftPrice(shopVersionDTO.getSoftPrice());
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  @Column(name = "soft_price")
  public Integer getSoftPrice() {
    return softPrice;
  }

  public void setSoftPrice(Integer softPrice) {
    this.softPrice = softPrice;
  }
}
