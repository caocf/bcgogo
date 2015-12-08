package com.bcgogo.api.request;

import com.bcgogo.api.VehicleFaultDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-21
 * Time: 下午7:27
 */
public class MultiFaultRequest {
 private VehicleFaultDTO[] vehicleFaults;
  private String appUserNo;

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public VehicleFaultDTO[] getVehicleFaults() {
    return vehicleFaults;
  }

  public void setVehicleFaults(VehicleFaultDTO[] vehicleFaults) {
    this.vehicleFaults = vehicleFaults;
  }
}
