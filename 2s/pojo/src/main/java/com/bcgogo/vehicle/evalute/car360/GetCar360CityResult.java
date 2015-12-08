package com.bcgogo.vehicle.evalute.car360;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-5
 * Time: 下午1:44
 */
public class GetCar360CityResult {
  private String status;
  private String prov_id;
  private String city_id;
  private List<Car360City> city_list;
  private String url;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getProv_id() {
    return prov_id;
  }

  public void setProv_id(String prov_id) {
    this.prov_id = prov_id;
  }

  public String getCity_id() {
    return city_id;
  }

  public void setCity_id(String city_id) {
    this.city_id = city_id;
  }

  public List<Car360City> getCity_list() {
    return city_list;
  }

  public void setCity_list(List<Car360City> city_list) {
    this.city_list = city_list;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
