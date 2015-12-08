package com.bcgogo.txn.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.request.MultiFaultRequest;
import com.bcgogo.common.Result;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-12-3
 * Time: 上午11:47
 */
public interface IHandleAppVehicleFaultCodeService {

  /**
   * 发送车辆故障信息
   */
  ApiResponse handleVehicleFaultInfo(VehicleFaultDTO faultDTO) throws Exception;

  List<AppVehicleFaultInfoDTO> saveAppVehicleFaultInfoDTOs(VehicleFaultDTO faultDTO, ObdDTO obdDTO, AppVehicleDTO appVehicleDTO);

  ApiResponse handleMultiVehicleFaultInfo(MultiFaultRequest multiFaultRequest) throws Exception;

  Result sendFaultCode(String imei, String faultCodes, Long reportTime);
}
