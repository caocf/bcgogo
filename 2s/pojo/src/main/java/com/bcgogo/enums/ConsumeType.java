package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-21
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public enum ConsumeType {
  MONEY("金额"),
  TIMES("计次划卡"),
  COUPON("消费券");

  String type;

  ConsumeType(String type){
    this.type = type;
  }

  public String getType(){
    return this.type;
  }
}
