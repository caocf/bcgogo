package com.bcgogo.txn.model.finance;

import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.enums.sms.StatType;
import com.bcgogo.enums.txn.finance.SmsCategory;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.ShopSmsRecordDTO;

import javax.persistence.*;
import javax.ws.rs.core.Response;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 * 店面账单详情
 */
@Entity
@Table(name = "shop_sms_record")
public class ShopSmsRecord extends LongIdentifier {
  private Long shopId;
  private SmsCategory smsCategory;
  private Double balance = 0.0d;
  private Long number = 0L;
  private Long operateTime;
  private Long operatorId;
  private Long refundTime;
  private Boolean flag;    //true或者null：业务生成  false:初始化生成的数据记录
  private Long smsId;
  private SmsSendScene smsSendScene;
  private StatType statType;


  public ShopSmsRecord() {
    super();
  }

  public ShopSmsRecord(ShopSmsRecordDTO dto) {
    this.setShopId(dto.getShopId());
    this.setBalance(dto.getBalance());
    this.setNumber(dto.getNumber());
    this.setSmsCategory(dto.getSmsCategory());
    this.setOperatorId(dto.getOperatorId());
    this.setRefundTime(dto.getRefundTime());
    this.setOperateTime(dto.getOperateTime() == null ? System.currentTimeMillis() : dto.getOperateTime());
    this.setSmsId(dto.getSmsId());
    this.setSmsSendScene(dto.getSmsSendScene());
    this.setStatType(dto.getStatType());
  }

  public ShopSmsRecord(long shopId, double balance, long number) {
    this.setShopId(shopId);
    this.setBalance(balance);
    this.setNumber(number);
    this.setOperateTime(System.currentTimeMillis());
  }

  public ShopSmsRecordDTO toDTO() {
    ShopSmsRecordDTO dto = new ShopSmsRecordDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());
    dto.setBalance(this.getBalance());
    dto.setNumber(this.getNumber());
    dto.setSmsCategory(this.getSmsCategory());
    dto.setOperateTime(this.getOperateTime());
    dto.setOperatorId(this.getOperatorId());
    dto.setRefundTime(this.getRefundTime());
    dto.setSmsId(this.getSmsId());
    dto.setSmsSendScene(this.getSmsSendScene());
    dto.setStatType(this.getStatType());
    return dto;
  }

  public void fromDTO(ShopSmsRecordDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());
    this.setBalance(dto.getBalance());
    this.setNumber(dto.getNumber());
    this.setSmsCategory(dto.getSmsCategory());
    this.setOperatorId(dto.getOperatorId());
    this.setRefundTime(dto.getRefundTime());
    this.setOperateTime(dto.getOperateTime());
    this.setSmsSendScene(dto.getSmsSendScene());
    this.setStatType(dto.getStatType());
    this.setSmsId(dto.getSmsId());
  }

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "refund_time")
  public Long getRefundTime() {
    return refundTime;
  }

  public void setRefundTime(Long refundTime) {
    this.refundTime = refundTime;
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "sms_category")
  public SmsCategory getSmsCategory() {
    return smsCategory;
  }

  public void setSmsCategory(SmsCategory smsCategory) {
    this.smsCategory = smsCategory;
  }

  @Column(name = "balance")
  public Double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  @Column(name = "number")
  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

  @Column(name = "operate_time")
  public Long getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Long operateTime) {
    this.operateTime = operateTime;
  }

  @Column(name = "flag")

  public Boolean getFlag() {
    return flag;
  }

  public void setFlag(Boolean flag) {
    this.flag = flag;
  }



  @Column(name = "sms_id")
  public Long getSmsId() {
    return smsId;
  }

  public void setSmsId(Long smsId) {
    this.smsId = smsId;
  }


  @Enumerated(EnumType.STRING)
  @Column(name = "sms_send_scene")

  public SmsSendScene getSmsSendScene() {
    return smsSendScene;
  }

  public void setSmsSendScene(SmsSendScene smsSendScene) {
    this.smsSendScene = smsSendScene;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "stat_type")

  public StatType getStatType() {
    return statType;
  }

  public void setStatType(StatType statType) {
    this.statType = statType;
  }
}
