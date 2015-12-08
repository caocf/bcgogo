package com.bcgogo.constant;

import com.bcgogo.enums.RemindEventType;

/**
 * Memcache中Key值的中间名称
 * Created by IntelliJ IDEA.
 * User: Wei Lingfeng
 * Date: 13-1-17
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */
public class NewTodoConstants {
  //待办单据总条目
  public final static String TODO_ORDER_AMOUNT = "todo_order_amount_";
  //待办销售单
  public final static String TODO_SALE_ORDER_AMOUNT = "todo_sale_order_amount_";
  //待办销售退货单
  public final static String TODO_SALE_RETURN_ORDER_AMOUNT = "todo_rale_return_order_amount_";
  //待办采购单
  public final static String TODO_PURCHASE_ORDER_AMOUNT = "todo_purchase_order_amount_";
  //待办入库退货单
  public final static String TODO_PURCHASE_RETURN_ORDER_AMOUNT = "todo_purchase_return_order_amount_";

  //待办事项提醒总数（首页，导航）
  public final static String REMIND_AMOUNT = "remind_amount_";
  //维修美容
  public final static String REPAIR_REMIND_AMOUNT = "repair_remind_amount_";
  //欠款提醒
  public final static String DEBT_REMIND_AMOUNT = "debt_remind_amount_";
  //进销存
  public final static String TXN_REMIND_AMOUNT = "txn_remind_amount_";
  //客户服务
  public final static String CUSTOMER_REMIND_AMOUNT = "customer_remind_amount_";

  //进销存导航提醒
  public final static String TODO_TXN_NAVI_AMOUNT = "todo_txn_navi_amount_";
  //客户管理导航提醒
  public final static String TODO_CUSTOMER_NAVI_AMOUNT = "todo_customer_navi_amount_";

  public static String getNewTodoMemcacheKey(String middleName, Long shopId){
    return MemcachePrefix.todoRemind.getValue() + middleName + shopId;
  }

  public static String getNewTodoMemcacheKey(RemindEventType type, Long shopId){
    return MemcachePrefix.todoRemind.getValue() + getMemcacheKeyMiddleName(type) + shopId;
  }

  public static String getMemcacheKeyMiddleName(RemindEventType type){
    String result = "";
    switch (type){
      case REPAIR:
        result = REPAIR_REMIND_AMOUNT;
        break;
      case DEBT:
        result = DEBT_REMIND_AMOUNT;
        break;
      case TXN:
        result = TXN_REMIND_AMOUNT;
        break;
      case CUSTOMER_SERVICE:
        result = CUSTOMER_REMIND_AMOUNT;
        break;
      case TODO_SALE_ORDER:
        result = TODO_SALE_ORDER_AMOUNT;
        break;
      case TODO_SALE_RETURN_ORDER:
        result = TODO_SALE_RETURN_ORDER_AMOUNT;
        break;
      case TODO_PURCHASE_ORDER:
        result = TODO_PURCHASE_ORDER_AMOUNT;
        break;
      case TODO_PURCHASE_RETURN_ORDER:
        result = TODO_PURCHASE_RETURN_ORDER_AMOUNT;
        break;
    }
    return result;
  }
}
