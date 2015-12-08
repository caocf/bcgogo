package com.bcgogo.config.model;

import com.bcgogo.api.ShopNameSuggestion;
import com.bcgogo.camera.CameraDTO;
import com.bcgogo.camera.CameraSearchCondition;
import com.bcgogo.common.Pager;
import com.bcgogo.config.CRMOperationLogCondition;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.*;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.*;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import java.util.List;
import java.util.Map;
import java.util.Set;

class SQL {

  public static Query getImageVersionConfig(Session session, Long shopId, String name) {
    return session.createQuery("from ImageVersionConfig c where c.name=:name and c.shopId = :shopId and c.status=:status")
        .setParameter("name", name)
        .setParameter("status", ObjectStatus.ENABLED)
        .setLong("shopId", shopId);
  }

  public static Query getConfig(Session session, String name, Long shopId) {
    if (shopId != null) {
      return session.createQuery("select c from Config c where c.name=:name and c.shopId = :shopId")
          .setParameter("name", name)
          .setLong("shopId", shopId);
    } else {
      return session.createQuery("select c from Config c where c.name=:name and c.shopId is null")
          .setParameter("name", name);
    }
  }

  /**
   *    根据传入参数name，value查询config表
   * @param session
   * @param name
   * @param value
   * @param shopId
   * @return
   */
    public static Query getConfig(Session session, String name,String value,Long shopId,Pager page) {
        String sql = "";
        if(StringUtil.isEmpty(name) && StringUtil.isEmpty(value)){
            sql = "SELECT * FROM config C ORDER BY C.last_update DESC";
            return session.createSQLQuery(sql).addEntity(Config.class).setFirstResult((page.getCurrentPage()-1) * page.getPageSize()).setMaxResults(page.getPageSize());
        }
        else if(StringUtil.isEmpty(value)){
            sql = "SELECT * FROM config C WHERE C.name = :name ORDER BY C.last_update DESC";
            return session.createSQLQuery(sql).addEntity(Config.class).setFirstResult((page.getCurrentPage()-1) * page.getPageSize()).setMaxResults(page.getPageSize())
              .setString("name", name);
        }
        else if(StringUtil.isEmpty(name)){
            sql = "SELECT * FROM config C WHERE C.value = :value ORDER BY C.last_update DESC";
            return session.createSQLQuery(sql).addEntity(Config.class).setFirstResult((page.getCurrentPage()-1) * page.getPageSize()).setMaxResults(page.getPageSize())
              .setString("value", value);
        }
        sql = "SELECT * FROM config C WHERE C.name = :name AND C.value = :value ORDER BY C.last_update DESC";
        return session.createSQLQuery(sql).addEntity(Config.class).setFirstResult((page.getCurrentPage()-1) * page.getPageSize()).setMaxResults(page.getPageSize())
          .setString("value", value).setString("name", name);
    }

  public static Query getAllImageVersionConfig(Session session) {
    return session.createQuery("from ImageVersionConfig c where c.status=:status").setParameter("status",ObjectStatus.ENABLED);
  }


    public static Query getAllConfig(Session session, int limit) {
        if (limit > 0) {
            return session.createQuery("select c from Config limit :limit").setInteger("limit", limit);
        } else {
            return session.createQuery("select c from Config c ");
        }
    }

    public static  Query countConfigs(Session session,String name,String value,Long shopId){
        String sql = "";
        if(StringUtil.isEmpty(name) && StringUtil.isEmpty(value)){
            sql = "select count(*) as count from Config c";
            return session.createQuery(sql);
        }
        else if(!StringUtil.isEmpty(value)){
            sql = "select count(*) from Config c where c.value = :value";
            return session.createQuery(sql).setString("value", value);
        }
        else if(!StringUtil.isEmpty(name)){
            sql = "select count(*) from Config c where c.name = :name";
            return session.createQuery(sql).setString("name", name);
        }
        sql = "select count(*) from Config c where c.name = :name and c.value = :value";
        return session.createQuery(sql).setString("value", value).setString("name", name);
    }

  public static Query getShop(Session session) {
    return session.createQuery("select s from Shop as s");
  }

  public static Query getShopId(Session session) {
    return session.createQuery("select id from Shop as s");
  }

  public static Query getShop(Session session,ShopStatus shopStatus,Long trialEndTime) {
    return session.createQuery("select s from Shop as s where s.shopStatus=:shopStatus and s.trialEndTime <:trialEndTime")
        .setLong("trialEndTime",trialEndTime).setParameter("shopStatus",ShopStatus.REGISTERED_TRIAL);
  }

  public static Query getSendInvitationCodeActiveShop(Session session) {
    return session.createQuery("select s from Shop as s where s.shopStatus in(:shopStatus) and s.shopState !=:shopState")
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPendingAndPaid());
  }

  public static Query getSendInvitationCodeActiveShopMobile(Session session) {
    return session.createQuery("select sc.mobile from Shop as s,ShopContact sc where sc.shopId=s.id and s.shopStatus in(:shopStatus) and s.shopState !=:shopState and sc.disabled=:disabled")
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPendingAndPaid()).setParameter("disabled", ContactConstant.ENABLED);
  }

  public static Query getShopExcludeTest(Session session) {
    return session.createQuery("select s from Shop as s where shopKind is null");
  }

  public static Query getActiveShop(Session session) {
    return session.createQuery("select s from Shop as s where s.shopStatus in(:shopStatus) and s.shopState !=:shopState")
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPaid());
  }

  public static Query getShopSuggestion(Session session,String name,ShopKind shopKind,int maxRows) {
    StringBuilder sb=new StringBuilder();
    sb.append("select s from Shop as s where s.shopStatus in(:shopStatus) and s.shopState !=:shopState");
    if(StringUtil.isNotEmpty(name)){
      sb.append(" and s.name like :name");
    }
    if(shopKind!=null){
      sb.append(" and s.shopKind = :shopKind");
    }
    Query query=session.createQuery(sb.toString()).setParameter("shopState", ShopState.DELETED)
      .setParameterList("shopStatus", ShopStatus.getShopTrialAndPaid())
      .setFirstResult(0).setMaxResults(maxRows);
    if(StringUtil.isNotEmpty(name)){
      query.setParameter("name","%"+name+"%");
    }
   if(shopKind!=null){
      query.setParameter("shopKind",shopKind);
    }
    return query;
  }

  public static Query getAdShops(Session session) {
    return session.createQuery("select s from Shop as s where (s.productAdType='ALL' or s.productAdType='PART') and s.shopStatus in(:shopStatus) and s.shopState !=:shopState")
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPaid());
  }

  public static Query searchShopByCondition(Session session, ShopSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select s.*,sv.value as shopVersionName,manager.user_no as managerUserNo,manager.id as managerId");
    return splittingQueryForSearchShopByCondition(session, condition, sql, false)
        .setMaxResults(condition.getLimit()).setFirstResult(condition.getStart());
  }

  private static Query splittingQueryForSearchShopByCondition(Session session, ShopSearchCondition condition, StringBuilder sql, boolean isCount) {
    sql.append(" from shop s ");
    sql.append(" LEFT JOIN bcuser.`user` manager on manager.shop_id = s.id and manager.user_type = :userType ");
    sql.append(" LEFT JOIN bcuser.shop_version sv on sv.id = s.shop_version_id  ");
    sql.append(" where s.shop_status in :statuses");
    if (!ArrayUtils.isEmpty(condition.getLocateStatuses())) {
      sql.append(" and s.locate_status in (:locateStatus)");
    }
    if (!ArrayUtils.isEmpty(condition.getShopStates())) {
      sql.append(" and s.shop_state in (:shopStates)");
    } else {
      sql.append(" and s.shop_state != :shopState");
    }
    if (!ArrayUtils.isEmpty(condition.getAreaId())) {
      sql.append(" and s.area_id in :areas ");
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentStatus())) {
      sql.append(" and s.payment_status in :paymentStatus ");
    }
    if (!ArrayUtils.isEmpty(condition.getRegisterType())) {
      sql.append(" and s.register_type in (:registerType) ");
    }
    if (!ArrayUtils.isEmpty(condition.getBargainStatuses())) {
      sql.append(" and s.bargain_status in (:bargainStatuses) ");
    }
    if (StringUtils.isNotBlank(condition.getName())) sql.append(" and s.name like :name ");
    if (StringUtils.isNotBlank(condition.getOwner())) sql.append(" and s.owner like :owner ");
    if (StringUtils.isNotBlank(condition.getAgent())) {
      sql.append(" and s.agent like:agent ");
    }
    if (StringUtils.isNotBlank(condition.getFollowName())) {
      sql.append(" and s.follow_name like:followName ");
    }
//    if (StringUtils.isNotBlank(condition.getSalesman())) {
//      sql.append(" and ( ").append(" sale.name like :salesman ");
//      if(CollectionUtils.isNotEmpty(condition.getShopIds())){
//        sql.append(" or s.id in (:shopIds)");
//      }
//      sql.append(" )");
//    }
    if (!ArrayUtils.isEmpty(condition.getShopVersionName())) sql.append(" and sv.name in :shopVersionNames ");
    if (condition.getSubmitApplicationDateStart() != null)
      sql.append(" and s.submit_application_date >= :submitApplicationDateStart ");
    if (condition.getSubmitApplicationDateEnd() != null)
      sql.append(" and s.submit_application_date <= :submitApplicationDateEnd ");
    if (condition.getRegistrationDateStart() != null)
      sql.append(" and s.registration_date >= :registrationDateStart ");
    if (condition.getRegistrationDateEnd() != null)
      sql.append(" and s.registration_date <= :registrationDateEnd ");
    if (!isCount) {
      if (StringUtil.isEmpty(condition.getSortFiled())) {
        sql.append(" order by s.last_update desc");
      } else {
        sql.append(" order by s.").append(condition.getSortFiled()).append(" desc");
      }
    }
    SQLQuery query = session.createSQLQuery(sql.toString());
    if (!isCount) {
      query.addEntity(Shop.class)
          .addScalar("shopVersionName", StandardBasicTypes.STRING)
          .addScalar("managerUserNo", StandardBasicTypes.STRING)
          .addScalar("managerId", StandardBasicTypes.LONG);
    }
    if (!ArrayUtils.isEmpty(condition.getLocateStatuses())) {
      query.setParameterList("locateStatus", condition.getLocateStatuses());
    }
    if (!ArrayUtils.isEmpty(condition.getRegisterType())) {
      query.setParameterList("registerType", condition.getRegisterType());
    }
    if (!ArrayUtils.isEmpty(condition.getPaymentStatus())) {
      query.setParameterList("paymentStatus", condition.getPaymentStatus());
    }
    if (!ArrayUtils.isEmpty(condition.getAreaId()))
      query.setParameterList("areas", condition.getAreaId());
    if (StringUtils.isNotBlank(condition.getName())) query.setString("name", "%" + condition.getName() + "%");
//    if (StringUtils.isNotBlank(condition.getSalesman())){
//      if(CollectionUtils.isNotEmpty(condition.getShopIds())){
//        query.setParameterList("shopIds", condition.getShopIds());
//      }
//      query.setString("salesman", "%" + condition.getSalesman() + "%");
//    }
    if (StringUtils.isNotBlank(condition.getAgent())) {
      query.setString("agent", "%" + condition.getAgent() + "%");
    }
    if (StringUtils.isNotBlank(condition.getFollowName())) {
      query.setString("followName", "%" + condition.getFollowName() + "%");
    }
    if (StringUtils.isNotBlank(condition.getOwner())) query.setString("owner", "%" + condition.getOwner() + "%");
    if (!ArrayUtils.isEmpty(condition.getShopVersionName()))
      query.setParameterList("shopVersionNames", condition.getShopVersionName());
    if (!ArrayUtils.isEmpty(condition.getBargainStatuses()))
      query.setParameterList("bargainStatuses", condition.getBargainStatuses());
