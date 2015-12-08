package com.bcgogo.api;

import com.bcgogo.enums.app.ObdUserVehicleStatus;

/**
 * User: ZhangJuntao
 * Date: 13-8-22
 * Time: 下午4:25
 */
public class ObdUserVehicleDTO {
  private Long id;//obd主键
  private Long obdId;//obd主键
  private String appUserNo;//app用户账号
  private Long appVehicleId;//用户车辆主键
  private Long bindTime;//obd绑定时间（该obd第一次绑定时间作为销售时间）
  private ObdUserVehicleStatus status;//状态

  public ObdUserVehicleDTO() {
    super();
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  public Long getBindTime() {
    return bindTime;
  }

  public void setBindTime(Long bindTime) {
    this.bindTime = bindTime;
  }

  public ObdUserVehicleStatus getStatus() {
    return status;
  }

  public void setStatus(ObdUserVehicleStatus status) {
    this.status = status;
  }
}
