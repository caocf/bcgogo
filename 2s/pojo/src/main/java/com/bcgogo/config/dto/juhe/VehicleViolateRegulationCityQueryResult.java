package com.bcgogo.config.dto.juhe;

import java.util.List;

/**
 * User: lw
 * Date: 14-04-23
 * Time: 下午1:58
 */
public class VehicleViolateRegulationCityQueryResult {
  private String province;
  private String city;
  private String hphm;
  private String hpzl;

  private List<VehicleViolateRegulationRecordDTO> lists;

  public VehicleViolateRegulationCityQueryResult(){

  }

  public VehicleViolateRegulationCityQueryResult(String province, String city, String hphm, String hpzl, List<VehicleViolateRegulationRecordDTO> lists) {
    this.setProvince(province);
    this.setCity(city);
    this.setHphm(hphm);
    this.setHpzl(hpzl);
    this.setLists(lists);
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

  public String getHphm() {
    return hphm;
  }

  public void setHphm(String hphm) {
    this.hphm = hphm;
  }

  public String getHpzl() {
    return hpzl;
  }

  public void setHpzl(String hpzl) {
    this.hpzl = hpzl;
  }

  public List<VehicleViolateRegulationRecordDTO> getLists() {
    return lists;
  }

  public void setLists(List<VehicleViolateRegulationRecordDTO> lists) {
    this.lists = lists;
  }
}
