package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.response.MessageResponse;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;

/**
 * User: ZhangJuntao
 * Date: 13-9-15
 * Time: 下午3:54
 */
public interface IAppPushMessageService {

  PushMessageType[] getPushMessageTypes(String[] types);

//  void pushMessage();

  MessageResponse getPollingMessage(Long appUserId, int limit, PushMessageType... types);
}
