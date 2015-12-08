package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-12-27
 * Time: 下午5:55
 * To change this template use File | Settings | File Templates.
 */
public enum PaymentWay {
    CHINA_PAY("银联"),
    CASH("现金");
    String way;
    PaymentWay(String way) {
      this.way = way;
    }

  public String getWay() {
    return way;
  }
}
