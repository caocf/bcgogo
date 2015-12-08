//package com.bcgogo.notification.smsSend;
//
//import com.bcgogo.enums.notification.SmsSendKind;
//import com.bcgogo.enums.sms.SenderType;
//import com.bcgogo.exception.BcgogoExceptionType;
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
// * Date: 12-3-30
// * Time: 下午5:11
// * To change this template use File | Settings | File Templates.
// */
//public class SmsSweSenderMock extends AbstractSender {
//  private static final Logger LOG = LoggerFactory.getLogger(SmsSweSender.class);
//
//  @Override
//  //AbstractSender发送方法实现
//  public SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws SmsException {
//    SmsSendResult smsSendResult = validateSendSms(smsSendDTO);   //商户、余额、数据不完整 检查
//    if (!smsSendResult.isSuccess()) {
//      return smsSendResult;
//    }
//    String receiveMobile = smsSendDTO.getReceiveMobile();
//    //swe分组发送
//    String[] receiveMobiles = SmsUtil.groupingSmsMobile(receiveMobile, SmsConstant.SmsSweConstant.mobileNumber);
//    for (int i = 0; i < receiveMobiles.length; i++) {
//      smsSendDTO.setReceiveMobile(receiveMobiles[i]);
//
//      smsSendResult = sendSmsImpl(smsSendDTO, smsSendResult);
//      if (!smsSendResult.getSmsResponse().equals(SmsConstant.SMS_STATUS_SUCCESS)) {
//        //把发送过的手机号剪切掉   begin=i * (receiveMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1)，end = receiveMobile.length()
//        smsSendResult.setFailMobiles(com.bcgogo.utils.StringUtil.subString(receiveMobile, i * (receiveMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1), receiveMobile.length()));
//        return smsSendResult;
//      }
//    }
//    return smsSendResult;
//  }
//
//  public SmsSendResult sendSmsImpl(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) throws SmsException {
//    if (flag)
//      //模拟中间发送失败的情况
//      if (smsSendDTO.getReceiveMobile().contains("false"))
//        code = 709;
//      else
//        code = SmsConstant.SmsSweConstant.SMS_STATUS_SUCCESS;
//    //接受返回信息
//    combineContent(smsSendDTO);
//    smsSendResult = getSmsSendResult(smsSendResult, smsSendDTO);
//    smsSendResult.setSmsSendKind(SmsSendKind.SWE_MOCK);
//    return smsSendResult;
//  }
//
//  //接受返回信息
//  private SmsSendResult getSmsSendResult(SmsSendResult smsSendResult, SmsSendDTO smsSendDTO) throws SmsException {
//    String returnData = "<dataRsp><executeResult><error>" + code + "</error><errorDescr>操作成功</errorDescr><errorParamsDescr/><result>1</result><taskid>8000005320120331171541824</taskid></executeResult></dataRsp>";
//    String responseCodeStr = parseXMLString(returnData, "error");
//    int responseCode = Integer.valueOf(responseCodeStr);
//    if (responseCode == SmsConstant.SmsSweConstant.SMS_STATUS_SUCCESS) { //判断短信返回值
//      String taskid = parseXMLString(returnData, "taskid");
//      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
//      smsSendResult.setSmsId(taskid);
//      int num = this.caculateSmsNum(smsSendDTO.getContent(), smsSendDTO.getReceiveMobile());
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
//  // 组合发送内容
//  private String combineContent(SmsSendDTO smsSendDTO) throws SmsException {
//    String key = "key=" + SmsConstant.SmsSweConstant.key;
//    String secret = "&secret=" + SmsConstant.SmsSweConstant.secret;
//    String sms_number = "&sms_number=" + smsSendDTO.getReceiveMobile();
//    String content = null;
//    String subject = "&subject=" + "统购短信";
//    try {
//      content = "&content=" + java.net.URLEncoder.encode(smsSendDTO.getContent(), "UTF-8");
//      if (System.currentTimeMillis() - tokenTime >= SmsConstant.SmsSweConstant.tokenTime || token == null) {
//        token = this.getToken();
//      }
//      String token = "&token=" + this.token;
//      String postData = key + secret + sms_number + content + subject + token;
//      return postData;
//    } catch (Exception e) {
//      LOG.error(e.getMessage(),e);
//      throw new SmsException(BcgogoExceptionType.MsgUnsupportedEncoding);
//    }
//
//  }
//
//  private static String token;
//  private static long tokenTime = System.currentTimeMillis();
//
//  //得到token值    token值不需要每次都取，各一定时间取一次
//  public String getToken() throws Exception {
//    String returnData = "<dataRsp><dataView><loginUser><accessToken>c8fbbf3cbd416d8fd1940ca7c387a8b239c688c2</accessToken><domainLevel>0</domainLevel><email>test@test.com</email><userLevel>1</userLevel><userState>ST002</userState><userStateDescr>已激活</userStateDescr><userid>80000053</userid><username>test</username></loginUser></dataView><executeResult><error>1</error><errorDescr>操作成功</errorDescr><errorParamsDescr/><result>1</result></executeResult></dataRsp>";
//    String token = parseXMLString(returnData, "accessToken");
//    return token;
//  }
//
//  public String parseXMLString(String context, String name) throws SmsException {
//      return XMLParser.getRootElement(context, name);
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
