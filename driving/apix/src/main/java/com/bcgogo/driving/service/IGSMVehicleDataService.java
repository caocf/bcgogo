package com.bcgogo.driving.service;

import com.bcgogo.driving.model.IllegalCity;
import com.bcgogo.driving.socket.protocol.PGsmVehicleData;
import com.bcgogo.pojox.api.GsmTBoxDataDTO;
import com.bcgogo.pojox.api.GsmVehicleDataCondition;
import com.bcgogo.pojox.api.GsmVehicleDataDTO;
import com.bcgogo.driving.socket.protocol.PGsmVehicleDataStart;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-31
 * Time: 17:50
 */
public interface IGSMVehicleDataService {
  //
//
  PGsmVehicleDataStart generatePGsmVehicleDataStart(PGsmVehicleData vehicleData);

  String getCachePGDataUUID(String imei);

  void saveOrUpdateGsmVehicleDataDTO(GsmVehicleDataDTO... gsmVehicleDataDTOs);

  void saveOrUpdateGsmTBoxDataDTO(GsmTBoxDataDTO... GsmTBoxDataDTOs);

  List<GsmVehicleDataDTO> getGsmVehicleDataDTO(GsmVehicleDataCondition condition);

  GsmVehicleDataDTO getFireUpGsmVehicleDataDTO(String uuid, String vehicleStatus);

  GsmTBoxDataDTO getFireUpGsmTBoxDataDTO(String uuid, String vehicleStatus);

  List<GsmVehicleDataDTO> getGsmVehicleDataDTOByAppUserNo(String appUserNo);

  List<GsmVehicleDataDTO> getGsmVehicleDataDTOByUUID(String uuid);

  String getPFireUpUUIDByImei(String imei);

  GsmVehicleDataDTO getLastGsmVehicleData(String appUserNo);

//  DriveLogDTO getDriveLogDTOById(Long id);
//
//  GsmVehicleDataDTO getLastGsmVehicleData(String appUserNo);


  void gpsToCityMask();

  IllegalCity getIllegalCityByAppUserNo(String appUserNo);

  GsmVehicleDataDTO getGsmVehicleDataByUUidAndUpdateTime(String uuid, long upLoadTime);

}
