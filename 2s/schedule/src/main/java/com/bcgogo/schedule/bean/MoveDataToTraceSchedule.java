package com.bcgogo.schedule.bean;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.recommend.IRecommendService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MoveDataToTraceSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(MoveDataToTraceSchedule.class);
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processMoveDataToTraceJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }


  public void processMoveDataToTraceJobs() {
    if (isLock()) {
      LOG.warn("MoveDataToTraceSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("MoveDataToTraceSchedule.............");
      IRecommendService recommendService = ServiceManager.getService(IRecommendService.class);
      recommendService.moveProductRecommendToTrace();
      recommendService.movePreBuyOrderItemRecommendToTrace();
      recommendService.moveShopRecommendToTrace();
      IPushMessageService pushMessageService = ServiceManager.getService(IPushMessageService.class);
      pushMessageService.pushMessageMigration(PushMessageType.getPushMessageTypesByScheduleCreated(),5000);
      pushMessageService.pushMessageReceiverMigration(5000);
      pushMessageService.pushMessageFeedbackRecordMigration(5000);
      pushMessageService.movePushMessageReceiverRecordToTraceByPushTime(DateUtil.getTheDayTime());
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
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }


}
