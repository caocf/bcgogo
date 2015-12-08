package com.bcgogo.notification.model;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSendNecessaryType;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.notification.InvitationCodeStatus;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.StatStatus;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.dto.SmsSearchCondition;
import com.bcgogo.notification.reminder.ReminderType;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.FestivalDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXMsgStatus;
import com.bcgogo.wx.WXShopAccountSearchCondition;
import com.bcgogo.wx.user.WXMsgSearchCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

public class SQL {

  public static Query getSmsJobsByStartTime(Session session, long startTime, SmsChannel smsChannel, boolean isGroupBy, Integer limit) {
    return session.createQuery("select s from SmsJob s where s.startTime < :startTime and s.smsChannel=:smsChannel " + (isGroupBy ? " group by s.smsId" : ""))
        .setLong("startTime", startTime).setMaxResults(limit).setParameter("smsChannel", smsChannel);
  }

  public static Query getSmsJobsByShopId(Session session, long shopId, int pageNo, int pageSize) {
    return session.createQuery("select s from SmsJob s where s.shopId = :shopId").setLong("shopId", shopId).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query getShopInBoxs(Session session, long shopId, int pageNo, int pageSize) {
    return session.createQuery("select ib from InBox as ib where ib.receiveShopId = :shopId")
        .setLong("shopId", shopId)
        .setFirstResult(pageNo * pageSize)
        .setMaxResults(pageSize);
  }

  public static Query getShopOutBoxs(Session session, long shopId, int pageNo, int pageSize) {
    return session.createQuery("from OutBox ob WHERE ob.shopId = :shopId ORDER BY ob.creationDate DESC ")
        .setLong("shopId", shopId)
        .setFirstResult(pageNo * pageSize)
        .setMaxResults(pageSize);
  }

  public static Query getOutBoxByShopAndMobile(Session session, long shopId, String sendMobile, int pageNo, int pageSize) {
    return session.createQuery("select ob from OutBox as ob where ob.shopId = :shopId and ob.sendMobile = :sendMobile order by ob.sendTime desc")
        .setString("sendMobile", sendMobile)
        .setLong("shopId", shopId).setFirstResult(pageNo * pageSize).setMaxResults(pageSize);
  }

  public static Query getSmsList(Session session,SmsSearchCondition condition) {
    StringBuilder sb=new StringBuilder();
    sb.append("from Sms s where s.shopId=:shopId and s.deleted=:deleted");
    if(condition.getSmsType()!=null){
      sb.append(" and s.smsType=:smsType");
    }
    if(condition.getStartTime()!=null){
      sb.append(" and s.editDate>:startTime");
    }
    if(condition.getEndTime()!=null){
      sb.append(" and s.editDate<:endTime");
    }
    sb.append(" order by s.editDate desc");
    Query query= session.createQuery(sb.toString());
    query.setLong("shopId", condition.getShopId()).setParameter("deleted", DeletedType.FALSE);
    if(condition.getSmsType()!=null){
      query.setParameter("smsType",condition.getSmsType());
    }
     if(condition.getStartTime()!=null){
       query.setParameter("startTime",condition.getStartTime());
    }
    if(condition.getEndTime()!=null){
       query.setParameter("endTime",condition.getEndTime());
    }
    if(condition.getPager()!=null){
      query.setFirstResult(condition.getPager().getRowStart()).setMaxResults(condition.getPager().getPageSize());
    }
    return query;
  }

  public static Query countSms(Session session,SmsSearchCondition condition) {
    StringBuilder sb=new StringBuilder();
    sb.append("select count(s) from Sms s,SmsIndex si where s.id=si.smsId and s.shopId=:shopId and s.deleted=:deleted");
    if(condition.getSmsType()!=null){
      sb.append(" and s.smsType=:smsType");
    }
    if(StringUtil.isNotEmpty(condition.getKeyWord())){
      sb.append(" and si.content like :keyWord");
    }

    if(condition.getStartTime()!=null){
      sb.append(" and s.editDate>:startTime");
    }
    if(condition.getEndTime()!=null){
      sb.append(" and s.editDate<:endTime");
    }
    Query query= session.createQuery(sb.toString());
    query.setLong("shopId", condition.getShopId()).setParameter("deleted", DeletedType.FALSE);
    if(condition.getSmsType()!=null){
      query.setParameter("smsType",condition.getSmsType());
    }
    if(StringUtil.isNotEmpty(condition.getKeyWord())){
      query.setParameter("keyWord","%"+condition.getKeyWord()+"%");
    }
    if(condition.getStartTime()!=null){
      query.setParameter("startTime",condition.getStartTime());
    }
    if(condition.getEndTime()!=null){
      query.setParameter("endTime",condition.getEndTime());
    }
    return query;
  }

  public static Query countShopOutBox(Session session, long shopId) {
    return session.createQuery("select count(*) from OutBox ob where ob.shopId = :shopId ")
        .setLong("shopId", shopId);
  }

  public static Query countOutBoxNumberByShopIdAndMobile(Session session, long shopId, String mobile) {
    return session.createQuery("select count(*) from OutBox ob where ob.shopId = :shopId and ob.sendMobile = :mobile")
        .setLong("shopId", shopId).setString("mobile", mobile);
  }

  public static Query countShopInBox(Session session, long shopId) {
    return session.createQuery("select count(*) from InBox ib where ib.receiveShopId = :shopId ")
        .setLong("shopId", shopId);
  }

  public static Query countShopSmsJobs(Session session, long shopId) {
    return session.createQuery("select count(*) from SmsJob tb where tb.shopId = :shopId ")
        .setLong("shopId", shopId);
  }

  public static Query getMsgTemplateByType(Session session, String type) {
    return session.createQuery("select mt from MessageTemplate mt where mt.type=:type")
        .setString("type", type);
  }

   public static Query getShopMsgTemplateById(Session session,Long shopId,Long templateId){
    return session.createQuery("select mt from MessageTemplate mt where mt.shopId=:shopId and mt.id=:id and mt.type=:type")
      .setParameter("shopId",shopId).setParameter("id",templateId).setParameter("type",MessageScene.SHOP_SMS_TEMPLATE.toString());
  }

   public static Query getShopMsgTemplateByName(Session session,Long shopId,String name){
    return session.createQuery("select mt from MessageTemplate mt where mt.shopId=:shopId and mt.name=:name and mt.type=:type")
      .setParameter("shopId",shopId).setParameter("name",name).setParameter("type",MessageScene.SHOP_SMS_TEMPLATE.toString());
  }

  public static Query getShopMsgTemplate(Session session,Long shopId,String keyWord,Pager pager){
    StringBuilder sb=new StringBuilder();
    sb.append("select mt from MessageTemplate mt where mt.shopId=:shopId and mt.type=:type");
    if(StringUtil.isNotEmpty(keyWord)){
      sb.append(" and (mt.name like :keyword or mt.content like :keyword)");
    }
    sb.append(" order by mt.creationDate desc");
    Query query=session.createQuery(sb.toString()).setParameter("shopId",shopId).setParameter("type",MessageScene.SHOP_SMS_TEMPLATE.toString())
      .setFirstResult((pager.getCurrentPage() - 1) * pager.getPageSize()).setMaxResults(pager.getPageSize());
    if(StringUtil.isNotEmpty(keyWord)){
      query.setParameter("keyword","%"+keyWord+"%");
    }
    return query;
  }

  public static Query countShopMsgTemplate(Session session,Long shopId,String keyWord){
    StringBuilder sb=new StringBuilder();
    sb.append("select count(mt) from MessageTemplate mt where mt.shopId=:shopId and mt.type=:type");
    if(StringUtil.isNotEmpty(keyWord)){
      sb.append(" and (mt.name like :keyword or mt.content like :keyword)");
    }
    Query query=session.createQuery(sb.toString())
      .setParameter("shopId",shopId).setParameter("type",MessageScene.SHOP_SMS_TEMPLATE.toString());
    if(StringUtil.isNotEmpty(keyWord)){
      query.setParameter("keyword","%"+keyWord+"%");
    }
    return query;
  }

  public static Query countMessageTemplate(Session session, String type,Long shopId) {
    if(StringUtil.isEmpty(type)){
      return session.createQuery("select count(mt) from MessageTemplate mt ");
    }
    return session.createQuery("select count(mt) from MessageTemplate mt where mt.type=:type")
        .setString("type", type);
  }

  public static Query searchMessageTemplate(Session session,String type,Long shopId,Pager pager){
    if(StringUtil.isEmpty(type)){
      return session.createQuery("select mt from MessageTemplate mt where mt.shopId<100 order by last_update desc").setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
    }
    return session.createQuery("select mt from MessageTemplate mt where mt.shopId<100 and mt.type=:type order by last_update desc")
        .setString("type", type).setFirstResult((pager.getCurrentPage()-1) * pager.getPageSize()).setMaxResults(pager.getPageSize());
  }

  public static Query getMessageTemplate(Session session)
  {
    return session.createQuery("select mt from MessageTemplate mt where mt.necessary = :necessary")
        .setParameter("necessary", MessageSendNecessaryType.UNNECESSARY);
  }

  public static Query getMessageSwitchDTOByShopIdAndScene(Session session,Long shopId,MessageScene scene)
  {
    return session.createQuery("select ms from MessageSwitch ms where ms.shopId =:shopId and ms.scene =:scene")
        .setLong("shopId",shopId).setParameter("scene",scene);
  }

  /**
   * 短信发送失败列表
   */
  public static Query getFailedSmsJobs(Session session, FailedSmsJobDTO failedSmsJobDTO, Pager pager, List<Long> shopIds) {
    StringBuffer hql = new StringBuffer();
    hql.append("select fsj from FailedSmsJob fsj where 1=1 ");
    if (failedSmsJobDTO == null)
      return null;
    if (StringUtils.isNotBlank(failedSmsJobDTO.getName())) {
      if (CollectionUtils.isNotEmpty(shopIds)) {
        hql.append("  and fsj.shopId in(:shopIds) ");
      }
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getReceiveMobile())) {
      hql.append(" and fsj.receiveMobile like:mobile ");
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getContent())) {
      hql.append(" and fsj.content like:content ");
    }
     hql.append(" order by fsj.startTime ");
    Query query = session.createQuery(hql.toString());
    if (StringUtils.isNotBlank(failedSmsJobDTO.getName())) {
      if (CollectionUtils.isNotEmpty(shopIds)) {
        query.setParameterList("shopIds", shopIds);
      }
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getReceiveMobile())) {
      query.setString("mobile", failedSmsJobDTO.getReceiveMobile() + "%");
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getContent())) {
      query.setString("content",  "%"+failedSmsJobDTO.getContent() + "%");
    }
    return query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }

  /**
   * 查询失败短信
   */
  public static Query getCountFailedSmsJob(Session session, FailedSmsJobDTO failedSmsJobDTO, List<Long> shopIds) {
    StringBuffer hql = new StringBuffer();
    hql.append("select count(*) from FailedSmsJob fsj where 1=1");
    if (failedSmsJobDTO == null)
      return null;
    if (StringUtils.isNotBlank(failedSmsJobDTO.getName())) {
      if (CollectionUtils.isNotEmpty(shopIds)) {
        hql.append("  and fsj.shopId in(:shopIds)");
      }
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getReceiveMobile())) {
      hql.append(" and fsj.receiveMobile like:mobile");
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getContent())) {
      hql.append(" and fsj.content like:content");
    }
    Query query = session.createQuery(hql.toString());
    if (StringUtils.isNotBlank(failedSmsJobDTO.getName())) {
      if (CollectionUtils.isNotEmpty(shopIds)) {
        query.setParameterList("shopIds", shopIds);
      }
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getReceiveMobile())) {
      query.setString("mobile", failedSmsJobDTO.getReceiveMobile() + "%");
    }
    if (StringUtils.isNotBlank(failedSmsJobDTO.getContent())) {
      query.setString("content", "%"+ failedSmsJobDTO.getContent() + "%");
    }
    return query;
  }

  public static Query getSmsByIds(Session session,Long shopId,Long[] smsIds) {
    return session.createQuery("select s from Sms s where s.shopId=:shopId and s.id in (:smsIds)").setParameter("shopId",shopId).setParameterList("smsIds",smsIds);
  }

  public static Query getSmsIndexBySmsId(Session session,Long shopId,Long smsId) {
    return session.createQuery("select s from SmsIndex s where s.shopId=:shopId and s.smsId=:smsId").setParameter("shopId",shopId).setParameter("smsId", smsId);
  }

  public static Query querySms(Session session,SmsSearchCondition condition) {
    StringBuilder sb=new StringBuilder("select s from Sms s,SmsIndex si where s.id=si.smsId and s.shopId=:shopId");
    if(condition.getSmsType()!=null){
      sb.append(" and s.smsType=:smsType");
    }
    if(StringUtil.isNotEmpty(condition.getKeyWord())){
      sb.append(" and si.content like :keyWord");
    }
    if(condition.getStartTime()!=null){
      sb.append(" and s.editDate>:startTime");
    }
    if(condition.getEndTime()!=null){
      sb.append(" and s.editDate<:endTime");
    }
    sb.append(" order by s.editDate desc");
    Query query= session.createQuery(sb.toString())
            .setParameter("shopId",condition.getShopId());
    if(condition.getSmsType()!=null){
      query.setParameter("smsType",condition.getSmsType());
    }
     if(condition.getStartTime()!=null){
       query.setParameter("startTime",condition.getStartTime());
    }
    if(condition.getEndTime()!=null){
       query.setParameter("endTime",condition.getEndTime());
    }
    if(StringUtil.isNotEmpty(condition.getKeyWord())){
      query.setParameter("keyWord","%"+condition.getKeyWord()+"%");
    }
     if(condition.getPager()!=null){
      query.setFirstResult(condition.getPager().getRowStart()).setMaxResults(condition.getPager().getPageSize());
    }
    return query;
  }


  /**
   * 查询敏感词
   */
  public static Query getSensitiveWords(Session session) {
    return session.createQuery("select s from SensitiveWords s");
  }

  public static SQLQuery getAnnoucements(Session session,AnnouncementDTO announcementIndex){
    StringBuffer sb=new StringBuffer();
    sb.append("select id as id,ac.title as title ,ac.release_date as releaseDate,ac.release_man_id as releaseManId,ac.release_man as releaseMan " +
        "from announcement ac where ac.status !=:status");
    if(announcementIndex.getEndDate()!=null){
      sb.append(" and ac.release_date<=:releaseDate");
    }
    sb.append(" order by release_date desc");
    SQLQuery query=(SQLQuery)session.createSQLQuery(sb.toString()).setParameter("status", ObjectStatus.DISABLED.toString());
    if(announcementIndex.getPager()!=null){
      query.setFirstResult(announcementIndex.getPager().getRowStart()).setMaxResults(announcementIndex.getPager().getPageSize());
    }
    if(announcementIndex.getEndDate()!=null){
      query.setLong("releaseDate",announcementIndex.getEndDate());
    }
    return query;
  }

  public static Query getFestivals(Session session,FestivalDTO festivalIndex){
    StringBuffer sb=new StringBuffer();
    sb.append("from Festival where status!=:status order by releaseDate desc");
    Query query=session.createQuery(sb.toString()).setString("status", ObjectStatus.DISABLED.toString());
    query.setFirstResult(festivalIndex.getStart()).setMaxResults(festivalIndex.getLimit());
    return query;
  }

  public static Query getFestivalCount(Session session,FestivalDTO festivalIndex){
    StringBuffer sb=new StringBuffer();
    sb.append("select count(*) from Festival where status!=:status ");
    Query query=session.createQuery(sb.toString()).setString("status", ObjectStatus.DISABLED.toString());
    return query;
  }

   public static Query getAnnouncementById(Session session,Long announcementId){
    return session.createQuery("select ac from Announcement ac where ac.id=:announcementId").setLong("announcementId",announcementId);
  }

  public static Query getAnnouncementCount(Session session,AnnouncementDTO announcementIndex){
    StringBuffer sb=new StringBuffer();
    sb.append("select count(ac) from Announcement ac where ac.status!=:status");
    if(announcementIndex.getEndDate()!=null){
      sb.append(" and ac.releaseDate<=:releaseDate");
    }
    Query query=session.createQuery(sb.toString()).setString("status", ObjectStatus.DISABLED.toString());
    if(announcementIndex.getPager()!=null){
      query.setFirstResult(announcementIndex.getPager().getRowStart()).setMaxResults(announcementIndex.getPager().getPageSize());
    }
    if(announcementIndex.getEndDate()!=null){
      query.setLong("releaseDate",announcementIndex.getEndDate());
    }
    return query;
  }

  public static Query getLastReleaseDateByToday(Session session) throws ParseException {
    return session.createQuery("select max(ac.releaseDate) from Announcement ac where ac.status !=:status and ac.releaseDate<=:releaseDate")
        .setString("status", ObjectStatus.DISABLED.toString()).setLong("releaseDate",DateUtil.getEndTimeOfToday());
  }

  public static Query getAnnouncementLastReadDate(Session session, Long shopId, Long userId) {
    return session.createQuery("select ar.lastReadDate from AnnouncementReadRecord ar where ar.userId=:userId and ar.shopId=:shopId ")
        .setLong("userId", userId).setLong("shopId", shopId);
  }

  public static Query getLastAnnouncementByToday(Session session) throws ParseException {
    return session.createQuery("from Announcement where status !=:status and releaseDate <=:releaseDate order by releaseDate desc")
        .setString("status", ObjectStatus.DISABLED.toString())
        .setLong("releaseDate", DateUtil.getEndTimeOfToday()).setFirstResult(0).setMaxResults(1);
  }

   public static Query getLastFestivalByToday(Session session) throws ParseException {
    return session.createQuery("from Festival where status !=:status and releaseDate <=:releaseDate order by releaseDate desc")
        .setString("status", ObjectStatus.DISABLED.toString())
        .setLong("releaseDate", DateUtil.getEndTimeOfToday()).setFirstResult(0).setMaxResults(1);
  }

  public static Query getCurrentFestivals(Session session) throws ParseException {
    return session.createQuery("from Festival where status !=:status and startRemindDate <=:currentDate and  endRemindDate>=:currentDate order by releaseDate desc")
        .setString("status", ObjectStatus.DISABLED.toString())
        .setLong("currentDate",System.currentTimeMillis());
  }



  public static Query getUserReadRecord(Session session,ReminderType type,Long shopId,Long userId){
    return session.createQuery("from UserReadRecord where shopId =:shopId and userId =:userId and reminderType=:reminderType")
        .setLong("shopId",shopId).setLong("userId",userId).setString("reminderType",type.toString());
  }

 public static Query getCurrentPromotionMsgJobDTO(Session session){
    return session.createQuery("from PromotionMsgJob where exeTime<=:now and exeStatus=:exeStatus")
      .setLong("now",System.currentTimeMillis()).setString("exeStatus", ExeStatus.READY.toString());
  }

  public static Query getInvitationCode(Session session, int start, int pageSize) {
    return session.createQuery("from InvitationCode ").setMaxResults(pageSize).setFirstResult(start);
  }

  public static Query getInvitationCode(Session session, int pageSize,long maxId) {
    return session.createQuery("from InvitationCode i where i.id > :maxId order by i.id asc ")
        .setMaxResults(pageSize).setFirstResult(0).setLong("maxId",maxId);
  }

  public static Query getInvitationCodeRecycle(Session session, int start, int pageSize) {
    return session.createQuery("from InvitationCodeRecycle ").setMaxResults(pageSize).setFirstResult(start);
  }

  public static Query findEffectiveInvitationCodeByCode(Session session, String code) {
    return session.createQuery("from InvitationCode i where i.code=:code and i.status =:status")
        .setString("code", code).setParameter("status", InvitationCodeStatus.EFFECTIVE);
  }

  public static Query findInvitationCodeByCode(Session session, String code) {
    return session.createQuery("from InvitationCode i where i.code=:code ").setString("code", code);
  }

  public static Query countEffectiveInvitationCodeByCode(Session session, List<String> codes) {
    return session.createQuery("select count(i) from InvitationCode i where i.code in :codes and i.status =:status")
        .setParameterList("codes", codes).setParameter("status", InvitationCodeStatus.EFFECTIVE);
  }

  public static Query getSmsSendingTimesByMobiles(Session session, Set<String> mobiles) {
    return session.createQuery("from SmsSendingTimes s where s.mobile in (:mobiles) ").setParameterList("mobiles", mobiles);
  }

  public static Query getOutBox(Session session, int pageSize, long startId) {
    return session.createQuery("from OutBox where id>:startId order by id asc").setLong("startId", startId).setMaxResults(pageSize);
  }

  public static Query getSmsJobsBySmsId(Session session, Long smsId, Long id, int limit, int sendTimes) {
    return session.createQuery("select s from SmsJob s where s.id != :id and s.smsId=:smsId and s.sendTimes=:sendTimes")
        .setLong("id", id).setMaxResults(limit).setLong("smsId", smsId).setInteger("sendTimes", sendTimes);
  }

 public static Query getSmsJobsBySmsId(Session session,Long shopId,Long... smsIds) {
    return session.createQuery("select s from SmsJob s where s.shopId = :shopId and s.smsId in(:smsIds)")
       .setLong("shopId",shopId).setParameterList("smsIds", smsIds);
  }

  public static Query getAllOutBox(Session session) {
    return session.createQuery("from OutBox");
  }

  public static Query getOutBoxByPager(Session session, int pageSize, int startPageNo) {
    return session.createQuery("from OutBox").setFirstResult((startPageNo - 1) * pageSize).setMaxResults(pageSize);
  }

  public static Query getOutBoxByStatStatus(Session session, int pageSize, StatStatus statStatus) {
    return session.createQuery("from OutBox where statStatus=:statStatus").setParameter("statStatus", statStatus).setMaxResults(pageSize);
  }


  public static Query countMsg(Session session,WXMsgSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select count(m) from WXMsg m where m.deleted=:deleted");
    if(condition.getShopId()!=null){
      sb.append(" and m.fromShopId=:shopId");
    }
    if(condition.getStartSendTime()!=null){
      sb.append(" and m.sendTime >= :startSendTime");
    }
    if(condition.getEndSendTime()!=null){
      sb.append(" and m.sendTime <= :endSendTime");
    }
    if(ArrayUtil.isNotEmpty(condition.getCategoryList())){
      sb.append(" and m.category in (:categorys)");
    }
    if(ArrayUtil.isNotEmpty(condition.getStatusList())){
      sb.append(" and m.status in (:statusList)");
    }
    Query query  = session.createQuery(sb.toString()).setParameter("deleted", DeletedType.FALSE);
    if(condition.getShopId()!=null){
      query.setParameter("shopId",condition.getShopId());
    }
    if(condition.getStartSendTime()!=null){
      query.setParameter("startSendTime",condition.getStartSendTime());
    }
    if(condition.getEndSendTime()!=null){
      query.setParameter("endSendTime",condition.getEndSendTime());
    }
    if(ArrayUtil.isNotEmpty(condition.getCategoryList())){
      query.setParameterList("categorys",condition.getCategoryList());
    }
    if(ArrayUtil.isNotEmpty(condition.getStatusList())){
      query.setParameterList("statusList",condition.getStatusList());
    }
    return query ;
  }


  public static Query getMsg(Session session,WXMsgSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXMsg where deleted=:deleted");
    if(condition.getShopId()!=null){
      sb.append(" and fromShopId=:shopId");
    }
    if(ArrayUtil.isNotEmpty(condition.getCategoryList())){
      sb.append(" and category in(:categorys)");
    }
    Query query  = session.createQuery(sb.toString()).setParameter("deleted",DeletedType.FALSE);
    if(condition.getShopId()!=null){
      query.setParameter("shopId",condition.getShopId());
    }
    if(ArrayUtil.isNotEmpty(condition.getCategoryList())){
      query.setParameterList("categorys",condition.getCategoryList());
    }
    if(condition.getPager()!=null){
      query.setFirstResult(condition.getPager().getRowStart())
        .setMaxResults(condition.getPager().getPageSize());
    }
    return query ;
  }

