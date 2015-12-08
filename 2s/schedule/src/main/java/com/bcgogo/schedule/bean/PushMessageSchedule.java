package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.pushMessage.IPushMessageService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消息推送
 * Author: ndong
 * Date: 2015-3-26
 * Time: 13:19
 */
public class PushMessageSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(PushMessageSchedule.class);
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
    try {
      if (isLock()) {
        return;
      }
      ServiceManager.getService(IPushMessageService.class).startPushMessageScheduleWork();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    } finally {
      lock = false;
    }
  }


}
