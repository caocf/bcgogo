package com.bcgogo.etl.model;

import com.bcgogo.etl.model.mongodb.XLongIdentifier;
import com.bcgogo.etl.model.mongodb.XObjectId;
import com.sun.org.apache.xpath.internal.objects.XObject;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-8
 * Time: 下午3:56
 */
public class IllegalCity extends XLongIdentifier{
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
