package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public enum PayMethod {
  CASH("现金"),
  CHEQUE("支票"),
  DEPOSIT("预付款"),
  CUSTOMER_DEPOSIT("预收款"),
  MEMBER_BALANCE_PAY("会员储值"),
  BANK_CARD("银行卡"),

  STATEMENT_ACCOUNT("对账"),//如果某个单据被对账了 支付方式存入对账

  PURCHASE_RETURN_DEPOSIT("deposit"),
  PURCHASE_RETURN_SURPAY("surPay"),

  COUPON("消费券")   //仅用于查询中心
  ;

  String value;

  PayMethod(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
