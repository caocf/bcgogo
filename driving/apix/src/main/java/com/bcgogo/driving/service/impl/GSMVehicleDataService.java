package com.bcgogo.driving.service.impl;


import com.bcgogo.driving.dao.AppVehicleDao;
import com.bcgogo.driving.dao.XGsmTBoxDataDao;
import com.bcgogo.driving.dao.XGsmVehicleDataDao;
import com.bcgogo.driving.dao.XIllegalCityDao;
import com.bcgogo.driving.model.*;

import com.bcgogo.driving.service.IDriveLogService;
import com.bcgogo.driving.service.IGSMVehicleDataService;
import com.bcgogo.driving.service.IGeocodingService;
import com.bcgogo.driving.service.IJuheService;
import com.bcgogo.driving.socket.protocol.PGsmVehicleData;
import com.bcgogo.driving.socket.protocol.PGsmVehicleDataStart;

import com.bcgogo.pojox.api.*;
import com.bcgogo.pojox.api.response.HttpResponse;
import com.bcgogo.pojox.cache.MemCacheAdapter;
import com.bcgogo.pojox.config.AddressComponent;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.constant.XConstant;
import com.bcgogo.pojox.enums.DriveLogStatus;
import com.bcgogo.pojox.enums.app.AppUserType;
import com.bcgogo.pojox.enums.app.MessageCode;
import com.bcgogo.pojox.enums.etl.GsmVehicleStatus;
import com.bcgogo.pojox.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: zj
 * Date: 2015-4-28
 * Time: 15:47
 */
@Service
public class GSMVehicleDataService implements IGSMVehicleDataService {
  private static final Logger LOG = LoggerFactory.getLogger(GSMVehicleDataService.class);

  @Autowired
  private AppVehicleDao appVehicleDao;
  @Autowired
  private XGsmVehicleDataDao gsmVehicleDataDao;
  @Autowired
  private XGsmTBoxDataDao gsmTBoxDataDao;
  @Autowired
  private XIllegalCityDao illegalCityDao;
  @Autowired
  private IGeocodingService geocodingService;
  @Autowired
  private IJuheService juheService;

  @Override
  public PGsmVehicleDataStart generatePGsmVehicleDataStart(PGsmVehicleData vehicleData) {
    if (vehicleData == null) return null;
    PGsmVehicleDataStart dataStart = new PGsmVehicleDataStart();
    dataStart.setImei(vehicleData.getImei());
    dataStart.setUuid(UUID.randomUUID().toString());
    dataStart.setTimestamp(DateUtil.convertDateLongToDateString("HHmmss", System.currentTimeMillis()));
    dataStart.setDate(DateUtil.convertDateLongToDateString("ddMMyyy", System.currentTimeMillis()));
    dataStart.setLat(vehicleData.getLat());
    dataStart.setLon(vehicleData.getLon());
    return dataStart;
  }

  public String getCachePGDataUUID(String imei) {
    if (StringUtil.isEmpty(imei)) return null;
    String cacheVal = StringUtil.valueOf(MemCacheAdapter.get(GSMConstant.KEY_PREFIX_P_GSM_VEHICLE_DATA_START + imei));
    if (StringUtil.isEmpty(cacheVal)) {
      LOG.debug("cacheVal is empty,getPFireUpUUIDByImei");
      return getPFireUpUUIDByImei(imei);
    } else {
      LOG.debug("read cacheVal");
    }
    return cacheVal;
  }

  @Override
  public void saveOrUpdateGsmVehicleDataDTO(GsmVehicleDataDTO... gsmVehicleDataDTOs) {
    LOG.debug("---saveOrUpdateGsmVehicleDataDTO");
    GsmVehicleData gsmVehicleData = null;
    for (GsmVehicleDataDTO gsmVehicleDataDTO : gsmVehicleDataDTOs) {
      if (StringUtil.isNotEmpty(gsmVehicleDataDTO.getIdStr())) {
        gsmVehicleData = gsmVehicleDataDao.getById(GsmVehicleData.class, gsmVehicleDataDTO.getIdStr());
      } else {
        gsmVehicleData = new GsmVehicleData();
      }
      gsmVehicleData.fromDTO(gsmVehicleDataDTO);
      gsmVehicleDataDao.saveOrUpdate(gsmVehicleData);
    }
  }

