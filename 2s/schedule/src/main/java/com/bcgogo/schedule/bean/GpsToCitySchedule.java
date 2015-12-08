package com.bcgogo.schedule.bean;

import com.bcgogo.etl.service.IGSMVehicleDataService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: zhangjie
 * Date: 15-07-15
 * Time: 上午10:45
 */
public class GpsToCitySchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(GpsToCitySchedule.class);
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
        LOG.warn("GpsToCitySchedule isLock!");
        return;
      }
      LOG.info("GpsToCitySchedule.............");
      IGSMVehicleDataService gsmVehicleDataService = ServiceManager.getService(IGSMVehicleDataService.class);
      gsmVehicleDataService.gpsToCityMask();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      lock = false;
    }
  }
}
