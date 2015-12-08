package com.bcgogo.txn.service.exportExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 13-12-12
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
public class BusinessStaffConstant {
    public static final String ASSISTANT_NAME="员工";
    public static final String DEPARTMENT_NAME="部门";
    public static final String STANDARD_HOURS="车辆施工标准工时";
    public static final String STANDARD_SERVICE="车辆施工工时金额";
    public static final String ACTUAL_HOURS="车辆施工实际工时";
    public static final String ACTUAL_SERVICE="车辆施工收入";
    public static final String SERVICE_ACHIEVEMENT="车辆施工提成";
    public static final String MEMBER="会员卡收入";
    public static final String MEMBER_ACHIEVEMENT="会员卡提成";
    public static final String WASH_COUNT="洗车次数";
    public static final String WASH="洗车收入";
    public static final String WASH_ACHIEVEMENT="洗车提成";
    public static final String SALE="商品销售收入";
    public static final String SALE_ACHIEVEMENT="商品销售提成";
    public static final String SALE_PROFIT="商品销售利润";
    public static final String SALE_PROFIT_ACHIEVEMENT="商品销售利润提成";
    public static final String BUSINESS_ACCOUNT="营业外记账";
    public static final String ALL_STAT_SUM="合计收入";
    public static final String ALL_ACHIEVEMENT="合计提成";
    public static final String ALL_PROFIT="合计利润";
    public static final String ALL_PROFIT_ACHIEVEMENT="合计利润提成";

    public static String[] staff_vehicleFields={
            ASSISTANT_NAME,
            DEPARTMENT_NAME,
            STANDARD_HOURS,
            STANDARD_SERVICE,
            ACTUAL_HOURS,
            ACTUAL_SERVICE,
            SERVICE_ACHIEVEMENT
    };
    public static String[] department_vehicleFields={
            DEPARTMENT_NAME,
            STANDARD_HOURS,
            STANDARD_SERVICE,
            ACTUAL_HOURS,
            ACTUAL_SERVICE,
            SERVICE_ACHIEVEMENT
    };

    public static String[] staff_memberFields={
            ASSISTANT_NAME,
            DEPARTMENT_NAME,
            MEMBER,
            MEMBER_ACHIEVEMENT
    };

    public static String[] department_memberFields={
            DEPARTMENT_NAME,
            MEMBER,
            MEMBER_ACHIEVEMENT
    };

    public static String[] staff_washFields={
            ASSISTANT_NAME,
            DEPARTMENT_NAME,
            WASH_COUNT,
            WASH,
            WASH_ACHIEVEMENT
    };

    public static String[] department_washFields={
            DEPARTMENT_NAME,
            WASH_COUNT,
            WASH,
            WASH_ACHIEVEMENT
    };

    public static String[] staff_salesFields={
            ASSISTANT_NAME,
            DEPARTMENT_NAME,
            SALE,
            SALE_ACHIEVEMENT,
            SALE_PROFIT,
            SALE_PROFIT_ACHIEVEMENT
    };

    public static String[] department_salesFields={
            DEPARTMENT_NAME,
            SALE,
            SALE_ACHIEVEMENT,
            SALE_PROFIT,
            SALE_PROFIT_ACHIEVEMENT
    };

    public static String[] staff_businessAccountFields={
            ASSISTANT_NAME,
            DEPARTMENT_NAME,
            BUSINESS_ACCOUNT
    };

    public static String[] department_businessAccountFields={
            DEPARTMENT_NAME,
            BUSINESS_ACCOUNT
    };

    public static String[] staff_allFields={
            ASSISTANT_NAME,
            DEPARTMENT_NAME,
            STANDARD_HOURS,
            STANDARD_SERVICE,
            ACTUAL_HOURS,
            ACTUAL_SERVICE,
            SERVICE_ACHIEVEMENT,
            MEMBER,
            MEMBER_ACHIEVEMENT,
            WASH_COUNT,
            WASH,
            WASH_ACHIEVEMENT,
            SALE,
            SALE_ACHIEVEMENT,
            SALE_PROFIT,
            SALE_PROFIT_ACHIEVEMENT,
            BUSINESS_ACCOUNT,
            ALL_STAT_SUM,
            ALL_ACHIEVEMENT,
            ALL_PROFIT,
            ALL_PROFIT_ACHIEVEMENT
    };

    public static String[] department_allFields={
            DEPARTMENT_NAME,
            STANDARD_HOURS,
            STANDARD_SERVICE,
            ACTUAL_HOURS,
            ACTUAL_SERVICE,
            SERVICE_ACHIEVEMENT,
            MEMBER,
            MEMBER_ACHIEVEMENT,
            WASH_COUNT,
            WASH,
            WASH_ACHIEVEMENT,
            SALE,
            SALE_ACHIEVEMENT,
            SALE_PROFIT,
            SALE_PROFIT_ACHIEVEMENT,
            BUSINESS_ACCOUNT,
            ALL_STAT_SUM,
            ALL_ACHIEVEMENT,
            ALL_PROFIT,
            ALL_PROFIT_ACHIEVEMENT
    };
}
