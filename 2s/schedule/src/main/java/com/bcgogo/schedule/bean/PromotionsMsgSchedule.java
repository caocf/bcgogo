package com.bcgogo.schedule.bean;

import com.bcgogo.common.Result;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PromotionMsgJobDTO;
import com.bcgogo.txn.service.messageCenter.IMessageService;
import com.bcgogo.utils.CollectionUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-7-17
 * Time: 下午2:26
 * To change this template use File | Settings | File Templates.
 */
public class PromotionsMsgSchedule  extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(PromotionsCheckSchedule.class);
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
    try{

      INotificationService notificationService=ServiceManager.getService(INotificationService.class);
      List<PromotionMsgJobDTO> jobDTOs=notificationService.getCurrentPromotionMsgJobDTO();
      if(CollectionUtil.isEmpty(jobDTOs)){
        return;
      }
      IMessageService messageService=ServiceManager.getService(IMessageService.class);
      List<Long> jobIdList=new ArrayList<Long>();
      Result result=new Result();
      for(PromotionMsgJobDTO jobDTO:jobDTOs){
        messageService.sendPromotionMsg(result, jobDTO.getMessageDTO());
        jobIdList.add(jobDTO.getId());
      }
      notificationService.updateFinishedPromotionMsgJob(jobIdList);
    }catch (Exception e){
       LOG.error(e.getMessage(),e);
    }finally {
      lock = false;
    }
  }
}
