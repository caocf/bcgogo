package com.bcgogo.txn.dto.finance;

/**
 * User: ZhangJuntao
 * Date: 13-3-26
 * Time: 下午3:13
 */
public class ShopSmsAccountDTO {
  private Long id;
  private Long shopId;
  private String shopName;

  private Double rechargeBalance = 0.0d;
  private Long rechargeNumber = 0L;

  private Double handSelBalance = 0.0d;
  private Long handSelNumber = 0L;

  private Double consumptionBalance = 0.0d;
  private Long consumptionNumber = 0L;

  private Double currentBalance = 0.0d;
  private Long currentNumber = 0L;

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

  public Double getRechargeBalance() {
    return rechargeBalance;
  }

  public void setRechargeBalance(Double rechargeBalance) {
    this.rechargeBalance = rechargeBalance;
  }

  public Long getRechargeNumber() {
    return rechargeNumber;
  }

  public void setRechargeNumber(Long rechargeNumber) {
    this.rechargeNumber = rechargeNumber;
  }

  public Double getHandSelBalance() {
    return handSelBalance;
  }

  public void setHandSelBalance(Double handSelBalance) {
    this.handSelBalance = handSelBalance;
  }

  public Long getHandSelNumber() {
    return handSelNumber;
  }

  public void setHandSelNumber(Long handSelNumber) {
    this.handSelNumber = handSelNumber;
  }

  public Double getConsumptionBalance() {
    return consumptionBalance;
  }

  public void setConsumptionBalance(Double consumptionBalance) {
    this.consumptionBalance = consumptionBalance;
  }

  public Long getConsumptionNumber() {
    return consumptionNumber;
  }

  public void setConsumptionNumber(Long consumptionNumber) {
    this.consumptionNumber = consumptionNumber;
  }

  public Double getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(Double currentBalance) {
    this.currentBalance = currentBalance;
  }

  public Long getCurrentNumber() {
    return currentNumber;
  }

  public void setCurrentNumber(Long currentNumber) {
    this.currentNumber = currentNumber;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }
}
