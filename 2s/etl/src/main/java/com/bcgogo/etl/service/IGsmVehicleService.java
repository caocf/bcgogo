package com.bcgogo.etl.service;

import com.bcgogo.etl.GsmVehicleInfoDTO;
import com.bcgogo.etl.model.GsmVehicleInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-20
 * Time: 上午11:39
 */
public interface IGsmVehicleService {

  //根据最小时间，最大时间取出车况数据
  Map<String, List<GsmVehicleInfo>> getGsmVehicleInfoMapByTime(Long startTime, Long endTime);

  Map<String,List<GsmVehicleInfo>> getUnHandledGsmVehicleInfoToHandling(int limit,final Set<String> imeis);

  List<GsmVehicleInfo> getGsmVehicleInfoByImeiAndUploadTime(String imei, Long uploadTimeStart, Long uploadTimeEnd);

  public GsmVehicleInfoDTO getGsmVehicleInfoByEmi(String emi,int limit);

}
