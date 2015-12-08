package com.bcgogo.pojox.api;

import com.bcgogo.pojox.config.BrandDTO;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午4:08
 */
public class VehicleResponse extends ApiResponse {
  private List<BrandDTO> result;
  private AppVehicleDTO vehicleInfo;

  public VehicleResponse() {
    super();
  }

  public VehicleResponse(ApiResponse response) {
    super(response);
  }

  public List<BrandDTO> getResult() {
    return result;
  }

  public void setResult(List<BrandDTO> result) {
    this.result = result;
  }

  public AppVehicleDTO getVehicleInfo() {
    return vehicleInfo;
  }

  public void setVehicleInfo(AppVehicleDTO vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }
}
