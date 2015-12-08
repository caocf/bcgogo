package com.bcgogo.user.dto;

/**
 * Created by XinyuQiu on 14-12-15.
 */
public class ShopInternalVehicleDriveStatDTO {
  private String vehicleNo;
  private Long vehicleId;
  private String vehicleInfo;
  private Double distance;
  private Double oilWear;
  private Double avgOilWear;
  private Double travelTime;
  private String travelTimeStr;
  private Integer driveCount;

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getVehicleInfo() {
    return vehicleInfo;
  }

  public void setVehicleInfo(String vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  public Double getAvgOilWear() {
    return avgOilWear;
  }

  public void setAvgOilWear(Double avgOilWear) {
    this.avgOilWear = avgOilWear;
  }

  public Double getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(Double travelTime) {
    this.travelTime = travelTime;
  }

  public String getTravelTimeStr() {
    return travelTimeStr;
  }

  public void setTravelTimeStr(String travelTimeStr) {
    this.travelTimeStr = travelTimeStr;
  }

  public Integer getDriveCount() {
    return driveCount;
  }

  public void setDriveCount(Integer driveCount) {
    this.driveCount = driveCount;
  }
}
