package com.bcgogo.api;

import com.bcgogo.enums.app.ObdUserVehicleStatus;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午2:57
 */
public class ObdInfo {
  private int isDefault = 0;//是否默认绑定的DBD
  private Long obdId;       //obd数据库Id
  private String obdSN;       //obdSN码
  private AppVehicleDTO vehicleInfo;

  public ObdInfo() {
    super();
  }

  public ObdInfo(AppVehicleDTO dto) {
    setVehicleInfo(dto);
  }

  public int getIsDefault() {
    return isDefault;
  }

  public void setIsDefault(int isDefault) {
    this.isDefault = isDefault;
  }

  public void setDefaultOBD(ObdUserVehicleStatus status) {
    this.isDefault = (status == ObdUserVehicleStatus.BUNDLING ? 1 : 0);
  }

  public void setNotDefaultOBD() {
    setIsDefault(0);
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public String getObdSN() {
    return obdSN;
  }

  public void setObdSN(String obdSN) {
    this.obdSN = obdSN;
  }

  public void setOBD(ObdDTO dto) {
    if (dto != null) {
      setObdId(dto.getId());
      setObdSN(dto.getSn());
    }
  }

  public AppVehicleDTO getVehicleInfo() {
    return vehicleInfo;
  }

  public void setVehicleInfo(AppVehicleDTO vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }
}
