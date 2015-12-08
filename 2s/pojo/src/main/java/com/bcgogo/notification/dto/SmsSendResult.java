package com.bcgogo.notification.dto;

import com.bcgogo.enums.notification.SmsSendKind;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-28
 * Time: 下午3:25
 */
public class SmsSendResult {
  private String smsResponse;
  private String smsResponseReason;
  private String smsId;
  private float smsPrice;
  private int smsNum;
  private String content;
  private SmsSendKind smsSendKind;
  private List<Long> sendSmsJobIds = new ArrayList<Long>();

  public float getSmsPrice() {
    return smsPrice;
  }

  public void setSmsPrice(float smsPrice) {
    this.smsPrice = smsPrice;
  }

  public int getSmsNum() {
    return smsNum;
  }

  public void setSmsNum(int smsNum) {
    this.smsNum = smsNum;
  }

  public String getSmsId() {
    return smsId;
  }

  public void setSmsId(String smsId) {
    this.smsId = smsId;
  }

  public String getSmsResponse() {
    return smsResponse;
  }

  public void setSmsResponse(String smsResponse) {
    this.smsResponse = smsResponse;
  }

  public boolean isSuccess() {
    return smsResponse != null && smsResponse.equals(SmsConstant.SMS_STATUS_SUCCESS);
  }

  public String getSmsResponseReason() {
    return smsResponseReason;
  }

  public void setSmsResponseReason(String smsResponseReason) {
    if (!StringUtil.isEmpty(smsResponseReason) && smsResponseReason.length() > 500)
      smsResponseReason = StringUtil.subString(smsResponseReason, 0, 500);
    this.smsResponseReason = smsResponseReason;
  }


  public SmsSendKind getSmsSendKind() {
    return smsSendKind;
  }

  public void setSmsSendKind(SmsSendKind smsSendKind) {
    this.smsSendKind = smsSendKind;
  }

  @Override
  public String toString() {
    return "SmsSendResult{" +
        "smsResponse='" + smsResponse + '\'' +
        ", smsResponseReason='" + smsResponseReason + '\'' +
        ", smsId='" + smsId + '\'' +
        ", smsPrice=" + smsPrice +
        ", smsNum=" + smsNum +
        ", smsSendKind=" + smsSendKind +
        '}';
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<Long> getSendSmsJobIds() {
    return sendSmsJobIds;
  }

  public void setSendSmsJobIds(List<Long> sendSmsJobIds) {
    this.sendSmsJobIds = sendSmsJobIds;
  }
}
