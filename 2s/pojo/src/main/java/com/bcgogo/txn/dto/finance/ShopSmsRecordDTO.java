package com.bcgogo.txn.dto.finance;

import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.notification.dto.OutBoxDTO;
import com.bcgogo.notification.dto.SmsJobDTO;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;

/**
 * User: ZhangJuntao
 * Date: 13-3-26
 * Time: 下午3:13
 */
public class ShopSmsRecordDTO {
  private Long id;
  private Long shopId;
  private String shopName;
  private SmsCategory smsCategory;
  private Double balance = 0.0d;
  private Long number = 0L;
  private Long operateTime;
  private Long operatorId;
  private Long refundTime;
  private String operateTimeStr;
  private Long smsId;
  private SmsSendScene smsSendScene;
  private StatType statType;
  private String smsSendSceneStr;

  public String getSmsSendSceneStr() {
    return smsSendSceneStr;
  }

  public void setSmsSendSceneStr(String smsSendSceneStr) {
    this.smsSendSceneStr = smsSendSceneStr;
  }

  public Long getRefundTime() {
    return refundTime;
  }

  public void setRefundTime(Long refundTime) {
    this.refundTime = refundTime;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public SmsCategory getSmsCategory() {
    return smsCategory;
  }

  public void setSmsCategory(SmsCategory smsCategory) {
    this.smsCategory = smsCategory;
  }

  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
    if(operateTime != null) {
      setOperateTimeStr(DateUtil.dateLongToStr(operateTime,DateUtil.DATE_STRING_FORMAT_DAY_HOUR_MIN));
    }
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getOperateTimeStr() {
    return operateTimeStr;
  }

  public void setOperateTimeStr(String operateTimeStr) {
    this.operateTimeStr = operateTimeStr;
  }

  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  public Long getSmsId() {
    return smsId;
  }

  public void setSmsId(Long smsId) {
    this.smsId = smsId;
  }

  public StatType getStatType() {
    return statType;
  }

  public void setStatType(StatType statType) {
    this.statType = statType;
  }

  public void fromSmsJob(SmsJobDTO smsJob) {
    this.setShopId(smsJob.getShopId());
    this.setSmsCategory(SmsCategory.SHOP_CONSUME);
    this.setOperatorId(smsJob.getUserId());
    this.setOperateTime(smsJob.getStartTime());
    this.setSmsSendScene(smsJob.getSmsSendScene());
    this.setSmsId(smsJob.getSmsId());
    this.setStatType(StatType.ONE_TIME);
  }
}
