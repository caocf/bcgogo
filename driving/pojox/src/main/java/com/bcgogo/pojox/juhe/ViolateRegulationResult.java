package com.bcgogo.pojox.juhe;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Hans
 * Date: 13-12-5
 * Time: 上午11:23
 */
public class ViolateRegulationResult {
  /**
   * 车辆 聚合城市关系
   */
  private Map<String, String> vehicleCityRelation = new HashMap<String, String>();
  /**
   * 每辆车对应的城市违章查询信息
   */
  private Map<String, List<ViolateRegulationCitySearchConditionDTO>> vehicleCityViolateRegulationConditions = new HashMap<String, List<ViolateRegulationCitySearchConditionDTO>>();


  public Map<String, String> getVehicleCityRelation() {
    return vehicleCityRelation;
  }

  public void setVehicleCityRelation(Map<String, String> vehicleCityRelation) {
    this.vehicleCityRelation = vehicleCityRelation;
  }

  public Map<String, List<ViolateRegulationCitySearchConditionDTO>> getVehicleCityViolateRegulationConditions() {
    return vehicleCityViolateRegulationConditions;
  }

  public void setVehicleCityViolateRegulationConditions(Map<String, List<ViolateRegulationCitySearchConditionDTO>> vehicleCityViolateRegulationConditions) {
    this.vehicleCityViolateRegulationConditions = vehicleCityViolateRegulationConditions;
  }


}
