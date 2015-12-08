package com.bcgogo.user.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.response.AppGsmVehicleResponse;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.enums.YesNo;
import com.bcgogo.user.model.UserWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-8-22
 * Time: 上午10:42
 */
public interface IAppUserVehicleObdService {

  /**
   * 绑定OBD
   *
   * @param obdBindingDTO OBDBindingDTO
   * @return Result
   */
  Pair<ApiResponse,Boolean> bindingObd(OBDBindingDTO obdBindingDTO) throws Exception;

  void createBaseVehicle(AppVehicleDTO appVehicleDTO, UserWriter writer);

  ObdDTO getObdBySn(String sn);

  AppVehicleDTO getBindingAppVehicleByVinUserNo(String appUserNo, String vin);

  Pair<ApiResponse,Boolean> addOrUpdateAppVehicle(AppVehicleDTO appVehicleDTO);

  Pair<Boolean,Map<String,String>> addVehiclesForGuest(List<AppVehicleDTO> appVehicleDTOs);

  void updateDefaultAppVehicle(Long appVehicleId, String appUserNo, boolean isSetDefault);

  AppVehicleDTO getAppVehicleByUserNoVehicleNo(String appUserNo, String vehicleNo);

  /**
   * 修改保养信息
   *
   * @param appVehicleMaintainDTO
   */
  Result updateVehicleMaintain(AppVehicleMaintainDTO appVehicleMaintainDTO);


  /**
   * 获取车辆列表
   */
  ApiResponse getAppVehicleResponseByAppUserNo(String appUserNo);

  /**
   * 获取一辆车信息
   */
  ApiResponse getAppVehicleDetail(Long vehicleId, String appUserNo);

  ApiResponse getAppVehicleDetail(String appUserNo,String vehicleVin);

  AppVehicleDTO getAppVehicleDetailByVehicleNo(String appUserNo, String vehicleNo, YesNo isDefault);

  /**
   * 获得app用户下的车辆
   *
   * @param appUserNo String
   * @return Map<String,AppVehicleDTO>
   */
  Map<String,AppVehicleDTO> getAppVehicleMapByAppUserNo(String appUserNo);

  Map<Long, AppVehicleDTO> getAppVehicleIdMapByAppUserNo(String appUserNo);

  /**
   * 删除车辆
   */
  ApiResponse deleteVehicle(Long vehicleId,Long appUserId);

  /**
   * 保存或更新车辆信息 油耗、当前里程等信息
   *
   * @param appVehicleDTO
   * @return
   */
  Result saveOrUpdateVehicleCondition(AppVehicleDTO appVehicleDTO);

  //mirror微信部分保存车辆信息
  Result saveOrUpdateVehicle(AppVehicleDTO appVehicleDTO);


  /**
   * 根据车牌号查找车辆
   * @param vehicleNo
   * @return
   */
  public List<AppVehicleDTO> getAppVehicleByVehicleNo(String vehicleNo);

  public AppVehicleDTO getAppVehicleById(Long vehicleId);

  void updateNextMaintainMileagePushMessageRemindLimit(Set<Long> vehicleIds);


  /**
   * 获取一辆车信息
   */
  AppGsmVehicleResponse gsmUserGetAppVehicle(String appUserNo);

  ApiResponse updateOilPriceByAppVehicleDTO(AppVehicleDTO appVehicleDTO);

  ObdDTO getObdByIMei(String iMei);

  ObdDTO getObd_MirrorByIMei(String iMei);

  ObdUserVehicleDTO getBundlingObdUserVehicleDTOByObdId(Long obdId);
}
