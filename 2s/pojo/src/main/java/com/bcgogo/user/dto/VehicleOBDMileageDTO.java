package com.bcgogo.user.dto;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-24
 * Time: 下午2:42
 */
public class VehicleOBDMileageDTO  implements Serializable {
  private Long vehicleId;
  private String vehicleNo;
  private Double obdMileage;    //OBD最新里程
  private Long mileageLastUpdateTime;//里程最后更新时间
  private Long shopId;

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

  public Double getObdMileage() {
    return obdMileage;
  }

  public void setObdMileage(Double obdMileage) {
    this.obdMileage = obdMileage;
  }

  public Long getMileageLastUpdateTime() {
    return mileageLastUpdateTime;
  }

  public void setMileageLastUpdateTime(Long mileageLastUpdateTime) {
    this.mileageLastUpdateTime = mileageLastUpdateTime;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }
}
