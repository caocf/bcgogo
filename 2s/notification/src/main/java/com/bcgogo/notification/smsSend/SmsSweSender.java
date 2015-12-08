//package com.bcgogo.notification.smsSend;
//
//import com.bcgogo.enums.notification.SmsSendKind;
//import com.bcgogo.enums.sms.SenderType;
//import com.bcgogo.exception.BcgogoExceptionType;
//import com.bcgogo.notification.dto.SmsSendDTO;
//import com.bcgogo.notification.dto.SmsSendResult;
//import com.bcgogo.utils.ShopConstant;
//import com.bcgogo.utils.SmsConstant;
//import com.bcgogo.utils.XMLParser;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
///**
// * Created by IntelliJ IDEA.
// * User: ZhangJuntao
// * Date: 12-3-28
// * Time: 下午3:28
// * To change this template use File | Settings | File Templates.
// */
////众方短信供应商
//public class SmsSweSender extends AbstractSender {
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
//      smsSendResult = sendSmsImpl(smsSendDTO, smsSendResult);
//      if (!smsSendResult.getSmsResponse().equals(SmsConstant.SMS_STATUS_SUCCESS)) {
//        //把发送过的手机号剪切掉   begin=i * (receiveMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1)，end = receiveMobile.length()
//        smsSendResult.setFailMobiles(com.bcgogo.utils.StringUtil.subString(receiveMobile, i * (receiveMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1), receiveMobile.length()));
//        return smsSendResult;
//      }
//    }
//    smsSendResult.setSmsSendKind(SmsSendKind.SWE);
//    return smsSendResult;
//  }
//
//  //实际短信发送
//  private SmsSendResult sendSmsImpl(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) throws SmsException {
////    String url = SmsConstant.SmsSweConstant.tricomUrl + "singleSms";
//    String url = getConfigService().getConfig("SMS_SWE_TRICOM_URL", ShopConstant.BC_SHOP_ID) + "singleSms";
//    //发送 短信
//    HttpURLConnection httpURLConnection = null;
//    try {
//      httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
//      httpURLConnection.setRequestMethod("POST");
//      httpURLConnection.setDoOutput(true);
//      OutputStream out = httpURLConnection.getOutputStream();
//      out.write(combineContent(smsSendDTO).getBytes("UTF-8"));
//      out.flush();
//      out.close();
//    } catch (IOException e) {
//      LOG.error(e.getMessage(), e);
//      throw new SmsException(BcgogoExceptionType.MsgIOException);
//    }
//    //接受返回信息
//    smsSendResult = getSmsSendResult(httpURLConnection, smsSendResult, smsSendDTO);
//    return smsSendResult;
//  }
//
//  //接受返回信息
//  private SmsSendResult getSmsSendResult(HttpURLConnection httpURLConnection, SmsSendResult smsSendResult, SmsSendDTO smsSendDTO) throws SmsException {
//    BufferedReader bufferedReader = null;
//    try {
//      bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//      StringBuffer stringBuffer = new StringBuffer();
//      int ch;
//      while ((ch = bufferedReader.read()) > -1) {
//        stringBuffer.append((char) ch);
//      }
//      bufferedReader.close();
//      String returnData = stringBuffer.toString();
//      String responseCodeStr = parseXMLString(returnData, "error");
//      if (StringUtils.isBlank(responseCodeStr)) return smsSendResult;
//      int responseCode = Integer.valueOf(responseCodeStr);
//      boolean isSendSuccess = (responseCode == SmsConstant.SmsSweConstant.SMS_STATUS_SUCCESS);
//      if (isSendSuccess) { //判断短信返回值
//        String taskid = parseXMLString(returnData, "taskid");
//        int num = this.caculateSmsNum(smsSendDTO.getContent(), smsSendDTO.getReceiveMobile());
//        smsSendResult.setSmsId(taskid);
//        smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
//        smsSendResult.setSmsNum(num);
//        smsSendResult.setSmsPrice(num * SmsConstant.SMS_UNIT_PRICE);
//        //BCGOGO 发送的短信不需要扣款
//        if (smsSendDTO.getSender().equals(SenderType.Shop)){
//          this.updateSmsBalanceAfterSendSuccess(smsSendDTO, smsSendResult);
//        } else{
//          //todo bcgogo 记账
//        }
//      } else {
//        smsSendResult.setSmsNum(0);
//        smsSendResult.setSmsPrice(0);
//        smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
//      }
//      smsSendResult.setSmsResponseReason(SmsConstant.SmsSweConstant.getSmsSenderByName(responseCode));
//      LOG.debug("短信" + (isSendSuccess == true ? "" : "未") + "发送到SWE平台:[mobile:{},内容:{}]" + (isSendSuccess == true ? "" : ("[reason:" + smsSendResult.getSmsResponseReason() + "]")), smsSendDTO.getReceiveMobile(), smsSendDTO.getContent());
//      return smsSendResult;
//    } catch (Exception e) {
//      LOG.error(e.getMessage(), e);
//      throw new SmsException(BcgogoExceptionType.MsgIOException);
//    }
//  }
//
//  // 组合发送内容
//  private String combineContent(SmsSendDTO smsSendDTO) throws SmsException {
////    String key = "key=" + SmsConstant.SmsSweConstant.key;
////    String secret = "&secret=" + SmsConstant.SmsSweConstant.secret;
//    String key = "key=" + getConfigService().getConfig("SMS_SWE_KEY", ShopConstant.BC_SHOP_ID);
//    String secret = "&secret=" + getConfigService().getConfig("SMS_SWE_SECRET", ShopConstant.BC_SHOP_ID);
//    String sms_number = "&sms_number=" + smsSendDTO.getReceiveMobile();
//    String content = null;
//    String subject = "&subject=" + "统购短信";
//    try {
//      content = "&content=" + java.net.URLEncoder.encode(smsSendDTO.getContent(), "UTF-8");
//      if (tokenTime == null) {
//        tokenTime = System.currentTimeMillis();
//      }
//      if (System.currentTimeMillis() - tokenTime >= SmsConstant.SmsSweConstant.tokenTime || token == null) {
//        token = this.getToken();
//        tokenTime = System.currentTimeMillis();
//      }
//      String token = "&token=" + this.token;
//      String postData = key + secret + sms_number + content + subject + token;
//      return postData;
//    } catch (Exception e) {
//      LOG.error(e.getMessage(), e);
//      throw new SmsException(BcgogoExceptionType.MsgUnsupportedEncoding);
//    }
//
//  }
//
//  private static String token;
//  private static Long tokenTime;
//
//  //得到token值    token值不需要每次都取，各1个小时取一次
//  public String getToken() throws Exception {
////    String url = SmsConstant.SmsSweConstant.tricomUrl + "login";
//    String url = getConfigService().getConfig("SMS_SWE_TRICOM_URL", ShopConstant.BC_SHOP_ID) + "login";
//    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
//    httpURLConnection.setRequestMethod("POST");
//    httpURLConnection.setDoOutput(true);
//    OutputStream out = httpURLConnection.getOutputStream();
////    String key = "key=" + SmsConstant.SmsSweConstant.key;
////    String secret = "&secret=" + SmsConstant.SmsSweConstant.secret;
////    String username = "&username=" + SmsConstant.SmsSweConstant.username;
////    String password = "&password=" + SmsConstant.SmsSweConstant.password;
//    String key = "key=" + getConfigService().getConfig("SMS_SWE_KEY", ShopConstant.BC_SHOP_ID);
//    String secret = "&secret=" + getConfigService().getConfig("SMS_SWE_SECRET", ShopConstant.BC_SHOP_ID);
//    String username = "&username=" + getConfigService().getConfig("SMS_SWE_USERNAME", ShopConstant.BC_SHOP_ID);
//    String password = "&password=" + getConfigService().getConfig("SMS_SWE_PASSWORD", ShopConstant.BC_SHOP_ID);
//    out.write((key + username + secret + password).getBytes("UTF-8"));
//    out.flush();
//    out.close();
//    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//    StringBuffer stringBuffer = new StringBuffer();
//    int ch;
//    while ((ch = bufferedReader.read()) > -1) {
//      stringBuffer.append((char) ch);
//    }
//    bufferedReader.close();
//    String returnData = stringBuffer.toString();
//    String token = parseXMLString(returnData, "accessToken");
//    return token;
//  }
//
//  public String parseXMLString(String context, String name) throws SmsException {
//    return XMLParser.getRootElement(context, name);
//  }
//
////  public static void main(String[] args) {
////    String receiveMobile = "234,353,554,654,323,243,545,843,434,343,178";
////    String[] sendMobiles = SmsUtil.groupingSmsMobile(receiveMobile, 10);
////    for (String str : sendMobiles) {
////      System.out.println(str);
////    }
////    for (int i = 0; i < sendMobiles.length; i++) {
////      if (i == 1) {
////        receiveMobile = com.bcgogo.utils.StringUtil.subString(receiveMobile, i * (sendMobiles[i - 1 <= 0 ? 0 : i - 1].length() + 1), receiveMobile.length());
////      }
////    }
////    System.out.println(receiveMobile);
////  }
//}
