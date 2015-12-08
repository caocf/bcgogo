package com.bcgogo.config.service;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.juhe.ViolateRegulationCitySearchConditionDTO;
import com.bcgogo.config.dto.juhe.ViolateRegulationResult;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.JuheViolateRegulationCitySearchCondition;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-11-7
 * Time: 下午4:25
 */
@Component
public class ViolateRegulationService implements IViolateRegulationService {
  @Autowired
  private ConfigDaoManager configDaoManager;


  @Override
  public ApiResponse getJuheViolateRegulationSerachCondition(String appUserNo, String[] juheCityCodes, Map<String, AppVehicleDTO> map, Map<String, AreaDTO> areaDTOMap) {
    ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_SUCCESS);
    ViolateRegulationResult result = new ViolateRegulationResult();
    if (ArrayUtil.isEmpty(juheCityCodes))
      return MessageCode.toApiResponse(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_FAIL, ValidateMsg.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_CITY_CODE_EMPTY);
    ViolateRegulationCitySearchConditionDTO conditionDTO;

    List<JuheViolateRegulationCitySearchCondition> conditionList = configDaoManager.getReader().getJuheViolateRegulationCitySearchCondition(juheCityCodes);
    AreaDTO areaDTO;
    for (String vehicleNo : map.keySet()) {
      areaDTO = areaDTOMap.get(vehicleNo);
      if (areaDTO != null)
        result.getVehicleCityRelation().put(vehicleNo, areaDTO.getJuheCityCode());
    }
    Set<Long> areaNoSet = ServiceManager.getService(IAreaService.class).getAreaNoByJuheCityCode(juheCityCodes);
    for (Long areaNo : areaNoSet) {
      areaDTO = AreaCacheManager.getAreaDTOByNo(areaNo);
      if (areaDTO != null)
        result.getJuheCityCodeAreaRelation().put(areaDTO.getJuheCityCode(), areaDTO.toApiArea());
    }
    for (AppVehicleDTO appVehicleDTO : map.values()) {
      List<ViolateRegulationCitySearchConditionDTO> conditionDTOs = new ArrayList<ViolateRegulationCitySearchConditionDTO>();
      for (JuheViolateRegulationCitySearchCondition condition : conditionList) {
        conditionDTO = condition.toViolateRegulationCitySearchConditionDTO();
        conditionDTO.setAppVehicleInfo(appVehicleDTO);
        if (conditionDTO.getStatus() == JuheStatus.IN_ACTIVE) {
          conditionDTO.setSuccess(false);
          conditionDTO.setMessage(ValidateMsg.DO_NOT_SUPPORT_THE_CITY.getValue());
        }
        conditionDTOs.add(conditionDTO);
      }
      result.getVehicleCityViolateRegulationConditions().put(appVehicleDTO.getVehicleNo(), conditionDTOs);
    }
    ApiResultResponse<ViolateRegulationResult> response = new ApiResultResponse<ViolateRegulationResult>(apiResponse);
    response.setResult(result);
    return response;
  }

}
