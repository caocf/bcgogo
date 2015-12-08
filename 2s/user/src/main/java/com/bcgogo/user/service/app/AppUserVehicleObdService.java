package com.bcgogo.user.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.response.AppGsmVehicleResponse;
import com.bcgogo.api.response.ObtainVehicleListResponse;
import com.bcgogo.api.response.VehicleResponse;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.juhe.JuheCityOilPriceDTO;
import com.bcgogo.config.service.App.IAppUserConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.app.*;
import com.bcgogo.enums.user.Status;
import com.bcgogo.etl.model.GsmPoint;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.etl.service.IGsmPointService;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.app.*;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 13-8-22
 * Time: 上午10:42
 */
@Component
public class AppUserVehicleObdService implements IAppUserVehicleObdService {
  private static final Logger LOG = LoggerFactory.getLogger(AppUserVehicleObdService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  private String bindingValidate(OBDBindingDTO obdBindingDTO) {
    String vResult = obdBindingDTO.validate();
    if (obdBindingDTO.isSuccess(vResult)) {
      //判断用户是否存在
      AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class)
        .getAppUserByUserNo(obdBindingDTO.getUserNo(), null);
      if (appUserDTO == null) {
        return ValidateMsg.APP_USER_NOT_EXIST.getValue();
      }
      AppVehicleDTO appVehicleDTO = getAppVehicleByUserNoVehicleNo(obdBindingDTO.getUserNo(), obdBindingDTO.getVehicleNo());
      if (appVehicleDTO != null && !appVehicleDTO.getVehicleId().equals(obdBindingDTO.getVehicleId())) {
        return ValidateMsg.APP_VEHICLE_NO_EXIST.getValue();
      }
      UserWriter writer = userDaoManager.getWriter();
      if (StringUtil.isNotEmpty(obdBindingDTO.getVehicleVin())) {
        AppVehicle appVehicle = writer.getAppVehicleByUserNoVehicleVin(obdBindingDTO.getUserNo(), obdBindingDTO.getVehicleVin());
        //新增车辆 判断 vin 与 vehicleId
        if (appVehicle != null && !appVehicle.getId().equals(obdBindingDTO.getVehicleId())) {
          return ValidateMsg.APP_VEHICLE_VIN_EXIST.getValue();
        }
      }
    }
    return vResult;
  }

  @Override
  public Pair<ApiResponse, Boolean> bindingObd(OBDBindingDTO obdBindingDTO) throws Exception {
    String vResult = bindingValidate(obdBindingDTO);
    boolean isToCreateMatchTask = false;
    if (!obdBindingDTO.isSuccess(vResult)) {
      return new Pair<ApiResponse, Boolean>(MessageCode.toApiResponse(MessageCode.OBD_BINDING_FAIL, vResult), isToCreateMatchTask);
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //判断obd id
      ObdDTO obdDTO = updateObdInfo(obdBindingDTO, writer);
      //obtain or create vehicle  appVehicleDTO cat be null
      Pair<AppVehicleDTO, Boolean> appVehiclePair = createAppVehicle(obdBindingDTO, writer);
      AppVehicleDTO appVehicleDTO = appVehiclePair.getKey();
      isToCreateMatchTask = appVehiclePair.getValue() == null ? false : appVehiclePair.getValue();
      //绑定 obdId userId vehicleId
      binding(writer, obdBindingDTO, obdDTO.getId(), (appVehicleDTO != null ? appVehicleDTO.getVehicleId() : null));
      //binding shop
      shopBinding(obdBindingDTO, writer, obdDTO, appVehicleDTO);
      writer.commit(status);
      return new Pair<ApiResponse, Boolean>(MessageCode.toApiResponse(MessageCode.OBD_BINDING_SUCCESS), isToCreateMatchTask);
    } finally {
      writer.rollback(status);
    }
  }

