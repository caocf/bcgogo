package com.bcgogo.txn.model.sql;

import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 14-2-13
 * Time: 下午4:47
 */
public class MessageSQL {
  public static Query countShopFaultInfoList(Session session, FaultInfoSearchConditionDTO searchCondition) {
    StringBuilder hql = new StringBuilder("select count(f) ");
    return splittingShopFaultInfoListHQL(hql, session, searchCondition, false);
  }

  private static Query splittingShopFaultInfoListHQL(StringBuilder hql, Session session, FaultInfoSearchConditionDTO searchCondition, boolean isPaging) {
    hql.append("from FaultInfoToShop f where f.shopId =:shopId ");
    if(searchCondition.getFaultAlertType()!=null){
      hql.append(" and f.faultAlertType =:faultAlertType");
    }
    //过滤掉没有描述的
    hql.append(" and f.faultCodeDescription is not null ");
    if (null != searchCondition.getTimeStart()){
      hql.append(" and f.faultCodeReportTime >=:timeStart");
    }

    if (null != searchCondition.getTimeEnd()){
      hql.append(" and f.faultCodeReportTime <=:timeEnd");
    }
    if (searchCondition.hasState()) {
      hql.append(" and (");
      boolean start = true;
      if (searchCondition.getIsUntreated() != null) {
        hql.append(" f.isCreateAppointOrder = ").append("'").append(YesNo.NO.toString()).append("'")
            .append(" and f.isSendMessage = ").append("'").append(YesNo.NO.toString()).append("'")
            .append(" and f.status = 'ACTIVE' ");
        start = false;
      }
      if (null != searchCondition.getIsCreateAppointOrder()) {
        if (!start) {
          hql.append(" or ");
        }
        hql.append(" f.isCreateAppointOrder = ").append("'").append(searchCondition.getIsCreateAppointOrder().toString()).append("'");
        start = false;
      }
      if (null != searchCondition.getIsSendMessage()) {
        if (!start) {
          hql.append(" or ");
        }
        hql.append(" f.isSendMessage = ").append("'").append(searchCondition.getIsSendMessage().toString()).append("'");
      }
      if(searchCondition.getIsDeleted() != null){
        if (!start) {
          hql.append(" or ");
        }
        String statusStr = "ACTIVE";
        if(YesNo.YES.equals(searchCondition.getIsDeleted())){
          statusStr = "DELETED";
        }
        hql.append(" f. status = ").append("'").append(statusStr).append("'");
      }
      hql.append(" ) ");
    }
    if (StringUtil.isNotEmpty(searchCondition.getMobile())) {
      hql.append(" and ( f.mobile like :mobile ");
      if(CollectionUtil.isNotEmpty(searchCondition.getMobiles())) {
        hql.append(" or f.mobile in (:mobiles) ");
      }
      hql.append(" )");
    }

    if (StringUtil.isNotEmpty(searchCondition.getVehicleNo()))
      hql.append(" and f.vehicleNo like :vehicleNo");
    if (CollectionUtil.isNotEmpty(searchCondition.getVehicleNoList()))
      hql.append(" and f.vehicleNo in (:vehicleNoList)");
    if (CollectionUtil.isNotEmpty(searchCondition.getIds()))
      hql.append(" and f.id in (:ids)");
    hql.append(" order by f.faultCodeReportTime desc");
    Query query = session.createQuery(hql.toString()).setLong("shopId", searchCondition.getShopId());
    if (null != searchCondition.getTimeStart())
      query.setLong("timeStart", searchCondition.getTimeStart());
    if (null != searchCondition.getTimeEnd())
      query.setParameter("timeEnd", searchCondition.getTimeEnd());
    if (StringUtil.isNotEmpty(searchCondition.getMobile())) {
      query.setParameter("mobile", "%" + searchCondition.getMobile() + "%");
      if (CollectionUtil.isNotEmpty(searchCondition.getMobiles())) {
        query.setParameterList("mobiles", searchCondition.getMobiles());
      }
    }
    if(searchCondition.getFaultAlertType()!=null){
      query.setParameter("faultAlertType", searchCondition.getFaultAlertType());
    }
    if (StringUtil.isNotEmpty(searchCondition.getVehicleNo()))
      query.setParameter("vehicleNo", "%" + searchCondition.getVehicleNo() + "%");
    if (CollectionUtil.isNotEmpty(searchCondition.getVehicleNoList()))
      query.setParameterList("vehicleNoList", searchCondition.getVehicleNoList());
    if (CollectionUtil.isNotEmpty(searchCondition.getIds()))
      query.setParameterList("ids", searchCondition.getIds());
    if (isPaging) {
      query.setMaxResults(searchCondition.getMaxRows());
      query.setFirstResult((searchCondition.getStartPageNo() - 1) * searchCondition.getMaxRows());
    }
    return query;
  }


  public static Query searchShopFaultInfoList(Session session, FaultInfoSearchConditionDTO searchCondition) {
    StringBuilder hql = new StringBuilder("select f ");
    return splittingShopFaultInfoListHQL(hql, session, searchCondition, true);
  }

