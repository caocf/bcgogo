package com.bcgogo.config.model;

import com.bcgogo.enums.DonationType;
import com.bcgogo.enums.shop.RegisterType;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-2-28
 * Time: 下午5:05
 */
@Entity
@Table(name = "sms_donation_log")
public class SmsDonationLog  extends LongIdentifier {
  private Long shopId;
  private Double value;
  private DonationType donationType;
  private RegisterType registerType;
  private Long donationTime;
  private String memo;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "value")
  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  @Column(name = "donation_type")
  @Enumerated(EnumType.STRING)
  public DonationType getDonationType() {
    return donationType;
  }

  public void setDonationType(DonationType donationType) {
    this.donationType = donationType;
  }

  @Column(name = "register_type")
  @Enumerated(EnumType.STRING)
  public RegisterType getRegisterType() {
    return registerType;
  }

  public void setRegisterType(RegisterType registerType) {
    this.registerType = registerType;
  }

  @Column(name="donation_time")
  public Long getDonationTime() {
    return donationTime;
  }

  public void setDonationTime(Long donationTime) {
    this.donationTime = donationTime;
  }

  @Column(name="memo")
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  @Override
  public String toString() {
    return "SmsDonationLog{" +
        "shopId=" + shopId +
        ", value=" + value +
        ", donationType=" + donationType +
        ", registerType=" + registerType +
        ", donationTime=" + donationTime +
        ", memo='" + memo + '\'' +
        "} " + super.toString();
  }
}
