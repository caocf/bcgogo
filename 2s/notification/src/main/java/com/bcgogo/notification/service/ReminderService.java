package com.bcgogo.notification.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.Reminder;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午2:43
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ReminderService implements IReminderService{

  private static final long  trialExpireTime=7*24*60*60*1000; //提醒提前提醒秒数；

  @Autowired
  private NotificationDaoManager daoManager;
  @Override
  public void saveOrUpdateReminder(Reminder reminder) {
         Map map=new HashMap();
  }

  public Map isTrialExpired(Long shopId) throws UnsupportedEncodingException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    long currentTime=System.currentTimeMillis();
    Map result=new HashMap();
    if(shopDTO==null||!ShopStatus.isRegistrationTrial(shopDTO.getShopStatus())||shopDTO.getTrialStartTime()==null||shopDTO.getTrialEndTime()==null){
      result.put("isTrialExpired",false);
    }else if((shopDTO.getTrialEndTime()-currentTime)>0&&shopDTO.getTrialEndTime()-currentTime<=trialExpireTime){
      int endDay_day =(int)Math.floor ((shopDTO.getTrialEndTime() - currentTime) / (1000 * 24 * 60 * 60.0));
      double total_seconds=(shopDTO.getTrialEndTime() - currentTime)%(1000 * 24 * 60 * 60.0);
      int left_hour =(int)Math.floor (total_seconds/(1000 * 60 * 60.0));
      total_seconds=total_seconds%(1000 * 60 * 60.0); //剩余分钟的second总数
      long left_minute =(int)Math.floor (total_seconds/(1000*60.0));
      String LeftText=(endDay_day)+"天"+left_hour+"时"+left_minute+"分";
      result.put("trialLeftTime", URLEncoder.encode(LeftText,"UTF-8"));
      result.put("trialEndDate", URLEncoder.encode(DateUtil.convertDateLongToDateString("yyyy年MM月dd日 HH时mm分", shopDTO.getTrialEndTime()),"UTF-8"));
      result.put("isTrialExpired", true);
    }
    return result;

  }

}
