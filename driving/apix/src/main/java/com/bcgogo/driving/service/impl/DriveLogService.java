package com.bcgogo.driving.service.impl;

import com.bcgogo.driving.dao.*;
import com.bcgogo.driving.model.*;
import com.bcgogo.driving.service.IDriveLogService;
import com.bcgogo.driving.service.IGSMVehicleDataService;
import com.bcgogo.driving.service.IGeocodingService;
import com.bcgogo.pojox.api.*;
import com.bcgogo.pojox.api.response.HttpResponse;
import com.bcgogo.pojox.config.AddressComponent;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.constant.XConstant;
import com.bcgogo.pojox.enums.DriveLogStatus;
import com.bcgogo.pojox.enums.DriveStatStatus;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.pojox.enums.app.MessageCode;
import com.bcgogo.pojox.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-21
 * Time: 下午2:33
 */
@Service
public class DriveLogService implements IDriveLogService {
  private static final Logger LOG = LoggerFactory.getLogger(DriveLogService.class);

  @Autowired
  private DriveLogDao driveLogDao;
  @Autowired
  private AppUserCustomerDao appUserCustomerDao;
  @Autowired
  private AppVehicleDao appVehicleDao;
  @Autowired
  private VehicleDao vehicleDao;
  @Autowired
  private DriveLogPlaceNoteDao driveLogPlaceNoteDao;
  @Autowired
  private IGeocodingService geocodingService;
  @Autowired
  private IGSMVehicleDataService gvDataService;

  @Override
  public void saveOrUpdateDriveLog(DriveLogDTO driveLogDTO) {
    DriveLog driveLog = null;
    if (driveLogDTO.getId() != null) {
      driveLog = driveLogDao.getById(driveLogDTO.getId());
    } else {
      driveLog = new DriveLog();
    }
    driveLog.fromDTO(driveLogDTO);
    driveLogDao.saveOrUpdate(driveLog);
    LOG.info("saveOrUpdateDriveLog success");
    driveLogDTO.setId(driveLog.getId());

  }

  private DriveLogDTO getDriveLogByUuid(String uuid) {
    DriveLog driveLog = driveLogDao.getDriveLogByUuid(uuid);
    return driveLog != null ? driveLog.toDTO() : null;
  }


  private DriveLogDTO getDrivingDriveLog(String appUserNo) {
    DriveLog driveLog = driveLogDao.getDrivingDriveLog(appUserNo);
    return driveLog != null ? driveLog.toDTO() : null;
  }