  @Override
  public void saveOrUpdateGsmTBoxDataDTO(GsmTBoxDataDTO... GsmTBoxDataDTOs) {
    LOG.debug("---saveOrUpdateGsmTBoxDataDTO---------");
    GsmTBoxData gsmTBoxData = null;
    for (GsmTBoxDataDTO gsmTBoxDataDTO : GsmTBoxDataDTOs) {
      if (StringUtil.isNotEmpty(gsmTBoxDataDTO.getIdStr())) {
        gsmTBoxData = gsmTBoxDataDao.getById(GsmTBoxData.class, gsmTBoxDataDTO.getIdStr());
      } else {
        gsmTBoxData = new GsmTBoxData();
      }
      gsmTBoxData.fromDTO(gsmTBoxDataDTO);
      gsmTBoxDataDao.saveOrUpdate(gsmTBoxData);
    }
  }


  @Override
  public List<GsmVehicleDataDTO> getGsmVehicleDataDTO(GsmVehicleDataCondition condition) {
    List<GsmVehicleData> dataList = gsmVehicleDataDao.getGsmVehicleDataDTO(condition);
    if (CollectionUtil.isEmpty(dataList)) return null;
    List<GsmVehicleDataDTO> dataDTOList = new ArrayList<GsmVehicleDataDTO>();
    for (GsmVehicleData data : dataList) {
      dataDTOList.add(data.toDTO());
    }
    return dataDTOList;
  }

  @Override
  public GsmVehicleDataDTO getFireUpGsmVehicleDataDTO(String uuid, String vehicleStatus) {
    if (StringUtil.isEmpty(uuid) || StringUtil.isEmpty(vehicleStatus)) return null;
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setUuid(uuid);
    condition.setVehicleStatus(vehicleStatus);
    condition.setOrderBy("uploadTime,desc");
    GsmVehicleData data = CollectionUtil.getFirst(gsmVehicleDataDao.getGsmVehicleDataDTO(condition));
    return data != null ? data.toDTO() : null;
  }

   @Override
  public GsmTBoxDataDTO getFireUpGsmTBoxDataDTO(String uuid, String vehicleStatus) {
    if (StringUtil.isEmpty(uuid) || StringUtil.isEmpty(vehicleStatus)) return null;
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setUuid(uuid);
    condition.setVehicleStatus(vehicleStatus);
    condition.setOrderBy("uploadTime,desc");
    GsmTBoxData data = CollectionUtil.getFirst(gsmTBoxDataDao.getGsmTBoxData(condition));
    return data != null ? data.toDTO() : null;
  }


  @Override
  public List<GsmVehicleDataDTO> getGsmVehicleDataDTOByAppUserNo(String appUserNo) {
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setAppUserNo(appUserNo);
    return getGsmVehicleDataDTO(condition);
  }

  @Override
  public List<GsmVehicleDataDTO> getGsmVehicleDataDTOByUUID(String uuid) {
    if (StringUtil.isEmpty(uuid)) return null;
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setUuid(uuid);
    return getGsmVehicleDataDTO(condition);
  }

  @Override
  public String getPFireUpUUIDByImei(String imei) {
    if (StringUtil.isEmpty(imei)) return null;
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setImei(imei);
//    condition.setVehicleStatus(GSMConstant.FIRE_UP);
    condition.setOrderBy("uploadTime,desc");
    GsmVehicleData data = CollectionUtil.getFirst(gsmVehicleDataDao.getGsmVehicleDataDTO(condition));
    return data != null ? data.getUuid() + "_" + data.getAppUserNo() : null;
  }

  @Override
  public GsmVehicleDataDTO getLastGsmVehicleData(String appUserNo) {
    GsmVehicleData gsmVehicleData = gsmVehicleDataDao.getLastGsmVehicleData(appUserNo, null);
    return gsmVehicleData != null ? gsmVehicleData.toDTO() : null;
  }

