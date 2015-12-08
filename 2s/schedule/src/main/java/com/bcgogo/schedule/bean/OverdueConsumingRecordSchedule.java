package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IRepairService;
import com.bcgogo.utils.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消息推送
 * Author: LiTao
 * Date: 2015-11-25
 * Time: 10:19
 */
public class OverdueConsumingRecordSchedule extends BcgogoQuartzJobBean {
    private static final Logger LOG = LoggerFactory.getLogger(OverdueConsumingRecordSchedule.class);
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
        IRepairService repairService=ServiceManager.getService(IRepairService.class);
        Long overdueTime=System.currentTimeMillis()-1000*60*60*24*2L;
        try {
            LOG.info("系统开始自动结算过期空白单据,开始时间:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
            repairService.overdueConsumingRecordAccount(overdueTime);
        } catch (Exception e) {
            LOG.error("OverdueConsumingRecordSchedule: 过期空白单据自动结算出错");
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }
        LOG.info("系统自动结算过期空白单据结束");
    }
}
