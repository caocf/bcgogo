package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-28
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
public enum PrintType {
  PURCHASE("采购单"),
  INVENTORY("入库单"),
  SALE("销售单"),
  BEAUTY("施工单"),
  GOOD_RETURN("退货单"),
  DEBT("欠款结算单"),
  REVENUE("营收统计单"),
  MemberCard("购卡续卡"),
  WASH_TICKET("洗车小票"),
  WASH_BEAUTY("洗车美容单");

  String type;

  PrintType(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return this.type;
  }
}
