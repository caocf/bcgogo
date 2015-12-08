package com.bcgogo.pojox.api;

import com.bcgogo.pojox.enums.etl.GsmVehicleStatus;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-14
 * Time: 16:45
 */
public class GsmVehicleDataCondition {
  private String uuid; //行程识别码
  private GsmVehicleStatus gsmVehicleStatus;
  private String appUserNo;
  private String imei;
  private String vehicleStatus;
  private String orderBy;
  private int limit;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public GsmVehicleStatus getGsmVehicleStatus() {
    return gsmVehicleStatus;
  }

  public void setGsmVehicleStatus(GsmVehicleStatus gsmVehicleStatus) {
    this.gsmVehicleStatus = gsmVehicleStatus;
  }

  public String getVehicleStatus() {
    return vehicleStatus;
  }

  public void setVehicleStatus(String vehicleStatus) {
    this.vehicleStatus = vehicleStatus;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
