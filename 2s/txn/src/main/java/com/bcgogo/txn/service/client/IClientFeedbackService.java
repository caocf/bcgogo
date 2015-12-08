package com.bcgogo.txn.service.client;

import com.bcgogo.client.FeedbackResult;
import com.bcgogo.enums.client.FeedbackType;
import com.bcgogo.enums.client.RecommendScene;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 下午5:49
 */
public interface IClientFeedbackService {
  FeedbackResult feedbackUserAction(Long shopId, String userNo, RecommendScene recommendScene,
                                    String recommendId, FeedbackType feedbackType) throws Exception;
}
