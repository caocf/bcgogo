package com.bcgogo.user.model.app;

import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.enums.app.DriveLogStatus;
import com.bcgogo.enums.app.DriveStatStatus;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-10
 * Time: 下午5:34
 */
@Entity
@Table(name = "drive_log")
public class DriveLog extends LongIdentifier {
//  private String uuid;//行程识别码
//  private String maxr;//最大转速
//  private String maxs;//最大车速
  private String appUserNo;
  private String appDriveLogId;//app里记录的行车日志的Id
  private Long lastUpdateTime;//最后更新时间
  private String vehicleNo;//当前日志的车牌号
  private Long startTime; //开始时间
  private String startLat;//开始维度
  private String startLon; //开始经度
  private String startPlace;//开始地址
  private Long endTime; //结束时间
  private String endLat;//结束维度
  private String endLon; //结束经度
  private String endPlace;//结束地址
  private Long travelTime;//行程时间
  private Double distance;//路程
  private Double oilWear;//平均油耗  L/100KM 取appVehicle
  private Double oilPrice;//油价
  private String oilKind;//油品
  private Double totalOilMoney;//油钱
  private DriveLogStatus status;//日志的状态
  private Double oilCost;//耗油量
  private DriveStatStatus driveStatStatus;//统计状态

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "app_drive_log_id")
  public String getAppDriveLogId() {
    return appDriveLogId;
  }

  public void setAppDriveLogId(String appDriveLogId) {
    this.appDriveLogId = appDriveLogId;
  }

  @Column(name = "last_update_time")
  public Long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  @Column(name = "vehicle_no")
  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  @Column(name = "start_time")
  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @Column(name = "start_lat")
  public String getStartLat() {
    return startLat;
  }

  public void setStartLat(String startLat) {
    this.startLat = startLat;
  }

  @Column(name = "start_lon")
  public String getStartLon() {
    return startLon;
  }

  public void setStartLon(String startLon) {
    this.startLon = startLon;
  }

  @Column(name = "start_place")
  public String getStartPlace() {
    return startPlace;
  }

  public void setStartPlace(String startPlace) {
    this.startPlace = startPlace;
  }

  @Column(name = "end_time")
  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @Column(name = "end_lat")
  public String getEndLat() {
    return endLat;
  }

  public void setEndLat(String endLat) {
    this.endLat = endLat;
  }

  @Column(name = "end_lon")
  public String getEndLon() {
    return endLon;
  }

  public void setEndLon(String endLon) {
    this.endLon = endLon;
  }

  @Column(name = "end_place")
  public String getEndPlace() {
    return endPlace;
  }

  public void setEndPlace(String endPlace) {
    this.endPlace = endPlace;
  }

  @Column(name = "travel_time")
  public Long getTravelTime() {
    return travelTime;
  }

  public void setTravelTime(Long travelTime) {
    this.travelTime = travelTime;
  }

  @Column(name = "distance")
  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  @Column(name = "oil_wear")
  public Double getOilWear() {
    return oilWear;
  }

  public void setOilWear(Double oilWear) {
    this.oilWear = oilWear;
  }

  @Column(name = "oil_price")
  public Double getOilPrice() {
    return oilPrice;
  }

  public void setOilPrice(Double oilPrice) {
    this.oilPrice = oilPrice;
  }

  @Column(name = "oil_kind")
  public String getOilKind() {
    return oilKind;
  }

  public void setOilKind(String oilKind) {
    this.oilKind = oilKind;
  }

  @Column(name = "total_oil_money")
  public Double getTotalOilMoney() {
    return totalOilMoney;
  }

  public void setTotalOilMoney(Double totalOilMoney) {
    this.totalOilMoney = totalOilMoney;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  public DriveLogStatus getStatus() {
    return status;
  }

  public void setStatus(DriveLogStatus status) {
    this.status = status;
  }

  @Column(name = "oil_cost")
  public Double getOilCost() {
    return oilCost;
  }

  public void setOilCost(Double oilCost) {
    this.oilCost = oilCost;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "drive_stat_status")
  public DriveStatStatus getDriveStatStatus() {
    return driveStatStatus;
  }

  public void setDriveStatStatus(DriveStatStatus driveStatStatus) {
    this.driveStatStatus = driveStatStatus;
  }

  public void fromDTO(DriveLogDTO dto){
     if(dto != null){
       this.setId(dto.getId());
       this.setAppUserNo(dto.getAppUserNo());
       this.setLastUpdateTime(dto.getLastUpdateTime());
       this.setVehicleNo(dto.getVehicleNo());
       this.setDistance(dto.getDistance());
       this.setEndLat(dto.getEndLat());
       this.setEndLon(dto.getEndLon());
       this.setEndPlace(dto.getEndPlace());
       this.setEndTime(dto.getEndTime());
       this.setAppDriveLogId(dto.getAppDriveLogId());
       this.setOilKind(dto.getOilKind());
       this.setOilPrice(dto.getOilPrice());
       this.setOilKind(dto.getOilKind());
       this.setOilWear(dto.getOilWear());
       this.setStartLat(dto.getStartLat());
       this.setStartLon(dto.getStartLon());
       this.setStartPlace(dto.getStartPlace());
       this.setStartTime(dto.getStartTime());
       this.setStatus(dto.getStatus());
       this.setTravelTime(dto.getTravelTime());
       this.setTotalOilMoney(dto.getTotalOilMoney());
       this.setOilCost(dto.getOilCost());
       this.setDriveStatStatus(dto.getDriveStatStatus());
     }
  }

  public DriveLogDTO toDTO() {
    DriveLogDTO dto = new DriveLogDTO();
    dto.setId(this.getId());
    dto.setAppUserNo(this.getAppUserNo());
    dto.setLastUpdateTime(this.getLastUpdateTime());
    dto.setVehicleNo(this.getVehicleNo());
    dto.setDistance(this.getDistance());
    dto.setEndLat(this.getEndLat());
    dto.setEndLon(this.getEndLon());
    dto.setEndPlace(this.getEndPlace());
    dto.setEndTime(this.getEndTime());
    dto.setAppDriveLogId(this.getAppDriveLogId());
    dto.setOilKind(this.getOilKind());
    dto.setOilPrice(this.getOilPrice());
    dto.setOilKind(this.getOilKind());
    dto.setOilWear(this.getOilWear());
    dto.setStartLat(this.getStartLat());
    dto.setStartLon(this.getStartLon());
    dto.setStartPlace(this.getStartPlace());
    dto.setStartTime(this.getStartTime());
    dto.setStatus(this.getStatus());
    dto.setTravelTime(this.getTravelTime());
    dto.setTotalOilMoney(this.getTotalOilMoney());
    dto.setOilCost(this.getOilCost());
    dto.setDriveStatStatus(this.getDriveStatStatus());
    return dto;
  }

  //update 的字段 不包含 id，AppDriveLogId
  public void updateFromDTO(DriveLogDTO dto) {
    if (dto != null) {
//      this.setId(dto.getId());
//      this.setAppDriveLogId(dto.getAppDriveLogId());
      this.setAppUserNo(dto.getAppUserNo());
      this.setLastUpdateTime(dto.getLastUpdateTime());
      this.setAppUserNo(dto.getAppUserNo());
      this.setDistance(dto.getDistance());
      this.setEndLat(dto.getEndLat());
      this.setEndLon(dto.getEndLon());
      this.setEndPlace(dto.getEndPlace());
      this.setEndTime(dto.getEndTime());
      this.setOilKind(dto.getOilKind());
      this.setOilPrice(dto.getOilPrice());
      this.setOilKind(dto.getOilKind());
      this.setOilWear(dto.getOilWear());
      this.setStartLat(dto.getStartLat());
      this.setStartLon(dto.getStartLon());
      this.setStartPlace(dto.getStartPlace());
      this.setStartTime(dto.getStartTime());
      this.setStatus(dto.getStatus());
      this.setTravelTime(dto.getTravelTime());
      this.setTotalOilMoney(dto.getTotalOilMoney());
      this.setOilCost(dto.getOilCost());
      this.setDriveStatStatus(dto.getDriveStatStatus());
    }
  }
}
