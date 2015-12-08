package com.bcgogo.txn.model.finance;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.finance.ShopSmsAccountDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午8:58
 * 店面短信账单
 */
@Entity
@Table(name = "shop_sms_account")
public class ShopSmsAccount extends LongIdentifier {
  private Long shopId;
  private Double rechargeBalance = 0.0d;
  private Long rechargeNumber = 0L;

  private Double handSelBalance = 0.0d;
  private Long handSelNumber = 0L;

  private Double consumptionBalance = 0.0d;
  private Long consumptionNumber = 0L;


  private Double currentBalance = 0.0d;
  private Long currentNumber = 0L;


  public ShopSmsAccount() {
    super();
  }

  public ShopSmsAccount(long shopId) {
    this.setShopId(shopId);
  }

  public ShopSmsAccountDTO toDTO() {
    ShopSmsAccountDTO dto = new ShopSmsAccountDTO();
    dto.setId(this.getId());
    dto.setShopId(this.getShopId());

    dto.setHandSelBalance(this.getHandSelBalance());
    dto.setHandSelNumber(this.getHandSelNumber());

    dto.setConsumptionBalance(this.getConsumptionBalance());
    dto.setConsumptionNumber(this.getConsumptionNumber());

    dto.setCurrentBalance(this.getCurrentBalance());
    dto.setCurrentNumber(this.getCurrentNumber());

    dto.setRechargeBalance(this.getRechargeBalance());
    dto.setRechargeNumber(this.getRechargeNumber());
    return dto;
  }

  public void fromDTO(ShopSmsAccountDTO dto) {
    this.setId(dto.getId());
    this.setShopId(dto.getShopId());

    this.setHandSelBalance(dto.getHandSelBalance());
    this.setHandSelNumber(dto.getHandSelNumber());

    this.setConsumptionBalance(dto.getConsumptionBalance());
    this.setConsumptionNumber(dto.getConsumptionNumber());

    this.setCurrentBalance(dto.getCurrentBalance());
    this.setCurrentNumber(dto.getCurrentNumber());

    this.setRechargeBalance(dto.getRechargeBalance());
    this.setRechargeNumber(dto.getRechargeNumber());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "recharge_balance")
  public Double getRechargeBalance() {
    return rechargeBalance;
  }

  public void setRechargeBalance(Double rechargeBalance) {
    this.rechargeBalance = rechargeBalance;
  }

  @Column(name = "recharge_number")
  public Long getRechargeNumber() {
    return rechargeNumber;
  }

  public void setRechargeNumber(Long rechargeNumber) {
    this.rechargeNumber = rechargeNumber;
  }

  @Column(name = "handsel_balance")
  public Double getHandSelBalance() {
    return handSelBalance;
  }

  public void setHandSelBalance(Double handSelBalance) {
    this.handSelBalance = handSelBalance;
  }

  @Column(name = "handsel_number")
  public Long getHandSelNumber() {
    return handSelNumber;
  }

  public void setHandSelNumber(Long handSelNumber) {
    this.handSelNumber = handSelNumber;
  }

  @Column(name = "consumption_balance")
  public Double getConsumptionBalance() {
    return consumptionBalance;
  }

  public void setConsumptionBalance(Double consumptionBalance) {
    this.consumptionBalance = consumptionBalance;
  }

  @Column(name = "consumption_number")
  public Long getConsumptionNumber() {
    return consumptionNumber;
  }

  public void setConsumptionNumber(Long consumptionNumber) {
    this.consumptionNumber = consumptionNumber;
  }

  @Column(name = "current_balance")
  public Double getCurrentBalance() {
    return currentBalance;
  }

  public void setCurrentBalance(Double currentBalance) {
    this.currentBalance = currentBalance;
  }

  @Column(name = "current_number")
  public Long getCurrentNumber() {
    return currentNumber;
  }

  public void setCurrentNumber(Long currentNumber) {
    this.currentNumber = currentNumber;
  }

}
