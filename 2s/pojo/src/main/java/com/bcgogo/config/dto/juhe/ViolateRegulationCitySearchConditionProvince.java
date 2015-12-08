package com.bcgogo.config.dto.juhe;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-10-23
 * Time: 下午2:00
 */
public class ViolateRegulationCitySearchConditionProvince {
  private String province;
  private List<ViolateRegulationCitySearchConditionCity> citys;

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public List<ViolateRegulationCitySearchConditionCity> getCitys() {
    return citys;
  }

  public void setCitys(List<ViolateRegulationCitySearchConditionCity> citys) {
    this.citys = citys;
  }
}
