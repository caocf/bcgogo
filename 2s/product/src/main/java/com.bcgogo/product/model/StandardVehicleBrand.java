package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 标准车辆品牌实体类
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "standard_vehicle_brand")
public class StandardVehicleBrand extends LongIdentifier {
  private String name;
  private String firstLetter;//车辆品牌首字母（不一定是首个汉字的首字母）
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

  @Column(name = "first_letter")
  public String getFirstLetter() {
    return firstLetter;
  }

  public void setFirstLetter(String firstLetter) {
    this.firstLetter = firstLetter;
  }

  public StandardVehicleBrandDTO toDTO() {
    StandardVehicleBrandDTO standardVehicleBrandDTO = new StandardVehicleBrandDTO();
    standardVehicleBrandDTO.setName(getName());
    standardVehicleBrandDTO.setId(getId());
    standardVehicleBrandDTO.setFirstLetter(getFirstLetter());
    standardVehicleBrandDTO.setFrequency(getFrequency());
    return standardVehicleBrandDTO;
  }

  public StandardVehicleBrand fromDTO(StandardVehicleBrandDTO standardVehicleBrandDTO) {
    this.setName(standardVehicleBrandDTO.getName());
    this.setId(standardVehicleBrandDTO.getId());
    this.setFirstLetter(standardVehicleBrandDTO.getFirstLetter());
    this.setFrequency(standardVehicleBrandDTO.getFrequency());
    return this;
  }
}
