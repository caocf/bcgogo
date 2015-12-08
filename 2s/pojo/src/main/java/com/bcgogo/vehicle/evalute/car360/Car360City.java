package com.bcgogo.vehicle.evalute.car360;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.utils.NumberUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-5
 * Time: 下午2:15
 */
public class Car360City {
  private String city_id;
  private String city_name;
  private String prov_id;
  private String prov_name;

  public AreaDTO toAreaDTO(){
    AreaDTO areaDTO=new AreaDTO();
    areaDTO.setNo(NumberUtil.longValue(getCity_id()));
    areaDTO.setName(getCity_name());
    areaDTO.setParentNo(NumberUtil.longValue(getProv_id()));
    return areaDTO;
  }

  public String getCity_id() {
    return city_id;
  }

  public void setCity_id(String city_id) {
    this.city_id = city_id;
  }

  public String getCity_name() {
    return city_name;
  }

  public void setCity_name(String city_name) {
    this.city_name = city_name;
  }

  public String getProv_id() {
    return prov_id;
  }

  public void setProv_id(String prov_id) {
    this.prov_id = prov_id;
  }

  public String getProv_name() {
    return prov_name;
  }

  public void setProv_name(String prov_name) {
    this.prov_name = prov_name;
  }
}
