package com.bcgogo.vehicle.evalute.car360;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-6
 * Time: 上午11:27
 */
public class GetCar360BrandResult {
  private String status;
  private List<Car360Brand> brand_list;
  private String url;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Car360Brand> getBrand_list() {
    return brand_list;
  }

  public void setBrand_list(List<Car360Brand> brand_list) {
    this.brand_list = brand_list;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
