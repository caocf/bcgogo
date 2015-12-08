package com.bcgogo.config.model;

import com.bcgogo.config.dto.MaintainShopLogDTO;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-7-17
 * Time: 下午6:04
 */
@Entity
@Table(name = "maintain_shop_log")
public class MaintainShopLog extends LongIdentifier {
  private Long userId;
  private Long shopId;
  private ShopStatus shopStatus;

  public MaintainShopLog() {
  }

  public MaintainShopLog(Long shopId, Long userId, ShopStatus shopStatus) {
    this.userId = userId;
    this.shopId = shopId;
    this.shopStatus = shopStatus;
  }

  public MaintainShopLog(MaintainShopLogDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setUserId(dto.getUserId());
    this.setShopStatus(dto.getShopStatus());
  }

  public MaintainShopLogDTO toDTO() {
    MaintainShopLogDTO dto = new MaintainShopLogDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setUserId(this.getUserId());
    dto.setShopStatus(this.getShopStatus());
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

  @Enumerated(EnumType.STRING)
  @Column(name = "shop_status")
  public ShopStatus getShopStatus() {
    return shopStatus;
  }

  public void setShopStatus(ShopStatus shopStatus) {
    this.shopStatus = shopStatus;
  }
}
