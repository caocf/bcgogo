package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 标准车辆车型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "standard_vehicle_model")
public class StandardVehicleModel extends LongIdentifier {
  private String name;//车型名字
  private Long standardVehicleBrandId;//标准车辆品牌id
  private Integer frequency = 1;

  @Column(name = "frequency")
  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "standard_vehicle_brand_id")
  public Long getStandardVehicleBrandId() {
    return standardVehicleBrandId;
  }

  public void setStandardVehicleBrandId(Long standardVehicleBrandId) {
    this.standardVehicleBrandId = standardVehicleBrandId;
  }

  public StandardVehicleModelDTO toDTO() {
    StandardVehicleModelDTO standardVehicleModelDTO = new StandardVehicleModelDTO();
    standardVehicleModelDTO.setName(getName());
    standardVehicleModelDTO.setStandardVehicleBrandId(getStandardVehicleBrandId());
    standardVehicleModelDTO.setId(getId());
    standardVehicleModelDTO.setFrequency(getFrequency());
    return standardVehicleModelDTO;
  }

  public StandardVehicleModel fromDTO(StandardVehicleModelDTO standardVehicleModelDTO) {
    this.setName(standardVehicleModelDTO.getName());
    this.setStandardVehicleBrandId(standardVehicleModelDTO.getStandardVehicleBrandId());
    this.setId(standardVehicleModelDTO.getId());
    this.setFrequency(standardVehicleModelDTO.getFrequency());

    return this;
  }
}
