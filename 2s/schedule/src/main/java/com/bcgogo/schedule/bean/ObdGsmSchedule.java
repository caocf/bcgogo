package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IDriveLogService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObdGsmSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(ObdGsmSchedule.class);
  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processJobs();
  }

  private void processJobs() {
    if (isLock()) {
      LOG.warn("ObdGsmSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("ObdGsmSchedule.............");
      IDriveLogService driveLogService = ServiceManager.getService(IDriveLogService.class);
      driveLogService.generateDriveLogByGsmVehicleInfo(100, null);
    } catch (Throwable e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      lock = false;
      debugResourceLeak();
    }
  }
}
