package com.bcgogo.txn.service.pushMessage;

import com.bcgogo.enums.txn.pushMessage.PushMessageSourceType;

/**
 * User: ZhangJuntao
 * Date: 13-6-20
 * Time: 上午10:56
 */
//todo create order
public interface IOrderPushMessageService {

  void createOrderPushMessageMessage(Long originShopId, Long pushShopId, Long sourceId, PushMessageSourceType sourceType) throws Exception;

}
