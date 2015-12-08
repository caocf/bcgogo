package com.bcgogo.notification.service;

import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.model.SmsSendingTimes;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-24
 * Time: 下午2:34
 */
@Component
public class SmsSendingTimesService implements ISmsSendingTimesService {
  private static final Logger LOG = LoggerFactory.getLogger(SmsSendingTimesService.class);

  @Autowired
  private NotificationDaoManager notificationDaoManager;

  @Override
  public void updateInvitationCodeSendingTimes(Set<String> mobiles) {
    if (CollectionUtil.isEmpty(mobiles)) return;
    NotificationWriter writer = notificationDaoManager.getWriter();
    Map<String, SmsSendingTimes> map = this.getSmsSendingTimesByMobiles(mobiles);
    SmsSendingTimes smsSendingTimes;
    Object status = writer.begin();
    try {
      for (String mobile : mobiles) {
        smsSendingTimes = map.get(mobile);
        if (smsSendingTimes != null) {
          smsSendingTimes.invitationCodeSendingTimesIncrementing();
          writer.update(smsSendingTimes);
        } else {
          writer.save(new SmsSendingTimes(mobile, 1));
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<String, SmsSendingTimes> getSmsSendingTimesByMobiles(Set<String> mobiles) {
    Map<String, SmsSendingTimes> map = new HashMap<String, SmsSendingTimes>();
    if (CollectionUtil.isEmpty(mobiles)) return map;
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<SmsSendingTimes> smsSendingTimesList = writer.getSmsSendingTimesByMobiles(mobiles);
    for (SmsSendingTimes smsSendingTimes : smsSendingTimesList) {
      map.put(smsSendingTimes.getMobile(), smsSendingTimes);
    }
    return map;
  }
}
