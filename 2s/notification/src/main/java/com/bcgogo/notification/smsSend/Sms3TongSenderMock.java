//package com.bcgogo.notification.smsSend;
//
//import com.bcgogo.enums.notification.SmsSendKind;
//import com.bcgogo.enums.sms.SenderType;
//import com.bcgogo.notification.dto.SmsSendDTO;
//import com.bcgogo.notification.dto.SmsSendResult;
//import com.bcgogo.utils.SmsConstant;
//import com.bcgogo.utils.XMLParser;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Created by IntelliJ IDEA.
// * User: ZhangJuntao
// * Date: 12-3-28
// * Time: 下午3:27
// */
////三通短信供应商 测试
//public class Sms3TongSenderMock extends AbstractSender {
//  private static final Logger LOG = LoggerFactory.getLogger(Sms3TongSenderMock.class);
//
//  @Override
//  //AbstractSender发送方法实现
//  public SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws SmsException {
//    SmsSendResult smsSendResult = validateSendSms(smsSendDTO);   //商户、余额、数据不完整 检查
//    if (!smsSendResult.isSuccess()) {
//      return smsSendResult;
//    }
//    String receiveMobile = smsSendDTO.getReceiveMobile();
//    //3通分组发送
//    String[] receiveMobiles = SmsUtil.groupingSmsMobile(receiveMobile, SmsConstant.Sms3TongConstant.mobileNumber);
//    for (int i = 0; i < receiveMobiles.length; i++) {
//      smsSendDTO.setReceiveMobile(receiveMobiles[i]);
//      smsSendResult = sendSmsImpl(smsSendDTO, smsSendResult);
//      if (!smsSendResult.getSmsResponse().equals(SmsConstant.SMS_STATUS_SUCCESS)) {
//        //把发送过的手机号剪切掉   begin=i * (receiveMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1)，end = receiveMobile.length()
//        smsSendResult.setFailMobiles(com.bcgogo.utils.StringUtil.subString(receiveMobile, i * (receiveMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1), receiveMobile.length()));
//        return smsSendResult;
//      }
//    }
//    smsSendResult.setSmsSendKind(SmsSendKind.THREE_TONG);
//    return smsSendResult;
//  }
//
//  //实现
//  public SmsSendResult sendSmsImpl(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) throws SmsException {
//    if (flag)
//      //模拟中间发送失败的情况
//      if (smsSendDTO.getReceiveMobile().contains("false"))
//        code = SmsConstant.Sms3TongConstant.SMS_RESPONSE_ERROR;
//      else
//        code = 1;
//    //接受返回信息
//    smsSendResult = getSmsSendResult(smsSendResult, smsSendDTO);
//    return smsSendResult;
//  }
//
//  //接受返回信息
//  private SmsSendResult getSmsSendResult(SmsSendResult smsSendResult, SmsSendDTO smsSendDTO) throws SmsException {
//    String returnData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//        "<result>\n" +
//        "<response>" + code + "</response><sms><phone>130982922</phone><smsID>1000000000000</smsID></sms></result>";
//    String responseStr = XMLParser.getRootElement(returnData, "response");
//    int responseCode = responseStr == null ? SmsConstant.Sms3TongConstant.SMS_RESPONSE_ERROR : Integer.parseInt(responseStr);
//    if (responseCode > SmsConstant.Sms3TongConstant.SMS_RESPONSE_ERROR) { //判断短信返回值
//      int num = this.caculateSmsNum(smsSendDTO.getContent(), smsSendDTO.getReceiveMobile());
//      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
//      smsSendResult.setSmsId(XMLParser.getRootElement(returnData, "smsID"));
//      smsSendResult.setSmsNum(num);
//      smsSendResult.setSmsPrice(num * SmsConstant.SMS_UNIT_PRICE);
//      //BCGOGO 发送的短信不需要扣款
//      if (smsSendDTO.getSender().equals(SenderType.Shop)){
//        this.updateSmsBalanceAfterSendSuccess(smsSendDTO, smsSendResult);
//      }
//    } else {
//      smsSendResult.setSmsNum(0);
//      smsSendResult.setSmsPrice(0);
//      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
//    }
//    return smsSendResult;
//  }
//
//  private static int code = 1;
//  private static boolean flag = false;
//
//  public static void setCode(int value) {
//    code = value;
//  }
//
//  public static void setFlag(boolean value) {
//    flag = value;
//  }
//
//}
