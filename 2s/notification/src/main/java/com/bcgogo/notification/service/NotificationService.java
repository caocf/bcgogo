package com.bcgogo.notification.service;


import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.StatStatus;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.BcgogoExceptionType;
import com.bcgogo.notification.cache.AnnouncementManager;
import com.bcgogo.notification.cache.FestivalManager;
import com.bcgogo.notification.dto.*;
import com.bcgogo.notification.model.*;
import com.bcgogo.notification.reminder.ReminderType;
import com.bcgogo.notification.smsSend.AbstractSender;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.notification.smsSend.SmsSenderFactory;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionMsgJobDTO;
import com.bcgogo.user.dto.AnnouncementDTO;
import com.bcgogo.user.dto.FestivalDTO;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
public class NotificationService implements INotificationService {
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  @Autowired
  private com.bcgogo.notification.model.NotificationDaoManager notificationDaoManager;

  @Override
  public void batchSaveSmsJob(List<SmsJob> smsJobs) {
    if (CollectionUtil.isEmpty(smsJobs)) return;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (SmsJob smsJob : smsJobs) {
        writer.save(smsJob);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }



  //把job转换成若干组
  private List<SmsJob> transformSmsJob(SmsJob job) throws SmsException {
    List<SmsJob> smsJobs = new ArrayList<SmsJob>();
    if (job == null) return smsJobs;
    job.setReceiveMobile(SmsUtil.filterMobiles(job.getReceiveMobile()));
    if (StringUtils.isBlank(job.getReceiveMobile())) return smsJobs;
    StringBuilder mobiles = new StringBuilder();
    String[] mobileArray = job.getReceiveMobile().split(",");
    for (int i = 0, num = 0, max = mobileArray.length; i < max; ) {
      mobiles.append(mobileArray[i]);
      if(i<max-1){
        mobiles.append(",");
      }
      num++;
      i++;
      if (num == SmsConstant.SMS_SEND_MOBILES_MAX_NUMBER || i == max
              || (mobiles.length() >= SmsConstant.SMS_SEND_MOBILES_MAX_LENGTH)) {
        try {
          SmsJob smsJob = job.clone();
          if(mobiles.toString().trim().endsWith(",")) {
            mobiles.delete(mobiles.length() - 1,mobiles.length());
          }
          smsJob.setReceiveMobile(mobiles.toString().trim());
          smsJobs.add(smsJob);
          mobiles.delete(0, mobiles.length() - 1);
          num = 0;
        } catch (Exception e) {
          throw new SmsException("notificationService transformSmsJob is error.");
        }
      }
    }
    return smsJobs;
  }

  @Override
  public Long saveOrUpdateSms(SmsDTO smsDTO){
    if(smsDTO==null) return null;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    Long smsId=null;
    try {
      if(smsDTO.getId()==null){
        Sms sms=new Sms();
        sms.fromDTO(smsDTO);
        sms.setEditDate(smsDTO.getEditDate()==null?System.currentTimeMillis():smsDTO.getEditDate());
        writer.save(sms);
        writer.commit(status);
        smsId=sms.getId();
      }else{
        Sms sms=CollectionUtil.getFirst(writer.getSmsByIds(smsDTO.getShopId(),smsDTO.getId()));
        sms.fromDTO(smsDTO);
        sms.setEditDate(System.currentTimeMillis());
        writer.update(sms);
        writer.commit(status);
        smsId=sms.getId();
      }
      smsDTO.setId(smsId);
      return smsId;
    }finally {
      writer.rollback(status);
    }
  }

   @Override
  public SmsIndexDTO getSmsIndexDTOBySmsId(Long shopId,Long smsId){
     NotificationWriter writer = notificationDaoManager.getWriter();
       SmsIndex smsIndex=writer.getSmsIndexBySmsId(shopId,smsId);
     return smsIndex!=null?smsIndex.toDTO():null;
   }

  @Override
  public Long saveOrUpdateSmsIndex(SmsIndexDTO smsIndexDTO){
    if(smsIndexDTO==null) return null;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    Long smsIndexId=null;
    try {
      if(smsIndexDTO.getId()==null){
        SmsIndex smsIndex=new SmsIndex();
        smsIndex.fromDTO(smsIndexDTO);
        writer.save(smsIndex);
        smsIndexId=smsIndex.getId();
      }else{
       SmsIndex smsIndex=writer.getById(SmsIndex.class,smsIndexDTO.getId());
        smsIndex.fromDTO(smsIndexDTO);
        writer.update(smsIndex);
        smsIndexId=smsIndex.getId();
      }
      writer.commit(status);
      smsIndexDTO.setId(smsIndexId);
      return smsIndexId;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateSmsJob(List<SmsJob> smsJobs){
    if(CollectionUtil.isEmpty(smsJobs)) return;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(SmsJob smsJob:smsJobs){
        if(smsJob==null||smsJob.getId()==null) continue;
        writer.update(smsJob);
      }
      writer.commit(status);
      return ;
    }finally {
      writer.rollback(status);
    }
  }


  @Override
  public void deleteSms(Long shopId,Long... smsIds){
    if(shopId==null||ArrayUtil.isEmpty(smsIds)) return;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SmsDTO> smsDTOList=getSmsDTOByIds(shopId,smsIds);
      if(CollectionUtil.isNotEmpty(smsDTOList)){
        for(SmsDTO smsDTO:smsDTOList){
          writer.delete(Sms.class,smsDTO.getId());
        }
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public SmsDTO getSmsDTOById(Long shopId,Long smsId){
    if(smsId==null) return null;
    return CollectionUtil.getFirst(getSmsDTOByIds(shopId,smsId));
  }

  @Override
  public List<SmsDTO> querySms(SmsSearchCondition condition){
    if(condition==null) return null;
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<Sms> smsList=writer.querySms(condition);
    List<SmsDTO> smsDTOList=new ArrayList<SmsDTO>();
    if(CollectionUtil.isNotEmpty(smsList)){
      for(Sms sms:smsList){
        smsDTOList.add(sms.toDTO());
      }
    }
    return smsDTOList;
  }

  @Override
  public List<SmsDTO> getSmsDTOByIds(Long shopId,Long... smsIds){
    if(ArrayUtil.isEmpty(smsIds)) return null;
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<Sms> smsList=writer.getSmsByIds(shopId,smsIds);
    List<SmsDTO> smsDTOList=new ArrayList<SmsDTO>();
    if(CollectionUtil.isNotEmpty(smsList)){
      for(Sms sms:smsList){
        if(sms==null) continue;
        smsDTOList.add(sms.toDTO());
      }
    }
    return smsDTOList;
  }

  @Override
  public void sendSmsAsync(SmsJobDTO smsJobDTO) throws SmsException {
    if (smsJobDTO == null) throw new SmsException("sms job is null.");
    if (smsJobDTO.getSender() == null) throw new SmsException("sms sender is null.");
    if(!SmsSendScene.APP_MESSAGE.equals(smsJobDTO.getSmsSendScene())){
      if (StringUtils.isBlank(smsJobDTO.getReceiveMobile())) throw new SmsException("mobile can't be empty");
    }
    if (smsJobDTO.getStartTime() == null) {
      smsJobDTO.setStartTime(System.currentTimeMillis());
    }
    SmsJob job = new SmsJob();
    job.fromDTO(smsJobDTO);
    batchSaveSmsJob(transformSmsJob(job));
  }

  @Override
  public void batchSendSmsAsync(List<SmsJobDTO> smsJobDTOs) throws SmsException {
    if (CollectionUtil.isEmpty(smsJobDTOs)) return;
    SmsJob job = new SmsJob();
    List<SmsJob> jobList = new ArrayList<SmsJob>();
    for (SmsJobDTO dto : smsJobDTOs) {
//      dto.setStartTime(System.currentTimeMillis());
      jobList.addAll(transformSmsJob(job.fromDTO(dto)));
    }
    batchSaveSmsJob(jobList);
  }

  @Override
  public void sendSmsSync(SmsJobDTO job) throws SmsException {
    if (job == null) {
      LOG.warn("sms job is null.");
      return;
    }
    SmsSendResult smsSendResult=null;
    try {
      if (job.getSender() == null) throw new SmsException("sms sender is null.");
      if (StringUtils.isBlank(job.getReceiveMobile())) throw new SmsException("mobile is empty.");
      //多运营商 按结果 循环发送
      smsSendResult = this.sendSmsInternal(job);
      if (smsSendResult == null) return;
      if (LOG.isDebugEnabled()) {
        LOG.debug("短信发送结果（sendSmsSync）：{}.", smsSendResult.toString());
      }
      if (SmsConstant.SMS_STATUS_SUCCESS.equals(smsSendResult.getSmsResponse())) {
        job.setSmsSendId(smsSendResult.getSmsId());
        job.setStatus(SmsConstant.SMS_STATUS_SUCCESS);
      } else {
        job.setReponseReason(smsSendResult.getSmsResponseReason());
        job.setStatus(smsSendResult.getSmsResponse());
        job.setSmsSendId(smsSendResult.getSmsId());
//        job.setReceiveMobile(smsSendResult.getFailMobiles());
      }
//      job.setSmsId(NumberUtil.longValue(smsSendResult.getSmsId()));
      job.setSendSmsJobIds(smsSendResult.getSendSmsJobIds());
      job.setSmsSendKind(smsSendResult.getSmsSendKind());
    } catch (SmsException e) {
      job.setStatus(SmsConstant.SMS_STATUS_FAIL);
      LOG.error("执行定时钟单个短信发送失败,失败原因:", e);
    } finally {
      postProcessAfterSend(job, smsSendResult);
    }

  }



  @Override
  public List<SmsJob> getSmsJobsByStartTime(long startTime) {
    return getSmsJobsByStartTime(startTime, 10);
  }

  private List<SmsJob> getSmsJobsByStartTime(long startTime, Integer limit) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    List<SmsJob> smsJobs = notificationWriter.getSmsJobsByStartTime(startTime, SmsChannel.INDUSTRY, false, limit);
    smsJobs.addAll(notificationWriter.getSmsJobsByStartTime(startTime, SmsChannel.MARKETING, true, limit));
    Collections.sort(smsJobs, new Comparator<SmsJob>() {
      @Override
      public int compare(SmsJob pou1, SmsJob pou2) {
        try {
          // 从大到小顺序
          return pou2.getStartTime().compareTo(pou1.getStartTime());
        } catch (Exception e) {
          return -1;
        }
      }
    });
    if (smsJobs.size() > limit) {
      return smsJobs.subList(0, limit);
    }
    return smsJobs;
  }

  @Override
  public List<SmsJobDTO> getSmsJobsByShopId(long shopId, int pageNo, int pageSize) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    List<SmsJob> smsJobs = notificationWriter.getSmsJobsByShopId(shopId, pageNo, pageSize);
    List<SmsJobDTO> smsJobDTOs = new ArrayList<SmsJobDTO>();
    for (SmsJob smsJob : smsJobs) {
      SmsJobDTO smsJobDTO = smsJob.toDTO();
      smsJobDTOs.add(smsJobDTO);
    }
    return smsJobDTOs;
  }



  @Override
  public List<InBoxDTO> getShopInBoxs(long shopId, int pageNo, int pageSize) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    List<InBoxDTO> inBoxDTOList = new ArrayList<InBoxDTO>();

    for (InBox inBox : notificationWriter.getShopInBoxs(shopId, pageNo, pageSize)) {
      inBoxDTOList.add(inBox.toDTO());
    }

    return inBoxDTOList;
  }

  public List<OutBoxDTO> getOutBoxByShopAndMobile(long shopId, String mobile, int pageNo, int pageSize) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    List<OutBoxDTO> outBoxDTOList = new ArrayList<OutBoxDTO>();

    for (OutBox outBox : notificationWriter.getOutBoxByShopAndMobile(shopId, mobile, pageNo, pageSize)) {
      outBoxDTOList.add(outBox.toDTO());
    }

    return outBoxDTOList;
  }


