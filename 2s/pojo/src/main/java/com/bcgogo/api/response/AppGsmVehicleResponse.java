package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.BrandDTO;

import java.util.List;

/**
 * User: lw
 * Date: 14-3-19
 * Time: 上午11:48
 */
public class AppGsmVehicleResponse extends ApiResponse {

  private AppVehicleDTO vehicleInfo;

  public AppGsmVehicleResponse() {
    super();
  }

  public AppGsmVehicleResponse(ApiResponse response) {
    super(response);
  }

  public AppVehicleDTO getVehicleInfo() {
    return vehicleInfo;
  }

  public void setVehicleInfo(AppVehicleDTO vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }

}
