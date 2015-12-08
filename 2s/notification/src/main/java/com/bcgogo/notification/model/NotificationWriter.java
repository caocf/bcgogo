package com.bcgogo.notification.model;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.StatStatus;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.dto.SmsSearchCondition;
import com.bcgogo.notification.reminder.ReminderType;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.FestivalDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.user.WXMsgSearchCondition;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

public class NotificationWriter extends GenericWriterDao {

  public NotificationWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public List<InBox> getShopInBoxs(long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopInBoxs(session, shopId, pageNo, pageSize);
      return (List<InBox>) q.list();
    } finally {
      release(session);
    }
  }

   public WXMsg getAuditingMsgById(Long id){
    Session session = this.getSession();
    try {
      Query q = SQL.getAuditingMsgById(session, id);
      return (WXMsg)q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public WXMsg getWXMsgByMsgId(String msgId){
    Session session = this.getSession();
    try {
      Query q = SQL.getWXMsgByMsgId(session, msgId);
      return (WXMsg)q.uniqueResult();
    } finally {
      release(session);
    }
  }

    public List<WXMsg> getMsg(WXMsgSearchCondition condition){
    Session session = this.getSession();
    try {
      Query q = SQL.getMsg(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countMsg(WXMsgSearchCondition condition){
    Session session = this.getSession();
    try {
      Query q = SQL.countMsg(session, condition);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int countWXShopBill(Long shopId){
    Session session = this.getSession();
    try {
      Query q = SQL.countWXShopBill(session,shopId);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

   public List<WXShopBill> getWXShopBill(Long shopId,Pager pager){
    Session session = this.getSession();
    try {
      Query query = SQL.getWXShopBill(session,shopId,pager);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Object getWXShopBillStat(Long shopId){
      Session session = this.getSession();
      try {
        Query q = SQL.getWXShopBillStat(session, shopId);
        return q.uniqueResult();
      } finally {
        release(session);
      }
    }



  public WXMsg getWXMsgById(Long id){
    Session session = this.getSession();
    try {
      Query q = SQL.getWXMsgById(session,id);
      return (WXMsg) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public WXMsg getWXMsgByIdAndShopId(Long shopId,Long msgId){
    Session session = this.getSession();
    try {
      Query q = SQL.getWXMsgByIdAndShopId(session,shopId,msgId);
      return (WXMsg)CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  /**
   * 查询待审核列表
   */
  public List<WXMsg> getAdultJobs(WXMsgDTO wxMsgDTO, Pager pager,String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAdultJobs(session, wxMsgDTO, pager,type);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 查询待审核总数
   */
  public int getCountAdultJob(WXMsgDTO wxMsgDTO,String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCountAdultJob(session, wxMsgDTO,type);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }


  //查询 WXMsgReceiver数量
  public int getCountWXMsgReceiverByMsgLocalId(Long msgLocalId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCountWXMsgReceiverByMsgLocalId(session,msgLocalId);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  //查询 WXMsgReceiver列表
  public List<WXMsgReceiver> getWXMsgReceiverByMsgLocalId(Long msgLocalId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWXMsgReceiverByMsgLocalId(session,msgLocalId);
      return q.list();
    } finally {
      release(session);
    }
  }



  public List<WXMsgReceiver> getWXMsgReceiverByMsgId(Long msgId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getWXMsgReceiverByMsgId(session, msgId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutBox> getShopOutBoxs(long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopOutBoxs(session, shopId, pageNo, pageSize);
      return  q.list();
    } finally {
      release(session);
    }
  }

  public List<OutBox> getOutBoxByShopAndMobile(long shopId, String mobile, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutBoxByShopAndMobile(session, shopId, mobile, pageNo, pageSize);
      return (List<OutBox>) q.list();
    } finally {
      release(session);
    }
  }

   public List<Sms> getSmsList(SmsSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsList(session,condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countSms(SmsSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query q = SQL.countSms(session,condition);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countShopOutBox(long shopId) {
    Session session = this.getSession();

    try {
      Query q = SQL.countShopOutBox(session, shopId);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countOutBoxNumberByShopIdAndMobile(long shopId, String mobile) {
    Session session = this.getSession();
    try {
      Query q = SQL.countOutBoxNumberByShopIdAndMobile(session, shopId, mobile);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countShopInBox(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopInBox(session, shopId);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public int countShopSmsJobs(long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopSmsJobs(session, shopId);
      if (q.uniqueResult() == null) {
        return 0;
      }
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<SmsJob> getSmsJobsByStartTime(long startTime, SmsChannel smsChannel, boolean isGroupBy, Integer limit) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsJobsByStartTime(session, startTime, smsChannel, isGroupBy, limit);
      return (List<SmsJob>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SmsJob> getSmsJobsByShopId(long shopId, int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsJobsByShopId(session, shopId, pageNo, pageSize);
      return (List<SmsJob>) q.list();
    } finally {
      release(session);
    }
  }




  public MessageTemplate getMsgTemplateByType(String type) {
    Session session = this.getSession();
    try {
      Query q = SQL.getMsgTemplateByType(session, type);
      return (MessageTemplate) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public MessageTemplate getShopMsgTemplateById(Long shopId,Long templateId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopMsgTemplateById(session,shopId,templateId);
      return (MessageTemplate)q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<MessageTemplate> getShopMsgTemplateByName(Long shopId,String name) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopMsgTemplateByName(session,shopId,name);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<MessageTemplate> getShopMsgTemplate(Long shopId,String keyWord,Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopMsgTemplate(session,shopId,keyWord,pager);
      return q.list();
    } finally {
      release(session);
    }
  }

   public int countShopMsgTemplate(Long shopId,String keyWord) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopMsgTemplate(session,shopId,keyWord);
      return NumberUtil.intValue(String.valueOf(q.uniqueResult()),0);
    } finally {
      release(session);
    }
  }

  public int countMessageTemplate(String type,Long shopId,int pageNo) {
    Session session = this.getSession();
   try {
     Query q = SQL.countMessageTemplate(session,type,shopId);
     return NumberUtil.intValue(String.valueOf(q.uniqueResult()),0);
   } finally {
     release(session);
   }
 }
 public List<MessageTemplate> searchMessageTemplate(String type, Long shopId, Pager pager){
   Session session = this.getSession();
   try {
     Query q = SQL.searchMessageTemplate(session, type,shopId,pager);
     return (List<MessageTemplate>) q.list();
   } finally {
     release(session);
   }
 }

 public List<MessageTemplate> getMessageTemplate()
 {
   Session session = this.getSession();
   try{
     Query q = SQL.getMessageTemplate(session);
     return (List<MessageTemplate>) q.list();
   }finally {
     release(session);
   }
 }

  public MessageSwitch getMessageSwitchDTOByShopIdAndScene(Long shopId,MessageScene scene)
  {
    Session session = this.getSession();
    try{
      Query q = SQL.getMessageSwitchDTOByShopIdAndScene(session,shopId,scene);
      List<MessageSwitch> messageSwitches = (List<MessageSwitch>)q.list();
      if(CollectionUtils.isNotEmpty(messageSwitches))
      {
        return messageSwitches.get(0);
      }
      return null;
    }finally {
      release(session);
    }
  }

  /**
   * 查询发送失败短信
   */
  public List<FailedSmsJob> getFailedSmsJobs(FailedSmsJobDTO failedSmsJobDTO, Pager pager, List<Long> shopIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getFailedSmsJobs(session, failedSmsJobDTO, pager, shopIds);
      return (List<FailedSmsJob>) q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 短信发送失败总数
   */
  public int getCountFailedSmsJob(FailedSmsJobDTO failedSmsJobDTO, List<Long> shipIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCountFailedSmsJob(session, failedSmsJobDTO, shipIds);
      return NumberUtil.intValue(String.valueOf(q.uniqueResult()), 0);
    } finally {
      release(session);
    }
  }

  public List<Sms> getSmsByIds(Long shopId,Long... smsIds){
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsByIds(session,shopId,smsIds);
      return q.list();
    } finally {
      release(session);
    }
  }

   public SmsIndex getSmsIndexBySmsId(Long shopId,Long smsId){
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsIndexBySmsId(session,shopId,smsId);
      return (SmsIndex)q.uniqueResult();
    } finally {
      release(session);
    }
  }

   public List<Sms> querySms(SmsSearchCondition condition){
    Session session = this.getSession();
    try {
      Query q = SQL.querySms(session,condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  /**
   * 获取敏感词
   */
  public List<SensitiveWords> getSensitiveWords() {
    Session session = this.getSession();
    try {
      Query q = SQL.getSensitiveWords(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Announcement> getAnnouncements(AnnouncementDTO announcementIndex){
    Session session = this.getSession();
    try{
      SQLQuery query =SQL.getAnnoucements(session, announcementIndex);
      query.addScalar("id", StandardBasicTypes.LONG)
          .addScalar("title", StandardBasicTypes.STRING)
          .addScalar("releaseDate", StandardBasicTypes.LONG)
          .addScalar("releaseManId", StandardBasicTypes.LONG)
          .addScalar("releaseMan", StandardBasicTypes.STRING);
      query.setResultTransformer(Transformers.aliasToBean(Announcement.class));
      return query.list();
    }finally {
      release(session);
    }
  }

  public List<Festival> getFestivals(FestivalDTO festivalIndex){
    Session session = this.getSession();
    try{
      Query query =SQL.getFestivals(session, festivalIndex);
      return query.list();
    }finally {
      release(session);
    }
  }

  public Announcement getAnnouncementById(Long announcementId){
    Session session = this.getSession();
    try{
      Query q = SQL.getAnnouncementById(session,announcementId);
      return (Announcement)q.uniqueResult();
    }finally {
      release(session);
    }
  }

  public Integer getAnnoucementCount(AnnouncementDTO announcementIndex){
    Session session = this.getSession();
    try{
      Query q = SQL.getAnnouncementCount(session,announcementIndex);
      return  NumberUtil.intValue(String.valueOf(q.uniqueResult()), 0);
    }finally {
      release(session);
    }
  }

   public Integer getFestivalCount(FestivalDTO festivalDTO){
    Session session = this.getSession();
    try{
      Query q = SQL.getFestivalCount(session,festivalDTO);
      return  NumberUtil.intValue(String.valueOf(q.uniqueResult()), 0);
    }finally {
      release(session);
    }
  }

  /**
   * 获取当前最新发布的公告时间
   * @return
   */
  public Long getLastReleaseDateByToday() throws ParseException {
    Session session = this.getSession();
    try {
      Query q = SQL.getLastReleaseDateByToday(session);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long getAnnouncementLastReadDate(Long shopId, Long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAnnouncementLastReadDate(session,shopId,userId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long saveOrUpdateAnnouncement(Long shopId, Long userId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAnnouncementLastReadDate(session,shopId,userId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

 public Announcement getLastAnnouncementByToday() throws ParseException {
   Session session = this.getSession();
   try {
     Query q = SQL.getLastAnnouncementByToday(session);
     return (Announcement)q.uniqueResult();
   } finally {
     release(session);
   }
  }

   public Festival getLastFestivalByToday() throws ParseException {
   Session session = this.getSession();
   try {
     Query q = SQL.getLastFestivalByToday(session);
     return (Festival)q.uniqueResult();
   } finally {
     release(session);
   }
  }

  public List<Festival> getCurrentFestivals() throws ParseException {
   Session session = this.getSession();
   try {
     Query q = SQL.getCurrentFestivals(session);
     return q.list();
   } finally {
     release(session);
   }
  }

  public UserReadRecord getUserReadRecord(ReminderType type,Long shopId,Long userId){
    Session session = this.getSession();
    try {
      Query q = SQL.getUserReadRecord(session, type, shopId, userId);
      return (UserReadRecord)q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<PromotionMsgJob> getCurrentPromotionMsgJobDTO(){
    Session session = this.getSession();
    try {
      Query q = SQL.getCurrentPromotionMsgJobDTO(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InvitationCode> getInvitationCode(int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInvitationCode(session, start, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InvitationCode> getInvitationCode(int pageSize, long maxId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInvitationCode(session, pageSize, maxId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<InvitationCodeRecycle> getInvitationCodeRecycle(int start, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getInvitationCodeRecycle(session, start, pageSize);
      return q.list();
    } finally {
      release(session);
    }
  }

  public InvitationCodeDTO findEffectiveInvitationCodeByCode(String code) {
    Session session = this.getSession();
    try {
      Query q = SQL.findEffectiveInvitationCodeByCode(session, code);
      InvitationCode invitationCode = (InvitationCode) q.uniqueResult();
      if (invitationCode == null) {
        LOG.warn("can't find InvitationCode by code:{}", code);
        return null;
      }
      return invitationCode.toDto();
    } finally {
      release(session);
    }
  }

  public InvitationCodeDTO findInvitationCodeByCode(String code) {
    Session session = this.getSession();
    try {
      Query q = SQL.findInvitationCodeByCode(session, code);
      InvitationCode invitationCode = (InvitationCode) q.uniqueResult();
      if (invitationCode == null) {
        LOG.warn("can't find InvitationCode by code:{}", code);
        return null;
      }
      return invitationCode.toDto();
    } finally {
      release(session);
    }
  }

  public Long countEffectiveInvitationCodeByCode(List<String> codes) {
    Session session = this.getSession();
    try {
      Query q = SQL.countEffectiveInvitationCodeByCode(session, codes);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SmsSendingTimes> getSmsSendingTimesByMobiles(Set<String> mobiles) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsSendingTimesByMobiles(session, mobiles);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutBox> getOutBox(int pageSize, long startId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutBox(session,pageSize, startId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SmsJob> getSmsJobsBySmsId(Long smsId, Long id, int limit, int sendTimes) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsJobsBySmsId(session, smsId, id, limit, sendTimes);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<SmsJob> getSmsJobsBySmsId(Long shopId,Long... smsIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getSmsJobsBySmsId(session,shopId,smsIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutBox> getAllOutBox() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllOutBox(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutBox> getOutBoxByPager(int pageSize, int startPageNo) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutBoxByPager(session,pageSize,startPageNo);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<OutBox> getOutBoxByStatStatus(int pageSize, StatStatus statStatus) {
    Session session = this.getSession();
    try {
      Query q = SQL.getOutBoxByStatStatus(session,pageSize,statStatus);
      return q.list();
    } finally {
      release(session);
    }
  }

   public WXSendStatusReport getWXSendStatusReportByMsgId(String msgId){
    Session session = this.getSession();
    try {
      Query q = SQL.getWXSendStatusReportByMsgId(session, msgId);
      return (WXSendStatusReport)q.uniqueResult();
    } finally {
      release(session);
    }
  }
}
