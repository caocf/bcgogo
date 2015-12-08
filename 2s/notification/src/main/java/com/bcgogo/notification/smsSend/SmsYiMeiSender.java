package com.bcgogo.notification.smsSend;

import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.notification.client.ClientFactory;
import com.bcgogo.notification.client.yimei.YimeiSmsSendParam;
import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.notification.dto.SmsSendResult;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.XMLParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 亿美  分通道(行业与营销分开)
 * User: ZhangJuntao
 * Date: 12-9-5
 * Time: 下午9:41
 */
public class SmsYiMeiSender extends AbstractSender {
  private static final Logger LOG = LoggerFactory.getLogger(SmsYiMeiSender.class);

  @Override
  public SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws Exception {
    groupSmsSends(smsSendDTO, SmsConstant.SmsYiMeiConstant.mobileNumber, SmsConstant.SmsYiMeiConstant.SEPARATOR);
    SmsSendResult smsSendResult = validateYimeiSendSms(smsSendDTO);   //商户、余额、数据不完整 检查
    smsSendResult.setSendSmsJobIds(smsSendDTO.getSendSmsJobIds());
    if (!smsSendResult.isSuccess()) {
      return smsSendResult;
    }
    smsSendResult = sendSmsImpl(smsSendDTO, smsSendResult);
    smsSendResult.setSmsSendKind(SmsSendKind.YI_MEI);
    smsSendResult.setContent(smsSendDTO.getContent());
    return smsSendResult;
  }

  protected SmsSendResult sendSmsImpl(SmsSendDTO smsSendDTO, SmsSendResult smsSendResult) throws Exception {
    if (StringUtils.isBlank(smsSendDTO.getShopName())) {
      smsSendDTO.setShopName(SmsConstant.SmsYiMeiConstant.DEFAULT_SENDER_NAME);
    }
    smsSendDTO.setContent("【" + smsSendDTO.getShopName() + "】"+smsSendDTO.getContent());
    YimeiSmsSendParam param = new YimeiSmsSendParam();
    param.setSeqid(String.valueOf(smsSendDTO.getId()));
    param.setSmsChannel(smsSendDTO.getSmsChannel());
    param.setPhone(smsSendDTO.getReceiveMobile());
    param.setMessage(smsSendDTO.getContent());
    String response = ClientFactory.getYimeiHttpSmsClient().sendSMS(param);
    String code = XMLParser.getRootElement(response, "error");
    if (StringUtils.isBlank(code)) return smsSendResult;
    int responseCode = Integer.valueOf(code);
    boolean isSuccess = (responseCode == SmsConstant.SmsYiMeiConstant.SMS_STATUS_SUCCESS);
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
      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
    }
    smsSendResult.setContent(smsSendDTO.getContent());
    String message = XMLParser.getRootElement(response, "message");
    smsSendResult.setSmsResponseReason("[" + code + ":" +
        SmsConstant.SmsYiMeiConstant.getSmsResponseCodeMap(responseCode)
        + (StringUtil.isEmpty(message) ? "" : ("," + message)) + "]");
    if (LOG.isDebugEnabled())
      LOG.debug("短信" + (isSuccess ? "" : "未") + "发送到亿美平台" + smsSendDTO.getSmsChannel().getValue() + ":[mobile:{},内容:{}]" + (isSuccess ? "" : ("[reason:" + smsSendResult.getSmsResponseReason() + "]")), mobiles, smsSendDTO.getContent());
    return smsSendResult;
  }

  protected SmsSendResult validateYimeiSendSms(SmsSendDTO smsSendDTO) {
    SmsSendResult smsSendResult = validateSendSms(smsSendDTO);   //商户、余额、数据不完整 检查
    if (!smsSendResult.isSuccess()) {
      return smsSendResult;
    }
    if (smsSendDTO.getSmsChannel() == null) {
      smsSendResult.setSmsResponse(SmsConstant.SMS_STATUS_FAIL);
      smsSendResult.setSmsResponseReason("亿美运营商通道类型为空。");
      return smsSendResult;
    }
    return smsSendResult;
  }


}

