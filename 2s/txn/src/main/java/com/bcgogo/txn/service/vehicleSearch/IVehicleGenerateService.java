package com.bcgogo.txn.service.vehicleSearch;

import com.bcgogo.search.dto.VehicleSearchResultDTO;

/**
 * Created by XinyuQiu on 14-5-26.
 */
public interface IVehicleGenerateService {
  void generateVehicleSearchResult(Long shopId, VehicleSearchResultDTO vehicleSearchResultDTO) throws Exception;
}
