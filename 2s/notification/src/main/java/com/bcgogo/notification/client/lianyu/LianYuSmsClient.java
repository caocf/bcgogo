package com.bcgogo.notification.client.lianyu;

import com.bcgogo.notification.smsSend.SmsException;

/**
 * User: zhangjie
 * Date: 14-12-02
 * Time: 下午
 */
public interface LianYuSmsClient {
  /**
   * 发送短信
   *
   * @param param LianYuSmsParam
   */
  String sendSMS(LianYuSmsParam param) throws SmsException;
  String balanceInquery(LianYuSmsParam smsSendParam) throws SmsException;
  String returnStatus(LianYuSmsParam smsSendParam)throws SmsException;
  String detectionStopWords(LianYuSmsParam smsSendParam)throws SmsException;

}
