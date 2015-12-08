package com.bcgogo.etl.model;

import com.bcgogo.enums.app.GsmPointType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Set;

/**
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午7:08
 */
public class SQL {

  public static Query getUnHandledGsmPoint(Session session, int limit) {
    return session.createQuery("from GsmPoint where gsmVehicleStatus=:gsmVehicleStatus and gsmPointType=:gsmPointType order by uploadTime asc,uploadServerTime asc")
        .setParameter("gsmVehicleStatus", GsmVehicleStatus.UN_HANDLE)
        .setParameter("gsmPointType", GsmPointType.AUT)
        .setMaxResults(limit);
  }

  public static Query getGsmVehicleInfoByUploadTime(Session session, Long startTime, Long endTime) {
    return session.createQuery("from GsmVehicleInfo where uploadTime >=:startTime and uploadTime<= :endTime order by uploadTime asc")
        .setParameter("startTime", startTime)
        .setParameter("endTime", endTime);
  }

  public static Query getGsmVehicleInfoByImeiAndUploadTime(Session session, String imei,Long startTime, Long endTime) {
    return session.createQuery("from GsmVehicleInfo where emi =:imei and uploadTime >=:startTime and uploadTime<= :endTime ")
        .setParameter("imei", imei)
        .setParameter("startTime", startTime)
        .setParameter("endTime", endTime);
  }

  public static Query getUnHandledGsmVehicleInfo(Session session, int limit, Set<String> imeis) {
    StringBuilder sb = new StringBuilder();
    sb.append("from GsmVehicleInfo where gsmVehicleStatus=:gsmVehicleStatus ");
    if (CollectionUtils.isNotEmpty(imeis)) {
      sb.append(" and emi in(:imeis) ");
    }
   // sb.append("order by uploadServerTime asc ");todo 本地测试用
   sb.append("order by uploadTime asc,uploadServerTime asc ");
    Query query = session.createQuery(sb.toString())
        .setParameter("gsmVehicleStatus", GsmVehicleStatus.UN_HANDLE)
        .setMaxResults(limit);
    if (CollectionUtils.isNotEmpty(imeis)) {
      query.setParameter("imeis", imeis);
    }
    return query;
  }

  public static Query getGsmPointsByUploadTime(Session session, Long startTime, Long endTime) {
    return session.createQuery("from GsmPoint where uploadTime >=:startTime and uploadTime<= :endTime and gsmPointType =:gsmPointType order by uploadTime asc")
        .setParameter("startTime", startTime)
        .setParameter("gsmPointType", GsmPointType.AUT)
        .setParameter("endTime", endTime);
  }

  public static Query getLastGsmPointByImei(Session session, String imei) {
    StringBuilder sb = new StringBuilder();
    sb.append("from GsmPoint where emi =:imei and  gsmPointType =:gsmPointType ");
    sb.append(" and CAST(lat AS int)>0 and CAST(lon AS int)>0 and uploadServerTime-uploadTime > :from ");
    sb.append("order by uploadTime desc");

    return session.createQuery(sb.toString())
        .setParameter("from",-60000L)
        .setParameter("imei", imei)
        .setParameter("gsmPointType", GsmPointType.AUT)
        .setMaxResults(1);
  }

  public static Query getGsmPointByIMeiAndUploadTime(Session session,String imei, Long startTime, Long endTime) {
    return session.createQuery("from GsmPoint where emi =:imei and uploadTime >=:startTime and uploadTime<= :endTime and gsmPointType =:gsmPointType order by uploadTime asc")
        .setParameter("imei", imei)
        .setParameter("startTime", startTime)
        .setParameter("gsmPointType", GsmPointType.AUT)
        .setParameter("endTime", endTime);
  }

  public static Query getGsmPointsByImeiGsmPointTypeUploadTime(Session session, String imei, GsmPointType gsmPointType,
                                                               Long uploadTimeStart, Long uploadTimeEnd, GsmVehicleStatus status) {
    return session.createQuery("from GsmPoint where emi =:imei and uploadTime >=:uploadTimeStart and uploadTime <=:uploadTimeEnd and gsmPointType =:gsmPointType and gsmVehicleStatus =:status")
        .setParameter("imei", imei)
        .setParameter("uploadTimeStart", uploadTimeStart)
        .setParameter("uploadTimeEnd", uploadTimeEnd)
        .setParameter("gsmPointType", gsmPointType)
        .setParameter("status", status);
  }

