package com.bcgogo.vehicle.evalute.car360;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-14
 * Time: 上午9:46
 */
public class GetCar360SeriesResult {
  private String status;
  private List<Car360Series> series_list;
  private String url;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Car360Series> getSeries_list() {
    return series_list;
  }

  public void setSeries_list(List<Car360Series> series_list) {
    this.series_list = series_list;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
