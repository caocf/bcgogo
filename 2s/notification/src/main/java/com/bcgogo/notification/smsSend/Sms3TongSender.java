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
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
///**
// * User: ZhangJuntao
// * Date: 12-3-28
// * Time: 下午3:27
// */
////三通短信供应商
//public class Sms3TongSender extends AbstractSender {
//  private static final Logger LOG = LoggerFactory.getLogger(Sms3TongSender.class);
//
//  @Override
//  //AbstractSender发送方法实现
//  public SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws Exception {
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
//  public SmsSendResult sendSmsImpl(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) throws Exception {
//    String url = getConfigService().getConfig("SMS_3TONG_TRICOM_URL", ShopConstant.BC_SHOP_ID);
//    //发送 短信
//    HttpURLConnection httpURLConnection = null;
//    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
//    httpURLConnection.setRequestMethod("POST");
//    httpURLConnection.setDoOutput(true);
//    OutputStream out = httpURLConnection.getOutputStream();
//    out.write(combineContent(smsSendDTO).getBytes("UTF-8"));
//    out.flush();
//    out.close();
//    //接受返回信息
//    smsSendResult = getSmsSendResult(httpURLConnection, smsSendResult, smsSendDTO);
//    return smsSendResult;
//  }
//
//  //接受返回信息
//  private SmsSendResult getSmsSendResult(HttpURLConnection httpURLConnection, SmsSendResult smsSendResult, SmsSendDTO smsSendDTO) throws Exception {
//    BufferedReader bufferedReader = null;
//    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//    StringBuffer stringBuffer = new StringBuffer();
//    int ch;
//    while ((ch = bufferedReader.read()) > -1) {
//      stringBuffer.append((char) ch);
//    }
//    bufferedReader.close();
//    String returnData = stringBuffer.toString();
//    String responseCodeStr = XMLParser.getRootElement(returnData, "response");
//    if (StringUtils.isBlank(responseCodeStr)) return smsSendResult;
//    int responseCode = Integer.valueOf(responseCodeStr);
//    boolean isSendSuccess = responseCode > SmsConstant.Sms3TongConstant.SMS_RESPONSE_ERROR;
//    if (isSendSuccess) { //判断短信返回值
//      int num = caculateSmsNum(smsSendDTO.getContent(), smsSendDTO.getReceiveMobile());
//      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
//      smsSendResult.setSmsId(XMLParser.getRootElement(returnData, "smsID"));
//      smsSendResult.setSmsNum(num);
//      smsSendResult.setSmsPrice(num * SmsConstant.SMS_UNIT_PRICE);
//      //BCGOGO 发送的短信不需要扣款
//      if (smsSendDTO.getSender().equals(SenderType.Shop)) {
//        this.updateSmsBalanceAfterSendSuccess(smsSendDTO, smsSendResult);
//      } else {
//        //todo bcgogo 记账
//      }
//    } else {
//      smsSendResult.setSmsNum(0);
//      smsSendResult.setSmsPrice(0);
//      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
//    }
//    smsSendResult.setSmsResponseReason(SmsConstant.Sms3TongConstant.getSmsSenderByName(responseCode));
//    LOG.debug("短信" + (isSendSuccess == true ? "" : "未") + "发送到3通平台:[mobile:{},内容:{}]" + (isSendSuccess == true ? "" : ("[reason:" + smsSendResult.getSmsResponseReason() + "]")), smsSendDTO.getReceiveMobile(), smsSendDTO.getContent());
//    return smsSendResult;
//  }
//
//  // 组合发送内容
//  private String combineContent(SmsSendDTO smsSendDTO) throws SmsException {
////    String account = "Account=" + SmsConstant.Sms3TongConstant.account;
////    String password = "&Password=" + SmsConstant.Sms3TongConstant.password;
//    String account = "Account=" + getConfigService().getConfig("SMS_3TONG_ACCOUNT", ShopConstant.BC_SHOP_ID);
//    String password = "&Password=" + getConfigService().getConfig("SMS_3TONG_PASSWORD", ShopConstant.BC_SHOP_ID);
//    String content = null;
//    try {
//      content = "&Content=" + java.net.URLEncoder.encode(smsSendDTO.getContent(), "UTF-8");
//    } catch (UnsupportedEncodingException e) {
//      throw new SmsException(BcgogoExceptionType.MsgUnsupportedEncoding);
//    }
//    String phone = "&Phone=" + smsSendDTO.getReceiveMobile();
//    String postData = account + password + phone + content;
//    return postData;
//  }
//}
