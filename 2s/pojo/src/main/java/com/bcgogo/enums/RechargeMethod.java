package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-27
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public enum RechargeMethod {
  CUSTOMER_RECHARGE("客户充值"),
  CRM_RECHARGE("后台充值");

  String rechargeMethod;
  RechargeMethod(String rechargeMethod) {
     this.rechargeMethod = rechargeMethod;
  }

  public String getRechargeMethod() {
    return rechargeMethod;
  }
}
