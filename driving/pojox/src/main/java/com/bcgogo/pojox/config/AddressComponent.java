package com.bcgogo.pojox.config;


import com.bcgogo.pojox.util.StringUtil;

/**
 * User: ZhangJuntao
 * Date: 13-8-5
 * Time: 下午3:43
 */
public class AddressComponent {
  private String province;  //省份名称
  private String city;    //城市名称
  private String district;    //区县名称
  private String street;     //街道名称
  private String streetNumber;  //门牌号码

  public String getAddress() {
    StringBuilder sb = new StringBuilder();
    sb.append(StringUtil.toTrim(this.getProvince()))
      .append(StringUtil.toTrim(getCity()))
      .append(StringUtil.toTrim(getDistrict()))
      .append(StringUtil.toTrim(getStreet()))
      .append(StringUtil.toTrim(getStreetNumber()));
    return sb.toString();
  }

  public String getStreetInfo() {
    StringBuffer sb = new StringBuffer();
    if (StringUtil.isNotEmpty(getDistrict())) {
      sb.append(getDistrict());
    }
    if (StringUtil.isNotEmpty(getStreet())) {
      sb.append(getStreet());
    }
    return sb.toString();
  }

  public String getStreetNumberInfo() {
    StringBuffer sb = new StringBuffer();
    if (StringUtil.isNotEmpty(getStreet())) {
      sb.append(getStreet());
    }
    if (StringUtil.isNotEmpty(getStreetNumber())) {
      sb.append(getStreetNumber());
    }
    return sb.toString();
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public void setStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
  }

  @Override
  public String toString() {
    return "AddressComponent{" +
      "province='" + province + '\'' +
      ", city='" + city + '\'' +
      ", district='" + district + '\'' +
      ", street='" + street + '\'' +
      ", streetNumber='" + streetNumber + '\'' +
      '}';
  }
}
