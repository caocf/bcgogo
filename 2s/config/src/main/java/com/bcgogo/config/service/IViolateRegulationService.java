package com.bcgogo.config.service;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.config.dto.AreaDTO;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-11-7
 * Time: 下午4:25
 */
public interface IViolateRegulationService {

  ApiResponse getJuheViolateRegulationSerachCondition(String appUserNo, String[] juheCityCodes,Map<String, AppVehicleDTO> map ,Map<String, AreaDTO> areaDTOMap);

}
