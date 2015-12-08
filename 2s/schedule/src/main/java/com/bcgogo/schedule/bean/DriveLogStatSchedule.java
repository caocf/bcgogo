package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.app.IDriveLogService;
import com.bcgogo.user.service.app.IDriveStatService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XinyuQiu on 14-5-4.
 */
public class DriveLogStatSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(DriveLogStatSchedule.class);
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
      LOG.warn("DriveLogStatSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("DriveLogStatSchedule.............");
      IDriveStatService driveStatService = ServiceManager.getService(IDriveStatService.class);
      driveStatService.statDriveLog(50);
    } catch (Exception e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }
}
