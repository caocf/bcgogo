package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-10-16
 * Time: 下午3:29
 * To change this template use File | Settings | File Templates.
 */
public enum RepairOrderSubmitType {
  SETTLED("settled"),//结算
  DISPATCH("dispatch"),  //派单
  CHANGE("change"),  //改单
  DONE("done");   //完工

  String name;
  RepairOrderSubmitType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
