package com.bcgogo.api;

import com.bcgogo.enums.app.ObdUserVehicleStatus;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-7-21
 * Time: 下午6:02
 * To change this template use File | Settings | File Templates.
 */
public class AppUserShopVehicleDTO {
  private Long id;
  private String appUserNo; //用户账号
  private Long shopId;
  private Long appVehicleId;
  private Long obdId;
  private ObdUserVehicleStatus status; //状态

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  public Long getObdId() {
    return obdId;
  }

  public void setObdId(Long obdId) {
    this.obdId = obdId;
  }

  public ObdUserVehicleStatus getStatus() {
    return status;
  }

  public void setStatus(ObdUserVehicleStatus status) {
    this.status = status;
  }
}
