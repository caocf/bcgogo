package com.bcgogo.notification.service;

import com.bcgogo.notification.model.SmsSendingTimes;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-24
 * Time: 下午2:35
 */
public interface ISmsSendingTimesService {
  void updateInvitationCodeSendingTimes(Set<String> mobiles);

  Map<String, SmsSendingTimes> getSmsSendingTimesByMobiles(Set<String> mobiles);
}
