package com.bcgogo.schedule.stat;

import com.bcgogo.report.service.IReportService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BizStatDTO;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.model.BizStatType;
import com.bcgogo.stat.service.IBizStatService;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.service.stat.assistantStat.IAssistantStatService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-15
 * Time: 上午11:35
 * To change this template use File | Settings | File Templates.
 */
public class BizStatSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(BizStatSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      try {
        LOG.info("后台开始员工业绩统计");
        IAssistantStatService assistantStatService = ServiceManager.getService(IAssistantStatService.class);
        assistantStatService.assistantStat(null);

        IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
        businessAccountService.assistantStatBusinessAccountStat(null);


      } catch (Exception e) {
        LOG.error("/BizStatSchedule method=executeJob 营业统计和员工业绩失败");
        LOG.error(e.getMessage(), e);
      } finally {
        LOG.info("后台结束统计");
        lock = false;
      }
    }

  }

  private void count(long shopId, int statYear, Integer statMonth, Integer statDay, Integer statWeek, long startTime, long endTime) {
    IReportService reportService = ServiceManager.getService(IReportService.class);
    double statSum;
    double statSumIncome = 0.0d;

    //CARREPAIR
    statSum = reportService.countShopCarRepairIncome(shopId, startTime, endTime);
    statSumIncome += statSum;
    this.saveBizStat(shopId, BizStatType.CARREPAIR, statYear, statMonth, statDay, statWeek, statSum);

    //SALES
    statSum = reportService.countShopSalesIncome(shopId, startTime, endTime);
    statSumIncome += statSum;

    this.saveBizStat(shopId, BizStatType.SALES, statYear, statMonth, statDay, statWeek, statSum);

    //CARWASHING
    statSum = reportService.countShopCarWashingIncome(shopId, startTime, endTime);
    statSumIncome += statSum;

    this.saveBizStat(shopId, BizStatType.CARWASHING, statYear, statMonth, statDay, statWeek, statSum);

    //INCOME
    this.saveBizStat(shopId, BizStatType.INCOME, statYear, statMonth, statDay, statWeek, statSumIncome);

    //PURCHASING
    statSum = reportService.countShopPurchasingCost(shopId, startTime, endTime);

    this.saveBizStat(shopId, BizStatType.PURCHASING, statYear, statMonth, statDay, statWeek, statSum);

    //GROSSPROFIT
    this.saveBizStat(shopId, BizStatType.GROSSPROFIT, statYear, statMonth, statDay, statWeek, statSumIncome - statSum);

  }

  private void saveBizStat(long shopId, BizStatType bizStatType, int statYear, Integer statMonth, Integer statDay, Integer statWeek, double statSum) {
    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    BizStatDTO bizStatDTO = new BizStatDTO();
    bizStatDTO.setShopId(shopId);
    bizStatDTO.setStatType(bizStatType.toString());
    bizStatDTO.setStatYear(Long.parseLong(String.valueOf(statYear)));
    if (statMonth != null) {
      bizStatDTO.setStatMonth(Long.parseLong(statMonth.toString()));
    }
    if (statDay != null) {
      bizStatDTO.setStatDay(Long.parseLong(statDay.toString()));
    }
    if (statWeek != null) {
      bizStatDTO.setStatWeek(Long.parseLong(statWeek.toString()));
    }
    bizStatDTO.setStatSum(statSum);

    bizStatService.saveBizStat(bizStatDTO);

  }
}
