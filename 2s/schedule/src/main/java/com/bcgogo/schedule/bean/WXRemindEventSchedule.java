package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.IWXTxnService;
import com.bcgogo.txn.service.WXTxnService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提醒事件给微信用户推信息
 * User: ndong
 * Date: 14-9-17
 * Time: 上午10:49
 */
public class WXRemindEventSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(WXRemindEventSchedule.class);
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
    IWXTxnService txnService= ServiceManager.getService(WXTxnService.class);
    try {
      if (isLock()) {
        return;
      }
       txnService.doWXRemindEvent();
    }catch (Exception e) {
      LOG.error(e.getMessage(),e);
    } finally {
      lock = false;
    }


  }

}
