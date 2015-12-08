package com.bcgogo.schedule.stat;

import com.bcgogo.report.service.IReportService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BizStatDTO;
import com.bcgogo.stat.model.BizStatType;
import com.bcgogo.stat.service.IBizStatService;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.utils.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 上周每个汽配店铺所关心的配件 汽修版的上周销量、入库量统计
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-18
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public class SalesInventoryWeekStatSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(SalesInventoryWeekStatSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    if (dayOfWeek != Calendar.MONDAY && dayOfWeek != Calendar.THURSDAY) {
      return;
    }

    if (lock) {
      return;
    } else {
      lock = true;
      try {
        LOG.info("后台上周每个汽配店铺所关心的配件 汽修版的上周销量、入库量统计");
        LOG.info("开始时间:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
        IPreciseRecommendService service = ServiceManager.getService(IPreciseRecommendService.class);
        service.salesInventoryMonthStat();

      } catch (Exception e) {
        LOG.error("/SalesInventoryWeekStatSchedule method=executeJob 上周销量、入库量统计失败");
        LOG.error(e.getMessage(), e);
      } finally {
        LOG.info("后台结束上周销量、入库量统计");
        lock = false;
        LOG.info("结束时间:" + DateUtil.dateLongToStr(System.currentTimeMillis()));
      }
    }
  }

}
