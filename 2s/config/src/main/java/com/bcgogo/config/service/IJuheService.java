package com.bcgogo.config.service;

import com.bcgogo.api.ApiArea;
import com.bcgogo.api.response.ApiVehicleViolateRegulationResponse;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.juhe.JuheViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.dto.juhe.VehicleViolateRegulationRecordDTO;
import com.bcgogo.config.dto.juhe.ViolateRegulationCitySearchConditionProvince;
import com.bcgogo.config.model.JuheViolateRegulationCitySearchCondition;
import com.bcgogo.enums.app.VRegulationRecordQueryType;
import com.bcgogo.enums.config.JuheStatus;

import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-10-24
 * Time: 下午3:08
 */
public interface IJuheService {
  void initJuheViolateRegulationCitySearchCondition(Map<String, ViolateRegulationCitySearchConditionProvince> map);

  List<ApiArea> obtainJuheSupportAreaAndViolateRegulations();

  Result queryUnHandledVehicleViolateRegulation(String city,String hphm,String hpzl,String engineNo,String classno,String registno);

  public ApiVehicleViolateRegulationResponse queryVehicleViolateRegulation(String city,String hphm,String hpzl,String engineno,String classno,String registno);

  List<VehicleViolateRegulationRecordDTO> getVehicleViolateRegulationRecord(String city, String vehicleNo, Long recordDate);

  void saveOrUpdateVehicleViolateRegulationRecord(VehicleViolateRegulationRecordDTO... recordDTO);

  public List<JuheViolateRegulationCitySearchConditionDTO> getJuheViolateRegulationCitySearchCondition(String juheCityCode, JuheStatus status);

  public Map<String,JuheViolateRegulationCitySearchConditionDTO> getJuheSearchCondition();

  public List queryVRegulationFromJuhe(String city,String hphm,String hpzl,String engineno,String classno,String registno,String key,VRegulationRecordQueryType queryType);

  JuheViolateRegulationCitySearchCondition getJuheViolateRegulationCitySearchConditionByCityName(String cityName);
}
