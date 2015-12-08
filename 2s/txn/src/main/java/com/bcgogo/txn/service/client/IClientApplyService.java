package com.bcgogo.txn.service.client;

import com.bcgogo.client.ClientAssortedMessage;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午9:55
 */
public interface IClientApplyService {
  ClientAssortedMessage getApplyMessage(Long shopId, String basePath, String userNo, String apiVersion) throws Exception;
}