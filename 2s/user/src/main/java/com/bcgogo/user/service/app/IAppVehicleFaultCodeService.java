package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppVehicleFaultInfoDTO;
import com.bcgogo.api.AppVehicleFaultInfoOperateDTO;
import com.bcgogo.api.request.FaultCodeListRequest;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.app.ErrorCodeTreatStatus;
import com.bcgogo.user.model.app.AppVehicleFaultInfo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-28
 * Time: 上午10:10
 */
public interface IAppVehicleFaultCodeService {

  //故障码操作
  ApiResponse handleFaultCode(String appUserNo, AppVehicleFaultInfoOperateDTO appVehicleFaultInfoOperateDTO);

  //故障码列表
  ApiResponse getFaultInfoList(FaultCodeListRequest faultCodeListRequest) throws Exception;

  List<AppVehicleFaultInfoDTO> searchAppVehicleFaultInfoDTOs(String appUserNo, Long defaultAppVehicleId,Pager pager,
                                                             ErrorCodeTreatStatus[] status);

  List<AppVehicleFaultInfoDTO> findAppVehicleFaultInfoDTOs(String appUserNo,String status);

  AppVehicleFaultInfo getAppVehicleFaultInfoById(Long id);

  void updateAppVehicleFaultInfo(AppVehicleFaultInfo appVehicleFaultInfo);

}
