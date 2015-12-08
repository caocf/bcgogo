package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleDTO;
import com.bcgogo.api.VehicleInfoSuggestionDTO;
import com.bcgogo.api.response.ApiVehicleViolateRegulationResponse;
import com.bcgogo.common.Result;
import com.bcgogo.user.dto.VehicleDTO;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-23
 * Time: 下午1:47
 */
public interface IAppVehicleService {

  @Deprecated
  ApiResponse getBrandModelByKeywordsV1(String keywords, String type, Long brandId);

  ApiResponse getBrandModelByKeywordsV2(String keywords, String type, Long brandId);

  VehicleInfoSuggestionDTO getVehicleInfoSuggestion(String vehicleNo, String mobile);

  void syncAppVehicle(List<VehicleDTO> vehicleDTOList);

  public ApiResponse addOrUpdateGsmAppVehicle(AppVehicleDTO appVehicleDTO);

  Result getVRegulationRecordDTO(String appUserNo) throws Exception;

  ApiVehicleViolateRegulationResponse getVRegulationRecordDTO_Mirror(String appUserNo) throws Exception;
}
