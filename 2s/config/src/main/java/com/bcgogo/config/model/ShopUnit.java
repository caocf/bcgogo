package com.bcgogo.config.model;

import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: QiuXinyu
 * Date: 12-5-10
 * Time: 上午11:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "shop_unit")
public class ShopUnit extends LongIdentifier {
  private Long shopId;
  private Long lastEditTime;
  private Long useRate;
  private String unitName;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "last_edit_time")
  public Long getLastEditTime() {
    return lastEditTime;
  }

  public void setLastEditTime(Long lastEditTime) {
    this.lastEditTime = lastEditTime;
  }

  @Column(name = "use_rate")
  public Long getUseRate() {
    return useRate;
  }

  public void setUseRate(Long useRate) {
    this.useRate = useRate;
  }

  @Column(name = "unit_name", length = 20)
  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public ShopUnitDTO toDTO() {             //仅需要保存name就好了，id,shopID绝对不能set过去
    ShopUnitDTO shopUnitDTO = new ShopUnitDTO();
//    shopUnitDTO.setId(this.getId());
//    shopUnitDTO.setShopId(this.getShopId());
//    shopUnitDTO.setLastEditTime(this.getLastEditTime());
//    shopUnitDTO.setUseRate(this.getUseRate());
    shopUnitDTO.setUnitName(this.getUnitName());
      return shopUnitDTO;
  }
}
