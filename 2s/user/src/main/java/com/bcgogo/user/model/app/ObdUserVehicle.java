package com.bcgogo.user.model.app;

import com.bcgogo.api.ObdUserVehicleDTO;
import com.bcgogo.enums.app.ObdUserVehicleStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 * obd、用户、车辆关系表
 */
@Entity
@Table(name = "obd_user_vehicle")
public class ObdUserVehicle extends LongIdentifier {
  private Long obdId;//obd主键
  private String appUserNo;//app用户账号
  private Long appVehicleId;//用户车辆主键
  private Long bindTime;//obd绑定时间（该obd第一次绑定时间作为销售时间）
  private ObdUserVehicleStatus status;//状态

  public ObdUserVehicle() {
    super();
  }

  public ObdUserVehicle(String userNo, Long vehicleId, Long obdId) {
    setStatus(ObdUserVehicleStatus.BUNDLING);
    setAppUserNo(userNo);
    setAppVehicleId(vehicleId);
    setObdId(obdId);
    setBindTime(System.currentTimeMillis());
  }

  public boolean isObdUserVehicleChanged(String appUserNo, Long appVehicleId, Long obdId) throws BcgogoException {
    if (StringUtil.isEmpty(appUserNo)) throw new BcgogoException("app user no is null.");
    if (appVehicleId == null) throw new BcgogoException("vehicle id is null.");
    if (obdId == null) throw new BcgogoException("obd id is null.");
    return appUserNo.equals(getAppUserNo()) && (
        (appVehicleId.equals(getAppVehicleId()) && !obdId.equals(getObdId()))
            || (!appVehicleId.equals(getAppVehicleId()) && obdId.equals(getObdId()))
    );
  }

  public boolean isSameObdUserVehicle(String appUserNo, Long appVehicleId, Long obdId) throws BcgogoException {
    if (StringUtil.isEmpty(appUserNo)) throw new BcgogoException("app user no is null.");
    if (appVehicleId == null) throw new BcgogoException("vehicle id is null.");
    if (obdId == null) throw new BcgogoException("obd id is null.");
    return appUserNo.equals(getAppUserNo()) && ((appVehicleId.equals(getAppVehicleId()))) && ((obdId.equals(getObdId())));
  }

  public ObdUserVehicle(ObdUserVehicleDTO dto) {
    setId(dto.getId());
    setObdId(dto.getObdId());
    setAppUserNo(dto.getAppUserNo());
    setBindTime(dto.getBindTime());
    setAppVehicleId(dto.getAppVehicleId());
    setStatus(dto.getStatus());
  }

  public ObdUserVehicleDTO toDTO() {
    ObdUserVehicleDTO dto = new ObdUserVehicleDTO();
    dto.setId(getId());
    dto.setObdId(getObdId());
    dto.setAppUserNo(getAppUserNo());
    dto.setBindTime(getBindTime());
    dto.setAppVehicleId(getAppVehicleId());
    dto.setStatus(getStatus());
    return dto;
  }

  public void fromDTO(ObdUserVehicleDTO dto) {
    this.setId(dto.getId());
    this.setObdId(dto.getObdId());
    this.setAppUserNo(dto.getAppUserNo());
    this.setBindTime(dto.getBindTime());
    this.setAppVehicleId(dto.getAppVehicleId());
    this.setStatus(dto.getStatus());
  }

  @Column(name = "obd_id")
  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "app_vehicle_id")
  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  @Column(name = "bind_time")
  public Long getBindTime() {
    return bindTime;
  }

  public void setBindTime(Long bindTime) {
    this.bindTime = bindTime;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public ObdUserVehicleStatus getStatus() {
    return status;
  }

  public void setStatus(ObdUserVehicleStatus status) {
    this.status = status;
  }

}
