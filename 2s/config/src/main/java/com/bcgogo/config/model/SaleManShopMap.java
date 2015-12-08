package com.bcgogo.config.model;

import com.bcgogo.config.dto.SaleManShopMapDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjuntao
 * Date: 12-12-29
 * Time: 上午9:16
 * 使用 MaintainShopLog 记录跟进人信息
 */
@Deprecated
@Entity
@Table(name = "sale_man_shop_map")
public class SaleManShopMap extends LongIdentifier {
  private Long userId;
  private Long shopId;

  public SaleManShopMap() {
  }

  public SaleManShopMap(SaleManShopMapDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setUserId(dto.getUserId());
  }

  public SaleManShopMapDTO toDTO() {
    SaleManShopMapDTO dto = new SaleManShopMapDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setUserId(this.getUserId());
    return dto;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