public static Query getWXAccountByCondition(Session session,WXShopAccountSearchCondition condition) {
    StringBuilder sb = new StringBuilder();
    sb.append("select a from WXAccount a,WXShopAccount sa where a.deleted='FALSE' and sa.deleted='FALSE'" +
      " and a.id=sa.accountId");
    if(condition.getShopId()!=null){
      sb.append(" and sa.shop_id=:shopId");
    }
    Query query =session.createQuery(sb.toString());
    if(condition.getShopId()!=null){
      query.setParameter("shopId", condition.getShopId());
    }
    return query;
  }


  public static Query getWXShopBill(Session session,Long shopId,Pager pager) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXShopBill where shopId=:shopId and deleted=:deleted order by vestDate desc");
    Query query  = session.createQuery(sb.toString())
      .setParameter("shopId", shopId)
      .setParameter("deleted", DeletedType.FALSE)
      ;
    if(pager!=null){
      query.setFirstResult(pager.getRowStart())
        .setMaxResults(pager.getPageSize());
    }
    return query ;
  }

  public static Query getWXShopBillStat(Session session,Long shopId) {
    SmsSendScene [] scenes={SmsSendScene.WX_SEND_MASS_MSG,SmsSendScene.WX_CONSUME_TEMPLATE};
    StringBuilder sb = new StringBuilder();
    sb.append("select sum(b.amount),sum(b.total) from WXShopBill b where deleted=:deleted and scene in (:scenes) and shopId=:shopId");
    return session.createQuery(sb.toString())
      .setParameter("deleted", DeletedType.FALSE)
      .setParameterList("scenes",scenes)
      .setParameter("shopId", shopId)
      ;
  }

  public static Query getWXMsgById(Session session,Long id) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXMsg where id=:id and deleted=:deleted");
    Query query  = session.createQuery(sb.toString())
      .setParameter("id",id)
      .setParameter("deleted",DeletedType.FALSE)
      ;
    return query ;
  }


  public static Query getAuditingMsgById(Session session,Long id) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXMsg where id=:id and status=:status and deleted=:deleted");
    Query query  = session.createQuery(sb.toString())
      .setParameter("id", id)
      .setParameter("status", WXMsgStatus.AUDITING)
      .setParameter("deleted",DeletedType.FALSE)
      ;
    return query ;
  }

  public static Query getWXMsgByMsgId(Session session,String msgId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXMsg where msgId=:msgId and deleted=:deleted");
    Query query  = session.createQuery(sb.toString())
      .setParameter("msgId", msgId)
      .setParameter("deleted",DeletedType.FALSE)
      ;
    return query ;
  }



  public static Query getWXMsgByIdAndShopId(Session session,Long shopId,Long id) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXMsg where id=:id and fromShopId=:shopId and deleted=:deleted");
    Query query  = session.createQuery(sb.toString())
      .setParameter("id", id)
      .setParameter("shopId",shopId)
      .setParameter("deleted",DeletedType.FALSE);
    return query ;
  }


  public static Query getCountWXMsgReceiverByMsgLocalId(Session session,Long msgLocalId){
    StringBuffer hql = new StringBuffer();
    hql.append("select count(r) from WXMsgReceiver r where r.deleted='FALSE' and r.msgId=:msgLocalId");
    Query query = session.createQuery(hql.toString())
      .setParameter("msgLocalId",msgLocalId)
      ;
    return query;
  }

  public static Query countWXShopBill(Session session,Long shopId){
    StringBuffer hql = new StringBuffer();
    hql.append("select count(s) from WXShopBill s where s.deleted='FALSE' and s.shopId=:shopId");
    Query query = session.createQuery(hql.toString())
      .setParameter("shopId",shopId)
      ;
    return query;
  }

  public static Query getWXMsgReceiverByMsgLocalId(Session session, Long msgLocalId){
    StringBuffer hql = new StringBuffer();
    hql.append("from WXMsgReceiver r where r.deleted='FALSE' and r.msgId=:msgLocalId");
    Query query = session.createQuery(hql.toString())
      .setParameter("msgLocalId",msgLocalId)
      ;
    return query;
  }

   public static Query getWXMsgReceiverByMsgId(Session session, Long msgId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXMsgReceiver  where msgId = :msgId and deleted =:deleted  ");
    Query query = session.createQuery(sb.toString())
        .setParameter("msgId", msgId)
        .setParameter("deleted", DeletedType.FALSE);
    return query;
  }