  /**
   * 鹏奥达OBD---生成driveLog
   *
   * @param dataDTO
   */
  public void handleDriveLog(GsmVehicleDataDTO dataDTO) {
    LOG.debug("handleDriveLog,dataDTO:{}", JsonUtil.objectToJson(dataDTO));
    DriveLogDTO driveLogDTO = getDriveLogByUuid(dataDTO.getUuid());
    LOG.debug("uuid:{},get driveLogDTO is empty?", dataDTO.getUuid(), driveLogDTO == null);
    if (driveLogDTO == null) {
      LOG.debug("生成行驶中的车况,imei:{},vehicle_status：{}", dataDTO.getImei(), dataDTO.getVehicleStatus());
      driveLogDTO = new DriveLogDTO();
      driveLogDTO.setAppDriveLogId(dataDTO.getUuid());
      driveLogDTO.setStatus(DriveLogStatus.DRIVING);
      driveLogDTO.setDriveStatStatus(DriveStatStatus.UN_STATISTIC);
      driveLogDTO.setStartTime(dataDTO.getUploadTime());
      driveLogDTO.setStartLat(dataDTO.getLat());
      driveLogDTO.setStartLon(dataDTO.getLon());
      driveLogDTO.setStartPlace(geocodingService.gpsCoordinate2FullAddress(driveLogDTO.getStartLat(), driveLogDTO.getStartLon()));
      driveLogDTO.setDistance(0d);
      driveLogDTO.setTravelTime(0l);
    }
    driveLogDTO.setLastUpdateTime(System.currentTimeMillis());
    driveLogDTO.setAppUserNo(dataDTO.getAppUserNo());
    driveLogDTO.setImei(dataDTO.getImei());
    driveLogDTO.setEndTime(dataDTO.getUploadTime());
    driveLogDTO.setEndLat(dataDTO.getLat());
    driveLogDTO.setEndLon(dataDTO.getLon());
    driveLogDTO.setEndPlace(geocodingService.gpsCoordinate2FullAddress(driveLogDTO.getEndLat(), driveLogDTO.getEndLon()));
    AppVehicle appVehicle = appVehicleDao.getAppVehicleByAppUserNo(dataDTO.getAppUserNo());
    driveLogDTO.setVehicleNo(appVehicle != null ? appVehicle.getVehicleNo() : null);
    driveLogDTO.setOilCost(NumberUtil.doubleVal(dataDTO.getOilWear())); //耗油量（剩余油量相减）
    driveLogDTO.setTravelTime((dataDTO.getUploadTime() - driveLogDTO.getStartTime()) / 1000);
    List<GsmVehicleDataDTO> allDataDTO = gvDataService.getGsmVehicleDataDTOByUUID(driveLogDTO.getAppDriveLogId());
    //踩点信息
    StringBuilder sb = new StringBuilder();
    if (CollectionUtil.isNotEmpty(allDataDTO)) {
      for (GsmVehicleDataDTO gsmVehicleDataDTO : allDataDTO) {
        sb.append(gsmVehicleDataDTO.getLat())
          .append(",")
          .append(gsmVehicleDataDTO.getLon())
          .append(",")
          .append(gsmVehicleDataDTO.getUploadTime())
          .append("|");
      }
    }
    driveLogDTO.setPlaceNotes(sb.toString());
    driveLogDTO.setDistance(GpsUtil.calPlaceNoteDistance(sb.toString())); //路程(通过GPS计算)
    if (GSMConstant.CUTOFF.equals(dataDTO.getVehicleStatus())) {
      LOG.debug("车辆熄火\ndriveLog,imei:{}", dataDTO.getImei());
      driveLogDTO.setStatus(DriveLogStatus.ENABLED);
    }
    saveOrUpdateDriveLog(driveLogDTO);
    //保存 driveLogPlaceNote
    DriveLogPlaceNote driveLogPlaceNote = driveLogPlaceNoteDao.getDriveLogPlaceNoteByDriveLogId(driveLogDTO.getId());
    if (driveLogPlaceNote == null) {
      driveLogPlaceNote = new DriveLogPlaceNote();
      driveLogPlaceNote.setAppUserNo(driveLogDTO.getAppUserNo());
      driveLogPlaceNote.setDriveLogId(driveLogDTO.getId());
      driveLogPlaceNote.setAppPlatform(AppPlatform.GSM);
      driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
      driveLogPlaceNoteDao.save(driveLogPlaceNote);
    } else {
      driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
      driveLogPlaceNoteDao.update(driveLogPlaceNote);
    }
    //更新车辆里程信息
    try {
      AppUserCustomer appUserCustomer = appUserCustomerDao.getAppUserCustomerByAppUserNo(driveLogDTO.getAppUserNo());
      Vehicle vehicle = vehicleDao.getById(appUserCustomer.getShopVehicleId());
      BigDecimal distance = new BigDecimal(driveLogDTO.getDistance());
      BigDecimal obdMileage = new BigDecimal(vehicle.getObdMileage() == null ? "0" : String.valueOf(vehicle.getObdMileage()));
      vehicle.setObdMileage(obdMileage.add(distance).doubleValue());
      vehicleDao.update(vehicle);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }

  }

