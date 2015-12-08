package com.bcgogo.txn.service.client;

import com.bcgogo.client.ClientPrompt;

/**
 * User: ZhangJuntao
 * Date: 13-6-19
 * Time: 上午11:42
 */
public interface IClientPromptMsgSelector {

  ClientPrompt getPrompt(String basePath, Long shopId, String apiVersion, String userNo) throws Exception;

}
