package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-8
 * Time: 下午1:56
 * To change this template use File | Settings | File Templates.
 */
public class ExportOrderConstant {
    public static final String RECEIPT_NO = "单据号";
    public static final String CUSTOMER_SUPPLIER_NAME = "客户/供应商";
    public static final String DATE = "日期";
    public static final String VEHICLE_NO = "车牌号";
    public static final String RECEIPT_TYPE = "单据类型";
    public static final String ORDER_CONTENT = "内容";
    public static final String TOTAL_AMOUNT = "单据金额";
    public static final String RECEIVABLE = "实收";
    public static final String PAYABLE = "实付";
    public static final String DEBT = "欠款";
    public static final String DISCOUNT = "优惠";
    public static final String STATUS = "状态";
    public static List<String> fieldList;
    public static String[] PayableOrderTypes = {
            "INVENTORY",
            "SALE_RETURN",
            "MEMBER_RETURN_CARD"
    };
    static {
        fieldList = new ArrayList<String>();
        fieldList.add(RECEIPT_NO);
        fieldList.add(CUSTOMER_SUPPLIER_NAME);
        fieldList.add(DATE);
        fieldList.add(VEHICLE_NO);
        fieldList.add(RECEIPT_TYPE);
        fieldList.add(ORDER_CONTENT);
        fieldList.add(TOTAL_AMOUNT);
        fieldList.add(RECEIVABLE);
        fieldList.add(PAYABLE);
        fieldList.add(DEBT);
        fieldList.add(DISCOUNT);
        fieldList.add(STATUS);
    }
}
