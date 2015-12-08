package com.bcgogo.enums.txn.finance;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 上午10:45
 * 支付方式
 */
public enum PaymentMethod {
  ONLINE_PAYMENT,//在线支付
  @Deprecated
  CUP_TRANSFER, //银联转账
  DOOR_CHARGE   //上门收取
}
