package com.bcgogo.api;

/**
 * 向客户端建议
 * Created by Hans on 13-12-10.
 */
public class VehicleInfoSuggestionDTO {
  private Long shopId;
  private String vehicleNo;
  private BrandModelDTO brandModel;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public BrandModelDTO getBrandModel() {
    return brandModel;
  }

  public void setBrandModel(BrandModelDTO brandModel) {
    this.brandModel = brandModel;
  }
}
