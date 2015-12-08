package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午3:01
 */
public class ObtainVehicleListResponse extends ApiResponse {
  private List<AppVehicleDTO> vehicleList = new ArrayList<AppVehicleDTO>();
  private String defaultOilPrice;
  private String defaultOilKind;

  public ObtainVehicleListResponse() {
    super();
  }

  public ObtainVehicleListResponse(ApiResponse response) {
    super(response);
  }

  public List<AppVehicleDTO> getVehicleList() {
    return vehicleList;
  }

  public void setVehicleList(List<AppVehicleDTO> vehicleList) {
    this.vehicleList = vehicleList;
  }

  public String getDefaultOilPrice() {
    return defaultOilPrice;
  }

  public void setDefaultOilPrice(String defaultOilPrice) {
    this.defaultOilPrice = defaultOilPrice;
  }

  public String getDefaultOilKind() {
    return defaultOilKind;
  }

  public void setDefaultOilKind(String defaultOilKind) {
    this.defaultOilKind = defaultOilKind;
  }
}
