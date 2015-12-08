package com.bcgogo.schedule.stat;

import com.bcgogo.config.service.IJuheService;
import com.bcgogo.report.service.IReportService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BizStatDTO;
import com.bcgogo.stat.model.BizStatType;
import com.bcgogo.stat.service.IBizStatService;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.service.app.ISendVRegulationMsgToAppService;
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
public class SendVRegulationMsgToAppSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(SendVRegulationMsgToAppSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    } else {
      lock = true;
      try {
        LOG.info("后台开始发送违章记录给车主");
        ISendVRegulationMsgToAppService sendVRegulationMsgToAppService = ServiceManager.getService(ISendVRegulationMsgToAppService.class);
        //发送给app用户和后视镜微信用户
        sendVRegulationMsgToAppService.sendVRegulationMsgToApp();
        //发送给一发微信用户
        sendVRegulationMsgToAppService.sendVRegulationMsgToYiFaWXUser();
      } catch (Exception e) {
        LOG.error("/SendVRegulationMsgToAppSchedule method=executeJob 后台发送违章记录给车主失败");
        LOG.error(e.getMessage(), e);
      } finally {
        LOG.info("后台结束发送违章记录给车主");
        lock = false;
      }
    }

  }


}
