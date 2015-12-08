package com.bcgogo.txn.service.client;

import com.bcgogo.client.ClientAssortedMessage;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:35
 */
public interface IClientSystemService {
  ClientAssortedMessage getSystemMessages(Long shopId, String basePath, String userNo, String apiVersion);

}
