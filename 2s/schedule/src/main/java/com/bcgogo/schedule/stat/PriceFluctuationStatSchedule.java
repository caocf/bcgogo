package com.bcgogo.schedule.stat;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IPurchaseCostStatService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-5
 * Time: 下午1:04
 * To change this template use File | Settings | File Templates.
 */
public class PriceFluctuationStatSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(PriceFluctuationStatSchedule.class);
  private static boolean lock = false;


  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    }
    if(1==1){ //todo 这里sql有问题 非常耗性能，暂时注释掉
      return;
    }
    lock = true;
    try {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.HOUR_OF_DAY, 0);
      c.set(Calendar.MINUTE, 0);
      c.set(Calendar.SECOND, 0);
      c.set(Calendar.MILLISECOND,0);
      Long endTime = c.getTimeInMillis();
      c.add(Calendar.YEAR, -1);
      Long startTime = c.getTimeInMillis();
      LOG.info("后台价格波动统计监听开始运行，startTime:"+startTime+",endTime:"+endTime);
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      //每天的凌晨1点，先清空数据，再执行统计
      //不分shopId，不限top10，在页面发起查询时，再限定shopId和top10
      List<Object[]> dtoList = purchaseCostStatService.queryAllProductPriceFluctuation(startTime, endTime);
      if(dtoList!=null && dtoList.size()!=0){
        //统计结果入库
        purchaseCostStatService.savePriceFluctuationStat(dtoList);
      }
    }catch (Exception e) {
      LOG.error("/PriceFluctuationStatSchedule, method=executeJob. 后台价格波动统计监听运行失败");
      LOG.error(e.getMessage(), e);
    } finally {
      LOG.info("后台价格波动统计监听运行结束");
      lock = false;
    }
  }
}
