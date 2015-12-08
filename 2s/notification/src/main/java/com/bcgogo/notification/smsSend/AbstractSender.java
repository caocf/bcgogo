package com.bcgogo.notification.smsSend;

import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBalanceService;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.notification.dto.SmsSendResult;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-28
 * Time: 下午3:24
 * To change this template use File | Settings | File Templates.
 */
//短信发送抽象类
public abstract class AbstractSender {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractSender.class);
  //发送抽象方法
  public abstract SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws Exception;

  //发送成功 更新SmsBalance
  public void updateSmsBalanceAfterSendSuccess(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) {
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    if(smsSendDTO.getShopId()==null){
      LOG.error("shopId is null");
      return;
    }
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(smsSendDTO.getShopId());
    if (shopBalanceDTO == null) {
      LOG.error("shopBalance is null");
      return;
    }
    shopBalanceDTO.setSmsBalance(shopBalanceDTO.getSmsBalance() - smsSendResult.getSmsPrice());
    shopBalanceService.updateSmsBalance(shopBalanceDTO);

  }

  //发送前账户是否 够发送短信
  public Boolean validateSendSms(Long shopId, double totalPrice) {
    IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
    ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(shopId);
    if (shopBalanceDTO != null && (shopBalanceDTO.getSmsBalance() - totalPrice > 0)) return true;
    return false;
  }

  //发送前账户验证
  public SmsSendResult validateSendSms(SmsSendDTO smsSendDTO) {
    SmsSendResult smsSendResult = new SmsSendResult();
    if (SenderType.bcgogo == smsSendDTO.getSender()) {
      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
    } else {
      IShopBalanceService shopBalanceService = ServiceManager.getService(IShopBalanceService.class);
      ShopBalanceDTO shopBalanceDTO = shopBalanceService.getSmsBalanceByShopId(smsSendDTO.getShopId());
      if (shopBalanceDTO == null) {//帐号不存在
        if (LOG.isDebugEnabled()) {
          LOG.debug("shopId:"+smsSendDTO.getShopId() + SmsConstant.SMS_STATUS_ACT_NOT_EXIST);
        }
        smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
        smsSendResult.setSmsResponseReason(SmsConstant.SMS_STATUS_ACT_NOT_EXIST);
      } else if (shopBalanceDTO.getSmsBalance() - SmsConstant.DEFAULT_SMS_DEBT <= 0) {
        if (LOG.isDebugEnabled()) {   //余额不足
          LOG.debug(smsSendDTO.getShopId() + SmsConstant.SMS_STATUS_LOW_BALANCE);
        }
        smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
        smsSendResult.setSmsResponseReason(SmsConstant.SMS_STATUS_LOW_BALANCE);
      } else if (StringUtil.isEmpty(smsSendDTO.getReceiveMobile()) || StringUtil.isEmpty(smsSendDTO.getContent())) {
        if (LOG.isDebugEnabled()) {   //数据不完整
          LOG.debug(smsSendDTO.getShopId() + SmsConstant.SMS_STATUS_INCOMPLETE);
        }
        smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
        smsSendResult.setSmsResponseReason(SmsConstant.SMS_STATUS_INCOMPLETE);
      } else {
        smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
      }
    }
    smsSendResult.setContent(smsSendDTO.getContent());
    smsSendResult.setSmsSendKind(smsSendDTO.getSmsSendKind());
    return smsSendResult;
  }

  //计算短信条数
  public int caculateSmsNum(String content, String mobiles) {
    if (StringUtil.isEmpty(content)) {
      return 0;
    }
    String[] receiveMobiles = mobiles.split(",");
    int num = receiveMobiles.length;
    int length = content.length();
    int smsCount = (int) Math.ceil(length / (SmsConstant.SMS_UNIT_LENGTH * 1.0));
    return smsCount * num;
  }

  //计算短信条数
  public int caculateSmsNum(String content, String... mobiles) {
    if (StringUtil.isEmpty(content)) {
      return 0;
    }
    int num = mobiles.length;
    int length = content.length();
    int smsCount = (int) Math.ceil(length / (SmsConstant.SMS_UNIT_LENGTH * 1.0));
    return smsCount * num;
  }

  public IConfigService configService;

  public IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }

  protected void groupSmsSends(SmsSendDTO smsSendDTO, Integer mobileLimit, String separator) {
    if (smsSendDTO.getSmsId() == null) {
      smsSendDTO.getSendSmsJobIds().add(smsSendDTO.getId());
      return;
    }
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    List<SmsJob> smsJobs = notificationService.getSmsJobsBySmsId(NumberUtil.longValue(smsSendDTO.getSmsId()), smsSendDTO.getId(),smsSendDTO.getSendTimes(), mobileLimit - 1);
    List<String> mobiles = new ArrayList<String>();
    mobiles.add(smsSendDTO.getReceiveMobile());
    smsSendDTO.getSendSmsJobIds().add(smsSendDTO.getId());
    for (SmsJob job : smsJobs) {
      smsSendDTO.getSendSmsJobIds().add(job.getId());
      mobiles.add(job.getReceiveMobile());
    }
    smsSendDTO.setReceiveMobile(StringUtils.join(mobiles, separator));
  }

  public static void main(String[] args){
        List<String> mobiles=new ArrayList<String>();
    mobiles.add("12312,12123");
    mobiles.add("12312");
    System.out.print(StringUtils.join(mobiles, ","));

  }

}
