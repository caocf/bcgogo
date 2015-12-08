package com.bcgogo.etl.service;

import com.bcgogo.enums.app.GsmPointType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.etl.GsmPointDTO;
import com.bcgogo.etl.model.GsmPoint;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 14-3-10
 * Time: 上午11:53
 */
public interface IGsmPointService {

  Map<String,List<GsmPoint>> getUnHandledGsmPointToHandling(int limit);


  Map<String,List<GsmPoint>> getGsmPointByUploadTime(Long startTime, Long endTime);

  GsmPoint getLastGsmPointByImei(String imei);

  List<GsmPoint> getGsmPointByIMeiAndUploadTime(String imei, Long startTime, Long endTime);

  List<GsmPoint> getGsmPointsByImeiGsmPointTypeUploadTime(String imei, GsmPointType pz, Long uploadTimeStart, Long uploadTimeEnd, GsmVehicleStatus status);

  void updateGsmPointStatus(List<GsmPoint> pzGSMPoints, GsmVehicleStatus gsmVehicleStatus);

  List<GsmPointDTO> getLastGsmPointByImei(Set<String> imeiSet);


}
