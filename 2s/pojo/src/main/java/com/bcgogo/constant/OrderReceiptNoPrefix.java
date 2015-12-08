package com.bcgogo.constant;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-18
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public enum OrderReceiptNoPrefix {

  SALE("XS"),
  PURCHASE("CG"),
  INVENTORY("RK"),
  REPAIR("SG"),
  WASH_BEAUTY("XM"),
  PURCHASE_RETURN("GT"),
  SALE_RETURN("ST"),
  REPAIR_PICKING("WL"),
  INNER_PICKING("NL"),
  INNER_RETURN("NT"),
  CUSTOMER_STATEMENT_ACCOUNT("KZ"),
  SUPPLIER_STATEMENT_ACCOUNT("GZ"),
  INVENTORY_CHECK("PD"),
  APPOINT_ORDER("YY"),
  BORROW_ORDER("JD"),
  ALLOCATE_RECORD("CD"),
  BCGOGO_HARDWARE_RECEIVABLE_ORDER("SY"),
  BCGOGO_SOFTWARE_RECEIVABLE_ORDER("SR"),
  BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER("SD"),
  ENQUIRY("XJ");

  ;


  private String prefix;

  OrderReceiptNoPrefix(String prefix)
  {
    this.prefix = prefix;
  }

  public String getPrefix()
  {
    return this.prefix;
  }
}
