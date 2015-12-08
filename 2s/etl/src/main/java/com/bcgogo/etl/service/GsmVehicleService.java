package com.bcgogo.etl.service;

import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.etl.GsmVehicleInfoDTO;
import com.bcgogo.etl.model.*;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-3-20
 * Time: 上午11:40
 */
@Component
public class GsmVehicleService implements IGsmVehicleService {

  private static final Logger LOG = LoggerFactory.getLogger(GsmVehicleService.class);
  @Autowired
  private EtlDaoManager etlDaoManager;

  //根据最小时间，最大时间取出车况数据
  @Override
  public Map<String, List<GsmVehicleInfo>> getGsmVehicleInfoMapByTime(Long startTime, Long endTime) {
    Map<String, List<GsmVehicleInfo>> result = new HashMap<String, List<GsmVehicleInfo>>();
    EtlWriter writer = etlDaoManager.getWriter();
    List<GsmVehicleInfo> gsmVehicleInfoList = writer.getGsmVehicleInfoByUploadTime(startTime, endTime);
    if (CollectionUtils.isNotEmpty(gsmVehicleInfoList)) {
      for (GsmVehicleInfo gsmVehicleInfo : gsmVehicleInfoList) {
        if (gsmVehicleInfo != null && StringUtils.isNotEmpty(gsmVehicleInfo.getEmi())) {
          List<GsmVehicleInfo> tmpList = result.get(gsmVehicleInfo.getEmi());
          if (tmpList == null) {
            tmpList = new ArrayList<GsmVehicleInfo>();
          }
          tmpList.add(gsmVehicleInfo);
          result.put(gsmVehicleInfo.getEmi(), tmpList);
        }
      }
    }
    return result;
  }

  @Override
  public Map<String, List<GsmVehicleInfo>> getUnHandledGsmVehicleInfoToHandling(int limit,final Set<String>imeis) {
    Map<String, List<GsmVehicleInfo>> result = new HashMap<String, List<GsmVehicleInfo>>();
    EtlWriter writer = etlDaoManager.getWriter();
    List<GsmVehicleInfo> unHandledGsmVehicle = writer.getUnHandledGsmVehicle(limit,imeis);
    List<GsmVehicleInfo> tmpList;
    Object status = writer.begin();
    try {
      for (GsmVehicleInfo gsmVehicleInfo : unHandledGsmVehicle) {
        gsmVehicleInfo.setGsmVehicleStatus(GsmVehicleStatus.HANDLED);
        tmpList = result.get(gsmVehicleInfo.getEmi());
        if (CollectionUtil.isEmpty(tmpList)) {
          tmpList = new ArrayList<GsmVehicleInfo>();
          result.put(gsmVehicleInfo.getEmi(), tmpList);
        }
        tmpList.add(gsmVehicleInfo);
        writer.update(gsmVehicleInfo);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public List<GsmVehicleInfo> getGsmVehicleInfoByImeiAndUploadTime(String imei, Long uploadTimeStart, Long uploadTimeEnd) {
    if(imei != null && uploadTimeStart != null && uploadTimeEnd != null){
      EtlWriter writer = etlDaoManager.getWriter();
      return writer.getGsmVehicleInfoByImeiAndUploadTime(imei, uploadTimeStart, uploadTimeEnd);
    }
  return new ArrayList<GsmVehicleInfo>();
  }

  public GsmVehicleInfoDTO getGsmVehicleInfoByEmi(String emi, int limit) {
    EtlReader etlReader = etlDaoManager.getReader();
    GsmVehicleInfo gsmVehicleInfo = etlReader.getGsmVehicleInfoByEmi(emi, limit);
    return gsmVehicleInfo == null ? null : gsmVehicleInfo.toDTO();
  }
}
