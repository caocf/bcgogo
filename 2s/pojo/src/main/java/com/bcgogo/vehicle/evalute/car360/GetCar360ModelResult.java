package com.bcgogo.vehicle.evalute.car360;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-14
 * Time: 上午10:44
 */
public class GetCar360ModelResult {
  private String status;
  private List<Car360Model> model_list;
  private String url;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Car360Model> getModel_list() {
    return model_list;
  }

  public void setModel_list(List<Car360Model> model_list) {
    this.model_list = model_list;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
