package com.bcgogo.api;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-28
 * Time: 下午1:18
 */
public class AppVehicleFaultInfoOperateDTO implements Serializable{
  private AppVehicleFaultInfoDTO[] appVehicleFaultInfoDTOs;//需要处理的一组故障信息

  public AppVehicleFaultInfoDTO[] getAppVehicleFaultInfoDTOs() {
    return appVehicleFaultInfoDTOs;
  }

  public void setAppVehicleFaultInfoDTOs(AppVehicleFaultInfoDTO[] appVehicleFaultInfoDTOs) {
    this.appVehicleFaultInfoDTOs = appVehicleFaultInfoDTOs;
  }
}
