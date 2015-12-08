package com.bcgogo.enums;

/**
 * 按陈总要求:流水统计页面下方列表:如果作废 非当天单据 流水统计下方列表要显示
 * 添加是否为 当天流水数据
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-12
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public enum DayType {

  STATEMENT_ACCOUNT("对账结算"),//对账单对 欠款单据进行结算后产生的流水
  STATEMENT_ORDER("对账单"),//对账单所产生的流水
  TODAY("今天"), //今天的流水数据
  OTHER_DAY("其他"),//非今天的流水数据
  NO_INIT_DAY(""); //未作初始化的流水数据


  String name;

  DayType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }


}
