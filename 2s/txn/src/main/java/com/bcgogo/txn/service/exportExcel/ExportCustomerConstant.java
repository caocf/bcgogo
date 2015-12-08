package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-8
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class ExportCustomerConstant {
    public static final String CUSTOMER_NAME = "客户名";
    public static final String CONTACT = "联系人";
    public static final String MOBILE = "手机";
    public static final String MEMBER_NO = "会员卡号";
    public static final String MEMBER_TYPE = "会员卡类型";
    public static final String BALANCE = "会员储值";
    public static final String TOTAL_AMOUNT = "累计消费";
    public static List<String> fieldList;
    static {
        fieldList = new ArrayList<String>();
        fieldList.add(CUSTOMER_NAME);
        fieldList.add(CONTACT);
        fieldList.add(MOBILE);
        fieldList.add(MEMBER_NO);
        fieldList.add(MEMBER_TYPE);
        fieldList.add(BALANCE);
        fieldList.add(TOTAL_AMOUNT);
    }
}
