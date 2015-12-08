package com.bcgogo.product.standardVehicleBrandModel;

import com.bcgogo.utils.NumberUtil;

/**
 * 标准车型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-23
 * Time: 上午11:09
 * To change this template use File | Settings | File Templates.
 */
public class StandardVehicleModelDTO {
  private String name;//车型名字
  private Long standardVehicleBrandId;//标准车辆品牌id
  private Long id;
  private Integer frequency;
  //指导价
  private String modelPrice;
  //年款
  private String modelYear;
  ////排放标准
  private String dischargeStandard;

  public Integer getFrequency() {
    return frequency;
  }

  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getStandardVehicleBrandId() {
    return standardVehicleBrandId;
  }

  public void setStandardVehicleBrandId(Long standardVehicleBrandId) {
    this.standardVehicleBrandId = standardVehicleBrandId;
  }

  public String getModelPrice() {
    return modelPrice;
  }

  public void setModelPrice(String modelPrice) {
    this.modelPrice = modelPrice;
  }

  public String getModelYear() {
    return modelYear;
  }

  public void setModelYear(String modelYear) {
    this.modelYear = modelYear;
  }

  public String getDischargeStandard() {
    return dischargeStandard;
  }

  public void setDischargeStandard(String dischargeStandard) {
    this.dischargeStandard = dischargeStandard;
  }
}
