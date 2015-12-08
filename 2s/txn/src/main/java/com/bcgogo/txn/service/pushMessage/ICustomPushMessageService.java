package com.bcgogo.txn.service.pushMessage;

import java.util.Set;

/**
 * 自定义消息
 * Created by Hans on 14-1-15.
 */
public interface ICustomPushMessageService {

  void createCustomPushMessage2App(Set<Long> customerIds, String content) throws Exception;

  void createCustomPushMessage2App(String content, String appUserNo, long shopId) throws Exception;

}