  @Override
  public List<OutBoxDTO> getShopOutBoxs(long shopId, int pageNo, int pageSize) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    List<OutBoxDTO> outBoxDTOList = new ArrayList<OutBoxDTO>();

    for (OutBox outBox : notificationWriter.getShopOutBoxs(shopId, pageNo, pageSize)) {
      outBoxDTOList.add(outBox.toDTO());
    }

    return outBoxDTOList;
  }

  @Override
  public List<SmsDTO> getSmsDTOList(SmsSearchCondition condition){
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    List<SmsDTO> smsDTOList=new ArrayList<SmsDTO>();
    List<Sms> smsList=notificationWriter.getSmsList(condition);
    if(CollectionUtil.isNotEmpty(smsList)){
      for(Sms sms:smsList){
        if(sms==null) continue;
        smsDTOList.add(sms.toDTO());
      }
    }
    return smsDTOList;
  }

  @Override
  public int countSms(SmsSearchCondition condition){
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.countSms(condition);
  }

  public int countShopOutBox(long shopId) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.countShopOutBox(shopId);
  }

  public int countOutBoxNumberByShopIdAndMobile(long shopId, String mobile) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.countOutBoxNumberByShopIdAndMobile(shopId, mobile);
  }

  public int countShopInBox(long shopId) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.countShopInBox(shopId);
  }

  public List<OutBox> getOutBox(int pageSize,long startId){
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.getOutBox(pageSize,startId);
  }

  public int countShopSmsJobs(long shopId) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.countShopSmsJobs(shopId);
  }


  private void postProcessAfterSend(SmsJobDTO smsJobDTO, SmsSendResult smsSendResult) throws SmsException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {

      for (Long id : smsJobDTO.getSendSmsJobIds()) {
        //实时短信 逻辑
        if (id == null) {
          if (smsJobDTO.getStatus().equals(SmsConstant.SMS_STATUS_SUCCESS)) {
            OutBox outBox = new OutBox(smsJobDTO);
            if (smsSendResult != null)
              outBox.setContent(smsSendResult.getContent());
            writer.save(outBox);
			      smsJobDTO.setStatus(SmsConstant.SMS_STATUS_SUCCESS);
          } else {
            SmsJob smsJob = new SmsJob();
            smsJob.fromDTO(smsJobDTO);
            smsJob.setSendTimes(1);
            smsJob.setLastSendTime(System.currentTimeMillis());
            String reason = SmsUtil.accumulateSmsJobResponseReasons(smsJob.getReponseReason(), smsJobDTO.getReponseReason());
            smsJob.setReponseReason(reason);
            smsJob.setStartTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY);
            writer.save(smsJob);
          }
          continue;
        }
        SmsJob smsJob = writer.getById(SmsJob.class, id);
        if (smsJob == null) return;
        smsJob.setSendTimes(smsJob.getSendTimes() + 1);
        smsJob.setLastSendTime(System.currentTimeMillis());
        String reason = SmsUtil.accumulateSmsJobResponseReasons(smsJob.getReponseReason(), smsJobDTO.getReponseReason());
        smsJob.setReponseReason(reason);

        smsJobDTO.setReceiveMobile(smsJob.getReceiveMobile());
        smsJobDTO.setShopId(smsJob.getShopId());
        smsJobDTO.setLastSendTime(smsJob.getLastSendTime());
        smsJobDTO.setSendTimes(smsJob.getSendTimes());

        if (smsJobDTO.getStatus().equals(SmsConstant.SMS_STATUS_SUCCESS)) {
          OutBox outBox = new OutBox(smsJobDTO);
          if (smsSendResult != null)
            outBox.setContent(smsSendResult.getContent());
          writer.save(outBox);
          writer.delete(SmsJob.class, id);
          smsJobDTO.setStatus(SmsConstant.SMS_STATUS_SUCCESS);
        } else if (smsJobDTO.getSendTimes() <= SmsConstant.SMS_RETRY_TIMES) {
          smsJob.setStatus(smsJobDTO.getStatus());
          smsJob.setStartTime(System.currentTimeMillis() + SmsConstant.SMS_FAILED_DELAY);
          writer.save(smsJob);
        } else {
          writer.delete(SmsJob.class, id);
          FailedSmsJob failedSmsJob = new FailedSmsJob(smsJobDTO);
          failedSmsJob.setReponseReason(reason);
          writer.save(failedSmsJob);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public void deleteSmsJobById(Long jobId){
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.delete(SmsJob.class,jobId);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 短信发送
   *
   * @param job 发送消息
   * @return smsSendResult    返回结果
   */
  private SmsSendResult sendSmsInternal(SmsJobDTO job) {
    SmsSendDTO smsSendDTO = job.toSmsSendDTO();
    SmsSendResult smsSendResult = new SmsSendResult();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String mockSms = configService.getConfig("MOCK_SMS", ShopConstant.BC_SHOP_ID);
    AbstractSender smsSender = null;
    String smsSenderStrategyStr = null;

    //行业和营销分开配置,行业调用亿美接口，营销调用联逾接口
    if(SmsChannel.INDUSTRY.equals(job.getSmsChannel())){
      smsSenderStrategyStr = configService.getConfig("SmsIndustrySenderStrategy", ShopConstant.BC_SHOP_ID);
    }else if(SmsChannel.MARKETING.equals(job.getSmsChannel())){
      smsSenderStrategyStr = configService.getConfig("SmsMarketingSenderStrategy", ShopConstant.BC_SHOP_ID);
    }


//    String smsSenderStrategyStr = configService.getConfig("SmsSenderStrategy", ShopConstant.BC_SHOP_ID);
    if (StringUtils.isBlank(smsSenderStrategyStr)) return smsSendResult;
    String[] smsSendStrategy = smsSenderStrategyStr == null ? null : smsSenderStrategyStr.split(",");
    String smsResponseReason = "";
    //多运营商 按结果 循环发送
    if (ArrayUtils.isEmpty(smsSendStrategy)) {
      smsSendResult.setSmsResponseReason("smsSendStrategy is empty!");
      return smsSendResult;
    }
    for (String strategy : smsSendStrategy) {
      try {
        if (LOG.isInfoEnabled()) LOG.info("sms smsSendStrategy {}", smsSendStrategy);
        if (LOG.isInfoEnabled()) LOG.info("MOCK_SMS is {}", mockSms);
        if (StringUtils.isNotBlank(mockSms) && mockSms.equals("off")) {
          smsSender = SmsSenderFactory.getSmsSenderByName(strategy);
        } else {
          smsSender = SmsSenderFactory.getSmsSenderMockByName(strategy);
        }
        smsSendResult = smsSender.sendSms(smsSendDTO);
        if (smsSendResult.isSuccess()) {//判断是否发送成功
          break;
        }
        //把每次失败返回的内容保存
        smsResponseReason += "[" + strategy + ":" + smsSendResult.getSmsResponseReason() + "],";
      } catch (Exception e) {
        LOG.error("[sendSms:" + e.getMessage() + "]", e);
      }
    }
    smsResponseReason = StringUtil.subString(smsResponseReason);
    smsSendResult.setSmsResponseReason(smsResponseReason);
    return smsSendResult;
  }

  //按公司名指定运营商发送
  public SmsSendResult sendSms(SmsSendDTO smsSendDTO, String name) throws Exception {
    AbstractSender senders = SmsSenderFactory.getSmsSenderByName(name);
    SmsSendResult smsSendResult = senders.sendSms(smsSendDTO);
    return smsSendResult;
  }

  public MessageTemplate getMsgTemplateByType(String type) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.getMsgTemplateByType(type);
  }

  @Override
  public MessageTemplateDTO getShopMsgTemplateDTOById(Long shopId,Long templateId){
    if(shopId==null||templateId==null) return null;
    NotificationWriter notificationWriter=notificationDaoManager.getWriter();
    MessageTemplate template=notificationWriter.getShopMsgTemplateById(shopId,templateId);
    return template!=null?template.toDTO():null;
  }

  @Override
  public List<MessageTemplateDTO> getShopMsgTemplateDTOByName(Long shopId,String name){
    if(shopId==null||StringUtil.isEmpty(name)) return null;
    NotificationWriter notificationWriter=notificationDaoManager.getWriter();
    List<MessageTemplate> templateList=notificationWriter.getShopMsgTemplateByName(shopId,name);
    List<MessageTemplateDTO> templateDTOList=new ArrayList<MessageTemplateDTO>();
    if(CollectionUtil.isNotEmpty(templateList)){
      for(MessageTemplate template:templateList){
        templateDTOList.add(template.toDTO());
      }
    }
    return templateDTOList;
  }

  @Override
  public List<MessageTemplateDTO> getShopMsgTemplate(Long shopId,String keyWord,Pager pager){
    NotificationWriter notificationWriter=notificationDaoManager.getWriter();
    List<MessageTemplate> templateList=notificationWriter.getShopMsgTemplate(shopId,keyWord,pager);
    List<MessageTemplateDTO> templateDTOs=new ArrayList<MessageTemplateDTO>();
    if(CollectionUtil.isNotEmpty(templateList)){
      for (MessageTemplate template:templateList){
        templateDTOs.add(template.toDTO());
      }
    }
    return templateDTOs;
  }

  @Override
  public Long saveShopMsgTemplate(MessageTemplateDTO templateDTO) throws BcgogoException {
    NotificationWriter writer=notificationDaoManager.getWriter();
    if(templateDTO.getShopId()==null) throw new BcgogoException(BcgogoExceptionType.IllegalArgument);
    Object status=writer.begin();
    try{
      if(templateDTO.getId()==null){
        MessageTemplate template=new MessageTemplate(templateDTO);
        writer.save(template);
        templateDTO.setId(template.getId());
        writer.commit(status);
      }else{
        MessageTemplate template=writer.getById(MessageTemplate.class,templateDTO.getId());
        if(template==null) return null;
        template.fromDTO(templateDTO);
        writer.update(template);
        writer.commit(status);
      }
      return templateDTO.getId();
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteSmsTemplate(Long[] templateIds) throws BcgogoException {
    if(ArrayUtil.isEmpty(templateIds)) throw new BcgogoException(BcgogoExceptionType.IllegalArgument);
    NotificationWriter writer=notificationDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(Long templateId:templateIds){
        writer.delete(MessageTemplate.class,templateId);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  public int countShopMsgTemplate(Long shopId,String keyWord) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.countShopMsgTemplate(shopId,keyWord);
  }

  @Override
  public void setMessageTemplate(String type, String content, Long shopId) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      MessageTemplate messageTemplate = new MessageTemplate();
      messageTemplate.setContent(content);
      messageTemplate.setType(type);
      messageTemplate.setShopId(shopId);
      writer.save(messageTemplate);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 多个短信模板的场景相同，这个方式是把相同场景的模板提取出一个，封装成的DTO里面只有name,scene，status字段
   * 把不能让客户控制短信开关的模板过滤掉
   *
   * @param shopId
   * @return
   */
  @Override
  public List<MessageTemplateDTO> getDistinctSceneMessageTemplateDTOAndSwitchStatus(Long shopId) {
    NotificationWriter writer = notificationDaoManager.getWriter();

    List<MessageTemplate> messageTemplates = writer.getMessageTemplate();

    if (null == messageTemplates) {
      return null;
    }

    List<MessageTemplateDTO> messageTemplateDTOs = new ArrayList<MessageTemplateDTO>();

    Map<MessageScene, String> messageSceneStringMap = new HashMap<MessageScene, String>();

    for (MessageTemplate messageTemplate : messageTemplates) {
      //把短信合并掉
      if (null == messageSceneStringMap.get(messageTemplate.getScene())) {
        MessageTemplateDTO messageTemplateDTO = new MessageTemplateDTO();
        messageTemplateDTO.setShopId(shopId);
        messageTemplateDTO.setName(messageTemplate.getName());
        messageTemplateDTO.setScene(messageTemplate.getScene());

        MessageSwitchDTO messageSwitchDTO = this.getMessageSwitchDTOByShopIdAndScene(shopId, messageTemplateDTO.getScene());

        if (null != messageSwitchDTO) {
          messageTemplateDTO.setStatus(messageSwitchDTO.getStatus());
        } else if(MessageScene.MEMBER_CONSUME_SMS_SWITCH.equals(messageTemplateDTO.getScene())) {
          messageTemplateDTO.setStatus(MessageSwitchStatus.ON);
        }else if(MessageScene.MOBILE_APP.equals(messageTemplateDTO.getScene())){
          messageTemplateDTO.setStatus(MessageSwitchStatus.ON);
        }
        messageSceneStringMap.put(messageTemplateDTO.getScene(), messageTemplateDTO.getName());
        messageTemplateDTOs.add(messageTemplateDTO);
      }
    }

    return CollectionUtils.isNotEmpty(messageTemplateDTOs) ? messageTemplateDTOs : null;
  }

  /**
   * 根据shopId和场景查出短信开关MessageSwitchDTO
   *
   * @param shopId
   * @param scene
   * @return
   */
  @Override
  public MessageSwitchDTO getMessageSwitchDTOByShopIdAndScene(Long shopId, MessageScene scene) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    MessageSwitch messageSwitch = writer.getMessageSwitchDTOByShopIdAndScene(shopId, scene);
    if (null == messageSwitch) {
      return null;
    }
    return messageSwitch.toDTO();
  }

  /**
   * 每次在界面改变开关的时候更新或者保存MessageSwitch并且保存更新MemCache
   *
   * @param shopId
   * @param scene
   * @param status
   * @return
   */
  public MessageSwitchDTO SaveOrUpdateMessageSwitch(Long shopId, MessageScene scene, MessageSwitchStatus status) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object newStatus = writer.begin();
    MessageSwitch messageSwitch = writer.getMessageSwitchDTOByShopIdAndScene(shopId, scene);
    try {
      if (null != messageSwitch) {
        messageSwitch.setStatus(status);
        writer.update(messageSwitch);
        MemCacheAdapter.set(MemcachePrefix.messageSwitch.toString() + scene.toString() + shopId, status);
      } else {
        messageSwitch = new MessageSwitch();
        messageSwitch.setStatus(status);
        messageSwitch.setScene(scene);
        messageSwitch.setShopId(shopId);
        writer.save(messageSwitch);
        MemCacheAdapter.set(MemcachePrefix.messageSwitch.toString() + scene.toString() + shopId, status);
      }
      writer.commit(newStatus);
      return messageSwitch.toDTO();
    } finally {
      writer.rollback(newStatus);
    }
  }

  /**
   * 查询所有公告，content字段较大，缓存不取
   *
   * @param announcementIndex
   * @return
   */
  public List<AnnouncementDTO> getAnnouncementDTOs(AnnouncementDTO announcementIndex) throws ParseException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<AnnouncementDTO> announcementDTOs = new ArrayList<AnnouncementDTO>();
    for (Announcement announcement : writer.getAnnouncements(announcementIndex)) {
      if (announcement == null) continue;
      announcementDTOs.add(announcement.toDTO());
    }
    return announcementDTOs;
  }

  public List<FestivalDTO> getFestivalDTOs(FestivalDTO festivalIndex) throws ParseException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<FestivalDTO> festivalDTOs = new ArrayList<FestivalDTO>();
    for (Festival festival : writer.getFestivals(festivalIndex)) {
      if (festival == null) continue;
      festivalDTOs.add(festival.toDTO());
    }
    return festivalDTOs;
  }

  public Integer getFestivalCount(FestivalDTO festivalDTO) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getFestivalCount(festivalDTO);
  }

  public Integer getAnnoucementCount(AnnouncementDTO announcementIndex) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getAnnoucementCount(announcementIndex);
  }

  public Long saveOrUpdateAnnouncement(AnnouncementDTO announcementDTO) throws Exception {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Announcement announcement = null;
    if (StringUtil.isNotEmpty(announcementDTO.getIdStr())) {
      announcement = writer.getById(Announcement.class, NumberUtil.longValue(announcementDTO.getIdStr()));
      if (announcement != null & ObjectStatus.DISABLED.equals(announcement.getStatus())) {
        announcementDTO.setId(null);
      }
      announcement.fromDTO(announcementDTO);
    } else {
      announcement = new Announcement();
      announcement.fromDTO(announcementDTO);
      announcement.setCreateDate(System.currentTimeMillis());
      announcement.setStatus(ObjectStatus.ENABLED);
    }
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(announcement);
      writer.commit(status);
      //更新memcache中lastReleaseDate;
      AnnouncementManager.setSynTime(ReminderType.ANNOUNCEMENT);
      return announcement.getId();
    } finally {
      writer.rollback(status);
    }
  }

  public Long saveOrUpdateFestival(FestivalDTO festivalDTO) throws Exception {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Festival festival = null;
    if (StringUtil.isNotEmpty(festivalDTO.getIdStr())) {
      festival = writer.getById(Festival.class, NumberUtil.longValue(festivalDTO.getIdStr()));
      if (festival != null & ObjectStatus.DISABLED.equals(festival.getStatus())) {
        festivalDTO.setId(null);
      }
      festival.fromDTO(festivalDTO);
    } else {
      festival = new Festival();
      festival.fromDTO(festivalDTO);
      festival.setCreateDate(System.currentTimeMillis());
      festival.setStatus(ObjectStatus.ENABLED);
    }
    Object status = writer.begin();
    try {
      writer.saveOrUpdate(festival);
      writer.commit(status);
      FestivalManager.setSynTime(ReminderType.FESTIVAL);
      return festival.getId();
    } finally {
      writer.rollback(status);
    }
  }

  public Announcement getAnnouncementById(Long announcementId) {
    if (announcementId == null) {
      return null;
    }
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getById(Announcement.class, announcementId);
  }

  public Festival getFestivalById(Long festivalId) {
    if (festivalId == null) {
      return null;
    }
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getById(Festival.class, festivalId);
  }

  /**
   * 获取最新的公告(只查找今天之前的)
   *
   * @return
   */
  public Announcement getLastAnnouncementByToday() throws ParseException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getLastAnnouncementByToday();
  }

  public Festival getLastFestivalByToday() throws ParseException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getLastFestivalByToday();
  }

  /**
   * 获取当天所有的节日
   *
   * @return
   * @throws ParseException
   */
  @Override
  public List<Festival> getCurrentFestivals() throws Exception {
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<Festival> festivals=writer.getCurrentFestivals();
    for(Festival festival:festivals){
      if(!DateUtil.isCurrentTime(festival.getReleaseDate())){
        festival.setReleaseDate(DateUtil.getStartTimeOfToday());
      }
    }
    return festivals;
  }

  public Result deleteAnnouncement(Result result, Long announcementId) throws ParseException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Announcement announcement = writer.getById(Announcement.class, announcementId);
    if (announcement == null || ObjectStatus.DISABLED.equals(announcement.getStatus())) {
      result.setSuccess(false);
      result.setMsg("公告不存在，或已经删除！");
      return result;
    }
    Object status = writer.begin();
    try {
      announcement.setStatus(ObjectStatus.DISABLED);
      writer.update(announcement);
      writer.commit(status);
      //更新memcache中lastReleaseDate;
      AnnouncementManager.setSynTime(ReminderType.ANNOUNCEMENT);
      return result;
    } finally {
      writer.rollback(status);
    }
  }

  public Result deleteFestival(Result result, Long festivalId) throws ParseException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Festival festival = writer.getById(Festival.class, festivalId);
    if (festival == null || ObjectStatus.DISABLED.equals(festival.getStatus())) {
      result.setSuccess(false);
      result.setMsg("公告不存在，或已经删除！");
      return result;
    }
    Object status = writer.begin();
    try {
      festival.setStatus(ObjectStatus.DISABLED);
      writer.update(festival);
      writer.commit(status);
      //更新memcache中lastReleaseDate;
      FestivalManager.setSynTime(ReminderType.FESTIVAL);
      return result;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 保存最新公告中的创建时间到用户读取记录
   */
  public void updateUserReadRecord(UserReadRecord readRecord) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserReadRecord lastReadRecord = writer.getUserReadRecord(readRecord.getReminderType(), readRecord.getShopId(), readRecord.getUserId());
      if (lastReadRecord != null) {
        lastReadRecord.setLastReadDate(readRecord.getLastReadDate());
        writer.update(lastReadRecord);
      } else {
        writer.save(readRecord);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public UserReadRecord getUserReadRecord(ReminderType type, Long shopId, Long userId) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getUserReadRecord(type, shopId, userId);
  }

  public List<PromotionMsgJobDTO> getCurrentPromotionMsgJobDTO(){
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<PromotionMsgJob> jobs=writer.getCurrentPromotionMsgJobDTO();
    List<PromotionMsgJobDTO> jobDTOs=new ArrayList<PromotionMsgJobDTO>();
    if(CollectionUtil.isNotEmpty(jobs)){
      for(PromotionMsgJob job:jobs){
        jobDTOs.add(job.toDTO());
      }
    }
    return jobDTOs;
  }

  public void savePromotionMsgJob(Result result,PromotionMsgJob job){
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status=writer.begin();
    try{
      writer.save(job);
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<SmsJob> getSmsJobsBySmsId(Long smsId, Long id,int sendTimes, int limit) {
    return notificationDaoManager.getWriter().getSmsJobsBySmsId(smsId, id, limit, sendTimes);
  }

  @Override
  public List<SmsJob> getSmsJobsBySmsId(Long shopId,Long... smsIds) {
    return notificationDaoManager.getWriter().getSmsJobsBySmsId(shopId,smsIds);
  }

  public void updateFinishedPromotionMsgJob(List<Long> jobIdList){
    if(CollectionUtil.isEmpty(jobIdList)){
      return;
    }
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status=writer.begin();
    try{
      for(Long jobId:jobIdList){
        PromotionMsgJob msgJob=writer.getById(PromotionMsgJob.class,jobId);
        if(msgJob==null){
          continue;
        }
        msgJob.setExeStatus(ExeStatus.FINISHED);
        writer.update(msgJob);
      }
      writer.commit(status);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<OutBox> getOutBoxByPager(int pageSize, int startPageNo) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.getOutBoxByPager(pageSize, startPageNo);
  }

  @Override
  public List<OutBox> getOutBoxByStatStatus(int pageSize, StatStatus statStatus) {
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    return notificationWriter.getOutBoxByStatStatus(pageSize, statStatus);
  }

  @Override
  public void updateOutBoxStatStatus(Set<Long> outBoxIds, StatStatus statStatus) {
    if(CollectionUtils.isEmpty(outBoxIds)) return;
    NotificationWriter notificationWriter = notificationDaoManager.getWriter();
    Object status = notificationWriter.begin();

    try {
      for(Long outBoxId : outBoxIds) {
        OutBox outBox = notificationWriter.getById(OutBox.class,outBoxId);
        outBox.setStatStatus(statStatus);
        notificationWriter.update(outBox);
      }
      notificationWriter.commit(status);
    } finally {
      notificationWriter.rollback(status);
    }
  }
}
