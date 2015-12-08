package com.bcgogo.enums.client;

import com.bcgogo.enums.txn.pushMessage.PushMessageReceiverStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-6-6
 * Time: 下午4:38
 */
public enum FeedbackType {
  USER_CLICK,
  CLOSE,
  AUTO_DISAPPEAR;

  private static Map<FeedbackType, PushMessageReceiverStatus> pushMessageStatusMapping = new HashMap<FeedbackType, PushMessageReceiverStatus>();

  static {
    pushMessageStatusMapping.put(USER_CLICK, PushMessageReceiverStatus.READ);
    pushMessageStatusMapping.put(CLOSE, PushMessageReceiverStatus.READ);
    pushMessageStatusMapping.put(AUTO_DISAPPEAR, PushMessageReceiverStatus.UNREAD);
  }


  public static PushMessageReceiverStatus getByPushMessageStatus(FeedbackType type) {
    return pushMessageStatusMapping.get(type);
  }

}
