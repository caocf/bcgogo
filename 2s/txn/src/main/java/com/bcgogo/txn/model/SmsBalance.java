package com.bcgogo.txn.model;

import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-12-21
 * Time: 上午10:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "sms_balance")
@Deprecated
public class SmsBalance extends LongIdentifier {
  private Long shopId;
  private Double smsBalance;
  private Double rechargeTotal;

  public SmsBalance() {
  }

  public SmsBalance(ShopBalanceDTO shopBalanceDTO) {
    this.setId(shopBalanceDTO.getId());
    this.setShopId(shopBalanceDTO.getShopId());
    this.setSmsBalance(shopBalanceDTO.getSmsBalance());
    this.setRechargeTotal(shopBalanceDTO.getRechargeTotal());
  }

  public SmsBalance fromDTO(ShopBalanceDTO shopBalanceDTO) {

    this.setId(shopBalanceDTO.getId());
    this.setShopId(shopBalanceDTO.getShopId());
    this.setSmsBalance(shopBalanceDTO.getSmsBalance());
    this.setRechargeTotal(shopBalanceDTO.getRechargeTotal());

    return this;
  }

  public ShopBalanceDTO toDTO() {
    ShopBalanceDTO shopBalanceDTO = new ShopBalanceDTO();
    shopBalanceDTO.setId(this.getId());
    shopBalanceDTO.setShopId(this.getShopId());
    shopBalanceDTO.setSmsBalance(this.getSmsBalance());
    shopBalanceDTO.setRechargeTotal(this.getRechargeTotal());
    return shopBalanceDTO;
  }
  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "sms_balance")
  public Double getSmsBalance() {
    return smsBalance;
  }

  public void setSmsBalance(Double smsBalance) {
    this.smsBalance = smsBalance;
  }

  @Column(name = "recharge_total")
  public Double getRechargeTotal() {
    return rechargeTotal;
  }

  public void setRechargeTotal(Double rechargeTotal) {
    this.rechargeTotal = rechargeTotal;
  }
}
