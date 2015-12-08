package com.bcgogo.notification.smsSend;

import com.bcgogo.utils.SmsConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-28
 * Time: 下午3:29
 */
//工厂类
public class SmsSenderFactory {
  private static Map<String, AbstractSender> abstractSenderMap = new HashMap<String, AbstractSender>();

  private static Map<String, AbstractSender> abstractSenderMockMap = new HashMap<String, AbstractSender>();

  static {
//    abstractSenderMap.put(SmsConstant.Sms3TongConstant.name, new Sms3TongSender());
//    abstractSenderMap.put(SmsConstant.SmsSweConstant.name, new SmsSweSender());
    abstractSenderMap.put(SmsConstant.SmsYiMeiConstant.name, new SmsYiMeiSender());
    abstractSenderMap.put(SmsConstant.SmsLianYuConstant.name, new SmsLianYuSender());
  }

  //按公司名创建供应商实例
  public static AbstractSender getSmsSenderByName(String name) {
    return abstractSenderMap.get(name);
  }

  //按公司名创建供应商实例  mock
  static {
//    abstractSenderMockMap.put(SmsConstant.Sms3TongConstant.name, new Sms3TongSenderMock());
//    abstractSenderMockMap.put(SmsConstant.SmsSweConstant.name, new SmsSweSenderMock());
    abstractSenderMockMap.put(SmsConstant.SmsYiMeiConstant.name, new SmsYiMeiSenderMock());
    abstractSenderMockMap.put(SmsConstant.SmsLianYuConstant.name, new SmsLianYuSenderMock());

  }

  public static AbstractSender getSmsSenderMockByName(String name) {
    return abstractSenderMockMap.get(name);
  }

}
