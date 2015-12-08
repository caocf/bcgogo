package com.bcgogo.driving.service;

import com.bcgogo.driving.model.JuheViolateRegulationCitySearchCondition;

/**
 * User: ZhangJuntao
 * Date: 13-10-24
 * Time: 下午3:08
 */
public interface IJuheService {

  JuheViolateRegulationCitySearchCondition getJuheViolateRegulationCitySearchConditionByCityName(String cityName);

}
