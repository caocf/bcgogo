package com.bcgogo.schedule.stat;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.txn.service.app.IHandleAppUserShopCustomerMatchService;
import com.bcgogo.user.model.app.AppUserCustomer;
import com.bcgogo.user.service.IRequestMonitorService;
import com.bcgogo.user.service.app.IAppUserCustomerMatchService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 手机端用户、店铺客户匹配逻辑
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-9-6
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class AppUserCustomerMatchSchedule extends BcgogoQuartzJobBean {

  public static final Logger LOG = LoggerFactory.getLogger(AppUserCustomerMatchSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      LOG.info("系统开始匹配手机端用户、店铺客户,开始时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));

      try {
//        IAppUserCustomerMatchService matchService = ServiceManager.getService(IAppUserCustomerMatchService.class);
//        List<AppUserCustomer> appUserCustomerList = matchService.appUserCustomerUpdateTaskHandle();
//
//        RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
//        txnService.addAppUserNoToRepairWashBeautyAppoint(appUserCustomerList);

        IHandleAppUserShopCustomerMatchService matchService = ServiceManager.getService(IHandleAppUserShopCustomerMatchService.class);
        matchService.handleAppUserCustomerMatch(null);
      } catch (Exception e) {
        LOG.error("AppUserCustomerMatchSchedule.executeJob");
        LOG.error(e.getMessage(), e);
      }
      lock = false;
      LOG.info("系统结束匹配手机端用户、店铺客户,结束时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
    }

  }
}