  private void shopBinding(OBDBindingDTO obdBindingDTO, UserWriter writer, ObdDTO obdDTO, AppVehicleDTO appVehicleDTO) {
    if (obdBindingDTO.getSellShopId() != null) {
      ServiceManager.getService(IAppUserShopBindingService.class)
        .binding(new ShopBindingDTO(obdBindingDTO, obdDTO, appVehicleDTO), writer);
      try {
        if (obdBindingDTO.getSellShopId() == null) return;
        //为店铺新增客户
        ServiceManager.getService(ICustomerService.class)
          .createOrMatchingCustomerByAppUserNo(obdBindingDTO.getUserNo(), obdBindingDTO.getSellShopId(), writer);
      } catch (BcgogoException e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }

  /**
   * 返回值中的boolean 表示，是否需要创建task
   */
  private Pair<AppVehicleDTO, Boolean> createAppVehicle(OBDBindingDTO obdBindingDTO, UserWriter writer) {
    AppVehicleDTO appVehicleDTO = obdBindingDTO.toAppVehicleDTO();
    //create base vehicle
    createBaseVehicle(appVehicleDTO, writer);
    //根据 vehicleVin 判断是新增还是修改
    AppVehicle vehicle = null;
    if (obdBindingDTO.getVehicleId() == null) {
      List<AppVehicle> appVehicleList = writer.getAppVehicleByUserNoVehicleNo(appVehicleDTO.getUserNo(), appVehicleDTO.getVehicleNo());
      if (CollectionUtil.isNotEmpty(appVehicleList)) vehicle = appVehicleList.get(0);
    } else {
      vehicle = writer.getById(AppVehicle.class, obdBindingDTO.getVehicleId());
    }
    boolean flag;
    if (vehicle == null) {
      AppVehicle appVehicle = new AppVehicle(appVehicleDTO);
      setDefaultAppVehicle(writer, appVehicleDTO, appVehicle);
      writer.save(appVehicle);
      flag = true;
      appVehicleDTO.setVehicleId(appVehicle.getId());
    } else {
      flag = !vehicle.getVehicleNo().equals(obdBindingDTO.getVehicleNo());
      vehicle.fromDTO(obdBindingDTO);
      vehicle.setStatus(Status.active);
      setDefaultAppVehicle(writer, appVehicleDTO, vehicle);
      writer.update(vehicle);
      appVehicleDTO = vehicle.toDTO();
    }
    return new Pair<AppVehicleDTO, Boolean>(appVehicleDTO, flag);
  }

  private void setDefaultAppVehicle(UserWriter writer, AppVehicleDTO appVehicleDTO, AppVehicle appVehicle) {
    List<AppVehicle> appVehicles = writer.getAppVehicleByAppUserNo(appVehicleDTO.getUserNo());
    if (CollectionUtil.isEmpty(appVehicles)) {
      appVehicle.setIsDefault(YesNo.YES);
    } else {
      for (AppVehicle entity : appVehicles) {
        entity.setIsDefault(YesNo.NO);
        writer.update(entity);
//        if (entity.getIsDefault() == YesNo.YES)
//          return;
      }
      appVehicle.setIsDefault(YesNo.YES);
    }
  }

  //create base vehicle
  @Override
  public void createBaseVehicle(AppVehicleDTO appVehicleDTO, UserWriter writer) {
    if (StringUtil.isNotEmpty(appVehicleDTO.getVehicleVin())) {
      VehicleBasicInfo vehicleBasicInfo = writer.getVehicleBasicInfoByVin(appVehicleDTO.getVehicleVin());
      if (vehicleBasicInfo == null) {
        vehicleBasicInfo = new VehicleBasicInfo(appVehicleDTO);
        writer.save(vehicleBasicInfo);
      }
    }
  }

  private void binding(UserWriter writer, OBDBindingDTO obdBindingDTO, Long obdId, Long vehicleId) throws Exception {
    if (obdId == null) throw new Exception("obd id is null");
    Set<String> updatedVehicleNoSet = new HashSet<String>();
    boolean newBinding = true;
    //获得绑定
//    ObdUserVehicle obdUserVehicle = writer.getBundlingObdUserVehicleByObdId(obdId);
    String userNo = obdBindingDTO.getUserNo();
    List<ObdUserVehicle> bundlingObdUserVehicleList = writer.getBundlingObdUserVehicleByUserNo(userNo);
    for (ObdUserVehicle entity : bundlingObdUserVehicleList) {
      if (entity.isSameObdUserVehicle(userNo, vehicleId, obdId)) {
        newBinding = false;
        break;
      } else if (entity.isObdUserVehicleChanged(userNo, vehicleId, obdId)) {
        entity.setStatus(ObdUserVehicleStatus.UN_BUNDLING);
        writer.update(entity);
        AppVehicle appVehicle = writer.getById(AppVehicle.class, entity.getAppVehicleId());
        List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(appVehicle.getVehicleNo());
        if (CollectionUtils.isNotEmpty(vehicleList)) {
          for (Vehicle vehicle : vehicleList) {
            vehicle.setObdId(null);
            writer.update(vehicle);
            updatedVehicleNoSet.add(vehicle.getLicenceNo());
          }
        }
        break;
      }
    }
    if (newBinding) {
      List<ObdUserVehicle> obdUserVehicleList = writer.getUnBundlingObdUserVehicle(userNo, vehicleId, obdId);
      if (CollectionUtil.isNotEmpty(obdUserVehicleList)) {
        ObdUserVehicle obdUserVehicle = obdUserVehicleList.get(0);
        obdUserVehicle.setStatus(ObdUserVehicleStatus.BUNDLING);
        writer.update(obdUserVehicle);
      } else {
        ObdUserVehicle obdUserVehicle = new ObdUserVehicle(userNo, vehicleId, obdId);
        writer.save(obdUserVehicle);
      }
      AppVehicle appVehicle = writer.getById(AppVehicle.class, vehicleId);
      List<Vehicle> vehicleList = writer.getVehicleByLicenceNo(appVehicle.getVehicleNo());
      if (CollectionUtils.isNotEmpty(vehicleList)) {
        for (Vehicle vehicle : vehicleList) {
          vehicle.setObdId(obdId);
          writer.update(vehicle);
          updatedVehicleNoSet.add(vehicle.getLicenceNo());
        }
      }
    }
    obdBindingDTO.setUpdatedVehicleNoSet(updatedVehicleNoSet);
  }

  private ObdDTO updateObdInfo(OBDBindingDTO obdBindingDTO, UserWriter writer) {
    OBD obd = writer.getObdBySn(obdBindingDTO.getObdSN());
    if (obd == null) {
      ObdDTO obdDTO = new ObdDTO(obdBindingDTO);
      obd = new OBD(obdDTO);
      writer.save(obd);
      obdDTO.setId(obd.getId());
    } else {
      if (obdBindingDTO.getSellShopId() != null) {
        //销售obd店铺
        obd.setSellShopId(obdBindingDTO.getSellShopId());
        writer.update(obd);
      }
    }
    return obd.toDTO();
  }

  @Override
  public ObdDTO getObdBySn(String sn) {
    UserWriter writer = userDaoManager.getWriter();
    OBD obd = writer.getObdBySn(sn);
    if (obd != null) return obd.toDTO();
    return null;
  }

//  @Override
//  public ObdUserVehicleDTO getBundlingObdUserVehicleBySN(String sn) {
//    UserWriter writer = userDaoManager.getWriter();
//    OBD obd = writer.getObdBySn(sn);
//    if (obd == null) return null;
//    ObdUserVehicle obdUserVehicle = writer.getBundlingObdUserVehicleByObdId(obd.getId());
//    if (obdUserVehicle != null) return obdUserVehicle.toDTO();
//    return null;
//  }

  /**
   * 通过appUser和 vin才能确定一辆车 而且都不能为空
   *
   * @param appUserNo
   * @param vin
   * @return
   */
  @Override
  public AppVehicleDTO getBindingAppVehicleByVinUserNo(String appUserNo, String vin) {
    if (StringUtil.isEmpty(vin) || StringUtil.isEmpty(appUserNo)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<AppVehicle> vehicleList = writer.getBindingAppVehicleByVinUserNo(vin, appUserNo);
    if (CollectionUtil.isEmpty(vehicleList)) {
      return null;
    }
    if (vehicleList.size() > 1) {
      LOG.error("appVehicle more than one:" + vin + ",userNo:" + appUserNo);
    }
    return vehicleList.get(0).toDTO();
  }

  private String addOrUpdateAppVehicleValidate(AppVehicleDTO appVehicleDTO, UserWriter writer) {
    String result = appVehicleDTO.saveVehicleValidate();
    if (appVehicleDTO.isFail(result)) {
      return result;
    }
    AppVehicleDTO tmp = getAppVehicleByUserNoVehicleNo(appVehicleDTO.getUserNo(), appVehicleDTO.getVehicleNo());
    if (tmp != null && !tmp.getVehicleId().equals(appVehicleDTO.getVehicleId())) {
      return ValidateMsg.APP_VEHICLE_NO_EXIST.getValue();
    }
    if (StringUtil.isNotEmpty(appVehicleDTO.getVehicleVin())) {
      AppVehicle tmpAppVehicle = writer.getAppVehicleByUserNoVehicleVin(appVehicleDTO.getUserNo(), appVehicleDTO.getVehicleVin());
      //新增车辆 判断 vin 与 vehicleId
      if (tmpAppVehicle != null && !tmpAppVehicle.getId().equals(appVehicleDTO.getVehicleId())) {
        return ValidateMsg.APP_VEHICLE_VIN_EXIST.getValue();
      }
    }
    return "";
  }


  @Override
  public Pair<ApiResponse, Boolean> addOrUpdateAppVehicle(AppVehicleDTO appVehicleDTO) {
    UserWriter writer = userDaoManager.getWriter();
    String result = addOrUpdateAppVehicleValidate(appVehicleDTO, writer);
    if (appVehicleDTO.isFail(result)) {
      return new Pair<ApiResponse, Boolean>(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_FAIL, result), false);
    }
    boolean flag = false; //是否要生成match task
    Object status = writer.begin();
    try {
      //create base vehicle
      createBaseVehicle(appVehicleDTO, writer);
      //create or update personal vehicle
      if (appVehicleDTO.getVehicleId() != null) {
        AppVehicle appVehicle = writer.getById(AppVehicle.class, appVehicleDTO.getVehicleId());
        if (appVehicle != null) {
          appVehicle.updateAppVehicleFromApp(appVehicleDTO);
          flag = !appVehicle.getVehicleNo().equals(appVehicleDTO.getVehicleNo());
          writer.update(appVehicle);
        }
      } else {
        flag = true;
        AppVehicle appVehicle = new AppVehicle(appVehicleDTO);
        writer.save(appVehicle);
        appVehicleDTO.setVehicleId(appVehicle.getId());
      }
      shopBinding(appVehicleDTO, writer);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    updateDefaultAppVehicle(appVehicleDTO.getVehicleId(), appVehicleDTO.getUserNo(), false);
    VehicleResponse response = new VehicleResponse(MessageCode.toApiResponse(MessageCode.SAVE_APP_VEHICLE_SUCCESS));
    response.setVehicleInfo(appVehicleDTO);
    return new Pair<ApiResponse, Boolean>(response, flag);
  }

  @Override
  public Pair<Boolean, Map<String, String>> addVehiclesForGuest(List<AppVehicleDTO> appVehicleDTOs) {
    Map<String, String> responses = new HashMap<String, String>();
    Pair<Boolean, Map<String, String>> pair = new Pair<Boolean, Map<String, String>>();
    pair.setValue(responses);
    UserWriter writer = userDaoManager.getWriter();
    boolean isSuccess = true;
    Object status = writer.begin();
    try {
      for (AppVehicleDTO appVehicleDTO : appVehicleDTOs) {
        String result = addOrUpdateAppVehicleValidate(appVehicleDTO, writer);
        if (appVehicleDTO.isFail(result)) {
          isSuccess = false;
          responses.put(appVehicleDTO.getVehicleNo(), result);
          continue;
        }
        //create base vehicle
        createBaseVehicle(appVehicleDTO, writer);
        //create or update personal vehicle
        if (appVehicleDTO.getVehicleId() != null) {
          AppVehicle appVehicle = writer.getById(AppVehicle.class, appVehicleDTO.getVehicleId());
          if (appVehicle != null) {
            appVehicle.updateAppVehicleFromApp(appVehicleDTO);
            writer.update(appVehicle);
          }
        } else {
          AppVehicle appVehicle = new AppVehicle(appVehicleDTO);
          writer.save(appVehicle);
          appVehicleDTO.setVehicleId(appVehicle.getId());
        }
      }
      pair.setKey(isSuccess);
      if (!isSuccess) return pair;
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    updateDefaultAppVehicle(appVehicleDTOs.get(0).getVehicleId(), appVehicleDTOs.get(0).getUserNo(), true);
    return pair;
  }

  private void shopBinding(AppVehicleDTO appVehicleDTO, UserWriter writer) {
    //add or update
    if (appVehicleDTO.getBindingShopId() != null)
      ServiceManager.getService(IAppUserShopBindingService.class)
        .binding(new ShopBindingDTO(appVehicleDTO), writer);
    //delete
    if (appVehicleDTO.getBindingShopId() == null && appVehicleDTO.getOrgBindingShopId() != null) {
      ServiceManager.getService(IAppUserShopBindingService.class)
        .unbinding(appVehicleDTO.getUserNo(), appVehicleDTO.getOrgBindingShopId(), appVehicleDTO.getVehicleId(), writer);
    }
  }


  @Override
  public void updateDefaultAppVehicle(Long appVehicleId, String appUserNo, boolean isSetDefault) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<AppVehicle> appVehicles = writer.getAppVehicleByAppUserNo(appUserNo);
      if (appVehicleId != null) {
        if (CollectionUtil.isNotEmpty(appVehicles) && appVehicles.size() == 1) {
          AppVehicle appVehicle = appVehicles.get(0);
          appVehicle.setIsDefault(YesNo.YES);
          writer.update(appVehicle);
          writer.commit(status);
        } else if (isSetDefault) {
          for (AppVehicle appVehicle : appVehicles) {
            if (!appVehicle.getId().equals(appVehicleId)) {
              appVehicle.setIsDefault(YesNo.NO);
            } else {
              appVehicle.setIsDefault(YesNo.YES);
            }
            writer.update(appVehicle);
          }
          writer.commit(status);
        }
      }
    } finally {
      writer.rollback(status);
    }

  }

  /**
   * 根据用户名和车牌号查找车辆
   *
   * @param appUserNo
   * @param vehicleNo
   * @return
   */
  @Override
  public AppVehicleDTO getAppVehicleByUserNoVehicleNo(String appUserNo, String vehicleNo) {
    UserWriter writer = userDaoManager.getWriter();
    List<AppVehicle> vehicleList = writer.getAppVehicleByUserNoVehicleNo(appUserNo, vehicleNo);

    if (CollectionUtils.isEmpty(vehicleList)) {
      return null;
    }

    if (vehicleList.size() > 1) {
      LOG.error("appVehicle 有多个:appUserNo:" + appUserNo + ",vehicleNo:" + vehicleNo);
    }
    return vehicleList.get(0).toDTO();
  }


  /**
   * 修改保养信息
   *
   * @param appVehicleMaintainDTO
   */
  public Result updateVehicleMaintain(AppVehicleMaintainDTO appVehicleMaintainDTO) {
    Result result = new Result();
    result.setSuccess(false);
    UserWriter writer = userDaoManager.getWriter();

    AppVehicle appVehicle = writer.getById(AppVehicle.class, appVehicleMaintainDTO.getVehicleId());
    if (appVehicle == null) {
      result.setMsg("用户车辆不存在");
      return result;
    }
    if (!appVehicle.getAppUserNo().equals(appVehicleMaintainDTO.getUserNo())) {
      result.setMsg("车辆不属于该用户");
      return result;
    }

    appVehicle.setNextMaintainMileage(appVehicleMaintainDTO.getNextMaintainMileage());
    appVehicle.setNextInsuranceTime(appVehicleMaintainDTO.getNextInsuranceTime());
    appVehicle.setNextExamineTime(appVehicleMaintainDTO.getNextExamineTime());
    appVehicle.setCurrentMileage(appVehicleMaintainDTO.getCurrentMileage());

    Object status = writer.begin();
    try {
      writer.saveOrUpdate(appVehicle);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    result.setSuccess(true);
    result.setMsg("修改保养信息成功");
    return result;
  }

  @Override
  public ApiResponse getAppVehicleResponseByAppUserNo(String appUserNo) {
    if (StringUtil.isEmptyAppGetParameter(appUserNo))
      return MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_FAIL, ValidateMsg.APP_USER_NO_EMPTY);
    UserWriter writer = userDaoManager.getWriter();
    AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(appUserNo, null);
    if (appUserDTO == null) {
      return MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_FAIL, ValidateMsg.APP_USER_NOT_EXIST);
    }
    ShopDTO shopDTO = null;
    ObtainVehicleListResponse obtainVehicleListResponse = new ObtainVehicleListResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_SUCCESS));
    List<AppVehicle> appVehicleList = writer.getAppVehicleByAppUserNo(appUserNo);
    Map<Long, ObdDTO> map = getAppVehicleIdBindingOBDMapByAppUserNo(appUserNo, writer);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IShopService shopService = ServiceManager.getService(IShopService.class);
    AppVehicleDTO dto;
    Set<Long> appVehicleIdSet = new HashSet<Long>();
    String defaultVehicleNo = null;
    for (AppVehicle appVehicle : appVehicleList) {
      appVehicleIdSet.add(appVehicle.getId());
    }
    Map<Long, Long> vehicleIdShopIdMap = ServiceManager.getService(IAppUserShopBindingService.class)
      .getShopBindingVehicleIdShopIdMap(appUserNo, appVehicleIdSet);
    for (AppVehicle appVehicle : appVehicleList) {
      dto = appVehicle.toDTO();
      Long bindingShopId = vehicleIdShopIdMap.get(appVehicle.getId());
      dto.from(map.get(appVehicle.getId()));
      dto.setBindingShopId(bindingShopId);
      if (bindingShopId != null) {
        shopDTO = shopService.getShopDTOById(bindingShopId);
      }
      dto.from(shopDTO);
      if (YesNo.YES.equals(dto.getIsDefault())) {
        defaultVehicleNo = dto.getVehicleNo();
      }
      obtainVehicleListResponse.getVehicleList().add(dto);
    }
    if (StringUtils.isNotBlank(defaultVehicleNo)) {
      IAppUserConfigService appUserConfigService = ServiceManager.getService(IAppUserConfigService.class);
      JuheCityOilPriceDTO juheCityOilPriceDTO = appUserConfigService.getJuheCityOilPriceDTOByFirstCarNo(defaultVehicleNo.substring(0, 1));
      if (juheCityOilPriceDTO != null) {
        obtainVehicleListResponse.setDefaultOilPrice(String.valueOf(juheCityOilPriceDTO.getE93()));
        obtainVehicleListResponse.setDefaultOilKind(AppUserConfigConstant.OIL_KIND_93);
      }
    }
    return obtainVehicleListResponse;
  }

  private Map<Long, ObdDTO> getAppVehicleIdBindingOBDMapByAppUserNo(String appUserNo, UserWriter writer) {
    List<Object[]> list = writer.getAppVehicleIdBindingOBDMappingByAppUserNo(appUserNo);
    Map<Long, ObdDTO> map = new HashMap<Long, ObdDTO>();
    for (Object[] objects : list) {
      map.put((Long) objects[0], ((OBD) objects[1]).toDTO());
    }
    return map;
  }

  @Override
  public ApiResponse getAppVehicleDetail(Long vehicleId, String appUserNo) {
    UserWriter writer = userDaoManager.getWriter();
    VehicleResponse vehicleResponse;
    if (vehicleId != null) {
      AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(appUserNo, null);
      if (appUserDTO == null) {
        return MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_FAIL, ValidateMsg.APP_USER_NOT_EXIST);
      }
      AppVehicle appVehicle = writer.getById(AppVehicle.class, vehicleId);
      if (appVehicle != null && appVehicle.getStatus() == Status.active) {
        vehicleResponse = new VehicleResponse(MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_SUCCESS));
        vehicleResponse.setVehicleInfo(getAppVehicleDTO(vehicleId, appUserNo, writer, appVehicle));
        return vehicleResponse;
      }
    } else {
      return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_FAIL, ValidateMsg.APP_VEHICLE_ID_EMPTY);
    }
    return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_FAIL, ValidateMsg.APP_VEHICLE_NOT_EXIST);
  }

  private AppVehicleDTO getAppVehicleDTO(Long vehicleId, String appUserNo, UserWriter writer, AppVehicle appVehicle) {
    Map<Long, ObdDTO> map = getAppVehicleIdBindingOBDMapByAppUserNo(appUserNo, writer);
    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(vehicleId);
    Map<Long, Long> vehicleIdShopIdMap = ServiceManager.getService(IAppUserShopBindingService.class)
      .getShopBindingVehicleIdShopIdMap(appUserNo, shopIdSet);
    AppVehicleDTO dto = appVehicle.toDTO();
    dto.setBindingShopId(vehicleIdShopIdMap.get(vehicleId));
    if (dto.getBindingShopId() != null) {
      ShopDTO shopDTO = ServiceManager.getService(IShopService.class).getShopDTOById(dto.getBindingShopId());
      dto.from(shopDTO);
    }
    dto.from(map.get(appVehicle.getId()));
    return dto;
  }

  @Override
  public ApiResponse getAppVehicleDetail(String appUserNo, String vehicleVin) {
    UserWriter writer = userDaoManager.getWriter();
    VehicleResponse vehicleResponse;
    if (StringUtil.isNotEmpty(appUserNo) && StringUtil.isNotEmpty(vehicleVin)) {
      AppVehicle appVehicle = writer.getAppVehicleDetail(appUserNo, vehicleVin);
      if (appVehicle != null && appVehicle.getStatus() == Status.active) {
        vehicleResponse = new VehicleResponse(MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_SUCCESS));
        vehicleResponse.setVehicleInfo(getAppVehicleDTO(appVehicle.getId(), appUserNo, writer, appVehicle));
        return vehicleResponse;
      }
    } else {
      return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_FAIL, ValidateMsg.APP_VEHICLE_ID_EMPTY);
    }
    return MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_FAIL, ValidateMsg.APP_VEHICLE_NOT_EXIST);
  }

  @Override
  public AppVehicleDTO getAppVehicleDetailByVehicleNo(String appUserNo, String vehicleNo, YesNo isDefault) {
    AppVehicle appVehicle = userDaoManager.getReader().getAppVehicleDetailByVehicleNo(appUserNo, vehicleNo, isDefault);
    if (appVehicle != null) {
      return appVehicle.toDTO();
    }
    return null;
  }

  @Override
  public Map<String, AppVehicleDTO> getAppVehicleMapByAppUserNo(String appUserNo) {
    List<AppVehicle> appVehicleList = userDaoManager.getWriter().getAppVehicleByAppUserNo(appUserNo);
    Map<String, AppVehicleDTO> map = new HashMap<String, AppVehicleDTO>();
    for (AppVehicle entity : appVehicleList) {
      map.put(entity.getVehicleNo(), entity.toDTO());
    }
    return map;
  }


  @Override
  public Map<Long, AppVehicleDTO> getAppVehicleIdMapByAppUserNo(String appUserNo) {
    List<AppVehicle> appVehicleList = userDaoManager.getWriter().getAppVehicleByAppUserNo(appUserNo);
    Map<Long, AppVehicleDTO> map = new HashMap<Long, AppVehicleDTO>();
    for (AppVehicle entity : appVehicleList) {
      map.put(entity.getId(), entity.toDTO());
    }
    return map;
  }

  @Override
  public ApiResponse deleteVehicle(Long vehicleId, Long appUserId) {
    UserWriter writer = userDaoManager.getWriter();
    if (vehicleId != null) {
      Object status = writer.begin();
      try {
        AppVehicle appVehicle = writer.getById(AppVehicle.class, vehicleId);
        if (appVehicle != null) {
          appVehicle.setStatus(Status.deleted);
          writer.saveOrUpdate(appVehicle);
          List<ObdUserVehicle> obdUserVehicleList = writer.getObdUserVehicle(appVehicle.getAppUserNo(), vehicleId);
          if (CollectionUtil.isNotEmpty(obdUserVehicleList)) {
            for (ObdUserVehicle vehicle : obdUserVehicleList) {
              vehicle.setStatus(ObdUserVehicleStatus.DELETED);
            }
          }
          ServiceManager.getService(IAppUserShopBindingService.class)
            .unbinding(appVehicle.getAppUserNo(), appVehicle.getId(), writer);
          writer.commit(status);
          List<AppVehicle> appVehicleList = writer.getAppVehicleByUserNoVehicleNo(appVehicle.getAppUserNo(), null);
          if (CollectionUtil.isNotEmpty(appVehicleList))
            updateDefaultAppVehicle(appVehicleList.get(0).getId(), appVehicleList.get(0).getAppUserNo(), true);
          return MessageCode.toApiResponse(MessageCode.DELETE_SINGLE_APP_VEHICLE_SUCCESS);
        }
      } finally {
        writer.rollback(status);
      }
    } else {
      return MessageCode.toApiResponse(MessageCode.DELETE_SINGLE_APP_VEHICLE_FAIL, "车辆Id为空");
    }
    return MessageCode.toApiResponse(MessageCode.DELETE_SINGLE_APP_VEHICLE_FAIL, "找不到车辆信息");
  }

  /**
   * 保存或更新车辆信息 油耗、当前里程等信息
   *
   * @param appVehicleDTO
   * @return
   */
  public Result saveOrUpdateVehicleCondition(AppVehicleDTO appVehicleDTO) {
    Result result = new Result();
    result.setSuccess(false);
    try {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      UserWriter writer = userDaoManager.getWriter();

      OBD obd = writer.getObdBySn(appVehicleDTO.getObdSN());
      if (obd == null) {
        result.setMsg("OBD不存在");
        return result;
      }

//      AppVehicleDTO dbVehicleDTO = this.getBindingAppVehicleByVinUserNo(appVehicleDTO.getUserNo(), appVehicleDTO.getVehicleVin());
      AppVehicleDTO dbVehicleDTO = this.getAppVehicleById(appVehicleDTO.getVehicleId());
      if (dbVehicleDTO == null) {
        result.setMsg("车辆不存在");
        return result;
      }

      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appVehicleDTO.getUserNo(), null);
      if (appUserDTO == null) {
        result.setMsg("用户不存在");
        return result;
      }

      AppVehicle appVehicle = writer.getById(AppVehicle.class, dbVehicleDTO.getVehicleId());

      appVehicle.setOilWear(appVehicleDTO.getOilWear());
      appVehicle.setCurrentMileage(appVehicleDTO.getCurrentMileage());
      appVehicle.setOilMass(appVehicleDTO.getOilMass());
      appVehicle.setInstantOilWear(appVehicleDTO.getInstantOilWear());
      appVehicle.setOilWearPerHundred(appVehicleDTO.getOilWearPerHundred());
      appVehicle.setEngineCoolantTemperature(appVehicleDTO.getEngineCoolantTemperature());
      appVehicle.setBatteryVoltage(appVehicleDTO.getBatteryVoltage());

      appVehicle.setReportTime(appVehicleDTO.getReportTime() == null ? System.currentTimeMillis() : appVehicleDTO.getReportTime());

      Object status = writer.begin();
      try {
        writer.update(appVehicle);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
      result.setSuccess(true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("保存车辆信息异常");
    }
    return result;
  }

  /**
   * 根据车牌号查找车辆
   *
   * @param vehicleNo
   * @return
   */
  @Override
  public List<AppVehicleDTO> getAppVehicleByVehicleNo(String vehicleNo) {
    UserWriter writer = userDaoManager.getWriter();
    List<AppVehicle> vehicleList = writer.getAppVehicleByUserNoVehicleNo(null, vehicleNo);
    List<AppVehicleDTO> appVehicleDTOList = new ArrayList<AppVehicleDTO>();

    if (CollectionUtils.isEmpty(vehicleList)) {
      return appVehicleDTOList;
    }
    for (AppVehicle appVehicle : vehicleList) {
      appVehicleDTOList.add(appVehicle.toDTO());
    }
    return appVehicleDTOList;
  }


  public AppVehicleDTO getAppVehicleById(Long vehicleId) {
    UserWriter writer = userDaoManager.getWriter();
    AppVehicle appVehicle = writer.getById(AppVehicle.class, vehicleId);
    return appVehicle == null ? null : appVehicle.toDTO();
  }

  @Override
  public void updateNextMaintainMileagePushMessageRemindLimit(Set<Long> vehicleIds) {
    if (CollectionUtil.isEmpty(vehicleIds)) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.updateNextMaintainMileagePushMessageRemindLimit(vehicleIds);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public AppGsmVehicleResponse gsmUserGetAppVehicle(String appUserNo) {
    IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
    UserWriter writer = userDaoManager.getWriter();
    AppGsmVehicleResponse appGsmVehicleResponse;
    if (StringUtil.isNotEmpty(appUserNo)) {
      AppUserDTO appUserDTO = ServiceManager.getService(IAppUserService.class).getAppUserByUserNo(appUserNo, null);
      if (appUserDTO == null) {
        appGsmVehicleResponse = new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_FAIL, ValidateMsg.APP_USER_NOT_EXIST));
        return appGsmVehicleResponse;
      }

      List<AppVehicle> appVehicleList = writer.getAppVehicleByAppUserNo(appUserDTO.getUserNo());

      if (CollectionUtil.isNotEmpty(appVehicleList)) {
        appGsmVehicleResponse = new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.SINGLE_APP_VEHICLE_SUCCESS));
        AppVehicleDTO appVehicleDTO = CollectionUtil.getFirst(appVehicleList).toDTO();

        AppUserConfig appUserConfig = writer.getAppUserConfigByName(appUserNo, AppUserConfigConstant.OIL_PRICE);
        if (appUserConfig != null) {
          appVehicleDTO.setOilPrice(NumberUtil.doubleVal(appUserConfig.getValue()));
        }


        ObdUserVehicle obdUserVehicle = CollectionUtil.getFirst(writer.getBundlingObdUserVehicleByUserNo(appUserNo));
        if (obdUserVehicle != null) {
          ObdSimBind obdSimBind = writer.getObdSimBindsByObdId(obdUserVehicle.getObdId());
          if (obdSimBind != null) {
//            ObdSim obdSim = writer.getById(ObdSim.class,obdSimBind.getSimId());
//            if(obdSim != null && StringUtils.isNotBlank(obdSim.getMobile())){
//              appUserDTO.setGsmObdImeiMoblie(obdSim.getMobile());
//            }
            OBD obd = writer.getById(OBD.class, obdSimBind.getObdId());
            if (obd != null && StringUtils.isNotBlank(obd.getImei())) {
              appUserDTO.setImei(obd.getImei());
            }
          }

        }

        if (AppUserType.POBD.equals(appUserDTO.getAppUserType())) {
          GsmVehicleDataDTO gsmVehicleDataDTO = ServiceManager.getService(IGSMVehicleDataService.class).getLastGsmVehicleData(appUserNo);
          LOG.info("POBD:获取最新车况,data:{}", JsonUtil.objectToJson(gsmVehicleDataDTO));
          if (gsmVehicleDataDTO != null) {
            appVehicleDTO.setCoordinateLat(gsmVehicleDataDTO.getLat());
            appVehicleDTO.setCoordinateLon(gsmVehicleDataDTO.getLon());
          }
        } else {
          GsmPoint gsmPoint = gsmPointService.getLastGsmPointByImei(appUserDTO.getImei());
          if (gsmPoint != null && NumberUtil.doubleVal(gsmPoint.getLon()) > 0 && NumberUtil.doubleVal(gsmPoint.getLat()) > 0) {
            appVehicleDTO.setCoordinateLat(NumberUtil.convertGPSLat(gsmPoint.getLat()));
            appVehicleDTO.setCoordinateLon(NumberUtil.convertGPSLot(gsmPoint.getLon()));
          }
        }

        appGsmVehicleResponse.setVehicleInfo(appVehicleDTO);
        return appGsmVehicleResponse;
      }
    } else {
      appGsmVehicleResponse = new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_FAIL, ValidateMsg.APP_VEHICLE_ID_EMPTY));
      return appGsmVehicleResponse;
    }
    appGsmVehicleResponse = new AppGsmVehicleResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_APP_VEHICLE_FAIL, ValidateMsg.APP_VEHICLE_NOT_EXIST));
    return appGsmVehicleResponse;
  }

  @Override
  public ApiResponse updateOilPriceByAppVehicleDTO(AppVehicleDTO appVehicleDTO) {

    if (appVehicleDTO.getOilPrice() == null) {
      return MessageCode.toApiResponse(MessageCode.SAVE_OIL_PRICE_FAIL, "请填写油价");
    }

    AppUserConfigUpdateRequest appUserConfigUpdateRequest = new AppUserConfigUpdateRequest();
    appUserConfigUpdateRequest.setAppUserNo(appVehicleDTO.getUserNo());
    AppUserConfigDTO[] appUserConfigDTOs = new AppUserConfigDTO[1];
    AppUserConfigDTO appUserConfigDTO = new AppUserConfigDTO();
    appUserConfigDTO.setAppUserNo(appVehicleDTO.getUserNo());
    appUserConfigDTO.setName(AppUserConfigConstant.OIL_PRICE);
    appUserConfigDTO.setSyncTime(System.currentTimeMillis());
    appUserConfigDTO.setValue(appVehicleDTO.getOilPrice().toString());
    appUserConfigDTOs[0] = appUserConfigDTO;
    appUserConfigUpdateRequest.setAppUserConfigDTOs(appUserConfigDTOs);

    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);


    ApiResponse apiResponse = appUserService.validateUpdateAppUserConfigByAppUser(appUserConfigUpdateRequest);
    if (MessageCode.UPDATE_APP_USER_CONFIG_SUCCESS.getCode() == apiResponse.getMsgCode()) {
      apiResponse = appUserService.updateAppUserConfig(appUserConfigUpdateRequest);
    }
    return apiResponse;
  }

  @Override
  public ObdDTO getObdByIMei(String iMei) {
    if (StringUtils.isEmpty(iMei)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    OBD obd = writer.getObdByImeiObdType(iMei,null);
    if (obd != null) {
      return obd.toDTO();
    }
    return null;
  }

  @Override
  public ObdDTO getObd_MirrorByIMei(String iMei) {
    if (StringUtils.isEmpty(iMei)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    OBD obd = writer.getObdByImeiObdType(iMei,null);
    if (obd != null) {
      return obd.toDTO();
    }
    return null;
  }

  @Override
  public ObdUserVehicleDTO getBundlingObdUserVehicleDTOByObdId(Long obdId) {
    if (obdId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    ObdUserVehicle obdUserVehicle = writer.getBundlingObdUserVehicleByObdId(obdId);
    if (obdUserVehicle != null) {
      return obdUserVehicle.toDTO();
    }
    return null;
  }

  /**
   * 保存或更新车辆信息
   *
   * @param appVehicleDTO
   * @return
   */
  @Override
  public Result saveOrUpdateVehicle(AppVehicleDTO appVehicleDTO) {
    Result result = new Result();
    result.setSuccess(false);
    try {
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      UserWriter writer = userDaoManager.getWriter();

      AppVehicleDTO dbVehicleDTO = this.getAppVehicleById(appVehicleDTO.getVehicleId());
      if (dbVehicleDTO == null) {
        result.setMsg("车辆不存在");
        return result;
      }

      AppUserDTO appUserDTO = appUserService.getAppUserByUserNo(appVehicleDTO.getUserNo(), null);
      if (appUserDTO == null) {
        result.setMsg("用户不存在");
        return result;
      }

      AppVehicle appVehicle = writer.getById(AppVehicle.class, dbVehicleDTO.getVehicleId());
      appVehicle.setMaintainPeriod(appVehicleDTO.getMaintainPeriod());
      appVehicle.setMobile(appVehicleDTO.getMobile());
      appVehicle.setNextMaintainMileage(appVehicleDTO.getNextMaintainMileage());
      appVehicle.setNextMaintainTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, appVehicleDTO.getNextMaintainTimeStr()));
      appVehicle.setNextExamineTime(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, appVehicleDTO.getNextExamineTimeStr()));
      Object status = writer.begin();
      try {
        writer.update(appVehicle);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
      result.setSuccess(true);
      result.setMsg("修改车辆信息成功！");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("保存车辆信息异常");
    }
    return result;
  }
}
