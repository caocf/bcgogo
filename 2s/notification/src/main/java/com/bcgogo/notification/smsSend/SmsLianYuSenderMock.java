package com.bcgogo.notification.smsSend;

import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.notification.dto.SmsSendResult;
import com.bcgogo.utils.SmsConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* User: ZhangJuntao
* Date: 12-9-5
* Time: 下午9:42
*/
public class SmsLianYuSenderMock extends SmsLianYuSender {
  private static final Logger LOG = LoggerFactory.getLogger(SmsLianYuSenderMock.class);
  public static int code = 0;

  public static void setCode(int code) {
    SmsLianYuSenderMock.code = code;
  }

  @Override
  public SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws Exception {
    groupSmsSends(smsSendDTO, SmsConstant.SmsLianYuConstant.mobileNumber, SmsConstant.SmsLianYuConstant.SEPARATOR);
    SmsSendResult smsSendResult = validateLianYuSendSms(smsSendDTO);   //商户、余额、数据不完整 检查
    smsSendResult.setSendSmsJobIds(smsSendDTO.getSendSmsJobIds());
    if (!smsSendResult.isSuccess()) {
      return smsSendResult;
    }
    smsSendResult = sendSmsImpl(smsSendDTO, smsSendResult);
    smsSendResult.setSmsSendKind(SmsSendKind.LIAN_YU_MOCK);
    return smsSendResult;
  }

  protected SmsSendResult sendSmsImpl(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) throws Exception {
    if (StringUtils.isBlank(smsSendDTO.getShopName())) {
      smsSendDTO.setShopName(SmsConstant.SmsLianYuConstant.DEFAULT_SENDER_NAME);
    }
    smsSendDTO.setContent("【" + smsSendDTO.getShopName() + "】" + smsSendDTO.getContent());
    int responseCode = sendSMSEx(smsSendDTO.getReceiveMobile(), smsSendDTO.getContent(), "", "GBK", 3, smsSendDTO.getId(), smsSendDTO.getSmsChannel());
    boolean isSuccess = (responseCode == SmsConstant.SmsLianYuConstant.SMS_STATUS_SUCCESS);
    String mobiles = smsSendDTO.getReceiveMobile();
    if (isSuccess) { //判断短信返回值
      int num = caculateSmsNum(smsSendDTO.getContent(), mobiles);
      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_SUCCESS);
      if (smsSendDTO.getId() == null) {
        smsSendResult.setSmsId(String.valueOf(System.nanoTime()));
      } else {
        smsSendResult.setSmsId(String.valueOf(smsSendDTO.getId()));
      }
      smsSendResult.setSmsNum(num);
      smsSendResult.setSmsPrice(num * SmsConstant.SMS_UNIT_PRICE);
      //注册短信
      //BCGOGO 发送的短信不需要扣款
      if (smsSendDTO.getSender().equals(SenderType.Shop)) {
        this.updateSmsBalanceAfterSendSuccess(smsSendDTO, smsSendResult);
      }
    } else {
      smsSendResult.setSmsNum(0);
      smsSendResult.setSmsPrice(0);
      smsSendResult.setSmsResponseReason("mock test");
      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
    }
    smsSendResult.setContent(smsSendDTO.getContent());
    smsSendResult.setSmsResponseReason("[" + responseCode + ":mock test]");
    if (LOG.isDebugEnabled())
      LOG.debug("短信" + (isSuccess ? "" : "未") + "发送到联逾平台" + smsSendDTO.getSmsChannel().getValue() + ":[mobile:{},内容:{}]" + (isSuccess ? "" : ("[reason:" + smsSendResult.getSmsResponseReason() + "]")), mobiles, smsSendDTO.getContent());
    return smsSendResult;
  }

  public int sendSMSEx(String mobiles, String smsContent, String addSerial, String srcCharset, int smsPriority, Long smsId, SmsChannel channel) {
    return code;
  }
}
