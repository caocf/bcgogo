package com.bcgogo.user.dto;

/**
 * Created by XinyuQiu on 14-12-11.
 */
public class ShopInternalVehicleGroupDTO {
  private Long shopId;
  private String shopIdStr;
  private String shopName;
  private String vehicleNos;
  private Integer vehicleCount;

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getVehicleNos() {
    return vehicleNos;
  }

  public void setVehicleNos(String vehicleNos) {
    this.vehicleNos = vehicleNos;
  }

  public Integer getVehicleCount() {
    return vehicleCount;
  }

  public void setVehicleCount(Integer vehicleCount) {
    this.vehicleCount = vehicleCount;
  }
}
