package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationCityQueryResponse;

/**
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 14-04-09
 * Time: 下午3:09
 */
public class ApiVehicleViolateRegulationResponse extends ApiResponse {
 private VehicleViolateRegulationCityQueryResponse queryResponse;

  public ApiVehicleViolateRegulationResponse() {
    super();
  }

  public ApiVehicleViolateRegulationResponse(ApiResponse response) {
    super(response);
  }

  public VehicleViolateRegulationCityQueryResponse getQueryResponse() {
    return queryResponse;
  }

  public void setQueryResponse(VehicleViolateRegulationCityQueryResponse queryResponse) {
    this.queryResponse = queryResponse;
  }
}
