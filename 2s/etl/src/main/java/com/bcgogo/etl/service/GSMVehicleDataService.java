package com.bcgogo.etl.service;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DriveLogDTO;
import com.bcgogo.api.GsmVehicleDataCondition;
import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.baidu.model.AddressComponent;
import com.bcgogo.baidu.service.IGeocodingService;
import com.bcgogo.config.model.JuheViolateRegulationCitySearchCondition;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.etl.common.XConstant;
import com.bcgogo.etl.dao.GsmVehicleDataDao;
import com.bcgogo.etl.dao.IllegalCityDao;
import com.bcgogo.etl.model.GsmVehicleData;
import com.bcgogo.etl.model.IllegalCity;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: zj
 * Date: 2015-4-28
 * Time: 15:47
 */
@Component
public class GSMVehicleDataService implements IGSMVehicleDataService {
  private static final Logger LOG = LoggerFactory.getLogger(GSMVehicleDataService.class);

  //
  @Autowired
  private GsmVehicleDataDao gsmVehicleDataDao;
  @Autowired
  private IllegalCityDao illegalCityDao;


  public String getPGsmVehicleDataUUID(String imei) {
    if (StringUtil.isEmpty(imei)) return null;
    String uuid = StringUtil.valueOf(MemCacheAdapter.get(GSMConstant.KEY_PREFIX_P_GSM_VEHICLE_DATA_START + imei));
    if (StringUtil.isEmpty(uuid)) {
      return getPFireUpUUIDByImei(imei);
    }
    return uuid;
  }

  public void saveOrUpdateGsmVehicleDataDTO(GsmVehicleDataDTO... gsmVehicleDataDTOs) {
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
  public List<GsmVehicleDataDTO> getGsmVehicleDataDTOByAppUserNo(String appUserNo) {
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setAppUserNo(appUserNo);
    return getGsmVehicleDataDTO(condition);
  }

  @Override
  public List<GsmVehicleDataDTO> getGsmVehicleDataDTOByUUID(String uuid) {
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setUuid(uuid);
    return getGsmVehicleDataDTO(condition);
  }

  @Override
  public String getPFireUpUUIDByImei(String imei) {
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setImei(imei);
    condition.setVehicleStatus(GSMConstant.FIRE_UP);
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
  public void generationDriveLogEveryTime(GsmVehicleDataDTO cutOffData) throws IOException, IllegalAccessException {
    if (cutOffData == null) {
      LOG.info("CUTOFF GsmVehicleDate is empty");
      return;
    }
    StopWatchUtil sw = new StopWatchUtil("generationDriveLog", "start");
    GsmVehicleDataDTO fireUpData = getFireUpGsmVehicleDataDTO(cutOffData.getUuid(), GSMConstant.FIRE_UP);
    if (fireUpData == null) {
      return;
    }
    //点火-熄火这段时间内所有的车况数据
    List<GsmVehicleDataDTO> gsmVehicleDataList_all = getGsmVehicleDataDTOByUUID(cutOffData.getUuid());
    saveDriveLog(fireUpData, cutOffData, gsmVehicleDataList_all);
    sw.stopAndPrintLog();
  }

  //生成driveLog
  public void saveDriveLog(GsmVehicleDataDTO gsmVehicleData_start, GsmVehicleDataDTO gsmVehicleData_end, List<GsmVehicleDataDTO> gsmVehicleDataList_all) throws IOException, IllegalAccessException {
    IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
    //生成行车日志
    DriveLogDTO driveLogDTO = new DriveLogDTO();
    driveLogDTO.setAppUserNo(gsmVehicleData_start.getAppUserNo());
    driveLogDTO.setLastUpdateTime(System.currentTimeMillis());
    AddressComponent addressComponent_start = geocodingService.gpsToAddress(gsmVehicleData_start.getLat(), gsmVehicleData_start.getLon());
    if (addressComponent_start != null) {
      driveLogDTO.setStartPlace(addressComponent_start.getAddress());                                  //开始地址
    }
    AddressComponent addressComponent_end = geocodingService.gpsToAddress(gsmVehicleData_end.getLat(), gsmVehicleData_end.getLon());
    if (addressComponent_end != null) {
      driveLogDTO.setEndPlace(addressComponent_end.getAddress());                                    //结束地址
    }
    driveLogDTO.setStartTime(gsmVehicleData_start.getUploadTime()); //开始时间
    driveLogDTO.setStartLat(gsmVehicleData_start.getLat());         //开始维度
    driveLogDTO.setStartLon(gsmVehicleData_start.getLon());         //开始经度
    driveLogDTO.setEndTime(gsmVehicleData_end.getUploadTime());     //结束时间
    driveLogDTO.setEndLat(gsmVehicleData_end.getLat());             //结束维度
    driveLogDTO.setEndLon(gsmVehicleData_end.getLon());             //结束经度

    if (AppUserType.GSM.equals(gsmVehicleData_end.getUserType())) {
      driveLogDTO.setDistance(NumberUtil.doubleVal(gsmVehicleData_end.getMile())); //路程(仪表里程数相减)
      driveLogDTO.setOilCost(NumberUtil.doubleVal(gsmVehicleData_end.getOilWear())); //耗油量（剩余油量相减）
      driveLogDTO.setTravelTime(gsmVehicleData_end.getTravelTime());
    } else {
      driveLogDTO.setDistance(NumberUtil.subtract(gsmVehicleData_end.getCurMil(), gsmVehicleData_start.getCurMil())); //路程(仪表里程数相减)
      driveLogDTO.setOilCost(NumberUtil.subtract(gsmVehicleData_start.getrOilMass(), gsmVehicleData_end.getrOilMass())); //耗油量（剩余油量相减）
      driveLogDTO.setTravelTime((gsmVehicleData_end.getUploadTime() - gsmVehicleData_start.getUploadTime()) / 1000);  //行程时间
    }
    //踩点信息
    StringBuilder sb = new StringBuilder();
    for (GsmVehicleDataDTO gsmVehicleDataDTO : gsmVehicleDataList_all) {
      sb.append(gsmVehicleDataDTO.getLat())
        .append(",")
        .append(gsmVehicleDataDTO.getLon())
        .append(",")
        .append(gsmVehicleDataDTO.getUploadTime())
        .append("|");
    }
    driveLogDTO.setPlaceNotes(sb.toString());
    String url = XConstant.URL_OPEN_SAVE_DRIVE_LOG;
    HttpResponse response = HttpUtils.sendPost(url, driveLogDTO);
    String appVehicleDTOJson = response.getContent();
    ApiResponse apiResponse = JsonUtil.jsonToObj(appVehicleDTOJson, ApiResponse.class);
    if (apiResponse == null || !MessageCode.SUCCESS.toString().equals(apiResponse.getStatus())) {
      LOG.error("保存行车日子异常。");
    }
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
        IGeocodingService geocodingService = ServiceManager.getService(IGeocodingService.class);
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
    IJuheService iJuheService = ServiceManager.getService(IJuheService.class);
    String cityCode = "";
    JuheViolateRegulationCitySearchCondition juheViolateRegulationCitySearchCondition = null;
    juheViolateRegulationCitySearchCondition
      = iJuheService.getJuheViolateRegulationCitySearchConditionByCityName(cityName);
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

