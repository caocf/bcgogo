package com.bcgogo.etl.service;

import com.bcgogo.api.GsmVehicleDataCondition;
import com.bcgogo.api.GsmVehicleDataDTO;
import com.bcgogo.etl.model.IllegalCity;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-31
 * Time: 17:50
 */
public interface IGSMVehicleDataService {

  String getPGsmVehicleDataUUID(String imei);

  void saveOrUpdateGsmVehicleDataDTO(GsmVehicleDataDTO... gsmVehicleDataDTOs);

  List<GsmVehicleDataDTO> getGsmVehicleDataDTO(GsmVehicleDataCondition condition);

  GsmVehicleDataDTO getFireUpGsmVehicleDataDTO(String uuid, String vehicleStatus);

  List<GsmVehicleDataDTO> getGsmVehicleDataDTOByAppUserNo(String appUserNo);

  List<GsmVehicleDataDTO> getGsmVehicleDataDTOByUUID(String uuid);

  String getPFireUpUUIDByImei(String imei);

  GsmVehicleDataDTO getLastGsmVehicleData(String appUserNo);

//  DriveLogDTO getDriveLogDTOById(Long id);
//
//  GsmVehicleDataDTO getLastGsmVehicleData(String appUserNo);

  void generationDriveLogEveryTime(GsmVehicleDataDTO gsmVehicleDataDTO) throws IOException, IllegalAccessException;

  void gpsToCityMask();

  IllegalCity getIllegalCityByAppUserNo(String appUserNo);

  GsmVehicleDataDTO getGsmVehicleDataByUUidAndUpdateTime(String uuid, long upLoadTime);

}