  public static Query getShopFaultInfoVehicleNoSuggestion(Session session, Long shopId, String keyword) {
    return session.createQuery("select distinct(f.vehicleNo) from FaultInfoToShop f where f.shopId =:shopId and f.status=:status and f.vehicleNo like :keyword")
        .setLong("shopId", shopId).setParameter("status", Status.ACTIVE).setParameter("keyword", "%" + keyword + "%").setMaxResults(15).setFirstResult(0);
  }

  public static Query getShopFaultInfoMobileSuggestion(Session session, Long shopId, String keyword) {
    return session.createQuery("select distinct(f.mobile) from FaultInfoToShop f where f.shopId =:shopId and f.status=:status and f.mobile like :keyword")
        .setLong("shopId", shopId).setParameter("status", Status.ACTIVE).setParameter("keyword", "%" + keyword + "%").setMaxResults(15).setFirstResult(0);
  }

  public static Query getFaultInfoToShopByIds(Session session, Long shopId, Long... ids) {
    StringBuilder sb = new StringBuilder();
    sb.append("from FaultInfoToShop f where f.shopId =:shopId and f.id in(:ids)");
    Query query = session.createQuery(sb.toString());
    query.setParameter("shopId", shopId).setParameterList("ids", ids);
    return query;
  }

  public static Query getUnHandledFaultInfoToShopByVehicleNo(Session session, Long shopId, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from FaultInfoToShop f where f.shopId =:shopId and f.vehicleNo =:vehicleNo and f.status =:status ");
    sb.append("and f.isSendMessage =:NO and f.isCreateAppointOrder =:NO and f.faultAlertType = :type");
    Query query = session.createQuery(sb.toString());
    query.setParameter("shopId", shopId)
        .setParameter("status", Status.ACTIVE)
        .setParameter("vehicleNo", vehicleNo)
        .setParameter("NO", YesNo.NO)
        .setParameter("type", FaultAlertType.FAULT_CODE);
    return query;
  }

  public static Query getUnHandledFaultInfoToShopsByVehicleNoFaultCode(Session session,String vehicleNo,Long shopId,Set<String> faultCodes) {
    StringBuilder sb = new StringBuilder();
    sb.append("from FaultInfoToShop f where f.shopId =:shopId and f.vehicleNo =:vehicleNo and f.status =:status ");
    sb.append("and f.isSendMessage =:NO and f.isCreateAppointOrder =:NO and f.faultAlertType = :type and f.faultCode in(:faultCodes) ");
    Query query = session.createQuery(sb.toString());
    query.setParameter("shopId", shopId)
        .setParameter("status", Status.ACTIVE)
        .setParameter("vehicleNo", vehicleNo)
        .setParameter("NO", YesNo.NO)
        .setParameterList("faultCodes",faultCodes)
        .setParameter("type", FaultAlertType.FAULT_CODE);
    return query;
  }

  public static Query countShopFaultInfoByVehicleNo(Session session, Long shopId, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(f.id) from FaultInfoToShop f where f.shopId =:shopId and f.vehicleNo =:vehicleNo  ");
    Query query = session.createQuery(sb.toString());
    query.setParameter("shopId", shopId)
        .setParameter("vehicleNo", vehicleNo);
    return query;
  }

  public static Query searchShopFaultInfoList_(Session session, FaultInfoSearchConditionDTO searchCondition) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT av.id,av.shop_id,av.app_vehicle_fault_info_id,av.fault_code_info_id,av.fault_code," +
        "av.fault_code_category,av.fault_code_description,av.app_user_no,av.app_vehicle_id," +
        "av.vehicle_no,av.vehicle_brand,av.vehicle_model,av.mobile,av.fault_code_report_time,av.is_send_message," +
        "av.is_create_appoint_order,av.status,av.appoint_order_id,av.fault_alert_type,av.lon,av.lat  ");
    sb.append("FROM fault_info_to_shop av  ");
    sb.append("WHERE av.shop_id = :shopId  and av.status = 'ACTIVE' and av.fault_code_description is not null "); //and r.handle_status = :handleStatus
    sb.append("ORDER BY av.fault_code_report_time desc    ");
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", searchCondition.getShopId());//.setParameter("handleStatus",handleStatus)
    if(searchCondition!=null&&searchCondition.getStartPageNo()!=0 && searchCondition.getMaxRows()!=0){
      query.setFirstResult((searchCondition.getStartPageNo()-1) * searchCondition.getMaxRows()).setMaxResults(searchCondition.getMaxRows());
    }
    return query;
  }

  public static Query countShopFaultInfoList_(Session session, FaultInfoSearchConditionDTO searchCondition) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(*) ");
    sb.append("FROM fault_info_to_shop av  ");
    sb.append("WHERE av.shop_id = :shopId  and av.status = 'ACTIVE' and av.fault_code_description is not null  "); //and r.handle_status = :handleStatus
    Query query = session.createSQLQuery(sb.toString()).setParameter("shopId", searchCondition.getShopId());   //.setParameter("handleStatus",handleStatus)
    return query;
  }



}
