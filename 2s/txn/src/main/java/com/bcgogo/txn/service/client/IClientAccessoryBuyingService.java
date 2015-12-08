package com.bcgogo.txn.service.client;

import com.bcgogo.client.ClientAssortedMessage;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:36
 */
public interface IClientAccessoryBuyingService {
  List<ClientAssortedMessage> getAccessoryBuyingMessages(Long shopId, String basePath, String userNo, String apiVersion) throws Exception;
}
