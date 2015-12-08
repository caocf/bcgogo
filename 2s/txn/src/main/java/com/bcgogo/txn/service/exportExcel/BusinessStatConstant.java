package com.bcgogo.txn.service.exportExcel;

/**
 * Created with IntelliJ IDEA.
 * User: jinyuan
 * Date: 13-8-9
 * Time: 上午12:09
 * To change this template use File | Settings | File Templates.
 */
public class BusinessStatConstant {
  public static final String VEST_DATE = "单据日期";
  public static final String VEHICLE = "车辆";
  public static final String CUSTOMER_NAME = "客户";
  public static final String SERVICE_CONTENT = "施工项目";
  public static final String SERVICE_TOTAL = "施工费";
  public static final String SERVICE_TOTAL_COST = "施工成本";
  public static final String SALES_CONTENT = "材料";
  public static final String SALES_TOTAL = "材料费";
  public static final String SALES_TOTAL_COST = "材料成本";
  public static final String AFTER_MEMBER_DISCOUNT_TOTAL = "单据总额";
  public static final String ORDER_TOTAL_COST = "总成本";
  public static final String SETTLED_AMOUNT = "实收";
  public static final String DEBT = "欠款";
  public static final String ORDER_PROFIT = "毛利";
  public static final String ORDER_PROFIT_RATE = "毛利率";
  public static final String DISCOUNT = "优惠";
  public static final String ORDER_TYPE = "单据类型";
  public static final String ORDER_CONTENT = "商品";
  public static final String AMOUNT = "单据总额";
  public static final String ITEM_NAME = "内容";
  public static final String RECEIPT_NO = "单据号";
  public static final String MEMBER_NO = "消费会员卡号";
  public static final String DEBT_WASH = "挂账";
  public static final String OTHER_INCOME_TOTAL ="其他费用";
  public static final String OTHER_INCOME_TOTAL_COST_PRICE ="其他成本";
  public static final String PRODUCT_TOTAL ="商品金额";
  public static final String PRODUCT_TOTAL_COST_PRICE="商品成本";

  public static String[] repairFields = {
      VEST_DATE,
      RECEIPT_NO,
      VEHICLE,
      SERVICE_TOTAL,
      SERVICE_TOTAL_COST,
      SALES_TOTAL,
      SALES_TOTAL_COST,
      OTHER_INCOME_TOTAL,
      OTHER_INCOME_TOTAL_COST_PRICE,
      AFTER_MEMBER_DISCOUNT_TOTAL,
      ORDER_TOTAL_COST,
      SETTLED_AMOUNT,
      DEBT,
      ORDER_PROFIT,
      ORDER_PROFIT_RATE,
      DISCOUNT
  };
  public static String[] salesFields = {
      VEST_DATE,
      RECEIPT_NO,
      ORDER_TYPE,
      CUSTOMER_NAME,
      ORDER_CONTENT,
      PRODUCT_TOTAL,
      PRODUCT_TOTAL_COST_PRICE,
      OTHER_INCOME_TOTAL,
      OTHER_INCOME_TOTAL_COST_PRICE,
      AMOUNT,
      SETTLED_AMOUNT,
      DEBT,
      ORDER_TOTAL_COST,
      ORDER_PROFIT,
      ORDER_PROFIT_RATE,
      DISCOUNT
  };
  public static String[] washFields = {
      VEST_DATE,
      RECEIPT_NO,
      VEHICLE,
      ITEM_NAME,
      MEMBER_NO,
      AFTER_MEMBER_DISCOUNT_TOTAL,
      SETTLED_AMOUNT,
      DEBT_WASH,
      DISCOUNT
  };

}
