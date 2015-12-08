package com.bcgogo.notification.client;

import com.bcgogo.notification.smsSend.SmsException;

/**
 * User: ZhangJuntao
 * Date: 13-5-7
 * Time: 下午2:14
 */
public interface SmsClient {
  /**
   * 发送短信
   *
   * @param param SmsSendParam
   */
  String sendSMS(SmsParam param) throws SmsException;

  void register(SmsParam param) throws Exception;

  String queryBalance(SmsParam param) throws Exception;

  void logout(SmsParam param) throws Exception;
}
