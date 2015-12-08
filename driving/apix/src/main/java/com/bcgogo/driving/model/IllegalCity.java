package com.bcgogo.driving.model;

import com.bcgogo.driving.model.mongodb.XLongIdentifier;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-8
 * Time: 下午3:56
 */
public class IllegalCity extends XLongIdentifier {
  private String appUserNo;
    private String juheCityCode;
    private String juheCityName;

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
