package com.bcgogo.schedule.payment;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.payment.IChinapayCheckService;
import com.bcgogo.txn.service.pushMessage.IAppointPushMessageService;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Author: zhangjuntao
 * Date: 12-6-5
 *自动定时钟(1个小时轮询一次)
 */
public class ChinapayQuerySchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(ChinapayQuerySchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processQueryJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  public void processQueryJobs() {
    if (isLock()) {
      return;
    }

    try {
      checkChinaPayByShopIdAndTime();
      createOverdueAppointRemindMessage();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }

  //china pay
  private void checkChinaPayByShopIdAndTime() {
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String paymentQueryTag = configService.getConfig("PaymentQueryTag", ShopConstant.BC_SHOP_ID);
      if (paymentQueryTag == null || (paymentQueryTag != null && !paymentQueryTag.trim().equals("on"))) {
        return;
      }
      IChinapayCheckService chinapayCheckService = ServiceManager.getService(IChinapayCheckService.class);

      LOG.info("PaymentQuerySchedule DateTime:" + DateUtil.dateLongToStr(System.currentTimeMillis()) + ".............");
      chinapayCheckService.checkChinaPayByShopIdAndTime(null, System.currentTimeMillis() - NumberUtil.DAY_TIMEMILLIS);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  //创建过期预约单提醒消息
  private void createOverdueAppointRemindMessage() {
    try {
      LOG.info("OverdueAppointRemind DateTime:" + DateUtil.dateLongToStr(System.currentTimeMillis()) + ".............");
      ServiceManager.getService(IAppointPushMessageService.class).createOverdueAppointRemindMessage(1000);
      List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
      for (ShopDTO shopDTO : shopDTOList) {
        List<Long> userIds = ServiceManager.getService(IUserCacheService.class).getUserIdsByShopId(shopDTO.getId());
        if(CollectionUtils.isNotEmpty(userIds)){
          for(Long userId : userIds){
            ServiceManager.getService(IPushMessageService.class).updatePushMessageCategoryStatNumberInMemCache(shopDTO.getId(), userId, PushMessageCategory.values());
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
