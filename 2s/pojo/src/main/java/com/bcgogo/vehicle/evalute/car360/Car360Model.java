package com.bcgogo.vehicle.evalute.car360;

import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-14
 * Time: 上午10:45
 */
public class Car360Model {
  private String model_id;
  private String model_name;
  //指导价
  private String model_price;
  //年款
  private String model_year;
  ////排放标准
  private String discharge_standard;

  public StandardVehicleModelDTO toStandardVehicleModelDTO(){
    StandardVehicleModelDTO modelDTO=new StandardVehicleModelDTO();
    modelDTO.setId(NumberUtil.longValue(getModel_id()));
    modelDTO.setName(getModel_name());
    modelDTO.setModelPrice(getModel_price());
    modelDTO.setModelYear(getModel_year());
    modelDTO.setDischargeStandard(getDischarge_standard());
    return modelDTO;
  }


  public String getModel_id() {
    return model_id;
  }

  public void setModel_id(String model_id) {
    this.model_id = model_id;
  }

  public String getModel_name() {
    return model_name;
  }

  public void setModel_name(String model_name) {
    this.model_name = model_name;
  }

  public String getModel_price() {
    return model_price;
  }

  public void setModel_price(String model_price) {
    this.model_price = model_price;
  }

  public String getModel_year() {
    return model_year;
  }

  public void setModel_year(String model_year) {
    this.model_year = model_year;
  }

  public String getDischarge_standard() {
    return discharge_standard;
  }

  public void setDischarge_standard(String discharge_standard) {
    this.discharge_standard = discharge_standard;
  }
}
