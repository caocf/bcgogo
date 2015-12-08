package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-8
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
public class CustomerTransactionConstant {
    public static final String CONSUME_TIME = "消费时间";
    public static final String CUSTOMER_NAME = "客户";
    public static final String MEMBER_NAME = "会员名";
    public static final String MOBILE = "手机";
    public static final String CONSUME_VEHICLE = "消费车牌";
    public static final String CONSUME_TYPE = "消费类型";
    public static final String RECEIPT_NO = "单据号";
    public static final String TOTAL_CONSUME = "消费总额";
    public static final String COST_PRICE = "成本";
    public static final String SETTLED = "实收";
    public static final String DISCOUNT = "优惠";
    public static final String DEBT = "挂账";
    public static final String GROSSFIT = "毛利";
    public static final String GROSSFIT_RATE = "毛利率";
    public static List<String> fieldList;
    static {
        fieldList = new ArrayList<String>();
        fieldList.add(CONSUME_TIME);
        fieldList.add(CUSTOMER_NAME);
        fieldList.add(MEMBER_NAME);
        fieldList.add(MOBILE);
        fieldList.add(CONSUME_VEHICLE);
        fieldList.add(CONSUME_TYPE);
        fieldList.add(RECEIPT_NO);
        fieldList.add(TOTAL_CONSUME);
        fieldList.add(COST_PRICE);
        fieldList.add(SETTLED);
        fieldList.add(DISCOUNT);
        fieldList.add(DEBT);
        fieldList.add(GROSSFIT);
        fieldList.add(GROSSFIT_RATE);
    }

}
