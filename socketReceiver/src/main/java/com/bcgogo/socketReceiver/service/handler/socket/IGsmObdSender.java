package com.bcgogo.socketReceiver.service.handler.socket;

import com.bcgogo.common.Result;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-7
 * Time: 上午11:37
 * To change this template use File | Settings | File Templates.
 */
public interface IGsmObdSender {
  Result sendCommand(String imei, String command);
}
