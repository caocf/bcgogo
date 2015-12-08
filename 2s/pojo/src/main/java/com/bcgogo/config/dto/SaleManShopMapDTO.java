package com.bcgogo.config.dto;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-29
 * Time: 上午9:40
 * 使用 MaintainShopLogDTO 记录跟进人信息
 */
@Deprecated
public class SaleManShopMapDTO {
  private Long id;
  private Long userId;
  private Long shopId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
