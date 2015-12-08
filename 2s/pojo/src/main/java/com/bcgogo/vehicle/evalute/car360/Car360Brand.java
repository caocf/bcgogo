package com.bcgogo.vehicle.evalute.car360;

import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-6
 * Time: 上午11:31
 */
public class Car360Brand {
  private String brand_id;
  private String brand_name;
  private String initial;

  public StandardVehicleBrandDTO toStandardVehicleBrandDTO(){
    StandardVehicleBrandDTO brandDTO=new StandardVehicleBrandDTO();
    brandDTO.setId(NumberUtil.longValue(getBrand_id()));
    brandDTO.setName(getBrand_name());
    brandDTO.setFirstLetter(getInitial());
    return brandDTO;
  }

  public String getBrand_id() {
    return brand_id;
  }

  public void setBrand_id(String brand_id) {
    this.brand_id = brand_id;
  }

  public String getBrand_name() {
    return brand_name;
  }

  public void setBrand_name(String brand_name) {
    this.brand_name = brand_name;
  }

  public String getInitial() {
    return initial;
  }

  public void setInitial(String initial) {
    this.initial = initial;
  }
}
