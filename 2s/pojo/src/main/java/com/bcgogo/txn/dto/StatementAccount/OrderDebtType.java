package com.bcgogo.txn.dto.StatementAccount;

/**
 * 店铺的单据欠款类型
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-1-9
 * Time: 上午10:06
 * To change this template use File | Settings | File Templates.
 */
public enum OrderDebtType {
  CUSTOMER_DEBT_RECEIVABLE,   //客户欠款收入
  CUSTOMER_DEBT_PAYABLE,      //客户欠款支出
  SUPPLIER_DEBT_RECEIVABLE,  //供应商欠款收入
  SUPPLIER_DEBT_PAYABLE;     //供应商欠款支出
}
