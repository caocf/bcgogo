package com.bcgogo.user.model;

import com.bcgogo.api.GsmVehicleDataCondition;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.user.model.wx.*;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.*;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeSearchCondition;
import com.bcgogo.wx.user.WXMsgSearchCondition;
import com.bcgogo.wx.user.WXUserDTO;
import com.bcgogo.wx.user.WXUserSearchCondition;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import java.util.Set;

/**
 * 同sql，sql文件过大，使用新sql文件
 * Author: ndong
 * Date: 14-10-17
 * Time: 下午3:08
 */
public class UserSQL {


  public static Query getWXUserByOpenId(Session session, String... openIds) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXUser where openId in(:openIds) and deleted=:deleted");
    Query query = session.createQuery(sb.toString())
      .setParameterList("openIds", openIds)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getWXUserByPager(Session session, WXAccountType accountType, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u from wx_user u join wx_account a on u.public_no=a.public_on where u.deleted=:deleted");
    if (accountType != null) {
      sb.append(" and a.account_type=:accountType");
    }
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXUser.class)
      .setParameter("deleted", DeletedType.FALSE)
      .setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    if (accountType != null) {
      query.setParameter("accountType", accountType);
    }
    return query;
  }


  public static Query countWXUser(Session session, WXAccountType accountType) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(u) from wx_user u join wx_account a on u.public_no=a.public_on where u.deleted=:deleted");
    if (accountType != null) {
      sb.append(" and a.account_type=:accountType");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (accountType != null) {
      query.setParameter("accountType", accountType);
    }
    return query;
  }

  public static Query getAllWXAccount(Session session) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXAccount where deleted=:deleted");
    return session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
  }


  public static Query getWXShopAccount(Session session, Long shopId, Long accountId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXShopAccount where deleted=:deleted");
    if (shopId != null) {
      sb.append(" and shopId=:shopId");
    }
    if (accountId != null) {
      sb.append(" and accountId=:accountId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (shopId != null) {
      query.setParameter("shopId", shopId);

    }
    if (accountId != null) {
      query.setParameter("accountId", accountId);
    }
    return query;
  }

  public static Query getWXAccountByPublicNo(Session session, String publicNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXAccount where publicNo=:publicNo and deleted=:deleted");
    return session.createQuery(sb.toString())
      .setParameter("publicNo", publicNo)
      .setParameter("deleted", DeletedType.FALSE)
      ;
  }

  public static Query getImpactVideoDTOByAppUserNo(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ImpactVideo where appUserNo=:appUserNo and deleted=:deleted");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("deleted", DeletedType.FALSE)
      ;
  }

  public static Query statImpactVideo(Session session, String appUserNo, Long startTime, Long endTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(v) from ImpactVideo v where appUserNo=:appUserNo and uploadTime>:startTime and uploadTime<:endTime and deleted=:deleted");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      .setParameter("startTime", startTime)
      .setParameter("endTime", endTime)
      .setParameter("deleted", DeletedType.FALSE)
      ;
  }

  public static Query getImpactVideoDTOByUUID(Session session, String uuid) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ImpactVideo where uuid=:uuid and deleted=:deleted");
    return session.createQuery(sb.toString())
      .setParameter("uuid", uuid)
      .setParameter("deleted", DeletedType.FALSE)
      ;
  }

  public static Query getWXAccountByOpenId(Session session, String openId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a.* from wx_account a join wx_user u on a.public_no=u.public_no where a.deleted='FALSE' and u.deleted='FALSE'" +
      " and u.open_id=:openId");
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXAccount.class)
      .setParameter("openId", openId);
    return query;
  }


  public static Query countWXAccount(Session session, WXShopAccountSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(a) from WXAccount a where deleted=:deleted");
    if (condition.getAccountType() != null) {
      sb.append(" and accountType=:accountType");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    ;
    if (condition.getAccountType() != null) {
      query.setParameter("accountType", condition.getAccountType());
    }
    return query;
  }

  public static Query getWXAccount(Session session, WXShopAccountSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXAccount where deleted=:deleted");
    if (condition.getAccountType() != null) {
      sb.append(" and accountType=:accountType");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (condition.getAccountType() != null) {
      query.setParameter("accountType", condition.getAccountType());
    }
//    if(condition.getPager()!=null){
//      query.setFirstResult(condition.getPager().getRowStart())
//        .setMaxResults(condition.getPager().getPageSize());
//    }
    return query;
  }

  public static Query countWXShopAccount(Session session, WXShopAccountSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(*) from WXShopAccount a  where a.deleted='FALSE'");
    if (condition.getShopId() != null) {
      sb.append(" and a.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (condition.getShopId() != null) {
      query.setParameter("shopId", condition.getShopId());
    }
    return query;
  }

  public static Query getWXShopAccount(Session session, WXShopAccountSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXShopAccount where deleted=:deleted");
    if (condition.getShopId() != null) {
      sb.append(" and shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (condition.getShopId() != null) {
      query.setParameter("shopId", condition.getShopId());
    }
    if (condition.getPager() != null) {
      query.setFirstResult(condition.getPager().getRowStart())
        .setMaxResults(condition.getPager().getPageSize());
    }
    return query;
  }


  public static Query getWXAccountByCondition(Session session, WXShopAccountSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a from WXAccount a,WXShopAccount sa where a.deleted='FALSE' and sa.deleted='FALSE'" +
      " and a.id=sa.accountId");
    if (condition.getShopId() != null) {
      sb.append(" and sa.shopId=:shopId");
    }
    Query query = session.createQuery(sb.toString());
    if (condition.getShopId() != null) {
      query.setParameter("shopId", condition.getShopId());
    }
    return query;
  }


  public static Query getShopWXUserByOpenId(Session session, String openId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopWXUser su  where su.openId=:openId and su.deleted=:deleted");
    Query query = session.createQuery(sb.toString())
      .setParameter("openId", openId)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getWXKWTemplate(Session session, String publicNo, String title) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXKWTemplate t  where t.publicNo=:publicNo and t.title=:title and t.deleted=:deleted");
    Query query = session.createQuery(sb.toString())
      .setParameter("publicNo", publicNo)
      .setParameter("title", title)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getShopWXUserByShopId(Session session, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopWXUser su  where su.shopId=:shopId and su.deleted=:deleted");
    Query query = session.createQuery(sb.toString())
      .setParameter("shopId", shopId)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getShopWXUser(Session session, Long shopId, String openId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from ShopWXUser su  where su.shopId=:shopId and su.openId=:openId and su.deleted=:deleted");
    Query query = session.createQuery(sb.toString())
      .setParameter("shopId", shopId)
      .setParameter("openId", openId)
      .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

  public static Query getShopWXUserInfo(Session session, String openId, String publicNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u,s from ShopWXUser su  join WXUser u on su.userId=u.id and su.openId=:openId and su.publicNo=:publicNo");
    Query query = session.createQuery(sb.toString())
      .setParameter("openId", openId).setParameter("publicNo", publicNo);
    return query;
  }

  public static Query getWXQRCode(Session session, WXQRCodeSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXQRCode where deleted='FALSE'");
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      sb.append(" and publicNo=:publicNo");
    }
    if (condition.getSceneId() != null) {
      sb.append(" and sceneId=:sceneId");
    }
    if (condition.getShopId() != null) {
      sb.append(" and shopId=:shopId");
    }
    if (condition.getScene() != null) {
      sb.append(" and scene=:scene");
    }
    Query query = session.createQuery(sb.toString());
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      query.setParameter("publicNo", condition.getPublicNo());
    }
    if (condition.getSceneId() != null) {
      query.setParameter("sceneId", condition.getSceneId());
    }
    if (condition.getShopId() != null) {
      query.setParameter("shopId", condition.getShopId());
    }
    if (condition.getScene() != null) {
      query.setParameter("scene", condition.getScene());
    }
    return query;
  }


  public static Query getWXQRCodeDTOByShopId(Session session, String publicNo, Long shopId, QRScene scene) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXQRCode where publicNo=:publicNo and shopId=:shopId and scene=:scene");
    Query query = session.createQuery(sb.toString())
      .setParameter("publicNo", publicNo)
      .setParameter("shopId", shopId)
      .setParameter("scene", scene);
    return query;
  }

  public static Query getUnExpireWXQRCodeDTOByShopId(Session session, Long shopId, QRScene scene) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXQRCode  where shopId=:shopId and scene=:scene and deleted=:deleted and expire_time >:expire_time");
    Query query = session.createQuery(sb.toString())
      .setParameter("shopId", shopId)
      .setParameter("scene", scene)
      .setParameter("deleted", DeletedType.FALSE)
      .setLong("expire_time", System.currentTimeMillis() + DateUtil.HOUR_MILLION_SECONDS);
    return query;
  }

  public static Query getUnExpireWXQRCode(Session session, String publicNo, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("select q.* from wx_qr_code q join app_user_wx_qr_code a on q.id=a.qr_code_id " +
      "where  a.deleted =:deleted " +
      "and q.expire_time >:expire_time and q.scene =:scene and q.deleted =:deleted");
    if (StringUtil.isNotEmpty(publicNo)) {
      sb.append(" and a.public_no=:public_no");
    }
    if (StringUtil.isNotEmpty(appUserNo)) {
      sb.append(" and a.app_user_no=:app_user_no");
    }
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXQRCode.class)
      .setString("deleted", DeletedType.FALSE.toString())
      .setLong("expire_time", System.currentTimeMillis() + DateUtil.HOUR_MILLION_SECONDS)
      .setString("scene", QRScene.MIRROR_USER.toString())
      .setString("deleted", DeletedType.FALSE.toString());
    if (StringUtil.isNotEmpty(publicNo)) {
      query.setString("public_no", publicNo);
    }
    if (StringUtil.isNotEmpty(appUserNo)) {
      query.setString("app_user_no", appUserNo);
    }
    return query;
  }


  public static Query getAppUserWXQRCodeDTOBySceneId(Session session, Long sceneId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a.* from app_user_wx_qr_code a  join wx_qr_code q on q.id=a.qr_code_id " +
      "where q.scene_id=:scene_id and q.scene=:scene and q.deleted ='FALSE' " +
      "and a.deleted ='FALSE'");
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(AppUserWXQRCode.class)
      .setLong("scene_id", sceneId)
      .setString("scene", QRScene.MIRROR_USER.toString());
    return query;
  }

  public static Query getAppUserWXQRCode(Session session, String publicNo, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppUserWXQRCode where deleted=:deleted");
    if (StringUtil.isNotEmpty(publicNo)) {
      sb.append(" and publicNo=:publicNo");
    }
    if (StringUtil.isNotEmpty(appUserNo)) {
      sb.append(" and appUserNo=:appUserNo");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (StringUtil.isNotEmpty(publicNo)) {
      query.setParameter("publicNo", publicNo);
    }
    if (StringUtil.isNotEmpty(appUserNo)) {
      query.setParameter("appUserNo", appUserNo);
    }
    return query;
  }

  public static Query getAccidentSpecialistByOpenId(Session session, Long shopId, String openId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AccidentSpecialist where deleted=:deleted");
    if (shopId != null) {
      sb.append(" and shopId =:shopId");
    }
    if (openId != null) {
      sb.append(" and openId=:openId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (shopId != null) {
      query.setParameter("shopId", shopId);
    }
    if (openId != null) {
      query.setParameter("openId", openId);
    }
    return query;
  }

  public static Query getAppWXUser(Session session, String appUserNo, String openId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppWXUser where deleted=:deleted");
    if (StringUtil.isNotEmpty(appUserNo)) {
      sb.append(" and appUserNo=:appUserNo");
    }
    if (StringUtil.isNotEmpty(openId)) {
      sb.append(" and openId=:openId");
    }
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE);
    if (StringUtil.isNotEmpty(appUserNo)) {
      query.setParameter("appUserNo", appUserNo);
    }
    if (StringUtil.isNotEmpty(openId)) {
      query.setParameter("openId", openId);
    }

    return query;
  }

  public static Query getUnAssignedWXQRCode(Session session, String publicNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXQRCode where publicNo=:publicNo and scene=:scene and shopId is null and sceneId>:reservedNum");
    Query query = session.createQuery(sb.toString())
      .setParameter("publicNo", publicNo)
      .setParameter("scene", QRScene.SHOP_USER)
      .setInteger("reservedNum", WXConstant.QR_CODE_SCENE_RESERVED)
      .setMaxResults(1);
    return query;
  }

  public static Query getWXQRCodeMaxScene(Session session, String publicNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("select max(sceneId) from WXQRCode where publicNo=:publicNo");
    return session.createQuery(sb.toString())
      .setParameter("publicNo", publicNo)
      ;
  }

  public static Query getWXUserVehicle(Session session, String openId, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXUserVehicle uv where uv.deleted=:deleted");
    if (StringUtil.isNotEmpty(openId)) {
      sb.append(" and uv.openId=:openId");
    }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      sb.append(" and uv.vehicleNo=:vehicleNo");
    }
    Query query = session.createQuery(sb.toString()).setParameter("deleted", DeletedType.FALSE);
    if (StringUtil.isNotEmpty(openId)) {
      query.setParameter("openId", openId);
    }
    if (StringUtil.isNotEmpty(vehicleNo)) {
      query.setParameter("vehicleNo", vehicleNo);
    }
    return query;
  }

  public static Query getWXUserVehicleByOpenId(Session session, String... openId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXUserVehicle uv where uv.deleted=:deleted and uv.openId in(:openId)");
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE)
      .setParameterList("openId", openId);
    return query;
  }

  public static Query getWXUserVehicleById(Session session, Long userVehicleId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXUserVehicle uv where uv.deleted=:deleted and uv.id=:userVehicleId");
    Query query = session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE)
      .setParameter("userVehicleId", userVehicleId);
    return query;
  }

  public static Query getWXUserByVehicleNo(Session session, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from wx_user u join wx_user_vehicle uv on u.open_id=uv.open_id and u.deleted='FALSE' and uv.deleted='FALSE' where uv.vehicle_no =:vehicleNo");
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXUser.class)
      .setParameter("vehicleNo", vehicleNo);
    return query;
  }

  public static Query getWXUserByShopId(Session session, Long shopId) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from wx_user u join shop_wx_user su on u.open_id=su.open_id and u.deleted='FALSE' and su.deleted='FALSE' where su.shop_id=:shopId");
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXUser.class)
      .setParameter("shopId", shopId);
    return query;
  }

  public static Query getWXUserDTOByPublicNo(Session session, String publicNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXUser u where u.deleted='FALSE'and u.publicNo=:publicNo");
    Query query = session.createQuery(sb.toString())
      .setParameter("publicNo", publicNo);
    return query;
  }


  public static Query getLastEvaluateRecordDTOByVehicleNo(Session session, String vehicleNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from EvaluateRecord where vehicleNo=:vehicleNo and deleted=:deleted order by evalDate desc");
    Query query = session.createQuery(sb.toString())
      .setParameter("vehicleNo", vehicleNo)
      .setParameter("deleted", DeletedType.FALSE)
      .setMaxResults(1);
    ;
    return query;
  }


  public static Query getWxUsersBySearchCondition(Session session, WXUserSearchCondition condition, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from wx_user u left join shop_wx_user su on su.open_id = u.open_id where su.deleted ='FALSE' and u.deleted ='FALSE'");
    if (condition.getShopId() != null) {
      sb.append(" and su.shop_id =:shop_id");
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      sb.append(" and u.public_no=:public_no");
    }
    if (StringUtil.isNotEmpty(condition.getNickName())) {
      sb.append(" and u.nick_name like:nick_name ");
    }
    if (StringUtil.isNotEmpty(condition.getRemark())) {
      sb.append(" and u.remark like:remark ");
    }
    sb.append(" order by u.subscribe_time desc");
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXUser.class);
    if (condition.getShopId() != null) {
      query.setParameter("shop_id", condition.getShopId());
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      query.setParameter("public_no", condition.getPublicNo());
    }
    if (StringUtil.isNotEmpty(condition.getNickName())) {
      query.setString("nick_name", "%" + condition.getNickName() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getRemark())) {
      query.setString("remark", "%" + condition.getRemark() + "%");
    }
    if (pager != null) {
      query.setFirstResult(pager.getRowStart());
      query.setMaxResults(pager.getPageSize());
    }
    return query;
  }

  public static Query countWxUsersBySearchCondition(Session session, WXUserSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(u.id) from wx_user u left join shop_wx_user su on su.open_id = u.open_id where su.deleted ='FALSE' and u.deleted ='FALSE'");
    if (condition.getShopId() != null) {
      sb.append(" and su.shop_id =:shop_id");
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      sb.append(" and u.public_no=:public_no ");
    }
    if (StringUtil.isNotEmpty(condition.getNickName())) {
      sb.append(" and u.nick_name like:nick_name ");
    }
    if (StringUtil.isNotEmpty(condition.getRemark())) {
      sb.append(" and u.remark like:remark ");
    }
    Query query = session.createSQLQuery(sb.toString());
    if (condition.getShopId() != null) {
      query.setParameter("shop_id", condition.getShopId());
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      query.setParameter("public_no", condition.getPublicNo());
    }
    if (StringUtil.isNotEmpty(condition.getNickName())) {
      query.setString("nick_name", "%" + condition.getNickName() + "%");
    }
    if (StringUtil.isNotEmpty(condition.getRemark())) {
      query.setString("remark", "%" + condition.getRemark() + "%");
    }
    return query;
  }

  public static Query countMyFans(Session session, WXUserSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(u.id) from wx_user u left join shop_wx_user su on su.open_id = u.open_id where (su.deleted ='FALSE' and u.deleted ='FALSE' ");
    if (condition.getShopId() != null) {
      sb.append(" and su.shop_id =:shop_id");
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      sb.append(" and u.public_no=:public_no ");
    }
    sb.append(")");
    if (StringUtil.isNotEmpty(condition.getKeyWord())) {
      sb.append(" and (u.nick_name like:keyWord or u.remark like:keyWord)");
    }
    Query query = session.createSQLQuery(sb.toString());
    if (condition.getShopId() != null) {
      query.setParameter("shop_id", condition.getShopId());
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      query.setParameter("public_no", condition.getPublicNo());
    }
    if (StringUtil.isNotEmpty(condition.getKeyWord())) {
      query.setString("keyWord", "%" + condition.getKeyWord() + "%");
    }
    return query;
  }


  public static Query getMyFans(Session session, WXUserSearchCondition condition, Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("select u.* from wx_user u left join shop_wx_user su on su.open_id = u.open_id where ( su.deleted ='FALSE' and u.deleted ='FALSE'");
    if (condition.getShopId() != null) {
      sb.append(" and su.shop_id =:shop_id");
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      sb.append(" and u.public_no=:public_no");
    }
    sb.append(")");
    if (StringUtil.isNotEmpty(condition.getKeyWord())) {
      sb.append(" and (u.nick_name like:keyWord or u.remark like:keyWord)");
    }
    sb.append(" order by u.subscribe_time desc");
    Query query = session.createSQLQuery(sb.toString())
      .addEntity(WXUser.class);
    if (condition.getShopId() != null) {
      query.setParameter("shop_id", condition.getShopId());
    }
    if (StringUtil.isNotEmpty(condition.getPublicNo())) {
      query.setParameter("public_no", condition.getPublicNo());
    }
    if (StringUtil.isNotEmpty(condition.getKeyWord())) {
      query.setString("keyWord", "%" + condition.getKeyWord() + "%");
    }
    if (pager != null) {
      query.setFirstResult(pager.getRowStart());
      query.setMaxResults(pager.getPageSize());
    }
    return query;
  }

  public static Query getProbableUserDTOByFinger(Session session, String finger) {
    Query query = session.createQuery("from UserClientInfo u where u.finger =: finger and score >= 100")
      .setParameter("finger", finger);
    return query;
  }

  public static Query getWXAccountStat(Session session) {
    Query query = session.createSQLQuery("SELECT a.id as id, a. NAME as name,count(*) AS userNum " +
      "FROM wx_user u JOIN wx_account a ON u.public_no = a.public_no " +
      "WHERE u.deleted = 'FALSE'AND u.deleted = 'FALSE' ")
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("userNum", StandardBasicTypes.INTEGER);
    return query;
  }

  public static Query getWXUserGrowth(Session session) {
    Long last7Day = System.currentTimeMillis() / 1000 - 7 * 24 * 60 * 60;
    Query query = session.createSQLQuery("SELECT a.id as id ,count(*) AS cnt FROM wx_user u JOIN wx_account a ON u.public_no = a.public_no " +
      "WHERE u.deleted = 'FALSE' AND u.deleted = 'FALSE' AND u.subscribe_time > " + last7Day)
      .addScalar("id", StandardBasicTypes.LONG)
      .addScalar("cnt", StandardBasicTypes.INTEGER);
    return query;
  }


  public static Query getWXFanDTOByLicence_no(Session session, String licence_no) {
    StringBuffer hql = new StringBuffer();
    hql.append("select v.licenceNo,v.model,v.brand,c.name,c.mobile,v.id,c.id from Vehicle v,Customer c,CustomerVehicle cv " +
      "where  cv.vehicleId=v.id and cv.customerId= c.id and v.licenceNo= '" + licence_no + "'");
    Query query = session.createQuery(hql.toString());
    return query;
  }

  public static Query getShopWxUserVehicleInfo(Session session, Long shopId, Set<String> vehicleNos) {
    StringBuilder sb = new StringBuilder();
    sb.append("select v.licence_no,v.model,v.brand,c.name,c.mobile,v.id as vehicleId,c.id as customerId from vehicle v ");
    sb.append("left join customer_vehicle cv on v.id = cv.vehicle_id ");
    sb.append("left join customer c on cv.customer_id = c.id ");
    sb.append("where v.shop_id =:shopId and v.licence_no in (:vehicleNos) ");
    sb.append("and (v.status is null or v.status = 'ENABLED') ");
    sb.append("and (cv.status is null or cv.status = 'ENABLED') ");
    sb.append("and (c.status is null or c.status = 'ENABLED') ");

    Query query = session.createSQLQuery(sb.toString())
      .addScalar("licence_no", StandardBasicTypes.STRING)
      .addScalar("model", StandardBasicTypes.STRING)
      .addScalar("brand", StandardBasicTypes.STRING)
      .addScalar("name", StandardBasicTypes.STRING)
      .addScalar("mobile", StandardBasicTypes.STRING)
      .addScalar("vehicleId", StandardBasicTypes.LONG)
      .addScalar("customerId", StandardBasicTypes.LONG)
      .setParameterList("vehicleNos", vehicleNos)
      .setParameter("shopId", shopId);
    return query;
  }

  public static Query getAppUserCustomerByAppUserNo(Session session, String appUserNo) {
    StringBuilder sb = new StringBuilder();
    sb.append("from AppUserCustomer where appUserNo=:appUserNo");
    return session.createQuery(sb.toString())
      .setParameter("appUserNo", appUserNo)
      ;
  }


  public static Query getGsmVehicleData(Session session, GsmVehicleDataCondition condition) {
    StringBuilder sb = new StringBuilder("from GsmVehicleData where 1=1");
    if (StringUtil.isNotEmpty(condition.getUuid())) {
      sb.append(" and uuid=:uuid");
    }
    if (condition.getGsmVehicleStatus() != null) {
      sb.append(" and gsmVehicleStatus=:gsmVehicleStatus");
    }
    if (StringUtil.isNotEmpty(condition.getVehicleStatus())) {
      sb.append(" and vehicleStatus=:vehicleStatus");
    }
    Query query = session.createQuery(sb.toString());
    if (StringUtil.isNotEmpty(condition.getUuid())) {
      query.setParameter("uuid", condition.getUuid());
    }
    if (condition.getGsmVehicleStatus() != null) {
      query.setParameter("gsmVehicleStatus", condition.getGsmVehicleStatus());
    }
    if (StringUtil.isNotEmpty(condition.getVehicleStatus())) {
      query.setParameter("vehicleStatus", condition.getVehicleStatus());
    }
    return query;
  }

//  public static Query getGsmVehicleDataByGpsCityStatus(Session session) {
//    StringBuilder sb = new StringBuilder("from GsmVehicleData where 1=1");
//    sb.append(" and gpsCityStatus=:gpsCityStatus");
//    Query query = session.createQuery(sb.toString());
//    query.setParameter("gpsCityStatus", GsmVehicleStatus.UN_HANDLE);
//    return query;
//  }
//
//
//  public static Query getGsmVehicleData(Session session, String appUserNo, String uuid, String status) {
//    StringBuilder sb = new StringBuilder();
//    sb.append("from GsmVehicleData where appUserNo=:appUserNo and uuid=:uuid and vehicleStatus=:vehicleStatus ");
//    return session.createQuery(sb.toString())
//      .setParameter("appUserNo", appUserNo).setParameter("uuid", uuid).setParameter("vehicleStatus", status)
//      ;
//  }
//
//  public static Query getGsmVehicleData_all(Session session, String appUserNo, Long startTime, Long endTime) {
//    StringBuilder sb = new StringBuilder();
//    sb.append("from GsmVehicleData where appUserNo =:appUserNo ");
//    if (startTime != null) {
//      sb.append(" and uploadServerTime >=:startTime ");
//    }
//    if (endTime != null) {
//      sb.append(" and uploadServerTime <=:endTime ");
//    }
//    sb.append(" order by uploadServerTime asc");
//    Query query = session.createQuery(sb.toString())
//      .setParameter("appUserNo", appUserNo);
//    if (startTime != null) {
//      query.setParameter("startTime", startTime);
//    }
//    if (endTime != null) {
//      query.setParameter("endTime", endTime);
//    }
//    return query;
//  }
//
//
//  public static Query getLastGsmVehicleData(Session session, String appUserNo) {
//    StringBuilder sb = new StringBuilder();
//    sb.append("from GsmVehicleData where appUserNo =:appUserNo ");
//    sb.append(" order by uploadTime desc");
//    Query query = session.createQuery(sb.toString())
//      .setParameter("appUserNo", appUserNo)
//      .setMaxResults(1);
//    return query;
//  }
//
//  public static Query getIllegalCityByAppUserNo(Session session, String appUserNo) {
//    StringBuilder sb = new StringBuilder();
//    sb.append("from IllegalCity where appUserNo =:appUserNo ");
//    Query query = session.createQuery(sb.toString())
//      .setParameter("appUserNo", appUserNo);
//    return query;
//  }

}
