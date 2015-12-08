package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: zhangjie
 * Date: 15-04-30
 * Time: 上午10:45
 */
public class GenerationDriveLogSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(GenerationDriveLogSchedule.class);
  private static boolean lock = false;

  public static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  public static synchronized boolean unLock() {
    if (!lock) {
      return true;
    }
    lock = false;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) {
    try {
      if (isLock()) {
        LOG.warn("GenerationDriveLogSchedule isLock!");
        return;
      }
      LOG.info("GenerationDriveLogSchedule.............");
//      ServiceManager.getService(IGSMVehicleDataService.class).generationDriveLog();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      lock = false;
    }
  }
}
