package com.bcgogo.enums;

import com.bcgogo.utils.BcgogoI18N;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * User: Jimuchen
 * Date: 12-6-19
 * Time: 下午8:11
 */
public enum CustInfoInvoice {
  NORMAL,
  VAT,         //增值税
  INNER,
  OTHER;

  public static Map<CustInfoInvoice, String> getLocaleMap(Locale locale){
    Map<CustInfoInvoice, String> map = new LinkedHashMap<CustInfoInvoice, String>();
    CustInfoInvoice[] invoices = CustInfoInvoice.values();
    for(CustInfoInvoice invoice:invoices){
      map.put(invoice, BcgogoI18N.getMessageByKey("CustInfoInvoice_" + invoice.toString(), locale));
    }
    return map;
  }
}
