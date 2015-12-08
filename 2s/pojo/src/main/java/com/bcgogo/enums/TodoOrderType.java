package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-11-19
 * Time: 下午5:36
 * To change this template use File | Settings | File Templates.
 */
public enum TodoOrderType {
  TODO_SALE_ORDERS("待办销售单"),
  TODO_SALE_RETURN_ORDERS("待办销售退货单"),
  TODO_PURCHASE_ORDERS("待办采购单"),
  TODO_PURCHASE_RETURN_ORDERS("待办入库退货单");

  String type;

  TodoOrderType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
