package com.bcgogo.txn.service.client;

import com.bcgogo.client.ClientAssortedMessage;

import java.text.ParseException;

/**
 * User: ZhangJuntao
 * Date: 13-6-9
 * Time: 上午10:31
 */
public interface IClientOrderService {
    ClientAssortedMessage getOrderStatMessage(Long shopId, String basePath, String userNo, String apiVersion) throws ParseException;
}
