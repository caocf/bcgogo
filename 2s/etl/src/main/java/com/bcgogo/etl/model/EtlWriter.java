package com.bcgogo.etl.model;

import com.bcgogo.common.Pair;
import com.bcgogo.enums.app.GsmPointType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午7:05
 */
public class EtlWriter extends GenericWriterDao {
  public EtlWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public List<GsmPoint> getUnHandledGsmPoint(int limit) {
    Session session = this.getSession();
    try {
      Query query = SQL.getUnHandledGsmPoint(session, limit);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<GsmVehicleInfo> getGsmVehicleInfoByUploadTime(Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmVehicleInfoByUploadTime(session, startTime,endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<GsmVehicleInfo> getGsmVehicleInfoByImeiAndUploadTime(String imei, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmVehicleInfoByImeiAndUploadTime(session, imei, startTime, endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<GsmPoint> getGsmPointsByUploadTime(Long startTime, Long endTime) {
    if(startTime == null || endTime == null){
      return new ArrayList<GsmPoint>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmPointsByUploadTime(session, startTime,endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<GsmVehicleInfo> getUnHandledGsmVehicle(int limit,Set<String> imeis) {
    Session session = this.getSession();
    try {
      Query query = SQL.getUnHandledGsmVehicleInfo(session, limit,imeis);
      return query.list();
    } finally {
      release(session);
    }
  }

  public GsmPoint getLastGsmPointByImei(String imei) {
    Session session = this.getSession();
    try {
      Query query = SQL.getLastGsmPointByImei(session, imei);
      List<GsmPoint> gsmPoints = query.list();
      return CollectionUtil.getFirst(gsmPoints);
    } finally {
      release(session);
    }
  }

  public List<GsmPoint> getGsmPointByIMeiAndUploadTime(String imei, Long startTime, Long endTime) {
    if(StringUtils.isEmpty(imei) || startTime == null || endTime == null){
      return new ArrayList<GsmPoint>();
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmPointByIMeiAndUploadTime(session,imei, startTime,endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<GsmPoint> getGsmPointsByImeiGsmPointTypeUploadTime(String imei, GsmPointType gsmPointType, Long uploadTimeStart,
                                                                 Long uploadTimeEnd, GsmVehicleStatus status) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmPointsByImeiGsmPointTypeUploadTime(session, imei, gsmPointType, uploadTimeStart, uploadTimeEnd, status);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<GsmPoint> getLastGsmPointByImei(Set<String> imei) {
    Session session = this.getSession();
    try {
      Query query = SQL.getLastGsmPointByImei(session, imei);
      List<GsmPoint> gsmPoints = query.list();
      return gsmPoints;
    } finally {
      release(session);
    }
  }

  public List<Pair<String,Long>> getGsmPointTraceGroup(int groupLimit, int searchLimit,long beforeTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmPointTraceGroup(session, groupLimit,searchLimit,beforeTime);
      List<Object[] > objectList = query.list();
      List<Pair<String,Long>> result = new ArrayList<Pair<String, Long>>();
      for(Object[] objects : objectList){
        Pair<String,Long> pair = new Pair<String, Long>(StringUtil.valueOf(objects[0]),NumberUtil.longValue(objects[1]));
        result.add(pair);
      }
      return result;
    } finally {
      release(session);
    }
  }

  public List<Pair<String,Long>> getGsmVehicleInfoTraceGroup(int groupLimit, int searchLimit,long beforeTime) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmVehicleInfoTraceGroup(session, groupLimit, searchLimit, beforeTime);
      List<Object[] > objectList = query.list();
      List<Pair<String,Long>> result = new ArrayList<Pair<String, Long>>();
      for(Object[] objects : objectList){
        Pair<String,Long> pair = new Pair<String, Long>(StringUtil.valueOf(objects[0]),NumberUtil.longValue(objects[1]));
        result.add(pair);
      }
      return result;
    } finally {
      release(session);
    }
  }

  public List<GsmPoint> getGsmPointsByImeiAndBeforeTimeAndLimit(String iMei, Long beforeTime, int tracePageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmPointsByImeiAndBeforeTimeAndLimit(session, iMei, beforeTime,tracePageSize);
      List<GsmPoint> gsmPoints = query.list();
      return gsmPoints;
    } finally {
      release(session);
    }
  }

  public List<GsmVehicleInfo> getGsmVehicleInfosByImeiAndBeforeTimeAndLimit(String iMei, Long beforeTime, int tracePageSize) {
    Session session = this.getSession();
    try {
      Query query = SQL.getGsmVehicleInfosByImeiAndBeforeTimeAndLimit(session, iMei, beforeTime,tracePageSize);
      List<GsmVehicleInfo> gsmVehicleInfos = query.list();
      return gsmVehicleInfos;
    } finally {
      release(session);
    }
  }

  public GsmPoint lastTraceGsmPoint(String imei, Long beforeTime, int traceLimit) {
    Session session = this.getSession();
    try {
      Query query = SQL.lastTraceGsmPoint(session, imei, beforeTime, traceLimit);
      List<GsmPoint> gsmPoints = query.list();
      return CollectionUtil.getFirst(gsmPoints);
    } finally {
      release(session);
    }
  }

  public GsmVehicleInfo lastTraceGsmVehicleInfo(String imei, Long beforeTime, int traceLimit) {
    Session session = this.getSession();
    try {
      Query query = SQL.lastTraceGsmVehicleInfo(session, imei, beforeTime, traceLimit);
      List<GsmVehicleInfo> gsmVehicleInfos = query.list();
      return CollectionUtil.getFirst(gsmVehicleInfos);
    } finally {
      release(session);
    }
  }
}
