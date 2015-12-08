package com.bcgogo.schedule.bean.recommend;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.recommend.IRecommendService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessoryRecommendSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(AccessoryRecommendSchedule.class);
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processAccessoryRecommendJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }


  public void processAccessoryRecommendJobs() {
    if (isLock()) {
      LOG.warn("AccessoryRecommendSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("AccessoryRecommendSchedule start.............");
      long currentTime = System.currentTimeMillis();
      IRecommendService recommendService = ServiceManager.getService(IRecommendService.class);
      recommendService.processAccessoryRecommend();
      if (LOG.isDebugEnabled()) LOG.debug((System.currentTimeMillis()-currentTime)+",AccessoryRecommendSchedule end.............");
    } catch (Exception e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }

}
