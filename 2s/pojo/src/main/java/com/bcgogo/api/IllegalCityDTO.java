package com.bcgogo.api;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 下午2:39
 */
public class IllegalCityDTO {
  private String id;
  private String appUserNo;
  private String juheCityCode;
  private String juheCityName;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

  public String getJuheCityName() {
    return juheCityName;
  }

  public void setJuheCityName(String juheCityName) {
    this.juheCityName = juheCityName;
  }
}
