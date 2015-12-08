package com.bcgogo.etl.service;

import com.bcgogo.enums.app.GsmPointType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.etl.GsmPointDTO;
import com.bcgogo.etl.model.EtlDaoManager;
import com.bcgogo.etl.model.EtlWriter;
import com.bcgogo.etl.model.GsmPoint;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: ZhangJuntao
 * Date: 14-3-10
 * Time: 上午11:53
 */
@Component
public class GsmPointService implements IGsmPointService {
  private static final Logger LOG = LoggerFactory.getLogger(GsmPointService.class);
  @Autowired
  private EtlDaoManager etlDaoManager;

  @Override
  public Map<String, List<GsmPoint>> getUnHandledGsmPointToHandling(int limit) {
    Map<String, List<GsmPoint>> result = new HashMap<String, List<GsmPoint>>();
    EtlWriter writer = etlDaoManager.getWriter();
    List<GsmPoint> pointList = writer.getUnHandledGsmPoint(limit);
    List<GsmPoint> tmpList;
    Object status = writer.begin();
    try {
      for (GsmPoint gp : pointList) {
        gp.setGsmVehicleStatus(GsmVehicleStatus.HANDLED);
        tmpList = result.get(gp.getEmi());
        if (CollectionUtil.isEmpty(tmpList)) {
          tmpList = new ArrayList<GsmPoint>();
          result.put(gp.getEmi(), tmpList);
        }
        tmpList.add(gp);
        writer.update(gp);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return result;
  }

  @Override
  public Map<String, List<GsmPoint>> getGsmPointByUploadTime(Long startTime, Long endTime) {
    Map<String, List<GsmPoint>> gsmPointMap = new HashMap<String, List<GsmPoint>>();
    if(startTime != null && endTime != null){
      EtlWriter writer = etlDaoManager.getWriter();
      List<GsmPoint> gsmPoints = writer.getGsmPointsByUploadTime(startTime,endTime);
      if(CollectionUtils.isNotEmpty(gsmPoints)){
        for(GsmPoint gsmPoint : gsmPoints){
          if(gsmPoint != null && StringUtils.isNotBlank(gsmPoint.getEmi())){
            List<GsmPoint> tempGsmPoints = gsmPointMap.get(gsmPoint.getEmi());
            if(tempGsmPoints == null){
              tempGsmPoints = new ArrayList<GsmPoint>();
            }
            tempGsmPoints.add(gsmPoint);
            gsmPointMap.put(gsmPoint.getEmi(),tempGsmPoints);
          }
        }
      }
    }
    return gsmPointMap;
  }

  @Override
  public List<GsmPoint> getGsmPointByIMeiAndUploadTime(String imei, Long startTime, Long endTime) {
    if (StringUtils.isEmpty(imei) || startTime == null || endTime == null) {
      return new ArrayList<GsmPoint>();
    }
    EtlWriter writer = etlDaoManager.getWriter();
    return writer.getGsmPointByIMeiAndUploadTime(imei,startTime, endTime);
  }

  @Override
  public GsmPoint getLastGsmPointByImei(String imei) {
    if(StringUtils.isEmpty(imei)){
      return null;
    }
    EtlWriter writer = etlDaoManager.getWriter();
    return writer.getLastGsmPointByImei(imei);
  }

  @Override
  public List<GsmPoint> getGsmPointsByImeiGsmPointTypeUploadTime(String imei, GsmPointType gsmPointType, Long uploadTimeStart,
                                                                 Long uploadTimeEnd, GsmVehicleStatus status) {
    if(StringUtils.isNotBlank(imei) && gsmPointType != null && uploadTimeStart != null && uploadTimeEnd != null && status != null){
      EtlWriter writer = etlDaoManager.getWriter();
      return writer.getGsmPointsByImeiGsmPointTypeUploadTime(imei, gsmPointType, uploadTimeStart, uploadTimeEnd, status);
    }
    return null;
  }

  @Override
  public void updateGsmPointStatus(List<GsmPoint> gsmPoints, GsmVehicleStatus gsmVehicleStatus) {
    if(CollectionUtils.isNotEmpty(gsmPoints) && gsmVehicleStatus != null){
      EtlWriter writer = etlDaoManager.getWriter();
      Object status = writer.begin();
      try{
        for(GsmPoint gsmPoint : gsmPoints){
          gsmPoint.setGsmVehicleStatus(gsmVehicleStatus);
          writer.update(gsmPoint);
        }
        writer.commit(status);
      }finally {
        writer.rollback(status);
      }
    }
  }

  @Override
  public List<GsmPointDTO> getLastGsmPointByImei(Set<String> imei) {
    List<GsmPointDTO> gsmPointDTOs = new ArrayList<GsmPointDTO>();
    if (CollectionUtil.isEmpty(imei)) {
      return gsmPointDTOs;
    }

    EtlWriter writer = etlDaoManager.getWriter();
    List<GsmPoint> gsmPoints = writer.getLastGsmPointByImei(imei);
    if (CollectionUtil.isEmpty(gsmPoints)) {
      return gsmPointDTOs;
    }

    for (GsmPoint gsmPoint : gsmPoints) {
      gsmPointDTOs.add(gsmPoint.toDTO());

    }
    return gsmPointDTOs;
  }

}
