package com.bcgogo.user.service;

import com.bcgogo.api.ObdUserVehicleDTO;
import com.bcgogo.common.Result;
import com.bcgogo.etl.model.GsmVehicleInfo;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.dto.VehicleOBDMileageDTO;
import com.bcgogo.user.model.Vehicle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Zou Jianhong
 * Date: 12-2-14
 * Time: 下午4:42
 * To change this template use File | Settings | File Templates.
 */

public interface IVehicleService {

  /**
   * 根据客户ID获得车辆信息
   * @param customerId
   * @return
   * @throws Exception
   */
  public List<CustomerVehicleResponse> findVehicleListByCustomerId(Long customerId ) throws Exception;

   List<VehicleDTO> getVehicleListByCustomerId(Long customerId) throws Exception;

  /**
   * 根据ID获得车辆信息
   * @param vehicleId
   * @return
   * @throws Exception
   */
  public VehicleDTO findVehicleById(Long vehicleId) throws Exception;

  /**
   * 在时间区间里查找车牌号
   *
   * @param fromTime
   * @param endTime
   * @author zhangchuanlong
   * @return
   */
  public  List<String> getVehicleLicenceNos(Long  shopId,Long fromTime,Long endTime);

  Map<Long,VehicleDTO> getVehicleByVehicleIdSet(Long shopId, Set<Long> vehicleIds);

  List<Long> getVehicleIds(Long shopId,int start,int rows);

  public VehicleDTO updateVehicle(VehicleDTO vehicleDTO);

  public List<CustomerVehicleResponse> findVehicleInfoByCustomerId(Long customerId) throws Exception;

  boolean compareVehicleSameWithHistory(VehicleDTO historyVehicleDTO, Long shopId);

  //连到appVehicle表扫描
  List<CustomerVehicleDTO> getMatchMaintainMileageCustomerVehicle(int pageSize,Double[] intervals,Long lastCustomerVehicleId);

  Map<Long,CustomerVehicleDTO> getCustomerVehicleDTOMapByVehicleIds(Long... vehicleIds);
  //根据vehicle上的OBDMileage扫描
  List<CustomerVehicleDTO> getMatchCustomerVehicleByVehicleOBDMileage(int pageSize,Double[] intervals,Long lastCustomerVehicleId);


  VehicleDTO getVehicleDTOByLicenceNo(Long shopId, String licenceNo);
  List<VehicleDTO> getVehicleDTOByLicenceNo(String... licenceNo);

  Map<String, VehicleDTO> getVehicleDTOMapByLicenceNo(Long shopId, Set<String> LicenceNoSet);

  VehicleDTO updateVehicleMobile(Long shopId,String licenceNo, String mobile);

  List<VehicleDTO> getVehiclesByCustomerMobile(String mobile, String vehicleNo);

  //扫描vehicle和appVehicle表，找到更新的appVehicle当前里程和跟新时间
  List<VehicleOBDMileageDTO> getVehicleOBDMileageByStartVehicleId(Long startVehicleId, int pageSize);

  //更新vehicle obd当前里程 和更新时间
  void updateVehicleOBDMileage(VehicleOBDMileageDTO vehicleOBDMileageDTO);

  public Map<Long, ObdUserVehicleDTO> getObdUserVehicles(Long... appVehicleId);

  public Map<String, List<ObdUserVehicleDTO>> getObdUserVehiclesByAppUserNos(Set<String> appUserNos);

  public VehicleDTO getVehicleDTOById(Long id);

  public Map<Long,Boolean> isAppVehicle(Long... vehicleIds);

  VehicleDTO getVehicleDTOByIMei(String iMei);

  void updateVehicleMilByGsmVehicleInfo(VehicleDTO vehicleDTO, List<GsmVehicleInfo> gsmVehicleInfoList);

  VehicleDTO getVehicleByGsmObdImei(Long vehicleId, String gsmObdImei);

  Map<String,VehicleDTO> getVehicleDTOMapByIMeis(Set<String> strings);

  List<String> getVehicleImeiByShopId(Long shopId);



  public Result maintainRegister(CustomerVehicleResponse customerVehicleResponse) throws Exception;

  public List<VehicleDTO> getVehicleByCondition(VehicleSearchConditionDTO conditionDTO);
}
