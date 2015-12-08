package com.bcgogo.schedule.bean;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.notification.SmsType;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.notification.dto.SmsSendResult;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.notification.smsSend.SmsYiMeiSenderMock;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.txn.service.pushMessage.ICustomPushMessageService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.*;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmsSendSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(SmsSendSchedule.class);
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processSmsJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }


  public void processSmsJobs() {
    if (isLock()) {
      LOG.warn("SmsSendSchedule isLock!");
      return;
    }
    try {
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String smsTag = configService.getConfig("smsTag", ShopConstant.BC_SHOP_ID);
      if (smsTag == null || (!smsTag.trim().equals("on"))) {
        return;
      }
      if (LOG.isDebugEnabled()) LOG.debug("SMSSendSchedule.............");
      List<SmsJob> jobs = notificationService.getSmsJobsByStartTime(getCurrentTime());//得到将要发送的短信
      while (CollectionUtil.isNotEmpty(jobs)) {
        LOG.debug("number of sms to send: {}", jobs.size());
        LOG.debug("SmsJobs : " + jobs.toString());
        for (SmsJob job : jobs) {
          if (job == null) {
            if (LOG.isDebugEnabled()) LOG.debug("SmsJob is null !");
            continue;
          }
          try {
            SmsJobDTO smsJobDTO = job.toDTO();
            if(SmsSendScene.APP_MESSAGE.equals(smsJobDTO.getSmsSendScene())){  //发送APP消息
              if(StringUtil.isNotEmpty(smsJobDTO.getAppUserNo())){
                ServiceManager.getService(ICustomPushMessageService.class).createCustomPushMessage2App(smsJobDTO.getContent(),smsJobDTO.getAppUserNo(),smsJobDTO.getShopId());
              }else if(smsJobDTO.getCustomerId()!=null){
                Set<Long> customerIdSet=new HashSet<Long>();
                customerIdSet.add(smsJobDTO.getCustomerId());
                ServiceManager.getService(ICustomPushMessageService.class).createCustomPushMessage2App(customerIdSet,smsJobDTO.getContent());
              }
              notificationService.deleteSmsJobById(smsJobDTO.getId());
            }else {              //实时发送
              notificationService.sendSmsSync(smsJobDTO);
              //保存记录
              saveShopSmsRecord(smsJobDTO);
            }
            //更新sms发送状态
            updateSmsStatus(smsJobDTO);
          } catch (SmsException e) {
            LOG.error("执行定时钟单个短信发送失败,失败原因:", e);
          }
        }
        jobs = notificationService.getSmsJobsByStartTime(getCurrentTime());//得到将要发送的短信
      }

    } catch (Exception e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }

  private Long currentTime = null;

  public Long getCurrentTime() {
    if (currentTime == null) return System.currentTimeMillis();
    return currentTime;
  }

  public void setCurrentTime(Long currentTime) {
    this.currentTime = currentTime;
  }

  private void saveShopSmsRecord(SmsJobDTO job) {
    if(!SenderType.bcgogo.equals(job.getSender()) && SmsConstant.SMS_STATUS_SUCCESS.equals(job.getStatus())) {
      if (StringUtils.isBlank(job.getShopName())) {
        job.setShopName(SmsConstant.SmsYiMeiConstant.DEFAULT_SENDER_NAME);
      }
      long shopNumber = (long) SmsUtil.calculateSmsNum(job.getContent() + "【" + job.getShopName() + "】", job.getReceiveMobile());
      ShopSmsRecordDTO shopSmsRecordDTO = ServiceManager.getService(ISmsAccountService.class).getShopSmsRecordBySmsId(job.getShopId(), job.getSmsId());
      if(shopSmsRecordDTO == null) {
        shopSmsRecordDTO = new ShopSmsRecordDTO();
        shopSmsRecordDTO.fromSmsJob(job);
        shopSmsRecordDTO.setNumber(shopNumber);
        shopSmsRecordDTO.setBalance(shopNumber / 10.0);
        SmsDTO smsDTO = ServiceManager.getService(INotificationService.class).getSmsDTOById(job.getShopId(),job.getSmsId());
        if(smsDTO != null) {
          shopSmsRecordDTO.setSmsSendScene(smsDTO.getSmsSendScene());
        }
      } else {
        shopSmsRecordDTO.setNumber(NumberUtil.longValue(shopSmsRecordDTO.getNumber()) + shopNumber);
        shopSmsRecordDTO.setBalance(NumberUtil.doubleVal(shopSmsRecordDTO.getBalance()) + shopNumber / 10.0);
      }
      ServiceManager.getService(ISmsAccountService.class).saveOrUpdateShopSmsRecord(shopSmsRecordDTO);
    }
  }

  private void updateSmsStatus(SmsJobDTO job) {
    if(job==null||job.getShopId()==null||job.getSmsId()==null) return;
    INotificationService notificationService=ServiceManager.getService(INotificationService.class);
    SmsDTO smsDTO=notificationService.getSmsDTOById(job.getShopId(),job.getSmsId());
    if(smsDTO!=null){
      smsDTO.setSmsType(SmsType.SMS_SENT);
      notificationService.saveOrUpdateSms(smsDTO);
    }
  }

}
