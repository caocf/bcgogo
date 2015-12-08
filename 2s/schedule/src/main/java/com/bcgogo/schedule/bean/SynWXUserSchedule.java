package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.wx.IWXUserService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-10
 * Time: 上午10:45
 */
public class SynWXUserSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(SynWXUserSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext){
    if (lock) {
      return;
    }
    try {
      ServiceManager.getService(IWXUserService.class).synALLWXUserDTOs();
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
  }
}