  @Override
  public GsmVehicleDataDTO getGsmVehicleDataByUUidAndUpdateTime(String uuid, long upLoadTime) {
    GsmVehicleData gsmVehicleData = gsmVehicleDataDao.getGsmVehicleDataByUUidAndUpdateTime(uuid, upLoadTime);
    return gsmVehicleData != null ? gsmVehicleData.toDTO() : null;
  }

  @Override
  public void gpsToCityMask() {
    IllegalCity illegalCity = null;
    AddressComponent addressComponent = null;
    String cityName = "";
    String cityCode = "";
    String lat = "";
    String lon = "";
    StopWatchUtil sw = new StopWatchUtil("gpsToCityMask", "start");
    List<GsmVehicleData> gsmVehicleDataList = gsmVehicleDataDao.getGsmVehicleDataByGpsCityStatus();
    for (GsmVehicleData gsmVehicleData : gsmVehicleDataList) {
      if (StringUtil.isNotEmpty(gsmVehicleData.getLat()) && gsmVehicleData.getLat().length() > 10) {
        lat = gsmVehicleData.getLat().substring(0, 10);
      } else {
        lat = gsmVehicleData.getLat();
      }
      if (StringUtil.isNotEmpty(gsmVehicleData.getLon()) && gsmVehicleData.getLon().length() > 10) {
        lon = gsmVehicleData.getLon().substring(0, 10);
      } else {
        lon = gsmVehicleData.getLon();
      }
      if (!("0.0".equals(lat) && "0.0".equals(lon))) {
        addressComponent = geocodingService.gpsToAddress(lat, lon);
      }
      if (addressComponent == null || StringUtil.isEmpty(addressComponent.getCity())) {
        continue;
      }
      cityName = addressComponent.getCity();
      cityName = cityName.replace("市", "");
      if (StringUtil.isNotEmpty(gsmVehicleData.getAppUserNo())) {
        illegalCity = gsmVehicleDataDao.getIllegalCityByAppUserNo(gsmVehicleData.getAppUserNo());
      }
      if (illegalCity != null) {
        if (illegalCity.getJuheCityName().indexOf(cityName) == -1) {//illegalCity中不存在该城市，更新illegalCity
          illegalCity.setJuheCityName(illegalCity.getJuheCityName() + "," + cityName);
          cityCode = getCityCodeByCityName(cityName);
          if (StringUtil.isNotEmpty(cityCode)) {
            illegalCity.setJuheCityCode(illegalCity.getJuheCityCode() + "," + cityCode);
          }
          illegalCityDao.update(illegalCity);
        }
        gsmVehicleData.setGpsCityStatus(GsmVehicleStatus.HANDLED);
        gsmVehicleDataDao.update(gsmVehicleData);
      } else {
        illegalCity = new IllegalCity();
        illegalCity.setAppUserNo(gsmVehicleData.getAppUserNo());
        illegalCity.setJuheCityName(cityName);
        cityCode = getCityCodeByCityName(cityName);
        if (StringUtil.isNotEmpty(cityCode)) {
          illegalCity.setJuheCityCode(cityCode);
        }
        illegalCityDao.save(illegalCity);
        gsmVehicleData.setGpsCityStatus(GsmVehicleStatus.HANDLED);
        gsmVehicleDataDao.update(gsmVehicleData);
      }

    }
    sw.stopAndPrintLog();
  }

  public String getCityCodeByCityName(String cityName) {
    String cityCode = "";
    JuheViolateRegulationCitySearchCondition juheViolateRegulationCitySearchCondition = null;
    juheViolateRegulationCitySearchCondition
      = juheService.getJuheViolateRegulationCitySearchConditionByCityName(cityName);
    if (juheViolateRegulationCitySearchCondition != null) {
      cityCode = juheViolateRegulationCitySearchCondition.getCityCode();
    }
    return cityCode;
  }

  @Override
  public IllegalCity getIllegalCityByAppUserNo(String appUserNo) {
    IllegalCity illegalCity = gsmVehicleDataDao.getIllegalCityByAppUserNo(appUserNo);
    return illegalCity;
  }


}

