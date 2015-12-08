package com.bcgogo.user.service.app;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.common.Pager;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.app.*;
import com.bcgogo.etl.model.GsmPoint;
import com.bcgogo.etl.model.GsmVehicleInfo;
import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.etl.service.IGsmPointService;
import com.bcgogo.etl.service.IGsmVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.Coordinate;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.*;
import com.bcgogo.user.service.IVehicleService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-13
 * Time: 上午9:15
 */
@Component
public class DriveLogService implements IDriveLogService {

  @Autowired
  private UserDaoManager userDaoManager;
  private static final Logger LOG = LoggerFactory.getLogger(DriveLogService.class);

  @Override
  public ApiResponse validateSaveDriveLog(DriveLogDTO driveLogDTO) {
    if (driveLogDTO == null) {
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_SAVED_FAIL, ValidateMsg.DRIVE_LOG_EMPTY);
    }
    if (StringUtils.isEmpty(driveLogDTO.getAppDriveLogId())) {
      return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_SAVED_FAIL, ValidateMsg.DRIVE_LOG_APP_ID_EMPTY);
    }
    return MessageCode.toApiResponse(MessageCode.DRIVE_LOG_SAVED_SUCCESS);
  }

  @Override
  public ApiResponse handleSaveDriveLog(DriveLogDTO driveLogDTO, boolean isUpdatePlaceNote) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      DriveLog driveLog = null;

      if (StringUtils.isNotBlank(driveLogDTO.getAppDriveLogId())) {
        driveLog = writer.getDriveLogByAppId(driveLogDTO.getAppUserNo(), driveLogDTO.getAppDriveLogId());
      }
      if (driveLog == null && driveLogDTO.getId() != null) {
        driveLog = writer.getDriveLogByAppUserNoAndId(driveLogDTO.getAppUserNo(), driveLogDTO.getId());
      }

      if (driveLog != null) {
        driveLog.updateFromDTO(driveLogDTO);
        writer.update(driveLog);
        driveLogDTO.setId(driveLog.getId());
//        DriveLogPlaceNote driveLogPlaceNote = writer.getDriveLogPlaceNoteByLogId(driveLogDTO.getAppUserNo(),driveLogDTO.getId());
//        if(driveLogPlaceNote != null && isUpdatePlaceNote){
//          driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
//          writer.update(driveLogPlaceNote);
//        }else{
//          driveLogPlaceNote = new DriveLogPlaceNote();
//          driveLogPlaceNote.fromDriveLogDTO(driveLogDTO);
//          writer.save(driveLogPlaceNote);
//        }
        if (isUpdatePlaceNote) {
          DriveLogPlaceNote driveLogPlaceNote = writer.getDriveLogPlaceNoteByLogId(driveLogDTO.getAppUserNo(), driveLogDTO.getId());
          if (driveLogPlaceNote == null) {
            driveLogPlaceNote = new DriveLogPlaceNote();
            driveLogPlaceNote.fromDriveLogDTO(driveLogDTO);
            writer.save(driveLogPlaceNote);
          } else {
            driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
            writer.update(driveLogPlaceNote);
          }
        }
      } else {
        driveLog = new DriveLog();
        driveLog.fromDTO(driveLogDTO);
        writer.save(driveLog);
        driveLogDTO.setId(driveLog.getId());
        DriveLogPlaceNote driveLogPlaceNote = new DriveLogPlaceNote();
        driveLogPlaceNote.fromDriveLogDTO(driveLogDTO);
        writer.save(driveLogPlaceNote);
      }

      AppUserConfig firstDriveLogCreateTime = writer.getAppUserConfigByName(driveLogDTO.getAppUserNo(),
        AppUserConfigConstant.FIRST_DRIVE_LOG_CREATE_TIME);
      if (firstDriveLogCreateTime == null && driveLogDTO.getStartTime() != null) {
        firstDriveLogCreateTime = new AppUserConfig();
        firstDriveLogCreateTime.setName(AppUserConfigConstant.FIRST_DRIVE_LOG_CREATE_TIME);
        firstDriveLogCreateTime.setValue(driveLogDTO.getStartTime().toString());
        firstDriveLogCreateTime.setSyncTime(System.currentTimeMillis());
        firstDriveLogCreateTime.setAppUserNo(driveLogDTO.getAppUserNo());
        writer.save(firstDriveLogCreateTime);
      } else if (firstDriveLogCreateTime != null && driveLogDTO.getStartTime() != null) {
        if (driveLogDTO.getStartTime() < NumberUtil.longValue(firstDriveLogCreateTime.getValue(), 0)) {
          firstDriveLogCreateTime.setValue(driveLogDTO.getStartTime().toString());
          writer.update(firstDriveLogCreateTime);
        }
      }
      AppUserConfig lastDriveLogUpdateTime = writer.getAppUserConfigByName(driveLogDTO.getAppUserNo(),
        AppUserConfigConstant.LAST_DRIVE_LOG_UPDATE_TIME);
      if (lastDriveLogUpdateTime == null && driveLogDTO.getLastUpdateTime() != null) {
        lastDriveLogUpdateTime = new AppUserConfig();
        lastDriveLogUpdateTime.setName(AppUserConfigConstant.LAST_DRIVE_LOG_UPDATE_TIME);
        lastDriveLogUpdateTime.setValue(driveLogDTO.getLastUpdateTime().toString());
        lastDriveLogUpdateTime.setSyncTime(System.currentTimeMillis());
        lastDriveLogUpdateTime.setAppUserNo(driveLogDTO.getAppUserNo());
        writer.save(lastDriveLogUpdateTime);
      } else if (lastDriveLogUpdateTime != null && driveLogDTO.getLastUpdateTime() != null) {
        if (driveLogDTO.getLastUpdateTime() > NumberUtil.longValue(lastDriveLogUpdateTime.getValue(), 0)) {
          lastDriveLogUpdateTime.setValue(driveLogDTO.getLastUpdateTime().toString());
          writer.update(lastDriveLogUpdateTime);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return new ApiResultResponse<DriveLogDTO>(MessageCode.toApiResponse(MessageCode.DRIVE_LOG_SAVED_SUCCESS), driveLogDTO);
  }

  @Override
  public List<DriveLogDTO> getDriveLogContents(String appUserNo, Long startTime, Long endTime) {
    if (StringUtils.isNotEmpty(appUserNo)) {
      UserWriter writer = userDaoManager.getWriter();
      return writer.getDriveLogContents(appUserNo, startTime, endTime);
    }
    return new ArrayList<DriveLogDTO>();
  }

  @Override
  public List<DriveLogDTO> getDriveLogDTOsByStartTime(String appUserNo, Long startTime, Long endTime) {
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    if (StringUtils.isNotEmpty(appUserNo)) {
      IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
      UserWriter writer = userDaoManager.getWriter();
      List<DriveLog> driveLogs = writer.getDriveLogByStartTime(appUserNo, startTime, endTime);
      if (CollectionUtils.isNotEmpty(driveLogs)) {
        for (DriveLog driveLog : driveLogs) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          if (DriveLogStatus.DRIVING.equals(driveLogDTO.getStatus())) {
            String imei = null;
            ObdUserVehicle obdUserVehicle = CollectionUtil.getFirst(writer.getObdUserVehicleByAppUserNo(appUserNo));
            if (obdUserVehicle != null && obdUserVehicle.getObdId() != null) {
              OBD obd = writer.getById(OBD.class, obdUserVehicle.getObdId());
              if (obd != null && StringUtils.isNotBlank(obd.getImei())) {
                imei = obd.getImei();
              }
            }
            List<GsmPoint> gsmPoint = gsmPointService.getGsmPointByIMeiAndUploadTime(imei, driveLogDTO.getStartTime(), driveLogDTO.getEndTime());
            setDriveLogPoint(driveLogDTO, gsmPoint);
          }
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;
  }

  @Override
  public DriveLogDTO getDriveLogDetail(String appUserNo, Long driveLogId) {
    if (StringUtils.isEmpty(appUserNo) || driveLogId == null) {
      return null;
    }
    DriveLogDTO driveLogDTO = null;
    UserWriter writer = userDaoManager.getWriter();
    DriveLog driveLog = writer.getDriveLogByAppUserNoAndId(appUserNo, driveLogId);
    if (driveLog != null) {
      driveLogDTO = driveLog.toDTO();
      DriveLogPlaceNote driveLogPlaceNote = writer.getDriveLogPlaceNoteByLogId(appUserNo, driveLogId);
      if (driveLogPlaceNote != null) {
        driveLogDTO.setPlaceNotes(driveLogPlaceNote.getPlaceNotes());
      }
    }
    return driveLogDTO;
  }

  @Override
  public List<DriveLogDTO> getDriveLogDetailDTOsByIds(String appUserNo, Set<Long> driveLogIds, boolean isContainPlaceNotes) {
    if (StringUtils.isEmpty(appUserNo) || CollectionUtil.isEmpty(driveLogIds)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLog> driveLogs = writer.getDriveLogByIds(appUserNo, driveLogIds);
    List<DriveLogDTO> driveLogDTOs = this.getDriveLogPlaceNote(driveLogs, appUserNo, isContainPlaceNotes);
    return driveLogDTOs;
  }

  @Override
  public void generateDriveLogByGsmVehicleInfo(final int limit, final Set<String> imeis) {
    IGsmVehicleService gsmVehicleService = ServiceManager.getService(IGsmVehicleService.class);
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
    IVehicleService vehicleService = ServiceManager.getService(IVehicleService.class);
    Map<String, List<GsmVehicleInfo>> gsmVehicleInfoMap;

    Long watchStartTime = System.currentTimeMillis();
    long loopTimes = 0;
    while (true) {
      loopTimes++;
      if (loopTimes > 10000) {
        LOG.error("当前循环执行了[{}]次,防止死循环，跳出当前循环", loopTimes);
        break;
      }
      if (System.currentTimeMillis() - watchStartTime > 3600000L) {
        LOG.error("当前循环执行了[{}]分钟,防止死循环，跳出当前循环", (System.currentTimeMillis() - watchStartTime) / 1000 / 60);
        break;
      }
      gsmVehicleInfoMap = gsmVehicleService.getUnHandledGsmVehicleInfoToHandling(limit, imeis);
      if (MapUtils.isEmpty(gsmVehicleInfoMap)) {
        break;
      }
      //循环处理imei


//      Map<String,AppUserDTO> appUserDTOMap = appUserService.getAppUserMapByUserNo(gsmVehicleInfoMap.keySet());
      //key imei
      Map<String, AppUserDTO> appUserDTOMap = appUserService.getAppUserMapByImeis(gsmVehicleInfoMap.keySet());
      //key imei
      Map<String, AppVehicleDTO> appVehicleDTOMap = appUserService.getAppVehicleMapByImeis(gsmVehicleInfoMap.keySet());
      //key imei
      Map<String, VehicleDTO> vehicleDTOMap = vehicleService.getVehicleDTOMapByIMeis(gsmVehicleInfoMap.keySet());
      Set<String> appUserNoSet = new HashSet<String>();
      if (MapUtils.isNotEmpty(appUserDTOMap)) {
        for (AppUserDTO appUserDTO : appUserDTOMap.values()) {
          if (appUserDTO != null && StringUtils.isNotBlank(appUserDTO.getUserNo())) {
            appUserNoSet.add(appUserDTO.getUserNo());
          }
        }
      }
      //key appUserNo
      Map<String, List<DriveLogDTO>> iMeiDriveLogDTOMap = driveLogService.getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(
        appUserNoSet, DriveLogStatus.DRIVING);

      for (String imei : gsmVehicleInfoMap.keySet()) {
        List<GsmVehicleInfo> gsmVehicleInfoList = gsmVehicleInfoMap.get(imei);
        LOG.warn("开始处理【{}】的数据【{}】条", imei, gsmVehicleInfoList == null ? 0 : gsmVehicleInfoList.size());
        if (CollectionUtils.isNotEmpty(gsmVehicleInfoList)) {
          AppUserDTO appUserDTO = appUserDTOMap.get(imei);
          AppVehicleDTO appVehicleDTO = appVehicleDTOMap.get(imei);
          VehicleDTO vehicleDTO = vehicleDTOMap.get(imei);

          if (appUserDTO != null && appVehicleDTO != null) {
            //找到上次未完成的行车日志
            List<DriveLogDTO> driveLogDTOs = iMeiDriveLogDTOMap.get(appUserDTO.getUserNo());
            //如果有多条结束掉前只留最后一条
            DriveLogDTO lastDriveLogDTO = driveLogService.finishNotLastDriveLog(driveLogDTOs);

            for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfoList) {

              if (!validateGsmVehicle(gsmVehicleInfo)) {
                continue;
              }

              if (lastDriveLogDTO != null) {
                //判断是否接的上上条数据
                if (isLastVehicleDriving(gsmVehicleInfo, lastDriveLogDTO)) {
                  addLastDriveLogByGsmVehicleInfo(lastDriveLogDTO, gsmVehicleInfo);
                } else {
                  lastDriveLogDTO.setStatus(DriveLogStatus.ENABLED);
                  saveOrUpdateLastDriveLogByGsmVehicleInfo(lastDriveLogDTO, imei);
                  lastDriveLogDTO = null;
                  if (isNewVehicleDriving(gsmVehicleInfo)) {
                    lastDriveLogDTO = new DriveLogDTO();
                    lastDriveLogDTO.setAppUserVehicleInfo(appUserDTO, appVehicleDTO);
                    lastDriveLogDTO.setDriveStatStatus(DriveStatStatus.UN_STATISTIC);
                    addFirstVehicleInfo(lastDriveLogDTO, gsmVehicleInfo);
                  }
                }
              } else {
                if (isNewVehicleDriving(gsmVehicleInfo)) {
                  lastDriveLogDTO = new DriveLogDTO();
                  lastDriveLogDTO.setAppUserVehicleInfo(appUserDTO, appVehicleDTO);
                  lastDriveLogDTO.setDriveStatStatus(DriveStatStatus.UN_STATISTIC);
                  addFirstVehicleInfo(lastDriveLogDTO, gsmVehicleInfo);
                }
              }
            }
            if (lastDriveLogDTO != null) {
              lastDriveLogDTO.setStatus(DriveLogStatus.DRIVING);
              lastDriveLogDTO.setDriveStatStatus(DriveStatStatus.UN_STATISTIC);
              saveOrUpdateLastDriveLogByGsmVehicleInfo(lastDriveLogDTO, imei);
            }
          }
          if (vehicleDTO != null) {
            vehicleService.updateVehicleMilByGsmVehicleInfo(vehicleDTO, gsmVehicleInfoList);
          }
          if (appVehicleDTO != null) {
            appUserService.updateAppVehicleInfoByGsmVehicleInfo(appVehicleDTO, gsmVehicleInfoList);
          }
        }
      }
    }
  }


  //判断一条车况是不是新开是一段行程
  //2，小计里程，大于0
  //3，驾驶时间大于0
  //4，小计油耗大于0
  //2，3，4 四个条件满足1个
  private boolean isNewVehicleDriving(GsmVehicleInfo gsmVehicleInfo) {
    if (gsmVehicleInfo != null) {
      int isSameTravel = 0;
      if (NumberUtil.doubleVal(gsmVehicleInfo.getTrmil()) > 0) {
        isSameTravel++;
      }
      if (generateTravelTime(gsmVehicleInfo) > 0) {
        isSameTravel++;
      }
      if (NumberUtil.doubleVal(gsmVehicleInfo.getCactrfe()) > 0) {
        isSameTravel++;
      }
      if (isSameTravel >= 1) {
        return true;
      }
    }
    return false;
  }

  private void addFirstVehicleInfo(DriveLogDTO lastDriveLogDTO, GsmVehicleInfo gsmVehicleInfo) {
    if (lastDriveLogDTO != null && gsmVehicleInfo != null) {
//      lastDriveLogDTO.setAppUserNo(gsmVehicleInfo.getEmi());
      lastDriveLogDTO.setEndTime(gsmVehicleInfo.getUploadTime());

      double oilCost = NumberUtil.round(NumberUtil.doubleValue(gsmVehicleInfo.getCactrfe(), 0) / 1000, 2);
      double trMil = NumberUtil.round(NumberUtil.doubleValue(gsmVehicleInfo.getTrmil(), 0), 2);
      //排除上一段行程残留的驾驶时间
      long travelTime = generateTravelTime(gsmVehicleInfo);
      lastDriveLogDTO.setStartTime(gsmVehicleInfo.getUploadTime() - travelTime * 1000);
      lastDriveLogDTO.setTravelTime(travelTime);
      lastDriveLogDTO.setOilCost(oilCost);
      lastDriveLogDTO.setAppDriveLogId(UUID.randomUUID().toString());

      Double oilPrice = null;
      UserWriter writer = userDaoManager.getWriter();
      AppUserConfig lastDriveLogOilPrice = writer.getAppUserConfigByName(lastDriveLogDTO.getAppUserNo(),
        AppUserConfigConstant.OIL_PRICE);
      if (lastDriveLogOilPrice != null && StringUtils.isNumeric(lastDriveLogOilPrice.getValue())) {
        oilPrice = NumberUtil.doubleVal(lastDriveLogOilPrice.getValue());
      }
      if (oilPrice == null) {
        oilPrice = GSMConstant.DEF_OIL_PRICE;
      }
      lastDriveLogDTO.setOilPrice(oilPrice);
      lastDriveLogDTO.setAppPlatform(AppPlatform.GSM);
      lastDriveLogDTO.setTotalOilMoney(NumberUtil.round(oilCost * oilPrice, 2));
      lastDriveLogDTO.setDistance(trMil);
      if (lastDriveLogDTO.getDistance() > 0) {
        double oilWear = NumberUtil.round(lastDriveLogDTO.getOilCost() / lastDriveLogDTO.getDistance() * 100, 2);
        if (oilWear > 30) {
          oilWear = 30;
          lastDriveLogDTO.setOilWear(oilWear);
        }
      }
    }
  }


  private void addLastDriveLogByGsmVehicleInfo(DriveLogDTO lastDriveLogDTO, GsmVehicleInfo gsmVehicleInfo) {

    if (lastDriveLogDTO != null && gsmVehicleInfo != null) {
      LOG.warn("addLastDriveLogByGsmVehicleInfo：【{}】", lastDriveLogDTO.getAppUserNo());
      double oilCost = NumberUtil.round(NumberUtil.doubleValue(gsmVehicleInfo.getCactrfe(), 0) / 1000, 2);
      double trMil = NumberUtil.round(NumberUtil.doubleValue(gsmVehicleInfo.getTrmil(), 0), 2);
      long travelTime = generateTravelTime(gsmVehicleInfo);

      if (trMil > NumberUtil.doubleVal(lastDriveLogDTO.getDistance()) + 0.0001) {
        lastDriveLogDTO.setDistance(trMil);
        lastDriveLogDTO.setEndTime(gsmVehicleInfo.getUploadTime());
      } else if (trMil < 0.1 && NumberUtil.doubleVal(lastDriveLogDTO.getDistance()) < 0.1) {
        if (travelTime > NumberUtil.longValue(lastDriveLogDTO.getTravelTime())) {
          lastDriveLogDTO.setEndTime(gsmVehicleInfo.getUploadTime());
        }
      }
      if (travelTime > NumberUtil.longValue(lastDriveLogDTO.getTravelTime())) {
        lastDriveLogDTO.setTravelTime(travelTime);
      }
      if (oilCost > NumberUtil.doubleVal(lastDriveLogDTO.getOilCost()) + 0.0001) {
        lastDriveLogDTO.setOilCost(oilCost);
        Double oilPrice = lastDriveLogDTO.getOilPrice();
        if (oilPrice == null) {
          UserWriter writer = userDaoManager.getWriter();
          AppUserConfig lastDriveLogOilPrice = writer.getAppUserConfigByName(lastDriveLogDTO.getAppUserNo(),
            AppUserConfigConstant.OIL_PRICE);
          if (lastDriveLogOilPrice != null && StringUtils.isNumeric(lastDriveLogOilPrice.getValue())) {
            oilPrice = NumberUtil.doubleVal(lastDriveLogOilPrice.getValue());
          }
        }
        if (oilPrice == null) {
          oilPrice = GSMConstant.DEF_OIL_PRICE;
        }
        lastDriveLogDTO.setTotalOilMoney(NumberUtil.round(oilCost * oilPrice, 2));
      }

      if (lastDriveLogDTO.getOilCost() != null && NumberUtil.doubleVal(lastDriveLogDTO.getDistance()) > 0) {
        double oilWear = NumberUtil.round(lastDriveLogDTO.getOilCost() / lastDriveLogDTO.getDistance() * 100, 2);
        if (oilWear > 30) {
          if (NumberUtil.longValue(lastDriveLogDTO.getTravelTime()) > 0 && NumberUtil.doubleVal(lastDriveLogDTO.getDistance()) > 0) {
            double gsmVehicleOilWear = NumberUtil.round(lastDriveLogDTO.getOilCost() / lastDriveLogDTO.getTravelTime()
              * NumberUtil.doubleVal(gsmVehicleInfo.getDrit()) / lastDriveLogDTO.getDistance() * 100, 2);
            if (gsmVehicleOilWear < 30) {
              oilWear = gsmVehicleOilWear;
            }
          }
          if (oilWear > 30) {
            oilWear = 30;
          }
        }
        lastDriveLogDTO.setOilWear(oilWear);
      }
    }
  }

  private long generateTravelTime(GsmVehicleInfo gsmVehicleInfo) {
    long travelTime = 0;
    if (gsmVehicleInfo != null) {
      double trMil = NumberUtil.round(NumberUtil.doubleValue(gsmVehicleInfo.getTrmil(), 0), 2);
      //怠速时间
      long idlet = NumberUtil.longValue(gsmVehicleInfo.getIdlet(), 0);
      //行驶时间
      long drit = NumberUtil.longValue(gsmVehicleInfo.getDrit(), 0);
      if (trMil < 0.1) {
        travelTime = idlet;
      } else {
        travelTime = idlet + drit;
      }
    }
    return travelTime;
  }

  //判断一条车况能不能接上 上一条未完成的行车日志
  //1，数据时间大于行车日志结束时间 （必要条件）
  //2，小计里程，大于等于行车日志里程
  //3，驾驶时间大于等于行车日志时间
  //4，小计油耗大于等于行车日志小计油耗
  //5，acc 等于1
  //2，3，4，5 四个条件满足3个
  private boolean isLastVehicleDriving(GsmVehicleInfo gsmVehicleInfo, DriveLogDTO lastDriveLogDTO) {
    if (gsmVehicleInfo != null && lastDriveLogDTO != null) {

      int isSameTravel = 0;
      if (gsmVehicleInfo.getUploadTime() - lastDriveLogDTO.getEndTime() > 18000000L) {//时间相差5个小时
//        isSameTravel -=3;
      }
      if (StringUtils.isNotBlank(gsmVehicleInfo.getTrmil()) && NumberUtil.doubleVal(gsmVehicleInfo.getTrmil()) > 0.0001) {
        double trMail = NumberUtil.doubleVal(gsmVehicleInfo.getTrmil());
        if (NumberUtil.minus(trMail, lastDriveLogDTO.getDistance()) > -0.00001) {//大于等于
          isSameTravel++;
        } else {//小于
          isSameTravel--;
        }
      }

      long travelTime = NumberUtil.longValue(gsmVehicleInfo.getDrit(), 0) + NumberUtil.longValue(gsmVehicleInfo.getIdlet(), 0);
//      long travelTime = generateTravelTime(gsmVehicleInfo);
      if (travelTime > 0) {
        if (travelTime >= NumberUtil.longValue(lastDriveLogDTO.getTravelTime())) {
          isSameTravel++;
        } else {
          if (StringUtils.isNotBlank(gsmVehicleInfo.getTrmil()) && NumberUtil.doubleVal(gsmVehicleInfo.getTrmil()) > 0.0001) {
            double trMail = NumberUtil.doubleVal(gsmVehicleInfo.getTrmil());
            if (NumberUtil.minus(trMail, lastDriveLogDTO.getDistance()) <= 0.00001) {//小于等于
              isSameTravel--;
            }
          }
        }
      }
      if (NumberUtil.doubleVal(gsmVehicleInfo.getCactrfe()) > 0.0001) {
        if (NumberUtil.round(NumberUtil.doubleVal(gsmVehicleInfo.getCactrfe()) / 1000, 2) >= NumberUtil.doubleVal(lastDriveLogDTO.getOilCost())) {
          isSameTravel++;
        } else {
//          isSameTravel --;
        }
      }

//      if (NumberUtil.doubleVal(gsmVehicleInfo.getCactrfe()) > 0.0001
//          && NumberUtil.doubleVal(gsmVehicleInfo.getCactrfe()) / 1000 >= NumberUtil.doubleVal(lastDriveLogDTO.getOilCost())) {
//        isSameTravel++;
//      }

      if (isSameTravel >= 1) {
        return true;
      }
    }
    return false;
  }

  //校验是否为补报数据，补报时间大于5分钟的舍弃掉
  private boolean validateGsmPoint(GsmPoint gsmPoint) {
    return gsmPoint != null && NumberUtil.minus(gsmPoint.getUploadServerTime(), gsmPoint.getUploadTime()) < 3000000L;
  }

  //校验是否为补报数据,是否为有效数据，补报时间大于5分钟的舍弃掉,
  private boolean validateGsmVehicle(GsmVehicleInfo gsmVehicleInfo) {
    return gsmVehicleInfo != null
      && NumberUtil.minus(gsmVehicleInfo.getUploadServerTime(), gsmVehicleInfo.getUploadTime()) < 3000000L
      && NumberUtil.minus(gsmVehicleInfo.getUploadServerTime(), gsmVehicleInfo.getUploadTime()) > -60000L
      && (NumberUtil.doubleVal(gsmVehicleInfo.getTrmil()) > 0.0001
      || NumberUtil.doubleVal(gsmVehicleInfo.getCactrfe()) > 0.0001
      || NumberUtil.longValue(gsmVehicleInfo.getIdlet(), 0) + NumberUtil.longValue(gsmVehicleInfo.getDrit(), 0) > 0.0001);
  }

  @Override
  public List<DriveLogDTO> getDriveLogDetailByStatusOrderByEndTimeAsc(String appUserNo, DriveLogStatus driveLogStatus, int limit) {
    if (StringUtils.isEmpty(appUserNo) || driveLogStatus == null) {
      return new ArrayList<DriveLogDTO>();
    }
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLog> driveLogs = writer.getDriveLogByStatusOrderByEndTimeAsc(appUserNo, driveLogStatus, limit);
    Map<Long, DriveLogPlaceNoteDTO> driveLogPlaceNoteMap = new HashMap<Long, DriveLogPlaceNoteDTO>();

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      Set<Long> toGetPlaceNoteIds = new HashSet<Long>();
      for (DriveLog driveLog : driveLogs) {
        toGetPlaceNoteIds.add(driveLog.getId());
      }
      List<DriveLogPlaceNote> driveLogPlaceNotes = writer.getDriveLogPlaceNoteByLogIds(appUserNo, toGetPlaceNoteIds);
      if (CollectionUtils.isNotEmpty(driveLogPlaceNotes)) {
        for (DriveLogPlaceNote driveLogPlaceNote : driveLogPlaceNotes) {
          if (driveLogPlaceNote != null && driveLogPlaceNote.getDriveLogId() != null) {
            driveLogPlaceNoteMap.put(driveLogPlaceNote.getDriveLogId(), driveLogPlaceNote.toDTO());
          }
        }
      }
    }

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      for (DriveLog driveLog : driveLogs) {
        if (driveLog != null) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          DriveLogPlaceNoteDTO driveLogPlaceNoteDTO = driveLogPlaceNoteMap.get(driveLog.getId());
          driveLogDTO.setPlaceNoteDTO(driveLogPlaceNoteDTO);
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;

  }

  @Override
  public List<DriveLogDTO> getDriveLogContentsByStatusAndStatStatus(String appUserNo, Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus) {
    if (StringUtils.isEmpty(appUserNo) || CollectionUtils.isEmpty(driveLogStatus) || statStatus == null) {
      return new ArrayList<DriveLogDTO>();
    }
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLog> driveLogs = writer.getDriveLogsByStatusAndStatStatus(appUserNo, driveLogStatus, statStatus);

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      for (DriveLog driveLog : driveLogs) {
        if (driveLog != null) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;
  }

  @Override
  public List<DriveLogDTO> getDriveLogContentsByStatusAndStatStatus(Set<DriveLogStatus> driveLogStatus, DriveStatStatus statStatus, int limit) {
    if (CollectionUtils.isEmpty(driveLogStatus) || statStatus == null || limit <= 0) {
      return new ArrayList<DriveLogDTO>();
    }
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLog> driveLogs = writer.getDriveLogsByStatusAndStatStatus(driveLogStatus, statStatus, limit);

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      for (DriveLog driveLog : driveLogs) {
        if (driveLog != null) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;
  }

  @Override
  public Map<String, List<DriveLogDTO>> getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(Set<String> appUserNos, DriveLogStatus driveLogStatus) {
    Map<String, List<DriveLogDTO>> driveLogDTOMap = new HashMap<String, List<DriveLogDTO>>();
    if (CollectionUtils.isNotEmpty(appUserNos) && driveLogStatus != null) {
      UserWriter writer = userDaoManager.getWriter();
      List<DriveLog> driveLogs = writer.getDriveLogByAppUserNosAndStatusOrderByEndTimeAsc(appUserNos, driveLogStatus);
      if (CollectionUtils.isNotEmpty(driveLogs)) {
        for (DriveLog driveLog : driveLogs) {
          if (driveLog != null && StringUtils.isNotBlank(driveLog.getAppUserNo())) {

            List<DriveLogDTO> driveLogDTOs = driveLogDTOMap.get(driveLog.getAppUserNo());
            if (driveLogDTOs == null) {
              driveLogDTOs = new ArrayList<DriveLogDTO>();
            }
            driveLogDTOs.add(driveLog.toDTO());
            driveLogDTOMap.put(driveLog.getAppUserNo(), driveLogDTOs);
          }
        }
      }
    }
    return driveLogDTOMap;
  }

  @Override
  public DriveLogDTO finishNotLastDriveLog(List<DriveLogDTO> driveLogDTOs) {
    if (CollectionUtils.isEmpty(driveLogDTOs)) {
      return null;
    }
    for (int i = 0; i < driveLogDTOs.size(); i++) {
      if (i == driveLogDTOs.size() - 1) {
        return driveLogDTOs.get(i);
      }
      DriveLogDTO driveLogDTO = driveLogDTOs.get(i);
      if (driveLogDTO != null) {
        IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
        driveLogDTO.setEndPlace(geocodingService.gpsCoordinateToAddress(driveLogDTO.getEndLat(), driveLogDTO.getEndLon()));
        UserWriter writer = userDaoManager.getWriter();
        Object status = writer.begin();
        try {
          DriveLog driveLog = writer.getById(DriveLog.class, driveLogDTO.getId());
          if (driveLog != null) {
            driveLog.setStatus(DriveLogStatus.ENABLED);
            driveLog.setEndPlace(driveLogDTO.getEndPlace());
            writer.update(driveLog);
            writer.commit(status);
          }
        } finally {
          writer.rollback(status);
        }
        //一段有效的里程保存的时候，需要比对当前appVehicle的最差油耗，最佳油耗，平均油耗
        //这里拿不到总油耗没办法更新总油耗
        if (DriveLogStatus.ENABLED.equals(driveLogDTO.getStatus())) {
          IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
          appUserService.updateAppVehicleOilWearAndPosition(driveLogDTO.getAppUserNo(), driveLogDTO.getVehicleNo(),
            NumberUtil.doubleValue(driveLogDTO.getOilWear(), 0), 0, null, null);
        }
      }
    }
    return driveLogDTOs.get(driveLogDTOs.size() - 1);
  }

  //结束一段行程，保存行程相关数据

  @Override
  public void saveOrUpdateLastDriveLog(DriveLogDTO lastDriveLogDTO, List<GsmVehicleInfo> gsmVehicleInfoList) {
    if (lastDriveLogDTO != null) {
      IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
      Long endTime = lastDriveLogDTO.getEndTime();
      if (StringUtils.isEmpty(lastDriveLogDTO.getStartPlace())) {
        lastDriveLogDTO.setStartPlace(geocodingService.gpsCoordinateToAddress(lastDriveLogDTO.getStartLat(), lastDriveLogDTO.getStartLon()));
      }
      lastDriveLogDTO.setEndPlace(geocodingService.gpsCoordinateToAddress(lastDriveLogDTO.getEndLat(), lastDriveLogDTO.getEndLon()));
      long travelTime = (NumberUtil.longValue(lastDriveLogDTO.getEndTime()) - NumberUtil.longValue(lastDriveLogDTO.getStartTime())) / 1000;
      if (DriveLogStatus.ENABLED.equals(lastDriveLogDTO.getStatus()) && travelTime < GSMConstant.EFFECTIVE_TRAVEL_TIME / 1000) {
        lastDriveLogDTO.setStatus(DriveLogStatus.DISABLED);
      }
      lastDriveLogDTO.setTravelTime(travelTime);

      GsmVehicleInfo lastGsmVehicleInfo = null;
      if (CollectionUtils.isNotEmpty(gsmVehicleInfoList)) {
        //找到小于endTime 中最后一条有效的gsmVehicleInfo
        for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfoList) {
          if (gsmVehicleInfo != null && gsmVehicleInfo.getUploadTime() != null) {
            if (gsmVehicleInfo.getUploadTime() <= endTime) {
              if (isValid(gsmVehicleInfo)) {
                lastGsmVehicleInfo = gsmVehicleInfo;
              }
            } else {
              break;
            }
          }
        }
      }
      double totalOilWear = 0;//总的平均油耗
      if (lastGsmVehicleInfo != null) {
        double oilCost = NumberUtil.round(NumberUtil.doubleValue(lastGsmVehicleInfo.getCactrfe(), 0) / 1000, 2);
        double distance = NumberUtil.round(NumberUtil.doubleValue(lastGsmVehicleInfo.getTrmil(), 0), 2);
        double oilWear = 0;
        if (distance > 0) {
          oilWear = NumberUtil.round(oilCost / distance * 100, 2);
          if (oilWear > 40) {
            oilWear = 40;
          }
        }
        UserWriter writer = userDaoManager.getWriter();
        AppUserConfig lastDriveLogOilPrice = writer.getAppUserConfigByName(lastDriveLogDTO.getAppUserNo(),
          AppUserConfigConstant.OIL_PRICE);

        double oilPrice = 7.7d;
        if (lastDriveLogOilPrice != null && StringUtils.isNumeric(lastDriveLogOilPrice.getValue())) {
          oilPrice = NumberUtil.doubleVal(lastDriveLogOilPrice.getValue());
        }
        double totalOilMoney = NumberUtil.round(oilCost * oilPrice, 2);
        totalOilWear = NumberUtil.round(NumberUtil.doubleValue(lastGsmVehicleInfo.getCacafe(), 0), 2);
        lastDriveLogDTO.setOilCost(oilCost);
        lastDriveLogDTO.setDistance(distance);
        lastDriveLogDTO.setOilWear(oilWear);
        lastDriveLogDTO.setTotalOilMoney(totalOilMoney);
        lastDriveLogDTO.setOilPrice(oilPrice);
        lastDriveLogDTO.setLastUpdateTime(System.currentTimeMillis());
        handleSaveDriveLog(lastDriveLogDTO, true);
        //一段有效的里程保存的时候，需要比对当前appVehicle的最差油耗，最佳油耗，平均油耗
        IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
        if (DriveLogStatus.ENABLED.equals(lastDriveLogDTO.getStatus())) {
          appUserService.updateAppVehicleOilWearAndPosition(lastDriveLogDTO.getAppUserNo(), lastDriveLogDTO.getVehicleNo(),
            NumberUtil.doubleValue(lastDriveLogDTO.getOilWear(), 0), totalOilWear, null, null);
        }
      }
    }

  }

  private void saveOrUpdateLastDriveLogByGsmVehicleInfo(DriveLogDTO lastDriveLogDTO, String imei) {
    if (lastDriveLogDTO != null) {
      LOG.warn("saveOrUpdateLastDriveLogByGsmVehicleInfo：【{}】,【{}】", lastDriveLogDTO.getAppUserNo(), lastDriveLogDTO.getStatus());
      IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
      IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
      boolean isFinished = DriveLogStatus.ENABLED.equals(lastDriveLogDTO.getStatus());
      if (isFinished) {
        long startTime = System.currentTimeMillis();
        List<GsmPoint> gsmPointsList = gsmPointService.getGsmPointByIMeiAndUploadTime(imei,
          lastDriveLogDTO.getStartTime(), lastDriveLogDTO.getEndTime());
        setDriveLogPoint(lastDriveLogDTO, gsmPointsList);
        LOG.warn("Finish 一段行程 总共花了【{}】秒，找到【{}】条gsmPoint数据，", (System.currentTimeMillis() - startTime) / 1000 / 60, gsmPointsList == null ? 0 : gsmPointsList.size());
      }
      lastDriveLogDTO.setLastUpdateTime(System.currentTimeMillis());
      handleSaveDriveLog(lastDriveLogDTO, isFinished);
      //一段有效的里程保存的时候，需要比对当前appVehicle的最差油耗，最佳油耗，平均油耗
      if (isFinished) {
        appUserService.updateAppVehicleOilWearAndPosition(lastDriveLogDTO.getAppUserNo(), lastDriveLogDTO.getVehicleNo(),
          NumberUtil.doubleValue(lastDriveLogDTO.getOilWear(), 0), 0d, null, null);
      }

    }
  }

  public void setDriveLogPoint(DriveLogDTO lastDriveLogDTO, List<GsmPoint> gsmPoints) {
    if (lastDriveLogDTO != null && CollectionUtils.isNotEmpty(gsmPoints)) {
      IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
      StringBuilder sb = new StringBuilder();
      String lastLat = null, lastLon = null;
      boolean isSetStartPlace = false;
      for (GsmPoint gsmPoint : gsmPoints) {
        if (gsmPoint != null && NumberUtil.doubleVal(gsmPoint.getLat()) > 0 && NumberUtil.doubleVal(gsmPoint.getLon()) > 0
          && NumberUtil.longValue(gsmPoint.getUploadTime()) >= NumberUtil.longValue(lastDriveLogDTO.getStartTime())
          && NumberUtil.longValue(gsmPoint.getUploadTime()) <= NumberUtil.longValue(lastDriveLogDTO.getEndTime())) {
          //数据时间大于接收时间1分钟的过滤掉
          if (NumberUtil.longValue(gsmPoint.getUploadTime()) - NumberUtil.longValue(gsmPoint.getUploadServerTime()) > 60000) {
            continue;
          }
          lastLat = NumberUtil.convertGPSLat(gsmPoint.getLat());
          lastLon = NumberUtil.convertGPSLot(gsmPoint.getLon());
          if (!isSetStartPlace) {
            isSetStartPlace = true;
            lastDriveLogDTO.setStartLat(lastLat);
            lastDriveLogDTO.setStartLon(lastLon);
            lastDriveLogDTO.setStartPlace(geocodingService.gpsCoordinateToAddress(lastDriveLogDTO.getStartLat(), lastDriveLogDTO.getStartLon()));
          }
          sb.append(lastLat)
            .append(",")
            .append(lastLon)
            .append(",")
            .append(gsmPoint.getUploadTime())
            .append("|");
        }
      }
      lastDriveLogDTO.setPlaceNotes(sb.toString());
      if (lastLat != null && lastLon != null) {
        lastDriveLogDTO.setEndLat(lastLat);
        lastDriveLogDTO.setEndLon(lastLon);
        lastDriveLogDTO.setEndPlace(geocodingService.gpsCoordinateToAddress(lastDriveLogDTO.getEndLat(), lastDriveLogDTO.getEndLon()));
      }
    }
  }

  public void setDriveLogGVPoint(DriveLogDTO lastDriveLogDTO, List<GsmVehicleDataDTO> gvDataDTOs) {
    LOG.debug("POBD:setDriveLogGVPoint");
    if (lastDriveLogDTO == null || CollectionUtils.isEmpty(gvDataDTOs)) {
      return;
    }
    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
    StringBuilder sb = new StringBuilder();
    String lastLat = null, lastLon = null;
    for (GsmVehicleDataDTO dataDTO : gvDataDTOs) {
      lastLat = dataDTO.getLat();
      lastLon = dataDTO.getLon();
      sb.append(lastLat)
        .append(",")
        .append(lastLon)
        .append(",")
        .append(dataDTO.getUploadTime())
        .append("|");
    }
    lastDriveLogDTO.setPlaceNotes(sb.toString());
    LOG.debug("POBD:{}", sb.toString());
    if (lastLat != null && lastLon != null) {
      lastDriveLogDTO.setEndLat(lastLat);
      lastDriveLogDTO.setEndLon(lastLon);
      lastDriveLogDTO.setEndPlace(geocodingService.gpsCoordinateToAddress(lastDriveLogDTO.getEndLat(), lastDriveLogDTO.getEndLon()));
    }
  }


  //有单次里程，单次油耗
  private boolean isValid(GsmVehicleInfo gsmVehicleInfo) {
    if (gsmVehicleInfo != null
      && StringUtils.isNotEmpty(gsmVehicleInfo.getTrmil())
      && StringUtils.isNotEmpty(gsmVehicleInfo.getCactrfe())
      && StringUtils.isNotBlank(gsmVehicleInfo.getCacafe())) {
      return true;
    }
    return false;
  }

  public List<DriveLogDTO> getDriveLogDTOsByImeiTime(String imei, Long startTime, Long endTime, Pager pager) {
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLog> driveLogs = writer.getDriveLogDTOsByImeiTime(imei, startTime, endTime, pager);
    List<DriveLogDTO> driveLogDTOs = this.getDriveLogForShop(driveLogs, null);

    return driveLogDTOs;
  }

  public int countDriveLogDTOsByImeiTime(String imei, Long startTime, Long endTime) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.countDriveLogDTOsByImeiTime(imei, startTime, endTime);
  }

  public List<DriveLogPlaceNoteDTO> getDriveLogPlaceNoteByLogIds(String appUserNo, Set<Long> driveLogIds) {
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLogPlaceNote> noteList = writer.getDriveLogPlaceNoteByLogIds(appUserNo, driveLogIds);

    List<DriveLogPlaceNoteDTO> noteDTOs = new ArrayList<DriveLogPlaceNoteDTO>();
    if (CollectionUtil.isEmpty(noteList)) {
      return noteDTOs;
    }

    for (DriveLogPlaceNote driveLogPlaceNote : noteList) {
      noteDTOs.add(driveLogPlaceNote.toDTO());
    }

    return noteDTOs;
  }


  public List<DriveLogDTO> getDriveLogPlaceNote(List<DriveLog> driveLogs, String appUserNo, boolean isContainPlaceNotes) {

    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    UserWriter writer = userDaoManager.getWriter();


    Map<Long, DriveLogPlaceNoteDTO> driveLogPlaceNoteMap = new HashMap<Long, DriveLogPlaceNoteDTO>();

    if (isContainPlaceNotes && CollectionUtils.isNotEmpty(driveLogs)) {
      Set<Long> toGetPlaceNoteIds = new HashSet<Long>();
      for (DriveLog driveLog : driveLogs) {
        toGetPlaceNoteIds.add(driveLog.getId());
        if (StringUtils.isEmpty(appUserNo) && StringUtils.isNotBlank(driveLog.getAppUserNo())) {
          appUserNo = driveLog.getAppUserNo();
        }
      }

      List<DriveLogPlaceNote> driveLogPlaceNotes = writer.getDriveLogPlaceNoteByLogIds(appUserNo, toGetPlaceNoteIds);
      if (CollectionUtils.isNotEmpty(driveLogPlaceNotes)) {
        for (DriveLogPlaceNote driveLogPlaceNote : driveLogPlaceNotes) {
          if (driveLogPlaceNote != null && driveLogPlaceNote.getDriveLogId() != null) {
            driveLogPlaceNoteMap.put(driveLogPlaceNote.getDriveLogId(), driveLogPlaceNote.toDTO());
          }
        }
      }
    }

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      for (DriveLog driveLog : driveLogs) {
        if (driveLog != null) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          if (isContainPlaceNotes) {
            DriveLogPlaceNoteDTO driveLogPlaceNoteDTO = driveLogPlaceNoteMap.get(driveLog.getId());
            driveLogDTO.setPlaceNoteDTO(driveLogPlaceNoteDTO);
            if (DriveLogStatus.DRIVING.equals(driveLogDTO.getStatus())) {
              String imei = null;
              OBD obd = null;
              ObdUserVehicle obdUserVehicle = CollectionUtil.getFirst(writer.getObdUserVehicleByAppUserNo(appUserNo));
              if (obdUserVehicle != null && obdUserVehicle.getObdId() != null) {
                obd = writer.getById(OBD.class, obdUserVehicle.getObdId());
              }
              if (obd != null) {
                if (ObdType.POBD.equals(obd.getObdType())) {
                  LOG.info("POBD:get all gvDataDTOs");
                  List<GsmVehicleDataDTO> gvDataDTOs = ServiceManager.getService(IGSMVehicleDataService.class).getGsmVehicleDataDTOByUUID(driveLogDTO.getAppDriveLogId());
                  driveLogDTO.setAppPlatform(AppPlatform.GSM);
                  setDriveLogGVPoint(driveLogDTO, gvDataDTOs);
                } else {
                  IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
                  List<GsmPoint> gsmPoints = gsmPointService.getGsmPointByIMeiAndUploadTime(imei,
                    driveLogDTO.getStartTime(), driveLogDTO.getEndTime());
                  driveLogDTO.setAppPlatform(AppPlatform.GSM);
                  setDriveLogPoint(driveLogDTO, gsmPoints);
                }
              }
            }
          }
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }

    return driveLogDTOs;
  }

  @Override
  public DriveLogDTO getDriveLogDTOById(Long driveLogId) {
    if (driveLogId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    DriveLog driveLog = writer.getDriveLogById(driveLogId);
    return driveLog != null ? driveLog.toDTO() : null;
  }

  @Override
  public DriveLogDTO getDriveLogByAppUserNoAndId(String appUserNo, Long driveLogId) {
    if (StringUtil.isEmpty(appUserNo) || driveLogId == null) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    DriveLog driveLog = writer.getDriveLogByAppUserNoAndId(appUserNo, driveLogId);
    return driveLog != null ? driveLog.toDTO() : null;
  }

  public DriveLogDTO getDriveLogDTOWidthPlaceNoteById(Long driveLogId) {
    if (driveLogId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    DriveLogDTO driveLogDTO = writer.getById(DriveLog.class, driveLogId).toDTO();       //todo 加状态
    DriveLogPlaceNote driveLogPlaceNote = writer.getDriveLogPlaceNoteByLogId(driveLogDTO.getAppUserNo(), driveLogId);
    driveLogDTO.setPlaceNotes(driveLogPlaceNote.getPlaceNotes());
    driveLogDTO.generateCoordinate();
    if (ArrayUtil.isEmpty(driveLogDTO.getCoordinates())) return null;
    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
    AddressComponent addressComponent = geocodingService.gpsToAddress(driveLogDTO.getStartLat(), driveLogDTO.getStartLon());
    if (addressComponent != null) {
      driveLogDTO.setStartPlace(addressComponent.getStreetInfo());
      driveLogDTO.setDetailStartPlace(addressComponent.getStreetNumberInfo());
    }
    addressComponent = geocodingService.gpsToAddress(driveLogDTO.getEndLat(), driveLogDTO.getEndLon());
    if (addressComponent != null) {
      driveLogDTO.setEndPlace(addressComponent.getStreetInfo());
      driveLogDTO.setDetailEndPlace(addressComponent.getStreetNumberInfo());
    }
    List<Coordinate> coordinateList = geocodingService.coordinateGspToBaiDu(driveLogDTO.getCoordinates());
    driveLogDTO.setBaiDuCoordinate(coordinateList);
    if (CollectionUtil.isNotEmpty(coordinateList)) {
      StringBuilder sb = new StringBuilder();
      for (Coordinate coordinate : coordinateList) {
        sb.append(coordinate.getLat())
          .append(",")
          .append(coordinate.getLng())
          .append("|");
      }
      driveLogDTO.setPlaceNotes(sb.toString().substring(0, sb.length() - 1));
    }
    return driveLogDTO;
  }


  public List<DriveLogDTO> getDriveLogForShop(List<DriveLog> driveLogs, String appUserNo) {

    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    UserWriter writer = userDaoManager.getWriter();


    Map<Long, DriveLogPlaceNoteDTO> driveLogPlaceNoteMap = new HashMap<Long, DriveLogPlaceNoteDTO>();

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      Set<Long> toGetPlaceNoteIds = new HashSet<Long>();
      for (DriveLog driveLog : driveLogs) {
        if (DriveLogStatus.DRIVING.equals(driveLog.getStatus())) {
          toGetPlaceNoteIds.add(driveLog.getId());
        }
      }
      List<DriveLogPlaceNote> driveLogPlaceNotes = writer.getDriveLogPlaceNoteByLogIds(appUserNo, toGetPlaceNoteIds);
      if (CollectionUtils.isNotEmpty(driveLogPlaceNotes)) {
        for (DriveLogPlaceNote driveLogPlaceNote : driveLogPlaceNotes) {
          if (driveLogPlaceNote != null && driveLogPlaceNote.getDriveLogId() != null) {
            driveLogPlaceNoteMap.put(driveLogPlaceNote.getDriveLogId(), driveLogPlaceNote.toDTO());
          }
        }
      }
    }

    if (CollectionUtils.isNotEmpty(driveLogs)) {
      for (DriveLog driveLog : driveLogs) {
        if (driveLog != null) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          if (DriveLogStatus.DRIVING.equals(driveLogDTO.getStatus())) {
            DriveLogPlaceNoteDTO driveLogPlaceNoteDTO = driveLogPlaceNoteMap.get(driveLog.getId());
            driveLogDTO.setPlaceNoteDTO(driveLogPlaceNoteDTO);
            String imei = null;
            ObdUserVehicle obdUserVehicle = CollectionUtil.getFirst(writer.getObdUserVehicleByAppUserNo(appUserNo));
            if (obdUserVehicle != null && obdUserVehicle.getObdId() != null) {
              OBD obd = writer.getById(OBD.class, obdUserVehicle.getObdId());
              if (obd != null && StringUtils.isNotBlank(obd.getImei())) {
                imei = obd.getImei();
              }
            }
            IGsmPointService gsmPointService = ServiceManager.getService(IGsmPointService.class);
            List<GsmPoint> gsmPoints = gsmPointService.getGsmPointByIMeiAndUploadTime(imei,
              driveLogDTO.getStartTime(), driveLogDTO.getEndTime());

            driveLogDTO.setAppPlatform(AppPlatform.GSM);
            setDriveLogPoint(driveLogDTO, gsmPoints);
          }
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;
  }

  @Override
  public List<DriveLogDTO> getDriveLogDTOsByTime(String appUserNo, Long startTime, Long endTime) {
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    if (StringUtils.isNotEmpty(appUserNo)) {
      UserWriter writer = userDaoManager.getWriter();
      List<DriveLog> driveLogs = writer.getDriveLogByStartTime(appUserNo, startTime, endTime);
      if (CollectionUtils.isNotEmpty(driveLogs)) {
        for (DriveLog driveLog : driveLogs) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;
  }

  @Override
  public List<DriveLogDTO> getDriveLogDTOsByTime_wx(String appUserNo, Long startTime, Long endTime) {
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    if (StringUtils.isNotEmpty(appUserNo)) {
      UserWriter writer = userDaoManager.getWriter();
      List<DriveLog> driveLogs = writer.getDriveLogByStartTime_wx(appUserNo, startTime, endTime);
      if (CollectionUtils.isNotEmpty(driveLogs)) {
        for (DriveLog driveLog : driveLogs) {
          DriveLogDTO driveLogDTO = driveLog.toDTO();
          driveLogDTOs.add(driveLogDTO);
        }
      }
    }
    return driveLogDTOs;
  }

  @Override
  public List<DriveLogDTO> getLastDriveLog(String appUserNo, int limit) {
    if (StringUtils.isEmpty(appUserNo)) {
      return null;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<DriveLog> driveLogs = writer.getLastDriveLog(appUserNo, limit);
    if (CollectionUtils.isEmpty(driveLogs)) {
      return null;
    }
    List<DriveLogDTO> driveLogDTOs = new ArrayList<DriveLogDTO>();
    for (DriveLog driveLog : driveLogs) {
      DriveLogDTO driveLogDTO = driveLog.toDTO();
      driveLogDTOs.add(driveLogDTO);
    }
    return driveLogDTOs;
  }

  /**
   * 根据appUserNo获取行车轨迹
   *
   * @param appUserNo
   * @return List<DriveLogDTO>
   */
  @Override
  public List<DriveLogDTO> getDriveLogDTOList(String appUserNo, Long startTime, Long endTime) {
    AddressComponent addressComponent_start = null;
    AddressComponent addressComponent_end = null;
    IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
    IGeocodingService iGeocodingService = ServiceManager.getService(IGeocodingService.class);
    List<DriveLogDTO> driveLogDTOList = null;
    List<DriveLogDTO> driveLogDTOList_time = new ArrayList<DriveLogDTO>(); //点火时间和熄火时间相同的轨迹
    driveLogDTOList = driveLogService.getDriveLogDTOsByTime_wx(appUserNo, startTime, endTime);
    for (DriveLogDTO driveLogDTO : driveLogDTOList) {
      addressComponent_start = iGeocodingService.gpsToAddress(driveLogDTO.getStartLat(), driveLogDTO.getStartLon());
      addressComponent_end = iGeocodingService.gpsToAddress(driveLogDTO.getEndLat(), driveLogDTO.getEndLon());
      if (addressComponent_start == null || StringUtil.isEmpty(addressComponent_start.getCity())) {
        driveLogDTO.setStartCity("");
        driveLogDTO.setStartPlace("位置未定位成功");
      } else {
        driveLogDTO.setStartCity(addressComponent_start.getCity());
      }
      if (addressComponent_end == null || StringUtil.isEmpty(addressComponent_end.getCity())) {
        driveLogDTO.setEndCity("");
        driveLogDTO.setEndPlace("位置未定位成功");
      } else {
        driveLogDTO.setEndCity(addressComponent_end.getCity());
      }
      //行车轨迹相关数据离谱显示处理
      //公里数
      driveLogDTO.setDistance(driveLogDTO.getDistance() == null ? 0 : driveLogDTO.getDistance());
      if (driveLogDTO.getDistance() < 0 || driveLogDTO.getDistance() > 10000) {
        driveLogDTO.setDistance(0.0);
      }
      //油耗
      driveLogDTO.setOilCost(driveLogDTO.getOilCost() == null ? 0 : driveLogDTO.getOilCost());
      if (driveLogDTO.getOilCost() < 0 || driveLogDTO.getOilCost() < 1 || driveLogDTO.getOilCost() > 10000) {
        driveLogDTO.setOilCost(0.0);
      }
      if (driveLogDTO.getOilCost() != null) {
        if (driveLogDTO.getOilCost().toString().length() > 6) {
          driveLogDTO.setOilCost(0.0);
        }
      }
      //时间
      if (driveLogDTO.getTravelTime() < 0 || driveLogDTO.getTravelTime() > 604800) { //行程时间大于7天置0
        driveLogDTO.setTravelTimeStr("0");
      }
      if (driveLogDTO.getStartTime().equals(driveLogDTO.getEndTime())) {
        driveLogDTOList_time.add(driveLogDTO);
      }
    }
    if (CollectionUtil.isNotEmpty(driveLogDTOList_time)) {  //过滤掉点火和熄火时间相同的轨迹
      driveLogDTOList.removeAll(driveLogDTOList_time);
    }
    return driveLogDTOList;
  }

  @Override
  public void saveOrUpdateDriveLog(DriveLogDTO driveLogDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    DriveLog driveLog = null;
    try {
      if (driveLogDTO.getId() != null) {
        driveLog = writer.getById(DriveLog.class, driveLogDTO.getId());
      } else {
        driveLog = new DriveLog();
      }
      driveLog.fromDTO(driveLogDTO);
      writer.saveOrUpdate(driveLog);
      writer.commit(status);
      LOG.info("saveOrUpdateDriveLog success");
      driveLogDTO.setId(driveLog.getId());
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void generateDriveLog(DriveLogDTO driveLogDTO) throws IOException {
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    AppVehicle appVehicle = CollectionUtil.getFirst(appUserService.getAppVehicleByAppUserNo(driveLogDTO.getAppUserNo()));
    if (appVehicle == null) {
      LOG.error("{} 对应AppVehicle不存在,", driveLogDTO.getAppUserNo());
      return;
    }
    driveLogDTO.setVehicleNo(appVehicle.getVehicleNo());                            //车牌号
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      DriveLog driveLog = new DriveLog();
      driveLogDTO.setStatus(DriveLogStatus.ENABLED);
      driveLogDTO.setDriveStatStatus(DriveStatStatus.UN_STATISTIC);
      driveLog.fromDTO(driveLogDTO);
      //当前油价
      // Double oilPrice = NumberUtil.doubleVal(writer.getAppUserConfigByName(driveLog.getAppUserNo(),
//  AppUserConfigConstant.OIL_PRICE).getValue());
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String gasoline_price = configService.getConfig("gasoline_price", ShopConstant.BC_SHOP_ID);
      if (StringUtil.isNotEmpty(gasoline_price)) {
        Double oilPrice = NumberUtil.doubleVal(gasoline_price);
        driveLogDTO.setTotalOilMoney(NumberUtil.round(oilPrice * driveLogDTO.getOilCost()));
      }
      writer.save(driveLog);
      //油耗信息
      Double oilWear = driveLogDTO.getOilCost();
      if (oilWear > NumberUtil.doubleVal(appVehicle.getWorstOilWear())) {
        appVehicle.setWorstOilWear(oilWear);
      }
      if (oilWear < NumberUtil.doubleVal(appVehicle.getBestOilWear()) || NumberUtil.doubleVal(appVehicle.getBestOilWear()) < 0.0001) {
        appVehicle.setBestOilWear(oilWear);
      }
      //计算平均油耗
      calcAvgOilWear(appVehicle, writer);
      //保存 driveLogPlaceNote
      DriveLogPlaceNote driveLogPlaceNote = new DriveLogPlaceNote();
      driveLogPlaceNote.fromDriveLogDTO_mirror(driveLog.toDTO());
      driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
      writer.save(driveLogPlaceNote);
      writer.commit(status);
      LOG.info("generateDriveLog success");
    } finally {
      writer.rollback(status);
    }
    //更新车况
    GsmVehicleDataDTO gsmVehicleDataDTO = ServiceManager.getService(IGSMVehicleDataService.class).getLastGsmVehicleData(appVehicle.getAppUserNo());
    if (gsmVehicleDataDTO != null) {
      appUserService.updateVehicle(gsmVehicleDataDTO.getAppUserNo(), NumberUtil.doubleVal(gsmVehicleDataDTO.getCurMil()));
    }

  }

  /**
   * 综合油耗，平均油耗
   * 取最近1000条行车油耗计算平均
   *
   * @param appVehicle
   * @param writer
   */
  private void calcAvgOilWear(AppVehicle appVehicle, UserWriter writer) {
    String appUserNo = appVehicle.getAppUserNo();
    IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
    List<DriveLogDTO> driveLogDTOList = driveLogService.getLastDriveLog(appUserNo, 1000);
    if (CollectionUtil.isEmpty(driveLogDTOList)) return;
    Double total = 0d;
    for (DriveLogDTO driveLogDTO : driveLogDTOList) {
      total = NumberUtil.add(total, driveLogDTO.getOilCost());
    }
    Double avgOilWear = total / driveLogDTOList.size();
    appVehicle.setAvgOilWear(avgOilWear);
    writer.saveOrUpdate(appVehicle);
  }


}
