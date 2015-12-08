package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.BrandModelDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-12-12
 * Time: 下午2:45
 */
@Deprecated
public class VehicleResponseV1 extends ApiResponse {
  private List<BrandModelDTO> result = new ArrayList<BrandModelDTO>();
  private AppVehicleDTO vehicleInfo;

  public VehicleResponseV1() {
    super();
  }

  public VehicleResponseV1(ApiResponse response) {
    super(response);
  }

  public List<BrandModelDTO> getResult() {
    return result;
  }

  public void setResult(List<BrandModelDTO> result) {
    this.result = result;
  }

  public AppVehicleDTO getVehicleInfo() {
    return vehicleInfo;
  }

  public void setVehicleInfo(AppVehicleDTO vehicleInfo) {
    this.vehicleInfo = vehicleInfo;
  }
}
