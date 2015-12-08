package com.bcgogo.enums.user;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:11
 * To change this template use File | Settings | File Templates.
 */
public enum VehicleBrandModelDataType {
  SUPPLIER("供应商"),
  CUSTOMER("客户");

  private String value;

  private VehicleBrandModelDataType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
