package com.bcgogo.schedule.bean;

import com.bcgogo.schedule.BcgogoQuartzJobBean;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SmsReceiveSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(SmsReceiveSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
  /*  if (lock) {
      return;
    } else {
      lock = true;
      try {
        INotificationService notificationService = ServiceManager.getService(INotificationService.class);
        //接收短信
        TricomSMS tricomSMS = new TricomSMS();
        String result = tricomSMS.receive();
        if (result != null) {
          int responseCode = getResponseFromResult(result, "response");
          if (responseCode > 0) {
            List<InBoxDTO> inBoxDTOList = getReceiveMsgsByResult(result, responseCode);
            if (inBoxDTOList != null) {
              for (InBoxDTO inBoxDTO : inBoxDTOList) {
                String phone = inBoxDTO.getSendMobile();
                OutBoxDTO outBoxDTO = notificationService.getOutBoxByMobile(phone);
                if (outBoxDTO != null) {
                  Long shopId = outBoxDTO.getShopId();
                  inBoxDTO.setReceiveShopId(shopId);
                }
                notificationService.createInBox(inBoxDTO);
              }
            }
          }
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      } finally {
        lock = false;
      }
    }     */
  }
 /*
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

  private List<InBoxDTO> getReceiveMsgsByResult(String result, int reponse) {
    if (reponse <= 0) {
      return null;
    }
    try {
      String[] smsArr = result.split("<sms>");
      if (smsArr != null && smsArr.length > 1) {
        List<InBoxDTO> inBoxDTOList = new ArrayList<InBoxDTO>();
        for (int m = 1; m < smsArr.length; m++) {
          InBoxDTO inBoxDTO = new InBoxDTO();
          String smsInfo = smsArr[m];
          //phone
          String[] phoneArr = smsInfo.split("phone");
          if (phoneArr != null && phoneArr.length > 1) {
            String phone = phoneArr[1].substring(1, phoneArr[1].length() - 2);
            inBoxDTO.setSendMobile(phone);
          }
          //content
          String[] contentArr = smsInfo.split("content");
          if (contentArr != null && contentArr.length > 1) {
            String content = contentArr[1].substring(1, contentArr[1].length() - 2);
            content = java.net.URLDecoder.decode(content, "UTF-8");
            inBoxDTO.setContent(content);
          }
          //sendtime
          String[] sendTimeArr = smsInfo.split("sendTime");
          if (sendTimeArr != null && sendTimeArr.length > 1) {
            String sendTime = sendTimeArr[1].substring(1, sendTimeArr[1].length() - 2);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date sendDate = sdf.parse(sendTime);
            c.setTime(sendDate);
            inBoxDTO.setReceiveTime(c);
          }
          inBoxDTOList.add(inBoxDTO);
        }
        return inBoxDTOList;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return null;
  }
    */
}
