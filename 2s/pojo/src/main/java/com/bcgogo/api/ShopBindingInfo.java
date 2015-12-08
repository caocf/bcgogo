package com.bcgogo.api;

/**
 * User: ZhangJuntao
 * Date: 13-12-16
 * Time: 下午5:40
 */
public class ShopBindingInfo {
  private Long shopId;
  private String shopName;
  private Long vehicleId;
  private String vehicleNo;

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

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }
}
