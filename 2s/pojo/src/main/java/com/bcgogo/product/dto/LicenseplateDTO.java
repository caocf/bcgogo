package com.bcgogo.product.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Rex
 * Date: 12-1-5
 * Time: 下午3:16
 * To change this template use File | Settings | File Templates.
 */
public class LicenseplateDTO implements Serializable {
      private  Long id;
      private  String carno;
      private  String areaName;
      private  String areaFirstname;
      private  String areaFirstcarno;

  public String getAreaFirstcarno() {
    return areaFirstcarno;
  }

  public void setAreaFirstcarno(String areaFirstcarno) {
    this.areaFirstcarno = areaFirstcarno;
  }
  public String getCarno() {
    return carno;
  }

  public void setCarno(String carno) {
    this.carno = carno;
  }

  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  public String getAreaFirstname() {
    return areaFirstname;
  }

  public void setAreaFirstname(String areaFirstname) {
    this.areaFirstname = areaFirstname;
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