  /**
   * 后视镜----生成driveLog
   *
   * @param cutOffData
   * @throws IOException
   * @throws IllegalAccessException
   */
  @Override
  public void saveDriveLog(GsmVehicleDataDTO cutOffData) throws IOException, IllegalAccessException {
    if (cutOffData == null) {
      LOG.info("CUTOFF GsmVehicleDate is empty");
      return;
    }
    LOG.debug("行程结束，生成行车轨迹,appUserNo:{}",cutOffData.getAppUserNo());
    StopWatchUtil sw = new StopWatchUtil("generationDriveLog", "start");
    GsmVehicleDataDTO fireUpData = gvDataService.getFireUpGsmVehicleDataDTO(cutOffData.getUuid(), GSMConstant.FIRE_UP);
    if (fireUpData == null) {
      return;
    }
    //点火-熄火这段时间内所有的车况数据
    List<GsmVehicleDataDTO> gsmVehicleDataList = gvDataService.getGsmVehicleDataDTOByUUID(cutOffData.getUuid());
    DriveLogDTO driveLogDTO = new DriveLogDTO();
    driveLogDTO.setAppUserNo(cutOffData.getAppUserNo());
    driveLogDTO.setLastUpdateTime(System.currentTimeMillis());
    AddressComponent addressComponent_start = geocodingService.gpsToAddress(fireUpData.getLat(), fireUpData.getLon());
    if (addressComponent_start != null) {
      driveLogDTO.setStartPlace(addressComponent_start.getAddress());                                  //开始地址
    }
    AddressComponent addressComponent_end = geocodingService.gpsToAddress(cutOffData.getLat(), cutOffData.getLon());
    if (addressComponent_end != null) {
      driveLogDTO.setEndPlace(addressComponent_end.getAddress());                                    //结束地址
    }
    driveLogDTO.setStartTime(fireUpData.getUploadTime()); //开始时间
    driveLogDTO.setStartLat(fireUpData.getLat());         //开始维度
    driveLogDTO.setStartLon(fireUpData.getLon());         //开始经度
    driveLogDTO.setEndTime(cutOffData.getUploadTime());     //结束时间
    driveLogDTO.setEndLat(cutOffData.getLat());             //结束维度
    driveLogDTO.setEndLon(cutOffData.getLon());             //结束经度
    driveLogDTO.setAppDriveLogId(fireUpData.getUuid());
    AppVehicle appVehicle = appVehicleDao.getAppVehicleByAppUserNo(driveLogDTO.getAppUserNo());
    driveLogDTO.setVehicleNo(appVehicle != null ? appVehicle.getVehicleNo() : null);
    driveLogDTO.setOilCost(NumberUtil.subtraction(fireUpData.getrOilMass(), cutOffData.getrOilMass())); //耗油量（剩余油量相减）
    driveLogDTO.setTravelTime((cutOffData.getUploadTime() - fireUpData.getUploadTime()) / 1000);  //行程时间
    //踩点信息
    StringBuilder sb = new StringBuilder();
    for (GsmVehicleDataDTO gsmVehicleDataDTO : gsmVehicleDataList) {
      sb.append(gsmVehicleDataDTO.getLat())
        .append(",")
        .append(gsmVehicleDataDTO.getLon())
        .append(",")
        .append(gsmVehicleDataDTO.getUploadTime())
        .append("|");
    }
    driveLogDTO.setPlaceNotes(sb.toString());
    Double distance = NumberUtil.subtraction(cutOffData.getCurMil(), fireUpData.getCurMil());//路程(仪表里程数相减)
    if (distance <= 0.01) {
      distance = GpsUtil.calPlaceNoteDistance(sb.toString()); //路程(通过GPS计算)
    }
    driveLogDTO.setDistance(distance);
    driveLogDTO.setStatus(DriveLogStatus.ENABLED);
    saveOrUpdateDriveLog(driveLogDTO);
    //save DriveLogPlaceNote
    DriveLogPlaceNote driveLogPlaceNote = new DriveLogPlaceNote();
    driveLogPlaceNote.setAppUserNo(driveLogDTO.getAppUserNo());
    driveLogPlaceNote.setDriveLogId(driveLogDTO.getId());
    driveLogPlaceNote.setAppPlatform(AppPlatform.OBD);
    driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
    driveLogPlaceNoteDao.save(driveLogPlaceNote);
    //更新车辆里程信息
    try {
      LOG.debug("更新车辆里程信息,appUserNo:{}",cutOffData.getAppUserNo());
      AppUserCustomer appUserCustomer = appUserCustomerDao.getAppUserCustomerByAppUserNo(driveLogDTO.getAppUserNo());
      Vehicle vehicle = vehicleDao.getById(appUserCustomer.getShopVehicleId());
      BigDecimal  distance_big = new BigDecimal(driveLogDTO.getDistance());
      BigDecimal obdMileage = new BigDecimal(vehicle.getObdMileage() == null ? "0" : String.valueOf(vehicle.getObdMileage()));
      vehicle.setObdMileage(obdMileage.add(distance_big).doubleValue());
      vehicleDao.update(vehicle);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    sw.stopAndPrintLog();
  }

  /**
   * 2s----生成driveLog
   *
   * @param cutOffData
   * @throws IOException
   * @throws IllegalAccessException
   */
  @Override
  public void generationDriveLog(GsmTBoxDataDTO cutOffData) throws IOException, IllegalAccessException {
    if (cutOffData == null) {
      LOG.info("CUTOFF GsmVehicleDate is empty");
      return;
    }
    StopWatchUtil sw = new StopWatchUtil("generationDriveLog", "start");
    GsmTBoxDataDTO fireUpData = gvDataService.getFireUpGsmTBoxDataDTO(cutOffData.getUuid(), GSMConstant.FIRE_UP);
    if (fireUpData == null) {
      return;
    }
    //点火-熄火这段时间内所有的车况数据
    List<GsmVehicleDataDTO> gsmVehicleDataList = gvDataService.getGsmVehicleDataDTOByUUID(cutOffData.getUuid());
    DriveLogDTO driveLogDTO = new DriveLogDTO();
    driveLogDTO.setAppUserNo(cutOffData.getAppUserNo());
    driveLogDTO.setLastUpdateTime(System.currentTimeMillis());
    AddressComponent addressComponent_start = geocodingService.gpsToAddress(fireUpData.getLat(), fireUpData.getLon());
    if (addressComponent_start != null) {
      driveLogDTO.setStartPlace(addressComponent_start.getAddress());                                  //开始地址
    }
    AddressComponent addressComponent_end = geocodingService.gpsToAddress(cutOffData.getLat(), cutOffData.getLon());
    if (addressComponent_end != null) {
      driveLogDTO.setEndPlace(addressComponent_end.getAddress());                                    //结束地址
    }
    driveLogDTO.setStartTime(fireUpData.getUploadTime()); //开始时间
    driveLogDTO.setStartLat(fireUpData.getLat());         //开始维度
    driveLogDTO.setStartLon(fireUpData.getLon());         //开始经度
    driveLogDTO.setEndTime(cutOffData.getUploadTime());     //结束时间
    driveLogDTO.setEndLat(cutOffData.getLat());             //结束维度
    driveLogDTO.setEndLon(cutOffData.getLon());             //结束经度
    driveLogDTO.setAppDriveLogId(fireUpData.getUuid());
    AppVehicle appVehicle = appVehicleDao.getAppVehicleByAppUserNo(driveLogDTO.getAppUserNo());
    driveLogDTO.setVehicleNo(appVehicle != null ? appVehicle.getVehicleNo() : null);
    driveLogDTO.setOilCost(cutOffData.getOilWear()); //耗油量（剩余油量相减）
    driveLogDTO.setTravelTime(cutOffData.getTravelTime());  //行程时间
    //踩点信息
    StringBuilder sb = new StringBuilder();
    for (GsmVehicleDataDTO gsmVehicleDataDTO : gsmVehicleDataList) {
      sb.append(gsmVehicleDataDTO.getLat())
        .append(",")
        .append(gsmVehicleDataDTO.getLon())
        .append(",")
        .append(gsmVehicleDataDTO.getUploadTime())
        .append("|");
    }
    driveLogDTO.setPlaceNotes(sb.toString());
    Double distance = cutOffData.getMile();//路程(仪表里程数相减)
    if (distance <= 0.01) {
      distance = GpsUtil.calPlaceNoteDistance(sb.toString()); //路程(通过GPS计算)
    }
    driveLogDTO.setDistance(distance);
    driveLogDTO.setStatus(DriveLogStatus.ENABLED);
    saveOrUpdateDriveLog(driveLogDTO);
      //save DriveLogPlaceNote
    DriveLogPlaceNote driveLogPlaceNote = new DriveLogPlaceNote();
    driveLogPlaceNote.setAppUserNo(driveLogDTO.getAppUserNo());
    driveLogPlaceNote.setDriveLogId(driveLogDTO.getId());
    driveLogPlaceNote.setAppPlatform(AppPlatform.OBD);
    driveLogPlaceNote.setPlaceNotes(driveLogDTO.getPlaceNotes());
    driveLogPlaceNoteDao.save(driveLogPlaceNote);
     //更新车辆里程信息
    try {
      AppUserCustomer appUserCustomer = appUserCustomerDao.getAppUserCustomerByAppUserNo(driveLogDTO.getAppUserNo());
      Vehicle vehicle = vehicleDao.getById(appUserCustomer.getShopVehicleId());
      BigDecimal distance_big = new BigDecimal(driveLogDTO.getDistance());
      BigDecimal obdMileage = new BigDecimal(vehicle.getObdMileage() == null ? "0" : String.valueOf(vehicle.getObdMileage()));
      vehicle.setObdMileage(obdMileage.add(distance_big).doubleValue());
      vehicleDao.update(vehicle);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    sw.stopAndPrintLog();
  }

}
