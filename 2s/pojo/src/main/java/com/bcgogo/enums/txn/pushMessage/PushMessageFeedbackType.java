package com.bcgogo.enums.txn.pushMessage;

import com.bcgogo.enums.client.FeedbackType;

import java.util.HashMap;
import java.util.Map;

public enum PushMessageFeedbackType {
  WEB_DEFAULT_HIT,
  WEB_NO_HIT,
  WEB_HIT,
  CLIENT_HIT,
  CLIENT_NO_HIT;

  private static Map<FeedbackType, PushMessageFeedbackType> feedbackTypeMapping = new HashMap<FeedbackType, PushMessageFeedbackType>();

  static {
    feedbackTypeMapping.put(FeedbackType.USER_CLICK, CLIENT_HIT);
    feedbackTypeMapping.put(FeedbackType.CLOSE, CLIENT_NO_HIT);
  }


  public static PushMessageFeedbackType getByFeedbackType(FeedbackType type) {
    return feedbackTypeMapping.get(type);
  }
}