public static Query getWXSendStatusReportByMsgId(Session session,String msgId) {
    StringBuilder sb = new StringBuilder();
    sb.append("from WXSendStatusReport where msgId=:msgId and deleted=:deleted");
    Query query  = session.createQuery(sb.toString())
      .setParameter("msgId", msgId)
      .setParameter("deleted",DeletedType.FALSE)
      ;
    return query ;
  }

  /**
    * 查询待审核数量
    */
   public static Query getCountAdultJob(Session session, WXMsgDTO wxMsgDTO,String type) {
     StringBuffer hql = new StringBuffer();
     if("aduitting".equals(type)){
       hql.append("select count(*) from WXMsg wxa where 1=1 and wxa.deleted='FALSE' and wxa.status='AUDITING'");
     } else{
       hql.append("select count(*) from WXMsg wxa where 1=1 and wxa.deleted='FALSE'and wxa.fromShopId="+wxMsgDTO.getFromShopId().toString());
     }

     if (wxMsgDTO == null) {
       return null;
     }
     if (StringUtils.isNotBlank(wxMsgDTO.getDescription())) {
       hql.append(" and wxa.description like:description");
     }
     if (StringUtils.isNotBlank(wxMsgDTO.getTitle())) {
       hql.append(" and wxa.title like:title");
     }
     Query query = session.createQuery(hql.toString());
     if (StringUtils.isNotBlank(wxMsgDTO.getDescription())) {
       query.setString("description","%"+wxMsgDTO.getDescription() + "%");
     }
     if (StringUtils.isNotBlank(wxMsgDTO.getTitle())) {
       query.setString("title", "%"+ wxMsgDTO.getTitle() + "%");
     }
     return query;
   }


  /**
   * 待审核列表
   */
  public static Query getAdultJobs(Session session, WXMsgDTO wxMsgDTO,Pager pager,String type) {
    StringBuffer hql = new StringBuffer();
    if("aduitting".equals(type)){
      hql.append("select wxa from WXMsg wxa where 1=1 and wxa.deleted='FALSE' and wxa.status='AUDITING'");
    } else{
      hql.append("select wxa from WXMsg wxa where 1=1 and wxa.deleted='FALSE'and wxa.fromShopId="+wxMsgDTO.getFromShopId().toString());
    }
    if (wxMsgDTO == null){
      return null;
    }
    if (StringUtils.isNotBlank(wxMsgDTO.getDescription())) {
      hql.append(" and wxa.description like:description ");
    }
    if (StringUtils.isNotBlank(wxMsgDTO.getTitle())) {
      hql.append(" and wxa.title like:title ");
    }
    hql.append(" order by wxa.sendTime desc ");
    Query query = session.createQuery(hql.toString());

    if (StringUtils.isNotBlank(wxMsgDTO.getDescription())) {
      query.setString("description","%"+wxMsgDTO.getDescription() + "%");
    }
    if (StringUtils.isNotBlank(wxMsgDTO.getTitle())) {
      query.setString("title",  "%"+wxMsgDTO.getTitle() + "%");
    }
    return query.setFirstResult(pager.getRowStart()).setMaxResults(pager.getPageSize());
  }





}