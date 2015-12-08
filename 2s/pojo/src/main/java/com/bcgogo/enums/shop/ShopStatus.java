package com.bcgogo.enums.shop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-28
 * Time: 下午10:53
 * 店铺升级状态
 */
public enum ShopStatus {
  NO_INTENTION("无意向"),
  LATENT("潜在"),
  INTENTION("意向"),
  CHECK_PENDING("待审核"),
  CHECK_PENDING_REJECTED("审核拒绝"),
  REGISTERED_TRIAL("注册试用"),
  REGISTERED_PAID("缴费使用");

  static List<ShopStatus> SHOP_TRIAL_AND_PAID;
  static List<ShopStatus> SHOP_TRIAL_AND_PAID_AND_PENDING;
  static List<String> SHOP_TRIAL_AND_PAID_STRING_LIST;

  static {
    if (SHOP_TRIAL_AND_PAID_AND_PENDING == null) {
      SHOP_TRIAL_AND_PAID_AND_PENDING = new ArrayList<ShopStatus>();
      SHOP_TRIAL_AND_PAID_AND_PENDING.add(REGISTERED_TRIAL);
      SHOP_TRIAL_AND_PAID_AND_PENDING.add(REGISTERED_PAID);
      SHOP_TRIAL_AND_PAID_AND_PENDING.add(CHECK_PENDING);
      SHOP_TRIAL_AND_PAID_AND_PENDING.add(CHECK_PENDING_REJECTED);
    }
    if (SHOP_TRIAL_AND_PAID == null) {
      SHOP_TRIAL_AND_PAID = new ArrayList<ShopStatus>();
      SHOP_TRIAL_AND_PAID.add(REGISTERED_TRIAL);
      SHOP_TRIAL_AND_PAID.add(REGISTERED_PAID);
    }
    if (SHOP_TRIAL_AND_PAID_STRING_LIST == null) {
      SHOP_TRIAL_AND_PAID_STRING_LIST = new ArrayList<String>();
      SHOP_TRIAL_AND_PAID_STRING_LIST.add(REGISTERED_TRIAL.name());
      SHOP_TRIAL_AND_PAID_STRING_LIST.add(REGISTERED_PAID.name());
    }
  }

  public static boolean isAuditedShopStatus(ShopStatus status) {
    return status == REGISTERED_TRIAL || status == REGISTERED_PAID;
  }

  public static boolean isRegistrationTrial(ShopStatus status) {
    return status == REGISTERED_TRIAL;
  }

  public static boolean isRegistrationPaid(ShopStatus status) {
    return status == REGISTERED_PAID;
  }

  public static List<ShopStatus> getShopTrialAndPendingAndPaid() {
    return SHOP_TRIAL_AND_PAID_AND_PENDING;
  }

  //已经使用一发软件
  public static List<ShopStatus> getShopTrialAndPaid() {
    return SHOP_TRIAL_AND_PAID;
  }

  public static List<String> getShopTrialAndPaidString() {
    return SHOP_TRIAL_AND_PAID_STRING_LIST;
  }

  private String value;

  ShopStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

}
