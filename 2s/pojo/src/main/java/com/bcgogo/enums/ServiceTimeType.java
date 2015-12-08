package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-10-22
 * Time: 下午5:49
 * To change this template use File | Settings | File Templates.
 */
public enum ServiceTimeType {
  NO("不可计次"),
  YES("可计次");

  String type;

  ServiceTimeType(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return this.type;
  }
}
