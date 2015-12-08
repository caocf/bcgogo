package com.bcgogo.api.request;

import com.bcgogo.api.AppVehicleDTO;

import java.util.List;

/**
 * Created by Hans on 14-1-10.
 */
public class VehicleRequest {
  private List<AppVehicleDTO> vehicles;

  public List<AppVehicleDTO> getVehicles() {
    return vehicles;
  }

  public void setVehicles(List<AppVehicleDTO> vehicles) {
    this.vehicles = vehicles;
  }
}
