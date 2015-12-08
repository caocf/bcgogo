package com.bcgogo.schedule.stat;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IRequestMonitorService;
import com.bcgogo.utils.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后台保存url配置监控数据
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-4-1
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 */
public class UrlMonitorStatSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(UrlMonitorStatSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      LOG.info("系统开始保存urlMonthStat数据,开始时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));

      try {
        IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
        requestMonitorService.saveUrlMonitorStatFromMemCache();
      } catch (Exception e) {
        LOG.error("UrlMonitorStatSchedule.executeJob");
        LOG.error(e.getMessage(), e);
      }
      lock = false;
      LOG.info("系统结束保存urlMonthStat数据,结束时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
    }

  }
}
