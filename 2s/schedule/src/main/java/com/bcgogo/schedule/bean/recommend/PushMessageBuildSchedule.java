package com.bcgogo.schedule.bean.recommend;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.txn.pushMessage.PushMessageCategory;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.user.service.permission.IUserCacheService;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PushMessageBuildSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(PushMessageBuildSchedule.class);
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processPushMessageBuildJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }


  public void processPushMessageBuildJobs() {
    if (isLock()) {
      LOG.warn("PushMessageBuildSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("PushMessageBuildSchedule start.............");
      long currentTime = System.currentTimeMillis();
      ITradePushMessageService tradePushMessageService = ServiceManager.getService(ITradePushMessageService.class);
      tradePushMessageService.processPushMessageBuildTask();
      if (LOG.isDebugEnabled()) LOG.debug((System.currentTimeMillis()-currentTime)+",PushMessageBuildSchedule end.............");
    } catch (Exception e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }

}
