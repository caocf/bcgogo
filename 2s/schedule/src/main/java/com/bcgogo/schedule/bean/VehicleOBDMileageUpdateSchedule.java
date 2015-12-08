package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-24
 * Time: 下午2:28
 */
public class VehicleOBDMileageUpdateSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleOBDMileageUpdateSchedule.class);
  private static final int pageSize = 50;  //todo测试的时候小一点能测出一些边界问题来，发布的时候记得改成50

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
    if (isLock()) {
      return;
    }
    try {
      process();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      lock = false;
    }
  }

  public void process() {

  }
}
