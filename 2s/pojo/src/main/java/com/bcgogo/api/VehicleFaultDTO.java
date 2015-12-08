package com.bcgogo.api;

import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午3:24
 */
public class VehicleFaultDTO {
  private String faultCode;//故障码     * 可以是多个 中间以逗号分隔
  private String userNo;//用户账号
  private Long vehicleId;
  private String vehicleVin;//车辆唯一标识号    *
  private String obdSN;//obd唯一标识号       *
  private Long reportTime;//故障时间（Long型unixTime）   *
  private String imei;//OBD imei号


  public String validate() {
    if (StringUtil.isEmpty(faultCode)) {
      return "故障码不能为空";
    }
    if (StringUtil.isEmpty(obdSN)) {
      return "OBD不存在";
    }
    if (vehicleId == null) {
      return ValidateMsg.VEHICLE_EMPTY.getValue();
    }
    return "";
  }

  public boolean isSuccess(String result) {
    return StringUtil.isEmpty(result);
  }

  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

  public String getObdSN() {
    return obdSN;
  }

  public void setObdSN(String obdSN) {
    this.obdSN = obdSN;
  }

  public Long getReportTime() {
    return reportTime;
  }

  public void setReportTime(Long reportTime) {
    this.reportTime = reportTime;
  }

  @Override
  public String toString() {
    return "VehicleFaultDTO{" +
        "faultCode='" + faultCode + '\'' +
        ", userNo='" + userNo + '\'' +
        ", vehicleVin='" + vehicleVin + '\'' +
        ", obdSN='" + obdSN + '\'' +
        ", reportTime=" + reportTime +
        '}';
  }

  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }
}
