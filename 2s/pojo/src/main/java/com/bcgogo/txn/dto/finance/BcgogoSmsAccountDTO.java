package com.bcgogo.txn.dto.finance;

/**
 * Created with IntelliJ IDEA.
 * User: hans
 * Date: 13-4-7
 * Time: 上午11:37
 */
public class BcgogoSmsAccountDTO {
  private String type;
  private Double totalRechargeBalance = 0.0d;
  private Double refundBalance = 0.0d;
  private Long refundNumber = 0L;
  private Long totalRechargeNumber = 0L;
  private Long handSelNumber = 0L;
  private Long consumptionNumber = 0L;
  private Long surplusNumber = 0L;

  public void calculateBcgogoSurplus() {
    surplusNumber = totalRechargeNumber - handSelNumber - consumptionNumber;
  }

  public void calculateShopSurplus(Long handSelNumber) {
    surplusNumber = totalRechargeNumber + (handSelNumber == null ? 0 : handSelNumber) - consumptionNumber;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getTotalRechargeBalance() {
    return totalRechargeBalance;
  }

  public void setTotalRechargeBalance(Double totalRechargeBalance) {
    this.totalRechargeBalance = totalRechargeBalance == null ? 0.0d : totalRechargeBalance;
  }

  public Long getTotalRechargeNumber() {
    return totalRechargeNumber;
  }

  public void setTotalRechargeNumber(Long totalRechargeNumber) {
    this.totalRechargeNumber = totalRechargeNumber == null ? 0l : totalRechargeNumber;
  }

  public Long getHandSelNumber() {
    return handSelNumber;
  }

  public void setHandSelNumber(Long handSelNumber) {
    this.handSelNumber = handSelNumber;
  }

  public Long getConsumptionNumber() {
    return consumptionNumber;
  }

  public void setConsumptionNumber(Long consumptionNumber) {
    this.consumptionNumber = consumptionNumber;
  }

  public Long getSurplusNumber() {
    return surplusNumber;
  }

  public void setSurplusNumber(Long surplusNumber) {
    this.surplusNumber = surplusNumber;
  }

  public Double getRefundBalance() {
    return refundBalance;
  }

  public void setRefundBalance(Double refundBalance) {
    this.refundBalance = refundBalance;
  }

  public Long getRefundNumber() {
    return refundNumber;
  }

  public void setRefundNumber(Long refundNumber) {
    this.refundNumber = refundNumber;
  }
}
