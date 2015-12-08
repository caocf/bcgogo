package com.bcgogo.api;

import com.bcgogo.enums.app.ImageVersion;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午2:59
 * app端系统配置信息
 */
public class AppConfig {
  private Long obdReadInterval;  //app从obd读取数据的周期间隔，单位为毫秒
  private Long serverReadInterval;//app从服务端读取数据的周期间隔，单位为毫秒
  private Double mileageInformInterval;//app向服务端发送车辆里程数的公里数间隔，单位为公里
  private String customerServicePhone; //客服电话
  private String remainOilMassWarn; // 车主剩余油量提醒配置,单位为%
  private Double AppVehicleErrorCodeWarnIntervals;     //app故障码提醒周期,单位小时
  private ImageVersion imageVersion;

  public AppConfig() {
  }

  public AppConfig(Long obdReadInterval, Long serverReadInterval, Double mileageInformInterval, String customerServicePhone,
                   String remainOilMassWarn,Double appVehicleErrorCodeWarnIntervals) {
    this.obdReadInterval = obdReadInterval;
    this.serverReadInterval = serverReadInterval;
    this.mileageInformInterval = mileageInformInterval;
    this.customerServicePhone = customerServicePhone;
    this.remainOilMassWarn = remainOilMassWarn;
    this.AppVehicleErrorCodeWarnIntervals = appVehicleErrorCodeWarnIntervals;
  }

  public Long getObdReadInterval() {
    return obdReadInterval;
  }

  public void setObdReadInterval(Long obdReadInterval) {
    this.obdReadInterval = obdReadInterval;
  }

  public Long getServerReadInterval() {
    return serverReadInterval;
  }

  public void setServerReadInterval(Long serverReadInterval) {
    this.serverReadInterval = serverReadInterval;
  }

  public Double getMileageInformInterval() {
    return mileageInformInterval;
  }

  public void setMileageInformInterval(Double mileageInformInterval) {
    this.mileageInformInterval = mileageInformInterval;
  }

  public String getCustomerServicePhone() {
    return customerServicePhone;
  }

  public void setCustomerServicePhone(String customerServicePhone) {
    this.customerServicePhone = customerServicePhone;
  }

  public String getRemainOilMassWarn() {
    return remainOilMassWarn;
  }

  public void setRemainOilMassWarn(String remainOilMassWarn) {
    this.remainOilMassWarn = remainOilMassWarn;
  }

  public Double getAppVehicleErrorCodeWarnIntervals() {
    return AppVehicleErrorCodeWarnIntervals;
  }

  public void setAppVehicleErrorCodeWarnIntervals(Double appVehicleErrorCodeWarnIntervals) {
    AppVehicleErrorCodeWarnIntervals = appVehicleErrorCodeWarnIntervals;
  }

  @Override
  public String toString() {
    return "AppConfig{" +
        "obdReadInterval=" + obdReadInterval +
        ", serverReadInterval=" + serverReadInterval +
        ", mileageInformInterval=" + mileageInformInterval +
        ", customerServicePhone='" + customerServicePhone + '\'' +
        ", remainOilMassWarn='" + remainOilMassWarn + '\'' +
        ", AppVehicleErrorCodeWarnIntervals=" + AppVehicleErrorCodeWarnIntervals +
        '}';
  }

  public ImageVersion getImageVersion() {
    return imageVersion;
  }

  public void setImageVersion(ImageVersion imageVersion) {
    this.imageVersion = imageVersion;
  }
}
