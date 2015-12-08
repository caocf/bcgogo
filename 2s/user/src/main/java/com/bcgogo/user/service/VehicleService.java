package com.bcgogo.user.service;

import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.api.ObdDTO;
import com.bcgogo.api.ObdUserVehicleDTO;
import com.bcgogo.common.Result;
import com.bcgogo.enums.VehicleStatus;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.etl.GsmVehicleInfoDTO;
import com.bcgogo.etl.model.GsmVehicleInfo;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.CustomerVehicleResponse;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.dto.VehicleOBDMileageDTO;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.app.ObdUserVehicle;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Zou Jianhong
 * Date: 12-2-14
 * Time: 下午4:46
 * To change this template use File | Settings | File Templates.
 */
@Component
public class VehicleService implements IVehicleService {

  private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  /**
   * 根据客户ID获得车辆信息
   *
   * @param customerId
   * @return
   * @throws Exception
   */
  @Override
  public List<CustomerVehicleResponse> findVehicleListByCustomerId(Long customerId) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    UserWriter writer = userDaoManager.getWriter();

    List<CustomerVehicleDTO> customerVehicleDTOList = new ArrayList<CustomerVehicleDTO>();
    for (CustomerVehicle cv : writer.getVehicleByCustomerId(customerId)) {
      customerVehicleDTOList.add(cv.toDTO());
    }
    List<CustomerVehicleResponse> customerVehicleResponseList = new ArrayList<CustomerVehicleResponse>();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);

    if (customerVehicleDTOList != null) {
      CustomerVehicleResponse customerVehicleResponse = null;
      for (CustomerVehicleDTO customerVehicleDTO : customerVehicleDTOList) {
        Long vehicleId = customerVehicleDTO.getVehicleId();
        if (vehicleId != null) {
          VehicleDTO vehicleDTO = vehicleService.findVehicleById(vehicleId);
          if (vehicleDTO != null && !VehicleStatus.DISABLED.equals(vehicleDTO.getStatus())) {
            //车辆基本信息
            customerVehicleResponse = new CustomerVehicleResponse();
            customerVehicleResponse.setLastDate(0L);
            customerVehicleResponse.setCustomerId(customerId);                //客户id
            customerVehicleResponse.setVehicleId(vehicleId);                  //车辆id
            customerVehicleResponse.setLicenceNo(vehicleDTO.getLicenceNo());                  //车牌号
            customerVehicleResponse.setBrand(vehicleDTO.getBrand());          //品牌
            customerVehicleResponse.setModel(vehicleDTO.getModel());          //车型
            customerVehicleResponse.setYear(vehicleDTO.getYear());            //年代
            customerVehicleResponse.setEngine(vehicleDTO.getEngine());        //排量
            customerVehicleResponse.setVin(vehicleDTO.getChassisNumber());              //车架号
            customerVehicleResponse.setEngineNo(vehicleDTO.getEngineNo());
            customerVehicleResponse.setCarDate(vehicleDTO.getCarDate());      //购车日期
            customerVehicleResponse.setStartMileage(vehicleDTO.getStartMileage());        //进厂里程
            if(vehicleDTO.getStartMileage()!=null){
              customerVehicleResponse.setStartMileageStr(StringUtil.subZeroAndDot(String.valueOf(vehicleDTO.getStartMileage())));
            }
            customerVehicleResponse.setObdMileage(vehicleDTO.getObdMileage());        //当前里程
            if(vehicleDTO.getObdMileage()!=null){
              customerVehicleResponse.setObdMileageStr(StringUtil.subZeroAndDot(String.valueOf(vehicleDTO.getObdMileage())));
            }

            customerVehicleResponse.setMaintainTime(customerVehicleDTO.getMaintainTime()); //预约保养时间
            customerVehicleResponse.setExamineTime(customerVehicleDTO.getExamineTime());//预约检车时间
            customerVehicleResponse.setInsureTime(customerVehicleDTO.getInsureTime()); //预约保险时间
            customerVehicleResponse.setMaintainMileage(customerVehicleDTO.getMaintainMileage()); //预约保养里程
            customerVehicleResponse.setContact(vehicleDTO.getContact());
            customerVehicleResponse.setMobile(vehicleDTO.getMobile());
            customerVehicleResponse.setColor(vehicleDTO.getColor());
            customerVehicleResponse.setAppointServiceDTOs(userService.getAppointServiceByCustomerVehicle(vehicleDTO.getShopId(), vehicleId, customerId));
            customerVehicleResponse.setConsumeTimes(NumberUtil.longValue(customerVehicleDTO.getConsumeTimes()));
            customerVehicleResponse.setTotalConsume(NumberUtil.toReserve(customerVehicleDTO.getTotalConsume(), NumberUtil.MONEY_PRECISION));
            customerVehicleResponse.setGsmObdImeiMoblie(vehicleDTO.getGsmObdImeiMoblie());
            customerVehicleResponse.setGsmObdImei(vehicleDTO.getGsmObdImei());
            customerVehicleResponse.setObdId(vehicleDTO.getObdId());

            customerVehicleResponse.setLastMaintainMileage(customerVehicleDTO.getLastMaintainMileage());
            customerVehicleResponse.setLastMaintainTime(customerVehicleDTO.getLastMaintainTime());
            customerVehicleResponse.setMaintainTimePeriod(customerVehicleDTO.getMaintainTimePeriod());
            customerVehicleResponse.setMaintainMileagePeriod(customerVehicleDTO.getMaintainMileagePeriod());
            customerVehicleResponse.setNextMaintainMileageAccess(customerVehicleDTO.getNextMaintainMileageAccess());

//            //里程更新与后视镜同步
//            ObdDTO obdDTO = ServiceManager.getService(IObdManagerService.class).getObdByImei(vehicleDTO.getGsmObdImei());
//            GsmVehicleInfoDTO gsmVehicleInfoDTO = null;
//            GsmVehicleDataDTO gsmVehicleDataDTO = null;
//            if(obdDTO!=null){
//              if (ObdType.MIRROR.equals(obdDTO.getObdType())) { //如果是后视镜的话
//                IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
//                ObdUserVehicleDTO obdUserVehicleDTO = CollectionUtil.getFirst(appUserService.getOBDUserVehicleByObdIds(obdDTO.getId()));
//                IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
//                if(obdUserVehicleDTO!=null){
//                   gsmVehicleDataDTO = gsmVehicleDataService.getLastGsmVehicleData(obdUserVehicleDTO.getAppUserNo());
//                }
//                if (gsmVehicleDataDTO != null) {
//                  if(StringUtil.isNotEmpty(gsmVehicleDataDTO.getCurMil())){
////                  vehicleDTO.setObdMileage(Double.valueOf(gsmVehicleDataDTO.getCurMil()));
////                  customerVehicleResponse.setObdMileage(vehicleDTO.getObdMileage());        //当前里程
//                    customerVehicleResponse.setObdMileageStr(gsmVehicleDataDTO.getCurMil().toString());
//                  }
//                }
//              }
//            }

            customerVehicleResponseList.add(customerVehicleResponse);
          }
        }
      }
    }
    return customerVehicleResponseList;
  }

  public List<CustomerVehicleResponse> findVehicleInfoByCustomerId(Long customerId) throws Exception {
    UserWriter userWriter = userDaoManager.getWriter();
    List<CustomerVehicle> customerVehicles=userWriter.getVehicleByCustomerId(customerId);
    if(CollectionUtils.isEmpty(customerVehicles)){
      return null;
    }
    List<CustomerVehicleResponse> responseList = new ArrayList<CustomerVehicleResponse>();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    CustomerVehicleResponse customerVehicleResponse = null;
    VehicleDTO vehicleDTO=null;
    for (CustomerVehicle customerVehicle : customerVehicles){
      if(customerVehicle==null||VehicleStatus.DISABLED.equals(customerVehicle.getStatus())||customerVehicle.getVehicleId()==null){
        continue;
      }
      vehicleDTO=vehicleService.findVehicleById(customerVehicle.getVehicleId());
      if (vehicleDTO != null && !VehicleStatus.DISABLED.equals(vehicleDTO.getStatus())) {
        customerVehicleResponse = new CustomerVehicleResponse(customerVehicle.toDTO(),vehicleDTO,null);
        responseList.add(customerVehicleResponse);
      }
    }
    return responseList;
  }

  /**
   * 比较不同，只比较：
   * vehicleId, licenceNo, brand, model
   * @param historyVehicleDTO
   * @param shopId
   * @return
   */
  @Override
  public boolean compareVehicleSameWithHistory(VehicleDTO historyVehicleDTO, Long shopId) {
    if(historyVehicleDTO == null || historyVehicleDTO.getId() == null){
      return false;
    }
    VehicleDTO vehicleDTO = ServiceManager.getService(IUserService.class).getVehicleById(historyVehicleDTO.getId());
    if(vehicleDTO == null){
      return false;
    }
    return vehicleDTO.compareSame(historyVehicleDTO);
  }

  @Override
  public List<VehicleDTO> getVehicleListByCustomerId(Long customerId) throws Exception {
    List<VehicleDTO> vehicleDTOList= new ArrayList<VehicleDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerVehicle>  customerVehicles =writer.getVehicleByCustomerId(customerId) ;
    if(CollectionUtils.isEmpty(customerVehicles))return vehicleDTOList;
     for (CustomerVehicle cv : customerVehicles) {
       if (cv.getVehicleId() == null) continue;
       Vehicle vehicle = writer.getById(Vehicle.class, cv.getVehicleId());
       if(vehicle!=null && vehicle.getStatus()!=VehicleStatus.DISABLED){
         VehicleDTO vehicleDTO =  vehicle.toDTO();
         vehicleDTO.setCustomerVehicleInfo(cv.toDTO());
         vehicleDTOList.add(vehicleDTO);
       }
    }
    return vehicleDTOList;
  }

  @Override
  public VehicleDTO findVehicleById(Long vehicleId) throws Exception {
    UserWriter writer = userDaoManager.getWriter();

    Vehicle vehicle = writer.getById(Vehicle.class, vehicleId);

    if (vehicle == null) return null;
    return vehicle.toDTO();
  }

  /**
   * 获取一段时间区间内车牌号
   *
   * @param shopId
   * @param fromTime
   * @param endTime
   * @return
   */
  @Override
  public List<String> getVehicleLicenceNos(Long shopId, Long fromTime, Long endTime) {
    UserWriter writer = userDaoManager.getWriter();
    List<String> licenceNos = new ArrayList<String>();
    List<Vehicle> vehicleList = writer.getVehicleLicenceNos(shopId, fromTime, endTime);
    if (vehicleList != null && vehicleList.size() > 0) {
      for (Vehicle v : vehicleList) {
        if (StringUtils.isNotBlank(v.getLicenceNo())) {
        licenceNos.add(v.getLicenceNo());
        } else {
          LOG.error("Vehicle[id:{}] licenceNo is empty!", v.getId());
      }
    }
    }
   return licenceNos;
  }

  @Override
  public Map<Long, VehicleDTO> getVehicleByVehicleIdSet(Long shopId, Set<Long> vehicleIds) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getVehicleByVehicleIdSet(shopId, vehicleIds);
  }

  @Override
  public List<Long> getVehicleIds(Long shopId, int start, int rows) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getVehicleIds(shopId,start,rows);
  }

  @Override
  public VehicleDTO updateVehicle(VehicleDTO vehicleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status=writer.begin();
    try{
      Vehicle vehicle= writer.getVehicleById(vehicleDTO.getShopId(),vehicleDTO.getId());
      if(vehicle==null) return null;
      vehicle.fromDTO(vehicleDTO);
      writer.update(vehicle);
      writer.commit(status);
      return vehicle.toDTO();
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<CustomerVehicleDTO> getMatchMaintainMileageCustomerVehicle(int pageSize, Double[] intervals, Long lastCustomerVehicleId) {
    UserWriter writer = userDaoManager.getWriter();
    List<CustomerVehicle> customerVehicles = writer.getMatchMaintainMileageCustomerVehicle(pageSize,intervals,lastCustomerVehicleId);
    List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
    if (CollectionUtil.isNotEmpty(customerVehicles)) {
      for (CustomerVehicle customerVehicle : customerVehicles) {
        customerVehicleDTOs.add(customerVehicle.toDTO());
      }
    }
    return customerVehicleDTOs;
  }

  @Override
  public List<CustomerVehicleDTO> getMatchCustomerVehicleByVehicleOBDMileage(int pageSize, Double[] intervals, Long lastCustomerVehicleId) {
    UserWriter writer = userDaoManager.getWriter();
      List<CustomerVehicle> customerVehicles = writer.getMatchCustomerVehicleByVehicleOBDMileage(pageSize,intervals,lastCustomerVehicleId);
      List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
      if (CollectionUtil.isNotEmpty(customerVehicles)) {
        for (CustomerVehicle customerVehicle : customerVehicles) {
          customerVehicleDTOs.add(customerVehicle.toDTO());
        }
      }
      return customerVehicleDTOs;
  }

  @Override
  public Map<Long,CustomerVehicleDTO> getCustomerVehicleDTOMapByVehicleIds(Long... vehicleIds){
    Map<Long,CustomerVehicleDTO> customerVehicleDTOMap = new HashMap<Long, CustomerVehicleDTO>();
    if(!ArrayUtils.isEmpty(vehicleIds)){
      UserWriter writer = userDaoManager.getWriter();
      List<CustomerVehicle> customerVehicles = writer.getCustomerByVehicleIds(vehicleIds);
      if(CollectionUtils.isNotEmpty(customerVehicles)){
        for(CustomerVehicle customerVehicle :customerVehicles ){
           if(customerVehicle != null && customerVehicle.getVehicleId() != null){
             customerVehicleDTOMap.put(customerVehicle.getVehicleId(),customerVehicle.toDTO());
           }
        }
      }
    }
    return customerVehicleDTOMap;
  }

  @Override
  public VehicleDTO getVehicleDTOByLicenceNo(Long shopId, String licenceNo) {
    VehicleDTO vehicle = null;
    UserWriter writer = userDaoManager.getWriter();
    List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopId, licenceNo);
    if (CollectionUtils.isNotEmpty(vehicleList)) {
      vehicle = vehicleList.get(0).toDTO();
    }
    return vehicle;
  }

  @Override
  public List<VehicleDTO> getVehicleDTOByLicenceNo(String... licenceNo) {
    if(ArrayUtils.isEmpty(licenceNo)) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(licenceNo);
    List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
    if(CollectionUtils.isNotEmpty(vehicleList)){
      for(Vehicle vehicle:vehicleList){
        vehicleDTOList.add(vehicle.toDTO());
      }
      return vehicleDTOList;
    }
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public Map<String, VehicleDTO> getVehicleDTOMapByLicenceNo(Long shopId, Set<String> LicenceNoSet) {

    Map<String, VehicleDTO> result = new HashMap<String, VehicleDTO>();
    if(shopId == null || CollectionUtils.isEmpty(LicenceNoSet)){
      return result;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopId, LicenceNoSet);
    if (CollectionUtils.isNotEmpty(vehicleList)) {
      for (Vehicle vehicle : vehicleList) {
        if (StringUtils.isNotEmpty(vehicle.getLicenceNo())) {
          result.put(vehicle.getLicenceNo(), vehicle.toDTO());
        }
      }
    }
    return result;
  }

  public VehicleDTO updateVehicleMobile(Long shopId, String licenceNo, String mobile) {
    UserWriter writer = userDaoManager.getWriter();
    VehicleDTO vehicleDTO = null;
    List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(shopId, licenceNo);
    if (CollectionUtils.isNotEmpty(vehicleList)) {
      Vehicle vehicle = vehicleList.get(0);
      vehicle.setMobile(mobile);
      vehicleDTO = vehicle.toDTO();
      Object status = writer.begin();
      try {
        writer.update(vehicle);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return vehicleDTO;
  }

  @Override
  public List<VehicleDTO> getVehiclesByCustomerMobile(String mobile, String vehicleNo) {
//    if (StringUtil.isNotEmpty(mobile)) {
//      UserReader reader = userDaoManager.getReader();
//      vehicleList = reader.getVehiclesByCustomerMobile(mobile, vehicleNo);
//    } else {
//      vehicleList = userDaoManager.getWriter()
//          .getVehicleByLicenceNo(vehicleNo);
//    }
//    Map<Long, List<VehicleDTO>> map = new HashMap<Long, List<VehicleDTO>>();
//    for (Vehicle v : vehicleList) {
//      List<VehicleDTO> list = map.get(v.getShopId());
//      if (CollectionUtil.isEmpty(list)) {
//        list = new ArrayList<VehicleDTO>();
//        map.put(v.getShopId(), list);
//      }
//      list.add(v.toDTO());
//    }
//    return map;
    List<Vehicle> vehicleList = userDaoManager.getReader().getVehiclesByCustomerMobile(mobile, vehicleNo);
    List<VehicleDTO> list = new ArrayList<VehicleDTO>();
    for (Vehicle v : vehicleList) {
      list.add(v.toDTO());
    }
    return list;
  }


  @Override
  public List<VehicleOBDMileageDTO> getVehicleOBDMileageByStartVehicleId(Long startVehicleId, int pageSize) {
    List<VehicleOBDMileageDTO> vehicleOBDMileageDTOs = new ArrayList<VehicleOBDMileageDTO>();
    if (startVehicleId == null || pageSize <= 0) {
      return vehicleOBDMileageDTOs;
    }
    List<Object[]> objectsList = userDaoManager.getReader().getVehicleOBDMileageByStartVehicleId(startVehicleId, pageSize);
    if (CollectionUtils.isNotEmpty(objectsList)) {
      for (Object[] objects : objectsList) {
        if(!ArrayUtils.isEmpty(objects)){
          //v.id vehicleId ,av.current_mileage_last_update_time updateTime,av.current_mileage currentMileage
          VehicleOBDMileageDTO vehicleOBDMileageDTO = new VehicleOBDMileageDTO();
          vehicleOBDMileageDTO.setVehicleId((Long)objects[0]);
          vehicleOBDMileageDTO.setMileageLastUpdateTime((Long) objects[1]);
          vehicleOBDMileageDTO.setObdMileage((Double)objects[2]);
          vehicleOBDMileageDTO.setShopId((Long)objects[3]);
          vehicleOBDMileageDTOs.add(vehicleOBDMileageDTO);
        }
      }
    }
    return vehicleOBDMileageDTOs;
  }

  @Override
  public void updateVehicleOBDMileage(VehicleOBDMileageDTO vehicleOBDMileageDTO) {
    if (vehicleOBDMileageDTO != null && vehicleOBDMileageDTO.getVehicleId() != null) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        Vehicle vehicle = writer.getById(Vehicle.class, vehicleOBDMileageDTO.getVehicleId());
        if (vehicle != null) {
          if (NumberUtil.longValue(vehicleOBDMileageDTO.getMileageLastUpdateTime()) > NumberUtil.longValue(vehicle.getMileageLastUpdateTime())
              && NumberUtil.doubleVal(vehicleOBDMileageDTO.getObdMileage()) > 0) {
            vehicle.setObdMileage(vehicleOBDMileageDTO.getObdMileage());
            vehicle.setMileageLastUpdateTime(vehicleOBDMileageDTO.getMileageLastUpdateTime());
            writer.update(vehicle);
            writer.commit(status);
          }
        }
      } finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public Map<Long, ObdUserVehicleDTO> getObdUserVehicles(Long... vehicleId) {
    Map<Long, ObdUserVehicleDTO> obdUserVehicleDTOMap = new HashMap<Long, ObdUserVehicleDTO>();
    if(ArrayUtils.isEmpty(vehicleId)) return obdUserVehicleDTOMap;
    UserWriter writer = userDaoManager.getWriter();
    List<ObdUserVehicle> obdUserVehicleList = writer.getObdUserVehicles(vehicleId);
    if(CollectionUtils.isNotEmpty(obdUserVehicleList)){
      for(ObdUserVehicle obdUserVehicle:obdUserVehicleList){
        obdUserVehicleDTOMap.put(obdUserVehicle.getAppVehicleId(),obdUserVehicle.toDTO());
      }
    }
    return obdUserVehicleDTOMap;
  }

  @Override
  public Map<String, List<ObdUserVehicleDTO>> getObdUserVehiclesByAppUserNos(Set<String> appUserNos) {
    Map<String, List<ObdUserVehicleDTO>> obdUserVehicleDTOMap = new HashMap<String, List<ObdUserVehicleDTO>>();
    if (CollectionUtils.isEmpty(appUserNos)) {
      return obdUserVehicleDTOMap;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<ObdUserVehicle> obdUserVehicleList = writer.getBundlingObdUserVehicleByUserNos(appUserNos);
    if (CollectionUtils.isNotEmpty(obdUserVehicleList)) {
      for (ObdUserVehicle obdUserVehicle : obdUserVehicleList) {
        if (obdUserVehicle != null && StringUtils.isNotBlank(obdUserVehicle.getAppUserNo())) {
          List<ObdUserVehicleDTO> obdUserVehicleDTOs = obdUserVehicleDTOMap.get(obdUserVehicle.getAppUserNo());
          if (obdUserVehicleDTOs == null) {
            obdUserVehicleDTOs = new ArrayList<ObdUserVehicleDTO>();
            obdUserVehicleDTOMap.put(obdUserVehicle.getAppUserNo(), obdUserVehicleDTOs);
          }
          obdUserVehicleDTOs.add(obdUserVehicle.toDTO());
        }
      }
    }
    return obdUserVehicleDTOMap;
  }

  public VehicleDTO getVehicleDTOById(Long id) {
    VehicleDTO vehicleDTO = null;
    UserWriter writer = userDaoManager.getWriter();
    Vehicle vehicle = writer.findById(Vehicle.class, id);
    if (vehicle != null) {
      vehicleDTO = vehicle.toDTO();
    }
    return vehicleDTO;
  }

  @Override
  public Map<Long, Boolean> isAppVehicle(Long... vehicleIds) {
    Map<Long, Boolean> isAppVehicleMap = new HashMap<Long, Boolean>();
    if(ArrayUtils.isEmpty(vehicleIds)){
      return isAppVehicleMap;
    }
    UserWriter writer = userDaoManager.getWriter();
    return writer.checkIsAppVehicle(vehicleIds);
  }

  @Override
  public VehicleDTO getVehicleDTOByIMei(String iMei) {
    if (StringUtils.isEmpty(iMei)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Vehicle> vehicles = writer.getVehicleByGsmObdImei(iMei);
    if (CollectionUtils.isNotEmpty(vehicles)) {
      if (vehicles.size() > 1) {
        LOG.error("一个IMei:{}号找到多个vehicle", iMei);
      }
      return CollectionUtil.getFirst(vehicles).toDTO();
    }
    return null;
  }

  @Override
  public void updateVehicleMilByGsmVehicleInfo(VehicleDTO vehicleDTO, List<GsmVehicleInfo> gsmVehicleInfoList) {
    if (vehicleDTO != null && vehicleDTO.getId() != null && CollectionUtils.isNotEmpty(gsmVehicleInfoList)) {
      LOG.warn("updateVehicleMilByGsmVehicleInfo：【{}】",vehicleDTO.getId());
      long lastUpdateVehicleMilTime = NumberUtil.longValue(vehicleDTO.getMileageLastUpdateTime());
      BigDecimal obdMileage = new BigDecimal(vehicleDTO.getObdMileage() == null ? "0" : String.valueOf(vehicleDTO.getObdMileage()));
      BigDecimal lastObdMileage = new BigDecimal(vehicleDTO.getLastObdMileage() == null ? "0" : String.valueOf(vehicleDTO.getLastObdMileage()));
      BigDecimal gsmObdMileage = null;
      boolean isNeedToUpdate = false;
      for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfoList) {
        if (gsmVehicleInfo != null
            && NumberUtil.isNumber(gsmVehicleInfo.getAdMil())
            && gsmVehicleInfo.getUploadTime() != null) {
          gsmObdMileage = new BigDecimal(StringUtil.isEmpty(gsmVehicleInfo.getAdMil())?"0":gsmVehicleInfo.getAdMil());   //gsm总里程
          //lastObdMileage 大于 gsmObdMileage 拔掉了重新插 (并且新的obd adMil 小于1，lastOBDMil -新的obd adMail 大于0.2）
          if (gsmVehicleInfo.getUploadTime() > lastUpdateVehicleMilTime
              && gsmObdMileage.doubleValue() > 0.01) {
            if (lastObdMileage.compareTo(gsmObdMileage) == 1) {
              if (gsmObdMileage.doubleValue() < 1 && lastObdMileage.subtract(gsmObdMileage).doubleValue() > 0.2) {
                obdMileage = obdMileage.add(gsmObdMileage);
                lastUpdateVehicleMilTime = gsmVehicleInfo.getUploadTime();
                lastObdMileage = gsmObdMileage;
                isNeedToUpdate = true;
              }
            } else if (lastObdMileage.compareTo(gsmObdMileage) == -1) {
              obdMileage = obdMileage.add(gsmObdMileage.subtract(lastObdMileage));
              lastUpdateVehicleMilTime = gsmVehicleInfo.getUploadTime();
              lastObdMileage = gsmObdMileage;
              isNeedToUpdate = true;
            }
          }
        }
      }

      if (isNeedToUpdate) {
        vehicleDTO.setLastObdMileage(lastObdMileage.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        vehicleDTO.setObdMileage(obdMileage.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());
        vehicleDTO.setMileageLastUpdateTime(lastUpdateVehicleMilTime);

        UserWriter writer = userDaoManager.getWriter();
        Object status = writer.begin();
        try {
          Vehicle vehicle = writer.getById(Vehicle.class, vehicleDTO.getId());
          if (vehicle != null) {
            vehicle.setObdMileage(NumberUtil.round(vehicleDTO.getObdMileage(), 2));
            vehicle.setMileageLastUpdateTime(vehicleDTO.getMileageLastUpdateTime());
            vehicle.setLastObdMileage(vehicleDTO.getLastObdMileage());
            writer.update(vehicle);
            writer.commit(status);
          }
        } finally {
          writer.commit(status);
        }
        if (vehicleDTO.getShopId() != null && vehicleDTO.getId() != null) {
          try {
            ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(vehicleDTO.getShopId(), vehicleDTO.getId());
          } catch (Exception e) {
            LOG.error("更新完车辆里程做索引失败" + e.getMessage(), e);
          }
        }
      }
    }
  }

  @Override
  public VehicleDTO getVehicleByGsmObdImei(Long vehicleId, String gsmObdImei) {
    UserWriter writer = userDaoManager.getWriter();
   Vehicle vehicle= CollectionUtil.getFirst(writer.getVehicleByGsmObdImei(vehicleId, gsmObdImei));
    return vehicle==null?null:vehicle.toDTO();
  }

  @Override
  public Map<String, VehicleDTO> getVehicleDTOMapByIMeis(Set<String> imeis) {
    Map<String, VehicleDTO> vehicleDTOMap = new HashMap<String, VehicleDTO>();
    if (CollectionUtils.isNotEmpty(imeis)) {
      UserWriter writer = userDaoManager.getWriter();
      List<Vehicle> vehicles = writer.getVehiclesByGsmObdImeis(imeis);
      if (CollectionUtils.isNotEmpty(vehicles)) {
        for (Vehicle vehicle : vehicles) {
          if (vehicle != null && StringUtils.isNotBlank(vehicle.getGsmObdImei())) {
            VehicleDTO vehicleDTO = vehicleDTOMap.get(vehicle.getGsmObdImei());
            if (vehicleDTO == null) {
              vehicleDTOMap.put(vehicle.getGsmObdImei(), vehicle.toDTO());
            }
          }
        }
      }
    }

    return vehicleDTOMap;
  }

  @Override
  public List<String> getVehicleImeiByShopId(Long shopId) {
    UserReader userReader = userDaoManager.getReader();
    return userReader.getVehicleImeiByShopId(shopId);
  }


  public Result maintainRegister(CustomerVehicleResponse customerVehicleResponse) throws Exception {

    Result result = new Result(false);
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerVehicleDTO customerVehicleDTO = userService.getCustomerVehicleDTOByVehicleIdAndCustomerId(customerVehicleResponse.getVehicleId(), customerVehicleResponse.getCustomerId());
    if (customerVehicleDTO == null) {
      return result;
    }

    VehicleDTO vehicleDTO = this.findVehicleById(customerVehicleResponse.getVehicleId());
    if (vehicleDTO == null) {
      return result;
    }

    customerVehicleDTO.setLastMaintainMileage(customerVehicleResponse.getLastMaintainMileage());
    if (StringUtil.isNotEmpty(customerVehicleResponse.getLastMaintainTimeStr())) {
      customerVehicleDTO.setLastMaintainTimeStr(customerVehicleResponse.getLastMaintainTimeStr());
      customerVehicleDTO.setLastMaintainTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, customerVehicleResponse.getLastMaintainTimeStr()));
    } else {
      customerVehicleDTO.setLastMaintainTime(null);
    }
    customerVehicleDTO.setMaintainMileagePeriod(customerVehicleResponse.getMaintainMileagePeriod());

    customerVehicleDTO.setMaintainTimePeriod(customerVehicleResponse.getMaintainTimePeriodStr());

    if (customerVehicleResponse.getLastMaintainMileage() != null && customerVehicleResponse.getMaintainMileagePeriod() != null) {
      customerVehicleDTO.setMaintainMileage((long)(customerVehicleResponse.getLastMaintainMileage() + customerVehicleResponse.getMaintainMileagePeriod()));
      customerVehicleResponse.setMaintainMileage(customerVehicleDTO.getMaintainMileage());
    }

    if (customerVehicleDTO.getLastMaintainTime() != null && customerVehicleResponse.getMaintainTimePeriodStr() != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(customerVehicleDTO.getLastMaintainTime());
      calendar.add(Calendar.MONTH,customerVehicleResponse.getMaintainTimePeriodStr().intValue());
      customerVehicleDTO.setMaintainTime(calendar.getTimeInMillis());
      customerVehicleResponse.setMaintainTime(customerVehicleDTO.getMaintainTime());
      customerVehicleResponse.setMaintainTimeStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, customerVehicleDTO.getMaintainTime()));
    }

    if (vehicleDTO.getObdMileage() != null && customerVehicleDTO.getMaintainMileage() != null) {
      customerVehicleDTO.setNextMaintainMileageAccess(customerVehicleDTO.getMaintainMileage() - vehicleDTO.getObdMileage());
      customerVehicleResponse.setNextMaintainMileageAccess(customerVehicleDTO.getNextMaintainMileageAccess());
    } else {
      customerVehicleDTO.setNextMaintainMileageAccess(null);
      customerVehicleResponse.setNextMaintainMileageAccess(null);
    }

    List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
    customerVehicleDTOs.add(customerVehicleDTO);

    userService.saveOrUpdateCustomerVehicle(customerVehicleDTOs);
    result.setData(customerVehicleResponse);
    result.setSuccess(true);
    return result;
  }

  public List<VehicleDTO> getVehicleByCondition(VehicleSearchConditionDTO conditionDTO) {
    UserReader reader = userDaoManager.getReader();
    List<Vehicle> vehicleList = reader.getVehicleByCondition(conditionDTO);

    List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
    if (CollectionUtil.isEmpty(vehicleList)) {
      return vehicleDTOList;
    }
    for (Vehicle vehicle : vehicleList) {
      vehicleDTOList.add(vehicle.toDTO());
    }


    return vehicleDTOList;
  }

}
