package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-7
 * Time: 下午1:37
 * To change this template use File | Settings | File Templates.
 */
public enum RemindEventType {
  //待办事项
  REPAIR("维修美容提醒"),
  TXN("进销存提醒"),
  DEBT("客户欠款提醒"),
  CUSTOMER_SERVICE("客户服务提醒"),
  MEMBER_SERVICE("会员服务到期提醒"),

  //待办单据
  TODO_SALE_ORDER("待办销售单"),
  TODO_SALE_RETURN_ORDER("待办销售退货单"),
  TODO_PURCHASE_ORDER("待办采购单"),
  TODO_PURCHASE_RETURN_ORDER("待办入库退货单");

  private final String name;

  private RemindEventType(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
