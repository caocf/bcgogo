package com.bcgogo.user.model.app;

import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: ZhangJuntao
 * Date: 13-8-20
 * Time: 下午12:00
 */
@Entity
@Table(name = "vehicle_basic_info")
public class VehicleBasicInfo extends LongIdentifier {
  private String vehicleVin;//手机端车辆vin码

  public VehicleBasicInfo() {
    super();
  }

  public VehicleBasicInfo(AppVehicleDTO dto) {
    setVehicleVin(dto.getVehicleVin());
  }

  @Column(name = "vehicle_vin")
  public String getVehicleVin() {
    return vehicleVin;
  }

  public void setVehicleVin(String vehicleVin) {
    this.vehicleVin = vehicleVin;
  }

}