  public static Query getGsmVehicleInfoByEmi(Session session, String emi, int limit) {
    return session.createQuery("from GsmVehicleInfo where emi =:emi order by uploadServerTime desc ")
        .setString("emi", emi)
        .setMaxResults(limit);
  }

  public static Query getLastGsmPointByImei(Session session, Set<String> imei) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a.* from ( select * from gsm_point where emi in(:imei) and  gsm_point_type =:gsmPointType ");
    sb.append(" and CAST(lat AS decimal)>0 and CAST(lon AS decimal)>0 ");
    sb.append("order by upload_time desc )a group by emi ");

    return session.createSQLQuery(sb.toString()).addEntity(GsmPoint.class)
        .setParameterList("imei", imei)
        .setParameter("gsmPointType", GsmPointType.AUT.toString());
  }

  public static Query getGsmPointTraceGroup(Session session, int groupLimit, int searchLimit,long beforeTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT g.emi,COUNT(g.id) from gsm_point g WHERE g.upload_server_time < :beforeTime GROUP BY g.emi HAVING count(g.id)>:searchLimit ");

    return session.createSQLQuery(sb.toString())
        .setParameter("beforeTime", beforeTime)
        .setParameter("searchLimit", searchLimit)
        .setMaxResults(groupLimit);
  }

  public static Query getGsmVehicleInfoTraceGroup(Session session, int groupLimit, int searchLimit,long beforeTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT g.emi,COUNT(g.id) from gsm_vehicle_info g WHERE g.upload_server_time < :beforeTime GROUP BY g.emi HAVING count(g.id)>:searchLimit ");

    return session.createSQLQuery(sb.toString())
        .setParameter("beforeTime", beforeTime)
        .setParameter("searchLimit", searchLimit)
        .setMaxResults(groupLimit);
  }

  public static Query getGsmPointsByImeiAndBeforeTimeAndLimit(Session session, String iMei, Long beforeTime, int tracePageSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT * FROM gsm_point g where g.emi = :iMei and g.upload_server_time < :beforeTime ORDER BY g.upload_server_time ASC  LIMIT :tracePageSize ");

    return session.createSQLQuery(sb.toString()).addEntity(GsmPoint.class)
        .setParameter("iMei", iMei)
        .setParameter("beforeTime", beforeTime)
        .setParameter("tracePageSize", tracePageSize);
  }

  public static Query getGsmVehicleInfosByImeiAndBeforeTimeAndLimit(Session session, String iMei, Long beforeTime, int tracePageSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT * FROM gsm_vehicle_info g where g.emi = :iMei and g.upload_server_time < :beforeTime ORDER BY g.upload_server_time ASC  LIMIT :tracePageSize ");

    return session.createSQLQuery(sb.toString()).addEntity(GsmVehicleInfo.class)
        .setParameter("iMei", iMei)
        .setParameter("beforeTime", beforeTime)
        .setParameter("tracePageSize", tracePageSize);
  }

  public static Query lastTraceGsmPoint(Session session, String imei, Long beforeTime, int traceLimit) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT g.* from gsm_point g where g.emi = :imei AND  g.upload_server_time <= :beforeTime  ORDER BY g.upload_server_time desc LIMIT :traceLimit,1");

    return session.createSQLQuery(sb.toString()).addEntity(GsmPoint.class)
        .setParameter("imei", imei)
        .setParameter("beforeTime", beforeTime)
        .setParameter("traceLimit", traceLimit);
  }

  public static Query lastTraceGsmVehicleInfo(Session session, String imei, Long beforeTime, int traceLimit) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT g.* from gsm_vehicle_info g where g.emi = :imei AND  g.upload_server_time <= :beforeTime  ORDER BY g.upload_server_time desc LIMIT :traceLimit,1");

    return session.createSQLQuery(sb.toString()).addEntity(GsmVehicleInfo.class)
        .setParameter("imei", imei)
        .setParameter("beforeTime", beforeTime)
        .setParameter("traceLimit", traceLimit);
  }
}
