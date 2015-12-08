package com.bcgogo.enums.txn.finance;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-27
 * Time: 下午5:51
 */
public enum SmsCategory {
  //充值
  BCGOGO_RECHARGE,
  SHOP_RECHARGE,
  CRM_RECHARGE,
  //消费
  BCGOGO_CONSUME,
  SHOP_CONSUME,
  //赠送
  REGISTER_HANDSEL,
  RECOMMEND_HANDSEL,
  RECHARGE_HANDSEL,
  //退款
  REFUND;

  static SmsCategory[] handsel = new SmsCategory[]{RECOMMEND_HANDSEL, REGISTER_HANDSEL, RECHARGE_HANDSEL};

  public static SmsCategory[] getHandsel() {
    return handsel;
  }

  public static boolean isRecharge(SmsCategory smsCategory) {
    return smsCategory == BCGOGO_RECHARGE || smsCategory == SHOP_RECHARGE;
  }
}
