package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-11-19
 * Time: 下午1:44
 * To change this template use File | Settings | File Templates.
 */
public class SmsQuerySchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(SmsQuerySchedule.class);

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
  /* if (lock) {
      return;
    } else {
      lock = true;
      try {
        INotificationService notificationService = ServiceManager.getService(INotificationService.class);
        ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
        TricomSMS tricomSMS = new TricomSMS();
        Long shopId = -1L;
        List<ToBoxDTO> toBoxDTOList = notificationService.getShopToBoxByStatus(shopId, 2);
        if (toBoxDTOList != null) {
          for (ToBoxDTO toBoxDTO : toBoxDTOList) {
            String smsId = toBoxDTO.getSmsId();
            if (smsId != null) {
              try {
                //Thread.sleep(60*1000);
                String result = tricomSMS.query(smsId.trim());
                int responseCode = getResponseFromResult(result, "response");
                if (responseCode > 0) {
                    //int status = getResponseFromResult(result, "stat");
                    OutBoxDTO outBoxDTO = new OutBoxDTO();
                    outBoxDTO.setContent(toBoxDTO.getContent());
                    outBoxDTO.setSendChannel(toBoxDTO.getLastSendChannel());
                    outBoxDTO.setSendMobile(toBoxDTO.getReceiveMobile());
                    //outBoxDTO.setSendTime(toBoxDTO.getLastSendTime());
                    outBoxDTO.setStatus(toBoxDTO.getStatus());
                    outBoxDTO.setType(toBoxDTO.getType());
                    outBoxDTO.setName(toBoxDTO.getName());
                    outBoxDTO.setLicenceNo(toBoxDTO.getLicenceNo());
                    outBoxDTO.setPriority(toBoxDTO.getPriority());
                    outBoxDTO.setRawData(toBoxDTO.getRawData());
                    outBoxDTO.setSender(toBoxDTO.getSender());
                    outBoxDTO.setShopId(toBoxDTO.getShopId());
                    outBoxDTO.setUserId(toBoxDTO.getUserId());
                    outBoxDTO.setSmsId(toBoxDTO.getSmsId());
                    outBoxDTO.setSendTime(toBoxDTO.getStartTime());
                    //删除to_box表中的记录     需要进行事务控制
                    notificationService.createOutBox(outBoxDTO);
                    notificationService.deleteToBoxByToBoxId(toBoxDTO.getId());
                    //扣除短信费用
                    Long smsShopId = toBoxDTO.getShopId();
                    if(smsShopId!=null){
                       SmsBalanceDTO smsBalanceDTO = smsRechargeService.getSmsBalanceByShopId(smsShopId);
                       if(smsBalanceDTO!=null){
                          double smsBalance = smsBalanceDTO.getSmsBalance();    //短信余额 -- 元
                          smsBalanceDTO.setSmsBalance(smsBalance-0.1);
                          smsRechargeService.updateSmsBalance(smsBalanceDTO);
                       }
                    }
                }
              } catch (Exception e) {
                LOG.error(e.getMessage(), e);
              }
            }
          }
        }
      } finally {
        lock = false;
      }
    } */
  }


  private int getResponseFromResult(String result, String tag) {
    if (result != null) {
      String[] strs = result.split(tag);
      if (strs != null && strs.length > 2) {
        String responseStr = strs[1];
//                 if(responseStr.length()==4){
//                     return Integer.parseInt(responseStr.substring(1,2));
//                 }else{
//                     return Integer.parseInt(responseStr.substring(1,3));
//                 }
        return Integer.parseInt(responseStr.substring(1, responseStr.length() - 2));
      }
    }
    return -1001;     //解析错误
  }


}
