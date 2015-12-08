package com.bcgogo.schedule.bean;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.notification.dto.InvitationCodeSendDTO;
import com.bcgogo.notification.invitationCode.InvitationCodeGeneratorClient;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.InvitationCodeSmsService;
import com.bcgogo.utils.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-17
 * Time: 下午8:58
 */
public class InvitationCodeSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(InvitationCodeSchedule.class);

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (LOG.isInfoEnabled()) LOG.info("InvitationCodeSchedule.............");
    //促销
    sendInvitationCodeSmsForCustomersAndSuppliers();
    //回收无效邀请码
    ServiceManager.getService(InvitationCodeGeneratorClient.class).recycleInvitationCode(null);
  }

  /**
   * 发送促销短信
   * 一定时间 如果店铺不发送bcgogo代发
   */
  private void sendInvitationCodeSmsForCustomersAndSuppliers() {
    try {
      String tag = ServiceManager.getService(IConfigService.class).getConfig("AUTOMATIC_PROMOTION_INVITATION_CODE_SMS", ShopConstant.BC_SHOP_ID);
      if (tag == null || (!tag.trim().equals("on"))) {
        LOG.info("AUTOMATIC_PROMOTION_INVITATION_CODE_SMS tag is not on!");
        return;
      }
      InvitationCodeSendDTO invitationCodeSendDTO = new InvitationCodeSendDTO();
      invitationCodeSendDTO.setSendTime(getSentTime());
      invitationCodeSendDTO.setCreateTime(getCreateTime());
      invitationCodeSendDTO.setEliminateShopIdList(eliminateShopIds());
      ServiceManager.getService(InvitationCodeSmsService.class).sendInvitationSmsForCustomersAndSuppliers(null,invitationCodeSendDTO);
    } catch (Exception e) {
      LOG.warn("邀请码促销定时钟发送失败！");
      LOG.error(e.getMessage(), e);
    }
  }

  private String addZero(int num) {
    if (num < 10) return "0" + num;
    else return String.valueOf(num);
  }

  private List<Long> eliminateShopIds() {
    String config = ServiceManager.getService(IConfigService.class).getConfig("ELIMINATE_SHOP_ID_LIST", ShopConstant.BC_SHOP_ID);
    return NumberUtil.parseLongValues(config);
  }

  //获得客户创建时间 默认10天前
  private Long getCreateTime() throws ParseException {
    String config = ServiceManager.getService(IConfigService.class).getConfig("SEND_INVITATION_CODE_BY_BCGOGO_DATE_LIMIT", ShopConstant.BC_SHOP_ID);
    Integer day = 10;
    if (StringUtil.isEmpty(config)) {
      LOG.warn("SEND_INVITATION_CODE_BY_BCGOGO_DATE_LIMIT config is empty!");
    } else {
      if (RegexUtils.isDigital(config)) {
        day = NumberUtil.intValue(config);
      } else {
        LOG.warn("SEND_INVITATION_CODE_BY_BCGOGO_DATE_LIMIT config is illegal!");
      }
    }
    return System.currentTimeMillis() - 1000 * 60 * 60 * 24 * day;
  }

  //默认9点发送
  private Long getSentTime() throws ParseException {
    String config = ServiceManager.getService(IConfigService.class).getConfig("INVITATION_CODE_PROMOTE_SMS_SEND_TIME", ShopConstant.BC_SHOP_ID);
    Integer hour = 9;
    if (StringUtil.isEmpty(config)) {
      LOG.warn("INVITATION_CODE_PROMOTE_SMS_SEND_TIME config is empty!");
    } else {
      if (RegexUtils.isDigital(config)) {
        hour = NumberUtil.intValue(config);
      } else {
        LOG.warn("INVITATION_CODE_PROMOTE_SMS_SEND_TIME config is illegal!");
      }
    }
    Calendar calendar = Calendar.getInstance();
    calendar.get(Calendar.DAY_OF_WEEK);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String dateString = year + "-" + addZero(month) + "-" + addZero(day) + " " + addZero(hour) + ":00";
    LOG.info("促销短信发送时间：" + dateString);
    return DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN, dateString);
  }
}
