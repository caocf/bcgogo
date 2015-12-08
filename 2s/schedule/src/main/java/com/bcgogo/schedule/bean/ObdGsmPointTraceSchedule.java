package com.bcgogo.schedule.bean;

import com.bcgogo.etl.service.IGsmDataTraceService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by XinyuQiu on 2015-03-24.
 */
public class ObdGsmPointTraceSchedule extends BcgogoQuartzJobBean {

  private static final Logger LOG = LoggerFactory.getLogger(ObdGsmPointTraceSchedule.class);
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
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processJobs();
  }

  public void processJobs() {
    if (isLock()) {
      LOG.warn("ObdGsmTraceSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("ObdGsmTraceSchedule.............");
      IGsmDataTraceService gsmDataTraceService = ServiceManager.getService(IGsmDataTraceService.class);
      gsmDataTraceService.traceObdGsmPointData();
    } catch (Throwable e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      lock = false;
      debugResourceLeak();
    }
  }
}
