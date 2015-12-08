package com.bcgogo.enums.user;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-22
 * Time: 下午4:32
 */
public enum MileageType {
  MILEAGE_500("<500km"),
  MILEAGE_500_1000("500-1000km"),
  MILEAGE_1000_1500("1000-1500km"),
  MILEAGE_1500_2000("1500-2000km");


  private String value;

  MileageType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }


}
