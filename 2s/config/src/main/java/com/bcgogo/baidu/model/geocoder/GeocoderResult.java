package com.bcgogo.baidu.model.geocoder;

import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.user.Coordinate;
import com.google.gson.annotations.SerializedName;

/**
 * User: ZhangJuntao
 * Date: 13-8-5
 * Time: 下午3:47
 */
public class GeocoderResult {
  private Coordinate location;
  @SerializedName("formatted_address")
  private String formattedAddress; //详细地址描述
  private String business;   //周围商圈
  private Integer cityCode;  //城市代码
  private String level;      //级别
  private String confidence;  //可信度,
  private String precise;     //位置的附加信息，是否精确查找（1为精确查找，0为不精确查找）,
  private AddressComponent addressComponent;

  public Coordinate getLocation() {
    return location;
  }

  public void setLocation(Coordinate location) {
    this.location = location;
  }

  public String getFormattedAddress() {
    return formattedAddress;
  }

  public void setFormattedAddress(String formattedAddress) {
    this.formattedAddress = formattedAddress;
  }

  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getConfidence() {
    return confidence;
  }

  public void setConfidence(String confidence) {
    this.confidence = confidence;
  }

  public String getPrecise() {
    return precise;
  }

  public void setPrecise(String precise) {
    this.precise = precise;
  }

  public AddressComponent getAddressComponent() {
    return addressComponent;
  }

  public void setAddressComponent(AddressComponent addressComponent) {
    this.addressComponent = addressComponent;
  }

  @Override
  public String toString() {
    return "GeocoderResult{" +
        "location=" + location +
        ", formattedAddress='" + formattedAddress + '\'' +
        ", business='" + business + '\'' +
        ", cityCode='" + cityCode + '\'' +
        ", level='" + level + '\'' +
        ", confidence='" + confidence + '\'' +
        ", precise='" + precise + '\'' +
        ", addressComponent=" + addressComponent +
        '}';
  }
}
