package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-11-12
 * Time: 下午2:57
 * 使用到可以自己增加  比如: 作废单据操作   INVALID_ORDER("作废单据")
 */
public enum OperationTypes {
  CREATE("创建"),
  UPDATE("更新"), //单据更新，包括施工单改单
  DELETE("删除"),

  ACCEPT("接受"),
  AUTO_ACCEPT("自动接受"), //预约单三十分钟自动接受
  REFUSE("拒绝"),
  SETTLE("结算"),
  DEBT_SETTLE("欠款结算"),
  INVALID("作废"),
  DISPATCH("发货"),
  SELL_STOP("中止销售"),

  OFFLINE_PAY("线下支付"),
  AUDIT_PASS("审核通过"),
  CHANGE_PRICE("改价"),


  FINISH("完工"), //施工单完工
	OUT_STORAGE("出库"),//领料出库
	RETURN_STORAGE("退料"),//领料单退料

	STORAGE("已入库"),//待办采购单入库完成
	CANCEL("取消"),//预约单，取消操作
  HANDLED("已施工"),//预约单，已施工
  STATEMENT_ACCOUNT("对账"),
  INSURANCE_ORDER("保险理赔"),
  QUALIFIED_CREDENTIALS("合格证"),
  REPAIR_ORDER_SECONDARY("结算附表");
  private final String name;
  private OperationTypes(String name){
    this.name = name;
  }

  public String getName(){
    return name;
  }
}
