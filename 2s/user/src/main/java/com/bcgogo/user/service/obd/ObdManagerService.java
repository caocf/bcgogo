package com.bcgogo.user.service.obd;

import com.bcgogo.api.*;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.enums.app.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.Vehicle;
import com.bcgogo.user.model.app.*;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.excelimport.obd.ObdImporter;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * Created by XinyuQiu on 14-6-24.
 */
@Component
public class ObdManagerService implements IObdManagerService {
  private static final Logger LOG = LoggerFactory.getLogger(ObdManagerService.class);

  @Autowired
  private UserDaoManager userDaoManager;

  @Autowired
  private ObdImporter obdImporter;

  @Override
  public List<ObdSimBindDTO> searchObdSimBindDTO(ObdSimSearchCondition condition) {
    UserWriter writer = userDaoManager.getWriter();
    List<ObdSimBindDTO> obdSimBindDTOs = writer.searchObdSimBindDTOsByAdmin(condition);
    if (CollectionUtils.isNotEmpty(obdSimBindDTOs)) {
      return obdSimBindDTOs;
    }
    return null;
  }

  @Override
  public int countObdSimBindDTO(ObdSimSearchCondition condition) {
    if (condition != null) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.countObdSimBindDTOsByAdmin(condition);
    }
    return 0;
  }

  public ObdSimBindDTO getObdSimBindByShopExact(String imei, String mobile) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getObdSimBindByShopExact(imei, mobile);
  }

  public List<ObdSimBindDTO> getObdSimBindDTOByShop(ObdSimSearchCondition condition) throws ParseException {
    UserWriter writer = userDaoManager.getWriter();
    List<ObdSimBindDTO> obdSimBindDTOs = writer.getObdSimBindDTOByShop(condition);
    if (CollectionUtil.isEmpty(obdSimBindDTOs)) {
      return new ArrayList<ObdSimBindDTO>();
    }
    Set<Long> obdIdSet = new HashSet<Long>();
    for (ObdSimBindDTO obdSimBindDTO : obdSimBindDTOs) {
      if (obdSimBindDTO.getUseDate() != null && obdSimBindDTO.getUsePeriod() != null) {
        Long useEndDate = DateUtil.getInnerYearTime(obdSimBindDTO.getUseDate(), obdSimBindDTO.getUsePeriod());
        obdSimBindDTO.setUseEndDateStr(DateUtil.convertDateLongToString(useEndDate, DateUtil.DEFAULT));
      }
      obdIdSet.add(obdSimBindDTO.getObdId());
    }
    return obdSimBindDTOs;
  }

  public int countObdSimBindByShop(ObdSimSearchCondition condition) throws ParseException {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countObdSimBindByShop(condition);
  }

  @Override
  public Result doStorageOBD(Long shopId, String shopName, ObdSimBindDTO[] obdSimBindDTOs) throws ParseException {
    Result result = new Result();
    ObdSimSearchCondition condition = new ObdSimSearchCondition();
    ObdSimBindDTO obdSimBindDTO = null;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (ObdSimBindDTO bindDTO : obdSimBindDTOs) {
        condition.setImei(bindDTO.getImei());
        condition.setMobile(bindDTO.getMobile());
        obdSimBindDTO = CollectionUtil.getFirst(getObdSimBindDTOByShop(condition));
        OBD obd = writer.getById(OBD.class, obdSimBindDTO.getObdId());
        obd.setSellShopId(shopId);
        obd.setObdStatus(OBDStatus.ON_SELL);
        obd.setStorageTime(System.currentTimeMillis());
        obd.setOwnerId(shopId);
        obd.setOwnerType(ObdSimOwnerType.SHOP);
        obd.setOwnerName(shopName);
        writer.update(obd);
        bindDTO.setOwnerId(obd.getOwnerId());
        bindDTO.setOwnerType(obd.getOwnerType());
        bindDTO.setOwnerName(obd.getOwnerName());

        ObdSimBind obdSimBind = writer.getObdSimBindsByObdId(obd.getId());
        if (obdSimBind != null && ObdSimBindStatus.ENABLED.equals(obdSimBind.getStatus())) {
          ObdSim obdSim = writer.getById(ObdSim.class, obdSimBind.getSimId());
          if (obdSim != null) {
            obdSim.setStatus(OBDStatus.ON_SELL);
            obdSim.setOwnerId(shopId);
            obdSim.setOwnerName(shopName);
            obdSim.setOwnerType(ObdSimOwnerType.SHOP);
            writer.update(obdSim);
            bindDTO.setSimId(obdSim.getId());
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    //记录操作日志
    for (ObdSimBindDTO bindDTO : obdSimBindDTOs) {
      createOBDSimOperationLog(bindDTO, OBDSimOperationType.IN_STORAGE);
    }
    return result;
  }

  @Override
  public Result gsmOBDBind(VehicleDTO vehicleDTO, String imei, String mobile, Long userId, String shopName, String userName) throws ParseException, BcgogoException {
    if (vehicleDTO == null || vehicleDTO.getShopId() == null ||
        StringUtil.isEmpty(imei) || StringUtil.isEmpty(mobile)) {
      return new Result();
    }
    OBDBindDTO bindDTO=new OBDBindDTO();
    bindDTO.setShopId(vehicleDTO.getShopId());
    bindDTO.setImei(imei);
    bindDTO.setMobile(mobile);
    bindDTO.setVehicleId(vehicleDTO.getId());
    bindDTO.setUserId(userId);
    bindDTO.setShopName(shopName);
    bindDTO.setUserName(userName);
    return gsmOBDBind(bindDTO);
  }

  @Override
  public ObdDTO getObdByImeiAndMobile(String imei,String mobile){
    ObdSimBindDTO obdSimBindDTO= new ObdSimBindDTO();
    ObdDTO obdDTO = new ObdDTO();
    try {
      obdSimBindDTO = getObdSimBindByShopExact(imei, mobile);
      obdDTO = getObdDTOById(obdSimBindDTO.getObdId());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return obdDTO;
  }

  @Override
  public Result gsmOBDBind(OBDBindDTO bindDTO) throws ParseException, BcgogoException {
    Result result = new Result();
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    VehicleDTO vehicleDTO = vehicleService.getVehicleDTOById(bindDTO.getVehicleId());
    if (vehicleDTO == null) {
      return result.LogErrorMsg("车辆信息异常!");
    }
    String imei=bindDTO.getImei();
    if (StringUtil.isEmpty(imei)) {
      return result.LogErrorMsg("请输入IMEI号!");
    }
    String mobile=bindDTO.getMobile();
    if (StringUtil.isEmpty(mobile)) {
      return result.LogErrorMsg("请输入SIM卡号!");
    }
    ObdSimBindDTO obdSimBindDTO = getObdSimBindByShopExact(imei, mobile);
    if (obdSimBindDTO == null) {
      return result.LogErrorMsg("您输入的OBD不存在，请联系客服，电话：0512-66733331");
    }
    Long shopId=bindDTO.getShopId();
    if (obdSimBindDTO.getSellShopId() != null && !obdSimBindDTO.getSellShopId().equals(shopId)) {
      return result.LogErrorMsg("您输入的OBD已经在其他店铺销售!");
    }
    if (obdSimBindDTO.getVehicleId() != null && !obdSimBindDTO.getVehicleId().equals(vehicleDTO.getId())) {
      return result.LogErrorMsg("您输入的OBD已经安装在车辆" + obdSimBindDTO.getLicenceNo() + "上!");
    }
    obdSimBindDTO.setOperateShopId(shopId);
    obdSimBindDTO.setOperateUserId(bindDTO.getUserId());
    obdSimBindDTO.setOperateUserName(bindDTO.getUserName());
    //入库OBD
    String shopName=bindDTO.getShopName();
    Long userId=bindDTO.getUserId();
    String userName=bindDTO.getUserName();
    if (obdSimBindDTO.getSellShopId() == null) {
      ObdSimBindDTO[] obdSimBindDTOs = {obdSimBindDTO};
      doStorageOBD(shopId,shopName, obdSimBindDTOs);
    }
    //如果是更换OBD操作
    if (StringUtil.isNotEmpty(vehicleDTO.getGsmObdImei()) && StringUtil.isNotEmpty(vehicleDTO.getGsmObdImeiMoblie())) {
      ObdSimBindDTO orig_obdSimBindDTO = getObdSimBindByShopExact(vehicleDTO.getGsmObdImei(), vehicleDTO.getGsmObdImeiMoblie());
      if (orig_obdSimBindDTO != null) {
        ObdDTO obdDTO = getObdDTOById(orig_obdSimBindDTO.getObdId());
        if (obdDTO != null) {
          obdDTO.setObdStatus(OBDStatus.ON_SELL);
          obdDTO.setOwnerId(shopId);
          obdDTO.setOwnerName(shopName);
          obdDTO.setOwnerType(ObdSimOwnerType.SHOP);
          obdDTO.setSellTime(null);
          updateOBD(obdDTO);
        }
        orig_obdSimBindDTO.setOperateShopId(shopId);
        orig_obdSimBindDTO.setOperateUserId(userId);
        orig_obdSimBindDTO.setOperateUserName(userName);
        createOBDSimOperationLog(orig_obdSimBindDTO, OBDSimOperationType.UN_INSTALL);
        //判断是否和appuser关联过
        LOG.info("gsm bind,appvehicleId={}", orig_obdSimBindDTO.getAppVehicleId());
        if (orig_obdSimBindDTO.getAppVehicleId() != null) {
          IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
          AppUserShopVehicleDTO appUserShopVehicleDTO = appUserService.getAppUserShopVehicleDTO(orig_obdSimBindDTO.getAppVehicleId(),
              orig_obdSimBindDTO.getObdId(), AppUserShopVehicleStatus.BUNDLING);
          if (appUserShopVehicleDTO != null) {
            appUserShopVehicleDTO.setStatus(ObdUserVehicleStatus.DELETED);
            appUserService.saveOrUpdateAppUserShopVehicle(appUserShopVehicleDTO);
            appUserShopVehicleDTO.setId(null);
            appUserShopVehicleDTO.setObdId(obdSimBindDTO.getObdId());
            appUserService.saveOrUpdateAppUserShopVehicle(appUserShopVehicleDTO);
          }
          ObdUserVehicleDTO obdUserVehicleDTO = appUserService.getObdUserVehicle(orig_obdSimBindDTO.getAppVehicleId(),
              orig_obdSimBindDTO.getObdId(), ObdUserVehicleStatus.BUNDLING);
          if (obdUserVehicleDTO != null) {
            obdUserVehicleDTO.setStatus(ObdUserVehicleStatus.DELETED);
            appUserService.saveOrUpdateObdUserVehicle(obdUserVehicleDTO);
            obdUserVehicleDTO.setId(null);
            obdUserVehicleDTO.setObdId(obdSimBindDTO.getObdId());
            appUserService.saveOrUpdateObdUserVehicle(obdUserVehicleDTO);
          }
          obdSimBindDTO.setAppVehicleId(orig_obdSimBindDTO.getAppVehicleId());
        }

      }
    }
    //安装到车辆
    vehicleDTO.setObdId(obdSimBindDTO.getObdId());
    vehicleDTO.setGsmObdImei(obdSimBindDTO.getImei());
    vehicleDTO.setGsmObdImeiMoblie(obdSimBindDTO.getMobile());
    vehicleService.updateVehicle(vehicleDTO);
    ObdDTO obdDTO = getObdDTOById(obdSimBindDTO.getObdId());
    if (obdDTO != null) {
      obdDTO.setObdStatus(OBDStatus.SOLD);
      obdDTO.setOwnerId(vehicleDTO.getId());
      obdDTO.setOwnerName(vehicleDTO.getLicenceNo());
      obdDTO.setOwnerType(ObdSimOwnerType.SHOP_VEHICLE);
      obdDTO.setSellTime(System.currentTimeMillis());
      obdDTO.setSellShopId(shopId);
      updateOBD(obdDTO);
    }
    //log
    obdSimBindDTO.setOperateUserId(userId);
    obdSimBindDTO.setOperateUserName(userName);
    createOBDSimOperationLog(obdSimBindDTO, OBDSimOperationType.INSTALL);
    try {
      ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(vehicleDTO.getShopId(), vehicleDTO.getId());
    } catch (Exception e) {
      LOG.error("更新完车辆里程做索引失败" + e.getMessage(), e);
    }
    //判断是否和appuser关联过
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppUserShopVehicleDTO appUserShopVehicleDTO = appUserService.getAppUserShopVehicleDTO(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), AppUserShopVehicleStatus.DELETED);
    if (obdSimBindDTO.getAppVehicleId() != null) {
      if (appUserShopVehicleDTO != null) {
        appUserShopVehicleDTO.setStatus(ObdUserVehicleStatus.BUNDLING);
        appUserService.saveOrUpdateAppUserShopVehicle(appUserShopVehicleDTO);
      }
      ObdUserVehicleDTO obdUserVehicleDTO = appUserService.getObdUserVehicle(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), ObdUserVehicleStatus.DELETED);
      if (obdUserVehicleDTO != null) {
        obdUserVehicleDTO.setStatus(ObdUserVehicleStatus.BUNDLING);
        appUserService.saveOrUpdateObdUserVehicle(obdUserVehicleDTO);
      }
    }
    //判断obd是否已和appuser关联
    appUserShopVehicleDTO = appUserService.getAppUserShopVehicleDTO(obdSimBindDTO.getAppVehicleId(), obdSimBindDTO.getObdId(), AppUserShopVehicleStatus.BUNDLING);
    if(appUserShopVehicleDTO != null){
      String appUserNo=appUserShopVehicleDTO.getAppUserNo();
      //判断obd类型
      if(ObdType.SGSM.equals(obdDTO.getObdType())) {
        //2s代金券更新
        ServiceManager.getService(ICouponService.class).updateCouponForGsmOBDBind(appUserNo, imei);
      }
    }
    return result;
  }

  @Override
  public void createOBDSimOperationLog(ObdSimBindDTO obdSimBindDTO, OBDSimOperationType operationType) {
    if (obdSimBindDTO == null) return;
    ObdSimHistory obdSimHistory = new ObdSimHistory(getObdSimDTOById(obdSimBindDTO.getSimId()));
    ObdHistory obdHistory = new ObdHistory(getObdDTOById(obdSimBindDTO.getObdId()));
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(obdHistory);
      writer.save(obdSimHistory);
      obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
      obdSimBindDTO.setObdHistoryId(obdHistory.getId());
      OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
      operationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
      operationLogDTO.setOperationType(operationType);
      String content = null;
      if (OBDSimOperationType.IN_STORAGE.equals(operationType)) {
        content = "店铺OBD或后视镜入库";
      } else if (OBDSimOperationType.INSTALL.equals(operationType)) {
        content = "店铺OBD或后视镜安装到车辆";
      } else if (OBDSimOperationType.UN_INSTALL.equals(operationType)) {
        content = "店铺OBD或后视镜从车辆卸载";
      }
      operationLogDTO.setContent(content);
      OBDSimOperationLog operationLog = new OBDSimOperationLog();
      operationLog.fromDTO(operationLogDTO);
      writer.save(operationLog);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateOBD(ObdDTO obdDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      OBD obd = writer.getById(OBD.class, obdDTO.getId());
      obd.fromDTO(obdDTO);
      writer.update(obd);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ImportResult importOBDInventoryFromExcel(ImportContext importContext) throws Exception {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.directParseData(importContext);

    //2.校验数据
    CheckResult checkResult = obdImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = obdImporter.importData(importContext);

    return importResult;
  }

  //OBD 库存数据初始化
  @Override
  public ImportResult initImportOBDInventoryFromExcel(ImportContext importContext) throws Exception {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.directParseData(importContext);

    //3.保存数据
    importResult = obdImporter.initImportData(importContext);

    return importResult;
  }

  @Override
  public Map<String, ObdDTO> getImeiObdDTOMap(Set<String> imeiSet, ObdType obdType) {
    Map<String, ObdDTO> obdDTOMap = new HashMap<String, ObdDTO>();
    if (CollectionUtils.isNotEmpty(imeiSet) && obdType != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<OBD> obds = writer.getObdByImeisObdType(imeiSet, obdType);
      if (CollectionUtils.isNotEmpty(obds)) {
        for (OBD obd : obds) {
          if (obd != null && StringUtils.isNotEmpty(obd.getImei())) {
            obdDTOMap.put(obd.getImei(), obd.toDTO());
          }
        }
      }
    }
    return obdDTOMap;
  }

  @Override
  public Map<String, ObdSimDTO> getMobileObdSimDTOMap(Set<String> mobileSet) {
    Map<String, ObdSimDTO> obdSimDTOMap = new HashMap<String, ObdSimDTO>();
    if (CollectionUtils.isNotEmpty(mobileSet)) {
      UserWriter writer = userDaoManager.getWriter();
      List<ObdSim> obdSimList = writer.getObdSimByMobiles(mobileSet);
      if (CollectionUtils.isNotEmpty(obdSimList)) {
        for (ObdSim obdSim : obdSimList) {
          if (obdSim != null && StringUtils.isNotEmpty(obdSim.getMobile())) {
            obdSimDTOMap.put(obdSim.getMobile(), obdSim.toDTO());
          }
        }
      }
    }
    return obdSimDTOMap;
  }

  @Override
  public Map<String, ObdSimDTO> getSimNoObdSimDTOMap(Set<String> simNoSet) {
    Map<String, ObdSimDTO> obdSimDTOMap = new HashMap<String, ObdSimDTO>();
    if (CollectionUtils.isNotEmpty(simNoSet)) {
      UserWriter writer = userDaoManager.getWriter();
      List<ObdSim> obdSimList = writer.getObdSimBySimNos(simNoSet);
      if (CollectionUtils.isNotEmpty(obdSimList)) {
        for (ObdSim obdSim : obdSimList) {
          if (obdSim != null && StringUtils.isNotEmpty(obdSim.getSimNo())) {
            obdSimDTOMap.put(obdSim.getSimNo(), obdSim.toDTO());
          }
        }
      }
    }
    return obdSimDTOMap;
  }

  @Override
  public void batchCreateObdAndSim(List<ObdSimBindDTO> obdSimBindDTOs) {
    if (CollectionUtils.isNotEmpty(obdSimBindDTOs)) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        for (ObdSimBindDTO obdSimBindDTO : obdSimBindDTOs) {
          if (obdSimBindDTO != null) {
            OBD obd = null;
            ObdSim obdSim = null;
            ObdSimBind obdSimBind = null;
            if (StringUtils.isNotBlank(obdSimBindDTO.getImei())) {
              obd = new OBD();
              obd.fromObdSimBindDTO(obdSimBindDTO);
              writer.save(obd);
              ObdHistory obdHistory = new ObdHistory(obd);
              writer.save(obdHistory);
              obdSimBindDTO.setObdId(obd.getId());
              obdSimBindDTO.setObdHistoryId(obdHistory.getId());
            }
            if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
              obdSim = new ObdSim();
              obdSim.fromObdSimBindDTO(obdSimBindDTO);
              writer.save(obdSim);
              obdSimBindDTO.setSimId(obdSim.getId());
              ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
              writer.save(obdSimHistory);
              obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
            }
            if (obd != null && obdSim != null) {
              obdSimBind = new ObdSimBind();
              obdSimBind.fromObdSimBindDTO(obdSimBindDTO);
              writer.save(obdSimBind);
            }
            OBDSimOperationLogDTO operationLogDTO = generateObdOperationLogDTO(obdSimBindDTO);
            OBDSimOperationLog operationLog = new OBDSimOperationLog();
            operationLog.fromDTO(operationLogDTO);
            writer.save(operationLog);
          }
        }

        writer.commit(status);
      } finally {
        writer.rollback(status);
      }

    }
  }

  //导入初始化
  @Override
  public void initCreateObdAndSim(List<ObdSimBindDTO> obdSimBindDTOs) {
    if (CollectionUtils.isNotEmpty(obdSimBindDTOs)) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        for (ObdSimBindDTO obdSimBindDTO : obdSimBindDTOs) {
          if (obdSimBindDTO != null && StringUtils.isNotBlank(obdSimBindDTO.getImei())
              && StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
            OBD obd = writer.getObdByImeiObdType(obdSimBindDTO.getImei(),null);
            Vehicle vehicle = CollectionUtil.getFirst(writer.getVehicleByGsmObdImei(obdSimBindDTO.getImei()));

            ObdSim obdSim = writer.getObdSimByMobile(obdSimBindDTO.getMobile());
            ObdSimBind obdSimBind = null;
            if (obd == null) {
              obd = new OBD();
              obd.fromObdSimBindDTO(obdSimBindDTO);
              writer.save(obd);
            } else {
              obd.fromObdSimBindDTO(obdSimBindDTO);
              if (vehicle != null) {
                obd.setObdStatus(OBDStatus.SOLD);
                obd.setOwnerType(ObdSimOwnerType.SHOP_VEHICLE);
                obd.setOwnerId(vehicle.getId());
                obd.setOwnerName(vehicle.getLicenceNo());
              }
              writer.update(obd);
            }
            ObdHistory obdHistory = new ObdHistory(obd);
            writer.save(obdHistory);
            obdSimBindDTO.setObdId(obd.getId());
            obdSimBindDTO.setObdHistoryId(obdHistory.getId());

            if (obdSim == null) {
              obdSim = new ObdSim();
              obdSim.fromObdSimBindDTO(obdSimBindDTO);
              writer.save(obdSim);
            } else {
              obdSim.fromObdSimBindDTO(obdSimBindDTO);
              if (vehicle != null) {
                obdSim.setStatus(OBDStatus.SOLD);
                obdSim.setOwnerType(ObdSimOwnerType.SHOP_VEHICLE);
                obdSim.setOwnerId(vehicle.getId());
                obdSim.setOwnerName(vehicle.getLicenceNo());
              }
              writer.update(obdSim);
            }

            writer.save(obdSim);
            obdSimBindDTO.setSimId(obdSim.getId());
            ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
            writer.save(obdSimHistory);
            obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());

            obdSimBind = writer.getObdSimBindByObdIdAndSimId(obd.getId(), obdSim.getId());
            if (obdSimBind == null) {
              obdSimBind = new ObdSimBind();
              obdSimBind.fromObdSimBindDTO(obdSimBindDTO);
              writer.save(obdSimBind);
            } else {
              obdSimBind.fromObdSimBindDTO(obdSimBindDTO);
              writer.update(obdSimBind);
            }


            OBDSimOperationLogDTO operationLogDTO = generateObdOperationLogDTO(obdSimBindDTO);
            OBDSimOperationLog operationLog = new OBDSimOperationLog();
            operationLog.fromDTO(operationLogDTO);
            writer.save(operationLog);
          }
        }

        writer.commit(status);
      } finally {
        writer.rollback(status);
      }

    }
  }

  private OBDSimOperationLogDTO generateObdOperationLogDTO(ObdSimBindDTO obdSimBindDTO) {
    if (obdSimBindDTO != null) {
      OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
      operationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
      OBDSimOperationType operationType = null;
      String content = null;
      if (StringUtils.isNotBlank(obdSimBindDTO.getImei()) && StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
        if (ObdMirrorType.MIRROR.equals(obdSimBindDTO.getObdMirrorType())) {
          operationType = OBDSimOperationType.COMBINE_MIRROR_OBD_SIM_IMPORT;
        } else {
          operationType = OBDSimOperationType.COMBINE_GSM_OBD_SIM_IMPORT;
        }
        content = obdSimBindDTO.generateCombineContent();
      } else if (StringUtils.isNotBlank(obdSimBindDTO.getImei())) {
        if (ObdMirrorType.MIRROR.equals(obdSimBindDTO.getObdMirrorType())) {
          operationType = OBDSimOperationType.SINGLE_MIRROR_OBD_IMPORT;
        } else {
          operationType = OBDSimOperationType.SINGLE_GSM_OBD_IMPORT;
        }
        content = obdSimBindDTO.generateSingleOBDContent();
      } else if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
        operationType = OBDSimOperationType.SINGLE_OBD_SIM_IMPORT;
        content = obdSimBindDTO.generateSingleOBDSimContent();
      }
      operationLogDTO.setContent(content);
      operationLogDTO.setOperationType(operationType);
      return operationLogDTO;
    }
    return null;
  }

  @Override
  public Result updateSingleObdSim(ObdSimBindDTO obdSimBindDTO) throws Exception {
    Result result = new Result();
    if (obdSimBindDTO != null) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        if (obdSimBindDTO.getObdId() != null && obdSimBindDTO.getSimId() != null) {
          result = validateAndUpdateCombineObd(obdSimBindDTO, writer);
        } else if (obdSimBindDTO.getObdId() != null) {
          result = validateAndUpdateSingleObd(obdSimBindDTO, writer);
        } else if (obdSimBindDTO.getSimId() != null) {
          result = validateAndUpdateSingleSim(obdSimBindDTO, writer);
        } else {
          result.setSuccess(false);
          result.setMsg(false, "您修改的OBD/后视镜，SIM卡不存在，请查询后再修改！");
        }

        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    return result;
  }

  private Result validateAndUpdateSingleSim(ObdSimBindDTO obdSimBindDTO, UserWriter writer) throws Exception {
    Result result = new Result();
    boolean isValidateSuccess = true;
    StringBuilder sb = new StringBuilder();

    if (StringUtils.isBlank(obdSimBindDTO.getSimNo())) {
      isValidateSuccess = false;
      sb.append("SIM卡编号为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getMobile())) {
      isValidateSuccess = false;
      sb.append("SIM卡手机号为空\r\n");
    }
    if (obdSimBindDTO.getUseDate() == null) {
      isValidateSuccess = false;
      sb.append("SIM卡开通年月为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getUseDateStr())) {
      isValidateSuccess = false;
      sb.append("SIM卡服务期为空\r\n");
    }

    if (!isValidateSuccess) {
      result.setMsg(false, sb.toString());
      return result;
    }


    ObdSim obdSim = writer.getById(ObdSim.class, obdSimBindDTO.getSimId());
    if (obdSim == null) {
      isValidateSuccess = false;
      sb.append("需要更新的SIM不存在\r\n");
    }
    if (obdSim != null && !obdSimBindDTO.getSimNo().equals(obdSim.getSimNo())) {
      Set<String> simNoSet = new HashSet<String>();
      simNoSet.add(obdSimBindDTO.getSimNo());
      List<ObdSim> checkSimNo = writer.getObdSimBySimNos(simNoSet);
      if (CollectionUtils.isNotEmpty(checkSimNo)) {
        isValidateSuccess = false;
        sb.append("SIM编号已经存在\r\n");
      }
    }
    if (obdSim != null && !obdSimBindDTO.getMobile().equals(obdSim.getMobile())) {
      Set<String> mobileSet = new HashSet<String>();
      mobileSet.add(obdSimBindDTO.getMobile());
      List<ObdSim> checkMobile = writer.getObdSimByMobiles(mobileSet);
      if (CollectionUtils.isNotEmpty(checkMobile)) {
        isValidateSuccess = false;
        sb.append("SIM手机号已经存在\r\n");
      }
    }
    if (!isValidateSuccess) {
      result.setMsg(false, sb.toString());
      return result;
    }


    boolean isObdSimNeedToUpdate = false;

    if (!NumberUtil.compareSame(obdSim.getUseDate(), obdSimBindDTO.getUseDate())) {
      isObdSimNeedToUpdate = true;
      obdSim.setUseDate(obdSimBindDTO.getUseDate());
    }
    if (!StringUtil.compareSame(obdSim.getSimNo(), obdSimBindDTO.getSimNo())) {
      isObdSimNeedToUpdate = true;
      obdSim.setSimNo(obdSimBindDTO.getSimNo());
    }
    if (!StringUtil.compareSame(obdSim.getMobile(), obdSimBindDTO.getMobile())) {
      isObdSimNeedToUpdate = true;
      obdSim.setMobile(obdSimBindDTO.getMobile());
    }

    if (!NumberUtil.compareSame(obdSim.getUsePeriod(), obdSimBindDTO.getUsePeriod())) {
      isObdSimNeedToUpdate = true;
      obdSim.setUsePeriod(obdSimBindDTO.getUsePeriod());
    }
    if (isObdSimNeedToUpdate) {
      writer.update(obdSim);
      ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
      writer.save(obdSimHistory);
      obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
    } else {
      ObdSimHistory obdSimHistory = writer.getLastObdSimHistoryBySimId(obdSim.getId());
      obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
    }
    if (isObdSimNeedToUpdate) {
      OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
      operationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
      operationLogDTO.setContent(obdSimBindDTO.generateSingleOBDSimContent());
      operationLogDTO.setOperationType(OBDSimOperationType.SINGLE_OBD_SIM_EDIT);
      OBDSimOperationLog operationLog = new OBDSimOperationLog();
      operationLog.fromDTO(operationLogDTO);
      writer.save(operationLog);
      result.setSuccess(true);
    } else {
      result.setMsg(false, "您没有更新的字段，请勿提交！");
    }

    return result;
  }

  private Result validateAndUpdateSingleObd(ObdSimBindDTO obdSimBindDTO, UserWriter writer) throws Exception {
    Result result = new Result();
    boolean isValidateSuccess = true;
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isBlank(obdSimBindDTO.getImei())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜 IMEI号为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getObdVersion())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜软件版本号为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getSpec())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜规格为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getColor())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜颜色为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getPack())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜包装为空\r\n");
    }
    if (obdSimBindDTO.getOpenShake() == null) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜震动报警为空\r\n");
    }
    if (obdSimBindDTO.getOpenCrash() == null) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜碰撞报警为空\r\n");
    }

    if (!isValidateSuccess) {
      result.setMsg(false, sb.toString());
      return result;
    }

    OBD obd = writer.getById(OBD.class, obdSimBindDTO.getObdId());
    if (obd == null) {
      isValidateSuccess = false;
      sb.append("需要更新的OBD/后视镜不存在\r\n");
    }

    if (obd == null || !obdSimBindDTO.getImei().equals(obd.getImei())) {
      Set<String> imeis = new HashSet<String>();
      imeis.add(obdSimBindDTO.getImei());
      List<OBD> obds = writer.getObdByImeisObdType(imeis, ObdType.GSM);
      if (CollectionUtils.isNotEmpty(obds)) {
        isValidateSuccess = false;
        sb.append("OBD/后视镜 IMEI 已存在\r\n");
      }
    }
    if (!isValidateSuccess) {
      result.setMsg(false, sb.toString());
      return result;
    }

    boolean isObdNeedToUpdate = false;
    if (!StringUtil.compareSame(obd.getImei(), obdSimBindDTO.getImei())) {
      isObdNeedToUpdate = true;
      obd.setImei(obdSimBindDTO.getImei());
    }
    if (!StringUtil.compareSame(obd.getObdVersion(), obdSimBindDTO.getObdVersion())) {
      isObdNeedToUpdate = true;
      obd.setObdVersion(obdSimBindDTO.getObdVersion());
    }
    if (!StringUtil.compareSame(obd.getSpec(), obdSimBindDTO.getSpec())) {
      isObdNeedToUpdate = true;
      obd.setSpec(obdSimBindDTO.getSpec());
    }
    if (!StringUtil.compareSame(obd.getColor(), obdSimBindDTO.getColor())) {
      isObdNeedToUpdate = true;
      obd.setColor(obdSimBindDTO.getColor());
    }
    if (!StringUtil.compareSame(obd.getPack(), obdSimBindDTO.getPack())) {
      isObdNeedToUpdate = true;
      obd.setPack(obdSimBindDTO.getPack());
    }
    if (!BGEnumUtil.compareSame(obd.getOpenCrash(), obdSimBindDTO.getOpenCrash())) {
      isObdNeedToUpdate = true;
      obd.setOpenCrash(obdSimBindDTO.getOpenCrash());
    }
    if (!BGEnumUtil.compareSame(obd.getOpenShake(), obdSimBindDTO.getOpenShake())) {
      isObdNeedToUpdate = true;
      obd.setOpenShake(obdSimBindDTO.getOpenShake());
    }
    if (isObdNeedToUpdate) {
      writer.update(obd);
      ObdHistory obdHistory = new ObdHistory(obd);
      writer.save(obdHistory);
      obdSimBindDTO.setObdHistoryId(obdHistory.getId());
    } else {
      ObdHistory obdHistory = writer.getLastObdHistoryByObdId(obd.getId());
      obdSimBindDTO.setObdHistoryId(obdHistory.getId());
    }


    if (isObdNeedToUpdate) {
      OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
      operationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
      operationLogDTO.setContent(obdSimBindDTO.generateSingleOBDContent());
      if (obd.getObdSimType().equals(OBDSimType.SINGLE_MIRROR_OBD)) {
        operationLogDTO.setOperationType(OBDSimOperationType.SINGLE_MIRROR_OBD_EDIT);
      } else {
        operationLogDTO.setOperationType(OBDSimOperationType.SINGLE_GSM_OBD_EDIT);
      }
      OBDSimOperationLog operationLog = new OBDSimOperationLog();
      operationLog.fromDTO(operationLogDTO);
      writer.save(operationLog);
      result.setSuccess(true);
    } else {
      result.setMsg(false, "您没有更新的字段，请勿提交！");
    }

    return result;
  }

  private Result validateAndUpdateCombineObd(ObdSimBindDTO obdSimBindDTO, UserWriter writer) throws Exception {
    Result result = new Result();
    boolean isValidateSuccess = true;
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isBlank(obdSimBindDTO.getImei())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜 IMEI号为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getObdVersion())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜软件版本号为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getSpec())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜规格为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getColor())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜颜色为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getPack())) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜包装为空\r\n");
    }
    if (obdSimBindDTO.getOpenShake() == null) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜震动报警为空\r\n");
    }
    if (obdSimBindDTO.getOpenCrash() == null) {
      isValidateSuccess = false;
      sb.append("OBD/后视镜碰撞报警为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getSimNo())) {
      isValidateSuccess = false;
      sb.append("SIM卡编号为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getMobile())) {
      isValidateSuccess = false;
      sb.append("SIM卡手机号为空\r\n");
    }
    if (obdSimBindDTO.getUseDate() == null) {
      isValidateSuccess = false;
      sb.append("SIM卡开通年月为空\r\n");
    }
    if (StringUtils.isBlank(obdSimBindDTO.getUseDateStr())) {
      isValidateSuccess = false;
      sb.append("SIM卡服务期为空\r\n");
    }

    if (!isValidateSuccess) {
      result.setMsg(false, sb.toString());
      return result;
    }

    OBD obd = writer.getById(OBD.class, obdSimBindDTO.getObdId());
    //Id + imei 都相同，防止前台串改数据
    if (obd == null || !obdSimBindDTO.getImei().equals(obd.getImei())) {
      isValidateSuccess = false;
      sb.append("需要更新的OBD/后视镜不存在\r\n");
    }
    ObdSim obdSim = writer.getById(ObdSim.class, obdSimBindDTO.getSimId());

    if (obdSim == null || !obdSimBindDTO.getSimNo().equals(obdSim.getSimNo())
        || !obdSimBindDTO.getMobile().equals(obdSim.getMobile())) {
      isValidateSuccess = false;
      sb.append("需要更新的SIM不存在\r\n");
    }
    if (!isValidateSuccess) {
      result.setMsg(false, sb.toString());
      return result;
    }

    boolean isObdNeedToUpdate = false;
    if (!StringUtil.compareSame(obd.getObdVersion(), obdSimBindDTO.getObdVersion())) {
      isObdNeedToUpdate = true;
      obd.setObdVersion(obdSimBindDTO.getObdVersion());
    }
    if (!StringUtil.compareSame(obd.getSpec(), obdSimBindDTO.getSpec())) {
      isObdNeedToUpdate = true;
      obd.setSpec(obdSimBindDTO.getSpec());
    }
    if (!StringUtil.compareSame(obd.getColor(), obdSimBindDTO.getColor())) {
      isObdNeedToUpdate = true;
      obd.setColor(obdSimBindDTO.getColor());
    }
    if (!StringUtil.compareSame(obd.getPack(), obdSimBindDTO.getPack())) {
      isObdNeedToUpdate = true;
      obd.setPack(obdSimBindDTO.getPack());
    }
    if (!BGEnumUtil.compareSame(obd.getOpenCrash(), obdSimBindDTO.getOpenCrash())) {
      isObdNeedToUpdate = true;
      obd.setOpenCrash(obdSimBindDTO.getOpenCrash());
    }
    if (!BGEnumUtil.compareSame(obd.getOpenShake(), obdSimBindDTO.getOpenShake())) {
      isObdNeedToUpdate = true;
      obd.setOpenShake(obdSimBindDTO.getOpenShake());
    }
    if (isObdNeedToUpdate) {
      writer.update(obd);
      ObdHistory obdHistory = new ObdHistory(obd);
      writer.save(obdHistory);
      obdSimBindDTO.setObdHistoryId(obdHistory.getId());
    } else {
      ObdHistory obdHistory = writer.getLastObdHistoryByObdId(obd.getId());
      obdSimBindDTO.setObdHistoryId(obdHistory.getId());
    }

    boolean isObdSimNeedToUpdate = false;

    if (!NumberUtil.compareSame(obdSim.getUseDate(), obdSimBindDTO.getUseDate())) {
      isObdSimNeedToUpdate = true;
      obdSim.setUseDate(obdSimBindDTO.getUseDate());
    }

    if (!NumberUtil.compareSame(obdSim.getUsePeriod(), obdSimBindDTO.getUsePeriod())) {
      isObdSimNeedToUpdate = true;
      obdSim.setUsePeriod(obdSimBindDTO.getUsePeriod());
    }
    if (isObdSimNeedToUpdate) {
      writer.update(obdSim);
      ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
      writer.save(obdSimHistory);
      obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
    } else {
      ObdSimHistory obdSimHistory = writer.getLastObdSimHistoryBySimId(obdSim.getId());
      obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
    }
    if (isObdNeedToUpdate || isObdSimNeedToUpdate) {
      OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
      operationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
      operationLogDTO.setContent(obdSimBindDTO.generateCombineContent());
      if (obd.getObdSimType().equals(OBDSimType.COMBINE_MIRROR_OBD_SIM)) {
        operationLogDTO.setOperationType(OBDSimOperationType.COMBINE_MIRROR_OBD_SIM_EDIT);
      } else {
        operationLogDTO.setOperationType(OBDSimOperationType.COMBINE_GSM_OBD_SIM_EDIT);
      }
      OBDSimOperationLog operationLog = new OBDSimOperationLog();
      operationLog.fromDTO(operationLogDTO);
      writer.save(operationLog);
      result.setSuccess(true);
    } else {
      result.setMsg(false, "您没有更新的字段，请勿提交！");
    }

    return result;
  }

  @Override
  public List<OBDSimOperationLogDTO> getObdSimOperationLogDTOs(OBDSimOperationLogDTOSearchCondition condition) {
    List<OBDSimOperationLogDTO> obdSimOperationLogDTOs = new ArrayList<OBDSimOperationLogDTO>();
    if (condition != null && (condition.getObdId() != null || condition.getSimId() != null)) {
      UserWriter writer = userDaoManager.getWriter();
      List<OBDSimOperationLog> obdSimOperationLogs = writer.getObdSimOperationLogs(condition);
      if (CollectionUtils.isNotEmpty(obdSimOperationLogs)) {
        for (OBDSimOperationLog operationLog : obdSimOperationLogs) {
          obdSimOperationLogDTOs.add(operationLog.toDTO());
        }
      }
    }
    return obdSimOperationLogDTOs;
  }

  @Override
  public int countObdSimOperationLogDTOs(OBDSimOperationLogDTOSearchCondition condition) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countObdSimOperationLogs(condition);
  }

  @Override
  public Result updateMultiObdSim(MultiObdSimUpdateDTO multiObdSimUpdateDTO) throws Exception {
    ObdSimBindDTO[] toUpdateObdSimBindDTOs = multiObdSimUpdateDTO.getToUpdateObdSimBindDTO();
    ObdSimBindDTO newObdSimBindDTO = multiObdSimUpdateDTO.getNewObdSimBindDTO();
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (newObdSimBindDTO != null && !ArrayUtils.isEmpty(toUpdateObdSimBindDTOs)) {
        for (ObdSimBindDTO obdSimBindDTO : toUpdateObdSimBindDTOs) {
          if (obdSimBindDTO != null) {
            OBD obd = null;
            ObdSim obdSim = null;
            boolean isObdUpdate = false;
            boolean isObdSimUpdate = false;
            if (obdSimBindDTO.getObdId() != null) {
              obd = writer.getById(OBD.class, obdSimBindDTO.getObdId());
            }
            if (obdSimBindDTO.getSimId() != null) {
              obdSim = writer.getById(ObdSim.class, obdSimBindDTO.getSimId());
            }
            if (obd != null) {
              if (StringUtils.isNotBlank(newObdSimBindDTO.getObdVersion())
                  && !StringUtil.compareSame(obd.getObdVersion(), newObdSimBindDTO.getObdVersion())) {
                isObdUpdate = true;
                obd.setObdVersion(newObdSimBindDTO.getObdVersion());
              }
              if (StringUtils.isNotBlank(newObdSimBindDTO.getSpec())
                  && !StringUtil.compareSame(obd.getSpec(), newObdSimBindDTO.getSpec())) {
                isObdUpdate = true;
                obd.setSpec(newObdSimBindDTO.getSpec());
              }
              if (StringUtils.isNotBlank(newObdSimBindDTO.getColor())
                  && !StringUtil.compareSame(obd.getColor(), newObdSimBindDTO.getColor())) {
                isObdUpdate = true;
                obd.setColor(newObdSimBindDTO.getColor());
              }
              if (StringUtils.isNotBlank(newObdSimBindDTO.getPack())
                  && !StringUtil.compareSame(obd.getPack(), newObdSimBindDTO.getPack())) {
                isObdUpdate = true;
                obd.setPack(newObdSimBindDTO.getPack());
              }
              if (newObdSimBindDTO.getOpenCrash() != null
                  && !BGEnumUtil.compareSame(obd.getOpenCrash(), newObdSimBindDTO.getOpenCrash())) {
                isObdUpdate = true;
                obd.setOpenCrash(newObdSimBindDTO.getOpenCrash());
              }
              if (newObdSimBindDTO.getOpenShake() != null
                  && !BGEnumUtil.compareSame(obd.getOpenShake(), newObdSimBindDTO.getOpenShake())) {
                isObdUpdate = true;
                obd.setOpenShake(newObdSimBindDTO.getOpenShake());
              }
              obdSimBindDTO.setObdDTO(obd.toDTO());
              if (isObdUpdate) {
                writer.update(obd);
                ObdHistory obdHistory = new ObdHistory(obd);
                writer.save(obdHistory);
                obdSimBindDTO.setObdHistoryId(obdHistory.getId());
              } else {
                ObdHistory obdHistory = writer.getLastObdHistoryByObdId(obd.getId());
                if (obdHistory == null) {
                  obdHistory = new ObdHistory(obd);
                  writer.save(obdHistory);
                }
                obdSimBindDTO.setObdHistoryId(obdHistory.getId());
              }
            }
            if (obdSim != null) {
              if (newObdSimBindDTO.getUseDate() != null && !StringUtil.compareSame(newObdSimBindDTO.getUseDate(), obdSim.getUseDate())) {
                isObdSimUpdate = true;
                obdSim.setUseDate(newObdSimBindDTO.getUseDate());
              }
              if (newObdSimBindDTO.getUsePeriod() != null && newObdSimBindDTO.getUsePeriod() > 0
                  && !NumberUtil.compareSame(newObdSimBindDTO.getUsePeriod(), obdSim.getUsePeriod())) {
                isObdSimUpdate = true;
                obdSim.setUsePeriod(newObdSimBindDTO.getUsePeriod());
              }
              obdSimBindDTO.setObdSimDTO(obdSim.toDTO());
              if (isObdSimUpdate) {
                writer.update(obdSim);
                ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
                writer.save(obdSimHistory);
                obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
              } else {
                ObdSimHistory obdSimHistory = writer.getLastObdSimHistoryBySimId(obdSim.getId());
                if (obdSimHistory == null) {
                  obdSimHistory = new ObdSimHistory(obdSim);
                  writer.save(obdSimHistory);
                }
                obdSimBindDTO.setSimHistoryId(obdSimHistory.getId());
              }
            }

            if (isObdSimUpdate || isObdUpdate) {
              OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
              operationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
              if (obd != null && obdSim != null) {
                operationLogDTO.setContent(obdSimBindDTO.generateCombineContent());
                operationLogDTO.setOperationType(OBDSimOperationType.COMBINE_GSM_OBD_SIM_EDIT);
              } else if (obdSim != null) {
                operationLogDTO.setContent(obdSimBindDTO.generateSingleOBDSimContent());
                operationLogDTO.setOperationType(OBDSimOperationType.SINGLE_OBD_SIM_EDIT);
              } else {
                operationLogDTO.setContent(obdSimBindDTO.generateSingleOBDContent());
                operationLogDTO.setOperationType(OBDSimOperationType.SINGLE_GSM_OBD_EDIT);
              }
              operationLogDTO.setShopId(newObdSimBindDTO.getOperateShopId());
              operationLogDTO.setUserName(newObdSimBindDTO.getOperateUserName());
              operationLogDTO.setUserId(newObdSimBindDTO.getOperateUserId());
              OBDSimOperationLog operationLog = new OBDSimOperationLog();
              operationLog.fromDTO(operationLogDTO);
              writer.save(operationLog);
            }
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return new Result();
  }

  @Override
  public Result splitObdSimBind(ObdSimBindDTO obdSimBindDTO) {
    Result result = new Result();
    boolean isValidate = true;
    StringBuilder message = new StringBuilder();
    if (obdSimBindDTO != null) {
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        OBD obd = null;
        if (obdSimBindDTO.getObdId() != null) {
          obd = writer.getById(OBD.class, obdSimBindDTO.getObdId());
        }
        ObdSim obdSim = null;
        if (obdSimBindDTO.getSimId() != null) {
          obdSim = writer.getById(ObdSim.class, obdSimBindDTO.getSimId());
        }
        ObdSimBind obdSimBind = null;
        if (obd != null && obdSim != null) {
          obdSimBind = writer.getObdSimBindByObdIdAndSimId(obd.getId(), obdSim.getId());
        }
        if (obdSimBind == null) {
          isValidate = false;
          message.append("需要拆分的OBD/后视镜 SIM Bind 未组装不允许拆分！");
        }
        if (isValidate && !OBDStatus.WAITING_OUT_STORAGE.equals(obd.getObdStatus())) {
          isValidate = false;
          if (obd.getObdStatus() == null) {
            message.append("需要拆分的OBD/后视镜状态为空，不允许拆分");
          } else {
            message.append("需要拆分的OBD/后视镜状态为【")
                .append(obd.getObdStatus().getName())
                .append("】不允许拆分");
          }
        }
        if (isValidate) {
          obd.setObdStatus(OBDStatus.UN_ASSEMBLE);
          //增加OBD和后视镜的区别拆分
          if (obd.getObdSimType().equals(OBDSimType.COMBINE_MIRROR_OBD_SIM)) {
            obd.setObdSimType(OBDSimType.SINGLE_MIRROR_OBD);
          } else {
            obd.setObdSimType(OBDSimType.SINGLE_GSM_OBD);
          }
          writer.update(obd);

          ObdHistory obdHistory = new ObdHistory(obd);
          writer.save(obdHistory);

          obdSim.setStatus(OBDStatus.UN_ASSEMBLE);
          obdSim.setObdSimType(OBDSimType.SINGLE_SIM);
          ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
          writer.save(obdSimHistory);

          obdSimBind.setStatus(ObdSimBindStatus.DISABLED);
          obdSimBind.setObdHistoryId(obdHistory.getId());
          obdSimBind.setSimHistoryId(obdSimHistory.getId());
          writer.update(obdSimBind);

          obdSimBindDTO.setObdDTO(obd.toDTO());
          obdSimBindDTO.setObdSimDTO(obdSim.toDTO());
          OBDSimOperationLogDTO obdSimOperationLogDTO = new OBDSimOperationLogDTO();
          obdSimOperationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
          obdSimOperationLogDTO.setOperationType(OBDSimOperationType.COMBINE_GSM_OBD_SIM_SPLIT);
          obdSimOperationLogDTO.setContent(obdSimBindDTO.generateCombineContent());
          OBDSimOperationLog operationLog = new OBDSimOperationLog();
          operationLog.fromDTO(obdSimOperationLogDTO);
          writer.save(operationLog);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    } else {
      isValidate = false;
      message.append("需要拆分的OBD不存在！");
    }
    result.setMsg(isValidate, message.toString());
    return result;
  }

  @Override
  public int countObdImeiSuggestion(ObdImeiSuggestion suggestion) {
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.countObdImeiSuggestion(suggestion);
    }
    return 0;
  }

  @Override
  public List<ObdSimBindDTO> getObdImeiSuggestion(ObdImeiSuggestion suggestion) {
    List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<String> obdImeis = writer.getObdImeiSuggestion(suggestion);
      if (CollectionUtils.isNotEmpty(obdImeis)) {
        for (String imei : obdImeis) {
          if (StringUtils.isNotBlank(imei)) {
            ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
            obdSimBindDTO.setImei(imei);
            obdSimBindDTOs.add(obdSimBindDTO);
          }
        }
      }
    }
    return obdSimBindDTOs;
  }

  @Override
  public int countObdVersionSuggestion(ObdVersionSuggestion suggestion) {
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.countObdVersionSuggestion(suggestion);
    }
    return 0;
  }

  @Override
  public List<ObdSimBindDTO> getObdVersionSuggestion(ObdVersionSuggestion suggestion) {
    List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<String> obdVersions = writer.getObdVersionSuggestion(suggestion);
      if (CollectionUtils.isNotEmpty(obdVersions)) {
        for (String obdVersion : obdVersions) {
          if (StringUtils.isNotBlank(obdVersion)) {
            ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
            obdSimBindDTO.setObdVersion(obdVersion);
            obdSimBindDTOs.add(obdSimBindDTO);
          }
        }
      }
    }
    return obdSimBindDTOs;
  }

  @Override
  public int countObdSimMobileSuggestion(SimMobileSuggestion suggestion) {
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.countObdSimMobileSuggestion(suggestion);
    }
    return 0;
  }

  @Override
  public List<ObdSimBindDTO> getObdSimMobileSuggestion(SimMobileSuggestion suggestion) {
    List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<String> mobiles = writer.getObdSimMobileSuggestion(suggestion);
      if (CollectionUtils.isNotEmpty(mobiles)) {
        for (String mobile : mobiles) {
          if (StringUtils.isNotBlank(mobile)) {
            ObdSimBindDTO obdSimBindDTO = new ObdSimBindDTO();
            obdSimBindDTO.setMobile(mobile);
            obdSimBindDTOs.add(obdSimBindDTO);
          }
        }
      }
    }
    return obdSimBindDTOs;
  }

  @Override
  public Result combineObdSim(ObdSimBindDTO obdSimBindDTO) throws Exception {
    Result result = new Result();
    boolean isValidate = true;
    StringBuilder message = new StringBuilder();
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      OBD obd = null;
      ObdSim obdSim = null;
      ObdSimBind obdSimBind = null;
      if (StringUtils.isNotBlank(obdSimBindDTO.getImei())) {
        obd = writer.getObdByImeiObdType(obdSimBindDTO.getImei(), null);
      }
      if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
        obdSim = writer.getObdSimByMobile(obdSimBindDTO.getMobile());
      }
      if (obd == null) {
        obd = writer.getObdByImeiObdType(obdSimBindDTO.getImei(),null);
        if (obd == null) {
          isValidate = false;
          message.append("需要组装的OBD/后视镜不存在，请重新选择！");
        }
      } else if (!OBDSimType.SINGLE_GSM_OBD.equals(obd.getObdSimType()) && !OBDSimType.SINGLE_MIRROR_OBD.equals(obd.getObdSimType())) {
        isValidate = false;
        message.append("需要组装的OBD/后视镜不是单品，请重新选择！");
      }
      if (obdSim == null) {
        isValidate = false;
        message.append("需要组装的SIM卡不存在，请重新选择！");
      } else if (!OBDSimType.SINGLE_SIM.equals(obdSim.getObdSimType())) {
        isValidate = false;
        message.append("需要组装的SIM卡不是单品，请重新选择！");
      }
      if (isValidate) {
        obdSimBind = writer.getObdSimBindByObdIdAndSimId(obd.getId(), obdSim.getId());

        obd.setObdStatus(OBDStatus.WAITING_OUT_STORAGE);
        if (obd.getObdSimType().equals(OBDSimType.SINGLE_MIRROR_OBD)) {
          obd.setObdSimType(OBDSimType.COMBINE_MIRROR_OBD_SIM);
        } else {
          obd.setObdSimType(OBDSimType.COMBINE_GSM_OBD_SIM);
        }
        writer.update(obd);

        ObdHistory obdHistory = new ObdHistory(obd);
        writer.save(obdHistory);

        obdSim.setStatus(OBDStatus.WAITING_OUT_STORAGE);
        if (obd.getObdSimType().equals(OBDSimType.SINGLE_MIRROR_OBD)) {
          obdSim.setObdSimType(OBDSimType.COMBINE_MIRROR_OBD_SIM);
        } else {
          obdSim.setObdSimType(OBDSimType.COMBINE_GSM_OBD_SIM);
        }
        ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
        writer.save(obdSimHistory);

        obdSimBindDTO.setObdDTO(obd.toDTO());
        obdSimBindDTO.setObdSimDTO(obdSim.toDTO());

        if (obdSimBind == null) {
          obdSimBind = new ObdSimBind();
          obdSimBind.fromObdSimBindDTO(obdSimBindDTO);
          writer.save(obdSimBind);
        } else {
          obdSimBind.fromObdSimBindDTO(obdSimBindDTO);
          writer.update(obdSimBind);
        }
        obdSimBindDTO.setObdDTO(obd.toDTO());
        obdSimBindDTO.setObdSimDTO(obdSim.toDTO());
        OBDSimOperationLogDTO obdSimOperationLogDTO = new OBDSimOperationLogDTO();
        obdSimOperationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
        obdSimOperationLogDTO.setOperationType(OBDSimOperationType.GSM_OBD_SIM_PACKAGE);
        obdSimOperationLogDTO.setContent(obdSimBindDTO.generateCombineContent());
        OBDSimOperationLog operationLog = new OBDSimOperationLog();
        operationLog.fromDTO(obdSimOperationLogDTO);
        writer.save(operationLog);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    result.setMsg(isValidate, message.toString());
    return result;
  }

  @Override
  public Result deleteObdSim(ObdSimBindDTO obdSimBindDTO) throws Exception {
    Result result = new Result();
    boolean isValidateObd = true;
    boolean isValidateSim = true;
    StringBuilder message = new StringBuilder();
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (StringUtils.isNotBlank(obdSimBindDTO.getImei())) {
        OBD obd = writer.getObdByImeiObdType(obdSimBindDTO.getImei(),null);
        if (obd == null) {
          obd = writer.getObdByImeiObdType(obdSimBindDTO.getImei(),null);
          if (obd == null) {
            isValidateObd = false;
            message.append("需要删除的产品不存在，请重新选择！");
          }
        } else if (!OBDSimType.SINGLE_GSM_OBD.equals(obd.getObdSimType()) && !OBDSimType.SINGLE_MIRROR_OBD.equals(obd.getObdSimType())) {
          isValidateObd = false;
          message.append("需要删除的OBD不是单品，请重新选择！");
        }
        if (isValidateObd) {
          obd.setObdStatus(OBDStatus.DISABLED);
          writer.update(obd);
          ObdHistory obdHistory = new ObdHistory(obd);
          writer.save(obdHistory);
          obdSimBindDTO.setObdDTO(obd.toDTO());
          obdSimBindDTO.setObdDTO(obd.toDTO());
          OBDSimOperationLogDTO obdSimOperationLogDTO = new OBDSimOperationLogDTO();
          obdSimOperationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
          obdSimOperationLogDTO.setOperationType(OBDSimOperationType.SINGLE_GSM_OBD_DELETE);
          obdSimOperationLogDTO.setContent(obdSimBindDTO.generateSingleOBDContent());
          OBDSimOperationLog operationLog = new OBDSimOperationLog();
          operationLog.fromDTO(obdSimOperationLogDTO);
          writer.save(operationLog);
        }
      }


      if (StringUtils.isNotBlank(obdSimBindDTO.getMobile())) {
        ObdSim obdSim = writer.getObdSimByMobile(obdSimBindDTO.getMobile());
        if (obdSim == null) {
          isValidateSim = false;
          message.append("需要删除的SIM卡不存在，请重新选择！");
        } else if (!OBDSimType.SINGLE_SIM.equals(obdSim.getObdSimType())) {
          isValidateSim = false;
          message.append("需要删除的SIM卡不是单品，请重新选择！");
        }
        if (isValidateSim) {
          obdSim.setStatus(OBDStatus.DISABLED);
          ObdSimHistory obdSimHistory = new ObdSimHistory(obdSim);
          writer.save(obdSimHistory);
          obdSimBindDTO.setObdSimDTO(obdSim.toDTO());
          obdSimBindDTO.setObdSimDTO(obdSim.toDTO());
          OBDSimOperationLogDTO obdSimOperationLogDTO = new OBDSimOperationLogDTO();
          obdSimOperationLogDTO.fromObdSimBindDTO(obdSimBindDTO);
          obdSimOperationLogDTO.setOperationType(OBDSimOperationType.SINGLE_OBD_SIM_DELETE);
          obdSimOperationLogDTO.setContent(obdSimBindDTO.generateSingleOBDSimContent());
          OBDSimOperationLog operationLog = new OBDSimOperationLog();
          operationLog.fromDTO(obdSimOperationLogDTO);
          writer.save(operationLog);
        }
      }


      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    result.setMsg(isValidateSim && isValidateObd, message.toString());
    return result;
  }

  @Override
  public int countObdOutStorageShopNameSuggestion(ShopNameSuggestion suggestion) {
    if (suggestion != null) {
      ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
      ConfigWriter writer = configDaoManager.getWriter();
      return writer.countObdSimMobileSuggestion(suggestion);
    }
    return 0;
  }

  @Override
  public List<ShopDTO> getObdOutStorageShopNameSuggestion(ShopNameSuggestion suggestion) {
    List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>();
    if (suggestion != null) {
      ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
      ConfigWriter writer = configDaoManager.getWriter();
      List<Shop> shops = writer.getObdOutStorageShopNameSuggestion(suggestion);
      if (CollectionUtils.isNotEmpty(shops)) {
        for (Shop shop : shops) {
          if (shop != null) {
            ShopDTO shopDTO = shop.toDTO();
            shopDTOs.add(shopDTO);
          }
        }
      }
    }
    return shopDTOs;
  }

  @Override
  public int countAgentNameSuggestion(AgentNameSuggestion suggestion) {
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.countAgentNameSuggestion(suggestion);
    }
    return 0;
  }

  @Override
  public List<UserDTO> getAgentNameSuggestion(AgentNameSuggestion suggestion) {
    List<UserDTO> userDTOs = new ArrayList<UserDTO>();
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<User> users = writer.getAgentNameSuggestion(suggestion);
      if (CollectionUtils.isNotEmpty(users)) {
        for (User user : users) {
          userDTOs.add(user.toDTO());
        }
      }
    }
    return userDTOs;
  }

  @Override
  public int countStaffNameSuggestion(AgentNameSuggestion suggestion) {
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.countStaffNameSuggestion(suggestion);
    }
    return 0;
  }

  @Override
  public List<UserDTO> getStaffNameSuggestion(AgentNameSuggestion suggestion) {
    List<UserDTO> userDTOs = new ArrayList<UserDTO>();
    if (suggestion != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<User> users = writer.getStaffNameSuggestion(suggestion);
      if (CollectionUtils.isNotEmpty(users)) {
        for (User user : users) {
          userDTOs.add(user.toDTO());
        }
      }
    }
    return userDTOs;
  }

  @Override
  public Result obdSimOutStorage(ObdSimOutStorageDTO outStorageDTO) {
    Result result = new Result();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      boolean isValidate = true;
      StringBuilder message = new StringBuilder();
      Set<String> imeiSet = new HashSet<String>();
      Map<String, OBD> imeiObdMap = new HashMap<String, OBD>();
      ShopDTO outStorageTargetShop = null;
      UserDTO outStorageTargetUser = null;
      if (outStorageDTO != null && !ArrayUtils.isEmpty(outStorageDTO.getOutStorageImeis())) {
        for (String imei : outStorageDTO.getOutStorageImeis()) {
          if (StringUtils.isNotBlank(imei)) {
            imeiSet.add(imei);
          }
        }
      }
      if (CollectionUtils.isEmpty(imeiSet)) {
        isValidate = false;
        message.append("请选择需要出库的设备号！\r\n");
      }
      if (isValidate) {
        imeiObdMap = writer.getimeiObdMapByImeisObdType(imeiSet, null);
        for (String imei : imeiSet) {
          OBD obd = imeiObdMap.get(imei);
          if (obd == null) {
            isValidate = false;
            message.append(imei).append("对应的OBD或后视镜不存在!\r\n");
          } else {
            if (!OBDSimType.COMBINE_GSM_OBD_SIM.equals(obd.getObdSimType())
              && !OBDSimType.COMBINE_GSM_POBD_SIM.equals(obd.getObdSimType())
              && !OBDSimType.COMBINE_MIRROR_OBD_SIM.equals(obd.getObdSimType())
              && !OBDSimType.COMBINE_GSM_OBD_SSIM.equals(obd.getObdSimType())) {
              isValidate = false;
              message.append(imei).append("对应的OBD或后视镜不是成品!\r\n");
            } else if (!OBDStatus.WAITING_OUT_STORAGE.equals(obd.getObdStatus())) {
              isValidate = false;
              message.append(imei).append("对应的OBD或后视镜不是待出库状态!\r\n");
            }
          }
        }
      }
      Set<ObdSimOwnerType> allowTarget = new HashSet<ObdSimOwnerType>();
      allowTarget.add(ObdSimOwnerType.AGENT);
      allowTarget.add(ObdSimOwnerType.SHOP);
      allowTarget.add(ObdSimOwnerType.EMPLOYEE);
      if (outStorageDTO == null || !allowTarget.contains(outStorageDTO.getOutStorageType())) {
        isValidate = false;
        message.append("您选择的出库对象不存在，请重新选择!\r\n");
      } else {
        if (ObdSimOwnerType.SHOP.equals(outStorageDTO.getOutStorageType())) {
          if (outStorageDTO.getOutStorageTargetId() != null)
            outStorageTargetShop = configService.getShopById(outStorageDTO.getOutStorageTargetId());
          if (outStorageTargetShop == null) {
            isValidate = false;
            message.append("您选择的出库对象不存在，请重新选择!\r\n");
          } else {
            outStorageDTO.setOutStorageTargetName(outStorageTargetShop.getName());
          }
        } else {
          if (outStorageDTO.getOutStorageTargetId() != null)
            outStorageTargetUser = writer.getUserDTO(outStorageDTO.getOperationShopId(), outStorageDTO.getOutStorageTargetId());
          if (outStorageTargetUser == null) {
            isValidate = false;
            message.append("您选择的出库对象不存在，请重新选择!\r\n");
          } else {
            outStorageDTO.setOutStorageTargetName(outStorageTargetUser.getName());
          }
        }
      }

      if (!isValidate) {
        result.setMsg(false, message.toString());
        return result;
      }

      if (isValidate) {
        Set<Long> obdIds = new HashSet<Long>();
        Set<Long> obdSimIds = new HashSet<Long>();
        Map<Long, ObdSimBind> obdIdObdSimBindMap = new HashMap<Long, ObdSimBind>();
        Map<Long, ObdSim> simIdObdSimMap = new HashMap<Long, ObdSim>();
        for (OBD obd : imeiObdMap.values()) {
          if (obd != null && obd.getId() != null) {
            obdIds.add(obd.getId());
          }
        }
        obdIdObdSimBindMap = writer.getObdSimBindMapByObdIds(obdIds);
        if (MapUtils.isNotEmpty(obdIdObdSimBindMap)) {
          for (ObdSimBind obdSimBind : obdIdObdSimBindMap.values()) {
            if (obdSimBind != null && obdSimBind.getSimId() != null) {
              obdSimIds.add(obdSimBind.getSimId());
            }
          }
        }
        simIdObdSimMap = writer.getIdObdSimMapByIds(obdSimIds);

        for (OBD obd : imeiObdMap.values()) {
          ObdSimBind obdSimBind = obdIdObdSimBindMap.get(obd.getId());
          ObdSim obdSim = null;
          ObdSimHistory obdSimHistory = null;
          if (obdSimBind != null && obdSimBind.getSimId() != null) {
            obdSim = simIdObdSimMap.get(obdSimBind.getSimId());
          }

          if (ObdSimOwnerType.SHOP.equals(outStorageDTO.getOutStorageType())) {
            obd.setObdStatus(OBDStatus.ON_SELL);
            obd.setSellShopId(outStorageDTO.getOutStorageTargetId());
            obd.setStorageTime(outStorageDTO.getOutStorageDate());
            obd.setOwnerType(ObdSimOwnerType.SHOP);
            obd.setOwnerId(outStorageDTO.getOutStorageTargetId());
            obd.setOwnerName(outStorageDTO.getOutStorageTargetName());
            writer.update(obd);
            ObdHistory obdHistory = new ObdHistory(obd);
            writer.save(obdHistory);
            if (obdSim != null) {
              obdSim.setStatus(OBDStatus.ON_SELL);
              obdSim.setOwnerType(ObdSimOwnerType.SHOP);
              obdSim.setOwnerId(outStorageDTO.getOutStorageTargetId());
              obdSim.setOwnerName(outStorageDTO.getOutStorageTargetName());
              writer.update(obdSim);
              obdSimHistory = new ObdSimHistory(obdSim);
              writer.save(obdSimHistory);
            }
            OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
            operationLogDTO.fromObdSimOutStorageDTO(outStorageDTO);
            operationLogDTO.setObdId(obd.getId());
            operationLogDTO.setObdHistoryId(obdHistory.getId());
            if (obdSim != null) {
              operationLogDTO.setSimId(obdSim.getId());
            }
            if (obdSimHistory != null) {
              operationLogDTO.setSimHistoryId(obdSimHistory.getId());
            }
            operationLogDTO.setOperationType(OBDSimOperationType.SELL_TO_SHOP);
            StringBuilder content = new StringBuilder();
            content.append("销售至店铺");
            if (outStorageTargetShop != null && StringUtils.isNotBlank(outStorageTargetShop.getName())) {
              content.append("【").append(outStorageTargetShop.getName()).append("】");
            }
            operationLogDTO.setContent(content.toString());
            OBDSimOperationLog operationLog = new OBDSimOperationLog();
            operationLog.fromDTO(operationLogDTO);
            writer.save(operationLog);
          } else if (ObdSimOwnerType.AGENT.equals(outStorageDTO.getOutStorageType())) {
            obd.setObdStatus(OBDStatus.AGENT);
            obd.setAgentId(outStorageDTO.getOutStorageTargetId());
            obd.setStorageTime(outStorageDTO.getOutStorageDate());
            obd.setOwnerType(ObdSimOwnerType.AGENT);
            obd.setOwnerId(outStorageDTO.getOutStorageTargetId());
            obd.setOwnerName(outStorageDTO.getOutStorageTargetName());
            writer.update(obd);
            ObdHistory obdHistory = new ObdHistory(obd);
            writer.save(obdHistory);
            if (obdSim != null) {
              obdSim.setStatus(OBDStatus.AGENT);
              obdSim.setAgentId(outStorageDTO.getOutStorageTargetId());
              obdSim.setOwnerType(ObdSimOwnerType.AGENT);
              obdSim.setOwnerId(outStorageDTO.getOutStorageTargetId());
              obdSim.setOwnerName(outStorageDTO.getOutStorageTargetName());
              writer.update(obdSim);
              obdSimHistory = new ObdSimHistory(obdSim);
              writer.save(obdSimHistory);
            }
            OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
            operationLogDTO.fromObdSimOutStorageDTO(outStorageDTO);
            if (obdSim != null) {
              operationLogDTO.setSimId(obdSim.getId());
            }
            if (obdSimHistory != null) {
              operationLogDTO.setSimHistoryId(obdSimHistory.getId());
            }
            operationLogDTO.setObdId(obd.getId());
            operationLogDTO.setObdHistoryId(obdHistory.getId());
            operationLogDTO.setOperationType(OBDSimOperationType.AGENT_OUT_STORAGE);
            StringBuilder content = new StringBuilder();
            if (outStorageTargetUser != null && StringUtils.isNotBlank(outStorageTargetUser.getName())) {
              content.append(outStorageTargetUser.getName());
            }
            content.append("代理出库");
            operationLogDTO.setContent(content.toString());
            OBDSimOperationLog operationLog = new OBDSimOperationLog();
            operationLog.fromDTO(operationLogDTO);
            writer.save(operationLog);
          } else {
            obd.setObdStatus(OBDStatus.PICKED);
            obd.setStorageTime(outStorageDTO.getOutStorageDate());
            obd.setSellerId(outStorageDTO.getOutStorageTargetId());
            obd.setOwnerType(ObdSimOwnerType.EMPLOYEE);
            obd.setOwnerId(outStorageDTO.getOutStorageTargetId());
            obd.setOwnerName(outStorageDTO.getOutStorageTargetName());
            writer.update(obd);
            ObdHistory obdHistory = new ObdHistory(obd);
            writer.save(obdHistory);
            if (obdSim != null) {
              obdSim.setStatus(OBDStatus.PICKED);
              obdSim.setSellerId(outStorageDTO.getOutStorageTargetId());
              obdSim.setOwnerType(ObdSimOwnerType.EMPLOYEE);
              obdSim.setOwnerId(outStorageDTO.getOutStorageTargetId());
              obdSim.setOwnerName(outStorageDTO.getOutStorageTargetName());
              writer.update(obdSim);
              obdSimHistory = new ObdSimHistory(obdSim);
              writer.save(obdSimHistory);
            }
            OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
            operationLogDTO.fromObdSimOutStorageDTO(outStorageDTO);
            if (obdSim != null) {
              operationLogDTO.setSimId(obdSim.getId());
            }
            if (obdSimHistory != null) {
              operationLogDTO.setSimHistoryId(obdSimHistory.getId());
            }
            operationLogDTO.setObdId(obd.getId());
            operationLogDTO.setObdHistoryId(obdHistory.getId());
            operationLogDTO.setOperationType(OBDSimOperationType.EMPLOYEE_OUT_STORAGE);
            StringBuilder content = new StringBuilder();
            if (outStorageTargetUser != null && StringUtils.isNotBlank(outStorageTargetUser.getName())) {
              content.append(outStorageTargetUser.getName());
            }
            content.append("领出出库");
            operationLogDTO.setContent(content.toString());
            OBDSimOperationLog operationLog = new OBDSimOperationLog();
            operationLog.fromDTO(operationLogDTO);
            writer.save(operationLog);
          }
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public Result obdSimSell(ObdSimOutStorageDTO outStorageDTO) {
    Result result = new Result();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      boolean isValidate = true;
      StringBuilder message = new StringBuilder();
      Set<String> imeiSet = new HashSet<String>();
      Map<String, OBD> imeiObdMap = new HashMap<String, OBD>();
      ShopDTO outStorageTargetShop = null;
      UserDTO outStorageTargetUser = null;
      if (outStorageDTO != null && !ArrayUtils.isEmpty(outStorageDTO.getOutStorageImeis())) {
        for (String imei : outStorageDTO.getOutStorageImeis()) {
          if (StringUtils.isNotBlank(imei)) {
            imeiSet.add(imei);
          }
        }
      }
      if (CollectionUtils.isEmpty(imeiSet)) {
        isValidate = false;
        message.append("请选择需要销售的设备号！\r\n");
      }
      if (isValidate) {
        imeiObdMap = writer.getimeiObdMapByImeisObdType(imeiSet,null);
        for (String imei : imeiSet) {
          OBD obd = imeiObdMap.get(imei);
          if (obd == null) {
            isValidate = false;
            message.append(imei).append("对应的OBD或后视镜不存在!\r\n");
          } else {
            if (!OBDSimType.COMBINE_GSM_OBD_SIM.equals(obd.getObdSimType()) && !OBDSimType.COMBINE_MIRROR_OBD_SIM.equals(obd.getObdSimType())) {
              isValidate = false;
              message.append(imei).append("对应的OBD或后视镜不是成品!\r\n");
            } else if (!(OBDStatus.AGENT.equals(obd.getObdStatus()) || OBDStatus.PICKED.equals(obd.getObdStatus()))) {
              isValidate = false;
              message.append(imei).append("对应的OBD或后视镜不是已代理或者已领出状态!\r\n");
            }
          }
        }
      }
      Set<ObdSimOwnerType> allowTarget = new HashSet<ObdSimOwnerType>();
      allowTarget.add(ObdSimOwnerType.AGENT);
      allowTarget.add(ObdSimOwnerType.SHOP);
      allowTarget.add(ObdSimOwnerType.EMPLOYEE);

      if (ObdSimOwnerType.SHOP.equals(outStorageDTO.getOutStorageType())) {
        if (outStorageDTO.getOutStorageTargetId() != null)
          outStorageTargetShop = configService.getShopById(outStorageDTO.getOutStorageTargetId());
        if (outStorageTargetShop == null) {
          isValidate = false;
          message.append("您选择的销售店铺不存在，请重新选择!\r\n");
        } else {
          outStorageDTO.setOutStorageTargetName(outStorageTargetShop.getName());
        }
      }

      if (!isValidate) {
        result.setMsg(false, message.toString());
        return result;
      }

      if (isValidate) {
        Set<Long> obdIds = new HashSet<Long>();
        Set<Long> obdSimIds = new HashSet<Long>();
        Map<Long, ObdSimBind> obdIdObdSimBindMap = new HashMap<Long, ObdSimBind>();
        Map<Long, ObdSim> simIdObdSimMap = new HashMap<Long, ObdSim>();
        for (OBD obd : imeiObdMap.values()) {
          if (obd != null && obd.getId() != null) {
            obdIds.add(obd.getId());
          }
        }
        obdIdObdSimBindMap = writer.getObdSimBindMapByObdIds(obdIds);
        if (MapUtils.isNotEmpty(obdIdObdSimBindMap)) {
          for (ObdSimBind obdSimBind : obdIdObdSimBindMap.values()) {
            if (obdSimBind != null && obdSimBind.getSimId() != null) {
              obdSimIds.add(obdSimBind.getSimId());
            }
          }
        }
        simIdObdSimMap = writer.getIdObdSimMapByIds(obdSimIds);

        for (OBD obd : imeiObdMap.values()) {
          ObdSimBind obdSimBind = obdIdObdSimBindMap.get(obd.getId());
          ObdSim obdSim = null;
          ObdSimHistory obdSimHistory = null;
          if (obdSimBind != null && obdSimBind.getSimId() != null) {
            obdSim = simIdObdSimMap.get(obdSimBind.getSimId());
          }

          obd.setObdStatus(OBDStatus.ON_SELL);
          obd.setSellShopId(outStorageDTO.getOutStorageTargetId());
          obd.setStorageTime(outStorageDTO.getOutStorageDate());
          obd.setOwnerType(ObdSimOwnerType.SHOP);
          obd.setOwnerId(outStorageDTO.getOutStorageTargetId());
          obd.setOwnerName(outStorageDTO.getOutStorageTargetName());
          writer.update(obd);
          ObdHistory obdHistory = new ObdHistory(obd);
          writer.save(obdHistory);
          if (obdSim != null) {
            obdSim.setStatus(OBDStatus.ON_SELL);
            obdSim.setOwnerType(ObdSimOwnerType.SHOP);
            obdSim.setOwnerId(outStorageDTO.getOutStorageTargetId());
            obdSim.setOwnerName(outStorageDTO.getOutStorageTargetName());
            writer.update(obdSim);
            obdSimHistory = new ObdSimHistory(obdSim);
            writer.save(obdSimHistory);
          }
          OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
          operationLogDTO.fromObdSimOutStorageDTO(outStorageDTO);
          operationLogDTO.setObdId(obd.getId());
          operationLogDTO.setObdHistoryId(obdHistory.getId());
          if (obdSim != null) {
            operationLogDTO.setSimId(obdSim.getId());
          }
          if (obdSimHistory != null) {
            operationLogDTO.setSimHistoryId(obdSimHistory.getId());
          }
          operationLogDTO.setOperationType(OBDSimOperationType.SELL_TO_SHOP);
          StringBuilder content = new StringBuilder();
          content.append("销售至店铺");
          if (outStorageTargetShop != null && StringUtils.isNotBlank(outStorageTargetShop.getName())) {
            content.append("【").append(outStorageTargetShop.getName()).append("】");
          }
          operationLogDTO.setContent(content.toString());
          OBDSimOperationLog operationLog = new OBDSimOperationLog();
          operationLog.fromDTO(operationLogDTO);
          writer.save(operationLog);

        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public Result obdSimReturn(ObdSimReturnDTO obdSimReturnDTO) {
    Result result = new Result();
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      boolean isValidate = true;
      StringBuilder message = new StringBuilder();
      Set<String> imeiSet = new HashSet<String>();
      Map<String, OBD> imeiObdMap = new HashMap<String, OBD>();
      if (obdSimReturnDTO != null && StringUtils.isNotBlank(obdSimReturnDTO.getReturnImei())) {
        imeiSet.add(obdSimReturnDTO.getReturnImei());
      }
      if (CollectionUtils.isEmpty(imeiSet)) {
        isValidate = false;
        message.append("请选择需要退货的设备号！\r\n");
      }
      if (isValidate) {
        imeiObdMap = writer.getimeiObdMapByImeisObdType(imeiSet, null);
        for (String imei : imeiSet) {
          OBD obd = imeiObdMap.get(imei);
          if (obd == null) {
            isValidate = false;
            message.append(imei).append("对应的OBD或后视镜不存在!\r\n");
          } else {
            if (!OBDSimType.COMBINE_GSM_OBD_SIM.equals(obd.getObdSimType()) && !OBDSimType.COMBINE_MIRROR_OBD_SIM.equals(obd.getObdSimType())) {
              isValidate = false;
              message.append(imei).append("对应的OBD或后视镜不是成品!\r\n");
            } else if (!OBDStatus.returnableStatusSet.contains(obd.getObdStatus())) {
              isValidate = false;
              message.append(imei).append("对应的OBD或后视镜不是可归还状态!\r\n");
            }
          }
        }
      }

      if (!isValidate) {
        result.setMsg(false, message.toString());
        return result;
      }

      if (isValidate) {
        Set<Long> obdIds = new HashSet<Long>();
        Set<Long> obdSimIds = new HashSet<Long>();
        Map<Long, ObdSimBind> obdIdObdSimBindMap = new HashMap<Long, ObdSimBind>();
        Map<Long, ObdSim> simIdObdSimMap = new HashMap<Long, ObdSim>();
        for (OBD obd : imeiObdMap.values()) {
          if (obd != null && obd.getId() != null) {
            obdIds.add(obd.getId());
          }
        }
        obdIdObdSimBindMap = writer.getObdSimBindMapByObdIds(obdIds);
        if (MapUtils.isNotEmpty(obdIdObdSimBindMap)) {
          for (ObdSimBind obdSimBind : obdIdObdSimBindMap.values()) {
            if (obdSimBind != null && obdSimBind.getSimId() != null) {
              obdSimIds.add(obdSimBind.getSimId());
            }
          }
        }
        simIdObdSimMap = writer.getIdObdSimMapByIds(obdSimIds);

        for (OBD obd : imeiObdMap.values()) {
          ObdSimBind obdSimBind = obdIdObdSimBindMap.get(obd.getId());
          ObdSim obdSim = null;
          ObdSimHistory obdSimHistory = null;
          if (obdSimBind != null && obdSimBind.getSimId() != null) {
            obdSim = simIdObdSimMap.get(obdSimBind.getSimId());
          }

          obd.setObdStatus(OBDStatus.WAITING_OUT_STORAGE);
          obd.setOwnerType(ObdSimOwnerType.STORAGE);
          obd.setOwnerId(obdSimReturnDTO.getOperationUserId());
          obd.setOwnerName(obdSimReturnDTO.getOperationName());
          obd.setSellShopId(null);
          obd.setSellTime(null);
          obd.setAgentId(null);
          obd.setSellerId(null);
          obd.setStorageTime(null);
          writer.update(obd);
          ObdHistory obdHistory = new ObdHistory(obd);
          writer.save(obdHistory);
          if (obdSim != null) {
            obdSim.setStatus(OBDStatus.WAITING_OUT_STORAGE);
            obdSim.setOwnerType(ObdSimOwnerType.STORAGE);
            obdSim.setOwnerId(obdSimReturnDTO.getOperationUserId());
            obdSim.setOwnerName(obdSimReturnDTO.getOperationName());
            writer.update(obdSim);
            obdSimHistory = new ObdSimHistory(obdSim);
            writer.save(obdSimHistory);
          }
          OBDSimOperationLogDTO operationLogDTO = new OBDSimOperationLogDTO();
          operationLogDTO.fromObdSimReturnDTO(obdSimReturnDTO);
          operationLogDTO.setObdId(obd.getId());
          operationLogDTO.setObdHistoryId(obdHistory.getId());
          if (obdSim != null) {
            operationLogDTO.setSimId(obdSim.getId());
          }
          if (obdSimHistory != null) {
            operationLogDTO.setSimHistoryId(obdSimHistory.getId());
          }
          operationLogDTO.setOperationType(OBDSimOperationType.RETURN_STORAGE);
          StringBuilder content = new StringBuilder();
          content.append("归还成品");
          String returnMsg = obdSimReturnDTO.getReturnMsgEnum() == null ? obdSimReturnDTO.getReturnMsgStr() : obdSimReturnDTO.getReturnMsgEnum().getName();

          if (StringUtils.isNotBlank(returnMsg)) {
            content.append("，原因：").append(returnMsg);
          }
          operationLogDTO.setContent(content.toString());
          OBDSimOperationLog operationLog = new OBDSimOperationLog();
          operationLog.fromDTO(operationLogDTO);
          writer.save(operationLog);

        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public ObdDTO getObdDTOById(Long obdId) {
    if (obdId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    OBD obd = CollectionUtil.getFirst(writer.getObdById(obdId));
    return obd == null ? null : obd.toDTO();
  }

  @Override
  public ObdSimDTO getObdSimDTOById(Long simId) {
    if (simId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    ObdSim obdSim = writer.getById(ObdSim.class, simId);
    return obdSim == null ? null : obdSim.toDTO();
  }

  @Override
  public ObdDTO getObdByImei(String imei) {
    if (StringUtil.isEmpty(imei)) return null;
    UserWriter writer = userDaoManager.getWriter();
    OBD obd = writer.getObdByImei(imei);
    return obd == null ? null : obd.toDTO();
  }

}