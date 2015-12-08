package com.bcgogo.wx.user;

import com.bcgogo.enums.DeletedType;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-5
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class WXUserVehicleDTO {
  private Long id;
  private String openId;
  private String vehicleNo;
  private String vin;
  private String engineNo;
  private Long province;     //省
  private Long city;          //市
  private String juheCityCode;
  private DeletedType deleted;
  private String idStr;

  public String getProvinceAndCity() {
    return provinceAndCity;
  }

  public void setProvinceAndCity(String provinceAndCity) {
    this.provinceAndCity = provinceAndCity;
  }

  private String provinceAndCity;

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }else {
      setIdStr("");
    }
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String getVin() {
    return vin;
  }

  public void setVin(String vin) {
    this.vin = vin;
  }

  public String getEngineNo() {
    return engineNo;
  }

  public void setEngineNo(String engineNo) {
    this.engineNo = engineNo;
  }

  public Long getProvince() {
    return province;
  }

  public void setProvince(Long province) {
    this.province = province;
  }

  public Long getCity() {
    return city;
  }

  public void setCity(Long city) {
    this.city = city;
  }

  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