//    if (CollectionUtils.isNotEmpty(condition.getUserIds()))
//      query.setParameterList("userIds", condition.getUserIds());
    if (condition.getSubmitApplicationDateStart() != null)
      query.setLong("submitApplicationDateStart", condition.getSubmitApplicationDateStart());
    if (condition.getSubmitApplicationDateEnd() != null)
      query.setLong("submitApplicationDateEnd", condition.getSubmitApplicationDateEnd());
    if (condition.getRegistrationDateStart() != null)
      query.setLong("registrationDateStart", condition.getRegistrationDateStart());
    if (condition.getRegistrationDateEnd() != null)
      query.setLong("registrationDateEnd", condition.getRegistrationDateEnd());
    if (!ArrayUtils.isEmpty(condition.getShopStates())) {
      query.setParameterList("shopStates", condition.getShopStates());
    } else {
      query.setString("shopState", ShopState.DELETED.name());
    }
    return query.setParameterList("statuses", condition.getShopStatuses()).setString("userType", UserType.SYSTEM_CREATE.toString());
  }

  public static Query countShopByCondition(Session session, ShopSearchCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(*) ");
    return splittingQueryForSearchShopByCondition(session, condition, sql, true);
  }

  //已注册
  public static Query getShopByState(Session session, int pageNo, int pageSize) {
    return session.createQuery("select s from Shop as s where s.shopState = :shopState and s.shopStatus in (:shopStatus)")
        .setFirstResult(pageNo * pageSize).setMaxResults(pageSize).setParameter("shopState",ShopState.ACTIVE).setParameterList("shopStatus",ShopStatus.getShopTrialAndPaidString());
  }

  //shao
  public static SQLQuery countShopByState(Session session) {
    return session.createSQLQuery("SELECT COUNT(*) AS COUNT FROM config.shop AS shop WHERE state=1");
  }

  //待注册
  public static Query getShopByState1(Session session, int pageNo, int pageSize) {
    return session.createQuery("select s from Shop as s where s.shopStatus = :shopStatus")
        .setFirstResult(pageNo * pageSize).setMaxResults(pageSize).setParameter("shopStatus",ShopStatus.CHECK_PENDING);
  }

  //shao
  public static SQLQuery countShopByState1(Session session) {
    return session.createSQLQuery("SELECT COUNT(*) AS COUNT FROM config.shop AS shop WHERE state IS NULL");
  }

  public static Query getShops(Session session, int pageNo, int pageSize) {
    return session.createQuery("select s from Shop as s").setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query getShopByName(Session session, String shopName) {
    return session.createQuery("select s from Shop as s where s.shopState !=:shopState and  s.name = :shopName ")
        .setString("shopName", shopName).setParameter("shopState", ShopState.DELETED);
  }

  public static Query getActiveUsingShopByName(Session session, String shopName) {
    return session.createQuery("select s from Shop as s where s.shopStatus in(:shopStatus) and s.shopState !=:shopState and  s.name = :shopName ")
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPaid()).setString("shopName", shopName);
  }

  public static Query getShopByObscureName(Session session, String name, ShopStatus... shopStatuses) {
    String hql = "select s from Shop as s where s.shopState !=:shopState and s.name like :name ";
    if (ArrayUtil.isNotEmpty(shopStatuses)) {
      hql += " and s.shopStatus in(:shopStatus) ";
    }
    Query q = session.createQuery(hql).setParameter("shopState", ShopState.DELETED).setString("name", "%" + name + "%");
    if (ArrayUtil.isNotEmpty(shopStatuses)) {
      q.setParameterList("shopStatus", shopStatuses);
    }
    return q.setMaxResults(15);
  }

  public static Query getShopIdByShopCondition(Session session,ShopSearchCondition shopSearchCondition) {
    StringBuilder sql = new StringBuilder("select s.id from Shop as s where s.shopStatus in(:shopStatus) and s.shopState !=:shopState ");
    if(StringUtils.isNotBlank(shopSearchCondition.getName())){
      sql.append("  and s.name like :name");
    }
    if(shopSearchCondition.getReviewDateStart()!=null){
      sql.append("  and s.reviewDate >= :reviewDateStart").append(shopSearchCondition.getReviewDateStart());
    }
    if(shopSearchCondition.getReviewDateEnd()!=null){
      sql.append("  and s.reviewDate <:reviewDateEnd").append(shopSearchCondition.getReviewDateEnd());
    }
    if(StringUtils.isNotBlank(shopSearchCondition.getFollowName())){
      sql.append("  and s.followName like :followName");
    }
    if(!ArrayUtils.isEmpty(shopSearchCondition.getShopVersionIds())){
      sql.append("  and s.shopVersionId in (:shopVersionIds)");
    }
    if (!ArrayUtils.isEmpty(shopSearchCondition.getBargainStatuses())) {
      sql.append(" and s.bargainStatus in (:bargainStatuses) ");
    }

    Query query = session.createQuery(sql.toString())
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPaid());

    if(StringUtils.isNotBlank(shopSearchCondition.getName())){
      query.setString("name", "%" + shopSearchCondition.getName() + "%");
    }
    if(shopSearchCondition.getReviewDateStart()!=null){
      query.setLong("reviewDateStart",shopSearchCondition.getReviewDateStart());
    }
    if(shopSearchCondition.getReviewDateEnd()!=null){
      query.setLong("reviewDateEnd",shopSearchCondition.getReviewDateEnd());
    }
    if(StringUtils.isNotBlank(shopSearchCondition.getFollowName())){
      query.setString("followName", "%" + shopSearchCondition.getFollowName() + "%");
    }
    if(!ArrayUtils.isEmpty(shopSearchCondition.getShopVersionIds())){
      query.setParameterList("shopVersionIds", shopSearchCondition.getShopVersionIds());
    }
    if (!ArrayUtils.isEmpty(shopSearchCondition.getBargainStatuses())) {
      query.setParameterList("bargainStatuses", shopSearchCondition.getBargainStatusesEnum());
    }
    return query;
  }

  public static Query getAllAreaList(Session session) {
    return session.createQuery("from Area ")
      ;
  }

  public static Query getAreaListByJuheCityCode(Session session,Set<String> cityCodes) {
    return session.createQuery("from Area where juheCityCode in (:cityCodes)").setParameterList("cityCodes",cityCodes);
  }

  public static Query getAreaList(Session session, String parentNo) {
    return session.createQuery("select a from Area as a where a.parentNo = :parentNo")
        .setString("parentNo", parentNo);
  }

  public static Query getArea(Session session, long no) {
    return session.createQuery("select a from Area as a where a.no = :no").setLong("no", no);
  }

  public static Query getAreaListByParentNos(Session session, List<Long> noList) {
    return session.createQuery("select a from Area as a where a.parentNo in :parentNos")
        .setParameterList("parentNos", noList);
  }

  public static Query getAreaListByNos(Session session, Set<Long> noList) {
    return session.createQuery("select a from Area as a where a.no in (:noList)")
        .setParameterList("noList", noList);
  }

  public static Query getBusinessList(Session session, String parentNo) {
    return session.createQuery("select b from Business as b where b.parentNo = :parentNo")
        .setString("parentNo", parentNo);
  }

  public static Query getShopBusinessList(Session session, Long shopId) {
    return session.createQuery("select s from ShopBusiness as s where s.shopId = :shopId")
        .setLong("shopId", shopId);
  }

  public static Query countShop(Session session) {
    return session.createQuery("select count(*) from Shop");
  }

  public static Query getShopByStoreManagerMobile(Session session, String mobile) {
    return session.createQuery("select s from Shop as s where s.mobile = :mobile").setString("mobile", mobile);
  }

  public static Query countShopByAgentIdAndTime(Session session, Long agentId, Long startTime, Long endTime) {
    return session.createQuery("select count(*) from Shop s where s.agentId=:agentId and s.reviewDate>:startTime and s.reviewDate<:endTime")
        .setLong("agentId", agentId).setLong("startTime", startTime).setLong("endTime", endTime);
  }

  public static Query getImportRecordList(Session session, List<Long> importRecordIdList, Long shopId, String status, String type){
    return session.createQuery("select ir from ImportRecord ir where ir.shopId = :shopId and ir.id in (:importRecordIdList) and ir.status = :status and ir.type = :type ")
        .setLong("shopId", shopId).setParameterList("importRecordIdList", importRecordIdList).setString("status", status).setString("type", type);
  }

  public static Query getStoreManager(Session session, String name) {
    return session.createQuery("select s from Shop as s where s.storeManager = :storeManager").setString("storeManager", name);
  }

    //根据店面ID获取短信充值的列表
  public static Query getSmsBalanceByShopId(Session session, long shopId) {
    return session.createQuery("select sb from ShopBalance as sb where sb.shopId = :shopId")
        .setLong("shopId", shopId);
  }

  public static Query getShopUnit(Session session, Long shopId) {
    return session.createQuery("select su from ShopUnit as su where su.shopId =:shopId order by su.lastEditTime desc").setLong("shopId", shopId);
  }

  public static Query getShopUnitByUnitName(Session session, Long shopId, String unitName) {
    return session.createQuery("select su from ShopUnit as su where su.shopId =:shopId and su.unitName =:unitName")
        .setLong("shopId", shopId).setString("unitName", unitName);
  }

  public static Query getShopConfig(Session session,ShopConfigScene scene,Long shopId)
  {
    return session.createQuery("select sc from ShopConfig sc where sc.scene =:scene and sc.shopId = :shopId")
        .setLong("shopId",shopId).setParameter("scene",scene);
  }

  public static Query deleteAllShopConfig(Session session) {
    return session.createQuery("delete from ShopConfig");
  }

  public static Query searchShopConfigDTOByShopAndScene(Session session,Long shopId,ShopConfigScene scene,Integer startPageNo, Integer maxRows)
  {
    StringBuffer sb = new StringBuffer();

    sb.append("select sc from ShopConfig sc where sc.scene =:scene ");

    if(null != shopId)
    {
      sb.append(" and sc.shopId=:shopId");
    }

    Query q = session.createQuery(sb.toString()).setParameter("scene",scene).setFirstResult((startPageNo.intValue()-1)*maxRows.intValue()).setMaxResults(maxRows);

    if(null != shopId)
    {
      q.setLong("shopId",shopId);
    }

    return q;
  }

  public static Query countShopConfigByScene(Session session,Long shopId,ShopConfigScene scene)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(sc) from ShopConfig sc where sc.scene = :scene");
    if(null != shopId)
    {
      sb.append(" and sc.shopId= :shopId");
    }
    Query q = session.createQuery(sb.toString()).setParameter("scene",scene);

    if(null != shopId)
    {
      q.setLong("shopId",shopId);
    }
    return q;
  }

  public static Query getShopCustomerRelationByShopId(Session session, Long shopId){
    return session.createQuery("select s from ShopCustomerRelation s where s.shopId =:shopId").setLong("shopId",shopId);
  }

  public static Query getShopCustomerRelationByCustomerId(Session session, Long customerId){
    return session.createQuery("select s from ShopCustomerRelation s where s.customerId =:customerId").setLong("customerId",customerId);
  }

  public static Query getCustomerShopStatus(Session session, Long customerId){
    StringBuffer sb = new StringBuffer();
    sb.append("select b.shop_state from shop_customer_relation a, shop b where a.shop_id=b.id and a.customer_id=:customerId");
    Query q = session.createSQLQuery(sb.toString()).setLong("customerId",customerId);
    return q;
  }

  public static Query getWholesalerShopRelationByWholesalerShopId(Session session, Long wholesalerShopId,
                                                                  List<RelationTypes> relationTypes) {
    StringBuffer sb = new StringBuffer();
    sb.append("select w from WholesalerShopRelation w where w.wholesalerShopId =:wholesalerShopId ");
    sb.append(" and w.status =:status ");
    if (CollectionUtils.isNotEmpty(relationTypes)) {
      sb.append(" and w.relationType in(:relationTypes)");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("wholesalerShopId", wholesalerShopId)
        .setParameter("status", ShopRelationStatus.ENABLED);
    if (CollectionUtils.isNotEmpty(relationTypes)) {
      query.setParameterList("relationTypes", relationTypes);
    }
    return query;
  }

  public static Query getWholesalerShopRelationByShopId(Session session, Long shopId, List<RelationTypes> relationTypeList) {
    StringBuffer sb = new StringBuffer();
    sb.append("select w from WholesalerShopRelation w where w.shopId =:shopId and w.status =:status");
    if (CollectionUtils.isNotEmpty(relationTypeList)) {
      sb.append(" and w.relationType in(:relationTypes)");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setParameter("status", ShopRelationStatus.ENABLED);
    if (CollectionUtils.isNotEmpty(relationTypeList)) {
      query.setParameterList("relationTypes", relationTypeList);
    }
    return query;
  }

  public static Query getWholesalerShopRelationByWholesalerShopIds(Session session, Long shopId ,
                                                     List<RelationTypes> relationTypeList, Long ... wholesalerShopIds){
    StringBuffer sb = new StringBuffer();
    sb.append("select w from WholesalerShopRelation w ");
    sb.append(" where w.shopId =:shopId and w.wholesalerShopId in(:wholesalerShopIds) and w.status =:status");
    if(CollectionUtils.isNotEmpty(relationTypeList)) {
      sb.append(" and w.relationType in(:relationTypes)");
    }
    Query query = session.createQuery(sb.toString());
     query.setLong("shopId", shopId)
        .setParameter("status", ShopRelationStatus.ENABLED)
        .setParameterList("wholesalerShopIds",wholesalerShopIds);
    if (CollectionUtils.isNotEmpty(relationTypeList)) {
      query.setParameterList("relationTypes", relationTypeList);
    }
    return query;
  }

  public static Query getWholesalerShopRelationByCustomerShopIds(Session session, Long shopId, List<RelationTypes> relationTypeList, Long... customerShopIds){
    StringBuffer sb = new StringBuffer();
    sb.append("select w from WholesalerShopRelation w ")
        .append("where w.wholesalerShopId =:shopId and w.shopId in(:customerShopIds) and w.status =:status");
    if(CollectionUtils.isNotEmpty(relationTypeList)){
      sb.append(" and w.relationType in (:relationTypes)");
    }
    Query query = session.createQuery(sb.toString());
    query.setLong("shopId", shopId)
        .setParameter("status", ShopRelationStatus.ENABLED)
        .setParameterList("customerShopIds",customerShopIds);
    if(CollectionUtils.isNotEmpty(relationTypeList)){
      query.setParameterList("relationTypes", relationTypeList);
    }
    return query;
  }

  public static Query getShopByShopId(Session session, Long... shopId) {
    return session.createQuery("select s from Shop as s where s.id in (:shopId)")
        .setParameterList("shopId", shopId);
  }
  public static Query getShopByShopId(Session session, List<Long> shopId) {
    return session.createQuery("select s from Shop as s where s.id in (:shopId)")
        .setParameterList("shopId", shopId);
  }

  public static Query getShopWithoutShopIdByShopVersionId(Session session,long shopVersionId, Long... shopId) {
    return session.createQuery("select s from Shop as s where s.id not in (:shopId) and s.shopVersionId =:shopVersionId")
        .setParameterList("shopId", shopId).setLong("shopVersionId", shopVersionId);
  }
  public static Query countCRMOperationLogsByCondition(Session session, CRMOperationLogCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select count(l) from CRMOperationLog l where l.shopId=:shopId ");
    return splicingGetCRMOperationLogsByConditionHQL(session, condition, sql);
  }

  public static Query getCRMOperationLogsByCondition(Session session, CRMOperationLogCondition condition) {
    StringBuilder sql = new StringBuilder();
    sql.append("select l from CRMOperationLog l where l.shopId=:shopId ");
    Query q = splicingGetCRMOperationLogsByConditionHQL(session, condition, sql);
    return q.setFirstResult(condition.getStart()).setMaxResults(condition.getLimit());
  }
  private static Query splicingGetCRMOperationLogsByConditionHQL(Session session, CRMOperationLogCondition condition, StringBuilder sql){
    if (StringUtils.isNotBlank(condition.getUserNo())) {
      sql.append(" and l.userNo = :userNo");
    }
    if (StringUtils.isNotBlank(condition.getContent())) {
      sql.append(" and l.content like :content");
    }
    if (StringUtils.isNotBlank(condition.getIpAddress())) {
      sql.append(" and l.ipAddress = :ipAddress");
    }
    if(StringUtils.isNotBlank(condition.getType())){
      sql.append(" and l.type like:type");
    }
    if (StringUtils.isNotBlank(condition.getModule())) {
      sql.append(" and l.module =:module");
    }
    if (condition.getOperateTimeStart() != null) {
      sql.append(" and l.operateTime >=:operateTimeStart");
    }
    if (condition.getOperateTimeEnd() != null) {
      sql.append(" and l.operateTime <=:operateTimeEnd");
    }
    Query q = session.createQuery(sql.toString()).setLong("shopId", condition.getShopId());
    if (StringUtils.isNotBlank(condition.getUserNo())) {
      q.setString("userNo", condition.getUserNo());
    }
    if (StringUtils.isNotBlank(condition.getContent())) {
      q.setString("content", "%" + condition.getContent() + "%");
    }
    if (StringUtils.isNotBlank(condition.getIpAddress())) {
      q.setString("ipAddress", condition.getIpAddress());
    }
    if (StringUtils.isNotBlank(condition.getType())) {
      q.setString("type", "%" + condition.getType() + "%");
    }
    if (StringUtils.isNotBlank(condition.getModule())) {
      q.setString("module", condition.getModule());
    }
    if (condition.getOperateTimeStart() != null) {
      q.setLong("operateTimeStart", condition.getOperateTimeStart());
    }
    if (condition.getOperateTimeEnd() != null) {
      q.setLong("operateTimeEnd", condition.getOperateTimeEnd());
    }
    return q;
  }

  public static SQLQuery getMergeRecords(Session session,MergeRecordDTO mergeRecordIndex) {
    StringBuffer sb=new StringBuffer();
    sb.append("select child_id as childId,parent_id as parentId ,child,parent,operator,merge_time as mergeTime from merge_record  where shop_id=:shopId");
    if(StringUtil.isNotEmpty(mergeRecordIndex.getCustomerOrSupplierName())){
      sb.append(" and (parent like :customerName or child like :customerName)");
    }
    if(StringUtil.isNotEmpty(mergeRecordIndex.getOperator())){
      sb.append(" and operator like :operator");
    }
    if(mergeRecordIndex.getStartTime()!=null){
      sb.append(" and merge_time>=:startTime");
    }
    if(mergeRecordIndex.getEndTime()!=null){
      sb.append(" and merge_time<=:endTime");
    }
    if(mergeRecordIndex.getMergeType()!=null){
      sb.append(" and merge_type =:mergeType");
    }
    sb.append(" order by merge_time desc");
    SQLQuery query=(SQLQuery)session.createSQLQuery(sb.toString()).setLong("shopId",mergeRecordIndex.getShopId());
    if(mergeRecordIndex.getPager()!=null){
      query.setFirstResult(mergeRecordIndex.getPager().getRowStart()).setMaxResults(mergeRecordIndex.getPager().getPageSize());
    }
    if(StringUtil.isNotEmpty(mergeRecordIndex.getCustomerOrSupplierName())){
      query.setString("customerName","%"+mergeRecordIndex.getCustomerOrSupplierName()+"%");
    }
    if(StringUtil.isNotEmpty(mergeRecordIndex.getOperator())){
      query.setString("operator","%"+mergeRecordIndex.getOperator()+"%");
    }
    if(mergeRecordIndex.getStartTime()!=null){
      query.setLong("startTime",mergeRecordIndex.getStartTime());
    }
    if(mergeRecordIndex.getEndTime()!=null){
      query.setLong("endTime",mergeRecordIndex.getEndTime());
    }
    if(mergeRecordIndex.getMergeType()!=null){
      query.setString("mergeType",mergeRecordIndex.getMergeType().toString());
    }
    return query;
  }

  public static Query getMergeRecordCount(Session session,MergeRecordDTO mergeRecordIndex) {
    StringBuffer sb=new StringBuffer();
    sb.append("select count(*) from MergeRecord where shopId=:shopId");
    if(StringUtil.isNotEmpty(mergeRecordIndex.getCustomerOrSupplierName())){
      sb.append(" and (parent like :customerOrSupplierName or child like :customerOrSupplierName)");
    }
    if(StringUtil.isNotEmpty(mergeRecordIndex.getOperator())){
      sb.append(" and operator like :operator");
    }
    if(mergeRecordIndex.getStartTime()!=null){
      sb.append(" and mergeTime>=:startTime");
    }
    if(mergeRecordIndex.getEndTime()!=null){
      sb.append(" and mergeTime<=:endTime");
    }
    if(mergeRecordIndex.getMergeType()!=null){
      sb.append(" and merge_type =:mergeType");
    }
    Query query=session.createQuery(sb.toString()).setLong("shopId",mergeRecordIndex.getShopId());
    if(StringUtil.isNotEmpty(mergeRecordIndex.getCustomerOrSupplierName())){
      query.setString("customerOrSupplierName","%"+mergeRecordIndex.getCustomerOrSupplierName()+"%");
    }
    if(StringUtil.isNotEmpty(mergeRecordIndex.getOperator())){
      query.setString("operator","%"+mergeRecordIndex.getOperator()+"%");
    }
    if(mergeRecordIndex.getStartTime()!=null){
      query.setLong("startTime",mergeRecordIndex.getStartTime());
    }
    if(mergeRecordIndex.getEndTime()!=null){
      query.setLong("endTime",mergeRecordIndex.getEndTime());
    }
    if(mergeRecordIndex.getMergeType()!=null){
      query.setString("mergeType",mergeRecordIndex.getMergeType().toString());
    }
    return query;
  }

  public static Query getMergeRecordDetail(Session session,Long shopId,Long parentId,Long childId) {
    if(shopId==null||parentId==null) return null;
    StringBuffer sb=new StringBuffer();
    sb.append("from MergeRecord where shopId=:shopId and parentId=:parentId and childId=:childId");
    Query query=session.createQuery(sb.toString()).setLong("shopId", shopId)
        .setLong("parentId", parentId).setLong("childId",childId);
    return query;
  }

  public static Query getSupplierOrCustomerShopSuggestion(Session session,Long shopId,String searchWord,String shopVersionIdStr, boolean isTestShop,String customerOrSupplier,String shopRange) {
    List<String> status = ShopStatus.getShopTrialAndPaidString();

    StringBuffer sb = new StringBuffer();
    if("related".equals(shopRange)){
      sb.append(" select s.* from shop s left join wholesaler_shop_relation wsr");
      if("supplierOnline".equals(customerOrSupplier)){
        sb.append(" on s.id=wsr.wholesaler_shop_id where wsr.shop_id =:shopId and status = 'ENABLED'");
        sb.append(" and wsr.relation_type in(:relationTypes) ");
      }else if("customerOnline".equals(customerOrSupplier)){
        sb.append(" on s.id=wsr.shop_id where wsr.wholesaler_shop_id =:shopId and status = 'ENABLED'");
        sb.append(" and wsr.relation_type in(:relationTypes) ");
      }
      sb.append(" and s.id <>:shopId ");
      sb.append(" and s.shop_status in(:status) ");
      sb.append(" and s.shop_version_id in(" + shopVersionIdStr + ") ");
      if (isTestShop) {
        sb.append(" and s.shop_kind =:shopKind ");
      }else {
        sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
      }
      if (StringUtils.isNotBlank(searchWord)) {
        sb.append(" and (s.name like:searchWord or s.name_py like:searchWordPY or s.name_fl like:searchWordFL)");
      }
    }else if("notRelated".equals(shopRange)){
      sb.append(" select s.* from shop s left join ( ");
      if("supplierOnline".equals(customerOrSupplier)){
        sb.append(" select distinct wholesaler_shop_id as wsId from wholesaler_shop_relation");
        sb.append(" where shop_id =:shopId and status = 'ENABLED' and relation_type in (:relationTypes)) as ws");   //CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST
      }else if("customerOnline".equals(customerOrSupplier)){
        sb.append(" select distinct shop_id as wsId from wholesaler_shop_relation");
        sb.append(" where wholesaler_shop_id =:shopId and status = 'ENABLED' and relation_type in (:relationTypes)) as ws");//WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST
      }
      sb.append(" on s.id = ws.wsId where ws.wsId is null and s.id <>:shopId ");
      sb.append(" and s.shop_status in(:status) ");
      sb.append(" and s.shop_version_id in("+shopVersionIdStr+") ");
      if (isTestShop) {
        sb.append(" and s.shop_kind =:shopKind ");
      }else {
        sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
      }
      if (StringUtils.isNotBlank(searchWord)) {
        sb.append(" and (s.name like:searchWord or s.name_py like:searchWordPY or s.name_fl like:searchWordFL)");
      }

    }else{
      sb.append(" select s.* from shop s");
      sb.append(" where s.id <>:shopId ");
      sb.append(" and s.shop_status in(:status) ");
      sb.append(" and s.shop_version_id in("+shopVersionIdStr+") ");
      if (isTestShop) {
        sb.append(" and s.shop_kind =:shopKind ");
      }else {
        sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
      }
      if (StringUtils.isNotBlank(searchWord)) {
        sb.append(" and (s.name like:searchWord or s.name_py like:searchWordPY or s.name_fl like:searchWordFL)");
      }
    }

    Query query = session.createSQLQuery(sb.toString()).addEntity(Shop.class);
    query.setLong("shopId", shopId);
    query.setParameterList("status", status);
    query.setString("shopKind", ShopKind.TEST.name());
    if(StringUtils.isNotBlank(shopRange)){
      if("supplierOnline".equals(customerOrSupplier)){
        query.setParameterList("relationTypes", RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST);
      }else if("customerOnline".equals(customerOrSupplier)){
        query.setParameterList("relationTypes", RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST);
      }
    }

    if (StringUtils.isNotBlank(searchWord)) {
      query.setString("searchWord", "%" + searchWord + "%");
      query.setString("searchWordFL", "%" + searchWord.toLowerCase() + "%");
      query.setString("searchWordPY", searchWord.toLowerCase() + "%");
    }

    return query;
  }


  public static Query searchApplyCustomerShop(Session session,ApplyShopSearchCondition searchCondition,String shopVersionIdStr
      , boolean isTestShop, Pager pager) {
    List<String> status = ShopStatus.getShopTrialAndPaidString();

    StringBuffer sb = new StringBuffer();
    sb.append("select distinct s.* from shop s left join ( ");
    sb.append(" select distinct shop_id  as wsId from wholesaler_shop_relation  ");
    sb.append(" where wholesaler_shop_id =:shopId and status = 'ENABLED' and relation_type in (:relationTypes) ) as ws");
    sb.append(" on s.id = ws.wsId");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" left join shop_business_scope sbs on s.id=sbs.shop_id ");
    }
    sb.append(" where ws.wsId is null and s.id <>:shopId ");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" and sbs.product_category_id in(:thirdCategoryIdStr)");
    }

    sb.append(" and s.shop_status in(:status) ");
    sb.append(" and s.shop_version_id in("+shopVersionIdStr+") ");
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      sb.append(" and s.id in (:shopIds) ");
    }
    if (isTestShop) {
      sb.append(" and s.shop_kind =:shopKind ");
    }else {
      sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
    }
    if (StringUtils.isNotBlank(searchCondition.getName())) {
      sb.append(" and s.name like:name");
    }
    if (searchCondition.getRegionNo() != null) {
      sb.append(" and s.region =:region");
    } else if (searchCondition.getCityNo() != null) {
      sb.append(" and s.city =:city");
    } else if (searchCondition.getProvinceNo() != null) {
      sb.append(" and s.province =:province");
    }
    sb.append(" and s.area_id is not null");
    if (searchCondition.getSortProvinceNo() != null) {
      sb.append(" order by abs(:sortProvinceNo - COALESCE(s.province,1000)) asc");
    }
    if (searchCondition.getSortCityNo() != null) {
      sb.append(",abs(:sortCityNo - COALESCE(s.city,COALESCE(s.province,1000)*1000)) asc ");
    }
    if (searchCondition.getSortProvinceNo() != null) {
      sb.append(",abs(:sortRegionNo - COALESCE(s.region,COALESCE(s.city,COALESCE(s.province,1000)*1000)*100)) asc ");
    }

    Query query = session.createSQLQuery(sb.toString()).addEntity(Shop.class);
    query.setLong("shopId", searchCondition.getShopId());
    query.setParameterList("status", status);
    query.setParameterList("relationTypes", RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST);
    if (StringUtils.isNotBlank(searchCondition.getName())) {
      query.setString("name", "%" + searchCondition.getName() + "%");
    }
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      query.setParameterList("thirdCategoryIdStr",searchCondition.getThirdCategoryIdStr());
    }
    query.setString("shopKind", ShopKind.TEST.name());
    if (searchCondition.getRegionNo() != null) {
      query.setLong("region", searchCondition.getRegionNo());
    } else if (searchCondition.getCityNo() != null) {
      query.setLong("city", searchCondition.getCityNo());
    } else if (searchCondition.getProvinceNo() != null) {
      query.setLong("province", searchCondition.getProvinceNo());
    }
    if (searchCondition.getSortProvinceNo() != null) {
      query.setLong("sortProvinceNo", searchCondition.getSortProvinceNo());
    }
    if (searchCondition.getSortCityNo() != null) {
      query.setLong("sortCityNo", searchCondition.getSortCityNo());
    }
    if (searchCondition.getSortProvinceNo() != null) {
      query.setLong("sortRegionNo", searchCondition.getSortProvinceNo());
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      query.setParameterList("shopIds", searchCondition.getShopIds());
    }
    query.setFirstResult(pager.getRowStart());
    query.setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query countApplyCustomerShop(Session session,ApplyShopSearchCondition searchCondition,String shopVersionIdStr
      , boolean isTestShop) {
    List<String> status = ShopStatus.getShopTrialAndPaidString();
    StringBuffer sb = new StringBuffer();
    sb.append("select count(distinct s.id) as amount from shop s left join ( ");
    sb.append(" select distinct shop_id  as wsId from wholesaler_shop_relation  ");
    sb.append(" where wholesaler_shop_id =:shopId and status = 'ENABLED' and relation_type in (:relationTypes)) as ws");
    sb.append(" on s.id = ws.wsId");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" left join shop_business_scope sbs on s.id=sbs.shop_id ");
    }
    sb.append(" where ws.wsId is null and s.id <>:shopId ");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" and sbs.product_category_id in(:thirdCategoryIdStr)");
    }

    sb.append(" and s.shop_status in(:status) ");
    sb.append(" and s.shop_version_id in("+shopVersionIdStr+") ");
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      sb.append(" and s.id in (:shopIds) ");
    }
    if (isTestShop) {
      sb.append(" and  s.shop_kind =:shopKind ");
    }else {
      sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
    }
    if (StringUtils.isNotBlank(searchCondition.getName())) {
      sb.append(" and s.name like:name");
    }
    if (searchCondition.getRegionNo() != null) {
      sb.append(" and s.region =:region");
    } else if (searchCondition.getCityNo() != null) {
      sb.append(" and s.city =:city");
    } else if (searchCondition.getProvinceNo() != null) {
      sb.append(" and s.province =:province");
    }
    sb.append(" and s.area_id is not null");
    Query query = session.createSQLQuery(sb.toString()).addScalar("amount", StandardBasicTypes.INTEGER);
    query.setLong("shopId", searchCondition.getShopId());
    query.setParameterList("status", status);
    query.setParameterList("relationTypes", RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_STRING_LIST);
    query.setString("shopKind", ShopKind.TEST.name());
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      query.setParameterList("thirdCategoryIdStr",searchCondition.getThirdCategoryIdStr());
    }
    if (StringUtils.isNotBlank(searchCondition.getName())) {
      query.setString("name", "%" + searchCondition.getName() + "%");
    }
    if (searchCondition.getRegionNo() != null) {
      query.setLong("region", searchCondition.getRegionNo());
    } else if (searchCondition.getCityNo() != null) {
      query.setLong("city", searchCondition.getCityNo());
    } else if (searchCondition.getProvinceNo() != null) {
      query.setLong("province", searchCondition.getProvinceNo());
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      query.setParameterList("shopIds", searchCondition.getShopIds());
    }
    return query;
  }

  public static Query countApplySupplierShop(Session session,ApplyShopSearchCondition searchCondition,String shopVersionIdStr
      , boolean isTestShop) {
    List<String> status = ShopStatus.getShopTrialAndPaidString();

    StringBuffer sb = new StringBuffer();
    sb.append("select count(distinct s.id) as amount from shop s left join ( ");
    sb.append(" select distinct wholesaler_shop_id  as wsId from wholesaler_shop_relation");
    sb.append(" where shop_id =:shopId and status = 'ENABLED' and relation_type in (:relationTypes)) as ws");
    sb.append(" on s.id = ws.wsId");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" left join shop_business_scope sbs on s.id=sbs.shop_id ");
    }
    sb.append(" where ws.wsId is null and s.id <>:shopId ");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" and sbs.product_category_id in(:thirdCategoryIdStr)");
    }

    sb.append(" and s.shop_status in(:status) ");
    sb.append(" and s.shop_version_id in(" + shopVersionIdStr + ") ");
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      sb.append(" and s.id in (:shopIds) ");
    }
    if (isTestShop) {
      sb.append(" and s.shop_kind =:shopKind ");
    } else {
      sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
    }

    if (StringUtils.isNotBlank(searchCondition.getName())) {
      sb.append(" and s.name like:name");
    }
    if (searchCondition.getRegionNo() != null) {
      sb.append(" and s.region =:region");
    } else if (searchCondition.getCityNo() != null) {
      sb.append(" and s.city =:city");
    } else if (searchCondition.getProvinceNo() != null) {
      sb.append(" and s.province =:province");
    }
    sb.append(" and s.area_id is not null");
    Query query = session.createSQLQuery(sb.toString()).addScalar("amount", StandardBasicTypes.INTEGER);
    query.setLong("shopId", searchCondition.getShopId());
    query.setParameterList("status", status);
    query.setParameterList("relationTypes", RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST);
    query.setString("shopKind", ShopKind.TEST.name());
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      query.setParameterList("thirdCategoryIdStr",searchCondition.getThirdCategoryIdStr());
    }
    if (StringUtils.isNotBlank(searchCondition.getName())) {
      query.setString("name", "%" + searchCondition.getName() + "%");
    }
    if (searchCondition.getRegionNo() != null) {
      query.setLong("region", searchCondition.getRegionNo());
    } else if (searchCondition.getCityNo() != null) {
      query.setLong("city", searchCondition.getCityNo());
    } else if (searchCondition.getProvinceNo() != null) {
      query.setLong("province", searchCondition.getProvinceNo());
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      query.setParameterList("shopIds", searchCondition.getShopIds());
    }
    return query;
  }

  public static Query searchApplySupplierShop(Session session, ApplyShopSearchCondition searchCondition, String shopVersionIdStr
      , boolean isTestShop, Pager pager) {
    List<String> status = ShopStatus.getShopTrialAndPaidString();
    StringBuffer sb = new StringBuffer();
    sb.append("select distinct s.* from shop s left join ( ");
    sb.append(" select distinct wholesaler_shop_id  as wsId from wholesaler_shop_relation");
    sb.append(" where shop_id =:shopId and status = 'ENABLED' and relation_type in (:relationTypes)) as ws");
    sb.append(" on s.id = ws.wsId");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" left join shop_business_scope sbs on s.id=sbs.shop_id ");
    }
    if(StringUtil.isNotEmpty(searchCondition.getBrandName())||StringUtil.isNotEmpty(searchCondition.getModelName())){
      sb.append(" left join product.shop_vehicle_brand_model vbm on s.id=vbm.shop_id and vbm.deleted='FALSE'");
    }
    sb.append(" where ws.wsId is null and s.id <>:shopId ");
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      sb.append(" and sbs.product_category_id in(:thirdCategoryIdStr)");
    }
    if(StringUtil.isNotEmpty(searchCondition.getBrandName())){
      sb.append(" and vbm.brand_name like :brandName");
    }
     if(StringUtil.isNotEmpty(searchCondition.getModelName())){
      sb.append(" and vbm.model_name like :modelName");
    }
    sb.append(" and s.shop_status in(:status) ");
    sb.append(" and s.shop_version_id in(" + shopVersionIdStr + ") ");
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      sb.append(" and s.id in (:shopIds) ");
    }
    if (isTestShop) {
      sb.append(" and s.shop_kind =:shopKind ");
    } else {
      sb.append(" and (s.shop_kind is null or s.shop_kind !=:shopKind)");
    }

    if (StringUtils.isNotBlank(searchCondition.getName())) {
      sb.append(" and s.name like:name");
    }
    if (searchCondition.getRegionNo() != null) {
      sb.append(" and s.region =:region");
    } else if (searchCondition.getCityNo() != null) {
      sb.append(" and s.city =:city");
    } else if (searchCondition.getProvinceNo() != null) {
      sb.append(" and s.province =:province");
    }
    sb.append(" and s.area_id is not null");

    if (searchCondition.getSortProvinceNo() != null) {
      sb.append(" order by abs(:sortProvinceNo - COALESCE(s.province,1000)) asc");
    }
    if (searchCondition.getSortCityNo() != null) {
      sb.append(",abs(:sortCityNo - COALESCE(s.city,COALESCE(s.province,1000)*1000)) asc ");
    }
    if (searchCondition.getSortProvinceNo() != null) {
      sb.append(",abs(:sortRegionNo - COALESCE(s.region,COALESCE(s.city,COALESCE(s.province,1000)*1000)*100)) asc ");
    }
    Query query = session.createSQLQuery(sb.toString()).addEntity(Shop.class);
    query.setLong("shopId", searchCondition.getShopId());
    query.setParameterList("status", status);
    query.setParameterList("relationTypes", RelationTypes.CUSTOMER_RELATE_TO_WHOLESALER_STRING_LIST);
    query.setString("shopKind", ShopKind.TEST.name());
    if (StringUtils.isNotBlank(searchCondition.getName())) {
      query.setString("name", "%" + searchCondition.getName() + "%");
    }
    if (CollectionUtils.isNotEmpty(searchCondition.getShopIds())) {
      query.setParameterList("shopIds", searchCondition.getShopIds());
    }
    if(!ArrayUtils.isEmpty(searchCondition.getThirdCategoryIdStr())){
      query.setParameterList("thirdCategoryIdStr",searchCondition.getThirdCategoryIdStr());
    }
    if(StringUtil.isNotEmpty(searchCondition.getBrandName())){
      query.setParameter("brandName", "%" + searchCondition.getBrandName() + "%");
    }
    if(StringUtil.isNotEmpty(searchCondition.getModelName())){
      query.setParameter("modelName", "%" + searchCondition.getModelName() + "%");
    }
    if (searchCondition.getRegionNo() != null) {
      query.setLong("region", searchCondition.getRegionNo());
    } else if (searchCondition.getCityNo() != null) {
      query.setLong("city", searchCondition.getCityNo());
    } else if (searchCondition.getProvinceNo() != null) {
      query.setLong("province", searchCondition.getProvinceNo());
    }
    if (searchCondition.getSortProvinceNo() != null) {
      query.setLong("sortProvinceNo", searchCondition.getSortProvinceNo());
    }
    if (searchCondition.getSortCityNo() != null) {
      query.setLong("sortCityNo", searchCondition.getSortCityNo());
    }
    if (searchCondition.getSortProvinceNo() != null) {
      query.setLong("sortRegionNo", searchCondition.getSortProvinceNo());
    }

    query.setFirstResult(pager.getRowStart());
    query.setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getShopRelationInvitesByInvitedShopIds(
      Session session, InviteType inviteType, InviteStatus status, Long shopId,Long expiredTime, Long... invitedShopIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from ShopRelationInvite where originShopId =:shopId and invitedShopId in (:invitedShopIds)");
    if (inviteType != null) {
      sb.append(" and inviteType =:inviteType");
    }
    if (status != null) {
      sb.append(" and status =:status");
    }
    if(expiredTime!=null){
      sb.append(" and inviteTime >:expiredTime");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameterList("invitedShopIds", invitedShopIds);
    if (inviteType != null) {
      query.setString("inviteType", inviteType.name());
    }
    if (status != null) {
      query.setString("status", status.name());
    }
    if (expiredTime != null) {
      query.setLong("expiredTime", System.currentTimeMillis()-expiredTime);
    }
    return query;
  }

  public static Query getShopRelationInvitesByOriginShopIds(
      Session session, InviteType inviteType, InviteStatus status, Long invitedShopId,Long expiredTime, Long... originShopIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("from ShopRelationInvite where invitedShopId =:invitedShopId and originShopId in (:originShopIds)");
    if (inviteType != null) {
      sb.append(" and inviteType =:inviteType");
    }
    if (status != null) {
      sb.append(" and status =:status");
    }
    if(expiredTime!=null){
      sb.append(" and inviteTime >:expiredTime");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("invitedShopId", invitedShopId)
        .setParameterList("originShopIds", originShopIds);
    if (inviteType != null) {
      query.setString("inviteType", inviteType.name());
    }
    if (status != null) {
      query.setString("status", status.name());
    }
    if (expiredTime != null) {
      query.setLong("expiredTime", System.currentTimeMillis()-expiredTime);
    }
    return query;
  }

  public static Query getShopRelationInviteDTOByInvitedShopIdAndId(Session session, Long invitedShopId, Long... inviteId) {
    Query query = session.createQuery("from ShopRelationInvite where id in(:inviteId) and invitedShopId =:invitedShopId ")
        .setLong("invitedShopId", invitedShopId)
        .setParameterList("inviteId", inviteId);
    return query;
  }

  public static Query getShopRelationInviteInvitedShopIdsByOriginShopObscureName(Session session, String originShopName) {
    Query query = session.createQuery("select sri.invitedShopId from ShopRelationInvite sri,Shop s where s.name like :originShopName and s.id = sri.originShopId group by sri.invitedShopId")
        .setString("originShopName", "%" + originShopName + "%");
    return query;
  }

  public static Query getShopRelationInviteOriginShopByInvitedShopIds(Session session, Long... shopIds) {
    Query query = session.createQuery("select sri.invitedShopId,s from Shop s,ShopRelationInvite sri where s.id = sri.originShopId and sri.invitedShopId in (:shopIds)  group by s.id")
        .setParameterList("shopIds", shopIds);
    return query;
  }

  public static Query getOprationLogByObjectId(Session session, ObjectTypes type, Long objectId){
    Query q = session.createQuery("from OperationLog where objectType = :type and objectId = :objectId order by created")
        .setString("type",type.toString()).setLong("objectId",objectId);
    return q;
  }

  public static Query countPendingShopRelationInvites(Session session, Long shopId, InviteType inviteType) {
    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from ShopRelationInvite s where s.invitedShopId =:shopId and s.status =:status");
    if (inviteType != null) {
      sb.append(" and s.inviteType =:inviteType");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setString("status", InviteStatus.PENDING.toString());
    if (inviteType != null) {
      query.setString("inviteType", inviteType.toString());
    }
    return query;
  }
  public static Query getPendingShopRelationInvites(Session session, Long shopId, InviteType inviteType) {
    StringBuffer sb = new StringBuffer();
    sb.append("from ShopRelationInvite s where s.invitedShopId =:shopId and s.status =:status");
    if (inviteType != null) {
      sb.append(" and s.inviteType =:inviteType");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setString("status", InviteStatus.PENDING.toString());
    if (inviteType != null) {
      query.setString("inviteType", inviteType.toString());
    }
    return query;
  }

  public static Query countSearchShopRelationInvites(Session session, Long invitedShopId, InviteType inviteType, Long originShopId, List<InviteStatus> statuses) {

    StringBuffer sb = new StringBuffer();
    sb.append("select count(s.id) from ShopRelationInvite s where s.invitedShopId =:invitedShopId and s.status in(:statuses)");
    if (inviteType != null) {
      sb.append(" and s.inviteType =:inviteType");
    }
    if (originShopId != null) {
      sb.append(" and s.originShopId =:originShopId");
    }
    Query query = session.createQuery(sb.toString())
        .setLong("invitedShopId", invitedShopId)
        .setParameterList("statuses", statuses);
    if (inviteType != null) {
      query.setString("inviteType", inviteType.toString());
    }
    if (originShopId != null) {
      query.setLong("originShopId", originShopId);
    }
    return query;
  }

  public static Query searchShopRelationInvites(Session session, Long invitedShopId, InviteType inviteType,
                                                List<InviteStatus> statuses, Long originShopId, Pager pager) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s from ShopRelationInvite s where s.invitedShopId =:invitedShopId and s.status in(:statuses)");
    if (originShopId != null) {
      sb.append(" and s.originShopId =:originShopId");
    }
    if (inviteType != null) {
      sb.append(" and s.inviteType =:inviteType");
    }
    sb.append(" order by s.creationDate desc ");
    Query query = session.createQuery(sb.toString())
        .setLong("invitedShopId", invitedShopId)
        .setParameterList("statuses", statuses)
        .setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());

    if (originShopId != null) {
      query.setLong("originShopId", originShopId);
    }
    if (inviteType != null) {
      query.setString("inviteType", inviteType.toString());
    }
    return query;
  }

  public static Query getLatestShopOperateHistory(Session session, Long operateShopId, ShopOperateType type) {
    StringBuilder sql = new StringBuilder();
    sql.append("select soh from ShopOperateHistory soh where soh.operateShopId=:operateShopId ");
    if (type != null) {
      sql.append(" and soh.operateType=:operateType");
    }
    sql.append(" order by soh.creationDate desc ");
    Query query = session.createQuery(sql.toString()).setLong("operateShopId", operateShopId);
    if (type != null) {
      query.setParameter("operateType", type);
    }
    return query;
  }

  public static Query getVideoById(Session session,Long videoId) {
    String hql="from HelpVideo where id=:videoId";
    return session.createQuery(hql).setLong("videoId", videoId);
  }

  public static Query getRegisterInfoByRegisterShopId(Session session, Long registerShopId) {
    return session.createQuery("from RegisterInfo where registerShopId  =:registerShopId ").setLong("registerShopId", registerShopId);
  }

  public static Query getFirstReadyShopOperationTask(Session session) {
    return session.createQuery("from ShopOperationTask where exeStatus  =:exeStatus order by createTime")
        .setParameter("exeStatus",ExeStatus.READY).setMaxResults(1);
  }

  public static Query getRelationWholesalerShopIds(Session session, Long shopId, List<RelationTypes> relationTypes) {
    StringBuffer sb = new StringBuffer();
    sb.append("select sr.wholesalerShopId from WholesalerShopRelation sr  ");
    sb.append(" WHERE sr.shopId =:shopId ");
    sb.append(" and sr.status =:shopRelationStatus ");
    sb.append(" and sr.relationType in (:relationTypes)");
    Query query = session.createQuery(sb.toString())
        .setLong("shopId", shopId)
        .setParameter("shopRelationStatus", ShopRelationStatus.ENABLED)
        .setParameterList("relationTypes", relationTypes);
    return query;
  }

 public static Query getRelationCustomerShopIds(Session session, Long wholesalerShopId,List<RelationTypes> relationTypes) {
   StringBuffer sb = new StringBuffer();
      sb.append("select sr.shopId from WholesalerShopRelation sr  ");
      sb.append(" WHERE sr.wholesalerShopId =:wholesalerShopId ");
      sb.append(" and sr.status =:shopRelationStatus ");
      sb.append(" and sr.relationType in (:relationTypes)");
      Query query = session.createQuery(sb.toString())
          .setLong("wholesalerShopId", wholesalerShopId)
          .setParameter("shopRelationStatus", ShopRelationStatus.ENABLED)
          .setParameterList("relationTypes", relationTypes);
      return query;
  }

//  public static Query getBcgogoRecommendSupplierShopIds(Session session, Long shopId) {
//    StringBuffer sb = new StringBuffer();
//    sb.append("select sr.wholesaler_shop_id as wholesalerShopId from wholesaler_shop_relation sr LEFT JOIN shop s ");
//    sb.append(" on sr.wholesaler_shop_id = s.id WHERE sr.shop_id =:shopId");
//    sb.append(" and sr.`status` =:shopRelationStatus and s.shop_state =:shopState and s.shop_status in(:shopStatus) ");
//    sb.append(" and s.shop_recommended_type in(:shopRecommendedType) ");
////    sb.append(" order by s.shop_recommended_type desc ");
//    SQLQuery query = session.createSQLQuery(sb.toString()).addScalar("wholesalerShopId",StandardBasicTypes.LONG);
//    query.setLong("shopId",shopId);
//    query.setParameter("shopRelationStatus",ShopRelationStatus.ENABLED.name());
//    query.setParameter("shopState",ShopState.ACTIVE.name());
//    query.setParameterList("shopStatus",ShopStatus.getShopTrialAndPaidString());
//    query.setParameterList("shopRecommendedType",ShopRecommendedType.getRecommendedType());
//    return query;
//  }

  public static Query getBcgogoRecommendSupplierShopIds(Session session, Long shopId,List<Long> shopVersionIds) {
    StringBuffer sb = new StringBuffer();
    sb.append("select s.id as id from shop s ");
    sb.append(" where s.shop_state =:shopState and s.shop_status in(:shopStatus) ");
    sb.append(" and s.shop_recommended_type in(:shopRecommendedType) and s.id !=:shopId and s.shop_version_id in(:shopVersionIds)");
    sb.append(" order by s.shop_recommended_grade asc");
    SQLQuery query = session.createSQLQuery(sb.toString()).addScalar("id",StandardBasicTypes.LONG);
    query.setLong("shopId",shopId);
    query.setParameter("shopState",ShopState.ACTIVE.name());
    query.setParameterList("shopStatus",ShopStatus.getShopTrialAndPaidString());
    query.setParameterList("shopRecommendedType",ShopRecommendedType.getRecommendedType());
    query.setParameterList("shopVersionIds",shopVersionIds);
    return query;
  }

  public static Query getAttachmentByShopId(Session session, Long shopId, AttachmentType attachmentType) {
    return session.createQuery("from Attachment where shopId  =:shopId and type=:type ").setLong("shopId", shopId).setParameter("type", attachmentType);
  }

  public static Query getSaleManShopMapByShopId(Session session, Long shopId) {
    return session.createQuery("from SaleManShopMap where shopId  =:shopId").setLong("shopId", shopId);
  }

  public static Query getSmsDonationLogByShopId(Session session, Long shopId) {
    return session.createQuery("from SmsDonationLog where shopId  =:shopId").setLong("shopId", shopId);
  }

  public static Query getShopBargainRecordsByShopId(Session session, long shopId) {
    return session.createQuery("from ShopBargainRecord where shopId  =:shopId").setLong("shopId", shopId);
  }

  public static Query getShopAuditPassBargainRecordByShopId(Session session,Long... shopId) {
    return session.createQuery("from ShopBargainRecord where shopId  in(:shopId) and bargainStatus =:bargainStatus").setParameterList("shopId", shopId).setParameter("bargainStatus",BargainStatus.AUDIT_PASS);
  }

  public static Query getShopBargainRecordByShopId(Session session, long shopId, BargainStatus status) {
    return session.createQuery("from ShopBargainRecord where shopId  =:shopId and bargainStatus =:status")
        .setLong("shopId", shopId).setParameter("status", status);
  }

  public static Query getShopExtensionLogs(Session session, long shopId) {
    return session.createQuery("from ShopExtensionLog where shopId  =:shopId").setLong("shopId", shopId);
  }

  public static Query countShopExtensionLogs(Session session, long shopId) {
    return session.createQuery("select count(*) from ShopExtensionLog where shopId  =:shopId").setLong("shopId", shopId);
  }

  public static Query countShopByShopStatusAndShopStatues(Session session, ShopState[] shopStates, ShopStatus shopStatus) {
    String hql = "select count(*) from Shop where shopStatus=:shopStatus ";
    if (!ArrayUtils.isEmpty(shopStates)) {
      hql += " and shopState in (:shopStates)";
    }
    Query query = session.createQuery(hql.toString()).setParameter("shopStatus", shopStatus);
    if (!ArrayUtils.isEmpty(shopStates)) {
      query.setParameterList("shopStates", shopStates);
    }
    return query;
  }

  public static Query getAllShopIds(Session session) {
    return session.createQuery("select s.id from Shop s");
  }

  public static Query getActiveShopIds(Session session) {
    return session.createQuery("select  s.id from Shop as s where s.shopStatus in(:shopStatus) and s.shopState !=:shopState")
        .setParameter("shopState", ShopState.DELETED).setParameterList("shopStatus", ShopStatus.getShopTrialAndPaid());
  }

  public static Query getCustomizerConfigByShopId(Session session, Long shopId) {
    return session.createQuery("from PageCustomizerConfig where shopId=:shopId and status=:status")
        .setLong("shopId", shopId).setParameter("status", PageCustomizerConfigStatus.ACTIVE);
  }

  public static Query getRelatedShopByCustomerMobile(Session session, Long shopId, Integer cancelRecommendAssociatedCountLimit, ShopKind shopKind) {
    StringBuilder sql = new StringBuilder();
    sql.append("select c.id as customerId,ct.mobile as customerMobile,s.id as shopId,s.name as shopName,c.name as customerName")
        .append(" from bcuser.customer c,config.shop s,config.shop_contact sc,bcuser.contact ct ")
        .append(" where sc.shop_id=s.id and ct.customer_id=c.id and ct.mobile = sc.mobile and (c.status is null or c.status!=:customerStatus)")
        .append(" and c.cancel_recommend_associated_count <=:cancelRecommendAssociatedCountLimit and c.cancel_recommend_associated_count >=0 ")
        .append(" and c.shop_id =:shopId and s.id !=:shopId and c.customer_shop_id is null ")
        .append(" and s.shop_status =:status and s.shop_kind =:shopKind ")
//        .append(" and s.shop_version_id not in (:shopVersionIds)")
        .append(" and ct.disabled =:customerContactEnabled")
        .append(" and sc.disabled =:shopContactEnabled")
        .append(" group by s.id");
    return session.createSQLQuery(sql.toString())
        .addScalar("customerId", StandardBasicTypes.LONG)
        .addScalar("customerMobile", StandardBasicTypes.STRING)
        .addScalar("shopId", StandardBasicTypes.LONG)
        .addScalar("shopName", StandardBasicTypes.STRING)
        .addScalar("customerName", StandardBasicTypes.STRING)
        .setLong("shopId", shopId)
        .setParameter("shopKind", shopKind.toString())
        .setParameter("customerStatus", CustomerStatus.DISABLED.toString())
        .setParameter("status", ShopStatus.REGISTERED_PAID.name())
        .setInteger("cancelRecommendAssociatedCountLimit", cancelRecommendAssociatedCountLimit)
//        .setParameterList("shopVersionIds", ConfigUtils.getWholesalerVersion())
        .setParameter("customerContactEnabled", ContactConstant.ENABLED)
        .setParameter("shopContactEnabled", ContactConstant.ENABLED);
  }

  public static Query getRelatedShopBySupplierMobile(Session session, Long shopId, Integer cancelRecommendAssociatedCountLimit, ShopKind shopKind) {
    StringBuilder sql = new StringBuilder();
    List<Long> shopVersionIds = ConfigUtils.getWholesalerVersion();
    sql.append("select su.id as supplierId,ct.mobile as supplierMobile,s.id as shopId,s.name as shopName,su.name as supplierName")
        .append(" from bcuser.supplier su,config.shop s,config.shop_contact sc,bcuser.contact ct ")
        .append(" where sc.shop_id=s.id and ct.supplier_id=su.id and ct.mobile = sc.mobile and (su.status is null or su.status!=:supplierStatus) ")
        .append(" and su.cancel_recommend_associated_count <=:cancelRecommendAssociatedCountLimit and su.cancel_recommend_associated_count >=0 ")
        .append(" and su.shop_id =:shopId and s.id !=:shopId and su.supplier_shop_id is null ")
        .append(" and s.shop_status =:status and s.shop_kind =:shopKind ")
        .append(" and s.shop_version_id in (:shopVersionIds)")
        .append(" and ct.disabled =:customerContactEnabled")
        .append(" and sc.disabled =:shopContactEnabled")
        .append(" group by s.id");
    return session.createSQLQuery(sql.toString())
        .addScalar("supplierId", StandardBasicTypes.LONG)
        .addScalar("supplierMobile", StandardBasicTypes.STRING)
        .addScalar("shopId", StandardBasicTypes.LONG)
        .addScalar("shopName", StandardBasicTypes.STRING)
        .addScalar("supplierName", StandardBasicTypes.STRING)
        .setLong("shopId", shopId)
        .setParameter("shopKind", shopKind.toString())
        .setParameter("supplierStatus", CustomerStatus.DISABLED.toString())
        .setParameter("status", ShopStatus.REGISTERED_PAID.name())
        .setInteger("cancelRecommendAssociatedCountLimit", cancelRecommendAssociatedCountLimit)
        .setParameterList("shopVersionIds", shopVersionIds)
        .setParameter("customerContactEnabled", ContactConstant.ENABLED)
        .setParameter("shopContactEnabled", ContactConstant.ENABLED);
  }

  public static Query getRecommendedShop(Session session) {
    return session.createQuery("from Shop where shopRecommendedType in(:shopRecommendedTypes)")
        .setParameterList("shopRecommendedTypes", ShopRecommendedType.getRecommendedType());
  }

  public static Query getShopRelationInviteInShopIds(Session session, Set<Long> shopIdSet, Long shopId, Set<InviteStatus> statusSet) {
    StringBuffer sb = new StringBuffer();
    sb.append("from ShopRelationInvite where ((originShopId in(:shopIdSet) and invitedShopId =:shopId) or (invitedShopId in(:shopIdSet) and originShopId=:shopId)) ");
    if (CollectionUtils.isNotEmpty(statusSet)) {
      sb.append(" and status in (:statusSet)");
    }
    Query query = session.createQuery(sb.toString()).setParameterList("shopIdSet", shopIdSet).setLong("shopId", shopId);
    if (CollectionUtils.isNotEmpty(statusSet)) {
      query.setParameterList("statusSet", statusSet);
    }
    return query;
  }

  public static Query getShopContactsByShopId(Session session, Long shopId) {
    return session.createQuery("from ShopContact where disabled=:disabled and shopId =:shopId ").setLong("shopId", shopId).setInteger("disabled", ContactConstant.ENABLED);
  }

  public static Query getShopIdByContactMobile(Session session, Long customerShopId, Long supplierShopId, List<String> mobiles) {
    return session
        .createSQLQuery("select s.id from shop s,shop_contact sc where s.id=sc.shop_id and s.id not in (:ids) and sc.mobile in (:mobiles) and sc.disabled=:disabled")
        .addScalar("id", StandardBasicTypes.LONG)
        .setParameterList("ids", new Long[]{customerShopId,supplierShopId}).setParameterList("mobiles", mobiles)
        .setInteger("disabled", ContactConstant.ENABLED);
  }


  public static Query getShopBusinessScopeByShopId(Session session,Set<Long> shopIdSet) {
    return session.createQuery("from ShopBusinessScope where shopId  in(:shopIdSet)").setParameterList("shopIdSet", shopIdSet);
  }

  public static Query getBusinessScopeIdsByShopId(Session session,Long shopId) {
    return session.createQuery("select productCategoryId from ShopBusinessScope where shopId=:shopId").setLong("shopId",shopId);
  }

  public static Query getShopBusinessScopeProductCategoryIdListByShopId(Session session, Long shopId) {
    return session.createQuery("select productCategoryId from ShopBusinessScope where shopId  =:shopId").setLong("shopId",shopId);
  }

   public static Query getBcgogoRecommendSupplierShop(Session session, Long shopId,List<Long> shopVersionIds) {
    StringBuffer sb = new StringBuffer();
    sb.append(" select s.id as id, s.name as name, s.province as province, s.city as city, s.region as region from shop s ");
    sb.append(" where s.shop_state =:shopState and s.shop_status in(:shopStatus) ");
    sb.append(" and s.shop_recommended_type in(:shopRecommendedType) and s.id !=:shopId and s.shop_version_id in(:shopVersionIds)");
    sb.append(" order by s.shop_recommended_grade asc");
    SQLQuery query = session.createSQLQuery(sb.toString()).addScalar("id",StandardBasicTypes.LONG).addScalar("name",StandardBasicTypes.STRING)
        .addScalar("province",StandardBasicTypes.LONG).addScalar("city",StandardBasicTypes.LONG).addScalar("region",StandardBasicTypes.LONG);
    query.setLong("shopId",shopId);
    query.setParameter("shopState",ShopState.ACTIVE.name());
    query.setParameterList("shopStatus",ShopStatus.getShopTrialAndPaidString());
    query.setParameterList("shopRecommendedType",ShopRecommendedType.getRecommendedType());
    query.setParameterList("shopVersionIds",shopVersionIds);
    return query;
  }

  public static Query getDataImageRelation(Session session, Set<Long> shopIdSet, Set<String> appUserNoSet, Set<ImageType> imageTypeSet, DataType dataType, Long... dataId) {
    StringBuilder sql = new StringBuilder("select d,i from DataImageRelation d,ImageInfo i where d.imageId=i.id and d.dataType=:dataType ");
    if (CollectionUtils.isNotEmpty(appUserNoSet)) {
      sql.append(" and d.appUserNo in (:appUserNo) ");
    }
    if (CollectionUtils.isNotEmpty(shopIdSet)) {
      sql.append(" and d.shopId in (:shopId) ");
    }
    sql.append(" and d.dataId in(:dataId) and d.imageType in(:imageTypeSet) and d.status=:status order by d.imageSequence ");
    Query query = session.createQuery(sql.toString())
        .setParameterList("dataId", dataId)
        .setParameter("status", ObjectStatus.ENABLED)
        .setParameter("dataType", dataType)
        .setParameterList("imageTypeSet", imageTypeSet);
    if (CollectionUtils.isNotEmpty(shopIdSet)) {
      query.setParameterList("shopId", shopIdSet);
    }
    if (CollectionUtils.isNotEmpty(appUserNoSet)) {
      query.setParameterList("appUserNo", appUserNoSet);
    }
    return query;
  }

  public static Query getAppUserDataImageRelation(Session session,Set<String> appUseNos,Set<ImageType> imageTypeSet,DataType dataType, Long... dataId) {

    StringBuffer sb = new StringBuffer();
    sb.append("select d,i from DataImageRelation d,ImageInfo i ");
    sb.append("where d.imageId=i.id and d.dataType=:dataType and d.appUserNo in (:appUseNos) ");
    sb.append("and d.dataId in(:dataId) and d.imageType in(:imageTypeSet) and d.status=:status order by d.imageSequence");

    Query query = session.createQuery(sb.toString())
        .setParameterList("dataId", dataId)
        .setParameter("status", ObjectStatus.ENABLED)
        .setParameter("dataType", dataType)
        .setParameterList("imageTypeSet", imageTypeSet)
        .setParameterList("appUseNos", appUseNos);
    return query;
  }

  public static Query getDataImageRelation(Session session,Long shopId,ImageType imageType,DataType dataType, Long dataId,int imageSequence) {
    StringBuffer sb = new StringBuffer("select d,i from DataImageRelation d,ImageInfo i where d.imageId=i.id and d.imageSequence=:imageSequence and d.dataType=:dataType and d.shopId =:shopId and d.dataId =:dataId and d.imageType =:imageType and d.status=:status");
    Query query = session.createQuery(sb.toString())
        .setLong("dataId", dataId)
        .setParameter("status", ObjectStatus.ENABLED)
        .setParameter("dataType", dataType)
        .setParameter("imageType", imageType)
        .setLong("shopId", shopId)
        .setInteger("imageSequence", imageSequence);
    return query;
  }
  public static Query getMaintainShopLog(Session session, Long shopId) {
    return session.createQuery("from MaintainShopLog where shopId =:shopId order by creationDate desc")
        .setLong("shopId", shopId);
  }

  public static Query getShopBusinessScopeById(Session session, Set<Long> ids) {
    return session.createQuery("from ShopBusinessScope where productCategoryId in (:ids) order by creationDate desc")
        .setParameterList("ids", ids);
  }

  public static Query getSolrReindexJobByBatchIdShopId(Session session, Long batchId, Long shopId) {
    return session.createQuery("from SolrReindexJob where batchId = :batchId and shopId = :shopId")
        .setLong("batchId", batchId).setLong("shopId", shopId);
  }

  public static Query getFailedSolrReindexJob(Session session, Long batchId, String reindexType) {
    return session.createQuery("from SolrReindexJob where batchId = :batchId and reindexType = :reindexType and exeStatus = :exeStatus")
        .setLong("batchId", batchId).setString("reindexType", reindexType).setParameter("exeStatus", ExeStatus.EXCEPTION);
  }

  public static Query getTodoJobByBatchId(Session session, Long batchId) {
    return session.createQuery("from SolrReindexJob where batchId = :batchId and exeStatus = :exeStatus order by shopId")
        .setLong("batchId", batchId).setParameter("exeStatus", ExeStatus.READY).setMaxResults(1);
  }

  public static Query getDataImageInfoList(Session session,Long shopId,Set<ImageType> imageTypeSet,DataType dataType, Long dataId) {
    StringBuffer sb = new StringBuffer("select i from DataImageRelation d,ImageInfo i where d.imageId=i.id and d.dataType=:dataType and d.shopId =:shopId and d.dataId =:dataId and d.imageType in(:imageTypeSet) and d.status=:status");

    Query query = session.createQuery(sb.toString())
        .setLong("dataId", dataId)
        .setParameter("status", ObjectStatus.ENABLED)
        .setParameter("dataType", dataType)
        .setParameterList("imageTypeSet", imageTypeSet)
        .setLong("shopId", shopId);
    return query;
  }


  @Deprecated
  public static Query getAllDataImageInfoList(Session session,Set<ImageType> imageTypeSet) {
    StringBuffer sb = new StringBuffer("select i from DataImageRelation d,ImageInfo i where d.imageId=i.id and d.imageType in(:imageTypeSet)");

    Query query = session.createQuery(sb.toString())
        .setParameterList("imageTypeSet", imageTypeSet);
    return query;
  }

  public static Query countBeFavoured(Session session, Long paramShopId) {
    String str = "select count(w.id) from WholesalerShopRelation w where status = :status and (" +
        "(relationType = :related AND (shopId = :paramShopId OR wholesalerShopId = :paramShopId)) " +
        "OR " +
        "(relationType = :customerCollection AND wholesalerShopId = :paramShopId) " +
        "OR " +
        "(relationType = :supplierCollection AND shopId = :paramShopId) " +
        ")";
    return session.createQuery(str).setParameter("status", ShopRelationStatus.ENABLED).setParameter("related", RelationTypes.RELATED)
        .setLong("paramShopId", paramShopId).setParameter("customerCollection", RelationTypes.CUSTOMER_COLLECTION)
        .setParameter("supplierCollection", RelationTypes.SUPPLIER_COLLECTION);
  }


  public static Query getAreaByCityCode(Session session, Integer cityCode) {
    return session.createQuery("from Area where cityCode =:cityCode")
      .setInteger("cityCode", cityCode).setMaxResults(1);
  }

  public static Query getServiceCategory(Session session, Long shopId) {
    return session.createQuery("from ServiceCategory s where s.shopId =:shopId and s.deleted =:isDeleted")
      .setLong("shopId", shopId).setParameter("isDeleted",DeletedType.FALSE);
  }

 public static Query getAgentProductByShopId(Session session, Long shopId) {
    return session.createQuery("from AgentProduct s where s.shopId =:shopId and s.deleted =:isDeleted")
      .setLong("shopId", shopId).setParameter("isDeleted",DeletedType.FALSE);
 }

 public static Query getShopAgentProductByShopId(Session session, Long shopId) {
   return session.createQuery("from ShopAgentProduct s where s.shopId =:shopId and s.deleted =:isDeleted")
       .setLong("shopId", shopId).setParameter("isDeleted",DeletedType.FALSE);
 }


  public static Query getShopServiceCategoriesById(Session session, Long shopId) {
    return session.createQuery("from ShopServiceCategory s where s.shopId =:shopId and s.deleted =:isDeleted")
      .setLong("shopId", shopId).setParameter("isDeleted",DeletedType.FALSE);
  }
  public static Query getShopServiceCategoryByShopIds(Session session, Long[] shopIds) {
    return session.createQuery("from ShopServiceCategory s where s.shopId in (:shopIds) and s.deleted =:isDeleted")
        .setParameterList("shopIds", shopIds).setParameter("isDeleted",DeletedType.FALSE);
  }

  public static Query getServiceCategoryChildren(Session session, Long parentId) {
    return session.createQuery("select s.id from ServiceCategory s where s.parentId =:parentId and s.deleted =:isDeleted")
        .setLong("parentId", parentId).setParameter("isDeleted", DeletedType.FALSE);
  }

  public static Query getServiceCategoryChildrenIds(Session session, Set<Long> parentId) {
    return session.createQuery("select s.id from ServiceCategory s where s.parentId in (:parentId) and s.deleted =:isDeleted")
        .setParameterList("parentId", parentId).setParameter("isDeleted", DeletedType.FALSE);
  }

  public static Query getServiceCategoryChildrenIdsByParentServiceScope(Session session, ServiceScope scope) {
    return session.createSQLQuery("select s.id as id from service_category s where s.deleted =:isDeleted and s.parent_id =(select p.id from service_category p where p.service_scope =(:scope) and p.deleted =:isDeleted)")
        .addScalar("id", StandardBasicTypes.LONG)
        .setParameter("isDeleted", DeletedType.FALSE.name())
        .setParameter("scope", scope.name());
  }

  public static Query getAllRelatedShopIds(Session session) {
    StringBuilder sb = new StringBuilder();
    sb.append(" select distinct w.shop_id as shopId from wholesaler_shop_relation w where w.status = 'ENABLED' ");
    sb.append(" union ");
    sb.append(" select distinct w.wholesaler_shop_id as shopId from wholesaler_shop_relation w where w.status = 'ENABLED'");
    return session.createSQLQuery(sb.toString()).addScalar("shopId",StandardBasicTypes.LONG);
  }
  public static Query getTodoBugfixShops(Session session) {
    String sql = "select s.* from shop s left join shop_contact sc on s.id = sc.shop_id where sc.id is null";
    return session.createSQLQuery(sql).addEntity(Shop.class);
  }

  public static Query getRecentlyUsedDataList(Session session, Long shopId,Long userId, RecentlyUsedDataType type,Integer maxSize) {
    return session.createQuery("from RecentlyUsedData where shopId = :shopId and userId =:userId and type=:type order by time desc")
        .setLong("shopId", shopId).setLong("userId", userId).setParameter("type",type).setMaxResults(maxSize);
  }
  public static Query getRecentlyUsedDataByDataId(Session session, Long shopId,Long userId, RecentlyUsedDataType type,Long dataId) {
    return session.createQuery("from RecentlyUsedData where shopId = :shopId and userId =:userId and type=:type and dataId =:dataId")
        .setLong("shopId", shopId).setLong("userId", userId).setLong("dataId", dataId).setParameter("type",type);
  }
  public static Query getRecentlyUsedDataListByDataId(Session session, Long shopId,Long userId, RecentlyUsedDataType type,Long... dataId) {
    return session.createQuery("from RecentlyUsedData where shopId = :shopId and userId =:userId and type=:type and dataId in(:dataId)")
        .setLong("shopId", shopId).setLong("userId", userId).setParameterList("dataId", dataId).setParameter("type",type);
  }

  public static Query deleteAllRecentlyUsedDataByType(Session session, Long shopId,Long userId, RecentlyUsedDataType type) {
    return session.createQuery("delete from RecentlyUsedData where shopId = :shopId and userId =:userId and type=:type ")
        .setLong("shopId", shopId).setLong("userId", userId).setParameter("type",type);
  }

  public static Query statRecentlyUsedDataCountByDataId(Session session, Long shopId,RecentlyUsedDataType type,Long... dataIds) {
    StringBuilder sb=new StringBuilder();
    sb.append("select r.dataId,sum(r.count) from RecentlyUsedData r where   r.type=:type and r.dataId in(:dataIds)");
    if(shopId!=null){
      sb.append(" and r.shopId = :shopId");
    }
    sb.append(" group by r.dataId");
    Query query= session.createQuery(sb.toString()).setParameterList("dataIds", dataIds).setParameter("type",type);
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }

  public static Query statShopRecentlyUsedDataCount(Session session, Long shopId,RecentlyUsedDataType type) {
    StringBuilder sb=new StringBuilder();
    sb.append("select sum(r.count) from RecentlyUsedData r where  r.type=:type and r.count is not null");
    if(shopId!=null){
      sb.append(" and r.shopId = :shopId");
    }
    Query query= session.createQuery(sb.toString()).setParameter("type",type);
    if(shopId!=null){
      query.setLong("shopId", shopId);
    }
    return query;
  }

 public static Query getViewedBusinessChance(Session session, Long shopId,Long userId,Long preBuyOrderItemId) {
    return session.createQuery("from RecentlyUsedData where shopId = :shopId and userId=:userId and dataId=:dataId")
        .setLong("shopId", shopId).setLong("userId",userId).setLong("dataId",preBuyOrderItemId);
  }

  public static Query getViewedBusinessChance(Session session, Long shopId,Long userId,Integer limit) {
    Query query= session.createQuery("from RecentlyUsedData where shopId = :shopId and userId=:userId and type=:type order by time desc")
      .setLong("shopId", shopId).setLong("userId", userId).setParameter("type",RecentlyUsedDataType.VISITED_BUSINESS_CHANCE);
    if(limit!=null){
      query.setFirstResult(0).setMaxResults(limit);
    }
    return query;
  }

  public static Query getViewedBusinessChances(Session session,Long... preBuyOrderItemIds) {
    return session.createQuery("select r from RecentlyUsedData r where dataId in (:preBuyOrderItemIds)")
      .setParameterList("preBuyOrderItemIds",preBuyOrderItemIds);
  }

  public static Query getShopAuditLogDTOListByShopIdAndStatus(Session session, Long shopId, AuditStatus auditStatus) {
     return session.createQuery("from ShopAuditLog where shopId = :shopId and auditStatus = :auditStatus order by auditTime")
         .setLong("shopId", shopId).setParameter("auditStatus",auditStatus);
  }

  public static Query deleteJuheViolateRegulationCitySearchCondition(Session session) {
    return session.createQuery("delete from JuheViolateRegulationCitySearchCondition");
  }
  public static Query updateShopAgentProductStatus(Session session, Long shopId, DeletedType deletedType) {
    return session.createQuery("update ShopAgentProduct set deleted =:deleted where shopId=:shopId").setLong("shopId", shopId).setParameter("deleted",deletedType);
  }

  public static Query getInitShopServiceScope(Session session) {
    return session.createSQLQuery("select s.* from shop s left join shop_service_category ssc on s.id=ssc.shop_id where ssc.id is null group by s.id").addEntity(Shop.class);
  }


  public static Query getShopProvinceAreaNoGroupByAreaId(Session session) {
    return session.createSQLQuery("select s.province as areaId from shop s where s.shop_state=:state and s.shop_status in (:shopStatus) group by s.area_id")
        .addScalar("areaId", StandardBasicTypes.LONG)
        .setParameter("state",ShopState.ACTIVE.name())
        .setParameterList("shopStatus",ShopStatus.getShopTrialAndPaidString());
  }

  public static Query getShopCityAreaNoGroupByAreaId(Session session, Long provinceId) {
    return session.createSQLQuery("select s.city as areaId from shop s where s.shop_state=:state and s.shop_status in (:shopStatus) and s.province =:provinceId group by s.area_id")
        .addScalar("areaId", StandardBasicTypes.LONG)
        .setParameter("state", ShopState.ACTIVE.name())
        .setParameter("provinceId", provinceId)
        .setParameterList("shopStatus", ShopStatus.getShopTrialAndPaidString());
  }

  public static Query getJuheCityCodeByBaiduCityCode(Session session, Integer[] baiduCityCodes) {
    return session.createQuery("select juheCityCode from Area where cityCode in (:cityCode)")
        .setParameterList("cityCode", baiduCityCodes);
  }


  public static Query getAreaNoByJuheCityCode(Session session, String... juheCityCodes) {
    return session.createQuery("select no from Area where juheCityCode in (:juheCityCodes)")
        .setParameterList("juheCityCodes", juheCityCodes);
  }

  public static Query getJuheViolateRegulationCitySearchCondition(Session session, String[] juheCityCodes) {
    return session.createQuery("from JuheViolateRegulationCitySearchCondition where cityCode in (:cityCode)").setParameterList("cityCode", juheCityCodes);
  }

  public static Query getActiveJuheArea(Session session) {
    return session.createQuery("select cityCode from JuheViolateRegulationCitySearchCondition where status = :status")
        .setParameter("status", JuheStatus.ACTIVE);
  }

  public static Query getActiveJuheViolateRegulationCitySearchCondition(Session session) {
    return session.createQuery("from JuheViolateRegulationCitySearchCondition where status = :status")
        .setParameter("status", JuheStatus.ACTIVE);
  }

  public static Query getJuheCityOilPriceByFirstCarNo(Session session, String areaFirstCarNo) {
    return session.createQuery("from JuheCityOilPrice where areaFirstCarNo = :areaFirstCarNo")
          .setParameter("areaFirstCarNo",areaFirstCarNo);
  }

  public static Query getOperationLogByPager(Session session, int startPageNo, int pageSize) {
    return session.createQuery("from OperationLog").setFirstResult((startPageNo - 1) * pageSize).setMaxResults(pageSize);
  }

  public static Query updateSalesOrderOperationLogType(Session session, List<String> objectTypesList) {
      StringBuffer sb = new StringBuffer();
      sb.append("update operation_log ol,(select object_id,min(created) as min_date from operation_log where object_type in(:objectTypeList) group by object_id) ol2 set ol.operation_type='CREATE' where ol.created=ol2.min_date and ol.object_id=ol2.object_id");
      return session.createSQLQuery(sb.toString()).setParameterList("objectTypeList",objectTypesList);
  }

  public static Query updateRepairOrderOperationLogType(Session session) {
    StringBuffer sb = new StringBuffer();
    sb.append("update operation_log ol,(select object_id,min(created) as min_date from operation_log where object_type=:objectType and operation_type=:operationType group by object_id) ol2 set ol.operation_type='DEBT_SETTLE' where ol.created=ol2.min_date and ol.object_id=ol2.object_id");
    return session.createSQLQuery(sb.toString()).setString("objectType",ObjectTypes.REPAIR_ORDER.toString()).setString("operationType",OperationTypes.SETTLE.toString());
  }

  public static Query getAppUpdateAnnounce(Session session, AppPlatform appPlatform, String appVersion, AppUserType appUserType) {
    return session.createQuery("from AppUpdateAnnounce where appPlatform = :appPlatform and appVersion =:appVersion and appUserType =:appUserType ")
        .setParameter("appVersion", appVersion).setParameter("appPlatform", appPlatform).setParameter("appUserType", appUserType);
  }


  public static Query getShopByShopVersionAndArea(Session session, Long[] shopVersionId, Long province, Long city, Long region) {

    StringBuilder sb = new StringBuilder();
    sb.append("select id from Shop as s where 1=1  ");
    if (shopVersionId != null) {
      sb.append(" and s.shopVersionId in (:shopVersionId) ");
    }
    if (province != null) {
      sb.append(" and s.province = :province");
    }
    if (city != null) {
      sb.append(" and s.city = :city");
    }
    if (region != null) {
      sb.append(" and s.region = :region");
    }
    Query query = session.createQuery(sb.toString());
    if (shopVersionId != null) {
      query.setParameterList("shopVersionId", shopVersionId);
    }
    if (province != null) {
      query.setLong("province", province);
    }
    if (city != null) {
      query.setLong("city", city);
    }
    if (region != null) {
      query.setLong("region", region);
    }
    return query;


  }


  public static Query getVehicleViolateRegulationQueryRecord(Session session, String city, String vehicleNo,Long queryDate,String resultCode) {
    return session.createQuery("from VehicleViolateRegulationQueryRecord where city = :city and vehicleNo = :vehicleNo and queryDate >=:queryDate and resultCode=:resultCode ")
        .setString("city", city).setString("vehicleNo", vehicleNo).setString("resultCode", resultCode).setLong("queryDate", queryDate);
  }

  public static Query getVehicleViolateRegulationRecord(Session session, String city, String vehicleNo, Long recordDate) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("from VehicleViolateRegulationRecord where city = :city and vehicleNo = :vehicleNo ");
    if (recordDate != null) {
      stringBuffer.append("and recordDate >=:recordDate ");
    }
    Query query = session.createQuery(stringBuffer.toString()).setString("city", city).setString("vehicleNo", vehicleNo);
    if (recordDate != null) {
      query.setLong("recordDate", recordDate);
    }
    return query;
  }

  public static Query getJuheViolateRegulationCitySearchCondition(Session session, String cityCode, JuheStatus status) {
    return session.createQuery("from JuheViolateRegulationCitySearchCondition where cityCode =:cityCode and status = :status ").setParameter("status", JuheStatus.ACTIVE).setString("cityCode", cityCode);
  }

  public static Query getAreaLikeCityName(Session session, String cityName) {
    return session.createQuery("from Area where name like:cityName").setString("cityName", "%" + cityName + "%");
  }


  public static Query getObdOutStorageShopNameSuggestion(Session session, ShopNameSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("from Shop where shopKind in (:shopKinds) ");
     if(CollectionUtil.isNotEmpty(suggestion.getShopVersionIds())){
      sb.append(" and shopVersionId in(:shopVersionIds)");
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      sb.append(" and name like :queryWord ");
    }
    Query query = session.createQuery(sb.toString())
        .setParameterList("shopKinds", suggestion.getShopKinds())
        .setFirstResult(suggestion.getStart())
        .setMaxResults(suggestion.getLimit());
     if(CollectionUtil.isNotEmpty(suggestion.getShopVersionIds())){
      query.setParameterList("shopVersionIds", suggestion.getShopVersionIds());
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      query.setParameter("queryWord", new StringBuilder("%").append(suggestion.getQueryWord()).append("%").toString());
    }
    return query;
  }

  public static Query countObdSimMobileSuggestion(Session session, ShopNameSuggestion suggestion) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(id) from Shop where shopKind in (:shopKinds) ");
    if(CollectionUtil.isNotEmpty(suggestion.getShopVersionIds())){
      sb.append(" and shopVersionId in(:shopVersionIds)");
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      sb.append(" and name like :queryWord");
    }
    Query query = session.createQuery(sb.toString())
        .setParameterList("shopKinds", suggestion.getShopKinds());
    if(CollectionUtil.isNotEmpty(suggestion.getShopVersionIds())){
      query.setParameterList("shopVersionIds", suggestion.getShopVersionIds());
    }
    if (StringUtils.isNotBlank(suggestion.getQueryWord())) {
      query.setParameter("queryWord", new StringBuilder("%").append(suggestion.getQueryWord()).append("%").toString());
    }
    return query;
  }

  public static Query getShopAdAreaByShopId(Session session, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopAdArea where shopId =:shopId");
    Query query = session.createQuery(sb.toString()).setParameter("shopId", shopId);
    return query;
  }

  public static Query getAdShopIds(Session session,Integer size) {
    StringBuilder sb = new StringBuilder();
    sb.append("select s.id from Shop s where (s.productAdType='ALL' or s.productAdType='PART')");
    Query query = session.createQuery(sb.toString()).setFirstResult(0).setMaxResults(size);
    return query;
  }

  public static Query getRecommendTree(Session session) {
    StringBuilder sb = new StringBuilder();
    sb.append("from RecommendTree  where status =:status");
    Query query = session.createQuery(sb.toString()).setParameter("status",RecommendTreeStatus.ENABLED);
    return query;
  }

  public static Query getRecommendShopByShopId(Session session, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from RecommendShop  where status =:status and shopId =:shopId");
    Query query = session.createQuery(sb.toString())
        .setParameter("status",RecommendTreeStatus.ENABLED)
        .setParameter("shopId",shopId);
    return query;
  }

  public static Query getRecommendShopByShopArea(Session session,Long parentId,Long province,Long city,Long region) {
    StringBuilder sql = new StringBuilder("select distinct (s.id) as id,s.name as name,s.landline as landline,s.mobile as mobile,s.address as address from  shop s " +
      "left join shop_ad_area sa on sa.shop_id=s.id " +
      "join recommend_shop rs on rs.shop_id=s.id "+
      "where (s.product_ad_type='ALL' or (s.product_ad_type='PART' and sa.area_id in (:province,:city,:region))) and rs.recommend_id =:parentId");
    sql.append(" order by s.ad_price_per_month desc");
    return session.createSQLQuery(sql.toString())
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("landline", StandardBasicTypes.STRING)
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("address", StandardBasicTypes.STRING)
      .setParameter("province", province)
      .setParameter("city",city)
      .setParameter("region",region)
      .setParameter("parentId",parentId)
      .setResultTransformer(Transformers.aliasToBean(Shop.class));
  }

  public static Query countRecommendShopByShopArea(Session session,Long province,Long city,Long region,Long... parentIds) {
    StringBuilder sql = new StringBuilder("select rs.recommend_id as id, count(s.id) as shopNum,s.name as name,s.landline as landline,s.mobile as mobile,s.address as address from  shop s " +
      "left join shop_ad_area sa on sa.shop_id=s.id " +
      "join recommend_shop rs on rs.shop_id=s.id "+
      "where (s.product_ad_type='ALL' or (s.product_ad_type='PART' and sa.area_id in (:province,:city,:region))) and rs.recommend_id in(:parentIds) group by rs.recommend_id");
    return session.createSQLQuery(sql.toString())
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("shopNum", StandardBasicTypes.INTEGER)
      .setParameter("province", province)
      .setParameter("city",city)
      .setParameter("region",region)
      .setParameterList("parentIds",parentIds);
  }


  public static Query getRecommendShopByRecommendId(Session session, Long recommendId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from RecommendShop  where status =:status and recommendId =:recommendId");
    Query query = session.createQuery(sb.toString())
        .setParameter("status",RecommendTreeStatus.ENABLED)
        .setParameter("recommendId",recommendId);
    return query;
  }

  public static Query getRecommendTreeByRecommendIds(Session session, Set<Long> recommendIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from RecommendTree  where status =:status and id in(:recommendIds)");
    Query query = session.createQuery(sb.toString())
        .setParameter("status",RecommendTreeStatus.ENABLED)
        .setParameterList("recommendIds",recommendIds);
    return query;
  }

   public static Query getWXImageLib(Session session,String name) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXImageLib where name=:name");
    Query query  = session.createQuery(sb.toString())
        .setParameter("name",name);
    return query ;
  }

  public static Query countCameraDTO(Session session,String shopId) {
    StringBuilder sb = new StringBuilder();
    if(StringUtil.isNotEmpty(shopId)){
      sb.append("SELECT count(t.id) from (SELECT c.id,cs.shop_id FROM `camera` c  left join `camera_shop` cs  on  c.id= cs.camera_id and cs.deleted != 'TRUE' left join `shop` s on  cs.shop_id=s.id GROUP BY c.id ) as t where t.shop_id="+shopId);
    }else{
      sb.append("SELECT count(t.id) from (SELECT c.id FROM `camera` c  left join `camera_shop` cs  on  c.id= cs.camera_id and cs.deleted != 'TRUE' left join `shop` s on  cs.shop_id=s.id  GROUP BY c.id ) as t");
    }
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }


  public static Query getCameraDTOList(Session session,Pager pager,String shopId) {
    StringBuilder sb = new StringBuilder();
    if(StringUtil.isNotEmpty(shopId)){
      sb.append("SELECT t.id,t.serial_no,t.last_heart_date,t.lan_ip,t.lan_port,t.username,t.password, ");
      sb.append("t.domain_name,t.domain_username,t.domain_password,t.status,t.remark,t.external_address,t.name,t.install_date,t.shop_id FROM ( ");
    }
    sb.append("SELECT c.id,c.serial_no,c.last_heart_date,c.lan_ip,c.lan_port,c.username,c.password, ");
    sb.append("c.domain_name,c.domain_username,c.domain_password,c.status,c.remark,c.external_address,s.name,cs.install_date,cs.shop_id  ");
    sb.append("FROM camera c ");
    sb.append("left join camera_shop cs  on  c.id= cs.camera_id and cs.deleted != 'TRUE' ");
    sb.append("left join shop s on  cs.shop_id=s.id  GROUP BY c.id ");
    if(StringUtil.isNotEmpty(shopId)){
      sb.append(") t WHERE t.shop_id = "+shopId);
    }
    Query query = session.createSQLQuery(sb.toString()).setFirstResult(pager.getRowStart()) .setMaxResults(pager.getPageSize());
    return query;
  }


  public static Query getCameraShop(Session session,CameraDTO cameraDTO){
    StringBuilder sb = new StringBuilder();
//    sb.append("SELECT cs.id,cs.camera_id,cs.shop_id,cs.install_date,cs.status FROM camera_shop cs WHERE cs.camera_id = "+cameraDTO.getId());
    sb.append("FROM CameraShop  WHERE camera_id =:camera_id and deleted = :deleted");
    Query query  = session.createQuery(sb.toString())
        .setParameter("camera_id", NumberUtil.longValue(cameraDTO.getId()))
        .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getCamera(Session session,Camera camera){
    StringBuilder sb = new StringBuilder();
    sb.append("FROM Camera  WHERE serial_no =:serial_no ");
    Query query  = session.createQuery(sb.toString())
        .setParameter("serial_no", camera.getSerial_no());
    return query;
  }

  public static Query getCameraBySerialNo(Session session,String serialNo){
     StringBuilder sb = new StringBuilder();
     sb.append("FROM Camera  WHERE serial_no =:serial_no ");
     Query query  = session.createQuery(sb.toString())
         .setParameter("serial_no",serialNo);
     return query;
   }


  public static Query countCameraRecordDTO(Session session,String id) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT count(t.id) from (SELECT c.id FROM camera_record c WHERE c.camera_id = "+id+") as t");
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }

  public static Query getCameraRecordDTOList(Session session,Pager pager,String id) {
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT c.id,c.camera_id,c.vehicle_no,c.arrive_date,c.ref_order_type,c.order_id,c.name ");
    sb.append("FROM camera_record c ");
    sb.append("WHERE c.camera_id = "+id+" GROUP BY c.id");
    Query query = session.createSQLQuery(sb.toString()).setFirstResult(pager.getRowStart()) .setMaxResults(pager.getPageSize());
    return query;
  }

  public static Query getCameraConfig(Session session, String name, Long shopId) {
    StringBuilder sb = new StringBuilder();
    if (shopId != null) {
      sb.append("from CameraConfig where name=:name and shop_id = :shop_id");
      Query query  = session.createQuery(sb.toString()).setParameter("name", name).setLong("shop_id", shopId);
      return query;
    } else {
      sb.append("from CameraConfig where name=:name");
      Query query  = session.createQuery(sb.toString()).setParameter("name", name);
      return query;
    }
  }

  public static Query getShopNameByCameraId(Session session, Long id){
    StringBuilder sb = new StringBuilder();
    sb.append("SELECT s.name  FROM camera_shop cs ");
    sb.append("left join shop s on s.id =cs.shop_id  ");
    sb.append("where cs.camera_id = "+id+" and cs.deleted = 'FALSE' ");
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }

  public static Query getCameraRecordByVehicle_no(Session session, String vehicle_no){
    StringBuilder sb = new StringBuilder();
    sb.append("FROM CameraRecord  WHERE vehicle_no =:vehicle_no ");
    Query query  = session.createQuery(sb.toString())
        .setParameter("vehicle_no", vehicle_no);
    return query;
  }

  public static Query getCameraConfigByCameraId(Session session, String camera_id){
    StringBuilder sb = new StringBuilder();
    sb.append("FROM CameraConfig  WHERE camera_id =:camera_id ");
    Query query  = session.createQuery(sb.toString())
        .setParameter("camera_id", NumberUtil.longValue(camera_id));
    return query;
  }

  public static Query getCameraRecordByShopId(Pager pager,Session session, CameraSearchCondition condition){
    StringBuilder sb = new StringBuilder();
    sb.append("FROM CameraRecord  WHERE shop_id =:shop_id ");
    if(StringUtil.isNotEmpty(condition.getVehicle_nos())){
      sb.append(" AND vehicle_no = '"+condition.getVehicle_nos()+"'");
    }
    if (condition.getStartDate() != null) {
      sb.append(" AND arrive_date >=:startTime  ");
    }
    if (condition.getEndDate() != null) {
      sb.append(" AND arrive_date <=:endTime ");
    }
    sb.append("ORDER BY arrive_date DESC ");
    Query query  = session.createQuery(sb.toString()).setFirstResult(pager.getRowStart()) .setMaxResults(pager.getPageSize())
        .setParameter("shop_id", NumberUtil.longValue(condition.getShopId()));
    if (condition.getStartDate() != null) {
      query.setParameter("startTime", condition.getStartDate());
    }
    if (condition.getEndDate() != null) {
      query.setParameter("endTime", condition.getEndDate());
    }
    return query;
  }

  public static Query countCameraRecordDTOByShopId(Session session,CameraSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    StringBuilder sbs = new StringBuilder();
      sb.append("SELECT c.id FROM camera_record c WHERE c.shop_id ="+condition.getShopId());
    if (StringUtil.isNotEmpty(condition.getVehicle_nos())) {
      sb.append(" and c.vehicle_no = '"+condition.getVehicle_nos()+"'");
    }
    if (condition.getStartDate() != null) {
      sb.append(" and c.arrive_date >= "+condition.getStartDate());
    }
    if (condition.getEndDate() != null) {
      sb.append(" and c.arrive_date <= "+condition.getEndDate());
    }
    sbs.append("SELECT count(t.id) from ("+ sb.toString() +") as t");
    Query query = session.createSQLQuery(sbs.toString());
    return query;
  }

  public static Query getVehicle_nos(Session session,String vehicle_nos){
    StringBuilder sb = new StringBuilder();
    sb.append("select DISTINCT cr.vehicle_no from camera_record cr  ");
    if(StringUtil.isNotEmpty(vehicle_nos)){
      sb.append("where cr.vehicle_no like  "+"'%"+vehicle_nos+"%'");
    }
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }

  public static Query getCameraConfigByShopId(Session session, String shopId){
    StringBuilder sb = new StringBuilder();
    sb.append("select cc.id,cc.camera_id,cc.interval_time_warn,cc.member_card,cc.order_type,cc.white_vehicle_nos,c.serial_no,cc.construction_project_text,cc.construction_project_value " +
        "from camera_config cc left join camera_shop cs on cc.camera_id= cs.camera_id left join camera c on c.id = cc.camera_id WHERE cs.shop_id = "+shopId);
//    sb.append("FROM CameraConfig cc left join CameraShop cs on cc.camera_id= cs.camera_id  WHERE cs.shop_id =:shop_id ");
    Query query  = session.createSQLQuery(sb.toString());
    return query;
  }

  public static Query getCountCameraConfigByShopId(Session session,String shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(t.id) from ( ");
    sb.append("select cc.id,cc.camera_id,cc.interval_time_warn,cc.member_card,cc.order_type,cc.white_vehicle_nos,c.serial_no,cc.construction_project_text " +
        "from camera_config cc left join camera_shop cs on cc.camera_id= cs.camera_id left join camera c on c.id = cc.camera_id WHERE cs.shop_id = "+shopId);
    sb.append(" ) as t") ;
    Query query = session.createSQLQuery(sb.toString());
    return query;
  }


  public static Query getJuheViolateRegulationCitySearchConditionByCityName(Session session, String cityName) {
    StringBuilder sb = new StringBuilder();
    sb.append("from JuheViolateRegulationCitySearchCondition where cityName =:cityName ");
    Query query = session.createQuery(sb.toString())
        .setParameter("cityName", cityName);
    return query;

  }

  public static Query findStationByMncAndLacAndCi(Session session,Map map){
    StringBuilder sb = new StringBuilder();
    sb.append("from BaseStation b where  b.mnc =:mnc and b.lac =:lac and b.ci =:ci");
    Query query = session.createQuery(sb.toString())
                        .setParameter("mnc", map.get("mnc"))
                        .setParameter("lac", map.get("lac"))
                        .setParameter("ci", map.get("ci"));
    return query;
  }

  public static Query getShopsByGPS (Session session ,double minlat , double maxlat ,double minlon , double maxlon){
    StringBuilder sb = new StringBuilder();
    sb.append("select id , name from shop where coordinate_lat >:minlat and coordinate_lat <:maxlat and coordinate_lon >:minlon and coordinate_lon <:maxlon ORDER BY id asc LIMIT 0,2");
    Query query = session.createSQLQuery(sb.toString())
                        .addScalar("id" , StandardBasicTypes.LONG)
                        .addScalar("name" , StandardBasicTypes.STRING)
                        .setParameter("minlat", minlat)
                        .setParameter("maxlat" , maxlat)
                        .setParameter("minlon" , minlon)
                        .setParameter("maxlon" , maxlon);
    return query;
  }

}

