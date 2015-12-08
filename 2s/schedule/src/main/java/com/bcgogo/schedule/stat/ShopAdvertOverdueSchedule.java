package com.bcgogo.schedule.stat;

import com.bcgogo.report.service.IReportService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BizStatDTO;
import com.bcgogo.stat.model.BizStatType;
import com.bcgogo.stat.service.IBizStatService;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.service.IAdvertService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import com.bcgogo.utils.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 14-4-25
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public class ShopAdvertOverdueSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(ShopAdvertOverdueSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      try {
        LOG.info("后台开始处理 店铺宣传自动到期" + DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, System.currentTimeMillis()));

        IAdvertService advertService = ServiceManager.getService(IAdvertService.class);
        advertService.shopAdvertOverdueHandle();

        LOG.info("后台结束处理 店铺宣传自动到期" + DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_ALL, System.currentTimeMillis()));
      } catch (Exception e) {
        LOG.error("/ShopAdvertOverdueSchedule method=executeJob");
        LOG.error(e.getMessage(), e);
      } finally {
        LOG.info("后台结束处理");
        lock = false;
      }
    }
  }

}
