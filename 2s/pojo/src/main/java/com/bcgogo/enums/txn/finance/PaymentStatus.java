package com.bcgogo.enums.txn.finance;

/**
 * User: ZhangJuntao
 * Date: 13-3-23
 * Time: 下午1:24
 */
public enum PaymentStatus {
  PARTIAL_PAYMENT,
  FULL_PAYMENT,//已付款 或者 待发货
  NON_PAYMENT,
  SHIPPED,//已发货
  CANCELED//交易取消
}
