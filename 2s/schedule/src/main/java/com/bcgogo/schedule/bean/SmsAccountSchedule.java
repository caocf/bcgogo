package com.bcgogo.schedule.bean;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.notification.StatStatus;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.notification.model.OutBox;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.notification.smsSend.SmsException;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.finance.ISmsAccountService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SmsAccountSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(SmsAccountSchedule.class);
  private static final Integer PAGE_SIZE = 10;
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processSmsJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  public void processSmsJobs() {
    if (isLock()) {
      LOG.warn("SmsAccountSchedule isLock!");
      return;
    }
    try {
      LOG.warn("SmsAccountSchedule.............");
      INotificationService notificationService = ServiceManager.getService(INotificationService.class);
      ISmsAccountService smsAccountService = ServiceManager.getService(ISmsAccountService.class);
      List<OutBox> outBoxList;
      Map<Long, Long> shopNumberMap;
      Set<Long> outBoxIds = new HashSet<Long>();
      while (true) {
        outBoxList = notificationService.getOutBoxByStatStatus(PAGE_SIZE, StatStatus.PENDDING);
        if (CollectionUtils.isEmpty(outBoxList)) break;
        shopNumberMap = new HashMap<Long, Long>();
        Long bcgogoNumber = 0l, shopNumber = 0L, shopId;
        for (OutBox outBox : outBoxList) {
          if (SenderType.bcgogo == outBox.getSender()) {
            bcgogoNumber = bcgogoNumber + (long) SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
          } else {
            if (outBox.getShopId() == null) continue;
            if (shopNumberMap.get(outBox.getShopId()) == null) {
              shopNumber = (long) SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
            } else {
              shopNumber = shopNumberMap.get(outBox.getShopId()) + (long) SmsUtil.calculateSmsNum(outBox.getContent(), outBox.getSendMobile());
            }
            shopNumberMap.put(outBox.getShopId(), shopNumber);
          }
          outBoxIds.add(outBox.getId());
        }
        try {
          LOG.warn("bcgogo消费统计:{}", bcgogoNumber);
          smsAccountService.createBcgogoConsumption(bcgogoNumber / 10.0, bcgogoNumber);
        } catch (Exception e) {
          LOG.error("短信统计：执行定时钟失败，失败原因:", e);
          break;
        }
        try {
          LOG.warn("客户消费统计[shopId-shopNumber]:{}", shopNumberMap);
          for (Map.Entry<Long, Long> entrySet : shopNumberMap.entrySet()) {
            shopId = entrySet.getKey();
            shopNumber = entrySet.getValue();
            smsAccountService.createShopSmsConsumption(shopId, shopNumber / 10.0, shopNumber);
          }
        } catch (Exception e) {
          LOG.error("短信统计：执行定时钟失败，失败原因:", e);
          break;
        }
        //更新统计状态
        notificationService.updateOutBoxStatStatus(outBoxIds,StatStatus.SUCCESS);
      }
    } catch(Exception e){
      LOG.error("SmsAccountSchedule error", e);
    } finally{
      lock = false;
    }
  }
}
