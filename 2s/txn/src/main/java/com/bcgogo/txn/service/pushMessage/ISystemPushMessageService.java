package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageLevel;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 上午10:49
 */
public interface ISystemPushMessageService {
  void createOrUpdateAnnouncementPushMessage(Long announcementId, String content, PushMessageLevel level) throws Exception;

  void createOrUpdateFestivalPushMessage(Long festivalId, PushMessageLevel level, String name, Long start, Long end) throws Exception;

}
