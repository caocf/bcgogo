package com.bcgogo.enums;

/**
 * 付款类型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-12
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public enum PaymentTypes {
  INVENTORY("入库单"),    //入库单
  INVENTORY_RETURN_CASH("入库退货单退预付款"),   //入库退货单退现金
  INVENTORY_RETURN_DEPOSIT("入库退货单退预付款"),//茹坤退货单退定金
  INVENTORY_RETURN("入库退货单"),
  INVENTORY_RETURN_REPEAL("入库退货单作废"),
  SUPPLIER_DEPOSIT("供应商付预付款"),//供应商付定金
  INVENTORY_REPEAL("入库作废单"),    //入库单作废
  INVENTORY_DEBT("供应商欠款结算单"),//供应商欠款结算单

  STATEMENT_ACCOUNT("对账单付款"),//使用对账单进行付款
  STATEMENT_ORDER("供应商对账单"),//供应商对账单

  BUSINESS_ACCOUNT("营业外记账");//营业外记账
  String name;

  PaymentTypes(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }


}
